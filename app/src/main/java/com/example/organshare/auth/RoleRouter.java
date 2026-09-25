package com.example.organshare.auth;

import android.content.Context;
import android.content.Intent;
import com.example.organshare.activities.admin.AdminDashboardActivity;
import com.example.organshare.activities.delivery.DeliveryDashboardActivity;
import com.example.organshare.activities.donor.DonorDashboardActivity;
import com.example.organshare.activities.hospital.HospitalDashboardActivity;
import com.example.organshare.activities.organbank.OrganBankDashboardActivity;
import com.example.organshare.utils.Constants;

public class RoleRouter {

    public static void navigateToDashboard(Context context, String role) {
        if (role == null) return;
        navigateToRoleSpecificDashboard(context, role);
    }

    public static void navigateToRoleSpecificDashboard(Context context, String role) {
        if (role == null) return;
        Intent intent;
        switch (role.toUpperCase()) {
            case Constants.ROLE_DONOR:
                intent = new Intent(context, DonorDashboardActivity.class);
                break;
            case Constants.ROLE_HOSPITAL:
                intent = new Intent(context, HospitalDashboardActivity.class);
                break;
            case Constants.ROLE_ORGAN_BANK:
                intent = new Intent(context, OrganBankDashboardActivity.class);
                break;
            case Constants.ROLE_DELIVERY:
                intent = new Intent(context, DeliveryDashboardActivity.class);
                break;
            case Constants.ROLE_ADMIN:
                intent = new Intent(context, AdminDashboardActivity.class);
                break;
            default:
                intent = new Intent(context, DonorDashboardActivity.class);
                break;
        }
        context.startActivity(intent);
    }
}
