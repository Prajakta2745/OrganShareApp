package com.example.organshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.OrderModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onTrackDelivery(OrderModel order);
        void onAssignDelivery(OrderModel order);
    }

    private final Context context;
    private final List<OrderModel> orderList;
    private final boolean isBankView;
    private final OnOrderClickListener listener;

    public OrderAdapter(Context context, List<OrderModel> orderList, boolean isBankView, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.isBankView = isBankView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isBankView ? R.layout.item_bank_order : R.layout.item_hospital_order;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view, isBankView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        if (isBankView) {
            holder.tvBankOrderId.setText(order.getOrderId());
            holder.tvBankOrderStatus.setText(order.getOrderStatus());
            holder.tvDestHospital.setText("Destination: " + order.getHospitalName());
            holder.tvBankOrderOrgan.setText("Organ: " + order.getOrganType() + " (Blood: " + order.getBloodGroup() + ")");
            holder.tvAssignedCourier.setText("Courier: " + (order.getDeliveryPersonName() != null ? order.getDeliveryPersonName() : "Not Assigned"));

            boolean isAssignable = "PENDING_ASSIGNMENT".equalsIgnoreCase(order.getOrderStatus()) || order.getDeliveryPersonId() == null;
            holder.btnAssignDelivery.setVisibility(isAssignable ? View.VISIBLE : View.GONE);
            holder.btnAssignDelivery.setOnClickListener(v -> {
                if (listener != null) listener.onAssignDelivery(order);
            });

        } else {
            holder.tvOrderId.setText(order.getOrderId());
            holder.tvOrderStatus.setText(order.getOrderStatus());
            holder.tvOrderOrgan.setText(order.getOrganType() + " (Blood: " + order.getBloodGroup() + ")");
            holder.tvPickupBank.setText("Pickup: " + order.getOrganBankName());
            holder.tvCourierInfo.setText("Courier: " + (order.getDeliveryPersonName() != null ? order.getDeliveryPersonName() + " (" + order.getDeliveryPersonPhone() + ")" : "Assigning Courier..."));

            holder.btnTrackDelivery.setOnClickListener(v -> {
                if (listener != null) listener.onTrackDelivery(order);
            });
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Hospital fields
        TextView tvOrderId, tvOrderStatus, tvOrderOrgan, tvPickupBank, tvCourierInfo;
        MaterialButton btnTrackDelivery;

        // Bank fields
        TextView tvBankOrderId, tvBankOrderStatus, tvDestHospital, tvBankOrderOrgan, tvAssignedCourier;
        MaterialButton btnAssignDelivery;

        public ViewHolder(@NonNull View itemView, boolean isBankView) {
            super(itemView);
            if (isBankView) {
                tvBankOrderId = itemView.findViewById(R.id.tvBankOrderId);
                tvBankOrderStatus = itemView.findViewById(R.id.tvBankOrderStatus);
                tvDestHospital = itemView.findViewById(R.id.tvDestHospital);
                tvBankOrderOrgan = itemView.findViewById(R.id.tvBankOrderOrgan);
                tvAssignedCourier = itemView.findViewById(R.id.tvAssignedCourier);
                btnAssignDelivery = itemView.findViewById(R.id.btnAssignDelivery);
            } else {
                tvOrderId = itemView.findViewById(R.id.tvOrderId);
                tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
                tvOrderOrgan = itemView.findViewById(R.id.tvOrderOrgan);
                tvPickupBank = itemView.findViewById(R.id.tvPickupBank);
                tvCourierInfo = itemView.findViewById(R.id.tvCourierInfo);
                btnTrackDelivery = itemView.findViewById(R.id.btnTrackDelivery);
            }
        }
    }
}
