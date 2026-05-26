package com.example.tatwa10.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.Adapters.ReviewAdapter;
import com.example.tatwa10.ApiService;
import com.example.tatwa10.ModelClass.Review;
import com.example.tatwa10.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DoctorInfoFragment extends Fragment {

    private TextView textName, textField, textPhone;
    private MaterialButton buttonBook, buttonCall, buttonWhatsapp;
    private ImageView imageDoctor;
    private RatingBar ratingBar;
    private RecyclerView recyclerReviews;
    private ReviewAdapter reviewAdapter;
    private String doctorName = "Unknown";
    private String doctorField = "General";
    private String doctorPhone = "N/A";
    private float doctorRating = 4.0f;
    private String imageUrl = "";
    private int doctorId = -1;
    private int patientId = -1;
    MaterialButton buttonAddReview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctor_info, container, false);

        //  FIND VIEWS
        textName = view.findViewById(R.id.text_view_name_doctor_info);
        textField = view.findViewById(R.id.text_view_field_doctor_info);
        textPhone = view.findViewById(R.id.text_view_phone_doctor_info);
        buttonBook = view.findViewById(R.id.button_doctor_book_appointment);
        buttonCall = view.findViewById(R.id.button_call_doctor);
        buttonWhatsapp = view.findViewById(R.id.button_whatsapp);
        imageDoctor = view.findViewById(R.id.image_doctor_info);
        ratingBar = view.findViewById(R.id.rating_doctor);
        recyclerReviews = view.findViewById(R.id.recycler_reviews);
        buttonAddReview = view.findViewById(R.id.button_add_review);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(getContext()));

        //  GET DATA SAFELY
        Bundle bundle = getArguments();

        if (bundle != null) {
            doctorName = bundle.getString("doctor_name", "Unknown");
            doctorField = bundle.getString("doctor_field", "General");
            doctorPhone = bundle.getString("doctor_phone", "N/A");
            doctorRating = bundle.getFloat("doctor_rating", 4.0f);
            imageUrl = bundle.getString("doctor_image", "");
            doctorId = bundle.getInt("doctor_id", -1);

            Log.d("RECEIVE_ID", "Received ID = " + doctorId);
        }
        SharedPreferences prefs = getActivity().getSharedPreferences("user", getContext().MODE_PRIVATE);
        patientId = prefs.getInt("patientId", -1);

        Log.d("PATIENT_ID", "Patient ID = " + patientId);

        Log.d("PATIENT_ID", "Patient ID = " + patientId);
        //  SET DATA
        textName.setText(doctorName);
        textField.setText(doctorField);

        if (doctorPhone == null || doctorPhone.equals("N/A") || doctorPhone.isEmpty()) {
            textPhone.setText("No phone available");
        } else {
            textPhone.setText(doctorPhone);
        }

        ratingBar.setRating(doctorRating);

        // 🖼 LOAD IMAGE
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_doctor)
                    .error(R.drawable.ic_doctor)
                    .into(imageDoctor);
        }

        //  LOAD REVIEWS (ONLY ONCE)
        loadReviews(doctorId);
        reviewAdapter = new ReviewAdapter(new ArrayList<>());
        recyclerReviews.setAdapter(reviewAdapter);
        //  BOOK
        buttonBook.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Opening booking...", Toast.LENGTH_SHORT).show();

            BookAppointmentFragment fragment = new BookAppointmentFragment();

            Bundle data = new Bundle();
            data.putString("doctor_name", doctorName);
            data.putString("doctor_field", doctorField);

            fragment.setArguments(data);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        buttonAddReview.setOnClickListener(v -> {

            View dialogView = LayoutInflater.from(getContext())
                    .inflate(R.layout.dialog_add_review, null);

            RatingBar ratingInput = dialogView.findViewById(R.id.input_rating);
            TextView commentInput = dialogView.findViewById(R.id.input_comment);

            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Add Review")
                    .setView(dialogView)
                    .setPositiveButton("Submit", (dialog, which) -> {

                        float rating = ratingInput.getRating();
                        String comment = commentInput.getText().toString();

                        submitReview(rating, comment);

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

            Log.d("BUTTON_TEST", "Button found");
        });
        //  CALL
        buttonCall.setOnClickListener(v -> {
            if (doctorPhone.equals("N/A")) {
                Toast.makeText(getContext(), "Phone not available", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + doctorPhone));
            startActivity(intent);
        });

        //  WHATSAPP
        buttonWhatsapp.setOnClickListener(v -> {
            try {
                String url = "https://wa.me/" + doctorPhone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (Exception e) {
                Toast.makeText(getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    //  LOAD REVIEWS FUNCTION
    private void loadReviews(int doctorId) {
        if (doctorId == -1) return;

        new Thread(() -> {
            try {
                String response = ApiService.getReviews(doctorId);

                Log.d("REVIEWS", response);

                if (response == null || response.isEmpty()) return;

                Type listType = new TypeToken<List<Review>>() {}.getType();
                List<Review> reviews = new Gson().fromJson(response, listType);

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {

                    if (reviews != null && reviews.size() > 0) {

                        reviewAdapter.setReviewList(reviews);

                        float avg = 0;
                        for (Review r : reviews) {
                            avg += r.getRating();
                        }
                        avg /= reviews.size();

                        ratingBar.setRating(avg);

                    } else {
                        ratingBar.setRating(0); //  no reviews yet
                        Toast.makeText(getContext(), "No reviews", Toast.LENGTH_SHORT).show();
                    }

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void submitReview(float rating, String comment) {

        new Thread(() -> {

            try {
                String json = "{"
                        + "\"doctorId\":" + doctorId + ","
                        + "\"patientId\":" + patientId + ","
                        + "\"rating\":" + rating + ","
                        + "\"comment\":\"" + comment + "\""
                        + "}";

                Log.d("SEND_JSON", json); //  DEBUG

                String response = ApiService.post(ApiService.BASE_URL + "Reviews", json);

                Log.d("API_RESPONSE", response); //  DEBUG

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {

                        Toast.makeText(getContext(), "Review submitted", Toast.LENGTH_SHORT).show();

                        loadReviews(doctorId); //  REFRESH

                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("SUBMIT_ERROR", e.getMessage());
            }

        }).start();
    }
}