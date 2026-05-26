package com.example.tatwa10.Fragments;

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

import com.android.volley.toolbox.Volley;
import com.example.tatwa10.MultipartRequest;
import com.example.tatwa10.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddLabResultFragment extends Fragment {

    private Button buttonUploadPdf;
    private TextView textPdfName;
    private Uri selectedPdfUri;
    private String selectedPdfName = "No PDF selected";

    private EditText editReport;
    private Button buttonSubmit;

    private int requestId, patientId;
    private String patientName, testName;

    private ActivityResultLauncher<String> pdfPickerLauncher;

    private String BASE_URL = "http://172.20.10.5:5116/api/Lab/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_lab_result, container, false);

        editReport = view.findViewById(R.id.edit_report);
        buttonSubmit = view.findViewById(R.id.button_submit);

        buttonUploadPdf = view.findViewById(R.id.button_upload_pdf);
        textPdfName = view.findViewById(R.id.text_pdf_name);

        //  PDF PICKER
        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedPdfUri = uri;
                        selectedPdfName = uri.getLastPathSegment();
                        textPdfName.setText("Selected: " + selectedPdfName);
                    }
                }
        );

        buttonUploadPdf.setOnClickListener(v ->
                pdfPickerLauncher.launch("application/pdf"));

        //  Receive data
        Bundle bundle = getArguments();
        if (bundle != null) {
            requestId = bundle.getInt("requestId");
            patientId = bundle.getInt("patientId");
            patientName = bundle.getString("patientName");
            testName = bundle.getString("testName");
        }

        buttonSubmit.setOnClickListener(v -> submitResult());

        return view;
    }

    private void submitResult() {

        String report = editReport.getText().toString().trim();

        if (TextUtils.isEmpty(report)) {
            editReport.setError("Enter report");
            return;
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("labRequestId", String.valueOf(requestId));
            params.put("report", report);

            byte[] fileData = null;

            if (selectedPdfUri != null) {
                InputStream inputStream = requireContext()
                        .getContentResolver()
                        .openInputStream(selectedPdfUri);

                fileData = readBytes(inputStream);
            }

            MultipartRequest request = new MultipartRequest(
                    BASE_URL + "result-with-file",
                    params,
                    fileData,
                    selectedPdfName,
                    response -> {
                        Toast.makeText(getContext(),
                                "Result + PDF uploaded",
                                Toast.LENGTH_LONG).show();

                        requireActivity().onBackPressed();
                    },
                    error -> {
                        String message = "Upload failed";

                        try {
                            if (error.networkResponse != null && error.networkResponse.data != null) {
                                String response = new String(error.networkResponse.data);
                                message += "\nCode: " + error.networkResponse.statusCode;
                                message += "\n" + response;
                            } else {
                                message += "\nNo response from server";
                            }
                        } catch (Exception e) {
                            message += "\nError parsing response";
                        }

                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
            );

            Volley.newRequestQueue(requireContext()).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //  HELPER METHOD (MISSING BEFORE)
    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }
}