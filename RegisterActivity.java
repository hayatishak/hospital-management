package com.example.tatwa10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editName, editEmail, editPassword;
    private Spinner spinnerGender;
    private Button buttonRegister;
    private EditText editPhone, editDob, editNationalId,
            editAllergies, editDiseases, editMedications, editAddress;

    private Spinner spinnerBlood, spinnerCountry, spinnerCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        spinnerGender = findViewById(R.id.spinner_gender);
        buttonRegister = findViewById(R.id.button_register);
        editPhone = findViewById(R.id.edit_phone);
        editDob = findViewById(R.id.edit_dob);
        editNationalId = findViewById(R.id.edit_national_id);
        editAllergies = findViewById(R.id.edit_allergies);
        editDiseases = findViewById(R.id.edit_diseases);
        editMedications = findViewById(R.id.edit_medications);
        editAddress = findViewById(R.id.edit_address);

        spinnerBlood = findViewById(R.id.spinner_blood);
        spinnerCountry = findViewById(R.id.spinner_country);
        spinnerCity = findViewById(R.id.spinner_city);
        String[] genders = {"Select Gender", "Male", "Female"};
        spinnerGender = findViewById(R.id.spinner_gender);
        spinnerGender.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                genders
        ));
// BLOOD TYPES
        String[] bloodTypes = {"Select Blood Type", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                bloodTypes
        );

        spinnerBlood.setAdapter(adapter);

// COUNTRIES
        String[] countries = {
                "Select Country",
                "Afghanistan",
                "Albania",
                "Algeria",
                "Argentina",
                "Armenia",
                "Australia",
                "Austria",
                "Azerbaijan",
                "Bahrain",
                "Bangladesh",
                "Belgium",
                "Brazil",
                "Bulgaria",
                "Canada",
                "Chile",
                "China",
                "Colombia",
                "Croatia",
                "Cyprus",
                "Czech Republic",
                "Denmark",
                "Egypt",
                "Estonia",
                "Finland",
                "France",
                "Georgia",
                "Germany",
                "Greece",
                "Hungary",
                "Iceland",
                "India",
                "Indonesia",
                "Iran",
                "Iraq",
                "Ireland",
                "Israel",
                "Italy",
                "Japan",
                "Jordan",
                "Kazakhstan",
                "Kenya",
                "Kuwait",
                "Latvia",
                "Lebanon",
                "Libya",
                "Lithuania",
                "Luxembourg",
                "Malaysia",
                "Malta",
                "Mexico",
                "Morocco",
                "Netherlands",
                "New Zealand",
                "Nigeria",
                "Norway",
                "Oman",
                "Pakistan",
                "Palestine",
                "Peru",
                "Philippines",
                "Poland",
                "Portugal",
                "Qatar",
                "Romania",
                "Russia",
                "Saudi Arabia",
                "Serbia",
                "Singapore",
                "Slovakia",
                "Slovenia",
                "South Africa",
                "South Korea",
                "Spain",
                "Sweden",
                "Switzerland",
                "Syria",
                "Thailand",
                "Tunisia",
                "Turkey",
                "Ukraine",
                "United Arab Emirates",
                "United Kingdom",
                "United States",
                "Uruguay",
                "Venezuela",
                "Vietnam",
                "Yemen"
        };
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                countries
        );

        spinnerCountry.setAdapter(countryAdapter);
        buttonRegister.setOnClickListener(v -> register());

// CITIES
        Map<String, String[]> countryCityMap = new HashMap<>();

// Lebanon
        //  ADD FOR ALL REMAINING COUNTRIES
        countryCityMap.put("Lebanon", new String[]{
                "Select City",
                "Beirut","Tripoli","Sidon","Tyre","Byblos","Zahle",
                "Baalbek","Jounieh","Aley","Batroun","Nabatieh","Bcharre","Halba"
        });
        countryCityMap.put("Afghanistan", new String[]{"Select City","Kabul","Kandahar","Herat"});
        countryCityMap.put("Albania", new String[]{"Select City","Tirana","Durres","Vlore"});
        countryCityMap.put("Algeria", new String[]{"Select City","Algiers","Oran","Constantine"});
        countryCityMap.put("Argentina", new String[]{"Select City","Buenos Aires","Cordoba","Rosario"});
        countryCityMap.put("Armenia", new String[]{"Select City","Yerevan","Gyumri","Vanadzor"});
        countryCityMap.put("Austria", new String[]{"Select City","Vienna","Salzburg","Graz"});
        countryCityMap.put("Azerbaijan", new String[]{"Select City","Baku","Ganja","Sumqayit"});
        countryCityMap.put("Bahrain", new String[]{"Select City","Manama","Riffa","Muharraq"});
        countryCityMap.put("Bangladesh", new String[]{"Select City","Dhaka","Chittagong","Khulna"});
        countryCityMap.put("Belgium", new String[]{"Select City","Brussels","Antwerp","Ghent"});
        countryCityMap.put("Bulgaria", new String[]{"Select City","Sofia","Plovdiv","Varna"});
        countryCityMap.put("Chile", new String[]{"Select City","Santiago","Valparaiso","Concepcion"});
        countryCityMap.put("Colombia", new String[]{"Select City","Bogota","Medellin","Cali"});
        countryCityMap.put("Croatia", new String[]{"Select City","Zagreb","Split","Rijeka"});
        countryCityMap.put("Cyprus", new String[]{"Select City","Nicosia","Limassol","Larnaca"});
        countryCityMap.put("Czech Republic", new String[]{"Select City","Prague","Brno","Ostrava"});
        countryCityMap.put("Denmark", new String[]{"Select City","Copenhagen","Aarhus","Odense"});
        countryCityMap.put("Estonia", new String[]{"Select City","Tallinn","Tartu","Narva"});
        countryCityMap.put("Finland", new String[]{"Select City","Helsinki","Espoo","Tampere"});
        countryCityMap.put("Georgia", new String[]{"Select City","Tbilisi","Batumi","Kutaisi"});
        countryCityMap.put("Greece", new String[]{"Select City","Athens","Thessaloniki","Patras"});
        countryCityMap.put("Hungary", new String[]{"Select City","Budapest","Debrecen","Szeged"});
        countryCityMap.put("Iceland", new String[]{"Select City","Reykjavik","Kopavogur","Akureyri"});
        countryCityMap.put("Indonesia", new String[]{"Select City","Jakarta","Surabaya","Bandung"});
        countryCityMap.put("Iran", new String[]{"Select City","Tehran","Isfahan","Shiraz"});
        countryCityMap.put("Iraq", new String[]{"Select City","Baghdad","Basra","Erbil"});
        countryCityMap.put("Ireland", new String[]{"Select City","Dublin","Cork","Galway"});
        countryCityMap.put("Israel", new String[]{"Select City","Tel Aviv","Jerusalem","Haifa"});
        countryCityMap.put("Jordan", new String[]{"Select City","Amman","Zarqa","Irbid"});
        countryCityMap.put("Kazakhstan", new String[]{"Select City","Almaty","Astana","Shymkent"});
        countryCityMap.put("Kenya", new String[]{"Select City","Nairobi","Mombasa","Kisumu"});
        countryCityMap.put("Kuwait", new String[]{"Select City","Kuwait City","Hawalli","Farwaniya"});
        countryCityMap.put("Latvia", new String[]{"Select City","Riga","Daugavpils","Liepaja"});
        countryCityMap.put("Libya", new String[]{"Select City","Tripoli","Benghazi","Misrata"});
        countryCityMap.put("Lithuania", new String[]{"Select City","Vilnius","Kaunas","Klaipeda"});
        countryCityMap.put("Luxembourg", new String[]{"Select City","Luxembourg","Esch","Differdange"});
        countryCityMap.put("Malaysia", new String[]{"Select City","Kuala Lumpur","Penang","Johor"});
        countryCityMap.put("Malta", new String[]{"Select City","Valletta","Birkirkara","Mosta"});
        countryCityMap.put("Mexico", new String[]{"Select City","Mexico City","Guadalajara","Monterrey"});
        countryCityMap.put("Morocco", new String[]{"Select City","Casablanca","Rabat","Marrakesh"});
        countryCityMap.put("Netherlands", new String[]{"Select City","Amsterdam","Rotterdam","The Hague"});
        countryCityMap.put("New Zealand", new String[]{"Select City","Auckland","Wellington","Christchurch"});
        countryCityMap.put("Nigeria", new String[]{"Select City","Lagos","Abuja","Kano"});
        countryCityMap.put("Norway", new String[]{"Select City","Oslo","Bergen","Trondheim"});
        countryCityMap.put("Oman", new String[]{"Select City","Muscat","Salalah","Sohar"});
        countryCityMap.put("Pakistan", new String[]{"Select City","Karachi","Lahore","Islamabad"});
        countryCityMap.put("Palestine", new String[]{"Select City","Gaza","Ramallah","Hebron"});
        countryCityMap.put("Peru", new String[]{"Select City","Lima","Cusco","Arequipa"});
        countryCityMap.put("Philippines", new String[]{"Select City","Manila","Cebu","Davao"});
        countryCityMap.put("Poland", new String[]{"Select City","Warsaw","Krakow","Gdansk"});
        countryCityMap.put("Portugal", new String[]{"Select City","Lisbon","Porto","Braga"});
        countryCityMap.put("Qatar", new String[]{"Select City","Doha","Al Rayyan","Al Wakrah"});
        countryCityMap.put("Romania", new String[]{"Select City","Bucharest","Cluj","Timisoara"});
        countryCityMap.put("Russia", new String[]{"Select City","Moscow","Saint Petersburg","Kazan"});
        countryCityMap.put("Serbia", new String[]{"Select City","Belgrade","Novi Sad","Nis"});
        countryCityMap.put("Singapore", new String[]{"Select City","Singapore","Jurong","Woodlands"});
        countryCityMap.put("Slovakia", new String[]{"Select City","Bratislava","Kosice","Presov"});
        countryCityMap.put("Slovenia", new String[]{"Select City","Ljubljana","Maribor","Celje"});
        countryCityMap.put("South Korea", new String[]{"Select City","Seoul","Busan","Incheon"});
        countryCityMap.put("Sweden", new String[]{"Select City","Stockholm","Gothenburg","Malmo"});
        countryCityMap.put("Switzerland", new String[]{"Select City","Zurich","Geneva","Basel"});
        countryCityMap.put("Syria", new String[]{"Select City","Damascus","Aleppo","Homs"});
        countryCityMap.put("Thailand", new String[]{"Select City","Bangkok","Chiang Mai","Phuket"});
        countryCityMap.put("Tunisia", new String[]{"Select City","Tunis","Sfax","Sousse"});
        countryCityMap.put("Ukraine", new String[]{"Select City","Kyiv","Lviv","Odessa"});
        countryCityMap.put("Uruguay", new String[]{"Select City","Montevideo","Salto","Punta del Este"});
        countryCityMap.put("Venezuela", new String[]{"Select City","Caracas","Maracaibo","Valencia"});
        countryCityMap.put("Vietnam", new String[]{"Select City","Hanoi","Ho Chi Minh","Da Nang"});
        countryCityMap.put("Yemen", new String[]{"Select City","Sanaa","Aden","Taiz"});
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCountry = spinnerCountry.getSelectedItem().toString();

                // 🔥 GET cities for selected country
                String[] cities = countryCityMap.get(selectedCountry);

                if (cities == null) {
                    cities = new String[]{"Select City"};
                }

                //  NOW use cities (NOT map)
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item,
                        cities
                );

                spinnerCity.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    private void register() {

        new Thread(() -> {

            try {

                String name = editName.getText().toString().trim();
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();
                String gender = spinnerGender.getSelectedItem().toString();
                String phone = editPhone.getText().toString().trim();
                String dob = editDob.getText().toString().trim();
                String nationalId = editNationalId.getText().toString().trim();
                String allergies = editAllergies.getText().toString().trim();
                String diseases = editDiseases.getText().toString().trim();
                String medications = editMedications.getText().toString().trim();
                String address = editAddress.getText().toString().trim();

                String blood = spinnerBlood.getSelectedItem().toString();
                String country = spinnerCountry.getSelectedItem().toString();
                String city = spinnerCity.getSelectedItem().toString();

                if (TextUtils.isEmpty(name)) {
                    runOnUiThread(() -> editName.setError("Enter name"));
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    runOnUiThread(() -> editEmail.setError("Enter email"));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    runOnUiThread(() -> editPassword.setError("Enter password"));
                    return;
                }

                if (gender.equals("Select Gender")) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show());
                    return;
                }

                String response = ApiService.registerPatient(name, email, password);

                if (response == null || response.isEmpty()) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show());
                    return;
                }

                if (!response.trim().startsWith("{")) {
                    runOnUiThread(() ->
                            Toast.makeText(this, response, Toast.LENGTH_LONG).show());
                    return;
                }

                JSONObject json = new JSONObject(response);

                if (!json.has("success") || !json.getBoolean("success")) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show());
                    return;
                }

                int patientId = json.getInt("patientId");

//  JSON


                JSONObject details = new JSONObject();
                details.put("Phone", phone);
                details.put("DateOfBirth", dob);
                details.put("NationalId", nationalId);
                details.put("BloodType", blood);
                details.put("Allergies", allergies);
                details.put("Diseases", diseases);
                details.put("Medications", medications);
                details.put("Address", address);
                details.put("Country", country);
                details.put("City", city);


                Log.d("JSON_SENT", details.toString());
//  ONLY ONE CALL
                ApiService.updatePatientDetails(patientId, details);
// KEEP THIS
                SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
                prefs.edit().putInt("patientId", patientId).apply();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, PhoneVerificationActivity.class);

//  pass email + password to auto-fill
                    intent.putExtra("email", email);
                    intent.putExtra("password", password);

                    startActivity(intent);
                    finish();                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

        }).start();
    }
}