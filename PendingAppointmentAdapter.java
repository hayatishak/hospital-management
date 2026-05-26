package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class PendingAppointmentAdapter
        extends RecyclerView.Adapter<PendingAppointmentAdapter.PendingHolder> {

    private List<Appointment> appointmentList;
    private OnItemClickListener listener;

    public PendingAppointmentAdapter(List<Appointment> appointmentList,
                                     OnItemClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_appointment_item, parent, false);

        return new PendingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);

        String name = appointment.getPatientName();
        if (name == null || name.isEmpty()) name = "Patient";

        holder.name.setText(name);
        holder.contact.setText("Appointment ID: " + appointment.getId());

        holder.date.setText(appointment.getDate());
        holder.time.setText(appointment.getTime());

        holder.callIcon.setOnClickListener(v -> {
            if (listener != null)
                listener.onCallClick(String.valueOf(appointment.getId()));
        });

        holder.messageIcon.setOnClickListener(v -> {
            if (listener != null)
                listener.onMessageClick(String.valueOf(appointment.getId()));
        });

        holder.completedBtn.setOnClickListener(v -> {
            if (listener != null)
                listener.onCompletedClick(appointment, position);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    static class PendingHolder extends RecyclerView.ViewHolder {

        TextView name, contact, date, time;
        ImageView callIcon, messageIcon;
        MaterialButton completedBtn;

        public PendingHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.text_view_item_pending_appointment_patient_name);
            contact = itemView.findViewById(R.id.text_view_item_pending_appointment_patient_contact);
            date = itemView.findViewById(R.id.text_view_item_pending_appointment_date);
            time = itemView.findViewById(R.id.text_view_item_pending_appointment_time);

            callIcon = itemView.findViewById(R.id.image_view_call_patient);
            messageIcon = itemView.findViewById(R.id.image_view_message_patient);
            completedBtn = itemView.findViewById(R.id.button_mark_completed);
        }
    }

    public interface OnItemClickListener {
        void onCallClick(String number);
        void onMessageClick(String number);
        void onCompletedClick(Appointment appointment, int position);
    }
}