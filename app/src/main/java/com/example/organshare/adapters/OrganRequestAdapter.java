package com.example.organshare.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organshare.R;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.utils.Constants;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OrganRequestAdapter
        extends RecyclerView.Adapter<OrganRequestAdapter.ViewHolder> {

    public interface OnRequestClickListener {

        void onViewDetails(OrganRequest request);

        void onAllocate(OrganRequest request);

        void onReject(OrganRequest request);
    }

    private final Context context;
    private final List<OrganRequest> requestList;
    private final boolean isBankView;
    private final OnRequestClickListener listener;

    public OrganRequestAdapter(
            Context context,
            List<OrganRequest> requestList,
            boolean isBankView,
            OnRequestClickListener listener
    ) {
        this.context = context;
        this.requestList = requestList;
        this.isBankView = isBankView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        int layoutId;

        if (isBankView) {
            layoutId = R.layout.item_bank_request;
        } else {
            layoutId = R.layout.item_hospital_request;
        }

        View view = LayoutInflater.from(context)
                .inflate(layoutId, parent, false);

        return new ViewHolder(view, isBankView);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        OrganRequest request = requestList.get(position);

        if (isBankView) {

            // -----------------------------
            // ORGAN BANK VIEW
            // -----------------------------

            holder.tvBankRequestId.setText(
                    request.getRequestId()
            );

            holder.tvBankEmergencyLevel.setText(
                    request.getEmergencyLevel()
            );

            holder.tvHospitalName.setText(
                    request.getHospitalName()
            );

            holder.tvOrganRequired.setText(
                    "Organ: "
                            + request.getOrganRequired()
                            + " ("
                            + request.getBloodGroup()
                            + ")"
            );

            holder.tvStatus.setText(
                    request.getStatus()
            );

            if (request.getCompatibilitySummary() != null
                    && !request.getCompatibilitySummary().isEmpty()) {

                holder.tvCompatibilityAid.setText(
                        "Matching Aid: "
                                + request.getCompatibilitySummary()
                );

            } else {

                holder.tvCompatibilityAid.setText(
                        "Matching Aid: Ready for Cross-Check"
                );
            }

            boolean isPending =
                    Constants.STATUS_PENDING.equalsIgnoreCase(
                            request.getStatus()
                    )
                            ||
                            Constants.STATUS_UNDER_REVIEW.equalsIgnoreCase(
                                    request.getStatus()
                            );

            holder.btnAllocate.setVisibility(
                    isPending ? View.VISIBLE : View.GONE
            );

            holder.btnReject.setVisibility(
                    isPending ? View.VISIBLE : View.GONE
            );

            holder.btnAllocate.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onAllocate(request);
                }
            });

            holder.btnReject.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onReject(request);
                }
            });

        } else {

            // -----------------------------
            // HOSPITAL VIEW
            // -----------------------------

            holder.tvRequestId.setText(
                    request.getRequestId()
            );

            holder.tvEmergencyLevel.setText(
                    request.getEmergencyLevel()
            );

            holder.tvOrganAndBlood.setText(
                    request.getOrganRequired()
                            + " (Blood: "
                            + request.getBloodGroup()
                            + ")"
            );

            holder.tvPatientRef.setText(
                    "Patient Ref: "
                            + (
                            request.getPatientRefId() != null
                                    ? request.getPatientRefId()
                                    : "N/A"
                    )
            );

            holder.tvRequiredDateTime.setText(
                    "Required By: "
                            + request.getRequiredDate()
                            + " ("
                            + request.getRequiredTime()
                            + ")"
            );

            holder.tvStatus.setText(
                    request.getStatus()
            );

            holder.btnViewDetails.setOnClickListener(v -> {

                if (listener != null) {
                    listener.onViewDetails(request);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return requestList != null
                ? requestList.size()
                : 0;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        // Hospital view
        TextView tvRequestId;
        TextView tvEmergencyLevel;
        TextView tvOrganAndBlood;
        TextView tvPatientRef;
        TextView tvRequiredDateTime;
        TextView tvStatus;

        MaterialButton btnViewDetails;

        // Organ bank view
        TextView tvBankRequestId;
        TextView tvBankEmergencyLevel;
        TextView tvHospitalName;
        TextView tvOrganRequired;
        TextView tvCompatibilityAid;

        MaterialButton btnReject;
        MaterialButton btnAllocate;

        public ViewHolder(
                @NonNull View itemView,
                boolean isBankView
        ) {

            super(itemView);

            if (isBankView) {

                tvBankRequestId =
                        itemView.findViewById(
                                R.id.tvBankRequestId
                        );

                tvBankEmergencyLevel =
                        itemView.findViewById(
                                R.id.tvBankEmergencyLevel
                        );

                tvHospitalName =
                        itemView.findViewById(
                                R.id.tvHospitalName
                        );

                tvOrganRequired =
                        itemView.findViewById(
                                R.id.tvOrganRequired
                        );

                tvStatus =
                        itemView.findViewById(
                                R.id.tvStatus
                        );

                tvCompatibilityAid =
                        itemView.findViewById(
                                R.id.tvCompatibilityAid
                        );

                btnReject =
                        itemView.findViewById(
                                R.id.btnReject
                        );

                btnAllocate =
                        itemView.findViewById(
                                R.id.btnAllocate
                        );

            } else {

                tvRequestId =
                        itemView.findViewById(
                                R.id.tvRequestId
                        );

                tvEmergencyLevel =
                        itemView.findViewById(
                                R.id.tvEmergencyLevel
                        );

                tvOrganAndBlood =
                        itemView.findViewById(
                                R.id.tvOrganAndBlood
                        );

                tvPatientRef =
                        itemView.findViewById(
                                R.id.tvPatientRef
                        );

                tvRequiredDateTime =
                        itemView.findViewById(
                                R.id.tvRequiredDateTime
                        );

                tvStatus =
                        itemView.findViewById(
                                R.id.tvStatus
                        );

                btnViewDetails =
                        itemView.findViewById(
                                R.id.btnViewDetails
                        );
            }
        }
    }
}