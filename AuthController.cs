using Microsoft.AspNetCore.Mvc;
using System.Data.SqlClient;

namespace HospitalAPI.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly string connectionString =
            "Server=localhost;Database=HospitalDB;Trusted_Connection=True;TrustServerCertificate=True;";

        // LOGIN
        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest request)//receive json body
        {
            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                {
                    conn.Open();

                    string query = @"
SELECT StaffId, Role, PasswordHash, FullName
FROM StaffUsers
WHERE StaffId = @id";

                    SqlCommand cmd = new SqlCommand(query, conn);//create sql command
                    cmd.Parameters.AddWithValue("@id", request.StaffId);//send the id

                    SqlDataReader reader = cmd.ExecuteReader();//execute sql command and read the result from db

                    if (reader.Read())
                    {
                        string dbPassword = reader["PasswordHash"].ToString();//get the pass value from db result

                        if (dbPassword != request.Password)
                            return Ok(new { success = false });

                        string staffId = reader["StaffId"].ToString();//read from db
                        string role = reader["Role"].ToString();
                        string name = reader["FullName"].ToString();

                        reader.Close(); // 🔥 IMPORTANT

                        // 🔥 GET DOCTOR ID
                        int doctorId = 0;

                        if (role.ToLower() == "doctor")//bcz each dr have their own dashbboard
                        {
                            string doctorQuery = "SELECT Id FROM Doctors WHERE StaffId = @staffId";

                            SqlCommand docCmd = new SqlCommand(doctorQuery, conn);
                            docCmd.Parameters.AddWithValue("@staffId", staffId);

                            object result = docCmd.ExecuteScalar();//run sql and return on value

                            if (result != null)
                                doctorId = Convert.ToInt32(result);
                        }

                        return Ok(new
                        {
                            success = true,
                            staffId = staffId,
                            role = role,
                            name = name,
                            doctorId = doctorId
                        });
                    }

                    return Ok(new { success = false });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }

        // REGISTER DOCTOR
        [HttpPost("register-doctor")]
        public IActionResult RegisterDoctor([FromBody] RegisterDoctorRequest request)
        {
            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                {
                    conn.Open();

                    string checkQuery = "SELECT COUNT(*) FROM StaffUsers WHERE StaffId=@id";
                    SqlCommand checkCmd = new SqlCommand(checkQuery, conn);
                    checkCmd.Parameters.AddWithValue("@id", request.StaffId);

                    int exists = (int)checkCmd.ExecuteScalar();

                    if (exists > 0)
                        return BadRequest("Doctor already exists");

                    string query = "INSERT INTO StaffUsers (StaffId, FullName, Role, PasswordHash) VALUES (@id, @name, @role, @pass)";

                    SqlCommand cmd = new SqlCommand(query, conn);
                    cmd.Parameters.AddWithValue("@id", request.StaffId);
                    cmd.Parameters.AddWithValue("@name", request.FullName);
                    cmd.Parameters.AddWithValue("@role", "doctor");
                    cmd.Parameters.AddWithValue("@pass", request.Password);

                    cmd.ExecuteNonQuery(); 

                    return Ok(new { success = true });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("register-lab")]
        public IActionResult RegisterLab([FromBody] RegisterLabRequest request)
        {
            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                {
                    conn.Open();

                    string checkQuery = "SELECT COUNT(*) FROM StaffUsers WHERE StaffId=@id";
                    SqlCommand checkCmd = new SqlCommand(checkQuery, conn);
                    checkCmd.Parameters.AddWithValue("@id", request.StaffId);

                    int exists = (int)checkCmd.ExecuteScalar();

                    if (exists > 0)
                        return BadRequest("Lab staff already exists");

                    string query = "INSERT INTO StaffUsers (StaffId, FullName, Role, PasswordHash) VALUES (@id, @name, @role, @pass)";

                    SqlCommand cmd = new SqlCommand(query, conn);
                    cmd.Parameters.AddWithValue("@id", request.StaffId);
                    cmd.Parameters.AddWithValue("@name", request.FullName);
                    cmd.Parameters.AddWithValue("@role", "lab"); // 🔥 KEY
                    cmd.Parameters.AddWithValue("@pass", request.Password);

                    cmd.ExecuteNonQuery();

                    return Ok(new { success = true });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
        [HttpPost("register-admin")]
        public IActionResult RegisterAdmin([FromBody] RegisterAdminRequest request)
        {
            try
            {
                using (SqlConnection conn = new SqlConnection(connectionString))
                {
                    conn.Open();

                    string checkQuery = "SELECT COUNT(*) FROM StaffUsers WHERE StaffId=@id";
                    SqlCommand checkCmd = new SqlCommand(checkQuery, conn);
                    checkCmd.Parameters.AddWithValue("@id", request.StaffId);

                    int exists = (int)checkCmd.ExecuteScalar();

                    if (exists > 0)
                        return BadRequest("Admin already exists");

                    string query = "INSERT INTO StaffUsers (StaffId, FullName, Role, PasswordHash) VALUES (@id, @name, @role, @pass)";

                    SqlCommand cmd = new SqlCommand(query, conn);
                    cmd.Parameters.AddWithValue("@id", request.StaffId);
                    cmd.Parameters.AddWithValue("@name", request.FullName);
                    cmd.Parameters.AddWithValue("@role", "admin"); // 🔥
                    cmd.Parameters.AddWithValue("@pass", request.Password);

                    cmd.ExecuteNonQuery();

                    return Ok(new { success = true });
                }
            }
            catch (Exception ex)
            {
                return BadRequest(ex.Message);
            }
        }
    }

    public class LoginRequest
    {
        internal string? email;
        internal string? password;

        public string? StaffId { get; set; }
        public string? Password { get; set; }
        public string? Email { get; internal set; }
    }

    public class RegisterDoctorRequest
    {
        public string? StaffId { get; set; }
        public string? FullName { get; set; }
        public string? Password { get; set; }
    }
    public class RegisterLabRequest
    {
        public string? StaffId { get; set; }
        public string? FullName { get; set; }
        public string? Password { get; set; }
    }
    public class RegisterAdminRequest
    {
        public string? StaffId { get; set; }
        public string? FullName { get; set; }
        public string? Password { get; set; }
    }
}