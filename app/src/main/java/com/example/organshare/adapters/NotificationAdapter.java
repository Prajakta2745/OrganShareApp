package com.example.organshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.AppNotification;
import com.example.organshare.utils.DateTimeUtils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<AppNotification> notificationList;

    public NotificationAdapter(Context context, List<AppNotification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification notif = notificationList.get(position);

        holder.tvNotifTitle.setText(notif.getTitle());
        holder.tvNotifMessage.setText(notif.getMessage());
        holder.tvNotifTime.setText(DateTimeUtils.formatDateTime(notif.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifTitle, tvNotifTime, tvNotifMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
        }
    }
}
