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
import com.example.organshare.adapters.FulfilledRequestAdapter;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class FulfilledRequestsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvFulfilledCountBadge, tvEmptyFulfilled;
    private ProgressBar progressBarFulfilled;
    private RecyclerView rvFulfilledRequests;
    private FulfilledRequestAdapter adapter;
    private final List<OrganRequest> fulfilledList = new ArrayList<>();
    private OrganBankRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fulfilled_requests);

        repository = new OrganBankRepository();

        initViews();
        setupNavigationDrawer();
        setupRecyclerView();
        loadFulfilledRequests();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvFulfilledCountBadge = findViewById(R.id.tvFulfilledCountBadge);
        tvEmptyFulfilled = findViewById(R.id.tvEmptyFulfilled);
        progressBarFulfilled = findViewById(R.id.progressBarFulfilled);
        rvFulfilledRequests = findViewById(R.id.rvFulfilledRequests);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupRecyclerView() {
        rvFulfilledRequests.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FulfilledRequestAdapter(this, fulfilledList, request -> {
            Intent intent = new Intent(FulfilledRequestsActivity.this, RequestDetailsActivity.class);
            intent.putExtra("REQUEST_DATA", request);
            startActivity(intent);
        });
        rvFulfilledRequests.setAdapter(adapter);
    }

    private void loadFulfilledRequests() {
        progressBarFulfilled.setVisibility(View.VISIBLE);
        repository.getAllRequests(new OrganBankRepository.DataCallback<List<OrganRequest>>() {
            @Override
            public void onSuccess(List<OrganRequest> result) {
                progressBarFulfilled.setVisibility(View.GONE);
                fulfilledList.clear();
                if (result != null) {
                    for (OrganRequest req : result) {
                        String status = req.getStatus() != null ? req.getStatus() : "";
                        if (Constants.STATUS_FULFILLED.equalsIgnoreCase(status) ||
                            Constants.STATUS_COMPLETED.equalsIgnoreCase(status) ||
                            Constants.STATUS_DELIVERED.equalsIgnoreCase(status)) {
                            fulfilledList.add(req);
                        }
                    }
                }
                tvFulfilledCountBadge.setText(fulfilledList.size() + " Requests Successfully Completed");
                adapter.notifyDataSetChanged();

                if (fulfilledList.isEmpty()) {
                    tvEmptyFulfilled.setVisibility(View.VISIBLE);
                } else {
                    tvEmptyFulfilled.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String error) {
                progressBarFulfilled.setVisibility(View.GONE);
                Toast.makeText(FulfilledRequestsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
