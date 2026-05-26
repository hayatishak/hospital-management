package com.example.tatwa10.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.Doctor;
import com.example.tatwa10.R;

import java.util.ArrayList;
import java.util.List;

public class DoctorsAdapter extends RecyclerView.Adapter<DoctorsAdapter.ViewHolder> {

    private final Context context;
    private List<Doctor> doctorList = new ArrayList<>();
    private List<Doctor> doctorListFull = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(Doctor doctor);
    }

    private OnItemClickListener listener;

    public DoctorsAdapter(Context context) {
        this.context = context;
    }

    public void setDoctorList(List<Doctor> list) {
        this.doctorList = list != null ? list : new ArrayList<>();
        this.doctorListFull = new ArrayList<>(this.doctorList);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void filter(String text) {
        List<Doctor> filteredList = new ArrayList<>();

        if (TextUtils.isEmpty(text)) {
            filteredList.addAll(doctorListFull);
        } else {
            String query = text.toLowerCase().trim();

            for (Doctor doctor : doctorListFull) {
                String name = doctor.getFullName() != null ? doctor.getFullName().toLowerCase() : "";
                String specialization = doctor.getSpecialization() != null ? doctor.getSpecialization().toLowerCase() : "";

                if (name.contains(query) || specialization.contains(query)) {
                    filteredList.add(doctor);
                }
            }
        }

        doctorList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctorList.get(position);

        holder.name.setText(doctor.getFullName() != null ? doctor.getFullName() : "");
        holder.specialization.setText(doctor.getSpecialization() != null ? doctor.getSpecialization() : "General");
        holder.phone.setText(doctor.getPhone() != null ? doctor.getPhone() : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(doctor);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorList != null ? doctorList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, specialization, phone;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view_doctor);
            name = itemView.findViewById(R.id.text_view_doctor_name);
            specialization = itemView.findViewById(R.id.text_view_doctor_specialization);
            phone = itemView.findViewById(R.id.text_view_doctor_phone);
        }
    }
}