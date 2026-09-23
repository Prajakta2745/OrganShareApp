package com.example.organshare.activities.donor;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.common.NotificationsActivity;
import com.example.organshare.fragments.DonorHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DonorDashboardActivity extends AppCompatActivity {

    private BottomNavigationView donorBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_dashboard);

        donorBottomNav = findViewById(R.id.donorBottomNav);
        loadFragment(new DonorHomeFragment());

        donorBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_donor_home) {
                loadFragment(new DonorHomeFragment());
                return true;
            } else if (itemId == R.id.nav_donor_profile) {
                Intent intent = new Intent(this, DonorProfileActivity.class);
                startActivity(intent);
                return false;
            } else if (itemId == R.id.nav_donor_info) {
                Intent intent = new Intent(this, DonationInfoActivity.class);
                startActivity(intent);
                return false;
            } else if (itemId == R.id.nav_donor_notifications) {
                Intent intent = new Intent(this, NotificationsActivity.class);
                startActivity(intent);
                return false;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.donorFragmentContainer, fragment)
                .commit();
    }
}
