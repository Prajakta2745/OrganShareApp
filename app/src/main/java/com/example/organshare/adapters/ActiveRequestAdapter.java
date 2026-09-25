package com.example.organshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.OrganRequest;

import java.util.List;

public class ActiveRequestAdapter extends RecyclerView.Adapter<ActiveRequestAdapter.ViewHolder> {
    private final Context context;
    private final List<OrganRequest> requestList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick(OrganRequest request); }

    public ActiveRequestAdapter(Context context, List<OrganRequest> requestList, OnItemClickListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_active_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrganRequest req = requestList.get(position);
        holder.tvActiveReqId.setText(req.getRequestId());
        holder.tvActiveReqStatus.setText(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        holder.tvActiveHospitalName.setText(req.getHospitalName());
        holder.tvActiveOrganBlood.setText("Organ: " + req.getOrganRequired() + " (Blood Group: " + req.getBloodGroup() + ")");
        holder.tvActiveQtyRequested.setText(String.valueOf(req.getRequestedQuantity()));
        holder.tvActiveQtyFulfilled.setText(String.valueOf(req.getFulfilledQuantity()));
        holder.tvActiveQtyRemaining.setText(String.valueOf(req.getRemainingQuantity()));
        holder.tvActiveDates.setText("Required By: " + (req.getRequiredDate() != null ? req.getRequiredDate() : "Immediate") + " | Priority: " + req.getEmergencyLevel());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(req);
        });
    }

    @Override
    public int getItemCount() { return requestList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActiveReqId, tvActiveReqStatus, tvActiveHospitalName, tvActiveOrganBlood;
        TextView tvActiveQtyRequested, tvActiveQtyFulfilled, tvActiveQtyRemaining, tvActiveDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActiveReqId = itemView.findViewById(R.id.tvActiveReqId);
            tvActiveReqStatus = itemView.findViewById(R.id.tvActiveReqStatus);
            tvActiveHospitalName = itemView.findViewById(R.id.tvActiveHospitalName);
            tvActiveOrganBlood = itemView.findViewById(R.id.tvActiveOrganBlood);
            tvActiveQtyRequested = itemView.findViewById(R.id.tvActiveQtyRequested);
            tvActiveQtyFulfilled = itemView.findViewById(R.id.tvActiveQtyFulfilled);
            tvActiveQtyRemaining = itemView.findViewById(R.id.tvActiveQtyRemaining);
            tvActiveDates = itemView.findViewById(R.id.tvActiveDates);
        }
    }
}
