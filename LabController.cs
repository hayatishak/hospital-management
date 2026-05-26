using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Http;
using System.IO;
namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class LabController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public LabController(HospitalDbContext context)
        {
            _context = context;
        }

        // ✅ Create Lab Request (Patient)
        [HttpPost("request")]
        public async Task<IActionResult> CreateRequest([FromBody] LabRequest request)
        {
            request.Status = "Requested";
            request.CreatedAt = DateTime.Now;

            _context.LabRequests.Add(request);
            await _context.SaveChangesAsync();

            return Ok(new { message = "Lab request created" });
        }

        // ✅ Get All Requests (Lab side)
        [HttpGet("requests")]
        public async Task<ActionResult<IEnumerable<LabRequest>>> GetRequests()
        {
            return await _context.LabRequests
                .OrderByDescending(r => r.CreatedAt)
                .ToListAsync();
        }

        // ✅ Mark as Processing
        [HttpPut("process/{id}")]
        public async Task<IActionResult> ProcessRequest(int id)
        {
            var req = await _context.LabRequests.FindAsync(id);
            if (req == null) return NotFound();

            req.Status = "Processing";
            req.ProcessingAt = DateTime.Now;

            await _context.SaveChangesAsync();

            return Ok();
        }

        // ✅ Add Result
        [HttpPost("result")]
        public async Task<IActionResult> AddResult([FromBody] LabResult result)
        {
            try
            {
                // 🔥 find request
                var request = await _context.LabRequests.FindAsync(result.LabRequestId);
                if (request == null)
                    return NotFound("Request not found");

                // 🔥 update request status
                request.Status = "Completed";
                request.CompletedAt = DateTime.Now;

                // 🔥 fill missing fields (IMPORTANT)
                result.PatientId = request.PatientId;
                result.PatientName = request.PatientName;
                result.TestName = request.TestName;
                result.ResultDate = DateTime.Now;

                // 🔥 save result
                _context.LabResults.Add(result);

                await _context.SaveChangesAsync();

                return Ok(new { message = "Result saved successfully" });
            }
            catch (Exception ex)
            {
                return StatusCode(500, ex.Message);
            }
        }

        // ✅ Get Patient Results
        [HttpGet("results/{patientId}")]
        public async Task<ActionResult<IEnumerable<LabResult>>> GetResults(int patientId)
        {
            return await _context.LabResults
                .Where(r => r.PatientId == patientId)
                .OrderByDescending(r => r.ResultDate)
                .ToListAsync();
        }
        [HttpPost("result-with-file")]
        [Consumes("multipart/form-data")]
        public async Task<IActionResult> UploadResultWithFile([FromForm] UploadLabResultDto dto)
        {
            try
            {
                Console.WriteLine("🔥 Upload API called");
                if (dto == null)
                    return BadRequest("No data received");

                if (dto.LabRequestId <= 0)
                    return BadRequest("Invalid LabRequestId");

                if (string.IsNullOrWhiteSpace(dto.Report))
                    return BadRequest("Report is required");

                var request = await _context.LabRequests.FindAsync(dto.LabRequestId);
                if (request == null)
                    return NotFound("Request not found");

                string? fileName = null;
                string? savedRelativePath = null;

                if (dto.File != null && dto.File.Length > 0)
                {
                    var uploadsFolder = Path.Combine(Directory.GetCurrentDirectory(), "Uploads");

                    Console.WriteLine("Saving to: " + uploadsFolder);

                    if (!Directory.Exists(uploadsFolder))
                    {
                        Directory.CreateDirectory(uploadsFolder);
                    }

                    var extension = Path.GetExtension(dto.File.FileName);

                    if (string.IsNullOrEmpty(extension))
                    {
                        extension = ".pdf"; // force pdf
                    }

                     fileName = Guid.NewGuid().ToString() + extension;

                    var fullPath = Path.Combine(uploadsFolder, fileName);

                    Console.WriteLine("Full path: " + fullPath);

                    using (var stream = new FileStream(fullPath, FileMode.Create))
                    {
                        await dto.File.CopyToAsync(stream);
                    }

                    savedRelativePath = "/uploads/" + fileName;
                }

                var result = new LabResult
                {
                    LabRequestId = dto.LabRequestId,
                    PatientId = request.PatientId,
                    PatientName = request.PatientName,
                    DoctorId = request.DoctorId,
                    DoctorName = request.DoctorName,
                    TestName = request.TestName,
                    Report = dto.Report,
                    PdfFileName = fileName,
                    PdfUrl = savedRelativePath,
                    ResultDate = DateTime.Now
                };

                _context.LabResults.Add(result);

                request.Status = "Completed";
                request.CompletedAt = DateTime.Now;

                await _context.SaveChangesAsync();

                return Ok(new
                {
                    message = "Result + file saved successfully",
                    pdfFileName = fileName,
                    pdfUrl = savedRelativePath
                });
            }
            catch (Exception ex)
            {
                return StatusCode(500, new
                {
                    message = "Server error while saving lab result",
                    error = ex.Message
                });
            }
        }
    }
}