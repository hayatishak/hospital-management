package com.example.tatwa10;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class DoctorVerificationActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private EditText editTextDoctorName;
    private Button buttonLogin;
    private ProgressDialog dialog;
    private TextView textContactIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_verification);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Signing In... Please wait");

        editTextDoctorName = findViewById(R.id.spinner_doctors_login);
        editTextPassword = findViewById(R.id.edit_text_doctor_password);
        buttonLogin = findViewById(R.id.button_doctor_login);
        textContactIT = findViewById(R.id.text_contact_it);

        buttonLogin.setOnClickListener(v -> loginDoctor());

        //  CONTACT IT CLICK
        textContactIT.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:81606274"));
            startActivity(intent);
        });
    }

    private void loginDoctor() {

        String id = editTextDoctorName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.show();

        new Thread(() -> {
            try {

                String response = ApiService.loginUser(id, password);

                System.out.println("LOGIN RESPONSE: " + response);

                LoginResponse login = new Gson().fromJson(response, LoginResponse.class);

                runOnUiThread(() -> {

                    dialog.dismiss();

                    if (login == null || !login.isSuccess()) {
                        Toast.makeText(this, "Invalid ID or Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String role = login.getRole().trim().toLowerCase();

                    switch (role) {

                        case "doctor":

                            String doctorName = login.getName();
                            int doctorId = login.getDoctorId();

                            getSharedPreferences("user", MODE_PRIVATE)
                                    .edit()
                                    .putString("doctorName", doctorName)
                                    .putInt("doctorId", doctorId) // SAVE ID
                                    .apply();

                            android.util.Log.d("DOCTOR_LOGIN", "Saved doctorId = " + doctorId); //  DEBUG

                            startActivity(new Intent(this, DoctorMainActivity.class));
                            break;

                        case "lab":
                            startActivity(new Intent(this, LabActivity.class));
                            break;

                        case "admin":
                            startActivity(new Intent(this, HospitalAdminActivity.class));
                            break;

                        default:
                            Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_LONG).show();
                            break;
                    }
                });

            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show();
                });
            }

        }).start();
    }
}