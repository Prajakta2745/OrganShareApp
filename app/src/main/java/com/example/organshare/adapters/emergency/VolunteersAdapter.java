package com.example.organshare.adapters.emergency;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.emergency.Volunteer;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class VolunteersAdapter extends RecyclerView.Adapter<VolunteersAdapter.ViewHolder> {

    private final Context context;
    private final List<Volunteer> volunteerList;
    private final OnVolunteerActionListener actionListener;
    private final OnVolunteerItemClickListener clickListener;

    public interface OnVolunteerActionListener {
        void onAssignTask(Volunteer item);
    }

    public interface OnVolunteerItemClickListener {
        void onToggleActiveClicked(Volunteer item);
    }

    public VolunteersAdapter(List<Volunteer> volunteerList, OnVolunteerItemClickListener listener) {
        this.context = null;
        this.volunteerList = volunteerList != null ? volunteerList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public VolunteersAdapter(Context context, List<Volunteer> volunteerList, OnVolunteerItemClickListener listener) {
        this.context = context;
        this.volunteerList = volunteerList != null ? volunteerList : new ArrayList<>();
        this.clickListener = listener;
        this.actionListener = null;
    }

    public VolunteersAdapter(Context context, List<Volunteer> volunteerList, OnVolunteerActionListener listener) {
        this.context = context;
        this.volunteerList = volunteerList != null ? volunteerList : new ArrayList<>();
        this.actionListener = listener;
        this.clickListener = null;
    }

    public void setVolunteerList(@Nullable List<Volunteer> list) {
        this.volunteerList.clear();
        if (list != null) {
            this.volunteerList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_volunteer_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Volunteer item = volunteerList.get(position);

        holder.tvVolunteerName.setText(item.getName() != null ? item.getName() : "Volunteer");
        holder.tvVolunteerPhone.setText("📞 " + (item.getPhone() != null ? item.getPhone() : "--"));
        holder.tvVolunteerLocation.setText("📍 Location: " + (item.getLocation() != null ? item.getLocation() : "City Wide"));

        String skills = item.getSkills() != null && !item.getSkills().isEmpty() ? TextUtils.join(", ", item.getSkills()) : "Emergency Response";
        holder.tvVolunteerSkills.setText("🏷️ Skills: " + skills);

        if (item.getCurrentAssignment() != null && !item.getCurrentAssignment().isEmpty()) {
            holder.tvVolunteerAssignment.setText("📋 Task: " + item.getCurrentAssignment());
        } else {
            holder.tvVolunteerAssignment.setText("📋 Task: Ready for Deployment");
        }

        String st = item.getAvailabilityStatus() != null ? item.getAvailabilityStatus().toUpperCase() : "AVAILABLE";
        holder.tvVolunteerStatusBadge.setText(st);

        if ("OFFLINE".equalsIgnoreCase(st) || "UNAVAILABLE".equalsIgnoreCase(st)) {
            holder.tvVolunteerStatusBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvVolunteerStatusBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if ("ASSIGNED".equalsIgnoreCase(st)) {
            holder.tvVolunteerStatusBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvVolunteerStatusBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvVolunteerStatusBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvVolunteerStatusBadge.setTextColor(Color.parseColor("#166534"));
        }

        holder.btnAssignVolunteer.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onToggleActiveClicked(item);
            }
            if (actionListener != null) {
                actionListener.onAssignTask(item);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onToggleActiveClicked(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return volunteerList != null ? volunteerList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVolunteerName, tvVolunteerPhone, tvVolunteerStatusBadge, tvVolunteerSkills, tvVolunteerLocation, tvVolunteerAssignment;
        MaterialButton btnAssignVolunteer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVolunteerName = itemView.findViewById(R.id.tvVolunteerName);
            tvVolunteerPhone = itemView.findViewById(R.id.tvVolunteerPhone);
            tvVolunteerStatusBadge = itemView.findViewById(R.id.tvVolunteerStatusBadge);
            tvVolunteerSkills = itemView.findViewById(R.id.tvVolunteerSkills);
            tvVolunteerLocation = itemView.findViewById(R.id.tvVolunteerLocation);
            tvVolunteerAssignment = itemView.findViewById(R.id.tvVolunteerAssignment);
            btnAssignVolunteer = itemView.findViewById(R.id.btnAssignVolunteer);
        }
    }
}
