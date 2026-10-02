package com.example.organshare.activities.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.organshare.R;
import com.example.organshare.activities.admin.AdminDashboardActivity;
import com.example.organshare.activities.delivery.DeliveryDashboardActivity;
import com.example.organshare.activities.donor.DigitalDonorCardActivity;
import com.example.organshare.activities.donor.DonationInfoActivity;
import com.example.organshare.activities.donor.DonorDashboardActivity;
import com.example.organshare.activities.hospital.CreateRequestActivity;
import com.example.organshare.activities.hospital.HospitalDashboardActivity;
import com.example.organshare.activities.hospital.SearchDonorsActivity;
import com.example.organshare.activities.hospital.TrackDeliveryMapActivity;
import com.example.organshare.activities.organbank.AddEditInventoryActivity;
import com.example.organshare.activities.organbank.OrganBankDashboardActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

public class CommonHomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView tvHomeWelcomeName, tvHomeUserRoleBadge;
    private MaterialCardView cardFindOrgan, cardRequestOrgan, cardAvailableOrgans, cardMyRequests, cardTrackDelivery, cardDigitalDonorCard;
    private MaterialButton btnExploreEducation;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_home);

        authManager = AuthManager.getInstance(this);

        initViews();
        setupNavigationDrawer();
        setupRoleSpecificCards();
        setupCardClicks();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tvHomeWelcomeName = findViewById(R.id.tvHomeWelcomeName);
        tvHomeUserRoleBadge = findViewById(R.id.tvHomeUserRoleBadge);

        cardFindOrgan = findViewById(R.id.cardFindOrgan);
        cardRequestOrgan = findViewById(R.id.cardRequestOrgan);
        cardAvailableOrgans = findViewById(R.id.cardAvailableOrgans);
        cardMyRequests = findViewById(R.id.cardMyRequests);
        cardTrackDelivery = findViewById(R.id.cardTrackDelivery);
        cardDigitalDonorCard = findViewById(R.id.cardDigitalDonorCard);
        btnExploreEducation = findViewById(R.id.btnExploreEducation);
    }

    private void setupNavigationDrawer() {
        NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
    }

    private void setupRoleSpecificCards() {
        String role = authManager.getSessionManager().getUserRole();
        String name = authManager.getSessionManager().getUserName();

        if (name != null && !name.isEmpty()) {
            tvHomeWelcomeName.setText("Welcome, " + name);
        }
        tvHomeUserRoleBadge.setText("Role: " + (role != null ? role.toUpperCase() : "DONOR"));

        // Configure card visibility based strictly on user authorization
        if (Constants.ROLE_DONOR.equalsIgnoreCase(role)) {
            cardFindOrgan.setVisibility(View.GONE);
            cardRequestOrgan.setVisibility(View.GONE);
            cardAvailableOrgans.setVisibility(View.GONE);
            cardTrackDelivery.setVisibility(View.GONE);
            cardMyRequests.setVisibility(View.VISIBLE); // Donation Pledge Details
            cardDigitalDonorCard.setVisibility(View.VISIBLE);
        } else if (Constants.ROLE_HOSPITAL.equalsIgnoreCase(role)) {
            cardFindOrgan.setVisibility(View.VISIBLE);
            cardRequestOrgan.setVisibility(View.VISIBLE);
            cardAvailableOrgans.setVisibility(View.VISIBLE);
            cardMyRequests.setVisibility(View.VISIBLE);
            cardTrackDelivery.setVisibility(View.VISIBLE);
            cardDigitalDonorCard.setVisibility(View.GONE);
        } else if (Constants.ROLE_ORGAN_BANK.equalsIgnoreCase(role)) {
            cardFindOrgan.setVisibility(View.VISIBLE);
            cardRequestOrgan.setVisibility(View.GONE);
            cardAvailableOrgans.setVisibility(View.VISIBLE);
            cardMyRequests.setVisibility(View.VISIBLE);
            cardTrackDelivery.setVisibility(View.VISIBLE);
            cardDigitalDonorCard.setVisibility(View.GONE);
        } else if (Constants.ROLE_DELIVERY.equalsIgnoreCase(role)) {
            cardFindOrgan.setVisibility(View.GONE);
            cardRequestOrgan.setVisibility(View.GONE);
            cardAvailableOrgans.setVisibility(View.GONE);
            cardMyRequests.setVisibility(View.GONE);
            cardTrackDelivery.setVisibility(View.VISIBLE);
            cardDigitalDonorCard.setVisibility(View.GONE);
        } else if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
            cardFindOrgan.setVisibility(View.VISIBLE);
            cardRequestOrgan.setVisibility(View.VISIBLE);
            cardAvailableOrgans.setVisibility(View.VISIBLE);
            cardMyRequests.setVisibility(View.VISIBLE);
            cardTrackDelivery.setVisibility(View.VISIBLE);
            cardDigitalDonorCard.setVisibility(View.GONE);
        }
    }

    private void setupCardClicks() {
        final String role = authManager.getSessionManager().getUserRole();

        cardFindOrgan.setOnClickListener(v -> startActivity(new Intent(this, SearchDonorsActivity.class)));

        cardRequestOrgan.setOnClickListener(v -> startActivity(new Intent(this, CreateRequestActivity.class)));

        cardAvailableOrgans.setOnClickListener(v -> {
            if (Constants.ROLE_DONOR.equalsIgnoreCase(role)) {
                startActivity(new Intent(this, com.example.organshare.activities.donor.AvailableHospitalsActivity.class));
            } else {
                startActivity(new Intent(this, AvailableOrgansActivity.class));
            }
        });

        cardMyRequests.setOnClickListener(v -> {
            if (Constants.ROLE_DONOR.equalsIgnoreCase(role)) {
                startActivity(new Intent(this, DonorDashboardActivity.class));
            } else {
                startActivity(new Intent(this, com.example.organshare.activities.hospital.OrganRequestsHubActivity.class));
            }
        });

        cardTrackDelivery.setOnClickListener(v -> {
            if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
                startActivity(new Intent(this, com.example.organshare.activities.admin.AdminDeliveryManagementActivity.class));
            } else if (Constants.ROLE_DELIVERY.equalsIgnoreCase(role)) {
                startActivity(new Intent(this, DeliveryDashboardActivity.class));
            } else {
                startActivity(new Intent(this, TrackDeliveryMapActivity.class));
            }
        });

        cardDigitalDonorCard.setOnClickListener(v -> startActivity(new Intent(this, DigitalDonorCardActivity.class)));

        btnExploreEducation.setOnClickListener(v -> startActivity(new Intent(this, DonationInfoActivity.class)));
    }
}
