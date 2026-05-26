package com.example.tatwa10.FragmentDoctors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.PrescriptionAdapterFrontend;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Prescription;
import com.example.tatwa10.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PatientPrescriptionFragment extends Fragment {

    private RecyclerView recyclerView;
    private PrescriptionAdapterFrontend adapter;
    private final List<Prescription> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_patient_prescription, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_prescription_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PrescriptionAdapterFrontend(list);
        recyclerView.setAdapter(adapter);

        loadMyPrescriptions();

        return view;

    }

    private void loadMyPrescriptions() {
        SharedPreferences prefs =
                requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        int patientId = prefs.getInt("patientId", -1);
        Log.d("PATIENT_ID", "ID = " + patientId);

        if (patientId == -1) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String response = ApiService.getPrescriptions(patientId);
                Log.d("PATIENT_API", response);
                JSONArray array = new JSONArray(response);

                list.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    Prescription p = new Prescription();

                    p.setDoctorName(obj.optString("doctorName", "Doctor"));
                    p.setMedicineName(obj.optString("medicaments", "No medicine"));

                    String notes = obj.optString("notes", "");
                    if (notes == null || notes.isEmpty() || notes.equals("null")) {
                        notes = "No notes";
                    }
                    p.setNotes(notes);

                    p.setBreakfast(obj.optBoolean("breakfast", false));
                    p.setLunch(obj.optBoolean("lunch", false));
                    p.setDinner(obj.optBoolean("dinner", false));
                    p.setDateStart(obj.optString("dateStart", "-"));
                    p.setDateEnd(obj.optString("dateEnd", "-"));
                    p.setDuration(obj.optInt("duration", 0));
                    list.add(p);
                }

                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}