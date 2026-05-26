package com.example.tatwa10;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tatwa10.FragmentDoctors.PatientPrescriptionFragment;
import com.example.tatwa10.Fragments.AppointmentFragment;
import com.example.tatwa10.Fragments.FindDoctorsFragment;
import com.example.tatwa10.Fragments.HomeFragment;
import com.example.tatwa10.Fragments.HospitalBranchesFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    public static String patientName;
    public static int patientId = 0;

    private DrawerLayout drawerLayout;
    public static NavigationView navigationView;

    public static String currentFragment = "home";

    private static final int TIME_INTERVAL = 2000;
    private long mBackPressed;

    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        patientId = prefs.getInt("patientId", 0);
        patientName = prefs.getString("name", "");

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.nav_view);

        //  Load Home first (NO back stack)
        if (savedInstanceState == null) {
            replace(new HomeFragment(), false);
            navigationView.setCheckedItem(R.id.nav_home);
        }

        //  Drawer clicks
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                currentFragment = "home";
                replace(new HomeFragment(), false);

            } else if (id == R.id.nav_doctors) {
                currentFragment = "doctors";
                replace(new FindDoctorsFragment(), false);

            } else if (id == R.id.nav_appointment) {
                currentFragment = "appointment";
                replace(new AppointmentFragment(), false);

            } else if (id == R.id.nav_prescription) {
                currentFragment = "prescription";
                replace(new PatientPrescriptionFragment(), false);

            } else if (id == R.id.nav_branches) {
                currentFragment = "branches";
                replace(new HospitalBranchesFragment(), false);

            } else if (id == R.id.nav_contact_us) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://keen-cobbler-068145.netlify.app/#contact")
                ));

            } else if (id == R.id.nav_log_out) {
                logOut();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Drawer toggle
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Permissions
        requestNotificationPermission();
        getFirebaseToken();

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CALL_PHONE},
                1
        );
    }

    // ==============================
    //  CENTRAL NAVIGATION
    // ==============================
    public void replace(Fragment fragment, boolean addToBackStack) {

        Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (current != null && current.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    // ==============================
    // BACK BUTTON (FIXED)
    // ==============================
    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();
        }
    }

    // ==============================
    // LOG OUT
    // ==============================
    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (d, w) -> {
                    startActivity(new Intent(this, StartingActivity.class));
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    // ==============================
    // FIREBASE TOKEN
    // ==============================
    private void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) return;
                    String token = task.getResult();
                    sendTokenToServer(token);
                });
    }

    private void sendTokenToServer(String token) {
        new Thread(() -> {
            try {
                ApiService.saveFcmToken(patientId, token);
            } catch (Exception ignored) {}
        }).start();
    }

    // ==============================
    // PERMISSIONS
    // ==============================
    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }
}