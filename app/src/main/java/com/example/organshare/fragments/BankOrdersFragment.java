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
import com.example.organshare.activities.organbank.AssignDeliveryActivity;
import com.example.organshare.adapters.OrderAdapter;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.OrderModel;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BankOrdersFragment extends Fragment {

    private RecyclerView rvBankOrders;
    private OrderAdapter adapter;
    private final List<OrderModel> orderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_orders, container, false);

        rvBankOrders = view.findViewById(R.id.rvBankOrders);
        rvBankOrders.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new OrderAdapter(requireContext(), orderList, true, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onTrackDelivery(OrderModel order) {}

            @Override
            public void onAssignDelivery(OrderModel order) {
                Intent intent = new Intent(requireActivity(), AssignDeliveryActivity.class);
                intent.putExtra("ORDER_DATA", order);
                startActivity(intent);
            }
        });
        rvBankOrders.setAdapter(adapter);

        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        FirestoreHelper.getFirestore().collection(FirestoreCollections.ORDERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot snap : queryDocumentSnapshots) {
                        orderList.add(snap.toObject(OrderModel.class));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
