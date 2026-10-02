package com.example.organshare.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.organshare.R;
import com.example.organshare.activities.admin.AdminDashboardActivity;
import com.example.organshare.activities.admin.AdminDeliveryManagementActivity;
import com.example.organshare.activities.admin.AdminReportsActivity;
import com.example.organshare.activities.admin.AuditLogsActivity;
import com.example.organshare.activities.admin.UserManagementActivity;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.common.AvailableOrgansActivity;
import com.example.organshare.activities.common.CommonHomeActivity;
import com.example.organshare.activities.common.NotificationsActivity;
import com.example.organshare.activities.common.SettingsActivity;
import com.example.organshare.activities.delivery.DeliveryDashboardActivity;
import com.example.organshare.activities.donor.BloodDonorRegistrationActivity;
import com.example.organshare.activities.donor.DigitalDonorCardActivity;
import com.example.organshare.activities.donor.DonationInfoActivity;
import com.example.organshare.activities.donor.DonorDashboardActivity;
import com.example.organshare.activities.donor.DonorProfileActivity;
import com.example.organshare.activities.donor.EligibilityScreenerActivity;
import com.example.organshare.activities.donor.FamilyDonationActivity;
import com.example.organshare.activities.donor.OrganPledgeActivity;
import com.example.organshare.activities.donor.AvailableHospitalsActivity;
import com.example.organshare.activities.emergency.EmergencyCoordinatorDashboardActivity;
import com.example.organshare.activities.hospital.ActiveRequestsActivity;
import com.example.organshare.activities.hospital.CreateBloodRequestActivity;
import com.example.organshare.activities.hospital.CreateRequestActivity;
import com.example.organshare.activities.hospital.FulfilledRequestsActivity;
import com.example.organshare.activities.hospital.HospitalDashboardActivity;
import com.example.organshare.activities.hospital.HospitalResourcesManagementActivity;
import com.example.organshare.activities.hospital.OrganRequestsHubActivity;
import com.example.organshare.activities.hospital.PendingRequestsActivity;
import com.example.organshare.activities.hospital.SearchDonorsActivity;
import com.example.organshare.activities.hospital.TrackDeliveryMapActivity;
import com.example.organshare.activities.organbank.AllocateOrganActivity;
import com.example.organshare.activities.organbank.AssignDeliveryActivity;
import com.example.organshare.activities.organbank.OrganBankDashboardActivity;
import com.example.organshare.auth.AuthManager;
import com.google.android.material.navigation.NavigationView;

public class NavigationDrawerHelper {

    public static void setupDrawer(final Activity activity, final DrawerLayout drawerLayout, NavigationView navigationView) {
        if (activity == null || drawerLayout == null || navigationView == null) return;

        AuthManager auth = AuthManager.getInstance(activity);
        String role = auth.getSessionManager().getUserRole();
        String name = auth.getSessionManager().getUserName();
        String email = auth.getSessionManager().getUserEmail();

        // 1. Setup Header Info
        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            TextView tvDrawerUserName = headerView.findViewById(R.id.tvDrawerUserName);
            TextView tvDrawerUserEmail = headerView.findViewById(R.id.tvDrawerUserEmail);
            TextView tvDrawerRoleBadge = headerView.findViewById(R.id.tvDrawerRoleBadge);

            if (tvDrawerUserName != null) tvDrawerUserName.setText(name != null && !name.isEmpty() ? name : "OrganShare User");
            if (tvDrawerUserEmail != null) tvDrawerUserEmail.setText(email != null && !email.isEmpty() ? email : "user@organshare.net");
            if (tvDrawerRoleBadge != null) tvDrawerRoleBadge.setText(role != null ? role.toUpperCase() : "DONOR");
        }

        // 2. Inflate Role-Specific Menu
        navigationView.getMenu().clear();
        if (Constants.ROLE_HOSPITAL.equalsIgnoreCase(role)) {
            navigationView.inflateMenu(R.menu.menu_drawer_hospital);
        } else if (Constants.ROLE_ORGAN_BANK.equalsIgnoreCase(role)) {
            navigationView.inflateMenu(R.menu.menu_drawer_organ_bank);
        } else if (Constants.ROLE_DELIVERY.equalsIgnoreCase(role)) {
            navigationView.inflateMenu(R.menu.menu_drawer_delivery);
        } else if (Constants.ROLE_ADMIN.equalsIgnoreCase(role)) {
            navigationView.inflateMenu(R.menu.menu_drawer_admin);
        } else {
            navigationView.inflateMenu(R.menu.menu_drawer_donor);
        }

        // 3. Setup Toolbar Hamburger & Home click
        View btnHamburger = activity.findViewById(R.id.btnHamburgerMenu);
        if (btnHamburger != null) {
            btnHamburger.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        View layoutBrand = activity.findViewById(R.id.layoutBrandHomeClick);
        if (layoutBrand != null) {
            layoutBrand.setOnClickListener(v -> {
                if (!(activity instanceof CommonHomeActivity)) {
                    Intent intent = new Intent(activity, CommonHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                }
            });
        }

        View btnNotif = activity.findViewById(R.id.btnToolbarNotification);
        if (btnNotif != null) {
            btnNotif.setOnClickListener(v -> activity.startActivity(new Intent(activity, NotificationsActivity.class)));
        }

        // 4. Handle Role-Specific Menu Item Selections
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();

            // Donor Items
            if (id == R.id.drawer_donor_dashboard){
                if (!(activity instanceof DonorDashboardActivity)) {
                    activity.startActivity(new Intent(activity, DonorDashboardActivity.class));
                }
                return true;
            } else if (id == R.id.drawer_donor_profile) {
                activity.startActivity(new Intent(activity, DonorProfileActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_pledge) {
                activity.startActivity(new Intent(activity, OrganPledgeActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_blood) {
                activity.startActivity(new Intent(activity, BloodDonorRegistrationActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_card) {
                activity.startActivity(new Intent(activity, DigitalDonorCardActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_history) {
                activity.startActivity(new Intent(activity, DonationInfoActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_family) {
                activity.startActivity(new Intent(activity, FamilyDonationActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_eligibility) {
                activity.startActivity(new Intent(activity, EligibilityScreenerActivity.class));
                return true;
            } else if (id == R.id.drawer_donor_available_hospitals) {
                activity.startActivity(new Intent(activity, AvailableHospitalsActivity.class));
                return true;
            }

            // Hospital Items
            else if (id == R.id.drawer_hospital_dashboard) {
                if (!(activity instanceof HospitalDashboardActivity)) {
                    activity.startActivity(new Intent(activity, HospitalDashboardActivity.class));
                }
                return true;
            } else if (id == R.id.drawer_hospital_profile) {
                activity.startActivity(new Intent(activity, DonorProfileActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_request_organ){
                activity.startActivity(new Intent(activity, CreateRequestActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_request_blood) {
                activity.startActivity(new Intent(activity, CreateBloodRequestActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_pending_requests) {
                activity.startActivity(new Intent(activity, PendingRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_active_requests) {
                activity.startActivity(new Intent(activity, ActiveRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_fulfilled_requests) {
                activity.startActivity(new Intent(activity, FulfilledRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_orders) {
                activity.startActivity(new Intent(activity, OrganRequestsHubActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_verification) {
                activity.startActivity(new Intent(activity, SearchDonorsActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_schedule_delivery) {
                activity.startActivity(new Intent(activity, TrackDeliveryMapActivity.class));
                return true;
            } else if (id == R.id.drawer_hospital_manage_resources) {
                activity.startActivity(new Intent(activity, HospitalResourcesManagementActivity.class));
                return true;
            }

            // Organ Bank Items
            else if (id == R.id.drawer_bank_dashboard) {
                if (!(activity instanceof OrganBankDashboardActivity)) {
                    activity.startActivity(new Intent(activity, OrganBankDashboardActivity.class));
                }
                return true;
            } else if (id == R.id.drawer_bank_profile) {
                activity.startActivity(new Intent(activity, DonorProfileActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_inventory) {
                activity.startActivity(new Intent(activity, AvailableOrgansActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_pending_requests) {
                activity.startActivity(new Intent(activity, PendingRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_active_requests) {
                activity.startActivity(new Intent(activity, ActiveRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_fulfilled_requests) {
                activity.startActivity(new Intent(activity, FulfilledRequestsActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_allocate_stock) {
                activity.startActivity(new Intent(activity, AllocateOrganActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_dispatch) {
                activity.startActivity(new Intent(activity, AssignDeliveryActivity.class));
                return true;
            } else if (id == R.id.drawer_bank_tracking) {
                activity.startActivity(new Intent(activity, TrackDeliveryMapActivity.class));
                return true;
            }

            // Admin Items
            else if (id == R.id.drawer_admin_dashboard) {
                if (!(activity instanceof AdminDashboardActivity)) {
                    activity.startActivity(new Intent(activity, AdminDashboardActivity.class));
                }
                return true;
            } else if (id == R.id.drawer_admin_user_approvals) {
                activity.startActivity(new Intent(activity, UserManagementActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_inventory) {
                activity.startActivity(new Intent(activity, AvailableOrgansActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_requests) {
                activity.startActivity(new Intent(activity, OrganRequestsHubActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_delivery) {
                activity.startActivity(new Intent(activity, AdminDeliveryManagementActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_reports) {
                activity.startActivity(new Intent(activity, AdminReportsActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_audit) {
                activity.startActivity(new Intent(activity, AuditLogsActivity.class));
                return true;
            } else if (id == R.id.drawer_admin_emergency_coordinator) {
                activity.startActivity(new Intent(activity, EmergencyCoordinatorDashboardActivity.class));
                return true;
            }

            // Delivery Items
            else if (id == R.id.drawer_delivery_dashboard || id == R.id.drawer_delivery_active || id == R.id.drawer_delivery_history) {
                if (!(activity instanceof DeliveryDashboardActivity)) {
                    activity.startActivity(new Intent(activity, DeliveryDashboardActivity.class));
                }
                return true;
            } else if (id == R.id.drawer_delivery_route_map) {
                activity.startActivity(new Intent(activity, TrackDeliveryMapActivity.class));
                return true;
            }

            // Common Notifications & Preferences
            else if (id == R.id.drawer_notifications) {
                activity.startActivity(new Intent(activity, NotificationsActivity.class));
                return true;
            } else if (id == R.id.drawer_settings) {
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                return true;
            } else if (id == R.id.drawer_logout) {
                showLogoutDialog(activity);
                return true;
            }

            return false;
        });
    }

    public static void showLogoutDialog(final Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to logout from OrganShare?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    AuthManager.getInstance(activity).logout(activity);
                    Intent intent = new Intent(activity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
