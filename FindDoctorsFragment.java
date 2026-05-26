package com.example.tatwa10.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.DoctorsAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.MainActivity;
import com.example.tatwa10.ModelClass.Doctor;
import com.example.tatwa10.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class FindDoctorsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DoctorsAdapter adapter;
    private TextInputEditText editTextSearchDoctor;

    public static final String DOCTOR_NAME = "doctor_name";
    public static final String DOCTOR_FIELD = "doctor_field";
    public static final String DOCTOR_PHONE = "doctor_phone";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        MainActivity.navigationView.setCheckedItem(R.id.nav_doctors);
        MainActivity.currentFragment = "findDoctors";

        View view = inflater.inflate(R.layout.fragment_find_doctors, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_doctors_list);
        editTextSearchDoctor = view.findViewById(R.id.edit_text_search_doctor);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new DoctorsAdapter(getContext());
        recyclerView.setAdapter(adapter);

        loadDoctorsFromAPI();

        adapter.setOnItemClickListener(doctor -> {
            Bundle args = new Bundle();
            args.putString(DOCTOR_NAME, doctor.getFullName());
            args.putString(DOCTOR_FIELD, doctor.getSpecialization());
            args.putString(DOCTOR_PHONE, doctor.getPhone());
            args.putString("doctor_image", doctor.getImageUrl());
            args.putFloat("doctor_rating", doctor.getRating());
            args.putInt("doctor_id", doctor.getId());
            Log.d("SEND_ID", "Sending ID = " + doctor.getId());
            DoctorInfoFragment fragment = new DoctorInfoFragment();
            fragment.setArguments(args);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        editTextSearchDoctor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void loadDoctorsFromAPI() {
        new Thread(() -> {
            try {
                String response = ApiService.getDoctors();
                Log.d("API_RESPONSE", response);

                if (response == null || response.isEmpty()) {
                    Log.e("API_ERROR", "Empty API response");
                    return;
                }

                Gson gson = new Gson();
                Type listType = new TypeToken<List<Doctor>>() {}.getType();
                List<Doctor> doctors = gson.fromJson(response, listType);

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> adapter.setDoctorList(doctors));

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("API_ERROR", e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        }).start();
    }
}