package com.example.tatwa10;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.tatwa10.FragmentDoctors.ApproveAppointmentFragment;
import com.example.tatwa10.FragmentDoctors.CompletedAppointmentFragment;
import com.example.tatwa10.FragmentDoctors.HomeDoctorsFragment;
import com.example.tatwa10.FragmentDoctors.PatientPrescriptionFragment;
import com.example.tatwa10.FragmentDoctors.PendingAppointmentFragment;
import com.example.tatwa10.Fragments.PrescriptionFragment;
import com.google.android.material.navigation.NavigationView;

public class DoctorMainActivity extends AppCompatActivity {

    private static final long TIME_INTERVAL = 2000;

    private DrawerLayout drawerLayout;
    private long mBackPressed;
    public static NavigationView navigationView;

    public static final String SHARED_PREFERENCES = "shared_prefs";
    public static String doctorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        drawerLayout = findViewById(R.id.drawer_layout_doctor);
        Toolbar toolbar = findViewById(R.id.toolbar_doctor);
        setSupportActionBar(toolbar);

        navigationView = findViewById(R.id.navigation_view_doctor);

        //  FIRST LOAD (Home)
        if (savedInstanceState == null) {
            replace(new HomeDoctorsFragment(), false);
            navigationView.setCheckedItem(R.id.nav_home2);
        }

        //  DRAWER NAVIGATION
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home2) {
                replace(new HomeDoctorsFragment(), false);

            } else if (id == R.id.nav_approve_appointment2) {
                replace(new ApproveAppointmentFragment(), false);

            } else if (id == R.id.nav_pending_appointment2) {
                replace(new PendingAppointmentFragment(), false);

            } else if (id == R.id.nav_completed_appointment2) {
                replace(new CompletedAppointmentFragment(), false);

            } else if (id == R.id.nav_prescription2) {
                replace(new PrescriptionFragment(), false);

            } else if (id == R.id.nav_log_out2) {
                logOut();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //  GET DOCTOR NAME
        SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
        doctorName = sp.getString("name", "Doctor");
    }

    // ==============================
    //  CENTRAL NAVIGATION (FIXED)
    // ==============================
    public void replace(Fragment fragment, boolean addToBackStack) {

        Fragment current = getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container_doctor);

        //  avoid duplicate fragment
        if (current != null && current.getClass().equals(fragment.getClass())) {
            return;
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container_doctor, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    // ==============================
    // LOG OUT
    // ==============================
    private void logOut() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out?")
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (d, w) -> {
                    SharedPreferences sp = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                    sp.edit().remove("name").apply();
                    startActivity(new Intent(this, StartingActivity.class));
                    finish();
                })
                .setNegativeButton("No", (d, w) -> d.cancel())
                .show();
    }

    // ==============================
    //  BACK BUTTON (FIXED)
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
}