package com.example.organshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.common.NotificationsActivity;
import com.example.organshare.activities.common.SettingsActivity;
import com.example.organshare.activities.donor.BloodDonorRegistrationActivity;
import com.example.organshare.activities.donor.DigitalDonorCardActivity;
import com.example.organshare.activities.donor.DonationInfoActivity;
import com.example.organshare.activities.donor.DonorProfileActivity;
import com.example.organshare.activities.donor.EligibilityScreenerActivity;
import com.example.organshare.activities.donor.FamilyDonationActivity;
import com.example.organshare.activities.donor.OrganPledgeActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class DonorHomeFragment extends Fragment {

    private TextView tvDonorWelcome, tvDonorBloodBadge, tvPledgeStatus, tvDonorIdDisplay;
    private ChipGroup chipGroupOrgans;
    private MaterialCardView cardDonorProfile, cardOrganPledge, cardBloodDonation, cardDigitalDonorCard;
    private MaterialCardView cardFamilyDonation, cardEligibilityScreener, cardDonationHistory, cardNotifications;
    private MaterialButton btnDonorSettings, btnDonorLogout;

    private DonorRepository donorRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donor_home, container, false);

        donorRepository = new DonorRepository();

        initViews(view);
        setupCardClicks();
        loadDonorData();

        return view;
    }

    private void initViews(View view) {
        tvDonorWelcome = view.findViewById(R.id.tvDonorWelcome);
        tvDonorBloodBadge = view.findViewById(R.id.tvDonorBloodBadge);
        tvPledgeStatus = view.findViewById(R.id.tvPledgeStatus);
        tvDonorIdDisplay = view.findViewById(R.id.tvDonorIdDisplay);
        chipGroupOrgans = view.findViewById(R.id.chipGroupOrgans);

        cardDonorProfile = view.findViewById(R.id.cardDonorProfile);
        cardOrganPledge = view.findViewById(R.id.cardOrganPledge);
        cardBloodDonation = view.findViewById(R.id.cardBloodDonation);
        cardDigitalDonorCard = view.findViewById(R.id.cardDigitalDonorCard);
        cardFamilyDonation = view.findViewById(R.id.cardFamilyDonation);
        cardEligibilityScreener = view.findViewById(R.id.cardEligibilityScreener);
        cardDonationHistory = view.findViewById(R.id.cardDonationHistory);
        cardNotifications = view.findViewById(R.id.cardNotifications);

        btnDonorSettings = view.findViewById(R.id.btnDonorSettings);
        btnDonorLogout = view.findViewById(R.id.btnDonorLogout);
    }

    private void setupCardClicks() {
        cardDonorProfile.setOnClickListener(v -> startActivity(new Intent(requireContext(), DonorProfileActivity.class)));
        cardOrganPledge.setOnClickListener(v -> startActivity(new Intent(requireContext(), OrganPledgeActivity.class)));
        cardBloodDonation.setOnClickListener(v -> startActivity(new Intent(requireContext(), BloodDonorRegistrationActivity.class)));
        cardDigitalDonorCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), DigitalDonorCardActivity.class)));
        cardFamilyDonation.setOnClickListener(v -> startActivity(new Intent(requireContext(), FamilyDonationActivity.class)));
        cardEligibilityScreener.setOnClickListener(v -> startActivity(new Intent(requireContext(), EligibilityScreenerActivity.class)));
        cardDonationHistory.setOnClickListener(v -> startActivity(new Intent(requireContext(), DonationInfoActivity.class)));
        cardNotifications.setOnClickListener(v -> startActivity(new Intent(requireContext(), NotificationsActivity.class)));

        btnDonorSettings.setOnClickListener(v -> startActivity(new Intent(requireContext(), SettingsActivity.class)));
        btnDonorLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to sign out of OrganShare?")
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

    @Override
    public void onResume() {
        super.onResume();
        loadDonorData();
    }

    private void loadDonorData() {
        if (!isAdded()) return;

        String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
        String displayName = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvDonorWelcome.setText("Welcome, " + (displayName != null ? displayName : "Donor"));

        donorRepository.getDonorByUserId(userId, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null && isAdded()) {
                    tvDonorBloodBadge.setText("Blood Group: " + (profile.getBloodGroup() != null ? profile.getBloodGroup() : "O+"));
                    tvPledgeStatus.setText((profile.getDonationStatus() != null ? profile.getDonationStatus() : "PLEDGED"));
                    tvDonorIdDisplay.setText("Donor ID: " + profile.getDonorId());

                    chipGroupOrgans.removeAllViews();
                    if (profile.isFullBodyDonation()) {
                        Chip chip = new Chip(requireContext());
                        chip.setText("Full Body Donation");
                        chip.setChipIconResource(R.drawable.ic_heart);
                        chipGroupOrgans.addView(chip);
                    } else if (profile.getOrgansWillingToDonate() != null && !profile.getOrgansWillingToDonate().isEmpty()) {
                        for (String organ : profile.getOrgansWillingToDonate()) {
                            Chip chip = new Chip(requireContext());
                            chip.setText(organ);
                            chip.setChipIconResource(R.drawable.ic_heart);
                            chipGroupOrgans.addView(chip);
                        }
                    } else {
                        Chip chip = new Chip(requireContext());
                        chip.setText("Tap 'Organ Pledges' to register preferences");
                        chipGroupOrgans.addView(chip);
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) {
                    // Fallback
                    tvDonorIdDisplay.setText("Donor ID: DNR-1001");
                }
            }
        });
    }
}
