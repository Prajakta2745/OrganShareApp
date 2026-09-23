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
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class DonorHomeFragment extends Fragment {

    private TextView tvDonorWelcome, tvDonorBloodBadge, tvPledgeStatus, tvDonorIdDisplay;
    private ChipGroup chipGroupOrgans;
    private ImageButton btnLogoutDonor;
    private DonorRepository donorRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_home, container, false);

        donorRepository = new DonorRepository();
        tvDonorWelcome = view.findViewById(R.id.tvDonorWelcome);
        tvDonorBloodBadge = view.findViewById(R.id.tvDonorBloodBadge);
        tvPledgeStatus = view.findViewById(R.id.tvPledgeStatus);
        tvDonorIdDisplay = view.findViewById(R.id.tvDonorIdDisplay);
        chipGroupOrgans = view.findViewById(R.id.chipGroupOrgans);
        btnLogoutDonor = view.findViewById(R.id.btnLogoutDonor);

        setupLogout();
        loadDonorData();

        return view;
    }

    private void setupLogout() {
        btnLogoutDonor.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void loadDonorData() {
        String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
        String displayName = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvDonorWelcome.setText("Welcome, " + displayName);

        donorRepository.getDonorByUserId(userId, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null && isAdded()) {
                    tvDonorBloodBadge.setText("Blood Group: " + (profile.getBloodGroup() != null ? profile.getBloodGroup() : "O+"));
                    tvPledgeStatus.setText((profile.getDonationStatus() != null ? profile.getDonationStatus() : "PLEDGED") + " - ACTIVE REGISTRATION");
                    tvDonorIdDisplay.setText("Donor Registration ID: " + profile.getDonorId());

                    chipGroupOrgans.removeAllViews();
                    if (profile.getOrgansWillingToDonate() != null) {
                        for (String organ : profile.getOrgansWillingToDonate()) {
                            Chip chip = new Chip(requireContext());
                            chip.setText(organ);
                            chip.setChipIconResource(R.drawable.ic_heart);
                            chipGroupOrgans.addView(chip);
                        }
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
