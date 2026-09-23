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
import androidx.fragment.app.Fragment;
import com.example.organshare.R;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.hospital.CreateRequestActivity;
import com.example.organshare.activities.hospital.SearchDonorsActivity;
import com.example.organshare.auth.AuthManager;
import com.google.android.material.card.MaterialCardView;

public class HospitalHomeFragment extends Fragment {

    private TextView tvHospitalWelcome;
    private MaterialCardView cardCreateRequest, cardSearchDonors;
    private ImageButton btnLogoutHospital;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_home, container, false);

        tvHospitalWelcome = view.findViewById(R.id.tvHospitalWelcome);
        cardCreateRequest = view.findViewById(R.id.cardCreateRequest);
        cardSearchDonors = view.findViewById(R.id.cardSearchDonors);
        btnLogoutHospital = view.findViewById(R.id.btnLogoutHospital);

        String name = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvHospitalWelcome.setText(name);

        cardCreateRequest.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateRequestActivity.class);
            startActivity(intent);
        });

        cardSearchDonors.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), SearchDonorsActivity.class);
            startActivity(intent);
        });

        btnLogoutHospital.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }
}
