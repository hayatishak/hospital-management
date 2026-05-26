package com.example.tatwa10.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.AppointmentAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class AppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private FloatingActionButton buttonAddAppointment;
    private AppointmentAdapter adapter;
    private ProgressBar progressBar;

    //  GLOBAL LIST
    private List<Appointment> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_appointment_list);
        buttonAddAppointment = view.findViewById(R.id.button_add_appointment);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //  INIT ADAPTER ONCE
        adapter = new AppointmentAdapter(getContext(), appointmentList);
        recyclerView.setAdapter(adapter);

        buttonAddAppointment.setOnClickListener(v ->
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new BookAppointmentFragment())
                        .addToBackStack(null)
                        .commit()
        );

        loadAppointments();

        return view;
    }

    private void loadAppointments() {

        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {

            SharedPreferences prefs = requireActivity()
                    .getSharedPreferences("user", Context.MODE_PRIVATE);

            int patientId = prefs.getInt("patientId", 0);

            String response = ApiService.getAcceptedAppointmentsByPatient(patientId);

            Log.d("API_RESPONSE", response);

            if (getActivity() == null) return;

            getActivity().runOnUiThread(() -> {

                try {
                    if (response != null && !response.isEmpty()) {

                        Gson gson = new Gson();

                        List<Appointment> newList = gson.fromJson(
                                response,
                                new TypeToken<List<Appointment>>() {}.getType()
                        );

                        //  UPDATE LIST PROPERLY
                        appointmentList.clear();
                        appointmentList.addAll(newList);

                        //  REFRESH UI
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);

            });

        }).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAppointments(); //  FORCE REFRESH AFTER PAYMENT
    }
}