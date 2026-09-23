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
import com.example.organshare.activities.organbank.AddEditInventoryActivity;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BankHomeFragment extends Fragment {

    private TextView tvBankWelcome, tvStatPendingReq, tvStatAvailableInv;
    private MaterialButton btnAddOrganInventory;
    private ImageButton btnLogoutBank;
    private OrganBankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_home, container, false);

        repository = new OrganBankRepository();
        tvBankWelcome = view.findViewById(R.id.tvBankWelcome);
        tvStatPendingReq = view.findViewById(R.id.tvStatPendingReq);
        tvStatAvailableInv = view.findViewById(R.id.tvStatAvailableInv);
        btnAddOrganInventory = view.findViewById(R.id.btnAddOrganInventory);
        btnLogoutBank = view.findViewById(R.id.btnLogoutBank);

        String name = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvBankWelcome.setText(name);

        btnAddOrganInventory.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddEditInventoryActivity.class);
            startActivity(intent);
        });

        btnLogoutBank.setOnClickListener(v -> {
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
        repository.getAllRequests(new OrganBankRepository.DataCallback<List<OrganRequest>>() {
            @Override
            public void onSuccess(List<OrganRequest> requests) {
                int pending = 0;
                if (requests != null) {
                    for (OrganRequest r : requests) {
                        if (Constants.STATUS_PENDING.equalsIgnoreCase(r.getStatus()) || Constants.STATUS_UNDER_REVIEW.equalsIgnoreCase(r.getStatus())) {
                            pending++;
                        }
                    }
                }
                if (isAdded()) tvStatPendingReq.setText(String.valueOf(pending));
            }

            @Override
            public void onFailure(String error) {}
        });

        repository.getInventory(new OrganBankRepository.DataCallback<List<OrganInventory>>() {
            @Override
            public void onSuccess(List<OrganInventory> inventory) {
                int avail = 0;
                if (inventory != null) {
                    for (OrganInventory i : inventory) {
                        if (Constants.INV_AVAILABLE.equalsIgnoreCase(i.getAvailabilityStatus())) {
                            avail++;
                        }
                    }
                }
                if (isAdded()) tvStatAvailableInv.setText(String.valueOf(avail));
            }

            @Override
            public void onFailure(String error) {}
        });
    }
}
