package com.example.tatwa10;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;

public class StartingActivity extends AppCompatActivity {

    private MaterialCardView buttonDoctor;
    private MaterialCardView buttonPatient;

    public static final String SHARED_PREFERENCES = "shared_prefs";
    public static final String KEY_DOCTOR_NAME = "name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //  Install splash
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        //  Remove system splash IMMEDIATELY
        splashScreen.setKeepOnScreenCondition(() -> false);

        setContentView(R.layout.activity_starting);

        // Initialize buttons
        buttonDoctor = findViewById(R.id.button_start_doctor);
        buttonPatient = findViewById(R.id.button_start_patient);

        buttonDoctor.setOnClickListener(v -> {
            startActivity(new Intent(this, DoctorVerificationActivity.class));
        });

        buttonPatient.setOnClickListener(v -> {
            startActivity(new Intent(this, PhoneVerificationActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if doctor already logged in (LOCAL storage)
        SharedPreferences sharedPreferences =
                getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        String name = sharedPreferences.getString(KEY_DOCTOR_NAME, null);

        if (name != null) {
            Intent intent = new Intent(this, DoctorMainActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
            finish();
        }
    }

}