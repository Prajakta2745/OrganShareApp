package com.example.organshare.activities.donor;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class BloodDonorRegistrationActivity extends AppCompatActivity {

    private AutoCompleteTextView actvBloodGroup;
    private SwitchMaterial switchEmergency;
    private TextInputEditText etLastDonationDate, etPreferredLocation, etContactPhone;
    private TextView tvBloodDonorStatusBadge;
    private CheckBox cbBloodConsent;
    private MaterialButton btnSaveBloodRegistration;

    private DonorRepository donorRepository;
    private DonorProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_donor_registration);

        donorRepository = new DonorRepository();

        initViews();
        setupDropdown();
        setupDatePicker();
        loadExistingData();

        btnSaveBloodRegistration.setOnClickListener(v -> saveBloodRegistration());
    }

    private void initViews() {
        actvBloodGroup = findViewById(R.id.actvBloodGroup);
        switchEmergency = findViewById(R.id.switchEmergency);
        etLastDonationDate = findViewById(R.id.etLastDonationDate);
        etPreferredLocation = findViewById(R.id.etPreferredLocation);
        etContactPhone = findViewById(R.id.etContactPhone);
        tvBloodDonorStatusBadge = findViewById(R.id.tvBloodDonorStatusBadge);
        cbBloodConsent = findViewById(R.id.cbBloodConsent);
        btnSaveBloodRegistration = findViewById(R.id.btnSaveBloodRegistration);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.BLOOD_GROUPS);
        actvBloodGroup.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etLastDonationDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etLastDonationDate.setText(selectedDate);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadExistingData() {
        String uid = AuthManager.getInstance(this).getSessionManager().getUserId();
        donorRepository.getDonorByUserId(uid, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null) {
                    currentProfile = profile;
                    if (profile.getBloodGroup() != null) actvBloodGroup.setText(profile.getBloodGroup(), false);
                    switchEmergency.setChecked(profile.isAvailableForEmergencyBlood());
                    etLastDonationDate.setText(profile.getLastBloodDonationDate() != null ? profile.getLastBloodDonationDate() : "");
                    etPreferredLocation.setText(profile.getPreferredBloodDonationLocation() != null ? profile.getPreferredBloodDonationLocation() : profile.getCity());
                    etContactPhone.setText(profile.getPhone() != null ? profile.getPhone() : "");
                    tvBloodDonorStatusBadge.setText(profile.getBloodDonorStatus() != null ? profile.getBloodDonorStatus() : "ACTIVE");
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(BloodDonorRegistrationActivity.this, "Failed to load data: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBloodRegistration() {
        if (actvBloodGroup.getText() == null || actvBloodGroup.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select your blood group.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbBloodConsent.isChecked()) {
            Toast.makeText(this, "Please accept the voluntary blood donor declaration.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentProfile == null) {
            currentProfile = new DonorProfile();
            currentProfile.setUserId(AuthManager.getInstance(this).getSessionManager().getUserId());
            currentProfile.setDonorId("DNR-" + (System.currentTimeMillis() % 100000));
        }

        currentProfile.setBloodGroup(actvBloodGroup.getText().toString().trim());
        currentProfile.setBloodDonorRegistered(true);
        currentProfile.setAvailableForEmergencyBlood(switchEmergency.isChecked());
        currentProfile.setLastBloodDonationDate(etLastDonationDate.getText() != null ? etLastDonationDate.getText().toString().trim() : "");
        currentProfile.setPreferredBloodDonationLocation(etPreferredLocation.getText() != null ? etPreferredLocation.getText().toString().trim() : "");
        currentProfile.setPhone(etContactPhone.getText() != null ? etContactPhone.getText().toString().trim() : currentProfile.getPhone());
        currentProfile.setBloodDonorStatus("ACTIVE");
        currentProfile.setBloodDonorConsentDate(DateTimeUtils.getCurrentDateTime());

        btnSaveBloodRegistration.setEnabled(false);
        donorRepository.saveDonorProfile(currentProfile, new DonorRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(BloodDonorRegistrationActivity.this, "Blood Donor Registration saved successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                btnSaveBloodRegistration.setEnabled(true);
                Toast.makeText(BloodDonorRegistrationActivity.this, "Error saving registration: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
