using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
namespace HospitalAPI.Controllers 
{
    [Route("api/[controller]")]
    [ApiController]
    public class ReviewsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public ReviewsController(HospitalDbContext context)
        {
            _context = context;
        }

        // ✅ GET REVIEWS BY DOCTOR
        [HttpGet("{doctorId}")]
        public async Task<IActionResult> GetReviews(int doctorId)
        {
            var reviews = await _context.Reviews
                .Where(r => r.DoctorId == doctorId)
                .OrderByDescending(r => r.CreatedAt)
                .ToListAsync();

            return Ok(reviews);
        }

        // ✅ ADD OR UPDATE REVIEW
        [HttpPost]
        public async Task<IActionResult> AddOrUpdateReview([FromBody] Review review)
        {
            try
            {
                if (review == null)
                    return BadRequest(new { success = false, message = "Review is null" });

                if (review.DoctorId <= 0 || review.PatientId <= 0)
                    return BadRequest(new { success = false, message = "Invalid IDs" });

                // 🔥 GET PATIENT NAME ONCE
                var patient = await _context.Patients
                    .FirstOrDefaultAsync(p => p.PatientId == review.PatientId);

                string name = patient != null ? patient.FullName : "Unknown";

                // 🔥 CHECK IF REVIEW EXISTS
                var existing = await _context.Reviews
                    .FirstOrDefaultAsync(r =>
                        r.DoctorId == review.DoctorId &&
                        r.PatientId == review.PatientId);

                if (existing != null)
                {
                    // ✅ UPDATE
                    existing.Rating = review.Rating;
                    existing.Comment = review.Comment;
                    existing.CreatedAt = DateTime.Now;
                    existing.PatientName = name;
                }
                else
                {
                    // ✅ ADD NEW
                    review.CreatedAt = DateTime.Now;
                    review.PatientName = name;
                    _context.Reviews.Add(review);
                }

                await _context.SaveChangesAsync();

                return Ok(new
                {
                    success = true,
                    message = "Review saved successfully"
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    success = false,
                    error = ex.Message
                });
            }
        }
    }
}