package com.example.organshare.activities.delivery;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.DeliveryHomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeliveryDashboardActivity extends AppCompatActivity {

    private BottomNavigationView deliveryBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        deliveryBottomNav = findViewById(R.id.deliveryBottomNav);
        loadFragment(new DeliveryHomeFragment());

        deliveryBottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_delivery_home || itemId == R.id.nav_delivery_active || itemId == R.id.nav_delivery_history) {
                loadFragment(new DeliveryHomeFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.deliveryFragmentContainer, fragment)
                .commit();
    }
}
