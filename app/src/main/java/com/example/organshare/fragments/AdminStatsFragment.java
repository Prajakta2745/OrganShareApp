package com.example.organshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.organshare.utils.Constants;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.admin.AdminDeliveryManagementActivity;
import com.example.organshare.activities.admin.AdminReportsActivity;
import com.example.organshare.activities.admin.AuditLogsActivity;
import com.example.organshare.activities.admin.UserManagementActivity;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.common.AvailableOrgansActivity;
import com.example.organshare.activities.hospital.ActiveRequestsActivity;
import com.example.organshare.activities.hospital.FulfilledRequestsActivity;
import com.example.organshare.activities.hospital.PendingRequestsActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.repositories.AdminRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Map;

public class AdminStatsFragment extends Fragment {

    private TextView tvAdminDonorsCount, tvAdminHospitalsCount, tvAdminAvailOrgansCount;
    private TextView tvAdminPendingReqCount, tvAdminActiveReqCount, tvAdminFulfilledReqCount;
    private TextView tvAdminPendingDelCount, tvAdminActiveDelCount, tvAdminDelayedDelCount, tvAdminCompletedDelCount;

    private MaterialCardView cardStatDonors, cardStatHospitals, cardStatAvailOrgans;
    private MaterialCardView cardStatPendingReq, cardStatActiveReq, cardStatFulfilledReq;
    private MaterialCardView cardStatPendingDel, cardStatActiveDel, cardStatDelayedDel, cardStatCompletedDel;

    private MaterialButton btnAdminManageUsers, btnAdminReports, btnAdminViewAuditLogs;
    private ImageButton btnLogoutAdmin;
    private AdminRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_stats, container, false);

        repository = new AdminRepository();

        initViews(view);
        setupCardNavigation();
        loadDynamicMetrics();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDynamicMetrics();
    }

    private void initViews(View view) {
        tvAdminDonorsCount = view.findViewById(R.id.tvAdminDonorsCount);
        tvAdminHospitalsCount = view.findViewById(R.id.tvAdminHospitalsCount);
        tvAdminAvailOrgansCount = view.findViewById(R.id.tvAdminAvailOrgansCount);
        tvAdminPendingReqCount = view.findViewById(R.id.tvAdminPendingReqCount);
        tvAdminActiveReqCount = view.findViewById(R.id.tvAdminActiveReqCount);
        tvAdminFulfilledReqCount = view.findViewById(R.id.tvAdminFulfilledReqCount);
        tvAdminPendingDelCount = view.findViewById(R.id.tvAdminPendingDelCount);
        tvAdminActiveDelCount = view.findViewById(R.id.tvAdminActiveDelCount);
        tvAdminDelayedDelCount = view.findViewById(R.id.tvAdminDelayedDelCount);
        tvAdminCompletedDelCount = view.findViewById(R.id.tvAdminCompletedDelCount);

        cardStatDonors = view.findViewById(R.id.cardStatDonors);
        cardStatHospitals = view.findViewById(R.id.cardStatHospitals);
        cardStatAvailOrgans = view.findViewById(R.id.cardStatAvailOrgans);
        cardStatPendingReq = view.findViewById(R.id.cardStatPendingReq);
        cardStatActiveReq = view.findViewById(R.id.cardStatActiveReq);
        cardStatFulfilledReq = view.findViewById(R.id.cardStatFulfilledReq);
        cardStatPendingDel = view.findViewById(R.id.cardStatPendingDel);
        cardStatActiveDel = view.findViewById(R.id.cardStatActiveDel);
        cardStatDelayedDel = view.findViewById(R.id.cardStatDelayedDel);
        cardStatCompletedDel = view.findViewById(R.id.cardStatCompletedDel);

        btnAdminManageUsers = view.findViewById(R.id.btnAdminManageUsers);
        btnAdminReports = view.findViewById(R.id.btnAdminReports);
        btnAdminViewAuditLogs = view.findViewById(R.id.btnAdminViewAuditLogs);
        btnLogoutAdmin = view.findViewById(R.id.btnLogoutAdmin);
    }

    private void setupCardNavigation() {
        cardStatDonors.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UserManagementActivity.class);
            intent.putExtra(UserManagementActivity.EXTRA_INITIAL_TAB, Constants.ROLE_DONOR);
            startActivity(intent);
        });
        cardStatHospitals.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UserManagementActivity.class);
            intent.putExtra(UserManagementActivity.EXTRA_INITIAL_TAB, Constants.ROLE_HOSPITAL);
            startActivity(intent);
        });
        cardStatAvailOrgans.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AvailableOrgansActivity.class)));
        cardStatPendingReq.setOnClickListener(v -> startActivity(new Intent(requireActivity(), PendingRequestsActivity.class)));
        cardStatActiveReq.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ActiveRequestsActivity.class)));
        cardStatFulfilledReq.setOnClickListener(v -> startActivity(new Intent(requireActivity(), FulfilledRequestsActivity.class)));

        cardStatPendingDel.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AdminDeliveryManagementActivity.class)));
        cardStatActiveDel.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AdminDeliveryManagementActivity.class)));
        cardStatDelayedDel.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AdminDeliveryManagementActivity.class)));
        cardStatCompletedDel.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AdminDeliveryManagementActivity.class)));

        btnAdminManageUsers.setOnClickListener(v -> startActivity(new Intent(requireActivity(), UserManagementActivity.class)));
        btnAdminReports.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AdminReportsActivity.class)));
        btnAdminViewAuditLogs.setOnClickListener(v -> startActivity(new Intent(requireActivity(), AuditLogsActivity.class)));

        btnLogoutAdmin.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadDynamicMetrics() {
        repository.getDashboardMetrics(new AdminRepository.DataCallback<Map<String, Integer>>() {
            @Override
            public void onSuccess(Map<String, Integer> metrics) {
                if (!isAdded()) return;
                tvAdminDonorsCount.setText(String.valueOf(metrics.getOrDefault("totalDonors", 0)));
                tvAdminHospitalsCount.setText(String.valueOf(metrics.getOrDefault("totalHospitals", 0)));
                tvAdminAvailOrgansCount.setText(String.valueOf(metrics.getOrDefault("availableOrgans", 0)));
                tvAdminPendingReqCount.setText(String.valueOf(metrics.getOrDefault("pendingRequests", 0)));
                tvAdminActiveReqCount.setText(String.valueOf(metrics.getOrDefault("activeRequests", 0)));
                tvAdminFulfilledReqCount.setText(String.valueOf(metrics.getOrDefault("fulfilledRequests", 0)));
                tvAdminPendingDelCount.setText(String.valueOf(metrics.getOrDefault("pendingDeliveries", 0)));
                tvAdminActiveDelCount.setText(String.valueOf(metrics.getOrDefault("activeDeliveries", 0)));
                tvAdminDelayedDelCount.setText(String.valueOf(metrics.getOrDefault("delayedDeliveries", 0)));
                tvAdminCompletedDelCount.setText(String.valueOf(metrics.getOrDefault("completedDeliveries", 0)));
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to update dashboard metrics: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
