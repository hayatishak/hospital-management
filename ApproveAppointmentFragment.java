package com.example.tatwa10.FragmentDoctors;

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

import com.example.tatwa10.Adapters.ApproveAppointmentAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApproveAppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private ApproveAppointmentAdapter adapter;
    private final List<Appointment> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_approve_appointment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_approve_appointment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ApproveAppointmentAdapter(appointmentList, listener);
        recyclerView.setAdapter(adapter);

        loadAppointments();

        return view;
    }

    // =============================
    // LOAD REQUESTED APPOINTMENTS
    // =============================

    private void loadAppointments() {

        new Thread(() -> {

            try {

                String response = ApiService.getRequestedAppointments();

                Log.d("API_APPROVE", response);

                if (response == null || response.isEmpty()) {
                    Log.e("API_ERROR", "Empty API response");
                    return;
                }

                Gson gson = new Gson(); //convert json to appointement object
                //and give list of requested  appointments
                Type listType = new TypeToken<List<Appointment>>(){}.getType();

                List<Appointment> list = gson.fromJson(response, listType);

                if (list == null) list = new ArrayList<>();

                appointmentList.clear();
                appointmentList.addAll(list);

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {

                Log.e("API_ERROR", "Fragment crash: " + e.getMessage());

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(),
                                    "Error loading appointments",
                                    Toast.LENGTH_LONG).show());
                }
            }

        }).start();
    }

    // =============================
    // BUTTON LISTENER
    // =============================

    private final ApproveAppointmentAdapter.OnItemClickListener listener =
            new ApproveAppointmentAdapter.OnItemClickListener() {

                @Override
                public void onAcceptClick(int position) {

                    Appointment a = appointmentList.get(position);

                    new Thread(() -> {

                        try {

                            ApiService.acceptAppointment(a.getId());

                            if (getActivity() == null) return;

                            getActivity().runOnUiThread(() -> {

                                Toast.makeText(getContext(),
                                        "Appointment Accepted",
                                        Toast.LENGTH_SHORT).show();

                                //  REMOVE ITEM FROM requested LIST
                                appointmentList.remove(position);
                                adapter.notifyItemRemoved(position);

                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }).start();
                }




                @Override
                public void onRejectClick(int position) {

                    Appointment a = appointmentList.get(position);

                    new Thread(() -> {

                        try {

                            ApiService.rejectAppointment(a.getId());

                            if (getActivity() == null) return;

                            getActivity().runOnUiThread(() -> {

                                Toast.makeText(getContext(),
                                        "Appointment Rejected",
                                        Toast.LENGTH_SHORT).show();

                                //  REMOVE ITEM FROM LIST
                                appointmentList.remove(position);
                                adapter.notifyItemRemoved(position);

                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }).start();
                }
            };
}