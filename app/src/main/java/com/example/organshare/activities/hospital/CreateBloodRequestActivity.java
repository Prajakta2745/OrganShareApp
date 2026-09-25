package com.example.organshare.activities.hospital;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.BloodRequest;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class CreateBloodRequestActivity extends AppCompatActivity {

    private AutoCompleteTextView actvBloodReqGroup, actvBloodEmergencyLevel;
    private TextInputEditText etBloodUnits, etBloodPatientRef, etBloodRequiredDate, etBloodRequiredTime;
    private TextInputEditText etBloodHospitalLocation, etBloodCoordinatorPhone, etBloodNotes;
    private MaterialButton btnSubmitBloodRequest;

    private static final String[] EMERGENCY_LEVELS = {
            Constants.EMERGENCY_NORMAL, Constants.EMERGENCY_URGENT, Constants.EMERGENCY_CRITICAL
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blood_request);

        initViews();
        setupDropdowns();
        setupDateTimePickers();

        btnSubmitBloodRequest.setOnClickListener(v -> submitBloodRequest());
    }

    private void initViews() {
        actvBloodReqGroup = findViewById(R.id.actvBloodReqGroup);
        actvBloodEmergencyLevel = findViewById(R.id.actvBloodEmergencyLevel);
        etBloodUnits = findViewById(R.id.etBloodUnits);
        etBloodPatientRef = findViewById(R.id.etBloodPatientRef);
        etBloodRequiredDate = findViewById(R.id.etBloodRequiredDate);
        etBloodRequiredTime = findViewById(R.id.etBloodRequiredTime);
        etBloodHospitalLocation = findViewById(R.id.etBloodHospitalLocation);
        etBloodCoordinatorPhone = findViewById(R.id.etBloodCoordinatorPhone);
        etBloodNotes = findViewById(R.id.etBloodNotes);
        btnSubmitBloodRequest = findViewById(R.id.btnSubmitBloodRequest);

        etBloodRequiredDate.setText(DateTimeUtils.getCurrentDate());
        etBloodRequiredTime.setText("18:00");
    }

    private void setupDropdowns() {
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.BLOOD_GROUPS);
        actvBloodReqGroup.setAdapter(bloodAdapter);

        ArrayAdapter<String> emgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, EMERGENCY_LEVELS);
        actvBloodEmergencyLevel.setAdapter(emgAdapter);
    }

    private void setupDateTimePickers() {
        etBloodRequiredDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etBloodRequiredDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etBloodRequiredTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                etBloodRequiredTime.setText(time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void submitBloodRequest() {
        String bloodGroup = actvBloodReqGroup.getText() != null ? actvBloodReqGroup.getText().toString().trim() : "";
        String unitsStr = etBloodUnits.getText() != null ? etBloodUnits.getText().toString().trim() : "1";
        String patientRef = etBloodPatientRef.getText() != null ? etBloodPatientRef.getText().toString().trim() : "";
        String location = etBloodHospitalLocation.getText() != null ? etBloodHospitalLocation.getText().toString().trim() : "";
        String contact = etBloodCoordinatorPhone.getText() != null ? etBloodCoordinatorPhone.getText().toString().trim() : "";

        if (bloodGroup.isEmpty() || patientRef.isEmpty() || location.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill in all mandatory fields (*).", Toast.LENGTH_SHORT).show();
            return;
        }

        int units = 1;
        try {
            units = Integer.parseInt(unitsStr);
            if (units <= 0) units = 1;
        } catch (Exception ignored) {}

        String reqId = "REQ-BLD-2026-" + (System.currentTimeMillis() % 100000);
        String hospitalName = AuthManager.getInstance(this).getSessionManager().getUserName();
        String hospitalId = AuthManager.getInstance(this).getSessionManager().getUserId();

        BloodRequest req = new BloodRequest();
        req.setRequestId(reqId);
        req.setHospitalId(hospitalId);
        req.setHospitalName(hospitalName != null ? hospitalName : "Transplant Center");
        req.setHospitalAddress(location);
        req.setHospitalContact(contact);
        req.setBloodGroup(bloodGroup);
        req.setUnitsRequested(units);
        req.setUnitsFulfilled(0);
        req.setUnitsRemaining(units);
        req.setPatientRefId(patientRef);
        req.setRequiredDate(etBloodRequiredDate.getText() != null ? etBloodRequiredDate.getText().toString() : DateTimeUtils.getCurrentDate());
        req.setRequiredTime(etBloodRequiredTime.getText() != null ? etBloodRequiredTime.getText().toString() : "18:00");
        req.setEmergencyLevel(actvBloodEmergencyLevel.getText() != null ? actvBloodEmergencyLevel.getText().toString() : Constants.EMERGENCY_URGENT);
        req.setStatus(Constants.STATUS_PENDING);
        req.setAdditionalNotes(etBloodNotes.getText() != null ? etBloodNotes.getText().toString() : "");

        btnSubmitBloodRequest.setEnabled(false);
        FirebaseFirestore db = FirestoreHelper.getFirestore();
        db.collection(FirestoreCollections.BLOOD_REQUESTS).document(reqId)
                .set(req)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateBloodRequestActivity.this, "Blood request submitted successfully! Requisition ID: " + reqId, Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitBloodRequest.setEnabled(true);
                    Toast.makeText(CreateBloodRequestActivity.this, "Failed to submit blood request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
