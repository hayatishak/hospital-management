package com.example.tatwa10.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.example.tatwa10.R;

public class HospitalBranchesFragment extends Fragment {

    public HospitalBranchesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_hospital_branches, container, false);

        // Beirut Branch
        view.findViewById(R.id.btn_beirut).setOnClickListener(v -> {
            openMap("33.8938,35.5018"); // Beirut
        });

        // Tripoli Branch
        view.findViewById(R.id.btn_tripoli).setOnClickListener(v -> {
            openMap("34.4367,35.8497"); // Tripoli
        });

        // Zahle Branch
        view.findViewById(R.id.btn_zahle).setOnClickListener(v -> {
            openMap("33.8462,35.9020"); // Zahle
        });
        //  Contact Support button
        view.findViewById(R.id.btn_contact_support).setOnClickListener(v -> {
            callSupport();
        });

        return view;

    }

    private void openMap(String latLng) {
        Uri uri = Uri.parse("geo:" + latLng + "?q=" + latLng + "(Hospicare Hospital)");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
    private void callSupport() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:81805208"));
        startActivity(intent);
    }
}