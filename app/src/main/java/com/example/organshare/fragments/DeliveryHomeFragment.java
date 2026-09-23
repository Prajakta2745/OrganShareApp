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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.activities.auth.LoginActivity;
import com.example.organshare.activities.delivery.DeliveryDetailsActivity;
import com.example.organshare.adapters.DeliveryAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.repositories.DeliveryRepository;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeFragment extends Fragment {

    private TextView tvDeliveryWelcome;
    private ImageButton btnLogoutDelivery;
    private RecyclerView rvAssignedDeliveries;
    private DeliveryAdapter adapter;
    private final List<DeliveryModel> deliveryList = new ArrayList<>();
    private DeliveryRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_home, container, false);

        repository = new DeliveryRepository();
        tvDeliveryWelcome = view.findViewById(R.id.tvDeliveryWelcome);
        btnLogoutDelivery = view.findViewById(R.id.btnLogoutDelivery);
        rvAssignedDeliveries = view.findViewById(R.id.rvAssignedDeliveries);

        String name = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvDeliveryWelcome.setText(name);

        rvAssignedDeliveries.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DeliveryAdapter(requireContext(), deliveryList, delivery -> {
            Intent intent = new Intent(requireActivity(), DeliveryDetailsActivity.class);
            intent.putExtra("DELIVERY_DATA", delivery);
            startActivity(intent);
        });
        rvAssignedDeliveries.setAdapter(adapter);

        btnLogoutDelivery.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        loadDeliveries();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();
    }

    private void loadDeliveries() {
        String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
        repository.getDeliveryProfileByUserId(userId, new DeliveryRepository.DataCallback<DeliveryPersonnelProfile>() {
            @Override
            public void onSuccess(DeliveryPersonnelProfile profile) {
                String courierId = profile != null ? profile.getDeliveryPersonId() : "DEL-USR-01";
                repository.getAssignedDeliveries(courierId, new DeliveryRepository.DataCallback<List<DeliveryModel>>() {
                    @Override
                    public void onSuccess(List<DeliveryModel> result) {
                        deliveryList.clear();
                        if (result != null) {
                            deliveryList.addAll(result);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
