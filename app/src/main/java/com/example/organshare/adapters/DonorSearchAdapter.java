package com.example.organshare.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.DonorProfile;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class DonorSearchAdapter extends RecyclerView.Adapter<DonorSearchAdapter.ViewHolder> {

    public interface OnDonorActionListener {
        void onRequestContact(DonorProfile donor);
        void onMarkCollected(DonorProfile donor);
    }

    private final Context context;
    private final List<DonorProfile> donorList;
    private final OnDonorActionListener listener;

    public DonorSearchAdapter(Context context, List<DonorProfile> donorList, OnDonorActionListener listener) {
        this.context = context;
        this.donorList = donorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donor_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonorProfile donor = donorList.get(position);

        holder.tvDonorMaskedName.setText(donor.getMaskedName());
        holder.tvBloodBadge.setText("Blood: " + (donor.getBloodGroup() != null ? donor.getBloodGroup() : "N/A"));
        holder.tvDonorIdAndGender.setText("Donor ID: " + donor.getDonorId() + " | Gender: " + (donor.getGender() != null ? donor.getGender() : "N/A"));
        holder.tvLocation.setText("Location: " + (donor.getCity() != null ? donor.getCity() : "") + ", " + (donor.getState() != null ? donor.getState() : ""));

        if (donor.getOrgansWillingToDonate() != null && !donor.getOrgansWillingToDonate().isEmpty()) {
            holder.tvPledgedOrgans.setText("Pledged Organs: " + TextUtils.join(", ", donor.getOrgansWillingToDonate()));
        } else {
            holder.tvPledgedOrgans.setText("Pledged Organs: All Viable Organs");
        }

        holder.btnContactCoordinator.setOnClickListener(v -> {
            if (listener != null) listener.onRequestContact(donor);
        });

        holder.btnMarkCollected.setOnClickListener(v -> {
            if (listener != null) listener.onMarkCollected(donor);
        });
    }

    @Override
    public int getItemCount() {
        return donorList != null ? donorList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDonorMaskedName, tvBloodBadge, tvDonorIdAndGender, tvLocation, tvPledgedOrgans;
        MaterialButton btnContactCoordinator, btnMarkCollected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDonorMaskedName = itemView.findViewById(R.id.tvDonorMaskedName);
            tvBloodBadge = itemView.findViewById(R.id.tvBloodBadge);
            tvDonorIdAndGender = itemView.findViewById(R.id.tvDonorIdAndGender);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvPledgedOrgans = itemView.findViewById(R.id.tvPledgedOrgans);
            btnContactCoordinator = itemView.findViewById(R.id.btnContactCoordinator);
            btnMarkCollected = itemView.findViewById(R.id.btnMarkCollected);
        }
    }
}
