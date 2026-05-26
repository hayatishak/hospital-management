using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using FirebaseAdmin.Messaging;
using System.Collections.Generic;
namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AppointmentsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public AppointmentsController(HospitalDbContext context)
        {
            _context = context;
        }

        // ==========================
        // GET ALL APPOINTMENTS
        // ==========================
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Appointment>>> GetAppointments()
        {
            return await _context.Appointments.ToListAsync();
        }

        // ==========================
        // CREATE APPOINTMENT
        // ==========================
        [HttpPost] // receives post request
        public async Task<IActionResult> BookAppointment([FromBody] Appointment appointment)
        {
            try
            {
                if (appointment == null)
                    return BadRequest("Appointment is null");

                // ✅ Always set status
                appointment.Status = "requested";

                // ✅ Payment default
                appointment.PaymentStatus = "pending";

                // 🔥 FIX 1: Ensure Amount is saved
                if (appointment.Amount <= 0)
                {
                    appointment.Amount = 50; // default price
                }

                // 🔥 FIX 2: Handle reference correctly
                if (!string.IsNullOrEmpty(appointment.PaymentMethod) &&
     (appointment.PaymentMethod.ToLower() == "omt" ||
      appointment.PaymentMethod.ToLower() == "wish"))
                {
                    if (string.IsNullOrEmpty(appointment.ReferenceNumber))
                    {
                        appointment.ReferenceNumber = "HSP" + new Random().Next(10000, 99999);
                    }
                }
                else
                {
                    appointment.ReferenceNumber = null; // for chash
                }

                // ✅ Save to DB
                _context.Appointments.Add(appointment);
                await _context.SaveChangesAsync();

                return Ok(new  //backend return response
                {
                    success = true,
                    message = "Saved!",
                    reference = appointment.ReferenceNumber,
                    id = appointment.Id
                });
            }
            catch (Exception ex)
            {
                return BadRequest(new
                {
                    error = ex.Message,
                    inner = ex.InnerException?.Message
                });
            }
        }
        // ==========================
        // GET REQUESTED APPOINTMENTS
        // ==========================
        [HttpGet("requested")]
        public async Task<IActionResult> GetRequestedAppointments()
        {
            var appointments = await _context.Appointments
                .Where(a => a.Status == "requested")
                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new
                    {
                        a.Id,
                        a.PatientId,
                        a.DoctorId,
                        patientName = p.FullName,
                        a.Date,
                        a.Time, 
                        a.Status
                    })
                .ToListAsync();

            return Ok(appointments);
        }
        // ==========================
        // ACCEPT APPOINTMENT
        // ==========================
        [HttpPut("accept/{id}")]
        public async Task<IActionResult> AcceptAppointment(int id)
        {
            var appointment = await _context.Appointments
                .FirstOrDefaultAsync(a => a.Id == id);

            if (appointment == null)
                return NotFound("Appointment not found");

            // Update appointment status
            appointment.Status = "accepted";

            // Create notification object in database
            var notification = new Notification
            {
                PatientId = appointment.PatientId,
                Message = $"Your appointment on {appointment.Date} at {appointment.Time} has been accepted",
                CreatedAt = DateTime.Now
            };

            _context.Notifications.Add(notification);

            // Save changes first
            await _context.SaveChangesAsync();

            // Find patient
            var patient = await _context.Patients
     .FirstOrDefaultAsync(p => p.PatientId == appointment.PatientId);//This searches the Patients table to find the patient who owns this appointment.
            //FcmToken is like the address of the patient’s phone for Firebase notifications.
            // Send Firebase Push Notification
            if (patient != null && !string.IsNullOrEmpty(patient.FcmToken))
            {
                try
                {
                    var message = new Message()
                    {
                        Token = patient.FcmToken,

                        Notification = new FirebaseAdmin.Messaging.Notification
                        {
                            Title = "Hospital App",
                            Body = notification.Message
                        },

                        Android = new AndroidConfig
                        {
                            Priority = Priority.High
                        },

                        Data = new Dictionary<string, string>
                {
                    { "type", "appointment" },
                    { "message", notification.Message }
                }
                    };

                    await FirebaseMessaging.DefaultInstance.SendAsync(message);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("Firebase error: " + ex.Message);
                }
            }

            return Ok("Appointment accepted");
        }
        // ==========================
        // REJECT APPOINTMENT
        // ==========================
        [HttpDelete("reject/{id}")]
        public async Task<IActionResult> RejectAppointment(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            _context.Appointments.Remove(appointment);

            await _context.SaveChangesAsync(); //save database changes after delete

            return Ok(new
            {
                message = "Appointment rejected"
            });
        }

        // ==========================
        // GET ACCEPTED APPOINTMENTS /Give me all accepted appointments that belong to this doctor.
        // ==========================
        [HttpGet("accepted/{doctorId}")]
        public async Task<IActionResult>
GetAcceptedAppointments(int doctorId)
        {
            var list = await _context.Appointments

                .Where(a =>
                    a.DoctorId == doctorId &&
                    a.Status != null &&
                    a.Status.Trim().ToLower() == "accepted")

                // join patient
                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new { a, p })

                // join doctor
                .Join(_context.Doctors,
                    ap => ap.a.DoctorId,
                    d => d.Id,
                    (ap, d) => new
                    {
                        ap.a.Id,
                        ap.a.PatientId,
                        ap.a.DoctorId,

                        patientName = ap.p.FullName,
                        doctorName = d.FullName,

                        paymentStatus = ap.a.PaymentStatus,

                        ap.a.Date,
                        ap.a.Time,
                        ap.a.Status
                    })

                .ToListAsync();

            return Ok(list);
        }
        // ==========================
        // COMPLETE APPOINTMENT
        // ==========================
        [HttpPut("complete/{id}")]
        public async Task<IActionResult> CompleteAppointment(int id)
        {
            var appointment = await _context.Appointments.FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            appointment.Status = "completed";

            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Appointment marked as completed"
            });
        }

        // ==========================
        // GET COMPLETED APPOINTMENTS
        // ==========================
        [HttpGet("completed/{doctorId}")]
        public async Task<IActionResult>
 GetCompletedAppointments(int doctorId)
        {
            var list = await _context.Appointments

                .Where(a =>
                    a.DoctorId == doctorId &&
                    a.Status != null &&
                    a.Status.Trim().ToLower() == "completed")

                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new
                    {
                        a.Id,
                        a.PatientId,
                        a.DoctorId,

                        patientName = p.FullName,

                        a.Date,
                        a.Time,
                        a.Status
                    })

                .ToListAsync();

            return Ok(list);
        }

        [HttpGet("patient-history/{patientId}")]
        public async Task<IActionResult> GetAcceptedAppointmentsByPatient(int patientId)
        {
            var list = await _context.Appointments
                .Where(a =>
    a.PatientId == patientId &&
    a.Status != null &&
   new[] { "requested", "accepted", "completed" }
        .Contains(a.Status.Trim().ToLower())
)

                .Join(_context.Patients,
                    a => a.PatientId,
                    p => p.PatientId,
                    (a, p) => new { a, p })

                .Join(_context.Doctors,
                    ap => ap.a.DoctorId,
                    d => d.Id,
                    (ap, d) => new
                    {
                        ap.a.Id,
                        ap.a.PatientId,
                        ap.a.DoctorId,
                        patientName = ap.p.FullName,
                        doctorName = d.FullName,
                        paymentStatus = ap.a.PaymentStatus,
                        ap.a.Date,
                        ap.a.Time,
                        ap.a.Status
                    })

                .ToListAsync();

            return Ok(list);
        }
        //Update the payment information for appointment with this ID.
        [HttpPut("pay/{id}")]
        public async Task<IActionResult> PayAppointment(int id, [FromBody] PaymentRequest request)
        {
            var appointment =
                await _context.Appointments
                .FindAsync(id);

            if (appointment == null)
                return NotFound();

            appointment.PaymentStatus = "paid";

            appointment.PaymentMethod =
                request.PaymentMethod;

            appointment.ReferenceNumber =
                request.ReferenceNumber;

            await _context.SaveChangesAsync();

            return Ok();
        }

        [HttpPut("mark-paid/{id}")] public async Task<IActionResult> MarkAsPaid(int id,[FromBody] PaymentRequest request)
        {
            var appointment =
                await _context.Appointments
                .FindAsync(id);

            if (appointment == null)
                return NotFound("Appointment not found");

            appointment.PaymentStatus = "paid";

            appointment.PaymentMethod =
                request.PaymentMethod;

            appointment.ReferenceNumber =
                request.ReferenceNumber;

            await _context.SaveChangesAsync();

            return Ok(new
            {
                message = "Appointment marked as paid"
            });
        }

    }
}