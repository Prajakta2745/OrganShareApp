package com.example.organshare.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.activities.delivery.DeliveryDetailsActivity;
import com.example.organshare.adapters.DeliveryAdapter;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.repositories.DeliveryRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AdminDeliveryManagementActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialButton btnTabPendingDel, btnTabActiveDel, btnTabDelayedDel, btnTabCompletedDel;
    private TextView tvEmptyAdminDel;
    private ProgressBar progressBarAdminDel;
    private RecyclerView rvAdminDeliveries;
    private DeliveryAdapter adapter;
    private final List<DeliveryModel> allDeliveries = new ArrayList<>();
    private final List<DeliveryModel> displayedDeliveries = new ArrayList<>();
    private DeliveryRepository repository;
    private String currentSelectedTab = "PENDING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_delivery_management);

        repository = new DeliveryRepository();

        initViews();
        setupNavigationDrawer();
        setupTabs();
        setupRecyclerView();
        loadDeliveries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDeliveries();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        btnTabPendingDel = findViewById(R.id.btnTabPendingDel);
        btnTabActiveDel = findViewById(R.id.btnTabActiveDel);
        btnTabDelayedDel = findViewById(R.id.btnTabDelayedDel);
        btnTabCompletedDel = findViewById(R.id.btnTabCompletedDel);
        tvEmptyAdminDel = findViewById(R.id.tvEmptyAdminDel);
        progressBarAdminDel = findViewById(R.id.progressBarAdminDel);
        rvAdminDeliveries = findViewById(R.id.rvAdminDeliveries);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupTabs() {
        btnTabPendingDel.setOnClickListener(v -> switchTab("PENDING"));
        btnTabActiveDel.setOnClickListener(v -> switchTab("ACTIVE"));
        btnTabDelayedDel.setOnClickListener(v -> switchTab("DELAYED"));
        btnTabCompletedDel.setOnClickListener(v -> switchTab("COMPLETED"));
    }

    private void switchTab(String tab) {
        currentSelectedTab = tab;
        filterAndDisplay();
    }

    private void setupRecyclerView() {
        rvAdminDeliveries.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryAdapter(this, displayedDeliveries, delivery -> {
            Intent intent = new Intent(AdminDeliveryManagementActivity.this, DeliveryDetailsActivity.class);
            intent.putExtra("DELIVERY_DATA", delivery);
            startActivity(intent);
        });
        rvAdminDeliveries.setAdapter(adapter);
    }

    private void loadDeliveries() {
        progressBarAdminDel.setVisibility(View.VISIBLE);
        repository.getAllDeliveries(new DeliveryRepository.DataCallback<List<DeliveryModel>>() {
            @Override
            public void onSuccess(List<DeliveryModel> result) {
                progressBarAdminDel.setVisibility(View.GONE);
                allDeliveries.clear();
                if (result != null) {
                    allDeliveries.addAll(result);
                }
                updateTabCounts();
                filterAndDisplay();
            }

            @Override
            public void onFailure(String error) {
                progressBarAdminDel.setVisibility(View.GONE);
                Toast.makeText(AdminDeliveryManagementActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTabCounts() {
        int pending = 0, active = 0, delayed = 0, completed = 0;
        for (DeliveryModel d : allDeliveries) {
            String st = d.getCurrentStatus() != null ? d.getCurrentStatus() : "";
            if (Constants.DEL_ASSIGNED.equalsIgnoreCase(st) || "PICKUP_PENDING".equalsIgnoreCase(st)) {
                pending++;
            } else if (Constants.DEL_DELAYED.equalsIgnoreCase(st)) {
                delayed++;
            } else if (Constants.DEL_COMPLETED.equalsIgnoreCase(st) || Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                completed++;
            } else {
                active++;
            }
        }

        btnTabPendingDel.setText("Pending (" + pending + ")");
        btnTabActiveDel.setText("Active (" + active + ")");
        btnTabDelayedDel.setText("Delayed (" + delayed + ")");
        btnTabCompletedDel.setText("Completed (" + completed + ")");
    }

    private void filterAndDisplay() {
        displayedDeliveries.clear();
        for (DeliveryModel d : allDeliveries) {
            String st = d.getCurrentStatus() != null ? d.getCurrentStatus() : "";
            if ("PENDING".equals(currentSelectedTab)) {
                if (Constants.DEL_ASSIGNED.equalsIgnoreCase(st) || "PICKUP_PENDING".equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("ACTIVE".equals(currentSelectedTab)) {
                if (!Constants.DEL_ASSIGNED.equalsIgnoreCase(st) &&
                    !"PICKUP_PENDING".equalsIgnoreCase(st) &&
                    !Constants.DEL_DELAYED.equalsIgnoreCase(st) &&
                    !Constants.DEL_COMPLETED.equalsIgnoreCase(st) &&
                    !Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("DELAYED".equals(currentSelectedTab)) {
                if (Constants.DEL_DELAYED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("COMPLETED".equals(currentSelectedTab)) {
                if (Constants.DEL_COMPLETED.equalsIgnoreCase(st) || Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            }
        }

        adapter.notifyDataSetChanged();
        if (displayedDeliveries.isEmpty()) {
            tvEmptyAdminDel.setVisibility(View.VISIBLE);
        } else {
            tvEmptyAdminDel.setVisibility(View.GONE);
        }
    }
}
