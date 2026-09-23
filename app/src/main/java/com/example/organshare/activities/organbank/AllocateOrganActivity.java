package com.example.organshare.activities.organbank;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.InventoryAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.firebase.FirestoreTransactions;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.CompatibilityHelper;
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AllocateOrganActivity extends AppCompatActivity {

    private TextView tvAllocHeader, tvAllocSubHeader, tvAllocReqHospital, tvAllocReqNeeded;
    private RecyclerView rvCompatibleInventory;
    private InventoryAdapter adapter;
    private final List<OrganInventory> inventoryList = new ArrayList<>();
    private OrganBankRepository repository;
    private OrganRequest currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocate_organ);

        repository = new OrganBankRepository();
        currentRequest = (OrganRequest) getIntent().getSerializableExtra("REQUEST_DATA");

        initViews();
        setupRecyclerView();
        loadCompatibleInventory();
    }

    private void initViews() {
        tvAllocHeader = findViewById(R.id.tvAllocHeader);
        tvAllocSubHeader = findViewById(R.id.tvAllocSubHeader);
        tvAllocReqHospital = findViewById(R.id.tvAllocReqHospital);
        tvAllocReqNeeded = findViewById(R.id.tvAllocReqNeeded);
        rvCompatibleInventory = findViewById(R.id.rvCompatibleInventory);

        if (currentRequest != null) {
            tvAllocSubHeader.setText("Select inventory organ compatible with Request: " + currentRequest.getRequestId());
            tvAllocReqHospital.setText("Hospital: " + currentRequest.getHospitalName());
            tvAllocReqNeeded.setText("Needed: " + currentRequest.getOrganRequired() + " (Blood: " + currentRequest.getBloodGroup() + ") | Priority: " + currentRequest.getEmergencyLevel());
        }
    }

    private void setupRecyclerView() {
        rvCompatibleInventory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this, inventoryList, this::showCompatibilityAndAllocationDialog);
        rvCompatibleInventory.setAdapter(adapter);
    }

    private void loadCompatibleInventory() {
        repository.getInventory(new OrganBankRepository.DataCallback<List<OrganInventory>>() {
            @Override
            public void onSuccess(List<OrganInventory> result) {
                inventoryList.clear();
                if (result != null && currentRequest != null) {
                    for (OrganInventory item : result) {
                        // Filter available and same organ type
                        if (Constants.INV_AVAILABLE.equalsIgnoreCase(item.getAvailabilityStatus()) &&
                                currentRequest.getOrganRequired().equalsIgnoreCase(item.getOrganType())) {
                            inventoryList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                if (inventoryList.isEmpty()) {
                    Toast.makeText(AllocateOrganActivity.this, "No available " + (currentRequest != null ? currentRequest.getOrganRequired() : "organs") + " in active vault.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AllocateOrganActivity.this, "Error loading inventory: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCompatibilityAndAllocationDialog(OrganInventory selectedOrgan) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_compatibility_check);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvCompatResult = dialog.findViewById(R.id.tvCompatResult);
        TextView tvPreservationWarning = dialog.findViewById(R.id.tvPreservationWarning);
        CheckBox cbConfirmPhysician = dialog.findViewById(R.id.cbConfirmPhysician);
        MaterialButton btnCancelCompat = dialog.findViewById(R.id.btnCancelCompat);
        MaterialButton btnProceedAllocation = dialog.findViewById(R.id.btnProceedAllocation);

        String matchStatus = CompatibilityHelper.getCompatibilityStatusString(
                currentRequest.getOrganRequired(),
                currentRequest.getBloodGroup(),
                selectedOrgan.getOrganType(),
                selectedOrgan.getBloodGroup()
        );
        tvCompatResult.setText("Software Match: " + matchStatus);

        int maxHours = CompatibilityHelper.getStandardPreservationHours(selectedOrgan.getOrganType());
        tvPreservationWarning.setText("Preservation Window: Max " + maxHours + " Hours (" + selectedOrgan.getOrganType() + " Cold Ischemia Limit)");

        btnCancelCompat.setOnClickListener(v -> dialog.dismiss());

        btnProceedAllocation.setOnClickListener(v -> {
            if (!cbConfirmPhysician.isChecked()) {
                Toast.makeText(this, "Please confirm clinical cross-match & board authorization.", Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            performAtomicAllocation(selectedOrgan);
        });

        dialog.show();
    }

    private void performAtomicAllocation(OrganInventory selectedOrgan) {
        String coordinatorId = AuthManager.getInstance(this).getSessionManager().getUserId();
        String coordinatorName = AuthManager.getInstance(this).getSessionManager().getUserName();

        Toast.makeText(this, "Executing atomic allocation lock...", Toast.LENGTH_SHORT).show();

        FirestoreTransactions.executeOrganAllocation(
                selectedOrgan.getInventoryId(),
                currentRequest.getRequestId(),
                coordinatorId,
                coordinatorName,
                selectedOrgan.getOrganBankId() != null ? selectedOrgan.getOrganBankId() : "BANK-001",
                selectedOrgan.getOrganBankName() != null ? selectedOrgan.getOrganBankName() : "National Organ Bank",
                "742 Healthcare Boulevard, Metro City",
                40.7128,
                -74.0060,
                new FirestoreTransactions.AllocationCallback() {
                    @Override
                    public void onSuccess(String orderId, String allocationId) {
                        Toast.makeText(AllocateOrganActivity.this, "Allocation successful! Order " + orderId + " created.", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(AllocateOrganActivity.this, "Allocation failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
