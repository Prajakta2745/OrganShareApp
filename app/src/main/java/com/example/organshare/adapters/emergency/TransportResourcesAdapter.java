package com.example.organshare.adapters.emergency;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.emergency.TransportResource;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class TransportResourcesAdapter extends RecyclerView.Adapter<TransportResourcesAdapter.ViewHolder> {

    private final Context context;
    private final List<TransportResource> transportList;
    private final OnTransportActionListener actionListener;
    private final OnTransportItemClickListener clickListener;

    public interface OnTransportActionListener {
        void onUpdateStatus(TransportResource item);
    }

    public interface OnTransportItemClickListener {
        void onUpdateStatusClicked(TransportResource item);
    }

    public TransportResourcesAdapter(List<TransportResource> transportList, OnTransportItemClickListener listener) {
        this.context = null;
        this.transportList = transportList != null ? transportList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public TransportResourcesAdapter(Context context, List<TransportResource> transportList, OnTransportItemClickListener listener) {
        this.context = context;
        this.transportList = transportList != null ? transportList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public TransportResourcesAdapter(Context context, List<TransportResource> transportList, OnTransportActionListener listener) {
        this.context = context;
        this.transportList = transportList != null ? transportList : new ArrayList<>();
        this.actionListener = listener;
        this.clickListener = null;
    }

    public void setTransportList(@Nullable List<TransportResource> list) {
        this.transportList.clear();
        if (list != null) {
            this.transportList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_transport_resource_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransportResource item = transportList.get(position);

        holder.tvVehicleId.setText(item.getVehicleId() != null ? item.getVehicleId() : "AMB");
        holder.tvVehicleType.setText(item.getVehicleType() != null ? item.getVehicleType() : "Ambulance");
        holder.tvDriverInfo.setText("👤 Driver: " + (item.getDriverName() != null ? item.getDriverName() : "Assigned Courier") + " (" + (item.getDriverContact() != null ? item.getDriverContact() : "--") + ")");
        holder.tvCurrentLocation.setText("📍 Location: " + (item.getCurrentLocation() != null ? item.getCurrentLocation() : "In Dispatch Range"));

        if (item.getCurrentAssignment() != null && !item.getCurrentAssignment().isEmpty()) {
            holder.tvCurrentAssignment.setText("📋 Assignment: " + item.getCurrentAssignment());
            holder.tvCurrentAssignment.setVisibility(View.VISIBLE);
        } else {
            holder.tvCurrentAssignment.setText("📋 Assignment: Ready for Dispatch");
            holder.tvCurrentAssignment.setVisibility(View.VISIBLE);
        }

        String st = item.getAvailabilityStatus() != null ? item.getAvailabilityStatus().toUpperCase() : "AVAILABLE";
        holder.tvTransportStatusBadge.setText(st);

        if ("MAINTENANCE".equalsIgnoreCase(st) || "UNAVAILABLE".equalsIgnoreCase(st)) {
            holder.tvTransportStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvTransportStatusBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("ASSIGNED".equalsIgnoreCase(st) || "IN_TRANSIT".equalsIgnoreCase(st)) {
            holder.tvTransportStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvTransportStatusBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvTransportStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvTransportStatusBadge.setTextColor(Color.parseColor("#166534"));
        }

        holder.btnToggleTransportStatus.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateStatusClicked(item);
            }
            if (actionListener != null) {
                actionListener.onUpdateStatus(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateStatusClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transportList != null ? transportList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicleId, tvVehicleType, tvTransportStatusBadge, tvDriverInfo, tvCurrentLocation, tvCurrentAssignment;
        MaterialButton btnToggleTransportStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleId = itemView.findViewById(R.id.tvVehicleId);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            tvTransportStatusBadge = itemView.findViewById(R.id.tvTransportStatusBadge);
            tvDriverInfo = itemView.findViewById(R.id.tvDriverInfo);
            tvCurrentLocation = itemView.findViewById(R.id.tvCurrentLocation);
            tvCurrentAssignment = itemView.findViewById(R.id.tvCurrentAssignment);
            btnToggleTransportStatus = itemView.findViewById(R.id.btnToggleTransportStatus);
        }
    }
}
