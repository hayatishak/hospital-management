package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.R;

import java.util.List;

public class CompletedAppointmentAdapter
        extends RecyclerView.Adapter<CompletedAppointmentAdapter.CompletedHolder> {

    private List<Appointment> appointmentList;

    public CompletedAppointmentAdapter(List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public CompletedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.completed_appointment_item, parent, false);

        return new CompletedHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);

        String name = appointment.getPatientName();
        if (name == null || name.isEmpty()) name = "Patient";

        holder.name.setText(name);
        holder.contact.setText("Appointment ID: " + appointment.getId());
        holder.date.setText(appointment.getDate());
        holder.time.setText(appointment.getTime());
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    static class CompletedHolder extends RecyclerView.ViewHolder {

        TextView name, contact, date, time;

        public CompletedHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_view_item_completed_appointment_patient_name);
            contact = itemView.findViewById(R.id.text_view_item_completed_patient_contact);
            date = itemView.findViewById(R.id.text_view_item_completed_appointment_date);
            time = itemView.findViewById(R.id.text_view_item_completed_appointment_time);
        }
    }
}