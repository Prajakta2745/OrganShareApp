package com.example.organshare.activities.organbank;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.BankHomeFragment;
import com.example.organshare.fragments.BankInventoryFragment;
import com.example.organshare.fragments.BankOrdersFragment;
import com.example.organshare.fragments.BankRequestsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OrganBankDashboardActivity extends AppCompatActivity {

    private BottomNavigationView bankBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_bank_dashboard);

        bankBottomNav = findViewById(R.id.bankBottomNav);
        loadFragment(new BankHomeFragment());

        bankBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_bank_home) {
                loadFragment(new BankHomeFragment());
                return true;
            } else if (itemId == R.id.nav_bank_requests) {
                loadFragment(new BankRequestsFragment());
                return true;
            } else if (itemId == R.id.nav_bank_inventory) {
                loadFragment(new BankInventoryFragment());
                return true;
            } else if (itemId == R.id.nav_bank_orders) {
                loadFragment(new BankOrdersFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.bankFragmentContainer, fragment)
                .commit();
    }
}
