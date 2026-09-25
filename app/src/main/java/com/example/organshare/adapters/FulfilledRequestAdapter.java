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

public class FulfilledRequestAdapter extends RecyclerView.Adapter<FulfilledRequestAdapter.ViewHolder> {
    private final Context context;
    private final List<OrganRequest> requestList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener { void onItemClick(OrganRequest request); }

    public FulfilledRequestAdapter(Context context, List<OrganRequest> requestList, OnItemClickListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_fulfilled_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrganRequest req = requestList.get(position);
        holder.tvFulfilledReqId.setText(req.getRequestId());
        holder.tvFulfilledBadge.setText("FULFILLED");
        holder.tvFulfilledHospital.setText(req.getHospitalName());
        holder.tvFulfilledOrganInfo.setText("Organ: " + req.getOrganRequired() + " (Blood: " + req.getBloodGroup() + ") | Qty: " + req.getFulfilledQuantity() + "/" + req.getRequestedQuantity());
        holder.tvFulfilledDates.setText("Order ID: " + (req.getAllocatedOrderId() != null ? req.getAllocatedOrderId() : "ORD-COMPLETED") + " | Fulfillment: " + (req.getFulfilledAt() != null ? req.getFulfilledAt() : "Completed"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(req);
        });
    }

    @Override
    public int getItemCount() { return requestList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFulfilledReqId, tvFulfilledBadge, tvFulfilledHospital, tvFulfilledOrganInfo, tvFulfilledDates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFulfilledReqId = itemView.findViewById(R.id.tvFulfilledReqId);
            tvFulfilledBadge = itemView.findViewById(R.id.tvFulfilledBadge);
            tvFulfilledHospital = itemView.findViewById(R.id.tvFulfilledHospital);
            tvFulfilledOrganInfo = itemView.findViewById(R.id.tvFulfilledOrganInfo);
            tvFulfilledDates = itemView.findViewById(R.id.tvFulfilledDates);
        }
    }
}
