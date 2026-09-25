package com.example.organshare.activities.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.UserManagementAdapter;
import com.example.organshare.models.UserModel;
import com.example.organshare.repositories.AdminRepository;
import com.example.organshare.utils.Constants;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    public static final String EXTRA_INITIAL_TAB = "EXTRA_INITIAL_TAB";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayoutUserRoles;
    private TextView tvUserCountBadge;
    private RecyclerView rvUserManagement;
    private UserManagementAdapter adapter;
    private final List<UserModel> masterUserList = new ArrayList<>();
    private final List<UserModel> displayedUserList = new ArrayList<>();
    private AdminRepository repository;
    private int selectedTabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        repository = new AdminRepository();
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        tabLayoutUserRoles = findViewById(R.id.tabLayoutUserRoles);
        tvUserCountBadge = findViewById(R.id.tvUserCountBadge);
        rvUserManagement = findViewById(R.id.rvUserManagement);
        rvUserManagement.setLayoutManager(new LinearLayoutManager(this));

        setupNavigationDrawer();
        setupTabs();
        setupAdapter();
        loadUsers();
    }

    private void setupNavigationDrawer() {
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }
    }

    private void setupTabs() {
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("All Users"));
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("Hospitals"));
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("Organ Banks"));
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("Delivery Partners"));
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("Donors"));
        tabLayoutUserRoles.addTab(tabLayoutUserRoles.newTab().setText("Pending Approvals"));

        String initialRole = getIntent().getStringExtra(EXTRA_INITIAL_TAB);
        if (Constants.ROLE_DONOR.equalsIgnoreCase(initialRole)) {
            selectedTabPosition = 4;
        } else if (Constants.ROLE_HOSPITAL.equalsIgnoreCase(initialRole)) {
            selectedTabPosition = 1;
        } else if (Constants.ROLE_ORGAN_BANK.equalsIgnoreCase(initialRole)) {
            selectedTabPosition = 2;
        } else if (Constants.ROLE_DELIVERY.equalsIgnoreCase(initialRole)) {
            selectedTabPosition = 3;
        } else if ("PENDING".equalsIgnoreCase(initialRole)) {
            selectedTabPosition = 5;
        }

        if (selectedTabPosition > 0 && selectedTabPosition < tabLayoutUserRoles.getTabCount()) {
            TabLayout.Tab tab = tabLayoutUserRoles.getTabAt(selectedTabPosition);
            if (tab != null) tab.select();
        }

        tabLayoutUserRoles.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedTabPosition = tab.getPosition();
                applyFilter();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupAdapter() {
        adapter = new UserManagementAdapter(this, displayedUserList, new UserManagementAdapter.OnUserActionListener() {
            @Override
            public void onApprove(UserModel user) {
                repository.updateUserVerification(user.getUid(), true, new AdminRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(UserManagementActivity.this, (user.getDisplayName() != null ? user.getDisplayName() : "User") + " approved & verified!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(UserManagementActivity.this, "Approval failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onToggleSuspend(UserModel user) {
                boolean newStatus = !user.isSuspended();
                repository.updateUserSuspension(user.getUid(), newStatus, new AdminRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(UserManagementActivity.this, "User status updated!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(UserManagementActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        rvUserManagement.setAdapter(adapter);
    }

    private void loadUsers() {
        repository.getAllUsers(new AdminRepository.DataCallback<List<UserModel>>() {
            @Override
            public void onSuccess(List<UserModel> result) {
                masterUserList.clear();
                if (result != null) {
                    masterUserList.addAll(result);
                }
                applyFilter();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UserManagementActivity.this, "Error loading users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilter() {
        displayedUserList.clear();
        for (UserModel user : masterUserList) {
            String role = user.getRole() != null ? user.getRole().trim().toUpperCase() : "";

            switch (selectedTabPosition) {
                case 1: // Hospitals only
                    if (Constants.ROLE_HOSPITAL.equalsIgnoreCase(role)) displayedUserList.add(user);
                    break;
                case 2: // Organ Banks only
                    if (Constants.ROLE_ORGAN_BANK.equalsIgnoreCase(role)) displayedUserList.add(user);
                    break;
                case 3: // Delivery Partners only
                    if (Constants.ROLE_DELIVERY.equalsIgnoreCase(role)) displayedUserList.add(user);
                    break;
                case 4: // Donors only
                    if (Constants.ROLE_DONOR.equalsIgnoreCase(role)) displayedUserList.add(user);
                    break;
                case 5: // Pending Approvals only
                    if (!user.isVerified()) displayedUserList.add(user);
                    break;
                case 0: // All
                default:
                    displayedUserList.add(user);
                    break;
            }
        }
        tvUserCountBadge.setText("Showing " + displayedUserList.size() + " accounts");
        adapter.notifyDataSetChanged();
    }
}
