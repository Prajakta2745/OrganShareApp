package com.example.organshare.activities.hospital;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.emergency.HospitalBedResource;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.example.organshare.repositories.HospitalRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HospitalResourcesManagementActivity extends AppCompatActivity {

    private TextInputEditText etAvailGeneral, etTotalGeneral;
    private TextInputEditText etAvailIcu, etTotalIcu;
    private TextInputEditText etAvailEmergency, etTotalEmergency;
    private SwitchMaterial switchAcceptingDonors;
    private MaterialButton btnSaveResourceCapacity;
    private ProgressBar progressBar;

    private EmergencyCoordinatorRepository repository;
    private HospitalRepository hospitalRepository;
    private String hospitalId;
    private String hospitalName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_resources_management);

        repository = new EmergencyCoordinatorRepository();
        hospitalRepository = new HospitalRepository();

        String sessionUserId = AuthManager.getInstance(this).getSessionManager().getUserId();
        String sessionName = AuthManager.getInstance(this).getSessionManager().getUserName();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            hospitalId = user.getUid();
            hospitalName = (sessionName != null && !sessionName.isEmpty()) ? sessionName :
                    (user.getDisplayName() != null ? user.getDisplayName() : "Hospital Facility");
        } else if (sessionUserId != null && !sessionUserId.isEmpty()) {
            hospitalId = sessionUserId;
            hospitalName = sessionName != null ? sessionName : "Hospital Facility";
        } else {
            hospitalId = "HOSP-001";
            hospitalName = "City Multispeciality Hospital";
        }

        initViews();
        resolveHospitalProfileAndLoadBeds(sessionUserId != null ? sessionUserId : hospitalId);
    }

    private void resolveHospitalProfileAndLoadBeds(String userId) {
        if (userId != null && !userId.isEmpty()) {
            hospitalRepository.getHospitalByUserId(userId, new HospitalRepository.DataCallback<HospitalProfile>() {
                @Override
                public void onSuccess(HospitalProfile profile) {
                    if (profile != null) {
                        if (profile.getHospitalId() != null && !profile.getHospitalId().isEmpty()) {
                            hospitalId = profile.getHospitalId();
                        }
                        if (profile.getHospitalName() != null && !profile.getHospitalName().isEmpty()) {
                            hospitalName = profile.getHospitalName();
                        }
                    }
                    loadCurrentBeds();
                }

                @Override
                public void onFailure(String error) {
                    loadCurrentBeds();
                }
            });
        } else {
            loadCurrentBeds();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        etAvailGeneral = findViewById(R.id.etAvailGeneral);
        etTotalGeneral = findViewById(R.id.etTotalGeneral);
        etAvailIcu = findViewById(R.id.etAvailIcu);
        etTotalIcu = findViewById(R.id.etTotalIcu);
        etAvailEmergency = findViewById(R.id.etAvailEmergency);
        etTotalEmergency = findViewById(R.id.etTotalEmergency);
        switchAcceptingDonors = findViewById(R.id.switchAcceptingDonors);
        btnSaveResourceCapacity = findViewById(R.id.btnSaveResourceCapacity);
        progressBar = findViewById(R.id.progressBar);

        btnSaveResourceCapacity.setOnClickListener(v -> saveCapacity());
    }

    private void loadCurrentBeds() {
        progressBar.setVisibility(View.VISIBLE);
        repository.getHospitalBeds(hospitalId, new EmergencyCoordinatorRepository.ResourceCallback<HospitalBedResource>() {
            @Override
            public void onSuccess(HospitalBedResource beds) {
                progressBar.setVisibility(View.GONE);
                if (beds != null) {
                    etAvailGeneral.setText(String.valueOf(beds.getAvailableGeneralBeds()));
                    etTotalGeneral.setText(String.valueOf(beds.getTotalGeneralBeds()));
                    etAvailIcu.setText(String.valueOf(beds.getAvailableIcuBeds()));
                    etTotalIcu.setText(String.valueOf(beds.getTotalIcuBeds()));
                    etAvailEmergency.setText(String.valueOf(beds.getAvailableEmergencyBeds()));
                    etTotalEmergency.setText(String.valueOf(beds.getTotalEmergencyBeds()));
                    switchAcceptingDonors.setChecked(beds.isAcceptingDonors());
                } else {
                    // Default values
                    etAvailGeneral.setText("15");
                    etTotalGeneral.setText("20");
                    etAvailIcu.setText("4");
                    etTotalIcu.setText("6");
                    etAvailEmergency.setText("3");
                    etTotalEmergency.setText("5");
                    switchAcceptingDonors.setChecked(true);
                }
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(HospitalResourcesManagementActivity.this, "Could not load current beds: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCapacity() {
        int availGen = parseInt(etAvailGeneral.getText() != null ? etAvailGeneral.getText().toString() : "0");
        int totGen = parseInt(etTotalGeneral.getText() != null ? etTotalGeneral.getText().toString() : "0");
        int availIcu = parseInt(etAvailIcu.getText() != null ? etAvailIcu.getText().toString() : "0");
        int totIcu = parseInt(etTotalIcu.getText() != null ? etTotalIcu.getText().toString() : "0");
        int availEmerg = parseInt(etAvailEmergency.getText() != null ? etAvailEmergency.getText().toString() : "0");
        int totEmerg = parseInt(etTotalEmergency.getText() != null ? etTotalEmergency.getText().toString() : "0");
        boolean accepting = switchAcceptingDonors.isChecked();

        HospitalBedResource bedResource = new HospitalBedResource(
                hospitalId, hospitalName,
                totGen, availGen,
                totIcu, availIcu,
                totEmerg, availEmerg,
                accepting
        );

        progressBar.setVisibility(View.VISIBLE);
        btnSaveResourceCapacity.setEnabled(false);

        repository.saveHospitalBeds(bedResource, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                progressBar.setVisibility(View.GONE);
                btnSaveResourceCapacity.setEnabled(true);
                Toast.makeText(HospitalResourcesManagementActivity.this, "Hospital resources updated & synced successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                btnSaveResourceCapacity.setEnabled(true);
                Toast.makeText(HospitalResourcesManagementActivity.this, "Error updating resources: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
