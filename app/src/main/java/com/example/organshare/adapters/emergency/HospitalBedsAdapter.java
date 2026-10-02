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
import com.example.organshare.models.emergency.HospitalBedResource;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class HospitalBedsAdapter extends RecyclerView.Adapter<HospitalBedsAdapter.ViewHolder> {

    private final Context context;
    private final List<HospitalBedResource> bedList;
    private final OnBedUpdateListener updateListener;
    private final OnBedItemClickListener clickListener;

    public interface OnBedUpdateListener {
        void onUpdateBed(HospitalBedResource bed);
    }

    public interface OnBedItemClickListener {
        void onUpdateBedsClicked(HospitalBedResource item);
    }

    public HospitalBedsAdapter(List<HospitalBedResource> bedList, OnBedItemClickListener listener) {
        this.context = null;
        this.bedList = bedList != null ? bedList : new ArrayList<>();
        this.clickListener = listener;
        this.updateListener = null;
    }

    public HospitalBedsAdapter(Context context, List<HospitalBedResource> bedList, OnBedItemClickListener listener) {
        this.context = context;
        this.bedList = bedList != null ? bedList : new ArrayList<>();
        this.clickListener = listener;
        this.updateListener = null;
    }

    public HospitalBedsAdapter(Context context, List<HospitalBedResource> bedList, OnBedUpdateListener listener) {
        this.context = context;
        this.bedList = bedList != null ? bedList : new ArrayList<>();
        this.updateListener = listener;
        this.clickListener = null;
    }

    public void setBedsList(@Nullable List<HospitalBedResource> list) {
        this.bedList.clear();
        if (list != null) {
            this.bedList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_hospital_bed_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HospitalBedResource bed = bedList.get(position);

        holder.tvBedHospitalName.setText(bed.getHospitalName() != null ? bed.getHospitalName() : "Hospital");
        holder.tvBedLocation.setText("📍 " + (bed.getLocation() != null ? bed.getLocation() : "Location"));
        holder.tvTotalBeds.setText("Total Capacity: " + bed.getTotalBeds());
        holder.tvAvailableBeds.setText("Available: " + bed.getAvailableBeds());
        holder.tvOccupiedBeds.setText("Occupied: " + bed.getOccupiedBeds());
        holder.tvICUBeds.setText("ICU Avail: " + bed.getAvailableICUBeds() + " / " + bed.getTotalICUBeds());
        holder.tvEmergencyBeds.setText("ER Avail: " + bed.getAvailableEmergencyBeds() + " / " + bed.getTotalEmergencyBeds());

        holder.tvBedStatusBadge.setText(bed.getStatus() != null ? bed.getStatus().toUpperCase() : "AVAILABLE");

        if ("CLOSED".equalsIgnoreCase(bed.getStatus()) || "FULL".equalsIgnoreCase(bed.getStatus()) || bed.getAvailableBeds() == 0) {
            holder.tvBedStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvBedStatusBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("LOW_CAPACITY".equalsIgnoreCase(bed.getStatus()) || bed.getAvailableBeds() <= 5) {
            holder.tvBedStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvBedStatusBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvBedStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvBedStatusBadge.setTextColor(Color.parseColor("#166534"));
        }

        holder.btnUpdateBedInfo.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateBedsClicked(bed);
            }
            if (updateListener != null) {
                updateListener.onUpdateBed(bed);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onUpdateBedsClicked(bed);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bedList != null ? bedList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBedHospitalName, tvBedLocation, tvBedStatusBadge;
        TextView tvTotalBeds, tvAvailableBeds, tvOccupiedBeds, tvICUBeds, tvEmergencyBeds, tvBedLastUpdated;
        MaterialButton btnUpdateBedInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBedHospitalName = itemView.findViewById(R.id.tvBedHospitalName);
            tvBedLocation = itemView.findViewById(R.id.tvBedLocation);
            tvBedStatusBadge = itemView.findViewById(R.id.tvBedStatusBadge);
            tvTotalBeds = itemView.findViewById(R.id.tvTotalBeds);
            tvAvailableBeds = itemView.findViewById(R.id.tvAvailableBeds);
            tvOccupiedBeds = itemView.findViewById(R.id.tvOccupiedBeds);
            tvICUBeds = itemView.findViewById(R.id.tvICUBeds);
            tvEmergencyBeds = itemView.findViewById(R.id.tvEmergencyBeds);
            tvBedLastUpdated = itemView.findViewById(R.id.tvBedLastUpdated);
            btnUpdateBedInfo = itemView.findViewById(R.id.btnUpdateBedInfo);
        }
    }
}
