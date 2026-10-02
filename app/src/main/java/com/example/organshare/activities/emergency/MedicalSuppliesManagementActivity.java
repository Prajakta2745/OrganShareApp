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
import com.example.organshare.adapters.emergency.MedicalSuppliesAdapter;
import com.example.organshare.models.emergency.MedicalSupplyResource;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class MedicalSuppliesManagementActivity extends AppCompatActivity implements MedicalSuppliesAdapter.OnSupplyItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddSupply;

    private MedicalSuppliesAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration suppliesListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_supplies_management);

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
        fabAddSupply = findViewById(R.id.fabAddSupply);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicalSuppliesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fabAddSupply.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        suppliesListener = repository.listenToMedicalSupplies(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setSuppliesList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setSuppliesList(list);
            }
        });
    }

    @Override
    public void onUpdateSupplyClicked(MedicalSupplyResource item) {
        showAddEditDialog(item);
    }

    private void showAddEditDialog(@Nullable MedicalSupplyResource item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_medical_supply, null);
        TextInputEditText etItemName = dialogView.findViewById(R.id.dialogEtItemName);
        Spinner spinnerCategory = dialogView.findViewById(R.id.dialogSpinnerCategory);
        TextInputEditText etAvailableQty = dialogView.findViewById(R.id.dialogEtAvailableQty);
        TextInputEditText etUnit = dialogView.findViewById(R.id.dialogEtUnit);
        TextInputEditText etHospital = dialogView.findViewById(R.id.dialogEtHospital);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        String[] categories = {"Equipment", "Oxygen", "Medication", "Surgical Kit", "PPE & Consumables"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(catAdapter);

        if (item != null) {
            etItemName.setText(item.getItemName());
            etAvailableQty.setText(String.valueOf(item.getAvailableQuantity()));
            etUnit.setText(item.getUnit());
            etHospital.setText(item.getHospitalName());
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(item.getCategory())) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etItemName.getText() != null ? etItemName.getText().toString().trim() : "";
            String unit = etUnit.getText() != null ? etUnit.getText().toString().trim() : "units";
            String hospital = etHospital.getText() != null ? etHospital.getText().toString().trim() : "Main Center";
            int qty;
            try {
                qty = Integer.parseInt(etAvailableQty.getText().toString().trim());
            } catch (Exception ex) {
                Toast.makeText(MedicalSuppliesManagementActivity.this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(name)) {
                etItemName.setError("Supply item name is required");
                return;
            }

            MedicalSupplyResource supply = item != null ? item : new MedicalSupplyResource();
            supply.setItemName(name);
            supply.setCategory(spinnerCategory.getSelectedItem().toString());
            supply.setAvailableQuantity(qty);
            supply.setUnit(unit);
            supply.setHospitalName(hospital);

            repository.saveMedicalSupply(supply, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(MedicalSuppliesManagementActivity.this, "Supply saved successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(MedicalSuppliesManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (suppliesListener != null) suppliesListener.remove();
    }
}
