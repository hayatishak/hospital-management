using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System.Text.Json.Serialization;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PatientsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public PatientsController(HospitalDbContext context)
        {
            _context = context;
        }

        [HttpPut("save-token/{patientId}")]
        public async Task<IActionResult> SaveToken(int patientId, [FromBody] string token)
        {
            var patient = await _context.Patients.FindAsync(patientId);

            if (patient == null)
                return NotFound("Patient not found");

            patient.FcmToken = token;
            await _context.SaveChangesAsync();

            return Ok(new { message = "Token saved successfully" });
        }
        [HttpPost("register")]
        public async Task<IActionResult> Register([FromBody] RegisterDto request)
        {
            try
            {
                // 🔥 CHECK NULL FIRST
                if (request == null)
                {
                    return BadRequest("Request is NULL (JSON not received)");
                }

                string email = request.Email?.Trim().ToLower() ?? "";
                string password = request.Password?.Trim() ?? "";
                string name = request.FullName?.Trim() ?? "";

                // 🔥 VALIDATION
                if (string.IsNullOrWhiteSpace(email) ||
                    string.IsNullOrWhiteSpace(password) ||
                    string.IsNullOrWhiteSpace(name))
                {
                    return Ok(new { success = false, message = "Invalid data" });
                }

                // 🔥 CHECK EXISTS
                var exists = await _context.Patients
                    .AnyAsync(p => p.Email != null && p.Email.ToLower() == email);

                if (exists)
                {
                    return Ok(new { success = false, message = "Patient already exists" });
                }

                // 🔥 CREATE PATIENT
                var patient = new Patient
                {
                    FullName = name,
                    Email = email,
                    Password = password
                };

                _context.Patients.Add(patient);
                await _context.SaveChangesAsync();

                return Ok(new
                {
                    success = true,
                    patientId = patient.PatientId,
                    name = patient.FullName
                });
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("login")]//login patient 
        public async Task<IActionResult> Login([FromBody] object rawData)//receive and stord
        {
            try
            {
                var json = rawData.ToString();//convert json to text format
                var data = System.Text.Json.JsonDocument.Parse(json);//parse to backend can read it

                string email = data.RootElement.GetProperty("email").GetString()?.Trim() ?? "";//read email and pass from json
                string password = data.RootElement.GetProperty("password").GetString()?.Trim() ?? "";

                var patient = await _context.Patients//table patient using dbcontext
                    .FirstOrDefaultAsync(p =>//search for the first patient have this email and pass
                        p.Email.ToLower().Trim() == email.ToLower() &&
                        p.Password.Trim() == password);

                if (patient == null)
                    return Ok(new { success = false, debugEmail = email });

                return Ok(new
                {
                    success = true,
                    patientId = patient.PatientId,
                    name = patient.FullName
                });
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpGet("{id}")]
        public async Task<IActionResult> GetPatientById(int id)
        {
            var patient = await _context.Patients
                .Where(p => p.PatientId == id)
                .Select(p => new
                {
                    p.PatientId,
                    p.FullName,
                    p.Email,
                    p.Phone,
                    p.DateOfBirth,
                    p.NationalId,
                    p.BloodType,
                    p.Allergies,
                    p.Diseases,
                    p.Medications,
                    p.Address,
                    p.Country,
                    p.City
                })
                .FirstOrDefaultAsync();

            if (patient == null)
            {
                return NotFound(new
                {
                    success = false,
                    message = "Patient not found"
                });
            }

            return Ok(patient);
        }
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Patient>>> GetPatients()
        {
            return await _context.Patients
                .OrderByDescending(p => p.PatientId) // 🔥 newest first
                .ToListAsync();
        }
        [HttpPut("update-details/{id}")]
        public async Task<IActionResult> UpdateDetails(int id, [FromBody] PatientUpdateDto? updated)
        {
            try
            {
                if (updated == null)
                {
                    Console.WriteLine("UPDATED IS NULL ❌");

                    return BadRequest(new
                    {
                        success = false,
                        message = "updated is null"
                    });
                }

                Console.WriteLine("PHONE: " + updated.Phone);
                Console.WriteLine("DOB: " + updated.DateOfBirth);
                Console.WriteLine("CITY: " + updated.City);

                var patient = await _context.Patients.FindAsync(id);

                if (patient == null)
                {
                    return NotFound(new
                    {
                        success = false,
                        message = "Patient not found"
                    });
                }

                if (updated.Phone != null) patient.Phone = updated.Phone;
                if (updated.NationalId != null) patient.NationalId = updated.NationalId;
                if (updated.BloodType != null) patient.BloodType = updated.BloodType;
                if (updated.Allergies != null) patient.Allergies = updated.Allergies;
                if (updated.Diseases != null) patient.Diseases = updated.Diseases;
                if (updated.Medications != null) patient.Medications = updated.Medications;
                if (updated.Address != null) patient.Address = updated.Address;
                if (updated.Country != null) patient.Country = updated.Country;
                if (updated.City != null) patient.City = updated.City;
                if (updated.DateOfBirth != null) patient.DateOfBirth = updated.DateOfBirth;

                await _context.SaveChangesAsync();

                return Ok(new { success = true, message = "Details updated" });
            }
            catch (Exception ex)
            {
                return BadRequest(new
                {
                    success = false,
                    message = ex.Message
                });
            }
        }
    }
}