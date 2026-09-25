package com.example.organshare.activities.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.UserModel;
import com.example.organshare.repositories.DonorRepository;
import com.example.organshare.repositories.HospitalRepository;
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etEditDisplayName, etEditPhone, etEditCity, etEditEmergencyContact;
    private TextInputLayout tilEditEmergencyContact;
    private TextView tvProtectedRoleNote;
    private MaterialButton btnSaveProfileChanges;
    private ProgressBar progressBarEditProfile;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        authManager = AuthManager.getInstance(this);

        initViews();
        loadExistingData();

        btnSaveProfileChanges.setOnClickListener(v -> saveProfileChanges());
    }

    private void initViews() {
        etEditDisplayName = findViewById(R.id.etEditDisplayName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditCity = findViewById(R.id.etEditCity);
        etEditEmergencyContact = findViewById(R.id.etEditEmergencyContact);
        tilEditEmergencyContact = findViewById(R.id.tilEditEmergencyContact);
        tvProtectedRoleNote = findViewById(R.id.tvProtectedRoleNote);
        btnSaveProfileChanges = findViewById(R.id.btnSaveProfileChanges);
        progressBarEditProfile = findViewById(R.id.progressBarEditProfile);
    }

    private void loadExistingData() {
        String role = authManager.getSessionManager().getUserRole();
        String name = authManager.getSessionManager().getUserName();
        String uid = authManager.getSessionManager().getUserId();

        etEditDisplayName.setText(name);
        tvProtectedRoleNote.setText("🔒 Role: " + (role != null ? role.toUpperCase() : "DONOR") + " (Protected System Attribute)");

        if (Constants.ROLE_DONOR.equalsIgnoreCase(role)) {
            new DonorRepository().getDonorByUserId(uid, new DonorRepository.DataCallback<DonorProfile>() {
                @Override
                public void onSuccess(DonorProfile profile) {
                    if (profile != null) {
                        etEditPhone.setText(profile.getPhone());
                        etEditCity.setText(profile.getCity());
                        etEditEmergencyContact.setText(profile.getEmergencyContact());
                    }
                }
                @Override public void onFailure(String error) {}
            });
        } else if (Constants.ROLE_HOSPITAL.equalsIgnoreCase(role)) {
            tilEditEmergencyContact.setHint("Hospital 24/7 Hotline");
            new HospitalRepository().getHospitalByUserId(uid, new HospitalRepository.DataCallback<HospitalProfile>() {
                @Override
                public void onSuccess(HospitalProfile profile) {
                    if (profile != null) {
                        etEditPhone.setText(profile.getContactNumber());
                        etEditCity.setText(profile.getCity());
                        etEditEmergencyContact.setText(profile.getContactNumber());
                    }
                }
                @Override public void onFailure(String error) {}
            });
        }
    }

    private void saveProfileChanges() {
        final String newName = etEditDisplayName.getText() != null ? etEditDisplayName.getText().toString().trim() : "";
        final String newPhone = etEditPhone.getText() != null ? etEditPhone.getText().toString().trim() : "";
        final String newCity = etEditCity.getText() != null ? etEditCity.getText().toString().trim() : "";
        final String newEmergency = etEditEmergencyContact.getText() != null ? etEditEmergencyContact.getText().toString().trim() : "";
        final String uid = authManager.getSessionManager().getUserId();
        final String email = authManager.getSessionManager().getUserEmail();
        final String role = authManager.getSessionManager().getUserRole();

        if (newName.isEmpty()) {
            etEditDisplayName.setError("Name is required");
            return;
        }

        progressBarEditProfile.setVisibility(View.VISIBLE);
        btnSaveProfileChanges.setEnabled(false);

        // 1. Update Core User Document
        Map<String, Object> updates = new HashMap<>();
        updates.put("displayName", newName);
        updates.put("phone", newPhone);

        FirestoreHelper.getFirestore().collection(FirestoreCollections.USERS).document(uid)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update Local Session
                    authManager.getSessionManager().createLoginSession(uid, email, newName, role);

                    progressBarEditProfile.setVisibility(View.GONE);
                    btnSaveProfileChanges.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBarEditProfile.setVisibility(View.GONE);
                    btnSaveProfileChanges.setEnabled(true);
                    Toast.makeText(EditProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
