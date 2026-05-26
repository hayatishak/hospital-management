package com.example.tatwa10.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.NotificationAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Notification;
import com.example.tatwa10.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        loadNotifications();

        return view;
    }

    private void loadNotifications() {

        new Thread(() -> {

            try {

                SharedPreferences prefs =
                        requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

                int patientId = prefs.getInt("patientId", 0);

                String response = ApiService.getNotifications(patientId);

                Log.d("NOTIFICATIONS_API", response);

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Notification>>(){}.getType();

                List<Notification> list = gson.fromJson(response, listType);

                if(list == null)
                    list = new ArrayList<>();

                List<Notification> finalList = list;

                requireActivity().runOnUiThread(() -> {

                    notifications.clear();
                    notifications.addAll(finalList);
                    adapter.notifyDataSetChanged();

                });

            } catch (Exception e){
                e.printStackTrace();
            }

        }).start();
    }
}