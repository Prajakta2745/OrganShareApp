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
import com.example.organshare.models.OrganInventory;
import com.example.organshare.utils.Constants;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    public interface OnInventoryClickListener {
        void onInventorySelected(OrganInventory item);
    }

    private final Context context;
    private final List<OrganInventory> inventoryList;
    private final OnInventoryClickListener listener;

    public InventoryAdapter(Context context, List<OrganInventory> inventoryList, OnInventoryClickListener listener) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrganInventory item = inventoryList.get(position);

        holder.tvInventoryId.setText(item.getInventoryId());
        holder.tvAvailabilityStatus.setText(item.getAvailabilityStatus());
        holder.tvOrganAndGroup.setText(item.getOrganType() + " (Blood: " + item.getBloodGroup() + ")");
        holder.tvPreservationDetails.setText("Storage: " + (item.getStorageTemperature() != null ? item.getStorageTemperature() : "4°C Hypothermic") + " | " + (item.getStorageLocation() != null ? item.getStorageLocation() : "Cryo Vault"));
        holder.tvDonorMasked.setText("Source Donor: " + (item.getDonorMaskedName() != null ? item.getDonorMaskedName() : "Pledged Donor") + " (" + item.getDonorRefId() + ")");

        if (Constants.INV_AVAILABLE.equalsIgnoreCase(item.getAvailabilityStatus())) {
            holder.tvAvailabilityStatus.setTextColor(Color.parseColor("#16A34A"));
        } else if (Constants.INV_ALLOCATED.equalsIgnoreCase(item.getAvailabilityStatus()) || Constants.INV_IN_TRANSIT.equalsIgnoreCase(item.getAvailabilityStatus())) {
            holder.tvAvailabilityStatus.setTextColor(Color.parseColor("#2563EB"));
        } else {
            holder.tvAvailabilityStatus.setTextColor(Color.parseColor("#DC2626"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onInventorySelected(item);
        });
    }

    @Override
    public int getItemCount() {
        return inventoryList != null ? inventoryList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInventoryId, tvAvailabilityStatus, tvOrganAndGroup, tvPreservationDetails, tvDonorMasked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInventoryId = itemView.findViewById(R.id.tvInventoryId);
            tvAvailabilityStatus = itemView.findViewById(R.id.tvAvailabilityStatus);
            tvOrganAndGroup = itemView.findViewById(R.id.tvOrganAndGroup);
            tvPreservationDetails = itemView.findViewById(R.id.tvPreservationDetails);
            tvDonorMasked = itemView.findViewById(R.id.tvDonorMasked);
        }
    }
}
