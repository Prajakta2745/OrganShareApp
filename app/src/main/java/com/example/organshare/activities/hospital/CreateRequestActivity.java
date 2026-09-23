package com.example.organshare.activities.hospital;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.HospitalRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.example.organshare.utils.IdGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class CreateRequestActivity extends AppCompatActivity {

    private AutoCompleteTextView actvOrganRequired, actvBloodGroupRequired, actvEmergencyLevel;
    private TextInputEditText etPatientRefId, etRequiredDate, etRequiredTime, etAdditionalNotes;
    private MaterialButton btnSubmitOrganRequest;
    private ProgressBar progressBarCreateReq;
    private HospitalRepository repository;
    private HospitalProfile hospitalProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        repository = new HospitalRepository();

        initViews();
        setupDropdowns();
        setupDateTimePickers();
        loadHospitalData();

        btnSubmitOrganRequest.setOnClickListener(v -> submitRequest());
    }

    private void initViews() {
        actvOrganRequired = findViewById(R.id.actvOrganRequired);
        actvBloodGroupRequired = findViewById(R.id.actvBloodGroupRequired);
        actvEmergencyLevel = findViewById(R.id.actvEmergencyLevel);
        etPatientRefId = findViewById(R.id.etPatientRefId);
        etRequiredDate = findViewById(R.id.etRequiredDate);
        etRequiredTime = findViewById(R.id.etRequiredTime);
        etAdditionalNotes = findViewById(R.id.etAdditionalNotes);
        btnSubmitOrganRequest = findViewById(R.id.btnSubmitOrganRequest);
        progressBarCreateReq = findViewById(R.id.progressBarCreateReq);

        etRequiredDate.setText(DateTimeUtils.getCurrentDate());
        etRequiredTime.setText("18:00");
    }

    private void setupDropdowns() {
        actvOrganRequired.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.ORGAN_TYPES));
        actvBloodGroupRequired.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.BLOOD_GROUPS));
        actvEmergencyLevel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"EMERGENCY", "URGENT", "NORMAL"}));
    }

    private void setupDateTimePickers() {
        etRequiredDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etRequiredDate.setText(formatted);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etRequiredTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String formatted = String.format("%02d:%02d", hourOfDay, minute);
                etRequiredTime.setText(formatted);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void loadHospitalData() {
        String userId = AuthManager.getInstance(this).getSessionManager().getUserId();
        repository.getHospitalByUserId(userId, new HospitalRepository.DataCallback<HospitalProfile>() {
            @Override
            public void onSuccess(HospitalProfile profile) {
                hospitalProfile = profile;
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    private void submitRequest() {
        String organ = actvOrganRequired.getText().toString().trim();
        String blood = actvBloodGroupRequired.getText().toString().trim();
        String emergency = actvEmergencyLevel.getText().toString().trim();
        String patientId = etPatientRefId.getText() != null ? etPatientRefId.getText().toString().trim() : "PAT-TEMP";
        String date = etRequiredDate.getText() != null ? etRequiredDate.getText().toString().trim() : DateTimeUtils.getCurrentDate();
        String time = etRequiredTime.getText() != null ? etRequiredTime.getText().toString().trim() : "18:00";
        String notes = etAdditionalNotes.getText() != null ? etAdditionalNotes.getText().toString().trim() : "";

        if (patientId.isEmpty()) {
            etPatientRefId.setError("Patient / Case Ref ID is required");
            etPatientRefId.requestFocus();
            return;
        }

        progressBarCreateReq.setVisibility(View.VISIBLE);
        btnSubmitOrganRequest.setEnabled(false);

        String requestId = IdGenerator.generateRequestId();
        OrganRequest req = new OrganRequest();
        req.setRequestId(requestId);
        req.setHospitalId(hospitalProfile != null ? hospitalProfile.getHospitalId() : "HOSP-001");
        req.setHospitalName(hospitalProfile != null ? hospitalProfile.getHospitalName() : AuthManager.getInstance(this).getSessionManager().getUserName());
        req.setHospitalAddress(hospitalProfile != null ? hospitalProfile.getAddress() : "100 Hospital Avenue, Metro City");
        req.setHospitalContact(hospitalProfile != null ? hospitalProfile.getContactNumber() : "+1-555-300-8800");
        req.setOrganRequired(organ);
        req.setBloodGroup(blood);
        req.setQuantity(1);
        req.setEmergencyLevel(emergency);
        req.setPatientRefId(patientId);
        req.setRequiredDate(date);
        req.setRequiredTime(time);
        req.setAdditionalNotes(notes);
        req.setStatus(Constants.STATUS_PENDING);
        req.setCompatibilitySummary("Awaiting Matching Verification");

        repository.createOrganRequest(req, new HospitalRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                progressBarCreateReq.setVisibility(View.GONE);
                btnSubmitOrganRequest.setEnabled(true);
                Toast.makeText(CreateRequestActivity.this, "Organ Request " + requestId + " submitted successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBarCreateReq.setVisibility(View.GONE);
                btnSubmitOrganRequest.setEnabled(true);
                Toast.makeText(CreateRequestActivity.this, "Submission failed: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
