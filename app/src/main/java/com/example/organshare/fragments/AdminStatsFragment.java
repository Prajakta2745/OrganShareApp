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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.admin.AuditLogsActivity;
import com.example.organshare.activities.admin.UserManagementActivity;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.repositories.AdminRepository;
import com.google.android.material.button.MaterialButton;

import java.util.Map;

public class AdminStatsFragment extends Fragment {

    private TextView tvAdminDonorsCount, tvAdminHospitalsCount, tvAdminRequestsCount, tvAdminInventoryCount;
    private MaterialButton btnAdminManageUsers, btnAdminViewAuditLogs;
    private ImageButton btnLogoutAdmin;
    private AdminRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_stats, container, false);

        repository = new AdminRepository();
        tvAdminDonorsCount = view.findViewById(R.id.tvAdminDonorsCount);
        tvAdminHospitalsCount = view.findViewById(R.id.tvAdminHospitalsCount);
        tvAdminRequestsCount = view.findViewById(R.id.tvAdminRequestsCount);
        tvAdminInventoryCount = view.findViewById(R.id.tvAdminInventoryCount);
        btnAdminManageUsers = view.findViewById(R.id.btnAdminManageUsers);
        btnAdminViewAuditLogs = view.findViewById(R.id.btnAdminViewAuditLogs);
        btnLogoutAdmin = view.findViewById(R.id.btnLogoutAdmin);

        btnAdminManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), UserManagementActivity.class);
            startActivity(intent);
        });

        btnAdminViewAuditLogs.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AuditLogsActivity.class);
            startActivity(intent);
        });

        btnLogoutAdmin.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        loadMetrics();

        return view;
    }

    private void loadMetrics() {
        repository.getDashboardMetrics(new AdminRepository.DataCallback<Map<String, Integer>>() {
            @Override
            public void onSuccess(Map<String, Integer> metrics) {
                if (isAdded()) {
                    tvAdminDonorsCount.setText(String.valueOf(metrics.getOrDefault("donorsCount", 0)));
                    tvAdminHospitalsCount.setText(String.valueOf(metrics.getOrDefault("hospitalsCount", 0)));
                    tvAdminRequestsCount.setText(String.valueOf(metrics.getOrDefault("requestsCount", 0)));
                    tvAdminInventoryCount.setText(String.valueOf(metrics.getOrDefault("inventoryCount", 0)));
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
