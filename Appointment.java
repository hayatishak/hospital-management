package com.example.tatwa10.ModelClass;

import com.google.gson.annotations.SerializedName;

public class Appointment {

    private int id;

    @SerializedName("doctorId")
    private int doctorId;

    @SerializedName("patientId")
    private int patientId;

    @SerializedName("paymentStatus")
    private String paymentStatus;

    private String date;
    private String time;
    private String status;

    @SerializedName("patientName")
    private String patientName;

    @SerializedName("doctorName")
    private String doctorName;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    public int getId() {
        return id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public String getTime() {
        return time == null ? "" : time;
    }

    public String getStatus() {
        return status == null ? "" : status;
    }

    public String getPatientName() {
        return patientName == null ? "Unknown Patient" : patientName;
    }

    public String getDoctorName() {
        return doctorName == null ? "Unknown Doctor" : doctorName;
    }

    public String getPaymentMethod() {
        return paymentMethod == null ? "Unknown" : paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
}