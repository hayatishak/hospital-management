package com.example.tatwa10.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tatwa10.R;

public class HomeCollectionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_collection, container, false);

        Button buttonRequest = view.findViewById(R.id.button_request_home);

        buttonRequest.setOnClickListener(v ->
                Toast.makeText(getContext(),
                        "Home Collection Requested!",
                        Toast.LENGTH_LONG).show());

        return view;
    }
}