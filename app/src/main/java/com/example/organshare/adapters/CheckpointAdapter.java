package com.example.organshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.models.Checkpoint;

import java.util.List;

public class CheckpointAdapter extends RecyclerView.Adapter<CheckpointAdapter.ViewHolder> {

    private final Context context;
    private final List<Checkpoint> checkpointList;

    public CheckpointAdapter(Context context, List<Checkpoint> checkpointList) {
        this.context = context;
        this.checkpointList = checkpointList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_checkpoint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Checkpoint cp = checkpointList.get(position);

        holder.tvCpLocationName.setText(cp.getLocationName());
        holder.tvCpTimestamp.setText(cp.getTimestamp());
        holder.tvCpStatus.setText("Status: " + cp.getStatusAtCheckpoint());
        holder.tvCpNotes.setText("Notes: " + (cp.getNotes() != null ? cp.getNotes() : "Passed checkpoint without issue."));
    }

    @Override
    public int getItemCount() {
        return checkpointList != null ? checkpointList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCpLocationName, tvCpTimestamp, tvCpStatus, tvCpNotes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCpLocationName = itemView.findViewById(R.id.tvCpLocationName);
            tvCpTimestamp = itemView.findViewById(R.id.tvCpTimestamp);
            tvCpStatus = itemView.findViewById(R.id.tvCpStatus);
            tvCpNotes = itemView.findViewById(R.id.tvCpNotes);
        }
    }
}
