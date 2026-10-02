package com.example.organshare.activities.emergency;

import android.os.Bundle;
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
import com.example.organshare.adapters.emergency.HospitalBedsAdapter;
import com.example.organshare.models.emergency.HospitalBedResource;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class HospitalBedsManagementActivity extends AppCompatActivity implements HospitalBedsAdapter.OnBedItemClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private HospitalBedsAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration bedsListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_beds_management);

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

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HospitalBedsAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        bedsListener = repository.listenToAllHospitalBeds(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setBedsList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setBedsList(list);
            }
        });
    }

    @Override
    public void onUpdateBedsClicked(HospitalBedResource item) {
        showUpdateDialog(item);
    }

    private void showUpdateDialog(HospitalBedResource item) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_hospital_beds, null);
        TextInputEditText etAvailGeneral = dialogView.findViewById(R.id.dialogEtAvailGeneral);
        TextInputEditText etTotalGeneral = dialogView.findViewById(R.id.dialogEtTotalGeneral);
        TextInputEditText etAvailIcu = dialogView.findViewById(R.id.dialogEtAvailIcu);
        TextInputEditText etTotalIcu = dialogView.findViewById(R.id.dialogEtTotalIcu);
        TextInputEditText etAvailEmergency = dialogView.findViewById(R.id.dialogEtAvailEmergency);
        TextInputEditText etTotalEmergency = dialogView.findViewById(R.id.dialogEtTotalEmergency);
        SwitchMaterial switchAccepting = dialogView.findViewById(R.id.dialogSwitchAccepting);
        MaterialButton btnCancel = dialogView.findViewById(R.id.dialogBtnCancel);
        MaterialButton btnSave = dialogView.findViewById(R.id.dialogBtnSave);

        etAvailGeneral.setText(String.valueOf(item.getAvailableGeneralBeds()));
        etTotalGeneral.setText(String.valueOf(item.getTotalGeneralBeds()));
        etAvailIcu.setText(String.valueOf(item.getAvailableIcuBeds()));
        etTotalIcu.setText(String.valueOf(item.getTotalIcuBeds()));
        etAvailEmergency.setText(String.valueOf(item.getAvailableEmergencyBeds()));
        etTotalEmergency.setText(String.valueOf(item.getTotalEmergencyBeds()));
        switchAccepting.setChecked(item.isAcceptingDonors());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            try {
                int availGen = Integer.parseInt(etAvailGeneral.getText().toString().trim());
                int totGen = Integer.parseInt(etTotalGeneral.getText().toString().trim());
                int availIcu = Integer.parseInt(etAvailIcu.getText().toString().trim());
                int totIcu = Integer.parseInt(etTotalIcu.getText().toString().trim());
                int availEmerg = Integer.parseInt(etAvailEmergency.getText().toString().trim());
                int totEmerg = Integer.parseInt(etTotalEmergency.getText().toString().trim());
                boolean accepting = switchAccepting.isChecked();

                item.setAvailableGeneralBeds(availGen);
                item.setTotalGeneralBeds(totGen);
                item.setAvailableIcuBeds(availIcu);
                item.setTotalIcuBeds(totIcu);
                item.setAvailableEmergencyBeds(availEmerg);
                item.setTotalEmergencyBeds(totEmerg);
                item.setAcceptingDonors(accepting);

                repository.saveHospitalBeds(item, new EmergencyCoordinatorRepository.ResourceCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(HospitalBedsManagementActivity.this, "Hospital beds updated successfully!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(HospitalBedsManagementActivity.this, "Update error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception ex) {
                Toast.makeText(HospitalBedsManagementActivity.this, "Please enter valid numeric values", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bedsListener != null) bedsListener.remove();
    }
}
