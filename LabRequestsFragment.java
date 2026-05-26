package com.example.tatwa10.Fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tatwa10.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class LabRequestsFragment extends Fragment {

    private LinearLayout container;


    private String BASE_URL = "http://172.20.10.5:5116/api/Lab/";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lab_requests, parent, false);

        container = view.findViewById(R.id.container_requests);

        loadRequests();

        return view;
    }

    private void loadRequests() {

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                BASE_URL + "requests",
                null,
                response -> showRequests(response),
                error -> Toast.makeText(getContext(),
                        "Error loading requests", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void showRequests(JSONArray array) {

        container.removeAllViews();

        try {
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                String name = obj.getString("patientName");
                String test = obj.getString("testName");
                String date = obj.getString("preferredDate");
                String status = obj.getString("status");

                //  Inflate card
                View card = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_lab_request, container, false);
                int id = obj.getInt("id");

                Button btnProcess = card.findViewById(R.id.button_process);

                btnProcess.setOnClickListener(v -> {

                    AddLabResultFragment fragment = new AddLabResultFragment();

                    Bundle bundle = new Bundle();
                    bundle.putInt("requestId", id);
                    bundle.putInt("patientId", obj.optInt("patientId"));
                    bundle.putString("patientName", name);
                    bundle.putString("testName", test);

                    fragment.setArguments(bundle);

                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                });
                TextView tvName = card.findViewById(R.id.text_patient);
                TextView tvTest = card.findViewById(R.id.text_test);
                TextView tvDate = card.findViewById(R.id.text_date);
                TextView tvStatus = card.findViewById(R.id.text_status);

                //  Set data
                tvName.setText(name);
                tvTest.setText("Test: " + test);
                tvDate.setText("Date: " + date);
                tvStatus.setText("Status: " + status);

                container.addView(card);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void processRequest(int id) {

        String url = BASE_URL + "process/" + id;

        StringRequest request = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {
                    Toast.makeText(getContext(),
                            "Request is now Processing",
                            Toast.LENGTH_SHORT).show();

                    loadRequests(); //  refresh list
                },
                error -> Toast.makeText(getContext(),
                        "Error processing request",
                        Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(getContext()).add(request);
    }
}