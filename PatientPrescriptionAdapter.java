package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Prescription;
import com.example.tatwa10.R;

import java.util.List;

public class PatientPrescriptionAdapter extends RecyclerView.Adapter<PatientPrescriptionAdapter.PatientHolder> {

    private List<Prescription> prescriptionList;
    private OnItemClickListener listener;

    // 🔹 Constructor with local list
    public PatientPrescriptionAdapter(List<Prescription> prescriptionList) {
        this.prescriptionList = prescriptionList;
    }

    // 🔹 Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.doctors_prescription_item, parent, false);
        return new PatientHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientHolder holder, int position) {

        Prescription prescription = prescriptionList.get(position);

        holder.textViewPatientName.setText(prescription.getPatientName());
        holder.textViewMedicineName.setText(prescription.getMedicineName());
        holder.textViewDateStart.setText(prescription.getDateStart());
        holder.textViewDateEnd.setText(prescription.getDateEnd());
        holder.textViewDuration.setText(String.valueOf(prescription.getDuration()));

        // Reset visibility first (important for RecyclerView reuse)
        holder.buttonBreakfast.setVisibility(View.GONE);
        holder.buttonLunch.setVisibility(View.GONE);
        holder.buttonDinner.setVisibility(View.GONE);

        if (prescription.isBreakfast()) holder.buttonBreakfast.setVisibility(View.VISIBLE);
        if (prescription.isLunch()) holder.buttonLunch.setVisibility(View.VISIBLE);
        if (prescription.isDinner()) holder.buttonDinner.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return prescriptionList.size();
    }

    // 🔹 ViewHolder
    class PatientHolder extends RecyclerView.ViewHolder {

        private TextView textViewPatientName;
        private TextView textViewMedicineName;
        private Button buttonBreakfast;
        private Button buttonLunch;
        private Button buttonDinner;
        private TextView textViewDateStart;
        private TextView textViewDateEnd;
        private TextView textViewDuration;

        public PatientHolder(@NonNull View itemView) {
            super(itemView);

            textViewPatientName = itemView.findViewById(R.id.text_view_doctor_prescription_patient_name);
            textViewMedicineName = itemView.findViewById(R.id.text_view_doctor_prescription_medicine_name);
            buttonBreakfast = itemView.findViewById(R.id.button_breakfast_doctor_prescription);
            buttonLunch = itemView.findViewById(R.id.button_lunch_doctor_prescription);
            buttonDinner = itemView.findViewById(R.id.button_dinner_doctor_prescription);
            textViewDateStart = itemView.findViewById(R.id.text_view_date_start_doctor_prescription);
            textViewDateEnd = itemView.findViewById(R.id.text_view_date_end_doctor_prescription);
            textViewDuration = itemView.findViewById(R.id.text_view_item_medicine_duration_doctor_prescription);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, prescriptionList.get(position));
                }
            });
        }
    }

    // 🔹 Clean interface WITHOUT Firebase
    public interface OnItemClickListener {
        void onItemClick(int position, Prescription prescription);
    }
}