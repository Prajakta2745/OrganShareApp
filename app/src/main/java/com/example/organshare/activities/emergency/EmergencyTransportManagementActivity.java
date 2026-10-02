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
import com.example.organshare.adapters.emergency.TransportResourcesAdapter;
import com.example.organshare.models.emergency.TransportResource;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class EmergencyTransportManagementActivity extends AppCompatActivity implements TransportResourcesAdapter.OnTransportItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddTransport;

    private TransportResourcesAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration transportListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_transport_management);

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
        fabAddTransport = findViewById(R.id.fabAddTransport);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransportResourcesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fabAddTransport.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        transportListener = repository.listenToTransportResources(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setTransportList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setTransportList(list);
            }
        });
    }

    @Override
    public void onUpdateStatusClicked(TransportResource item) {
        showAddEditDialog(item);
    }

    private void showAddEditDialog(@Nullable TransportResource item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_transport, null);
        Spinner spinnerType = dialogView.findViewById(R.id.dialogSpinnerVehicleType);
        TextInputEditText etVehicleNumber = dialogView.findViewById(R.id.dialogEtVehicleNumber);
        TextInputEditText etDriverName = dialogView.findViewById(R.id.dialogEtDriverName);
        TextInputEditText etDriverPhone = dialogView.findViewById(R.id.dialogEtDriverPhone);
        TextInputEditText etLocation = dialogView.findViewById(R.id.dialogEtLocation);
        Spinner spinnerStatus = dialogView.findViewById(R.id.dialogSpinnerStatus);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        String[] types = {"ICU Ambulance", "Standard Ambulance", "Donor Shuttle", "Organ Transit Van"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        spinnerType.setAdapter(typeAdapter);

        String[] statuses = {"AVAILABLE", "IN_TRANSIT", "MAINTENANCE", "OFF_DUTY"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, statuses);
        spinnerStatus.setAdapter(statusAdapter);

        if (item != null) {
            etVehicleNumber.setText(item.getVehicleNumber());
            etDriverName.setText(item.getDriverName());
            etDriverPhone.setText(item.getDriverPhone());
            etLocation.setText(item.getCurrentLocationName());
            for (int i = 0; i < types.length; i++) {
                if (types[i].equalsIgnoreCase(item.getVehicleType())) {
                    spinnerType.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < statuses.length; i++) {
                if (statuses[i].equalsIgnoreCase(item.getStatus())) {
                    spinnerStatus.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String vehicleNum = etVehicleNumber.getText() != null ? etVehicleNumber.getText().toString().trim() : "";
            String driverName = etDriverName.getText() != null ? etDriverName.getText().toString().trim() : "";
            String driverPhone = etDriverPhone.getText() != null ? etDriverPhone.getText().toString().trim() : "";
            String location = etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

            if (TextUtils.isEmpty(vehicleNum)) {
                etVehicleNumber.setError("Vehicle number is required");
                return;
            }

            TransportResource resource = item != null ? item : new TransportResource();
            resource.setVehicleNumber(vehicleNum);
            resource.setVehicleType(spinnerType.getSelectedItem().toString());
            resource.setDriverName(driverName);
            resource.setDriverPhone(driverPhone);
            resource.setCurrentLocationName(location);
            resource.setStatus(spinnerStatus.getSelectedItem().toString());

            repository.saveTransportResource(resource, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(EmergencyTransportManagementActivity.this, "Transport unit saved!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(EmergencyTransportManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (transportListener != null) transportListener.remove();
    }
}
