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
import com.example.organshare.models.emergency.ResourceHistory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceHistoryAdapter extends RecyclerView.Adapter<ResourceHistoryAdapter.ViewHolder> {

    private final Context context;
    private final List<ResourceHistory> historyList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());

    public ResourceHistoryAdapter(List<ResourceHistory> historyList) {
        this.context = null;
        this.historyList = historyList != null ? historyList : new ArrayList<>();
    }

    public ResourceHistoryAdapter(Context context, List<ResourceHistory> historyList) {
        this.context = context;
        this.historyList = historyList != null ? historyList : new ArrayList<>();
    }

    public void setHistoryList(@Nullable List<ResourceHistory> list) {
        this.historyList.clear();
        if (list != null) {
            this.historyList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context ctx = context != null ? context : parent.getContext();
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_resource_history_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResourceHistory item = historyList.get(position);

        holder.tvHistoryResourceName.setText(item.getResourceName() != null ? item.getResourceName() : "Resource Change");
        holder.tvHistoryChangeValues.setText(item.getPreviousValue() + " → " + item.getNewValue());
        holder.tvHistoryNotes.setText(item.getNotes() != null ? item.getNotes() : (item.getDestination() != null ? "Destination: " + item.getDestination() : "No extra details"));

        String timeStr = item.getTimestamp() != null ? dateFormat.format(item.getTimestamp()) : "Recently";
        holder.tvHistoryPerformedBy.setText("👤 " + (item.getPerformedBy() != null ? item.getPerformedBy() : "Coordinator") + " | " + timeStr);

        String act = item.getAction() != null ? item.getAction().toUpperCase() : "UPDATED";
        holder.tvHistoryActionBadge.setText(act);

        if (act.contains("ALLOCATED") || act.contains("DEDUCTED")) {
            holder.tvHistoryActionBadge.setBackgroundResource(R.drawable.bg_badge_critical);
            holder.tvHistoryActionBadge.setTextColor(Color.parseColor("#991B1B"));
        } else if (act.contains("STATUS") || act.contains("ASSIGNED")) {
            holder.tvHistoryActionBadge.setBackgroundResource(R.drawable.bg_badge_warning);
            holder.tvHistoryActionBadge.setTextColor(Color.parseColor("#92400E"));
        } else {
            holder.tvHistoryActionBadge.setBackgroundResource(R.drawable.bg_badge_normal);
            holder.tvHistoryActionBadge.setTextColor(Color.parseColor("#166534"));
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryResourceName, tvHistoryActionBadge, tvHistoryChangeValues, tvHistoryNotes, tvHistoryPerformedBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryResourceName = itemView.findViewById(R.id.tvHistoryResourceName);
            tvHistoryActionBadge = itemView.findViewById(R.id.tvHistoryActionBadge);
            tvHistoryChangeValues = itemView.findViewById(R.id.tvHistoryChangeValues);
            tvHistoryNotes = itemView.findViewById(R.id.tvHistoryNotes);
            tvHistoryPerformedBy = itemView.findViewById(R.id.tvHistoryPerformedBy);
        }
    }
}
