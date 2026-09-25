package com.example.organshare.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.AdminStatsFragment;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView adminBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }

        adminBottomNav = findViewById(R.id.adminBottomNav);
        loadFragment(new AdminStatsFragment());

        adminBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_stats) {
                loadFragment(new AdminStatsFragment());
                return true;
            } else if (itemId == R.id.nav_admin_users) {
                startActivity(new Intent(this, UserManagementActivity.class));
                return false;
            } else if (itemId == R.id.nav_admin_audit) {
                startActivity(new Intent(this, AuditLogsActivity.class));
                return false;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.adminFragmentContainer, fragment)
                .commit();
    }
}
