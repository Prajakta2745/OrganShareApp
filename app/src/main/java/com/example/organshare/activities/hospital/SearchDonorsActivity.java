package com.example.organshare.activities.hospital;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.DonorSearchAdapter;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.repositories.DonorRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.DateTimeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchDonorsActivity extends AppCompatActivity {

    private AutoCompleteTextView actvSearchBlood, actvSearchOrgan;
    private TextInputEditText etSearchLocation;
    private MaterialButton btnPerformSearch;
    private ProgressBar progressBarSearch;
    private RecyclerView rvDonorSearchResults;
    private DonorSearchAdapter adapter;
    private final List<DonorProfile> donorList = new ArrayList<>();
    private DonorRepository donorRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_donors);

        donorRepository = new DonorRepository();

        initViews();
        setupDropdowns();
        setupRecyclerView();

        btnPerformSearch.setOnClickListener(v -> performSearch());

        // Initial search
        performSearch();
    }

    private void initViews() {
        actvSearchBlood = findViewById(R.id.actvSearchBlood);
        actvSearchOrgan = findViewById(R.id.actvSearchOrgan);
        etSearchLocation = findViewById(R.id.etSearchLocation);
        btnPerformSearch = findViewById(R.id.btnPerformSearch);
        progressBarSearch = findViewById(R.id.progressBarSearch);
        rvDonorSearchResults = findViewById(R.id.rvDonorSearchResults);
    }

    private void setupDropdowns() {
        List<String> bloods = new ArrayList<>();
        bloods.add("All");
        bloods.addAll(Arrays.asList(Constants.BLOOD_GROUPS));
        actvSearchBlood.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloods));

        List<String> organs = new ArrayList<>();
        organs.add("All");
        organs.addAll(Arrays.asList(Constants.ORGAN_TYPES));
        actvSearchOrgan.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, organs));
    }

    private void setupRecyclerView() {
        rvDonorSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorSearchAdapter(this, donorList, new DonorSearchAdapter.OnDonorActionListener() {
            @Override
            public void onRequestContact(DonorProfile donor) {
                Toast.makeText(SearchDonorsActivity.this, "Request for Donor " + donor.getDonorId() + " forwarded to State Coordinator.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onMarkCollected(DonorProfile donor) {
                showMarkCollectedDialog(donor);
            }
        });
        rvDonorSearchResults.setAdapter(adapter);
    }

    private void showMarkCollectedDialog(DonorProfile donor) {
        List<String> organs = donor.getOrgansWillingToDonate();
        String defaultOrgan = (organs != null && !organs.isEmpty()) ? organs.get(0) : "Kidney";

        new AlertDialog.Builder(this)
                .setTitle("Mark Pledged Organ as Collected")
                .setMessage("Convert verified pledge from Donor (" + donor.getMaskedName() + ") into Available Organ Vault Inventory?")
                .setPositiveButton("Confirm Collection", (dialog, which) -> {
                    convertPledgeToAvailableStock(donor, defaultOrgan);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void convertPledgeToAvailableStock(DonorProfile donor, String organType) {
        FirebaseFirestore db = FirestoreHelper.getFirestore();
        String invId = "INV-" + (System.currentTimeMillis() % 100000);

        OrganInventory item = new OrganInventory();
        item.setInventoryId(invId);
        item.setOrganType(organType);
        item.setBloodGroup(donor.getBloodGroup() != null ? donor.getBloodGroup() : "O+");
        item.setAvailabilityStatus(Constants.INV_AVAILABLE);
        item.setDonorRefId(donor.getDonorId());
        item.setDonorMaskedName(donor.getMaskedName());
        item.setOrganBankId("BANK-001");
        item.setOrganBankName("Central Organ Preservation Bank");
        item.setCollectionDate(DateTimeUtils.getCurrentDate());
        item.setExpiryDate(DateTimeUtils.getCurrentDate());
        item.setPreservationLimitHours(24);
        item.setStorageLocation("Preservation Suite Bay " + ((System.currentTimeMillis() % 5) + 1));
        item.setStorageTemperature("4°C Hypothermic Perfusion");
        item.setVerified(true);
        item.setNotes("Pledge verified and registered into active vault inventory.");

        db.collection(FirestoreCollections.INVENTORY).document(invId)
                .set(item)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SearchDonorsActivity.this, "Pledge converted! Organ Inventory ID: " + invId + " is now AVAILABLE for Organ Bank allocation.", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SearchDonorsActivity.this, "Error converting pledge: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void performSearch() {
        String blood = actvSearchBlood.getText().toString().trim();
        String organ = actvSearchOrgan.getText().toString().trim();
        String location = etSearchLocation.getText() != null ? etSearchLocation.getText().toString().trim() : "";

        if ("All".equalsIgnoreCase(blood)) blood = "";
        if ("All".equalsIgnoreCase(organ)) organ = "";

        progressBarSearch.setVisibility(View.VISIBLE);

        donorRepository.searchDonors(blood, organ, location, new DonorRepository.DataCallback<List<DonorProfile>>() {
            @Override
            public void onSuccess(List<DonorProfile> result) {
                progressBarSearch.setVisibility(View.GONE);
                donorList.clear();
                if (result != null) {
                    donorList.addAll(result);
                }
                adapter.notifyDataSetChanged();
                if (donorList.isEmpty()) {
                    Toast.makeText(SearchDonorsActivity.this, "No matching donors found for the given criteria.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(String error) {
                progressBarSearch.setVisibility(View.GONE);
                Toast.makeText(SearchDonorsActivity.this, "Search error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
