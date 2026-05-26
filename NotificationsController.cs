using HospitalAPI.Data;
using HospitalAPI.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class NotificationsController : ControllerBase
    {
        private readonly HospitalDbContext _context;

        public NotificationsController(HospitalDbContext context)
        {
            _context = context;
        }

        [HttpGet("{patientId}")]
        public async Task<ActionResult<IEnumerable<Notification>>> GetNotifications(int patientId)
        {
            return await _context.Notifications
                .Where(n => n.PatientId == patientId)
                .OrderByDescending(n => n.CreatedAt)
                .ToListAsync();
        }
    }
}