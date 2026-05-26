using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Globalization;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class RoomsController : ControllerBase
    {
        private readonly string connectionString =
            "Server=localhost;Database=HospitalDB;Trusted_Connection=True;TrustServerCertificate=True;";

        // ===============================
        // GET ALL ROOMS
        // ===============================
        [HttpGet]
        public IActionResult GetAllRooms()
        {
            List<object> rooms = new List<object>();

            using (SqlConnection conn = new SqlConnection(connectionString))
            {
                conn.Open();

                string query = "SELECT * FROM Rooms";
                SqlCommand cmd = new SqlCommand(query, conn);
                SqlDataReader reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    rooms.Add(new
                    {
                        id = reader["Id"],
                        name = reader["Name"].ToString(),
                        isReserved = reader["IsReserved"] != DBNull.Value && (bool)reader["IsReserved"],
                        patientName = reader["PatientName"]?.ToString(),
                        reservationDate = reader["ReservationDate"]?.ToString(),
                        startDateTime = reader["StartDateTime"]?.ToString(),
                        endDateTime = reader["EndDateTime"]?.ToString(),
                        paymentStatus = reader["PaymentStatus"]?.ToString(),
                        type = reader["Type"]?.ToString(),

                        phone = reader["Phone"]?.ToString(),
                        bloodType = reader["BloodType"]?.ToString(),
                        address = reader["Address"]?.ToString()
                    });
                }
            }

            return Ok(rooms);
        }

        // ===============================
        // 🔥 RESERVE ROOM (WITH TIME CHECK)
        // ===============================
        [HttpPost("reserve")]
        public IActionResult ReserveRoom([FromBody] RoomReservationDto request)
        {
            using (SqlConnection conn = new SqlConnection(connectionString))
            {
                conn.Open();

                if (request.PaymentStatus != "paid")
                {
                    return Ok(new { success = false, message = "Please complete payment first" });
                }

                DateTime start = DateTime.Parse(request.StartDateTime);
                DateTime end = DateTime.Parse(request.EndDateTime);

                string overlapQuery = @"
        SELECT COUNT(*) FROM Rooms
        WHERE Id = @id AND IsReserved = 1
        AND (
            @start < EndDateTime AND @end > StartDateTime
        )";

                SqlCommand overlapCmd = new SqlCommand(overlapQuery, conn);
                overlapCmd.Parameters.AddWithValue("@id", request.RoomId);
                overlapCmd.Parameters.AddWithValue("@start", start);
                overlapCmd.Parameters.AddWithValue("@end", end);

                int count = (int)overlapCmd.ExecuteScalar();

                if (count > 0)
                {
                    return Ok(new { success = false, message = "Time already booked ❌" });
                }

                string query = @"UPDATE Rooms 
                         SET IsReserved = 1,
                             PatientName = @patient,
                             ReservationDate = @date,
                             StartDateTime = @start,
                             EndDateTime = @end,
                             PaymentStatus = @payment,
                             Phone = @phone,
                             BloodType = @blood,
                             Address = @address
                         WHERE Id = @id";

                SqlCommand cmd = new SqlCommand(query, conn);

                cmd.Parameters.AddWithValue("@patient", request.PatientName ?? "");
                cmd.Parameters.AddWithValue("@date", request.ReservationDate ?? "");
                cmd.Parameters.AddWithValue("@start", start);
                cmd.Parameters.AddWithValue("@end", end);
                cmd.Parameters.AddWithValue("@payment", request.PaymentStatus ?? "pending");
                cmd.Parameters.AddWithValue("@phone", request.Phone ?? "");
                cmd.Parameters.AddWithValue("@blood", request.BloodType ?? "");
                cmd.Parameters.AddWithValue("@address", request.Address ?? "");
                cmd.Parameters.AddWithValue("@id", request.RoomId);

                int rows = cmd.ExecuteNonQuery();

                if (rows > 0)
                {
                    string historyQuery = @"INSERT INTO RoomHistory
(RoomId, PatientName, ReservationDate, StartDateTime, EndDateTime, PaymentStatus)
VALUES (@roomId, @patient, @date, @start, @end, @payment)";

                    SqlCommand historyCmd = new SqlCommand(historyQuery, conn);
                    historyCmd.Parameters.AddWithValue("@roomId", request.RoomId);
                    historyCmd.Parameters.AddWithValue("@patient", request.PatientName ?? "");
                    historyCmd.Parameters.AddWithValue("@date", request.ReservationDate ?? "");
                    historyCmd.Parameters.AddWithValue("@start", start);
                    historyCmd.Parameters.AddWithValue("@end", end);
                    historyCmd.Parameters.AddWithValue("@payment", request.PaymentStatus ?? "pending");

                    historyCmd.ExecuteNonQuery();

                    return Ok(new { success = true });
                }
                else
                {
                    return Ok(new { success = false, message = "Room not found" });
                }
            }
        }

        // ===============================
        // CANCEL ROOM
        // ===============================
        [HttpPut("cancel/{id}")]
        public IActionResult CancelRoom(int id)
        {
            using (SqlConnection conn = new SqlConnection(connectionString))
            {
                conn.Open();

                string query = @"UPDATE Rooms 
                                 SET IsReserved = 0,
                                     PatientName = NULL,
                                     ReservationDate = NULL,
                                     StartDateTime = NULL,
                                     EndDateTime = NULL,
                                     PaymentStatus = NULL,
                                     Phone = NULL,
                                     BloodType = NULL,
                                     Address = NULL
                                 WHERE Id = @id";

                SqlCommand cmd = new SqlCommand(query, conn);
                cmd.Parameters.AddWithValue("@id", id);

                cmd.ExecuteNonQuery();

                return Ok(new { success = true });
            }
        }

        // ===============================
        // GET HISTORY
        // ===============================
        [HttpGet("history/{roomId}")]
        public IActionResult GetHistory(int roomId)
        {
            List<object> history = new List<object>();

            using (SqlConnection conn = new SqlConnection(connectionString))
            {
                conn.Open();
                string query = @"
SELECT 
    r.PatientName,
    r.ReservationDate,
    r.StartDateTime,
    r.EndDateTime,
    r.PaymentStatus,
    p.Phone,
    p.BloodType,
    p.Address
FROM RoomHistory r
LEFT JOIN Patients p ON r.PatientName = p.FullName
WHERE r.RoomId = @id
ORDER BY r.StartDateTime DESC";

                SqlCommand cmd = new SqlCommand(query, conn);
                cmd.Parameters.AddWithValue("@id", roomId);

                SqlDataReader reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    string start = reader["StartDateTime"] != DBNull.Value
                        ? Convert.ToDateTime(reader["StartDateTime"]).ToString("yyyy-MM-dd HH:mm")
                        : "Not set";

                    string end = reader["EndDateTime"] != DBNull.Value
                        ? Convert.ToDateTime(reader["EndDateTime"]).ToString("yyyy-MM-dd HH:mm")
                        : "Not finished";

                    // 🔥 NO PARSE → NO CRASH
                    string reservationDate = reader["ReservationDate"] != DBNull.Value
                        ? reader["ReservationDate"].ToString()
                        : "-";

                    history.Add(new
                    {
                        patientName = reader["PatientName"].ToString(),
                        reservationDate = reservationDate,
                        startDateTime = start,
                        endDateTime = end,
                        paymentStatus = reader["PaymentStatus"].ToString(),

                        phone = reader["Phone"] != DBNull.Value ? reader["Phone"].ToString() : "N/A",
                        bloodType = reader["BloodType"] != DBNull.Value ? reader["BloodType"].ToString() : "N/A",
                        address = reader["Address"] != DBNull.Value ? reader["Address"].ToString() : "N/A"
                    });
                }
            }

            return Ok(history);
        }

        // ===============================
        // DTO
        // ===============================
        public class RoomReservationDto
        {
            public int RoomId { get; set; }
            public string? PatientName { get; set; }
            public string? ReservationDate { get; set; }
            public string PaymentStatus { get; set; }

            public string StartDateTime { get; set; }
            public string EndDateTime { get; set; }

            public string? Phone { get; set; }
            public string? BloodType { get; set; }
            public string? Address { get; set; }
        }
    }
}