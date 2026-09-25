package com.example.organshare.activities.delivery;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.fragments.DeliveryHomeFragment;
import com.example.organshare.utils.NavigationDrawerHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class DeliveryDashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView deliveryBottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_dashboard);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        if (drawerLayout != null && navigationView != null) {
            NavigationDrawerHelper.setupDrawer(this, drawerLayout, navigationView);
        }

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
