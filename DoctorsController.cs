using Microsoft.AspNetCore.Mvc;
using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.EntityFrameworkCore;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class DoctorsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public DoctorsController(HospitalDbContext context)
        {
            _context = context;
        }

        // GET: api/doctors
        [HttpGet]
        public async Task<IActionResult> GetDoctors()
        {
            var doctors = await _context.Doctors
                .Select(d => new
                {
                    d.Id,
                    d.StaffId,
                    d.FullName,
                    d.Phone,
                    d.Specialization,
                    d.Price
                })
                .ToListAsync();

            return Ok(doctors);
        }

        // POST: api/doctors

        [HttpPost]
        public async Task<IActionResult> AddDoctor([FromBody] DoctorDto dto)
        {
            try
            {
                if (dto == null)
                    return BadRequest("Doctor is null");

                var doctor = new Doctor
                {
                    StaffId = dto.StaffId,
                    FullName = dto.FullName,
                    PasswordHash = dto.PasswordHash,
                    Role = "doctor",

                    // ✅ ADD THESE
                    Phone = dto.Phone,
                    Specialization = dto.Specialization,
                    Price = dto.Price
                };

                _context.Doctors.Add(doctor);
                await _context.SaveChangesAsync();

                var staffUser = new StaffUser
                {
                    StaffId = doctor.StaffId,
                    Role = "doctor",
                    FullName = doctor.FullName,
                    PasswordHash = doctor.PasswordHash
                };

                _context.StaffUsers.Add(staffUser);
                await _context.SaveChangesAsync();

                return Ok(new
                {
                    success = true,
                    message = "Doctor created successfully"
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    error = ex.Message
                });
            }
        }
        // DELETE: api/doctors/5
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteDoctor(int id)
        {
            var doctor = await _context.Doctors.FindAsync(id);

            if (doctor == null)
            {
                return NotFound();
            }

            // also remove from StaffUsers if needed
            var staffUser = await _context.StaffUsers
                .FirstOrDefaultAsync(s => s.StaffId == doctor.StaffId);

            if (staffUser != null)
            {
                _context.StaffUsers.Remove(staffUser);
            }

            _context.Doctors.Remove(doctor);
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}