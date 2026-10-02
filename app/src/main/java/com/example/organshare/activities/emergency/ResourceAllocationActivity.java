package com.example.organshare.activities.emergency;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.organshare.R;
import com.example.organshare.models.emergency.EmergencyRequest;
import com.example.organshare.models.emergency.ResourceAllocation;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResourceAllocationActivity extends AppCompatActivity {

    private Spinner spinnerResourceType;
    private TextInputEditText etSourceFacility, etTargetFacility, etQuantity, etNotes;
    private MaterialButton btnExecuteAllocation;
    private ProgressBar progressBar;

    private EmergencyCoordinatorRepository repository;
    private String linkedEmergencyRequestId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_allocation);

        repository = new EmergencyCoordinatorRepository();
        initViews();
        handleIntentData();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerResourceType = findViewById(R.id.spinnerResourceType);
        etSourceFacility = findViewById(R.id.etSourceFacility);
        etTargetFacility = findViewById(R.id.etTargetFacility);
        etQuantity = findViewById(R.id.etQuantity);
        etNotes = findViewById(R.id.etNotes);
        btnExecuteAllocation = findViewById(R.id.btnExecuteAllocation);
        progressBar = findViewById(R.id.progressBar);

        String[] types = {"Emergency Bed (General)", "ICU Bed", "Trauma Bed", "Blood Reserves (O- / Universal)", "Blood Platelets", "Emergency Ambulance", "Ventilator Support", "Oxygen Supply", "Volunteer Medic"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerResourceType.setAdapter(adapter);

        btnExecuteAllocation.setOnClickListener(v -> executeAllocation());
    }

    private void handleIntentData() {
        EmergencyRequest req = (EmergencyRequest) getIntent().getSerializableExtra("EMERGENCY_REQUEST_DATA");
        if (req != null) {
            linkedEmergencyRequestId = req.getEmergencyRequestId();
            if (req.getHospitalName() != null) {
                etTargetFacility.setText(req.getHospitalName());
            }
            if (req.getRequiredQuantity() > 0) {
                etQuantity.setText(String.valueOf(req.getRequiredQuantity()));
            }
            if (req.getDescription() != null) {
                etNotes.setText("Requisition #" + req.getEmergencyRequestId() + ": " + req.getDescription());
            }
        }
    }

    private void executeAllocation() {
        String source = etSourceFacility.getText() != null ? etSourceFacility.getText().toString().trim() : "";
        String target = etTargetFacility.getText() != null ? etTargetFacility.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";
        int qty;

        try {
            qty = Integer.parseInt(etQuantity.getText() != null ? etQuantity.getText().toString().trim() : "1");
        } catch (Exception ex) {
            Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(source)) {
            etSourceFacility.setError("Source facility is required");
            return;
        }
        if (TextUtils.isEmpty(target)) {
            etTargetFacility.setError("Target facility is required");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String coordinatorId = user != null ? user.getUid() : "admin_coordinator";
        String coordinatorName = user != null && user.getDisplayName() != null ? user.getDisplayName() : "Chief Coordinator";

        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setEmergencyRequestId(linkedEmergencyRequestId);
        allocation.setResourceType(spinnerResourceType.getSelectedItem().toString());
        allocation.setSourceFacilityName(source);
        allocation.setTargetFacilityName(target);
        allocation.setAllocatedQuantity(qty);
        allocation.setCoordinatorId(coordinatorId);
        allocation.setCoordinatorName(coordinatorName);
        allocation.setNotes(notes);
        allocation.setStatus("COMPLETED");

        progressBar.setVisibility(View.VISIBLE);
        btnExecuteAllocation.setEnabled(false);

        repository.recordAllocation(allocation, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                progressBar.setVisibility(View.GONE);
                btnExecuteAllocation.setEnabled(true);
                Toast.makeText(ResourceAllocationActivity.this, "Allocation authorized & recorded in audit history!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                btnExecuteAllocation.setEnabled(true);
                Toast.makeText(ResourceAllocationActivity.this, "Allocation error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
