package com.example.tatwa10.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.tatwa10.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class PatientLabResultsFragment extends Fragment {

    private LinearLayout container;
    private int patientId;

    private String BASE_URL = "http://172.20.10.5:5116/api/Lab/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_lab_results, parent, false);

        container = view.findViewById(R.id.container_results);

        //  GET REAL PATIENT ID
        SharedPreferences prefs =
                requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        patientId = prefs.getInt("patientId", 0);

        //  DEBUG (optional)
        Toast.makeText(getContext(), "Patient ID: " + patientId, Toast.LENGTH_SHORT).show();

        loadResults();

        return view;
    }

    private void loadResults() {

        String url = BASE_URL + "results/" + patientId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> showResults(response),
                error -> {
                    String msg = "Error loading results";

                    if (error.networkResponse != null) {
                        msg += " | Code: " + error.networkResponse.statusCode;
                    } else {
                        msg += " | No server response";
                    }

                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
        );

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void showResults(JSONArray array) {

        container.removeAllViews();

        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                String test = obj.getString("testName");
                String report = obj.getString("report");
                String date = obj.getString("resultDate");

                String pdfUrl = obj.optString("pdfUrl");

                View card = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_patient_result, container, false);

                TextView tvTest = card.findViewById(R.id.text_test);
                TextView tvReport = card.findViewById(R.id.text_report);
                TextView tvDate = card.findViewById(R.id.text_date);
                Button btnPdf = card.findViewById(R.id.button_view_pdf);

                tvTest.setText(test);
                tvReport.setText(report);
                tvDate.setText(date);

                //  HANDLE PDF BUTTON
                if (pdfUrl == null || pdfUrl.isEmpty() || pdfUrl.equals("null")) {
                    btnPdf.setVisibility(View.GONE);
                } else {

                    btnPdf.setVisibility(View.VISIBLE);

                    btnPdf.setOnClickListener(v -> {
                        try {
                            String fullUrl = "http://172.20.10.5:5116" + pdfUrl;


                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(fullUrl)); //  NO application/pdf

                            startActivity(intent);

                        } catch (Exception e) {
                            Toast.makeText(getContext(),
                                    "Cannot open PDF",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                container.addView(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}