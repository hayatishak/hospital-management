package com.example.tatwa10.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.MainActivity;
import com.example.tatwa10.ModelClass.Profile;
import com.example.tatwa10.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 12;

    private CircleImageView imageViewProfileImage;
    private EditText editTextProfileName;
    private EditText editTextProfileEmail;
    private TextView textViewProfileNumber;
    private RadioGroup radioGroupSex;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private EditText editTextProfileAge;
    private Button buttonContinueSave;

    private Uri imageUri;
    private ProgressDialog dialog;

    // 🔹 Fake local profile (demo)
    private Profile profile = new Profile("John Doe", "john@email.com", "+0000000000", true, "25");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_profile, container, false);

        MainActivity.currentFragment = "editProfile";


        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Loading...");
        dialog.setCancelable(false);
        dialog.show();


        editTextProfileName = view.findViewById(R.id.edit_text_profile_name);
        editTextProfileEmail = view.findViewById(R.id.edit_text_profile_email);

        radioGroupSex = view.findViewById(R.id.radio_group_profile_sex);
        radioButtonMale = view.findViewById(R.id.radio_button_profile_male);
        radioButtonFemale = view.findViewById(R.id.radio_button_profile_female);
        editTextProfileAge = view.findViewById(R.id.edit_text_profile_age);
        buttonContinueSave = view.findViewById(R.id.button_profile_continue_save);

        buttonContinueSave.setText("Save");

        setUpProfile();

        imageViewProfileImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        });

        buttonContinueSave.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void setUpProfile() {

        editTextProfileName.setText(profile.getName());
        editTextProfileEmail.setText(profile.getEmail());
        editTextProfileAge.setText(profile.getAge());

        textViewProfileNumber.setText("Phone Number :   " + profile.getPhone());

        if (profile.isSex()) {
            radioButtonMale.setChecked(true);
        } else {
            radioButtonFemale.setChecked(true);
        }

        dialog.dismiss();
    }

    private void saveProfile() {

        String name = editTextProfileName.getText().toString().trim();
        String email = editTextProfileEmail.getText().toString().trim();
        String age = editTextProfileAge.getText().toString().trim();
        boolean sex = radioGroupSex.getCheckedRadioButtonId() == R.id.radio_button_profile_male;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(age)) {
            Toast.makeText(getContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 FRONT-END DEMO: update local object only
        profile = new Profile(name, email, profile.getPhone(), sex, age);

        Toast.makeText(getContext(), "Profile Updated Successfully (Demo)", Toast.LENGTH_SHORT).show();

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && data != null) {
            imageUri = data.getData();
            imageViewProfileImage.setImageURI(imageUri);
            Toast.makeText(getContext(), "Image Loaded (Demo)", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Image loading failed", Toast.LENGTH_SHORT).show();
        }
    }
}
