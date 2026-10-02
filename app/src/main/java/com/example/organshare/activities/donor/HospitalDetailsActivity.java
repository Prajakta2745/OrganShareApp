package com.example.organshare.activities.donor;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.organshare.R;
import com.example.organshare.models.emergency.BloodResource;
import com.example.organshare.models.emergency.HospitalBedResource;
import com.example.organshare.models.emergency.HospitalResourceOverview;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HospitalDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_HOSPITAL_ID = "extra_hospital_id";
    public static final String EXTRA_HOSPITAL_NAME = "extra_hospital_name";
    public static final String EXTRA_HOSPITAL_ADDRESS = "extra_hospital_address";
    public static final String EXTRA_HOSPITAL_PHONE = "extra_hospital_phone";

    private String hospitalId;
    private String hospitalName;
    private String hospitalAddress;
    private String hospitalPhone;

    private TextView tvHospitalName, tvHospitalAddress, tvContactPhone, tvAcceptingStatusBadge;
    private TextView tvGeneralBeds, tvIcuBeds, tvEmergencyBeds, tvLastUpdated, tvBloodOverview;
    private MaterialButton btnSelectHospital;
    private ProgressBar progressBar;

    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration bedsListener;
    private ListenerRegistration bloodListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);

        hospitalId = getIntent().getStringExtra(EXTRA_HOSPITAL_ID);
        hospitalName = getIntent().getStringExtra(EXTRA_HOSPITAL_NAME);
        hospitalAddress = getIntent().getStringExtra(EXTRA_HOSPITAL_ADDRESS);
        hospitalPhone = getIntent().getStringExtra(EXTRA_HOSPITAL_PHONE);

        if (hospitalId == null || hospitalId.isEmpty()) {
            HospitalResourceOverview overview = (HospitalResourceOverview) getIntent().getSerializableExtra("HOSPITAL_OVERVIEW_DATA");
            if (overview != null) {
                hospitalId = overview.getHospitalId();
                hospitalName = overview.getHospitalName();
                hospitalAddress = overview.getLocation();
                hospitalPhone = overview.getContactNumber();
            }
        }

        if (hospitalId == null || hospitalId.isEmpty()) {
            Toast.makeText(this, "Invalid hospital selected.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new EmergencyCoordinatorRepository();
        initViews();
        setupListeners();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvHospitalAddress = findViewById(R.id.tvHospitalAddress);
        tvContactPhone = findViewById(R.id.tvContactPhone);
        tvAcceptingStatusBadge = findViewById(R.id.tvAcceptingStatusBadge);

        tvGeneralBeds = findViewById(R.id.tvGeneralBeds);
        tvIcuBeds = findViewById(R.id.tvIcuBeds);
        tvEmergencyBeds = findViewById(R.id.tvEmergencyBeds);
        tvLastUpdated = findViewById(R.id.tvLastUpdated);
        tvBloodOverview = findViewById(R.id.tvBloodOverview);

        btnSelectHospital = findViewById(R.id.btnSelectHospital);
        progressBar = findViewById(R.id.progressBar);

        tvHospitalName.setText(hospitalName != null ? hospitalName : "Hospital Overview");
        tvHospitalAddress.setText(hospitalAddress != null ? hospitalAddress : "Address unavailable");
        tvContactPhone.setText("Phone: " + (hospitalPhone != null ? hospitalPhone : "N/A"));

        btnSelectHospital.setOnClickListener(v -> performFreshAvailabilityCheckAndProceed());
    }

    private void setupListeners() {
        bedsListener = repository.listenToHospitalBeds(hospitalId, beds -> {
            if (beds != null) {
                updateBedsUI(beds);
            }
        });

        bloodListener = repository.listenToBloodResources(hospitalId, bloodList -> {
            updateBloodUI(bloodList);
        });
    }

    private void updateBedsUI(HospitalBedResource beds) {
        tvGeneralBeds.setText(beds.getAvailableGeneralBeds() + " / " + beds.getTotalGeneralBeds());
        tvIcuBeds.setText(beds.getAvailableIcuBeds() + " / " + beds.getTotalIcuBeds());
        tvEmergencyBeds.setText(beds.getAvailableEmergencyBeds() + " / " + beds.getTotalEmergencyBeds());

        if (beds.isAcceptingDonors()) {
            tvAcceptingStatusBadge.setText("● ACCEPTING DONORS");
            tvAcceptingStatusBadge.setTextColor(0xFF16A34A);
            btnSelectHospital.setEnabled(true);
        } else {
            tvAcceptingStatusBadge.setText("● CAPACITY FULL / UNAVAILABLE");
            tvAcceptingStatusBadge.setTextColor(0xFFDC2626);
        }

        if (beds.getLastUpdated() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            tvLastUpdated.setText("Last updated: " + sdf.format(beds.getLastUpdated()));
        }
    }

    private void updateBloodUI(List<BloodResource> bloodList) {
        if (bloodList == null || bloodList.isEmpty()) {
            tvBloodOverview.setText("No blood units registered currently.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (BloodResource br : bloodList) {
            sb.append(br.getBloodGroup())
                    .append(" (").append(br.getRhFactor()).append("): ")
                    .append(br.getAvailableUnits()).append(" Units available")
                    .append("\n");
        }
        tvBloodOverview.setText(sb.toString().trim());
    }

    private void performFreshAvailabilityCheckAndProceed() {
        progressBar.setVisibility(View.VISIBLE);
        btnSelectHospital.setEnabled(false);

        // Strict Fresh Check from Firestore
        repository.getHospitalBeds(hospitalId, new EmergencyCoordinatorRepository.ResourceCallback<HospitalBedResource>() {
            @Override
            public void onSuccess(HospitalBedResource beds) {
                progressBar.setVisibility(View.GONE);
                btnSelectHospital.setEnabled(true);

                if (beds == null || !beds.isAcceptingDonors()) {
                    new AlertDialog.Builder(HospitalDetailsActivity.this)
                            .setTitle("Hospital Unavailable")
                            .setMessage("We just verified live database availability: This hospital is currently at maximum intake capacity or temporarily closed for donor admissions. Please select another hospital.")
                            .setPositiveButton("Choose Another", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                    return;
                }

                // Prompt user for Transport requirement
                showTransportOptionDialog();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                btnSelectHospital.setEnabled(true);
                Toast.makeText(HospitalDetailsActivity.this, "Live verification error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTransportOptionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hospital Verified & Selected!")
                .setMessage("Live capacity confirmed at " + hospitalName + ".\n\nDo you need an emergency ambulance / volunteer transit to reach the hospital?")
                .setPositiveButton("Yes, Request Transport", (dialog, which) -> {
                    Intent intent = new Intent(HospitalDetailsActivity.this, DonorTransportRequestActivity.class);
                    intent.putExtra(EXTRA_HOSPITAL_ID, hospitalId);
                    intent.putExtra(EXTRA_HOSPITAL_NAME, hospitalName);
                    startActivity(intent);
                })
                .setNegativeButton("No, I Will Travel Myself", (dialog, which) -> {
                    reserveDirectBedAndShowConfirmation();
                })
                .show();
    }

    private void reserveDirectBedAndShowConfirmation() {
        progressBar.setVisibility(View.VISIBLE);
        btnSelectHospital.setEnabled(false);

        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String uid = user != null ? user.getUid() : "donor_self";
        String name = user != null && user.getDisplayName() != null ? user.getDisplayName() : "Registered Donor";
        String phone = user != null && user.getPhoneNumber() != null ? user.getPhoneNumber() : "+91-98230-00000";

        repository.confirmHospitalAndReserveBed(hospitalId, uid, name, phone, false, "",
                new EmergencyCoordinatorRepository.ConfirmationCallback() {
                    @Override
                    public void onConfirmed(String emergencyRequestId, String transportRequestId, String message) {
                        progressBar.setVisibility(View.GONE);
                        btnSelectHospital.setEnabled(true);

                        new AlertDialog.Builder(HospitalDetailsActivity.this)
                                .setTitle("Bed Reserved Successfully!")
                                .setMessage("Your admission bed at " + hospitalName + " has been successfully reserved.\n\n" +
                                        "Confirmation Ticket: #" + emergencyRequestId + "\n\n" +
                                        "Hospital Address: " + (hospitalAddress != null ? hospitalAddress : "See dashboard") + "\n" +
                                        "Emergency Contact: " + (hospitalPhone != null ? hospitalPhone : "+91-98230-11111"))
                                .setPositiveButton("Go to Dashboard", (d, w) -> {
                                    Intent intent = new Intent(HospitalDetailsActivity.this, DonorDashboardActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    finish();
                                })
                                .setNegativeButton("Close", (d, w) -> finish())
                                .setCancelable(false)
                                .show();
                    }

                    @Override
                    public void onFailed(String reason) {
                        progressBar.setVisibility(View.GONE);
                        btnSelectHospital.setEnabled(true);
                        Toast.makeText(HospitalDetailsActivity.this, "Reservation note: " + reason, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bedsListener != null) bedsListener.remove();
        if (bloodListener != null) bloodListener.remove();
    }
}
