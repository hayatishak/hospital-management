package com.example.tatwa10.Fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.MainActivity;
import com.example.tatwa10.Models.LabTest;
import com.example.tatwa10.R;
import com.example.tatwa10.Utils.FakeLabDatabase;

public class LabResultsFragment extends Fragment {

    private LinearLayout container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup containerView,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lab_results, containerView, false);
        container = view.findViewById(R.id.results_container);

        loadResults();

        return view;
    }

    private void loadResults() {
        container.removeAllViews();

        boolean hasResults = false;

        for (LabTest result : FakeLabDatabase.labResults) {

            if (result.getPatientId().equals(String.valueOf(MainActivity.patientId))) {

                TextView tv = new TextView(getContext());
                tv.setText("Test: " + result.getName() +
                        "\nResult: " + result.getResult() +
                        "\nDate: " + result.getDate());
                tv.setPadding(0, 20, 0, 20);

                container.addView(tv);
                hasResults = true;
            }
        }

        if (!hasResults) {
            TextView empty = new TextView(getContext());
            empty.setText("No Lab Results Available");
            container.addView(empty);
        }
    }
}