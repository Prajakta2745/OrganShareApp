package com.example.organshare.activities.admin;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.AdminStatsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    private BottomNavigationView adminBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminBottomNav = findViewById(R.id.adminBottomNav);
        loadFragment(new AdminStatsFragment());

        adminBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_stats) {
                loadFragment(new AdminStatsFragment());
                return true;
            } else if (itemId == R.id.nav_admin_users) {
                startActivity(new android.content.Intent(this, UserManagementActivity.class));
                return false;
            } else if (itemId == R.id.nav_admin_audit) {
                startActivity(new android.content.Intent(this, AuditLogsActivity.class));
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
