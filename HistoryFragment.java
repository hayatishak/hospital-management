package com.example.tatwa10.FragmentDoctors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private PrescriptionAdapterFrontend adapter;
    private final List<Prescription> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recycler_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PrescriptionAdapterFrontend(list);
        recyclerView.setAdapter(adapter);

        loadHistory();

        return view;
    }

    private void loadHistory() {
        SharedPreferences prefs =
                requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        int doctorId = prefs.getInt("doctorId", -1);

        if (doctorId == -1) {
            Toast.makeText(getContext(), "Doctor not found", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                String response = ApiService.getDoctorPrescriptions(doctorId);
                JSONArray array = new JSONArray(response);

                list.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    Prescription p = new Prescription();
                    p.setPatientName(obj.optString("patientName", "Unknown"));
                    p.setMedicineName(obj.optString("medicineName", "No medicine"));
                    p.setNotes(obj.optString("notes", ""));
                    p.setDuration(obj.optInt("duration", 0));
                    p.setDateStart(obj.optString("dateStart", "-"));
                    p.setDateEnd(obj.optString("dateEnd", "-"));
                    p.setBreakfast(obj.optBoolean("breakfast", false));
                    p.setLunch(obj.optBoolean("lunch", false));
                    p.setDinner(obj.optBoolean("dinner", false));

                    list.add(p);
                }

                requireActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Failed to load doctor history", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
}