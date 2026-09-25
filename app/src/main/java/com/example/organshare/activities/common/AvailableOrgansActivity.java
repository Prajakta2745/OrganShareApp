package com.example.organshare.activities.common;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.InventoryAdapter;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AvailableOrgansActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AutoCompleteTextView actvFilterOrganType, actvFilterBloodGroup, actvFilterStatus;
    private TextInputEditText etFilterLocation;
    private TextView tvAvailableOrgansCount, tvEmptyAvailableOrgans;
    private ProgressBar progressBarAvailableOrgans;
    private RecyclerView rvAvailableOrgans;
    private InventoryAdapter adapter;
    private final List<OrganInventory> fullInventoryList = new ArrayList<>();
    private final List<OrganInventory> filteredList = new ArrayList<>();
    private OrganBankRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_organs);

        repository = new OrganBankRepository();

        initViews();
        setupNavigationDrawer();
        setupDropdownFilters();
        setupRecyclerView();
        loadAllOrgans();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        actvFilterOrganType = findViewById(R.id.actvFilterOrganType);
        actvFilterBloodGroup = findViewById(R.id.actvFilterBloodGroup);
        actvFilterStatus = findViewById(R.id.actvFilterStatus);
        etFilterLocation = findViewById(R.id.etFilterLocation);
        tvAvailableOrgansCount = findViewById(R.id.tvAvailableOrgansCount);
        tvEmptyAvailableOrgans = findViewById(R.id.tvEmptyAvailableOrgans);
        progressBarAvailableOrgans = findViewById(R.id.progressBarAvailableOrgans);
        rvAvailableOrgans = findViewById(R.id.rvAvailableOrgans);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupDropdownFilters() {
        List<String> organTypes = new ArrayList<>();
        organTypes.add("All");
        organTypes.addAll(Arrays.asList(Constants.ORGAN_TYPES));
        actvFilterOrganType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, organTypes));

        List<String> bloodGroups = new ArrayList<>();
        bloodGroups.add("All");
        bloodGroups.addAll(Arrays.asList(Constants.BLOOD_GROUPS));
        actvFilterBloodGroup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, bloodGroups));

        String[] statuses = {"AVAILABLE", "ALL", "RESERVED", "ALLOCATED", "IN TRANSIT", "DELIVERED", "UNAVAILABLE"};
        actvFilterStatus.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses));

        actvFilterOrganType.setOnItemClickListener((p, v, pos, id) -> applyFilters());
        actvFilterBloodGroup.setOnItemClickListener((p, v, pos, id) -> applyFilters());
        actvFilterStatus.setOnItemClickListener((p, v, pos, id) -> applyFilters());

        etFilterLocation.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        rvAvailableOrgans.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(this, filteredList, item -> {
            Toast.makeText(AvailableOrgansActivity.this, "Organ ID: " + item.getInventoryId() + " (" + item.getOrganType() + ") Status: " + item.getAvailabilityStatus(), Toast.LENGTH_SHORT).show();
        });
        rvAvailableOrgans.setAdapter(adapter);
    }

    private void loadAllOrgans() {
        progressBarAvailableOrgans.setVisibility(View.VISIBLE);
        repository.getInventory(new OrganBankRepository.DataCallback<List<OrganInventory>>() {
            @Override
            public void onSuccess(List<OrganInventory> result) {
                progressBarAvailableOrgans.setVisibility(View.GONE);
                fullInventoryList.clear();
                if (result != null) {
                    fullInventoryList.addAll(result);
                }
                applyFilters();
            }

            @Override
            public void onFailure(String error) {
                progressBarAvailableOrgans.setVisibility(View.GONE);
                Toast.makeText(AvailableOrgansActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        String organFilter = actvFilterOrganType.getText().toString().trim();
        String bloodFilter = actvFilterBloodGroup.getText().toString().trim();
        String statusFilter = actvFilterStatus.getText().toString().trim();
        String locFilter = etFilterLocation.getText() != null ? etFilterLocation.getText().toString().trim().toLowerCase() : "";

        filteredList.clear();
        for (OrganInventory item : fullInventoryList) {
            boolean matchesOrgan = "All".equalsIgnoreCase(organFilter) || organFilter.equalsIgnoreCase(item.getOrganType());
            boolean matchesBlood = "All".equalsIgnoreCase(bloodFilter) || bloodFilter.equalsIgnoreCase(item.getBloodGroup());
            boolean matchesStatus = "ALL".equalsIgnoreCase(statusFilter) || statusFilter.equalsIgnoreCase(item.getAvailabilityStatus());
            
            boolean matchesLoc = locFilter.isEmpty() ||
                    (item.getStorageLocation() != null && item.getStorageLocation().toLowerCase().contains(locFilter)) ||
                    (item.getOrganBankName() != null && item.getOrganBankName().toLowerCase().contains(locFilter));

            if (matchesOrgan && matchesBlood && matchesStatus && matchesLoc) {
                filteredList.add(item);
            }
        }

        adapter.notifyDataSetChanged();
        tvAvailableOrgansCount.setText("Displaying " + filteredList.size() + " Verified Organ Records");

        if (filteredList.isEmpty()) {
            tvEmptyAvailableOrgans.setVisibility(View.VISIBLE);
        } else {
            tvEmptyAvailableOrgans.setVisibility(View.GONE);
        }
    }
}
