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
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class DeliveryHomeFragment extends Fragment {

    private TextView tvDeliveryWelcome, tvEmptyCourierDeliveries;
    private TextView tvCourierPendingCount, tvCourierActiveCount, tvCourierDelayedCount, tvCourierCompletedCount;
    private MaterialCardView cardCourierPending, cardCourierActive, cardCourierDelayed, cardCourierCompleted;
    private MaterialButton btnCourierTabAll, btnCourierTabPending, btnCourierTabActive, btnCourierTabDelayed, btnCourierTabDone;
    private ImageButton btnLogoutDelivery;
    private RecyclerView rvAssignedDeliveries;
    private DeliveryAdapter adapter;
    private final List<DeliveryModel> allAssignedDeliveries = new ArrayList<>();
    private final List<DeliveryModel> displayedDeliveries = new ArrayList<>();
    private DeliveryRepository repository;
    private String selectedTab = "ALL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_home, container, false);

        repository = new DeliveryRepository();

        initViews(view);
        setupTabs();
        setupRecyclerView();
        loadDeliveries();

        btnLogoutDelivery.setOnClickListener(v -> {
            AuthManager.getInstance(requireContext()).logout(requireContext());
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDeliveries();
    }

    private void initViews(View view) {
        tvDeliveryWelcome = view.findViewById(R.id.tvDeliveryWelcome);
        tvEmptyCourierDeliveries = view.findViewById(R.id.tvEmptyCourierDeliveries);
        tvCourierPendingCount = view.findViewById(R.id.tvCourierPendingCount);
        tvCourierActiveCount = view.findViewById(R.id.tvCourierActiveCount);
        tvCourierDelayedCount = view.findViewById(R.id.tvCourierDelayedCount);
        tvCourierCompletedCount = view.findViewById(R.id.tvCourierCompletedCount);

        cardCourierPending = view.findViewById(R.id.cardCourierPending);
        cardCourierActive = view.findViewById(R.id.cardCourierActive);
        cardCourierDelayed = view.findViewById(R.id.cardCourierDelayed);
        cardCourierCompleted = view.findViewById(R.id.cardCourierCompleted);

        btnCourierTabAll = view.findViewById(R.id.btnCourierTabAll);
        btnCourierTabPending = view.findViewById(R.id.btnCourierTabPending);
        btnCourierTabActive = view.findViewById(R.id.btnCourierTabActive);
        btnCourierTabDelayed = view.findViewById(R.id.btnCourierTabDelayed);
        btnCourierTabDone = view.findViewById(R.id.btnCourierTabDone);

        btnLogoutDelivery = view.findViewById(R.id.btnLogoutDelivery);
        rvAssignedDeliveries = view.findViewById(R.id.rvAssignedDeliveries);

        String name = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
        tvDeliveryWelcome.setText("Welcome, " + (name != null ? name : "Courier"));
    }

    private void setupTabs() {
        btnCourierTabAll.setOnClickListener(v -> filterDeliveries("ALL"));
        btnCourierTabPending.setOnClickListener(v -> filterDeliveries("PENDING"));
        btnCourierTabActive.setOnClickListener(v -> filterDeliveries("ACTIVE"));
        btnCourierTabDelayed.setOnClickListener(v -> filterDeliveries("DELAYED"));
        btnCourierTabDone.setOnClickListener(v -> filterDeliveries("COMPLETED"));

        cardCourierPending.setOnClickListener(v -> filterDeliveries("PENDING"));
        cardCourierActive.setOnClickListener(v -> filterDeliveries("ACTIVE"));
        cardCourierDelayed.setOnClickListener(v -> filterDeliveries("DELAYED"));
        cardCourierCompleted.setOnClickListener(v -> filterDeliveries("COMPLETED"));
    }

    private void filterDeliveries(String tab) {
        selectedTab = tab;
        displayedDeliveries.clear();
        for (DeliveryModel d : allAssignedDeliveries) {
            String st = d.getCurrentStatus() != null ? d.getCurrentStatus() : "";
            if ("ALL".equals(tab)) {
                displayedDeliveries.add(d);
            } else if ("PENDING".equals(tab)) {
                if (Constants.DEL_ASSIGNED.equalsIgnoreCase(st) || "PICKUP_PENDING".equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("ACTIVE".equals(tab)) {
                if (!Constants.DEL_ASSIGNED.equalsIgnoreCase(st) &&
                    !"PICKUP_PENDING".equalsIgnoreCase(st) &&
                    !Constants.DEL_DELAYED.equalsIgnoreCase(st) &&
                    !Constants.DEL_COMPLETED.equalsIgnoreCase(st) &&
                    !Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("DELAYED".equals(tab)) {
                if (Constants.DEL_DELAYED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            } else if ("COMPLETED".equals(tab)) {
                if (Constants.DEL_COMPLETED.equalsIgnoreCase(st) || Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                    displayedDeliveries.add(d);
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (displayedDeliveries.isEmpty()) {
            tvEmptyCourierDeliveries.setVisibility(View.VISIBLE);
        } else {
            tvEmptyCourierDeliveries.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        rvAssignedDeliveries.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DeliveryAdapter(requireContext(), displayedDeliveries, delivery -> {
            Intent intent = new Intent(requireActivity(), DeliveryDetailsActivity.class);
            intent.putExtra("DELIVERY_DATA", delivery);
            startActivity(intent);
        });
        rvAssignedDeliveries.setAdapter(adapter);
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
                        allAssignedDeliveries.clear();
                        if (result != null) {
                            allAssignedDeliveries.addAll(result);
                        }
                        updateSummaryCounts();
                        filterDeliveries(selectedTab);
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

    private void updateSummaryCounts() {
        int pending = 0, active = 0, delayed = 0, completed = 0;
        for (DeliveryModel d : allAssignedDeliveries) {
            String st = d.getCurrentStatus() != null ? d.getCurrentStatus() : "";
            if (Constants.DEL_ASSIGNED.equalsIgnoreCase(st) || "PICKUP_PENDING".equalsIgnoreCase(st)) {
                pending++;
            } else if (Constants.DEL_DELAYED.equalsIgnoreCase(st)) {
                delayed++;
            } else if (Constants.DEL_COMPLETED.equalsIgnoreCase(st) || Constants.DEL_DELIVERED.equalsIgnoreCase(st)) {
                completed++;
            } else {
                active++;
            }
        }
        if (isAdded()) {
            tvCourierPendingCount.setText(String.valueOf(pending));
            tvCourierActiveCount.setText(String.valueOf(active));
            tvCourierDelayedCount.setText(String.valueOf(delayed));
            tvCourierCompletedCount.setText(String.valueOf(completed));
        }
    }
}
