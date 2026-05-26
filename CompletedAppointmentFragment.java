package com.example.tatwa10.FragmentDoctors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.CompletedAppointmentAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CompletedAppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private CompletedAppointmentAdapter adapter;
    private List<Appointment> completedAppointments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_completed_appointment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_completed_appointment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CompletedAppointmentAdapter(completedAppointments);
        recyclerView.setAdapter(adapter);

        loadCompleted();

        return view;
    }

    private void loadCompleted() {

        new Thread(() -> {

            try {

                SharedPreferences prefs =
                        requireActivity()
                                .getSharedPreferences(
                                        "user",
                                        Context.MODE_PRIVATE
                                );

                int doctorId =
                        prefs.getInt("doctorId", 0);

                String response =
                        ApiService
                                .getCompletedAppointments(doctorId);

                Log.d("API_COMPLETED", response);

                if (response == null || response.isEmpty()) {
                    Log.e("API_ERROR", "Completed API empty");
                    return;
                }

                Gson gson = new Gson(); //Here we use it to convert JSON
                // response from backend into Java objects.
                Type listType = new TypeToken<List<Appointment>>(){}.getType();
                //This line tells Gson:
                //The JSON contains a List of Appointment objects.
                //Because Gson cannot automatically know this type :
                //List<Appointment>
                //So we use TypeToken.
                List<Appointment> list = gson.fromJson(response, listType);
                //Convert JSON into Java Objects
                if (list == null) list = new ArrayList<>();

                completedAppointments.clear();
                completedAppointments.addAll(list); //add list of completed
                // appointments

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {

                e.printStackTrace();
                Log.e("API_ERROR", e.getMessage());

            }

        }).start();
    }

}