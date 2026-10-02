package com.example.organshare.activities.donor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.emergency.DonorAvailableHospitalsAdapter;
import com.example.organshare.models.emergency.HospitalResourceOverview;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;

public class AvailableHospitalsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvFilterStatusBanner;
    private ProgressBar progressBarHospitals;
    private LinearLayout layoutEmptyHospitals;
    private TextView tvEmptyHospitals;
    private MaterialButton btnRefreshHospitals;
    private RecyclerView rvAvailableHospitals;
    private DonorAvailableHospitalsAdapter adapter;
    private final List<HospitalResourceOverview> displayedList = new ArrayList<>();
    private EmergencyCoordinatorRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_hospitals);

        repository = new EmergencyCoordinatorRepository();

        initViews();
        setupNavigationDrawer();
        setupRecyclerView();
        loadHospitals();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvFilterStatusBanner = findViewById(R.id.tvFilterStatusBanner);
        progressBarHospitals = findViewById(R.id.progressBarHospitals);
        layoutEmptyHospitals = findViewById(R.id.layoutEmptyHospitals);
        tvEmptyHospitals = findViewById(R.id.tvEmptyHospitals);
        btnRefreshHospitals = findViewById(R.id.btnRefreshHospitals);
        rvAvailableHospitals = findViewById(R.id.rvAvailableHospitals);

        if (btnRefreshHospitals != null) {
            btnRefreshHospitals.setOnClickListener(v -> loadHospitals());
        }
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupRecyclerView() {
        rvAvailableHospitals.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorAvailableHospitalsAdapter(this, displayedList, hospital -> {
            Intent intent = new Intent(AvailableHospitalsActivity.this, HospitalDetailsActivity.class);
            intent.putExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_ID, hospital.getHospitalId());
            intent.putExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_NAME, hospital.getHospitalName());
            intent.putExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_ADDRESS, hospital.getLocation());
            intent.putExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_PHONE, hospital.getContactNumber());
            intent.putExtra("HOSPITAL_OVERVIEW_DATA", hospital);
            startActivity(intent);
        });
        rvAvailableHospitals.setAdapter(adapter);
    }

    private void loadHospitals() {
        progressBarHospitals.setVisibility(View.VISIBLE);
        layoutEmptyHospitals.setVisibility(View.GONE);
        rvAvailableHospitals.setVisibility(View.GONE);

        repository.getAllHospitalOverviews(new EmergencyCoordinatorRepository.DataCallback<List<HospitalResourceOverview>>() {
            @Override
            public void onSuccess(List<HospitalResourceOverview> result) {
                progressBarHospitals.setVisibility(View.GONE);
                displayedList.clear();
                if (result != null && !result.isEmpty()) {
                    displayedList.addAll(result);
                    rvAvailableHospitals.setVisibility(View.VISIBLE);
                    layoutEmptyHospitals.setVisibility(View.GONE);
                    if (tvFilterStatusBanner != null) {
                        tvFilterStatusBanner.setText("Showing " + displayedList.size() + " Available Hospitals");
                    }
                } else {
                    rvAvailableHospitals.setVisibility(View.GONE);
                    layoutEmptyHospitals.setVisibility(View.VISIBLE);
                    if (tvFilterStatusBanner != null) {
                        tvFilterStatusBanner.setText("No Available Hospitals");
                    }
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String error) {
                progressBarHospitals.setVisibility(View.GONE);
                Toast.makeText(AvailableHospitalsActivity.this, "Error loading hospitals: " + error, Toast.LENGTH_SHORT).show();
                if (displayedList.isEmpty()) {
                    layoutEmptyHospitals.setVisibility(View.VISIBLE);
                    rvAvailableHospitals.setVisibility(View.GONE);
                }
            }
        });
    }
}
