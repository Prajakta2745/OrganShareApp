package com.example.organshare.activities.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.organshare.R;
import com.example.organshare.repositories.AdminRepository;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.Map;

public class AdminReportsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvReportTotalReq, tvReportPendingReq, tvReportActiveReq, tvReportFulfilledReq;
    private TextView tvReportAvailOrgans, tvReportPendingDel, tvReportActiveDel, tvReportDelayedDel, tvReportCompletedDel;
    private AdminRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);

        repository = new AdminRepository();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        setupNavigationDrawer();
        initViews();
        loadReportsData();
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void initViews() {
        tvReportTotalReq = findViewById(R.id.tvReportTotalReq);
        tvReportPendingReq = findViewById(R.id.tvReportPendingReq);
        tvReportActiveReq = findViewById(R.id.tvReportActiveReq);
        tvReportFulfilledReq = findViewById(R.id.tvReportFulfilledReq);

        tvReportAvailOrgans = findViewById(R.id.tvReportAvailOrgans);
        tvReportPendingDel = findViewById(R.id.tvReportPendingDel);
        tvReportActiveDel = findViewById(R.id.tvReportActiveDel);
        tvReportDelayedDel = findViewById(R.id.tvReportDelayedDel);
        tvReportCompletedDel = findViewById(R.id.tvReportCompletedDel);
    }

    private void loadReportsData() {
        repository.getDashboardMetrics(new AdminRepository.DataCallback<Map<String, Integer>>() {
            @Override
            public void onSuccess(Map<String, Integer> metrics) {
                tvReportTotalReq.setText(String.valueOf(metrics.getOrDefault("totalRequests", 0)));
                tvReportPendingReq.setText(String.valueOf(metrics.getOrDefault("pendingRequests", 0)));
                tvReportActiveReq.setText(String.valueOf(metrics.getOrDefault("activeRequests", 0)));
                tvReportFulfilledReq.setText(String.valueOf(metrics.getOrDefault("fulfilledRequests", 0)));

                tvReportAvailOrgans.setText(String.valueOf(metrics.getOrDefault("availableOrgans", 0)));
                tvReportPendingDel.setText(String.valueOf(metrics.getOrDefault("pendingDeliveries", 0)));
                tvReportActiveDel.setText(String.valueOf(metrics.getOrDefault("activeDeliveries", 0)));
                tvReportDelayedDel.setText(String.valueOf(metrics.getOrDefault("delayedDeliveries", 0)));
                tvReportCompletedDel.setText(String.valueOf(metrics.getOrDefault("completedDeliveries", 0)));
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AdminReportsActivity.this, "Error loading report metrics: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
