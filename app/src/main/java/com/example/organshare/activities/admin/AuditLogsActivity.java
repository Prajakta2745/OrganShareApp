package com.example.organshare.activities.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.AuditLogAdapter;
import com.example.organshare.models.AuditLog;
import com.example.organshare.repositories.AdminRepository;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class AuditLogsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView rvAuditLogs;
    private AuditLogAdapter adapter;
    private final List<AuditLog> logList = new ArrayList<>();
    private AdminRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_logs);

        repository = new AdminRepository();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        rvAuditLogs = findViewById(R.id.rvAuditLogs);
        rvAuditLogs.setLayoutManager(new LinearLayoutManager(this));

        setupNavigationDrawer();

        adapter = new AuditLogAdapter(this, logList);
        rvAuditLogs.setAdapter(adapter);

        loadAuditLogs();
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void loadAuditLogs() {
        repository.getAuditLogs(new AdminRepository.DataCallback<List<AuditLog>>() {
            @Override
            public void onSuccess(List<AuditLog> result) {
                logList.clear();
                if (result != null && !result.isEmpty()) {
                    logList.addAll(result);
                } else {
                    // Demo initial audit entry
                    logList.add(new AuditLog("LOG-INIT", "SYS-001", "System Core", "ADMIN",
                            "SYSTEM_INITIALIZED", "SYSTEM", "SYS-CORE", "Transplantation network nodes initialized."));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AuditLogsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
