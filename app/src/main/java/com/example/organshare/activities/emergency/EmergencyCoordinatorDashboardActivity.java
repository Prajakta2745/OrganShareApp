package com.example.organshare.activities.emergency;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.emergency.EmergencyRequestsAdapter;
import com.example.organshare.models.emergency.EmergencyRequest;
import com.example.organshare.models.emergency.HospitalBedResource;
import com.example.organshare.models.emergency.MedicalSupplyResource;
import com.example.organshare.models.emergency.TransportResource;
import com.example.organshare.models.emergency.Volunteer;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class EmergencyCoordinatorDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvSummaryBeds, tvSummaryBlood, tvSummaryTransport, tvSummaryVolunteers, tvSummarySupplies, tvSummaryRequests;
    private TextView tvViewAllRequests, tvAlertBlood, tvAlertICU;
    private MaterialCardView cardCoordBeds, cardCoordBlood, cardCoordTransport, cardCoordVolunteers, cardCoordSupplies, cardCoordRequests;
    private MaterialButton btnViewResourceHistory;
    private RecyclerView rvCoordinatorRequests;
    private EmergencyRequestsAdapter requestsAdapter;
    private final List<EmergencyRequest> activeRequests = new ArrayList<>();
    private EmergencyCoordinatorRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_coordinator_dashboard);

        repository = new EmergencyCoordinatorRepository();

        initViews();
        setupNavigationDrawer();
        setupCardClicks();
        setupRequestsRecyclerView();
        loadDashboardMetrics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardMetrics();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        tvSummaryBeds = findViewById(R.id.tvSummaryBeds);
        tvSummaryBlood = findViewById(R.id.tvSummaryBlood);
        tvSummaryTransport = findViewById(R.id.tvSummaryTransport);
        tvSummaryVolunteers = findViewById(R.id.tvSummaryVolunteers);
        tvSummarySupplies = findViewById(R.id.tvSummarySupplies);
        tvSummaryRequests = findViewById(R.id.tvSummaryRequests);
        tvViewAllRequests = findViewById(R.id.tvViewAllRequests);
        tvAlertBlood = findViewById(R.id.tvAlertBlood);
        tvAlertICU = findViewById(R.id.tvAlertICU);

        cardCoordBeds = findViewById(R.id.cardCoordBeds);
        cardCoordBlood = findViewById(R.id.cardCoordBlood);
        cardCoordTransport = findViewById(R.id.cardCoordTransport);
        cardCoordVolunteers = findViewById(R.id.cardCoordVolunteers);
        cardCoordSupplies = findViewById(R.id.cardCoordSupplies);
        cardCoordRequests = findViewById(R.id.cardCoordRequests);
        btnViewResourceHistory = findViewById(R.id.btnViewResourceHistory);
        rvCoordinatorRequests = findViewById(R.id.rvCoordinatorRequests);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupCardClicks() {
        cardCoordBeds.setOnClickListener(v -> startActivity(new Intent(this, HospitalBedsManagementActivity.class)));
        cardCoordBlood.setOnClickListener(v -> startActivity(new Intent(this, BloodResourcesManagementActivity.class)));
        cardCoordTransport.setOnClickListener(v -> startActivity(new Intent(this, EmergencyTransportManagementActivity.class)));
        cardCoordVolunteers.setOnClickListener(v -> startActivity(new Intent(this, VolunteerManagementActivity.class)));
        cardCoordSupplies.setOnClickListener(v -> startActivity(new Intent(this, MedicalSuppliesManagementActivity.class)));
        cardCoordRequests.setOnClickListener(v -> startActivity(new Intent(this, EmergencyRequestsActivity.class)));
        tvViewAllRequests.setOnClickListener(v -> startActivity(new Intent(this, EmergencyRequestsActivity.class)));
        btnViewResourceHistory.setOnClickListener(v -> startActivity(new Intent(this, ResourceHistoryActivity.class)));
    }

    private void setupRequestsRecyclerView() {
        rvCoordinatorRequests.setLayoutManager(new LinearLayoutManager(this));
        requestsAdapter = new EmergencyRequestsAdapter(this, activeRequests, item -> {
            Intent intent = new Intent(this, ResourceAllocationActivity.class);
            intent.putExtra("EMERGENCY_REQUEST_DATA", item);
            startActivity(intent);
        });
        rvCoordinatorRequests.setAdapter(requestsAdapter);
    }

    private void loadDashboardMetrics() {
        // Beds
        repository.getAllHospitalBeds(new EmergencyCoordinatorRepository.DataCallback<List<HospitalBedResource>>() {
            @Override
            public void onSuccess(List<HospitalBedResource> result) {
                int totalAvail = 0;
                int icuAvail = 0;
                if (result != null) {
                    for (HospitalBedResource b : result) {
                        totalAvail += b.getAvailableBeds();
                        icuAvail += b.getAvailableICUBeds();
                    }
                }
                tvSummaryBeds.setText("Avail: " + totalAvail);
                if (icuAvail <= 3) {
                    tvAlertICU.setText("🟠 ICU Beds — Low Regional Capacity (" + icuAvail + " ICU Beds Remaining)");
                }
            }

            @Override public void onFailure(String error) {}
        });

        // Transport
        repository.getAllTransportResources(new EmergencyCoordinatorRepository.DataCallback<List<TransportResource>>() {
            @Override
            public void onSuccess(List<TransportResource> result) {
                int avail = 0;
                if (result != null) {
                    for (TransportResource t : result) {
                        if ("AVAILABLE".equalsIgnoreCase(t.getAvailabilityStatus())) avail++;
                    }
                }
                tvSummaryTransport.setText("Avail: " + avail + " / " + (result != null ? result.size() : 0));
            }

            @Override public void onFailure(String error) {}
        });

        // Volunteers
        repository.getAllVolunteers(new EmergencyCoordinatorRepository.DataCallback<List<Volunteer>>() {
            @Override
            public void onSuccess(List<Volunteer> result) {
                int avail = 0;
                if (result != null) {
                    for (Volunteer v : result) {
                        if ("AVAILABLE".equalsIgnoreCase(v.getAvailabilityStatus())) avail++;
                    }
                }
                tvSummaryVolunteers.setText("Active: " + avail);
            }

            @Override public void onFailure(String error) {}
        });

        // Supplies
        repository.getAllMedicalSupplies(new EmergencyCoordinatorRepository.DataCallback<List<MedicalSupplyResource>>() {
            @Override
            public void onSuccess(List<MedicalSupplyResource> result) {
                int count = result != null ? result.size() : 0;
                tvSummarySupplies.setText("Tracked: " + count + " Types");
            }

            @Override public void onFailure(String error) {}
        });

        // Requests
        repository.getAllEmergencyRequests(new EmergencyCoordinatorRepository.DataCallback<List<EmergencyRequest>>() {
            @Override
            public void onSuccess(List<EmergencyRequest> result) {
                activeRequests.clear();
                int pending = 0;
                if (result != null) {
                    for (EmergencyRequest r : result) {
                        if (!"FULFILLED".equalsIgnoreCase(r.getStatus()) && !"CANCELLED".equalsIgnoreCase(r.getStatus())) {
                            pending++;
                            if (activeRequests.size() < 5) activeRequests.add(r);
                        }
                    }
                }
                tvSummaryRequests.setText("Pending: " + pending);
                requestsAdapter.notifyDataSetChanged();
            }

            @Override public void onFailure(String error) {}
        });
    }
}
