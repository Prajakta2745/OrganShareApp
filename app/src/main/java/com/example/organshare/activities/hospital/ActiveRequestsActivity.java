package com.example.organshare.activities.hospital;

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
import com.example.organshare.adapters.ActiveRequestAdapter;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ActiveRequestsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvActiveCountBadge, tvEmptyActive;
    private ProgressBar progressBarActive;
    private RecyclerView rvActiveRequests;
    private ActiveRequestAdapter adapter;
    private final List<OrganRequest> activeList = new ArrayList<>();
    private OrganBankRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_requests);

        repository = new OrganBankRepository();

        initViews();
        setupNavigationDrawer();
        setupRecyclerView();
        loadActiveRequests();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvActiveCountBadge = findViewById(R.id.tvActiveCountBadge);
        tvEmptyActive = findViewById(R.id.tvEmptyActive);
        progressBarActive = findViewById(R.id.progressBarActive);
        rvActiveRequests = findViewById(R.id.rvActiveRequests);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupRecyclerView() {
        rvActiveRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActiveRequestAdapter(this, activeList, request -> {
            Intent intent = new Intent(ActiveRequestsActivity.this, RequestDetailsActivity.class);
            intent.putExtra("REQUEST_DATA", request);
            startActivity(intent);
        });
        rvActiveRequests.setAdapter(adapter);
    }

    private void loadActiveRequests() {
        progressBarActive.setVisibility(View.VISIBLE);
        repository.getAllRequests(new OrganBankRepository.DataCallback<List<OrganRequest>>() {
            @Override
            public void onSuccess(List<OrganRequest> result) {
                progressBarActive.setVisibility(View.GONE);
                activeList.clear();
                if (result != null) {
                    for (OrganRequest req : result) {
                        String status = req.getStatus() != null ? req.getStatus() : "";
                        if (Constants.STATUS_APPROVED.equalsIgnoreCase(status) ||
                            Constants.STATUS_ACTIVE.equalsIgnoreCase(status) ||
                            Constants.STATUS_PARTIALLY_FULFILLED.equalsIgnoreCase(status) ||
                            Constants.STATUS_ALLOCATED.equalsIgnoreCase(status) ||
                            Constants.STATUS_IN_TRANSIT.equalsIgnoreCase(status)) {
                            activeList.add(req);
                        }
                    }
                }
                tvActiveCountBadge.setText(activeList.size() + " Active Requests in Pipeline");
                adapter.notifyDataSetChanged();

                if (activeList.isEmpty()) {
                    tvEmptyActive.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyActive.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                progressBarActive.setVisibility(View.GONE);
                Toast.makeText(ActiveRequestsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
