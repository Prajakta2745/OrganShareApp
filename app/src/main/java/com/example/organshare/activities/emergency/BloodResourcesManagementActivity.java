package com.example.organshare.activities.emergency;

import
        android.os.Bundle;
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
import com.example.organshare.adapters.emergency.BloodResourcesAdapter;
import com.example.organshare.models.emergency.BloodResource;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class BloodResourcesManagementActivity extends AppCompatActivity implements BloodResourcesAdapter.OnBloodItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddBlood;

    private BloodResourcesAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration bloodListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_resources_management);

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
        fabAddBlood = findViewById(R.id.fabAddBlood);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BloodResourcesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fabAddBlood.setOnClickListener(v -> showAddBloodDialog(null));
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        bloodListener = repository.listenToAllBloodResources(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setBloodList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setBloodList(list);
            }
        });
    }

    @Override
    public void onUpdateBloodClicked(BloodResource item) {
        showAddBloodDialog(item);
    }

    private void showAddBloodDialog(@Nullable BloodResource item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_blood_units, null);
        TextInputEditText etHospitalId = dialogView.findViewById(R.id.dialogEtHospitalId);
        TextInputEditText etHospitalName = dialogView.findViewById(R.id.dialogEtHospitalName);
        Spinner spinnerBloodGroup = dialogView.findViewById(R.id.dialogSpinnerBloodGroup);
        Spinner spinnerRhFactor = dialogView.findViewById(R.id.dialogSpinnerRhFactor);
        TextInputEditText etUnits = dialogView.findViewById(R.id.dialogEtUnits);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> bgAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        spinnerBloodGroup.setAdapter(bgAdapter);

        String[] rhFactors = {"Positive", "Negative"};
        ArrayAdapter<String> rhAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rhFactors);
        spinnerRhFactor.setAdapter(rhAdapter);

        if (item != null) {
            etHospitalId.setText(item.getHospitalId());
            etHospitalName.setText(item.getHospitalName());
            etUnits.setText(String.valueOf(item.getAvailableUnits()));
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String hospId = etHospitalId.getText() != null ? etHospitalId.getText().toString().trim() : "hosp_1";
            String hospName = etHospitalName.getText() != null ? etHospitalName.getText().toString().trim() : "General Hospital";
            String bloodGroup = spinnerBloodGroup.getSelectedItem().toString();
            String rhFactor = spinnerRhFactor.getSelectedItem().toString();
            int units;
            try {
                units = Integer.parseInt(etUnits.getText().toString().trim());
            } catch (Exception ex) {
                Toast.makeText(BloodResourcesManagementActivity.this, "Invalid units value", Toast.LENGTH_SHORT).show();
                return;
            }

            BloodResource resource = item != null ? item : new BloodResource();
            resource.setHospitalId(hospId);
            resource.setHospitalName(hospName);
            resource.setBloodGroup(bloodGroup);
            resource.setRhFactor(rhFactor);
            resource.setAvailableUnits(units);

            repository.saveBloodResource(resource, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(BloodResourcesManagementActivity.this, "Blood stock saved successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(BloodResourcesManagementActivity.this, "Error saving blood stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bloodListener != null) bloodListener.remove();
    }
}
