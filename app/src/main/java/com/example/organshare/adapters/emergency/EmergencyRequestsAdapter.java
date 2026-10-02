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
import com.example.organshare.models.emergency.EmergencyRequest;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class EmergencyRequestsAdapter extends RecyclerView.Adapter<EmergencyRequestsAdapter.ViewHolder> {

    private final Context context;
    private final List<EmergencyRequest> requestList;
    private final OnEmergencyRequestClickListener listener;

    public interface OnEmergencyRequestClickListener {
        void onUpdateStatusClicked(EmergencyRequest item);
    }

    public EmergencyRequestsAdapter(List<EmergencyRequest> requestList, OnEmergencyRequestClickListener listener) {
        this.context = null;
        this.requestList = requestList != null ? requestList : new ArrayList<>();
        this.listener = listener;
    }

    public EmergencyRequestsAdapter(Context context, List<EmergencyRequest> requestList, OnEmergencyRequestClickListener listener) {
        this.context = context;
        this.requestList = requestList != null ? requestList : new ArrayList<>();
        this.listener = listener;
    }

    public void setRequestList(@Nullable List<EmergencyRequest> list) {
        this.requestList.clear();
        if (list != null) {
            this.requestList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_emergency_request_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmergencyRequest item = requestList.get(position);

        holder.tvEmergencyReqId.setText(item.getEmergencyRequestId() != null ? item.getEmergencyRequestId() : "ER");
        holder.tvEmergencyResourceRequired.setText("Required: " + (item.getResourceDetails() != null ? item.getResourceDetails() : (item.getRequiredQuantity() + " " + item.getResourceType())));
        holder.tvEmergencyHospitalName.setText("🏥 " + (item.getHospitalName() != null ? item.getHospitalName() : "General Emergency Queue"));
        holder.tvEmergencyDescription.setText(item.getDescription() != null ? item.getDescription() : "Urgent medical resource requisition.");

        // Priority Badge
        String prio = item.getPriority() != null ? item.getPriority().toUpperCase() : "MEDIUM";
        holder.tvEmergencyPriorityBadge.setText(prio);

        if ("CRITICAL".equalsIgnoreCase(prio)) {
            holder.tvEmergencyPriorityBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvEmergencyPriorityBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("HIGH".equalsIgnoreCase(prio)) {
            holder.tvEmergencyPriorityBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvEmergencyPriorityBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvEmergencyPriorityBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvEmergencyPriorityBadge.setTextColor(Color.parseColor("#166534"));
        }

        // Status Badge
        String st = item.getStatus() != null ? item.getStatus().toUpperCase() : "PENDING";
        holder.tvEmergencyStatusBadge.setText(st);

        if ("FULFILLED".equalsIgnoreCase(st) || "COMPLETED".equalsIgnoreCase(st)) {
            holder.tvEmergencyStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvEmergencyStatusBadge.setTextColor(Color.parseColor("#166534"));
            holder.btnAllocateResource.setText("View Allocation ✓");
        } else if ("IN_PROGRESS".equalsIgnoreCase(st) || "PARTIALLY_FULFILLED".equalsIgnoreCase(st)) {
            holder.tvEmergencyStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvEmergencyStatusBadge.setTextColor(Color.parseColor("#92400E"));
            holder.btnAllocateResource.setText("Complete Allocation →");
        } else {
            holder.tvEmergencyStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvEmergencyStatusBadge.setTextColor(Color.parseColor("#991B1B"));
            holder.btnAllocateResource.setText("Allocate Resource →");
        }

        holder.btnAllocateResource.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatusClicked(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUpdateStatusClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList != null ? requestList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmergencyReqId, tvEmergencyPriorityBadge, tvEmergencyStatusBadge;
        TextView tvEmergencyResourceRequired, tvEmergencyHospitalName, tvEmergencyDescription;
        MaterialButton btnAllocateResource;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmergencyReqId = itemView.findViewById(R.id.tvEmergencyReqId);
            tvEmergencyPriorityBadge = itemView.findViewById(R.id.tvEmergencyPriorityBadge);
            tvEmergencyStatusBadge = itemView.findViewById(R.id.tvEmergencyStatusBadge);
            tvEmergencyResourceRequired = itemView.findViewById(R.id.tvEmergencyResourceRequired);
            tvEmergencyHospitalName = itemView.findViewById(R.id.tvEmergencyHospitalName);
            tvEmergencyDescription = itemView.findViewById(R.id.tvEmergencyDescription);
            btnAllocateResource = itemView.findViewById(R.id.btnAllocateResource);
        }
    }
}
