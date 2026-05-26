package com.example.tatwa10.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.PrescriptionAdapterFrontend;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Patient;
import com.example.tatwa10.ModelClass.Prescription;
import com.example.tatwa10.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PrescriptionFragment extends Fragment {

    private RecyclerView recyclerView;
    private Spinner spinnerPatients;

    private PrescriptionAdapterFrontend adapter;



    private LinearLayout layoutPatientInfo;

    private TextView textName, textEmail, textPhone;
    private TextView textDob, textNationalId, textBlood;
    private TextView textAllergies, textDisease, textMedications, textAddress;

    private final List<Patient> patientList = new ArrayList<>();
    private final List<Prescription> prescriptionList = new ArrayList<>();

    private int selectedPatientId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prescription, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_prescription_list);

        spinnerPatients = view.findViewById(R.id.spinner_patients);


        layoutPatientInfo = view.findViewById(R.id.layout_patient_info);

        textName = view.findViewById(R.id.text_name);
        textEmail = view.findViewById(R.id.text_email);
        textPhone = view.findViewById(R.id.text_phone);

        textDob = view.findViewById(R.id.text_dob);
        textNationalId = view.findViewById(R.id.text_national_id);
        textBlood = view.findViewById(R.id.text_blood);

        textAllergies = view.findViewById(R.id.text_allergies);
        textDisease = view.findViewById(R.id.text_disease);
        textMedications = view.findViewById(R.id.text_medications);
        textAddress = view.findViewById(R.id.text_address);

        FloatingActionButton fab =
                view.findViewById(R.id.button_add_prescription);

        fab.setOnClickListener(v -> showAddDialog());

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext()));

        adapter = new PrescriptionAdapterFrontend(prescriptionList);

        recyclerView.setAdapter(adapter);

        loadPatients();



        return view;
    }

    // ===============================
    // LOAD PRESCRIPTIONS
    // ===============================
    private void loadPrescriptions(int patientId) {

        new Thread(() -> {

            try {

                SharedPreferences prefs =
                        requireActivity()
                                .getSharedPreferences(
                                        "user",
                                        Context.MODE_PRIVATE
                                );

                int doctorId = prefs.getInt("doctorId", -1);

                String response =
                        ApiService.getDoctorPatientPrescriptions(
                                patientId,
                                doctorId
                        );

                Log.d("PRESCRIPTION_API", response);

                if (response == null || response.isEmpty())
                    return;

                JSONArray array = new JSONArray(response);

                prescriptionList.clear();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject obj = array.getJSONObject(i);

                    Prescription p = new Prescription();

                    p.setMedicineName(
                            obj.optString(
                                    "medicaments",
                                    "No medicine"
                            )
                    );

                    p.setDoctorName(
                            obj.optString(
                                    "doctorName",
                                    "Unknown"
                            )
                    );

                    String notes =
                            obj.optString("notes", "");

                    if (notes == null
                            || notes.equals("null")
                            || notes.isEmpty()) {

                        notes = "No notes";
                    }

                    p.setNotes(notes);

                    p.setDuration(
                            obj.optInt("duration", 0)
                    );

                    p.setDateStart(
                            obj.optString("dateStart", "-")
                    );

                    p.setDateEnd(
                            obj.optString("dateEnd", "-")
                    );

                    p.setBreakfast(
                            obj.optBoolean("breakfast", false)
                    );

                    p.setLunch(
                            obj.optBoolean("lunch", false)
                    );

                    p.setDinner(
                            obj.optBoolean("dinner", false)
                    );

                    prescriptionList.add(p);
                }

                requireActivity().runOnUiThread(() ->
                        adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // ===============================
    // LOAD PATIENTS
    // ===============================
    private void loadPatients() {

        new Thread(() -> {

            try {

                String response = ApiService.getPatients();

                JSONArray array = new JSONArray(response);

                patientList.clear();

                List<String> names = new ArrayList<>();

                names.add("Select Patient");

                for (int i = 0; i < array.length(); i++) {

                    JSONObject obj = array.getJSONObject(i);

                    Patient p = new Patient();

                    p.setPatientId(
                            obj.optInt("patientId", -1)
                    );

                    p.setFullName(
                            obj.optString(
                                    "fullName",
                                    "Unknown"
                            )
                    );

                    p.setEmail(
                            obj.optString(
                                    "email",
                                    "No email"
                            )
                    );

                    p.setPhone(
                            obj.optString(
                                    "phone",
                                    "No phone"
                            )
                    );

                    p.setBloodType(
                            obj.optString(
                                    "bloodType",
                                    ""
                            )
                    );

                    p.setAddress(
                            obj.optString(
                                    "address",
                                    ""
                            )
                    );

                    p.setDateOfBirth(
                            obj.optString(
                                    "dateOfBirth",
                                    ""
                            )
                    );

                    p.setNationalId(
                            obj.optString(
                                    "nationalId",
                                    ""
                            )
                    );

                    p.setAllergies(
                            obj.optString(
                                    "allergies",
                                    ""
                            )
                    );

                    p.setDiseases(
                            obj.optString(
                                    "diseases",
                                    ""
                            )
                    );

                    p.setMedications(
                            obj.optString(
                                    "medications",
                                    ""
                            )
                    );

                    patientList.add(p);

                    names.add(p.getFullName());
                }

                requireActivity().runOnUiThread(() -> {

                    ArrayAdapter<String> spinnerAdapter =
                            new ArrayAdapter<>(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    names
                            );

                    spinnerPatients.setAdapter(spinnerAdapter);

                    spinnerPatients.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                @Override
                                public void onItemSelected(
                                        AdapterView<?> parent,
                                        View view,
                                        int position,
                                        long id) {

                                    if (position == 0)
                                        return;

                                    Patient patient =
                                            patientList.get(position - 1);

                                    selectedPatientId =
                                            patient.getPatientId();

                                    layoutPatientInfo
                                            .setVisibility(View.VISIBLE);

                                    textName.setText(
                                            "Name: "
                                                    + patient.getFullName()
                                    );

                                    textEmail.setText(
                                            "Email: "
                                                    + patient.getEmail()
                                    );

                                    textPhone.setText(
                                            "Phone: "
                                                    + patient.getPhone()
                                    );

                                    textDob.setText(
                                            "DOB: "
                                                    + patient.getDateOfBirth()
                                    );

                                    textNationalId.setText(
                                            "National ID: "
                                                    + patient.getNationalId()
                                    );

                                    textBlood.setText(
                                            "Blood Type: "
                                                    + patient.getBloodType()
                                    );

                                    textAllergies.setText(
                                            "Allergies: "
                                                    + patient.getAllergies()
                                    );

                                    textDisease.setText(
                                            "Diseases: "
                                                    + patient.getDiseases()
                                    );

                                    textMedications.setText(
                                            "Medications: "
                                                    + patient.getMedications()
                                    );

                                    textAddress.setText(
                                            "Address: "
                                                    + patient.getAddress()
                                    );

                                    prescriptionList.clear();

                                    adapter.notifyDataSetChanged();

                                    loadPrescriptions(selectedPatientId);
                                }

                                @Override
                                public void onNothingSelected(
                                        AdapterView<?> parent) {

                                }
                            });
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // ===============================
    // SAVE PRESCRIPTION
    // ===============================
    private void savePrescription(int patientId,
                                  String medicine,
                                  String notes,
                                  int duration) {

        new Thread(() -> {

            try {

                Calendar cal = Calendar.getInstance();

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                        );

                String dateStart =
                        sdf.format(cal.getTime());

                cal.add(Calendar.DATE, duration - 1);

                String dateEnd =
                        sdf.format(cal.getTime());

                JSONObject json = new JSONObject();

                json.put("patientId", patientId);

                SharedPreferences prefs =
                        requireActivity()
                                .getSharedPreferences(
                                        "user",
                                        Context.MODE_PRIVATE
                                );

                int doctorId =
                        prefs.getInt("doctorId", -1);

                if (doctorId == -1) {

                    Toast.makeText(
                            getContext(),
                            "Doctor ID not found",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                json.put("doctorId", doctorId);

                json.put("medicaments", medicine);

                json.put("notes", notes);

                json.put("duration", duration);

                json.put("dateStart", dateStart);

                json.put("dateEnd", dateEnd);

                json.put("breakfast", false);
                json.put("lunch", false);
                json.put("dinner", false);

                ApiService.addPrescription(json.toString());

                requireActivity().runOnUiThread(() -> {

                    Toast.makeText(
                            getContext(),
                            "Saved",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadPrescriptions(selectedPatientId);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    // ===============================
    // DIALOG
    // ===============================
    private void showAddDialog() {

        View view =
                LayoutInflater.from(getContext())
                        .inflate(
                                R.layout.dialog_add_prescription,
                                null
                        );

        EditText input =
                view.findViewById(R.id.edit_input);

        LinearLayout durationLayout =
                view.findViewById(R.id.duration_layout);

        TextView stepText =
                view.findViewById(R.id.text_step);

        EditText customDays =
                view.findViewById(R.id.edit_custom_days);

        Button b3 = view.findViewById(R.id.btn_3);
        Button b5 = view.findViewById(R.id.btn_5);
        Button b7 = view.findViewById(R.id.btn_7);
        Button b10 = view.findViewById(R.id.btn_10);
        Button b14 = view.findViewById(R.id.btn_14);
        Button bCustom = view.findViewById(R.id.btn_custom);

        final int[] step = {1};
        final String[] medicine = {""};
        final String[] notes = {""};
        final int[] duration = {0};

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(
                        requireContext()
                )
                        .setTitle("New Prescription")
                        .setView(view)
                        .setCancelable(false)
                        .setNegativeButton(
                                "Cancel",
                                (d, w) -> d.dismiss()
                        )
                        .setPositiveButton(
                                "Next",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            Button next =
                    dialog.getButton(
                            androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE
                    );

            next.setOnClickListener(v -> {

                if (step[0] == 1) {

                    if (input.getText()
                            .toString()
                            .trim()
                            .isEmpty()) {

                        Toast.makeText(
                                getContext(),
                                "Enter medicine",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    medicine[0] =
                            input.getText()
                                    .toString()
                                    .trim();

                    input.setText("");

                    input.setHint("Enter notes");

                    step[0] = 2;

                    stepText.setText("Step 2: Notes");

                } else if (step[0] == 2) {

                    String n =
                            input.getText()
                                    .toString()
                                    .trim();

                    if (n.isEmpty()) {
                        n = "No notes";
                    }

                    notes[0] = n;

                    input.setVisibility(View.GONE);

                    durationLayout.setVisibility(View.VISIBLE);

                    step[0] = 3;

                    stepText.setText("Step 3: Duration");

                    next.setText("Save");

                } else {

                    if (customDays.getVisibility()
                            == View.VISIBLE) {

                        String val =
                                customDays.getText()
                                        .toString()
                                        .trim();

                        if (!val.isEmpty()) {

                            try {

                                duration[0] =
                                        Integer.parseInt(val);

                            } catch (Exception e) {

                                duration[0] = 0;
                            }
                        }
                    }

                    if (duration[0] <= 0) {

                        Toast.makeText(
                                getContext(),
                                "Select duration",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    if (selectedPatientId == -1) {

                        Toast.makeText(
                                getContext(),
                                "Select patient first",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    savePrescription(
                            selectedPatientId,
                            medicine[0],
                            notes[0],
                            duration[0]
                    );

                    dialog.dismiss();
                }
            });
        });

        b3.setOnClickListener(v -> duration[0] = 3);
        b5.setOnClickListener(v -> duration[0] = 5);
        b7.setOnClickListener(v -> duration[0] = 7);
        b10.setOnClickListener(v -> duration[0] = 10);
        b14.setOnClickListener(v -> duration[0] = 14);

        bCustom.setOnClickListener(v -> {

            customDays.setVisibility(View.VISIBLE);

            duration[0] = 0;
        });

        dialog.show();
    }
}