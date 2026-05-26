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

public class PrescriptionAdapterFrontend
        extends RecyclerView.Adapter<PrescriptionAdapterFrontend.ViewHolder> {

    private final List<Prescription> list;

    public PrescriptionAdapterFrontend(List<Prescription> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prescription_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Prescription p = list.get(position);

        holder.textDoctor.setText(
                p.getDoctorName() != null && !p.getDoctorName().isEmpty()
                        ? "Prescribed by Dr. " + p.getDoctorName()
                        : "Doctor: Unknown"
        );

        holder.textMedicine.setText(
                p.getMedicineName() != null && !p.getMedicineName().isEmpty()
                        ? p.getMedicineName()
                        : "No medicine"
        );

        holder.textNotes.setText(
                p.getNotes() != null && !p.getNotes().isEmpty()
                        ? "Notes: " + p.getNotes()
                        : "Notes: No notes"
        );

        holder.textDuration.setText(
                p.getDuration() > 0
                        ? "Duration: " + p.getDuration() + " days"
                        : "Duration: -"
        );

        holder.textStart.setText("Start: " +
                (p.getDateStart() != null ? p.getDateStart() : "-"));

        holder.textEnd.setText("End: " +
                (p.getDateEnd() != null ? p.getDateEnd() : "-"));

        holder.btnBreakfast.setVisibility(p.isBreakfast() ? View.VISIBLE : View.GONE);
        holder.btnLunch.setVisibility(p.isLunch() ? View.VISIBLE : View.GONE);
        holder.btnDinner.setVisibility(p.isDinner() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDoctor, textMedicine, textNotes, textStart, textEnd, textDuration;
        Button btnBreakfast, btnLunch, btnDinner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textDoctor = itemView.findViewById(R.id.text_view_doctor_prescribed_by);
            textMedicine = itemView.findViewById(R.id.text_view_medicine_name);
            textNotes = itemView.findViewById(R.id.text_view_notes);
            textStart = itemView.findViewById(R.id.text_view_date_start);
            textEnd = itemView.findViewById(R.id.text_view_date_end);
            textDuration = itemView.findViewById(R.id.text_view_duration);

            btnBreakfast = itemView.findViewById(R.id.button_breakfast);
            btnLunch = itemView.findViewById(R.id.button_lunch);
            btnDinner = itemView.findViewById(R.id.button_dinner);
        }
    }
}