package com.example.organshare.activities.donor;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DonorProfileActivity extends AppCompatActivity {

    private TextInputEditText etProfileName, etProfilePhone, etProfileCity, etProfileEmergency;
    private MaterialButton btnSaveProfile;
    private DonorRepository donorRepository;
    private DonorProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        donorRepository = new DonorRepository();
        etProfileName = findViewById(R.id.etProfileName);
        etProfilePhone = findViewById(R.id.etProfilePhone);
        etProfileCity = findViewById(R.id.etProfileCity);
        etProfileEmergency = findViewById(R.id.etProfileEmergency);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        String userId = AuthManager.getInstance(this).getSessionManager().getUserId();
        donorRepository.getDonorByUserId(userId, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null) {
                    currentProfile = profile;
                    etProfileName.setText(profile.getFullName());
                    etProfilePhone.setText(profile.getPhone());
                    etProfileCity.setText(profile.getCity());
                    etProfileEmergency.setText(profile.getEmergencyContact());
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DonorProfileActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentProfile == null) return;
        currentProfile.setFirstName(etProfileName.getText() != null ? etProfileName.getText().toString() : "");
        currentProfile.setPhone(etProfilePhone.getText() != null ? etProfilePhone.getText().toString() : "");
        currentProfile.setCity(etProfileCity.getText() != null ? etProfileCity.getText().toString() : "");
        currentProfile.setEmergencyContact(etProfileEmergency.getText() != null ? etProfileEmergency.getText().toString() : "");

        donorRepository.saveDonorProfile(currentProfile, new DonorRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(DonorProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(DonorProfileActivity.this, "Update failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
