package com.example.organshare.activities.hospital;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.HospitalHomeFragment;
import com.example.organshare.fragments.HospitalOrdersFragment;
import com.example.organshare.fragments.HospitalRequestsFragment;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HospitalDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView hospitalBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }

        hospitalBottomNav = findViewById(R.id.hospitalBottomNav);
        loadFragment(new HospitalHomeFragment());

        hospitalBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_hospital_home) {
                loadFragment(new HospitalHomeFragment());
                return true;
            } else if (itemId == R.id.nav_hospital_search) {
                loadFragment(new HospitalHomeFragment());
                startActivity(new Intent(this, SearchDonorsActivity.class));
                return false;
            } else if (itemId == R.id.nav_hospital_requests) {
                loadFragment(new HospitalRequestsFragment());
                return true;
            } else if (itemId == R.id.nav_hospital_orders) {
                loadFragment(new HospitalOrdersFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.hospitalFragmentContainer, fragment)
                .commit();
    }
}
