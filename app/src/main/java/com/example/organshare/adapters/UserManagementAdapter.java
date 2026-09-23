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
import com.example.organshare.models.UserModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.ViewHolder> {

    public interface OnUserActionListener {
        void onApprove(UserModel user);
        void onToggleSuspend(UserModel user);
    }

    private final Context context;
    private final List<UserModel> userList;
    private final OnUserActionListener listener;

    public UserManagementAdapter(Context context, List<UserModel> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_management, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = userList.get(position);

        holder.tvUserDisplayName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Unnamed User");
        holder.tvUserRoleBadge.setText(user.getRole());
        holder.tvUserEmail.setText(user.getEmail());

        if (user.isSuspended()) {
            holder.tvVerificationState.setText("Status: Suspended");
            holder.tvVerificationState.setTextColor(Color.parseColor("#DC2626"));
            holder.btnRejectOrSuspend.setText("Unsuspend");
        } else if (user.isVerified()) {
            holder.tvVerificationState.setText("Status: Active & Verified");
            holder.tvVerificationState.setTextColor(Color.parseColor("#16A34A"));
            holder.btnRejectOrSuspend.setText("Suspend");
        } else {
            holder.tvVerificationState.setText("Status: Pending Admin Review");
            holder.tvVerificationState.setTextColor(Color.parseColor("#D97706"));
            holder.btnRejectOrSuspend.setText("Reject");
        }

        holder.btnApproveUser.setVisibility(user.isVerified() ? View.GONE : View.VISIBLE);

        holder.btnApproveUser.setOnClickListener(v -> {
            if (listener != null) listener.onApprove(user);
        });

        holder.btnRejectOrSuspend.setOnClickListener(v -> {
            if (listener != null) listener.onToggleSuspend(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserDisplayName, tvUserRoleBadge, tvUserEmail, tvVerificationState;
        MaterialButton btnRejectOrSuspend, btnApproveUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserDisplayName = itemView.findViewById(R.id.tvUserDisplayName);
            tvUserRoleBadge = itemView.findViewById(R.id.tvUserRoleBadge);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvVerificationState = itemView.findViewById(R.id.tvVerificationState);
            btnRejectOrSuspend = itemView.findViewById(R.id.btnRejectOrSuspend);
            btnApproveUser = itemView.findViewById(R.id.btnApproveUser);
        }
    }
}
