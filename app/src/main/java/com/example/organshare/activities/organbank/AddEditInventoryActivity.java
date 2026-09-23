package com.example.organshare.activities.organbank;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.OrganBankProfile;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.CompatibilityHelper;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.example.organshare.utils.IdGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditInventoryActivity extends AppCompatActivity {

    private AutoCompleteTextView actvInvOrganType, actvInvBloodGroup;
    private TextInputEditText etInvDonorRef, etInvStorageLocation, etInvStorageTemp;
    private MaterialButton btnSaveInventoryItem;
    private OrganBankRepository repository;
    private OrganBankProfile bankProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_inventory);

        repository = new OrganBankRepository();

        initViews();
        setupDropdowns();
        loadBankData();

        btnSaveInventoryItem.setOnClickListener(v -> saveInventory());
    }

    private void initViews() {
        actvInvOrganType = findViewById(R.id.actvInvOrganType);
        actvInvBloodGroup = findViewById(R.id.actvInvBloodGroup);
        etInvDonorRef = findViewById(R.id.etInvDonorRef);
        etInvStorageLocation = findViewById(R.id.etInvStorageLocation);
        etInvStorageTemp = findViewById(R.id.etInvStorageTemp);
        btnSaveInventoryItem = findViewById(R.id.btnSaveInventoryItem);
    }

    private void setupDropdowns() {
        actvInvOrganType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.ORGAN_TYPES));
        actvInvBloodGroup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Constants.BLOOD_GROUPS));
    }

    private void loadBankData() {
        String userId = AuthManager.getInstance(this).getSessionManager().getUserId();
        repository.getBankByUserId(userId, new OrganBankRepository.DataCallback<OrganBankProfile>() {
            @Override
            public void onSuccess(OrganBankProfile profile) {
                bankProfile = profile;
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    private void saveInventory() {
        String organ = actvInvOrganType.getText().toString().trim();
        String blood = actvInvBloodGroup.getText().toString().trim();
        String donorRef = etInvDonorRef.getText() != null ? etInvDonorRef.getText().toString().trim() : "DNR-1001";
        String storageLoc = etInvStorageLocation.getText() != null ? etInvStorageLocation.getText().toString().trim() : "Vault 1";
        String storageTemp = etInvStorageTemp.getText() != null ? etInvStorageTemp.getText().toString().trim() : "4°C Hypothermic";

        String invId = IdGenerator.generateInventoryId();
        OrganInventory item = new OrganInventory();
        item.setInventoryId(invId);
        item.setOrganType(organ);
        item.setBloodGroup(blood);
        item.setAvailabilityStatus(Constants.INV_AVAILABLE);
        item.setCollectionDate(DateTimeUtils.getCurrentDate());
        item.setExpiryDate(DateTimeUtils.getCurrentDate());
        item.setPreservationLimitHours(CompatibilityHelper.getStandardPreservationHours(organ));
        item.setDonorRefId(donorRef);
        item.setDonorMaskedName("Pledged Donor");
        item.setOrganBankId(bankProfile != null ? bankProfile.getBankId() : "BANK-001");
        item.setOrganBankName(bankProfile != null ? bankProfile.getBankName() : "National Organ Bank");
        item.setStorageLocation(storageLoc);
        item.setStorageTemperature(storageTemp);
        item.setVerified(true);

        repository.saveInventoryItem(item, new OrganBankRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(AddEditInventoryActivity.this, "Organ " + invId + " added to Vault successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AddEditInventoryActivity.this, "Error saving: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
