package com.example.tatwa10.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tatwa10.R;

import org.json.JSONObject;

public class BookLabTestFragment extends Fragment {

    private Spinner spinnerTests;
    private EditText editDate;
    private EditText editNotes;
    private Button buttonSubmit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_lab_test, container, false);

        spinnerTests = view.findViewById(R.id.spinner_tests);
        editDate = view.findViewById(R.id.edit_date);
        editNotes = view.findViewById(R.id.edit_notes);
        buttonSubmit = view.findViewById(R.id.button_submit_lab);

        String[] tests = {
                "Complete Blood Count (CBC)",
                "Blood Sugar Test",
                "X-Ray",
                "MRI Scan",
                "COVID-19 Test"
        };

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        tests);

        spinnerTests.setAdapter(adapter);

        buttonSubmit.setOnClickListener(v -> submitBooking());

        return view;
    }

    private void submitBooking() {

        String testName = spinnerTests.getSelectedItem().toString();
        String date = editDate.getText().toString().trim();
        String notes = editNotes.getText().toString().trim();

        if (TextUtils.isEmpty(date)) {
            editDate.setError("Please select a date");
            return;
        }

        try {
            JSONObject json = new JSONObject();

            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("user", getContext().MODE_PRIVATE);

            int patientId = prefs.getInt("patientId", 0);
            String patientName = prefs.getString("name", "Unknown");

            json.put("patientId", patientId);
            json.put("patientName", patientName);
            json.put("doctorId", JSONObject.NULL);
            json.put("doctorName", JSONObject.NULL);
            json.put("testName", testName);
            json.put("preferredDate", date);
            json.put("notes", notes);

            String url = "http://172.20.10.5:5116/api/Lab/request";

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,
                    response -> {
                        Toast.makeText(getContext(),
                                "Lab Request Sent",
                                Toast.LENGTH_LONG).show();

                        editDate.setText("");
                        editNotes.setText("");
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(getContext(),
                                "Error sending request",
                                Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(getContext()).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}