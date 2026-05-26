package com.example.tatwa10.Fragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.MainActivity;
import com.example.tatwa10.R;

public class MedicalRecordInfoFragment extends Fragment {

    private TextView textViewInfoTitle;
    private ImageView imageViewInfoImage;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_medical_record_info, container, false);
        MainActivity.currentFragment = "medicalRecordInfo";

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);

        textViewInfoTitle = view.findViewById(R.id.text_view_medical_records_info_title);
        imageViewInfoImage = view.findViewById(R.id.image_view_medical_record);

        if (getArguments() != null) {
            dialog.show();

            String title = getArguments().getString("title");
            String imageUriString = getArguments().getString("imageUri");

            textViewInfoTitle.setText(title);

            //  Load local image if provided
            if (!TextUtils.isEmpty(imageUriString)) {
                Uri uri = Uri.parse(imageUriString);
                imageViewInfoImage.setImageURI(uri);
            } else {
                //  Fallback demo image from drawable
                imageViewInfoImage.setImageResource(R.drawable.ic_launcher_background);
            }

            dialog.dismiss();
        }

        return view;
    }
}
