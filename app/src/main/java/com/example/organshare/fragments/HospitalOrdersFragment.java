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
import com.example.organshare.activities.hospital.HospitalOrderDetailsActivity;
import com.example.organshare.activities.hospital.TrackDeliveryMapActivity;
import com.example.organshare.adapters.OrderAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.repositories.HospitalRepository;

import java.util.ArrayList;
import java.util.List;

public class HospitalOrdersFragment extends Fragment {

    private RecyclerView rvHospitalOrders;
    private OrderAdapter adapter;
    private final List<OrderModel> orderList = new ArrayList<>();
    private HospitalRepository hospitalRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_orders, container, false);

        hospitalRepository = new HospitalRepository();
        rvHospitalOrders = view.findViewById(R.id.rvHospitalOrders);
        rvHospitalOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new OrderAdapter(requireContext(), orderList, false, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onTrackDelivery(OrderModel order) {
                Intent intent = new Intent(requireActivity(), TrackDeliveryMapActivity.class);
                intent.putExtra("DELIVERY_ID", order.getDeliveryId());
                intent.putExtra("ORDER_DATA", order);
                startActivity(intent);
            }

            @Override
            public void onAssignDelivery(OrderModel order) {}
        });
        rvHospitalOrders.setAdapter(adapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {
        String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
        hospitalRepository.getHospitalByUserId(userId, new HospitalRepository.DataCallback<HospitalProfile>() {
            @Override
            public void onSuccess(HospitalProfile profile) {
                String hospitalId = profile != null ? profile.getHospitalId() : "HOSP-001";
                hospitalRepository.getHospitalOrders(hospitalId, new HospitalRepository.DataCallback<List<OrderModel>>() {
                    @Override
                    public void onSuccess(List<OrderModel> result) {
                        orderList.clear();
                        if (result != null) {
                            orderList.addAll(result);
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
