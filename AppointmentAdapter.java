package com.example.tatwa10.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Appointment;
import com.example.tatwa10.PaymentActivity;
import com.example.tatwa10.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentHolder> {

    private List<Appointment> appointmentList;
    private Context context;

    public AppointmentAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appointment_item, parent, false);
        return new AppointmentHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull AppointmentHolder holder, int position) {

        Appointment appointment = appointmentList.get(position);
        Log.d("CHECK_ITEM",
                "ID=" + appointment.getId() +
                        " | PaymentStatus=" + appointment.getPaymentStatus());

        // 🔥 FIX: reset recycled view
        holder.buttonPay.setVisibility(View.GONE);

        Log.d("FINAL_STATUS_CHECK", "Status = " + appointment.getPaymentStatus());

        holder.textDoctorName.setText(
                appointment.getDoctorName() != null ? appointment.getDoctorName() : "Doctor"
        );

        holder.textDate.setText(
                appointment.getDate() != null ? appointment.getDate() : "-"
        );

        holder.textTime.setText(
                appointment.getTime() != null ? appointment.getTime() : "-"
        );

        String paymentStatus = appointment.getPaymentStatus();
        String status = appointment.getStatus();

        if (paymentStatus != null && paymentStatus.equalsIgnoreCase("paid")) {

            holder.textPaymentStatus.setText("Payment: Paid");
            holder.textPaymentStatus.setTextColor(Color.parseColor("#16A34A"));

            holder.buttonPay.setVisibility(View.GONE);

        } else {

            holder.textPaymentStatus.setText("Payment: Pending");
            holder.textPaymentStatus.setTextColor(Color.parseColor("#E67E22"));

            if (status != null && status.equalsIgnoreCase("accepted")) {
                holder.buttonPay.setVisibility(View.VISIBLE);
            }
        }

        holder.buttonPay.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaymentActivity.class);

            intent.putExtra("appointmentId", appointment.getId());
            intent.putExtra("doctorName", appointment.getDoctorName());
            intent.putExtra("patientName", appointment.getPatientName());
            intent.putExtra("date", appointment.getDate());
            intent.putExtra("time", appointment.getTime());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentHolder extends RecyclerView.ViewHolder {

        private TextView textDoctorName;
        private TextView textDate;
        private TextView textTime;
        private TextView textPaymentStatus;
        private ImageView imageDoctor;
        private MaterialButton buttonPay;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);

            textDoctorName = itemView.findViewById(R.id.textDoctorName);
            textDate = itemView.findViewById(R.id.textDate);
            textTime = itemView.findViewById(R.id.textTime);
            textPaymentStatus = itemView.findViewById(R.id.textPaymentStatus);
            imageDoctor = itemView.findViewById(R.id.imageDoctor);
            buttonPay = itemView.findViewById(R.id.button_pay_now);
        }
    }
}