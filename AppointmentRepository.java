package com.example.tatwa10.ModelClass;

import com.example.tatwa10.ModelClass.Appointment;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    public static final List<Appointment> pendingAppointments = new ArrayList<>();
    public static final List<Appointment> completedAppointments = new ArrayList<>();

}
