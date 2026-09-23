package com.example.organshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.activities.organbank.AddEditInventoryActivity;
import com.example.organshare.adapters.InventoryAdapter;
import com.example.organshare.models.OrganInventory;
import com.example.organshare.repositories.OrganBankRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BankInventoryFragment extends Fragment {

    private RecyclerView rvBankInventory;
    private FloatingActionButton fabAddInventory;
    private InventoryAdapter adapter;
    private final List<OrganInventory> inventoryList = new ArrayList<>();
    private OrganBankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_inventory, container, false);

        repository = new OrganBankRepository();
        rvBankInventory = view.findViewById(R.id.rvBankInventory);
        fabAddInventory = view.findViewById(R.id.fabAddInventory);

        rvBankInventory.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InventoryAdapter(requireContext(), inventoryList, item -> {
            Toast.makeText(requireContext(), "Organ: " + item.getOrganType() + " | Status: " + item.getAvailabilityStatus(), Toast.LENGTH_SHORT).show();
        });
        rvBankInventory.setAdapter(adapter);

        fabAddInventory.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddEditInventoryActivity.class);
            startActivity(intent);
        });

        loadInventory();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInventory();
    }

    private void loadInventory() {
        repository.getInventory(new OrganBankRepository.DataCallback<List<OrganInventory>>() {
            @Override
            public void onSuccess(List<OrganInventory> result) {
                inventoryList.clear();
                if (result != null) {
                    inventoryList.addAll(result);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
