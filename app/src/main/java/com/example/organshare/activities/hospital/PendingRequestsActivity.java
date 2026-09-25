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
import com.example.organshare.activities.organbank.AllocateOrganActivity;
import com.example.organshare.adapters.OrganRequestAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class PendingRequestsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvPendingCountBadge, tvEmptyPending;
    private ProgressBar progressBarPending;
    private RecyclerView rvPendingRequests;

    private OrganRequestAdapter adapter;
    private final List<OrganRequest> pendingList = new ArrayList<>();

    private OrganBankRepository repository;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        repository = new OrganBankRepository();

        userRole = AuthManager.getInstance(this)
                .getSessionManager()
                .getUserRole();

        initViews();
        setupNavigationDrawer();
        setupRecyclerView();
        loadPendingRequests();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        tvPendingCountBadge = findViewById(R.id.tvPendingCountBadge);
        tvEmptyPending = findViewById(R.id.tvEmptyPending);
        progressBarPending = findViewById(R.id.progressBarPending);
        rvPendingRequests = findViewById(R.id.rvPendingRequests);
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(
                    this,
                    drawerLayout,
                    navigationView
            );
        }
    }

    private void setupRecyclerView() {

        boolean canManage =
                Constants.ROLE_ADMIN.equalsIgnoreCase(userRole)
                        || Constants.ROLE_ORGAN_BANK.equalsIgnoreCase(userRole);

        rvPendingRequests.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new OrganRequestAdapter(
                this,
                pendingList,
                canManage,
                new OrganRequestAdapter.OnRequestClickListener() {

                    @Override
                    public void onViewDetails(OrganRequest request) {

                        Intent intent = new Intent(
                                PendingRequestsActivity.this,
                                RequestDetailsActivity.class
                        );

                        intent.putExtra("REQUEST_DATA", request);

                        startActivity(intent);
                    }

                    @Override
                    public void onAllocate(OrganRequest request) {

                        Intent intent = new Intent(
                                PendingRequestsActivity.this,
                                AllocateOrganActivity.class
                        );

                        intent.putExtra(
                                "REQUEST_ID",
                                request.getRequestId()
                        );

                        intent.putExtra(
                                "ORGAN_TYPE",
                                request.getOrganRequired()
                        );

                        intent.putExtra(
                                "BLOOD_GROUP",
                                request.getBloodGroup()
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void onReject(OrganRequest request) {

                        updateRequestStatus(
                                request,
                                Constants.STATUS_REJECTED
                        );
                    }
                }
        );

        rvPendingRequests.setAdapter(adapter);
    }

    /**
     * Updates the request status in Firestore.
     */
    private void updateRequestStatus(
            OrganRequest request,
            String newStatus
    ) {

        String userId = AuthManager.getInstance(this)
                .getSessionManager()
                .getUserId();

        String userName = AuthManager.getInstance(this)
                .getSessionManager()
                .getUserName();

        repository.updateRequestStatus(
                request.getRequestId(),
                newStatus,
                userId,
                userName,
                new OrganBankRepository.DataCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {

                        Toast.makeText(
                                PendingRequestsActivity.this,
                                "Request status updated successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadPendingRequests();
                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(
                                PendingRequestsActivity.this,
                                "Update failed: " + error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    /**
     * Loads pending and under-review requests.
     */
    private void loadPendingRequests() {

        progressBarPending.setVisibility(View.VISIBLE);

        repository.getAllRequests(
                new OrganBankRepository.DataCallback<List<OrganRequest>>() {

                    @Override
                    public void onSuccess(List<OrganRequest> result) {

                        progressBarPending.setVisibility(View.GONE);

                        pendingList.clear();

                        if (result != null) {

                            for (OrganRequest req : result) {

                                String status = req.getStatus();

                                if (Constants.STATUS_PENDING.equalsIgnoreCase(status)
                                        || Constants.STATUS_UNDER_REVIEW.equalsIgnoreCase(status)) {

                                    pendingList.add(req);
                                }
                            }
                        }

                        tvPendingCountBadge.setText(
                                pendingList.size()
                                        + " Requests Awaiting Processing"
                        );

                        adapter.notifyDataSetChanged();

                        if (pendingList.isEmpty()) {

                            tvEmptyPending.setVisibility(View.VISIBLE);

                        } else {

                            tvEmptyPending.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(String error) {

                        progressBarPending.setVisibility(View.GONE);

                        Toast.makeText(
                                PendingRequestsActivity.this,
                                "Error: " + error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}