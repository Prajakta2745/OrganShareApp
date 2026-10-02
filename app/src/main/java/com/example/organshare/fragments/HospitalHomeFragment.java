package com.example.organshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.common.NotificationsActivity;
import com.example.organshare.activities.hospital.CreateBloodRequestActivity;
import com.example.organshare.activities.hospital.CreateRequestActivity;
import com.example.organshare.activities.hospital.HospitalResourcesManagementActivity;
import com.example.organshare.activities.hospital.OrganRequestsHubActivity;
import com.example.organshare.activities.hospital.SearchDonorsActivity;
import com.example.organshare.activities.hospital.TrackDeliveryMapActivity;
import com.example.organshare.auth.AuthManager;
import com.google.android.material.card.MaterialCardView;

public class HospitalHomeFragment extends Fragment {

    private TextView tvHospitalWelcome, tvVerificationBadge;
    private ImageButton btnLogoutHospital;
    private MaterialCardView cardCreateRequest, cardCreateBloodRequest, cardHospitalRequestsHub;
    private MaterialCardView cardHospitalDeliveryTracking, cardSearchDonors, cardHospitalManageBeds, cardHospitalNotif;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_home, container, false);

        initViews(view);
        setupActions();
        loadHospitalInfo();

        return view;
    }

    private void initViews(View view) {
        tvHospitalWelcome = view.findViewById(R.id.tvHospitalWelcome);
        tvVerificationBadge = view.findViewById(R.id.tvVerificationBadge);
        btnLogoutHospital = view.findViewById(R.id.btnLogoutHospital);

        cardCreateRequest = view.findViewById(R.id.cardCreateRequest);
        cardCreateBloodRequest = view.findViewById(R.id.cardCreateBloodRequest);
        cardHospitalRequestsHub = view.findViewById(R.id.cardHospitalRequestsHub);
        cardHospitalDeliveryTracking = view.findViewById(R.id.cardHospitalDeliveryTracking);
        cardSearchDonors = view.findViewById(R.id.cardSearchDonors);
        cardHospitalManageBeds = view.findViewById(R.id.cardHospitalManageBeds);
        cardHospitalNotif = view.findViewById(R.id.cardHospitalNotif);
    }

    private void setupActions() {
        cardCreateRequest.setOnClickListener(v -> startActivity(new Intent(requireContext(), CreateRequestActivity.class)));
        cardCreateBloodRequest.setOnClickListener(v -> startActivity(new Intent(requireContext(), CreateBloodRequestActivity.class)));
        cardHospitalRequestsHub.setOnClickListener(v -> startActivity(new Intent(requireContext(), OrganRequestsHubActivity.class)));
        cardHospitalDeliveryTracking.setOnClickListener(v -> startActivity(new Intent(requireContext(), TrackDeliveryMapActivity.class)));
        cardSearchDonors.setOnClickListener(v -> startActivity(new Intent(requireContext(), SearchDonorsActivity.class)));
        if (cardHospitalManageBeds != null) {
            cardHospitalManageBeds.setOnClickListener(v -> startActivity(new Intent(requireContext(), HospitalResourcesManagementActivity.class)));
        }
        cardHospitalNotif.setOnClickListener(v -> startActivity(new Intent(requireContext(), NotificationsActivity.class)));

        btnLogoutHospital.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadHospitalInfo() {
        String name = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        if (name != null && !name.isEmpty()) {
            tvHospitalWelcome.setText(name);
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to sign out from Hospital Portal?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    AuthManager.getInstance(requireContext()).logout(requireContext());
                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
