using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class PrescriptionsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public PrescriptionsController(HospitalDbContext context)
        {
            _context = context;
        }

        [HttpPost("add")]
        public async Task<IActionResult> AddPrescription([FromBody] Prescription p)
        {
            try
            {
                if (p == null)
                    return BadRequest("NULL DATA");

                p.CreatedAt = DateTime.Now;

                _context.Prescriptions.Add(p);

                int rows = await _context.SaveChangesAsync();

                return Ok(new
                {
                    success = true,
                    rows = rows,
                    data = p
                });
            }
            catch (Exception ex)
            {
                return BadRequest("ERROR: " + ex.Message);
            }
        }
        [HttpGet("{patientId}")]
        public async Task<IActionResult> GetByPatient(int patientId)
        {
            var prescriptions = await _context.Prescriptions
                .Where(p => p.PatientId == patientId)
                .OrderByDescending(p => p.CreatedAt)
                .Select(p => new
                {
                    patientName = _context.Patients
                        .Where(pt => pt.PatientId == p.PatientId)
                        .Select(pt => pt.FullName)
                        .FirstOrDefault() ?? "Unknown",

                    doctorName = _context.Doctors
                        .Where(d => d.Id == p.DoctorId)
                        .Select(d => d.FullName)
                        .FirstOrDefault() ?? "Unknown",

                    medicaments = p.Medicaments ?? "",
                    notes = string.IsNullOrEmpty(p.Notes) ? "No notes" : p.Notes,

                    duration = p.Duration,

                    dateStart = p.DateStart.HasValue
                        ? p.DateStart.Value.ToString("yyyy-MM-dd")
                        : "-",

                    dateEnd = p.DateEnd.HasValue
                        ? p.DateEnd.Value.ToString("yyyy-MM-dd")
                        : "-",

                    breakfast = p.Breakfast,
                    lunch = p.Lunch,
                    dinner = p.Dinner
                })
                .ToListAsync();

            return Ok(prescriptions);
        }

        [HttpGet("doctor/{id}")]
        public async Task<IActionResult> GetByDoctor(int id)
        {
            var prescriptions = await _context.Prescriptions
                .Where(p => p.DoctorId == id)
                .Include(p => p.Patient)
                .OrderByDescending(p => p.CreatedAt)
                .Select(p => new
                {
                    patientName = p.Patient != null ? p.Patient.FullName : "Unknown",
                    medicineName = p.Medicaments ?? "",
                    notes = p.Notes ?? "",
                    duration = p.Duration,
                    dateStart = p.DateStart.HasValue
    ? p.DateStart.Value.ToString("yyyy-MM-dd")
    : "-",

                    dateEnd = p.DateEnd.HasValue
    ? p.DateEnd.Value.ToString("yyyy-MM-dd")
    : "-",
                    breakfast = p.Breakfast,
                    lunch = p.Lunch,
                    dinner = p.Dinner
                })
                .ToListAsync();

            return Ok(prescriptions);
        }
        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var prescriptions = await _context.Prescriptions
                .OrderByDescending(p => p.CreatedAt)
                .Select(p => new
                {
                    // 🔥 ADD HERE
                    patientName = _context.Patients
                        .Where(pt => pt.PatientId == p.PatientId)
                        .Select(pt => pt.FullName)
                        .FirstOrDefault() ?? "Unknown",

                    doctorName = _context.Doctors
                        .Where(d => d.Id == p.DoctorId)
                        .Select(d => d.FullName)
                        .FirstOrDefault() ?? "Unknown",

                    medicaments = p.Medicaments ?? "",
                    notes = string.IsNullOrEmpty(p.Notes) ? "No notes" : p.Notes,

                    duration = p.Duration,

                    dateStart = p.DateStart.HasValue
                        ? p.DateStart.Value.ToString("yyyy-MM-dd")
                        : "-",

                    dateEnd = p.DateEnd.HasValue
                        ? p.DateEnd.Value.ToString("yyyy-MM-dd")
                        : "-",

                    breakfast = p.Breakfast,
                    lunch = p.Lunch,
                    dinner = p.Dinner
                })
                .ToListAsync();

            return Ok(prescriptions);
        }

        [HttpGet("debug")]
        public IActionResult Debug()
        {
            return Ok(new
            {
                db = _context.Database.GetDbConnection().Database,
                count = _context.Prescriptions.Count()
            });
        }
        [HttpGet("test")]
        public async Task<IActionResult> Test()
        {
            var data = await _context.Prescriptions.ToListAsync();
            return Ok(data);
        }
    }
}