package com.example.organshare.adapters.emergency;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.emergency.HospitalResourceOverview;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class DonorAvailableHospitalsAdapter extends RecyclerView.Adapter<DonorAvailableHospitalsAdapter.ViewHolder> {

    private final Context context;
    private final List<HospitalResourceOverview> hospitalList;
    private final OnHospitalSelectedListener listener;

    public interface OnHospitalSelectedListener {
        void onHospitalSelected(HospitalResourceOverview hospital);
    }

    public DonorAvailableHospitalsAdapter(Context context, List<HospitalResourceOverview> hospitalList, OnHospitalSelectedListener listener) {
        this.context = context;
        this.hospitalList = hospitalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor_available_hospital_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HospitalResourceOverview item = hospitalList.get(position);

        holder.tvHospitalName.setText(item.getHospitalName() != null ? item.getHospitalName() : "Hospital");
        holder.tvHospitalLocation.setText("📍 " + (item.getLocation() != null ? item.getLocation() : "Location"));
        holder.tvAvailableBedsCount.setText(String.valueOf(item.getAvailableBeds()));
        holder.tvICUBedsCount.setText(String.valueOf(item.getAvailableICUBeds()));
        holder.tvEmergencyBedsCount.setText(String.valueOf(item.getAvailableEmergencyBeds()));

        holder.tvHospitalStatusBadge.setText(item.getHospitalStatus() != null ? item.getHospitalStatus().toUpperCase() : "OPEN");

        // Status badge colors
        if ("CLOSED".equalsIgnoreCase(item.getHospitalStatus())) {
            holder.tvHospitalStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvHospitalStatusBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("LOW_CAPACITY".equalsIgnoreCase(item.getHospitalStatus()) || "BUSY".equalsIgnoreCase(item.getHospitalStatus())) {
            holder.tvHospitalStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvHospitalStatusBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvHospitalStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvHospitalStatusBadge.setTextColor(Color.parseColor("#166534"));
        }

        // Blood Status
        if ("CRITICAL".equalsIgnoreCase(item.getoPlusBloodStatus()) || "OUT_OF_STOCK".equalsIgnoreCase(item.getoPlusBloodStatus())) {
            holder.tvBloodStockIndicator.setText("🩸 O+ Blood: CRITICAL");
            holder.tvBloodStockIndicator.setTextColor(Color.parseColor("#EF4444"));
        } else if ("LOW".equalsIgnoreCase(item.getoPlusBloodStatus())) {
            holder.tvBloodStockIndicator.setText("🩸 O+ Blood: LOW");
            holder.tvBloodStockIndicator.setTextColor(Color.parseColor("#F59E0B"));
        } else {
            holder.tvBloodStockIndicator.setText("🩸 O+ Blood: AVAILABLE");
            holder.tvBloodStockIndicator.setTextColor(Color.parseColor("#166534"));
        }

        // Transport Status
        if ("AVAILABLE".equalsIgnoreCase(item.getTransportStatus())) {
            holder.tvTransportIndicator.setText("🚑 Transport: AVAILABLE");
            holder.tvTransportIndicator.setTextColor(Color.parseColor("#166534"));
        } else {
            holder.tvTransportIndicator.setText("🚑 Transport: NOT AVAILABLE");
            holder.tvTransportIndicator.setTextColor(Color.parseColor("#64748B"));
        }

        holder.btnSelectHospital.setOnClickListener(v -> {
            if (listener != null) listener.onHospitalSelected(item);
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList != null ? hospitalList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHospitalName, tvHospitalLocation, tvHospitalStatusBadge;
        TextView tvAvailableBedsCount, tvICUBedsCount, tvEmergencyBedsCount;
        TextView tvBloodStockIndicator, tvTransportIndicator;
        MaterialButton btnSelectHospital;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHospitalName = itemView.findViewById(R.id.tvHospitalName);
            tvHospitalLocation = itemView.findViewById(R.id.tvHospitalLocation);
            tvHospitalStatusBadge = itemView.findViewById(R.id.tvHospitalStatusBadge);
            tvAvailableBedsCount = itemView.findViewById(R.id.tvAvailableBedsCount);
            tvICUBedsCount = itemView.findViewById(R.id.tvICUBedsCount);
            tvEmergencyBedsCount = itemView.findViewById(R.id.tvEmergencyBedsCount);
            tvBloodStockIndicator = itemView.findViewById(R.id.tvBloodStockIndicator);
            tvTransportIndicator = itemView.findViewById(R.id.tvTransportIndicator);
            btnSelectHospital = itemView.findViewById(R.id.btnSelectHospital);
        }
    }
}
