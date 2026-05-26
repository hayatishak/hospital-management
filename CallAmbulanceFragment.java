package com.example.tatwa10.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.MainActivity;
import com.example.tatwa10.R;
import com.google.android.material.card.MaterialCardView;

public class CallAmbulanceFragment extends Fragment {

    private MaterialCardView cardCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_ambulance, container, false);
        MainActivity.currentFragment = "callAmbulance";

        cardCall = view.findViewById(R.id.card_call);

        cardCall.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);

            } else {
                callAmbulance();
            }
        });

        return view;
    }

    private void callAmbulance() {
        new AlertDialog.Builder(getContext())
                .setTitle("Call Ambulance")
                .setMessage("Are you sure you want to call an ambulance?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:114"));
                    startActivity(callIntent);
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callAmbulance();
            } else {
                Toast.makeText(getContext(),
                        "Permission denied to make calls from this app",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
