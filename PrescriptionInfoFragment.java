package com.example.tatwa10.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tatwa10.FragmentDoctors.PatientPrescriptionFragment;
import com.example.tatwa10.MainActivity;
import com.example.tatwa10.ModelClass.Prescription;
import com.example.tatwa10.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PrescriptionInfoFragment extends Fragment {

    private Spinner spinnerDoctorsList;
    private EditText editTextMedicineName;
    private CheckBox checkBoxBreakfast, checkBoxLunch, checkBoxDinner;
    private NumberPicker numberPickerDuration;
    private ImageView imageViewDateStart;
    private TextView textViewDateStart, textViewDateEnd;

    private Calendar calendarDateStart, calendarDateEnd;

    private Prescription oldPrescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_add_prescription, container, false);
        MainActivity.currentFragment = "prescriptionInfo";

        spinnerDoctorsList = view.findViewById(R.id.spinner_doctors_list);
        editTextMedicineName = view.findViewById(R.id.edit_text_medicine_name);
        checkBoxBreakfast = view.findViewById(R.id.checkbox_breakfast);
        checkBoxLunch = view.findViewById(R.id.checkbox_lunch);
        checkBoxDinner = view.findViewById(R.id.checkbox_dinner);
        numberPickerDuration = view.findViewById(R.id.number_picker_duration);
        imageViewDateStart = view.findViewById(R.id.image_view_date_start);
        textViewDateStart = view.findViewById(R.id.text_view_add_date_start);
        textViewDateEnd = view.findViewById(R.id.text_view_add_date_end);

        numberPickerDuration.setMinValue(1);
        numberPickerDuration.setMaxValue(50);

        calendarDateStart = Calendar.getInstance();

        String[] names = getContext().getResources().getStringArray(R.array.doctors_name);
        ArrayAdapter<String> adapterNames = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctorsList.setAdapter(adapterNames);

        // FIXED: use empty constructor
        if (getArguments() != null) {

            oldPrescription = new Prescription();

            oldPrescription.setDoctorName(getArguments().getString("doctorName", "Doctor"));
            oldPrescription.setMedicineName(getArguments().getString("medicine", ""));
            oldPrescription.setPatientName(getArguments().getString("patientName", ""));

            oldPrescription.setNotes("Take after food");
            oldPrescription.setBreakfast(true);
            oldPrescription.setLunch(false);
            oldPrescription.setDinner(false);

            oldPrescription.setDateStart("01 Mar 2026");
            oldPrescription.setDateEnd("05 Mar 2026");
            oldPrescription.setDuration(5);

            setUp(adapterNames);
        }

        imageViewDateStart.setOnClickListener(v -> showDatePicker());

        numberPickerDuration.setOnValueChangedListener((picker, oldVal, newVal) -> {
            calendarDateEnd = (Calendar) calendarDateStart.clone();
            calendarDateEnd.add(Calendar.DATE, newVal - 1);

            DateFormat df = new SimpleDateFormat("E ,dd MMM yyyy");
            textViewDateEnd.setText(df.format(calendarDateEnd.getTime()));
        });

        return view;
    }

    private void showDatePicker() {

        DatePickerFragment date = new DatePickerFragment();

        Calendar cal = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", cal.get(Calendar.YEAR));
        args.putInt("month", cal.get(Calendar.MONTH));
        args.putInt("day", cal.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);

        date.setCallBack((DatePicker view, int year, int month, int day) -> {

            calendarDateStart.set(Calendar.YEAR, year);
            calendarDateStart.set(Calendar.MONTH, month);
            calendarDateStart.set(Calendar.DATE, day);

            DateFormat dfStart = new SimpleDateFormat("E ,dd MMM yyyy");
            textViewDateStart.setText(dfStart.format(calendarDateStart.getTime()));

            calendarDateEnd = (Calendar) calendarDateStart.clone();
            calendarDateEnd.add(Calendar.DATE, numberPickerDuration.getValue() - 1);

            DateFormat dfEnd = new SimpleDateFormat("E ,dd MMM yyyy");
            textViewDateEnd.setText(dfEnd.format(calendarDateEnd.getTime()));
        });

        date.show(getParentFragmentManager(), "Date Picker");
    }

    private void setUp(ArrayAdapter<String> adapter) {

        int spinnerPosition = adapter.getPosition(oldPrescription.getDoctorName());
        spinnerDoctorsList.setSelection(spinnerPosition);

        editTextMedicineName.setText(oldPrescription.getMedicineName());
        checkBoxBreakfast.setChecked(oldPrescription.isBreakfast());
        checkBoxLunch.setChecked(oldPrescription.isLunch());
        checkBoxDinner.setChecked(oldPrescription.isDinner());

        textViewDateStart.setText(oldPrescription.getDateStart());
        textViewDateEnd.setText(oldPrescription.getDateEnd());
        numberPickerDuration.setValue(oldPrescription.getDuration());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_prescription_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.icon_save_prescription) {
            savePrescription();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePrescription() {

        String doctorName = spinnerDoctorsList.getSelectedItem().toString();
        String medicineName = editTextMedicineName.getText().toString().trim();
        boolean breakfast = checkBoxBreakfast.isChecked();
        boolean lunch = checkBoxLunch.isChecked();
        boolean dinner = checkBoxDinner.isChecked();
        int duration = numberPickerDuration.getValue();
        String dateStart = textViewDateStart.getText().toString();
        String dateEnd = textViewDateEnd.getText().toString();

        if (TextUtils.isEmpty(medicineName)) {
            Toast.makeText(getContext(), "Please add medicine", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!breakfast && !lunch && !dinner) {
            Toast.makeText(getContext(), "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }


        Prescription prescription = new Prescription();

        prescription.setDoctorName(doctorName);
        prescription.setMedicineName(medicineName);
        prescription.setNotes("");
        prescription.setBreakfast(breakfast);
        prescription.setLunch(lunch);
        prescription.setDinner(dinner);
        prescription.setDateStart(dateStart);
        prescription.setDateEnd(dateEnd);
        prescription.setDuration(duration);
        prescription.setPatientName(MainActivity.patientName);

        Toast.makeText(getContext(), "Prescription Updated", Toast.LENGTH_SHORT).show();

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new PrescriptionFragment())
                .commit();
    }
}