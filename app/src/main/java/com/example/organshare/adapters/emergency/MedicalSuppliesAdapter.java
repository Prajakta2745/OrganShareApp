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
import com.example.organshare.models.emergency.MedicalSupplyResource;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class MedicalSuppliesAdapter extends RecyclerView.Adapter<MedicalSuppliesAdapter.ViewHolder> {

    private final Context context;
    private final List<MedicalSupplyResource> supplyList;
    private final OnSupplyActionListener actionListener;
    private final OnSupplyItemClickListener clickListener;

    public interface OnSupplyActionListener {
        void onEditSupply(MedicalSupplyResource item);
    }

    public interface OnSupplyItemClickListener {
        void onUpdateSupplyClicked(MedicalSupplyResource item);
    }

    public MedicalSuppliesAdapter(List<MedicalSupplyResource> supplyList, OnSupplyItemClickListener listener) {
        this.context = null;
        this.supplyList = supplyList != null ? supplyList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public MedicalSuppliesAdapter(Context context, List<MedicalSupplyResource> supplyList, OnSupplyItemClickListener listener) {
        this.context = context;
        this.supplyList = supplyList != null ? supplyList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public MedicalSuppliesAdapter(Context context, List<MedicalSupplyResource> supplyList, OnSupplyActionListener listener) {
        this.context = context;
        this.supplyList = supplyList != null ? supplyList : new ArrayList<>();
        this.actionListener = listener;
        this.clickListener = null;
    }

    public void setSuppliesList(@Nullable List<MedicalSupplyResource> list) {
        this.supplyList.clear();
        if (list != null) {
            this.supplyList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_medical_supply_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MedicalSupplyResource item = supplyList.get(position);

        holder.tvSupplyName.setText(item.getSupplyName() != null ? item.getSupplyName() : "Medical Item");
        holder.tvSupplyCategory.setText("Category: " + (item.getCategory() != null ? item.getCategory() : "GENERAL"));
        holder.tvSupplyStorage.setText("📍 " + (item.getStorageLocation() != null ? item.getStorageLocation() : "Central Vault"));
        holder.tvSupplyQuantityText.setText("Available: " + item.getAvailableQuantity() + " " + (item.getUnit() != null ? item.getUnit() : "Units") + " (Min: " + item.getMinimumRequiredQuantity() + ")");

        String st = item.getStatus() != null ? item.getStatus().toUpperCase() : "NORMAL";
        holder.tvSupplyStatusBadge.setText(st);

        if ("OUT_OF_STOCK".equalsIgnoreCase(st) || "CRITICAL".equalsIgnoreCase(st) || item.getAvailableQuantity() == 0) {
            holder.tvSupplyStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvSupplyStatusBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("LOW".equalsIgnoreCase(st)) {
            holder.tvSupplyStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvSupplyStatusBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvSupplyStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvSupplyStatusBadge.setTextColor(Color.parseColor("#166534"));
        }

        holder.btnEditSupply.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateSupplyClicked(item);
            }
            if (actionListener != null) {
                actionListener.onEditSupply(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateSupplyClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return supplyList != null ? supplyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSupplyName, tvSupplyCategory, tvSupplyStorage, tvSupplyQuantityText, tvSupplyStatusBadge;
        MaterialButton btnEditSupply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSupplyName = itemView.findViewById(R.id.tvSupplyName);
            tvSupplyCategory = itemView.findViewById(R.id.tvSupplyCategory);
            tvSupplyStorage = itemView.findViewById(R.id.tvSupplyStorage);
            tvSupplyQuantityText = itemView.findViewById(R.id.tvSupplyQuantityText);
            tvSupplyStatusBadge = itemView.findViewById(R.id.tvSupplyStatusBadge);
            btnEditSupply = itemView.findViewById(R.id.btnEditSupply);
        }
    }
}
