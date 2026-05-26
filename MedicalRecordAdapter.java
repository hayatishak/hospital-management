package com.example.tatwa10.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatwa10.ModelClass.MedicalRecord;
import com.example.tatwa10.R;

import java.util.List;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.MedicalViewHolder> {

    private List<MedicalRecord> medicalRecordList;
    private OnItemClickListener listener;

    // 🔹 Constructor with local list
    public MedicalRecordAdapter(List<MedicalRecord> medicalRecordList) {
        this.medicalRecordList = medicalRecordList;
    }

    // 🔹 Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medical_record_item, parent, false);
        return new MedicalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalViewHolder holder, int position) {
        MedicalRecord medicalRecord = medicalRecordList.get(position);
        holder.textViewTitle.setText(medicalRecord.getTitle());
    }

    @Override
    public int getItemCount() {
        return medicalRecordList.size();
    }

    // 🔹 ViewHolder
    class MedicalViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewTitle;

        public MedicalViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTitle = itemView.findViewById(R.id.text_view_medical_records_title);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position, medicalRecordList.get(position));
                }
            });
        }
    }

    // 🔹 Clean interface WITHOUT Firebase
    public interface OnItemClickListener {
        void onItemClick(int position, MedicalRecord medicalRecord);
    }
}
