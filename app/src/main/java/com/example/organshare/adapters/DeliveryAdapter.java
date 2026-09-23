package com.example.organshare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.ViewHolder> {

    public interface OnDeliveryClickListener {
        void onManageDelivery(DeliveryModel delivery);
    }

    private final Context context;
    private final List<DeliveryModel> deliveryList;
    private final OnDeliveryClickListener listener;

    public DeliveryAdapter(Context context, List<DeliveryModel> deliveryList, OnDeliveryClickListener listener) {
        this.context = context;
        this.deliveryList = deliveryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_delivery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryModel delivery = deliveryList.get(position);

        holder.tvDeliveryId.setText(delivery.getDeliveryId());
        holder.tvDeliveryStatusBadge.setText(delivery.getCurrentStatus());
        holder.tvDeliveryOrgan.setText("Organ: " + delivery.getOrganType() + " (" + delivery.getBloodGroup() + ")");
        holder.tvDeliveryPickup.setText("Pickup: " + delivery.getPickupAddress());
        holder.tvDeliveryDestination.setText("Drop: " + delivery.getDestinationHospitalName());

        if (Constants.DEL_DELAYED.equalsIgnoreCase(delivery.getCurrentStatus())) {
            holder.tvDeliveryStatusBadge.setTextColor(Color.parseColor("#E11D48"));
        } else if (Constants.DEL_DELIVERED.equalsIgnoreCase(delivery.getCurrentStatus())) {
            holder.tvDeliveryStatusBadge.setTextColor(Color.parseColor("#059669"));
        } else {
            holder.tvDeliveryStatusBadge.setTextColor(Color.parseColor("#2563EB"));
        }

        holder.btnManageDelivery.setOnClickListener(v -> {
            if (listener != null) listener.onManageDelivery(delivery);
        });
    }

    @Override
    public int getItemCount() {
        return deliveryList != null ? deliveryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeliveryId, tvDeliveryStatusBadge, tvDeliveryOrgan, tvDeliveryPickup, tvDeliveryDestination;
        MaterialButton btnManageDelivery;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDeliveryId = itemView.findViewById(R.id.tvDeliveryId);
            tvDeliveryStatusBadge = itemView.findViewById(R.id.tvDeliveryStatusBadge);
            tvDeliveryOrgan = itemView.findViewById(R.id.tvDeliveryOrgan);
            tvDeliveryPickup = itemView.findViewById(R.id.tvDeliveryPickup);
            tvDeliveryDestination = itemView.findViewById(R.id.tvDeliveryDestination);
            btnManageDelivery = itemView.findViewById(R.id.btnManageDelivery);
        }
    }
}
