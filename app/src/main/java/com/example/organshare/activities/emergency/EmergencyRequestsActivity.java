package com.example.organshare.activities.emergency;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organshare.R;
import com.example.organshare.adapters.emergency.EmergencyRequestsAdapter;
import com.example.organshare.models.emergency.EmergencyRequest;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class EmergencyRequestsActivity extends AppCompatActivity implements EmergencyRequestsAdapter.OnEmergencyRequestClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabCreateRequest;

    private EmergencyRequestsAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration requestsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_requests);

        repository = new EmergencyCoordinatorRepository();
        initViews();
        setupListener();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        fabCreateRequest = findViewById(R.id.fabCreateRequest);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyRequestsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fabCreateRequest.setOnClickListener(v -> showCreateDialog());
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        requestsListener = repository.listenToEmergencyRequests(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setRequestList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setRequestList(list);
            }
        });
    }

    @Override
    public void onUpdateStatusClicked(EmergencyRequest item) {
        String[] options = {"IN_PROGRESS", "FULFILLED", "CANCELLED"};
        new AlertDialog.Builder(this)
                .setTitle("Update Emergency Status")
                .setItems(options, (dialog, which) -> {
                    String newStatus = options[which];
                    item.setStatus(newStatus);
                    repository.createEmergencyRequest(item, new EmergencyCoordinatorRepository.ResourceCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            Toast.makeText(EmergencyRequestsActivity.this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(EmergencyRequestsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    private void showCreateDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_emergency_request, null);
        TextInputEditText etHospitalName = dialogView.findViewById(R.id.dialogEtHospitalName);
        Spinner spinnerResourceType = dialogView.findViewById(R.id.dialogSpinnerResourceType);
        Spinner spinnerPriority = dialogView.findViewById(R.id.dialogSpinnerPriority);
        TextInputEditText etQuantity = dialogView.findViewById(R.id.dialogEtQuantity);
        TextInputEditText etDescription = dialogView.findViewById(R.id.dialogEtDescription);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSubmit = dialogView.findViewById(R.id.dialogBtnSubmit);

        String[] types = {"ICU Bed", "Blood Unit (O-)", "Emergency Blood (Any)", "Ambulance Transport", "Oxygen Cylinder", "Ventilator"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerResourceType.setAdapter(typeAdapter);

        String[] priorities = {"CRITICAL", "HIGH", "MEDIUM", "LOW"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorities);
        spinnerPriority.setAdapter(priorityAdapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String hospName = etHospitalName.getText() != null ? etHospitalName.getText().toString().trim() : "";
            String desc = etDescription.getText() != null ? etDescription.getText().toString().trim() : "";
            int qty;
            try {
                qty = Integer.parseInt(etQuantity.getText().toString().trim());
            } catch (Exception ex) {
                Toast.makeText(EmergencyRequestsActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(hospName)) {
                etHospitalName.setError("Hospital name is required");
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String userId = user != null ? user.getUid() : "coordinator_1";

            EmergencyRequest req = new EmergencyRequest();
            req.setHospitalId(userId);
            req.setHospitalName(hospName);
            req.setResourceType(spinnerResourceType.getSelectedItem().toString());
            req.setPriority(spinnerPriority.getSelectedItem().toString());
            req.setRequiredQuantity(qty);
            req.setDescription(desc);
            req.setStatus("OPEN");

            repository.createEmergencyRequest(req, new EmergencyCoordinatorRepository.ResourceCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Toast.makeText(EmergencyRequestsActivity.this, "Emergency Request broadcasted successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EmergencyRequestsActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestsListener != null) requestsListener.remove();
    }
}
