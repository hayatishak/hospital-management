package com.example.tatwa10.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.ApiService;
import com.example.tatwa10.MainActivity;
import com.example.tatwa10.ModelClass.Doctor;
import com.example.tatwa10.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookAppointmentFragment extends Fragment {

    private ProgressDialog dialog;
    private Spinner spinnerDoctorsList;
    private MaterialButton buttonPayment;
    private ChipGroup chipGroupSlots;
    private TextView textSelectedDate;

    private List<Doctor> doctorsList = new ArrayList<>();
    private String selectedDate = "";
    private String selectedTime = "";

    public static final String[] times = {
            "10:00 - 10:15 AM","10:15 - 10:30 AM","10:30 - 10:45 AM","10:45 - 11:00 AM",
            "11:00 - 11:15 AM","11:15 - 11:30 AM","11:30 - 11:45 AM","11:45 - 12:00 PM",
            "12:00 - 12:15 PM","12:15 - 12:30 PM","12:30 - 12:45 PM","12:45 - 1:00 PM"
    };

    private List<String> availableSlots;

    public BookAppointmentFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_appointment, container, false);

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading Available Slots...");

        spinnerDoctorsList = view.findViewById(R.id.spinner_appointment_doctors);
        buttonPayment = view.findViewById(R.id.button_proceed_payment);
        chipGroupSlots = view.findViewById(R.id.chipGroupSlots);
        textSelectedDate = view.findViewById(R.id.textSelectedDate);

        setupDoctors();
        setupCalendar(view);
        loadSlots();
        setupChipSelection();

        // BUTTON CLICK
        buttonPayment.setOnClickListener(v -> {

            //  DEBUG (VERY IMPORTANT)
            android.util.Log.d("DEBUG", "PatientId: " + MainActivity.patientId);

            int position = spinnerDoctorsList.getSelectedItemPosition();

            if (position < 0 || position >= doctorsList.size()) {
                Toast.makeText(getContext(), "Please select a doctor", Toast.LENGTH_SHORT).show();
                return;
            }

            Doctor selectedDoctor = doctorsList.get(position);

            android.util.Log.d("DEBUG", "DoctorId: " + selectedDoctor.getId());

            if (selectedDate.isEmpty()) {
                Toast.makeText(getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTime.isEmpty()) {
                Toast.makeText(getContext(), "Please select a time slot", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🚨 IMPORTANT CHECK
            if (MainActivity.patientId == 0) {
                Toast.makeText(getContext(), "ERROR: patientId is 0", Toast.LENGTH_LONG).show();
                return;
            }

            if (selectedDoctor.getId() == 0) {
                Toast.makeText(getContext(), "ERROR: doctorId is 0", Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject json = new JSONObject();

            try {
                json.put("doctorId", selectedDoctor.getId());
                json.put("patientId", MainActivity.patientId);

                json.put("doctorName", selectedDoctor.getFullName());
                json.put("patientName", MainActivity.patientName);

                json.put("date", selectedDate);
                json.put("time", selectedTime);

                json.put("status", "requested");
                json.put("paymentMethod", "");
                json.put("paymentStatus", "pending");

            } catch (Exception e) {
                e.printStackTrace();
            }

            new Thread(() -> {
                String response = ApiService.bookAppointment(json.toString());

                android.util.Log.d("API_RESPONSE", response);

                requireActivity().runOnUiThread(() -> {
                    if (response != null && response.contains("success")) {
                        Toast.makeText(getContext(), "Appointment request sent", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Error booking appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        return view;
    }

    // =========================
    // LOAD DOCTORS
    // =========================
    private void setupDoctors() {

        new Thread(() -> {

            try {
                String response = ApiService.getDoctors();

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Doctor>>(){}.getType();

                List<Doctor> doctors = gson.fromJson(response, listType);

                if (doctors == null) doctors = new ArrayList<>();

                doctorsList = doctors;

                List<String> names = new ArrayList<>();

                for (Doctor doctor : doctorsList) {
                    names.add(doctor.getFullName());
                }

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_item,
                            names
                    );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spinnerDoctorsList.setAdapter(adapter);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // =========================
    // DATE PICKER
    // =========================
    private void setupCalendar(View view) {

        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select appointment date")
                        .build();

        view.findViewById(R.id.layoutCalendarClick).setOnClickListener(v ->
                datePicker.show(getParentFragmentManager(), "DATE_PICKER")
        );

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDate = datePicker.getHeaderText();
            textSelectedDate.setText(selectedDate);
        });
    }

    // =========================
    // LOAD SLOTS
    // =========================
    private void loadSlots() {

        dialog.show();

        availableSlots = new ArrayList<>(Arrays.asList(times));

        availableSlots.remove(0);
        availableSlots.remove(3);

        chipGroupSlots.removeAllViews();

        for (String slot : availableSlots) {
            Chip chip = new Chip(getContext());
            chip.setText(slot);
            chip.setCheckable(true);
            chip.setClickable(true);
            chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_selector));
            chipGroupSlots.addView(chip);
        }

        dialog.dismiss();
    }

    // =========================
    // SELECT SLOT
    // =========================
    private void setupChipSelection() {

        chipGroupSlots.setOnCheckedChangeListener((group, checkedId) -> {

            Chip chip = group.findViewById(checkedId);

            if (chip != null) {
                selectedTime = chip.getText().toString();
            }
        });
    }
}