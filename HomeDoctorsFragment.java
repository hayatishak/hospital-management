package com.example.tatwa10.FragmentDoctors;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.DoctorMainActivity;
import com.example.tatwa10.Fragments.RequestLabTestFragment;
import com.example.tatwa10.R;
import com.example.tatwa10.Fragments.DoctorLabResultsFragment;
import com.example.tatwa10.Fragments.PrescriptionFragment;

import java.util.Calendar;

public class HomeDoctorsFragment extends Fragment {

    private TextView textViewName;

    // Appointment Cards
    private LinearLayout buttonApproveAppointment;
    private LinearLayout buttonPendingAppointment;
    private LinearLayout buttonCompletedAppointment;

    // Patient Cards

    private LinearLayout buttonPatientPrescription;

    // Laboratory Cards


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_doctors, container, false);

        // Highlight navigation item
        DoctorMainActivity.navigationView.setCheckedItem(R.id.nav_home2);

        // Greeting
        TextView textGreeting = view.findViewById(R.id.homet1);
        textViewName = view.findViewById(R.id.text_view_home_name);

        String name = requireActivity()
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .getString("doctorName", "Doctor");

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);

        String greeting;

        if (hour < 12)
            greeting = "Good Morning";
        else if (hour < 18)
            greeting = "Good Afternoon";
        else
            greeting = "Good Evening";

        textGreeting.setText(greeting + ",");
        textViewName.setText(name + " 👋");

        // ================= FIND VIEWS =================

        buttonApproveAppointment = view.findViewById(R.id.button_approve_appointments);
        buttonPendingAppointment = view.findViewById(R.id.button_pending_appointments);
        buttonCompletedAppointment = view.findViewById(R.id.button_completed_appointments);


        buttonPatientPrescription = view.findViewById(R.id.button_your_patients);



        // ================= APPOINTMENTS =================

        buttonApproveAppointment.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_doctor, new ApproveAppointmentFragment())
                        .addToBackStack(null)
                        .commit()
        );

        buttonPendingAppointment.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_doctor, new PendingAppointmentFragment())
                        .addToBackStack(null)
                        .commit()
        );

        buttonCompletedAppointment.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_doctor, new CompletedAppointmentFragment())
                        .addToBackStack(null)
                        .commit()
        );

        // ================= PATIENTS =================



        buttonPatientPrescription.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_doctor, new PrescriptionFragment())
                        .addToBackStack(null)
                        .commit()
        );



        return view;
    }
}