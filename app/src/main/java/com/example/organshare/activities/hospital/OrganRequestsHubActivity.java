package com.example.organshare.activities.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.organshare.R;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class OrganRequestsHubActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialCardView cardNavPendingRequests, cardNavActiveRequests, cardNavFulfilledRequests, cardNavCreateRequest;
    private TextView tvHubPendingCount, tvHubActiveCount, tvHubFulfilledCount;
    private OrganBankRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_requests_hub);

        repository = new OrganBankRepository();

        initViews();
        setupNavigationDrawer();
        setupListeners();
        loadCounts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCounts();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        cardNavPendingRequests = findViewById(R.id.cardNavPendingRequests);
        cardNavActiveRequests = findViewById(R.id.cardNavActiveRequests);
        cardNavFulfilledRequests = findViewById(R.id.cardNavFulfilledRequests);
        cardNavCreateRequest = findViewById(R.id.cardNavCreateRequest);

        tvHubPendingCount = findViewById(R.id.tvHubPendingCount);
        tvHubActiveCount = findViewById(R.id.tvHubActiveCount);
        tvHubFulfilledCount = findViewById(R.id.tvHubFulfilledCount);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupListeners() {
        cardNavPendingRequests.setOnClickListener(v -> startActivity(new Intent(this, PendingRequestsActivity.class)));
        cardNavActiveRequests.setOnClickListener(v -> startActivity(new Intent(this, ActiveRequestsActivity.class)));
        cardNavFulfilledRequests.setOnClickListener(v -> startActivity(new Intent(this, FulfilledRequestsActivity.class)));
        cardNavCreateRequest.setOnClickListener(v -> startActivity(new Intent(this, CreateRequestActivity.class)));
    }

    private void loadCounts() {
        repository.getAllRequests(new OrganBankRepository.DataCallback<List<OrganRequest>>() {
            @Override
            public void onSuccess(List<OrganRequest> result) {
                int pending = 0;
                int active = 0;
                int fulfilled = 0;

                if (result != null) {
                    for (OrganRequest r : result) {
                        String st = r.getStatus() != null ? r.getStatus() : "";
                        if (Constants.STATUS_PENDING.equalsIgnoreCase(st) || Constants.STATUS_UNDER_REVIEW.equalsIgnoreCase(st)) {
                            pending++;
                        } else if (Constants.STATUS_APPROVED.equalsIgnoreCase(st) ||
                                   Constants.STATUS_ACTIVE.equalsIgnoreCase(st) ||
                                   Constants.STATUS_PARTIALLY_FULFILLED.equalsIgnoreCase(st) ||
                                   Constants.STATUS_ALLOCATED.equalsIgnoreCase(st) ||
                                   Constants.STATUS_IN_TRANSIT.equalsIgnoreCase(st)) {
                            active++;
                        } else if (Constants.STATUS_FULFILLED.equalsIgnoreCase(st) || Constants.STATUS_COMPLETED.equalsIgnoreCase(st) || Constants.STATUS_DELIVERED.equalsIgnoreCase(st)) {
                            fulfilled++;
                        }
                    }
                }

                tvHubPendingCount.setText(pending + " Requests Awaiting Review");
                tvHubActiveCount.setText(active + " Requests In Active Processing");
                tvHubFulfilledCount.setText(fulfilled + " Requests Successfully Fulfilled");
            }

            @Override
            public void onFailure(String error) {}
        });
    }
}
