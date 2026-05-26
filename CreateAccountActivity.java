package com.example.tatwa10;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editId, editPassword;

    //  ADD THESE
    private EditText editName, editSpec, editPhone, editDescription, editQualification;

    private CheckBox cbDoctor, cbLab, cbAdmin;
    private Button btnCreate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        editId = findViewById(R.id.edit_id);
        editPassword = findViewById(R.id.edit_password);

        //  LINK NEW FIELDS
        editName = findViewById(R.id.edit_name);
        editSpec = findViewById(R.id.edit_spec);
        editPhone = findViewById(R.id.edit_phone);
        editDescription = findViewById(R.id.edit_description);
        editQualification = findViewById(R.id.edit_qualification);

        cbDoctor = findViewById(R.id.cb_doctor);
        cbLab = findViewById(R.id.cb_lab);
        cbAdmin = findViewById(R.id.cb_admin);

        btnCreate = findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(v -> createAccount());
    }

    private void createAccount() {

        String id = editId.getText().toString().trim();
        String pass = editPassword.getText().toString().trim();
        String name = editName.getText().toString().trim();

        if (id.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // DOCTOR
        if (cbDoctor.isChecked()) {

            new Thread(() -> {
                String phone = editPhone.getText().toString().trim();
                String spec = editSpec.getText().toString().trim();

                String response = ApiService.registerDoctor(id, name, pass, phone, spec);

                runOnUiThread(() ->
                        Toast.makeText(this, "Doctor: " + response, Toast.LENGTH_LONG).show()
                );
            }).start();
        }

        // LAB
        else if (cbLab.isChecked()) {

            new Thread(() -> {
                String response = ApiService.registerLab(id, name, pass);

                runOnUiThread(() ->
                        Toast.makeText(this, "Lab: " + response, Toast.LENGTH_LONG).show()
                );
            }).start();
        }

        // ADMIN (optional)
        else if (cbAdmin.isChecked()) {

            new Thread(() -> {
                String response = ApiService.registerAdmin(id, name, pass);

                runOnUiThread(() ->
                        Toast.makeText(this, "Admin: " + response, Toast.LENGTH_LONG).show()
                );
            }).start();
        }

        else {
            Toast.makeText(this, "Select role", Toast.LENGTH_SHORT).show();
            return;
        }

        // CLEAR
        editId.setText("");
        editPassword.setText("");
        editName.setText("");
    }
}