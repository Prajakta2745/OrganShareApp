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
import com.example.organshare.models.emergency.BloodResource;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class BloodResourcesAdapter extends RecyclerView.Adapter<BloodResourcesAdapter.ViewHolder> {

    private final Context context;
    private final List<BloodResource> bloodList;
    private final OnBloodActionListener actionListener;
    private final OnBloodItemClickListener clickListener;

    public interface OnBloodActionListener {
        void onAddUnits(BloodResource item);
    }

    public interface OnBloodItemClickListener {
        void onUpdateBloodClicked(BloodResource item);
    }

    public BloodResourcesAdapter(List<BloodResource> bloodList, OnBloodItemClickListener listener) {
        this.context = null;
        this.bloodList = bloodList != null ? bloodList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public BloodResourcesAdapter(Context context, List<BloodResource> bloodList, OnBloodItemClickListener listener) {
        this.context = context;
        this.bloodList = bloodList != null ? bloodList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public BloodResourcesAdapter(Context context, List<BloodResource> bloodList, OnBloodActionListener listener) {
        this.context = context;
        this.bloodList = bloodList != null ? bloodList : new ArrayList<>();
        this.actionListener = listener;
        this.clickListener = null;
    }

    public void setBloodList(@Nullable List<BloodResource> list) {
        this.bloodList.clear();
        if (list != null) {
            this.bloodList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_blood_resource_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodResource item = bloodList.get(position);

        holder.tvBloodGroupBadge.setText(item.getBloodGroup() != null ? item.getBloodGroup() : "O+");
        holder.tvBloodHospitalName.setText(item.getHospitalName() != null ? item.getHospitalName() : "Hospital Blood Vault");
        holder.tvBloodUnitsAvailable.setText("Available: " + item.getAvailableUnits() + " units (Min Req: " + item.getMinimumRequiredUnits() + ")");

        if ("OUT_OF_STOCK".equalsIgnoreCase(item.getStatus()) || item.getAvailableUnits() == 0) {
            holder.tvBloodStatusText.setText("Status: OUT OF STOCK");
            holder.tvBloodStatusText.setTextColor(Color.parseColor("#EF4444"));
        } else if ("CRITICAL".equalsIgnoreCase(item.getStatus())) {
            holder.tvBloodStatusText.setText("Status: CRITICAL STOCK");
            holder.tvBloodStatusText.setTextColor(Color.parseColor("#EF4444"));
        } else if ("LOW".equalsIgnoreCase(item.getStatus())) {
            holder.tvBloodStatusText.setText("Status: LOW STOCK");
            holder.tvBloodStatusText.setTextColor(Color.parseColor("#F59E0B"));
        } else {
            holder.tvBloodStatusText.setText("Status: NORMAL / VERIFIED");
            holder.tvBloodStatusText.setTextColor(Color.parseColor("#166534"));
        }

        holder.btnAddBloodUnits.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateBloodClicked(item);
            }
            if (actionListener != null) {
                actionListener.onAddUnits(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateBloodClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bloodList != null ? bloodList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBloodGroupBadge, tvBloodHospitalName, tvBloodUnitsAvailable, tvBloodStatusText;
        MaterialButton btnAddBloodUnits;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBloodGroupBadge = itemView.findViewById(R.id.tvBloodGroupBadge);
            tvBloodHospitalName = itemView.findViewById(R.id.tvBloodHospitalName);
            tvBloodUnitsAvailable = itemView.findViewById(R.id.tvBloodUnitsAvailable);
            tvBloodStatusText = itemView.findViewById(R.id.tvBloodStatusText);
            btnAddBloodUnits = itemView.findViewById(R.id.btnAddBloodUnits);
        }
    }
}
