package com.example.organshare.activities.emergency;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organshare.R;
import com.example.organshare.adapters.emergency.VolunteersAdapter;
import com.example.organshare.models.emergency.Volunteer;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class VolunteerManagementActivity extends AppCompatActivity implements VolunteersAdapter.OnVolunteerItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddVolunteer;

    private VolunteersAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration volunteerListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_management);

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
        fabAddVolunteer = findViewById(R.id.fabAddVolunteer);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VolunteersAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        fabAddVolunteer.setOnClickListener(v -> showAddEditDialog(null));
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        volunteerListener = repository.listenToVolunteers(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setVolunteerList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setVolunteerList(list);
            }
        });
    }

    @Override
    public void onToggleActiveClicked(Volunteer item) {
        item.setActive(!item.isActive());
        repository.saveVolunteer(item, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(VolunteerManagementActivity.this, "Volunteer status updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(VolunteerManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddEditDialog(@Nullable Volunteer item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_volunteer, null);
        TextInputEditText etFullName = dialogView.findViewById(R.id.dialogEtFullName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.dialogEtPhone);
        TextInputEditText etRole = dialogView.findViewById(R.id.dialogEtRole);
        TextInputEditText etCity = dialogView.findViewById(R.id.dialogEtCity);
        SwitchMaterial switchActive = dialogView.findViewById(R.id.dialogSwitchActive);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        if (item != null) {
            etFullName.setText(item.getFullName());
            etPhone.setText(item.getPhone());
            etRole.setText(item.getRoleOrSpecialty());
            etCity.setText(item.getCity());
            switchActive.setChecked(item.isActive());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
            String role = etRole.getText() != null ? etRole.getText().toString().trim() : "";
            String city = etCity.getText() != null ? etCity.getText().toString().trim() : "";

            if (TextUtils.isEmpty(fullName)) {
                etFullName.setError("Name is required");
                return;
            }

            Volunteer volunteer = item != null ? item : new Volunteer();
            volunteer.setFullName(fullName);
            volunteer.setPhone(phone);
            volunteer.setRoleOrSpecialty(role);
            volunteer.setCity(city);
            volunteer.setActive(switchActive.isChecked());

            repository.saveVolunteer(volunteer, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(VolunteerManagementActivity.this, "Volunteer saved successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(VolunteerManagementActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (volunteerListener != null) volunteerListener.remove();
    }
}
