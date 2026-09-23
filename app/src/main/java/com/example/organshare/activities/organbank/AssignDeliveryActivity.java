package com.example.organshare.activities.organbank;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.DeliveryAdapter;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.repositories.OrganBankRepository;

import java.util.ArrayList;
import java.util.List;

public class AssignDeliveryActivity extends AppCompatActivity {

    private TextView tvAssignCourierHeader, tvAssignSubHeader;
    private RecyclerView rvAvailableCouriers;
    private OrganBankRepository repository;
    private OrderModel currentOrder;
    private final List<DeliveryPersonnelProfile> courierList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_delivery);

        repository = new OrganBankRepository();
        currentOrder = (OrderModel) getIntent().getSerializableExtra("ORDER_DATA");

        tvAssignCourierHeader = findViewById(R.id.tvAssignCourierHeader);
        tvAssignSubHeader = findViewById(R.id.tvAssignSubHeader);
        rvAvailableCouriers = findViewById(R.id.rvAvailableCouriers);

        if (currentOrder != null) {
            tvAssignSubHeader.setText("Order: " + currentOrder.getOrderId() + " (" + currentOrder.getOrganType() + " -> " + currentOrder.getHospitalName() + ")");
        }

        rvAvailableCouriers.setLayoutManager(new LinearLayoutManager(this));
        loadCouriers();
    }

    private void loadCouriers() {
        repository.getAvailableDeliveryPersonnel(new OrganBankRepository.DataCallback<List<DeliveryPersonnelProfile>>() {
            @Override
            public void onSuccess(List<DeliveryPersonnelProfile> result) {
                courierList.clear();
                if (result != null && !result.isEmpty()) {
                    courierList.addAll(result);
                } else {
                    // Provide fallback demo courier if database is fresh
                    DeliveryPersonnelProfile demoCourier = new DeliveryPersonnelProfile();
                    demoCourier.setDeliveryPersonId("DEL-USR-01");
                    demoCourier.setFullName("Marcus Vance");
                    demoCourier.setPhone("+1-555-700-1122");
                    demoCourier.setVehicleType("Rapid Medical Van");
                    demoCourier.setVerified(true);
                    courierList.add(demoCourier);
                }

                // Setup courier selection
                CourierSelectionAdapter adapter = new CourierSelectionAdapter(AssignDeliveryActivity.this, courierList, courier -> {
                    assignCourier(courier);
                });
                rvAvailableCouriers.setAdapter(adapter);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AssignDeliveryActivity.this, "Error loading couriers: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignCourier(DeliveryPersonnelProfile courier) {
        if (currentOrder == null) return;
        Toast.makeText(this, "Assigning " + courier.getFullName() + " to delivery...", Toast.LENGTH_SHORT).show();

        repository.assignDeliveryPersonnel(currentOrder, courier, new OrganBankRepository.DataCallback<String>() {
            @Override
            public void onSuccess(String deliveryId) {
                Toast.makeText(AssignDeliveryActivity.this, "Courier Assigned! Delivery " + deliveryId + " active.", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AssignDeliveryActivity.this, "Assignment failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Inner Selection Adapter
    public static class CourierSelectionAdapter extends RecyclerView.Adapter<CourierSelectionAdapter.ViewHolder> {
        private final android.content.Context context;
        private final List<DeliveryPersonnelProfile> list;
        private final OnSelectListener listener;

        public interface OnSelectListener { void onSelect(DeliveryPersonnelProfile courier); }

        public CourierSelectionAdapter(android.content.Context context, List<DeliveryPersonnelProfile> list, OnSelectListener listener) {
            this.context = context;
            this.list = list;
            this.listener = listener;
        }

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            android.view.View v = android.view.LayoutInflater.from(context).inflate(R.layout.item_user_management, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
            DeliveryPersonnelProfile c = list.get(position);
            holder.tvName.setText(c.getFullName());
            holder.tvRole.setText(c.getVehicleType() != null ? c.getVehicleType() : "Medical Courier");
            holder.tvEmail.setText("Phone: " + c.getPhone());
            holder.tvStatus.setText("Status: Available for Dispatch");
            holder.btnApprove.setText("Assign Courier");
            holder.btnApprove.setOnClickListener(v -> listener.onSelect(c));
            holder.btnReject.setVisibility(android.view.View.GONE);
        }

        @Override
        public int getItemCount() { return list.size(); }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvRole, tvEmail, tvStatus;
            com.google.android.material.button.MaterialButton btnApprove, btnReject;
            public ViewHolder(@androidx.annotation.NonNull android.view.View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvUserDisplayName);
                tvRole = itemView.findViewById(R.id.tvUserRoleBadge);
                tvEmail = itemView.findViewById(R.id.tvUserEmail);
                tvStatus = itemView.findViewById(R.id.tvVerificationState);
                btnApprove = itemView.findViewById(R.id.btnApproveUser);
                btnReject = itemView.findViewById(R.id.btnRejectOrSuspend);
            }
        }
    }
}
