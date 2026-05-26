package com.example.tatwa10.FragmentDoctors;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.PendingAppointmentAdapter;
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

public class PendingAppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private PendingAppointmentAdapter adapter;
    private List<Appointment> pendingAppointments = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pending_appointments, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_pending_appointment);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendingAppointmentAdapter(pendingAppointments, listener);
        recyclerView.setAdapter(adapter);

        loadPending();

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        loadPending(); // reload when returning
    }

    private void loadPending() {

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
                                .getAcceptedAppointments(doctorId);

                Log.d("API_PENDING", response);

                if (response == null || response.isEmpty()) {
                    Log.e("API_ERROR", "Pending API empty");
                    return;
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Appointment>>(){}.getType();

                List<Appointment> list = gson.fromJson(response, listType);

                if (list == null) list = new ArrayList<>();

                pendingAppointments.clear();
                pendingAppointments.addAll(list);

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("API_ERROR", e.getMessage());
            }

        }).start();
    }

    private final PendingAppointmentAdapter.OnItemClickListener listener =
            new PendingAppointmentAdapter.OnItemClickListener() {

                @Override
                public void onCallClick(String number) {

                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }

                @Override
                public void onMessageClick(String number) {

                    Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                    messageIntent.setData(Uri.parse("sms:" + number));
                    startActivity(messageIntent);
                }

                @Override
                public void onCompletedClick(Appointment appointment, int position) {

                    new Thread(() -> {

                        ApiService.completeAppointment(appointment.getId());

                        if (getActivity() == null) return;

                        getActivity().runOnUiThread(() -> {

                            if (position >= 0 && position < pendingAppointments.size()) {
                                pendingAppointments.remove(position);
                                adapter.notifyItemRemoved(position);
                            }

                        });

                    }).start();
                }


            };
}