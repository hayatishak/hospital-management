package com.example.tatwa10.Fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.Models.LabTest;
import com.example.tatwa10.R;
import com.example.tatwa10.Utils.FakeLabDatabase;

public class RequestLabTestFragment extends Fragment {

    private EditText editPatientId, editTestName, editReport;
    private Button buttonUploadPdf, buttonSend;

    private String selectedPdfName = "No PDF Selected";

    private ActivityResultLauncher<String> pdfPickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request_lab_test, container, false);

        editPatientId = view.findViewById(R.id.edit_patient_id);
        editTestName = view.findViewById(R.id.edit_test_name);
        editReport = view.findViewById(R.id.edit_report);

        buttonUploadPdf = view.findViewById(R.id.button_upload_pdf);
        buttonSend = view.findViewById(R.id.button_send_result);

        // Modern Activity Result API for PDF picking
        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPdfName = uri.getLastPathSegment();
                        Toast.makeText(getContext(), "PDF Selected: " + selectedPdfName, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        buttonUploadPdf.setOnClickListener(v -> pdfPickerLauncher.launch("application/pdf"));

        buttonSend.setOnClickListener(v -> sendResult());

        return view;
    }

    private void sendResult() {

        String patientId = editPatientId.getText().toString().trim();
        String testName = editTestName.getText().toString().trim();
        String report = editReport.getText().toString().trim();

        if (TextUtils.isEmpty(patientId) ||
                TextUtils.isEmpty(testName) ||
                TextUtils.isEmpty(report)) {

            Toast.makeText(getContext(), "All fields required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add new lab test to fake database
        LabTest result = new LabTest(
                FakeLabDatabase.labResults.size() + 1, // auto-increment ID
                testName,
                report,
                "PDF: " + selectedPdfName, // store PDF name in date field temporarily
                patientId
        );

        FakeLabDatabase.labResults.add(result);

        new AlertDialog.Builder(getContext())
                .setTitle("Success")
                .setMessage("Lab Result Sent to Patient")
                .setPositiveButton("OK", null)
                .show();

        // Reset fields
        editPatientId.setText("");
        editTestName.setText("");
        editReport.setText("");
        selectedPdfName = "No PDF Selected";
    }
}