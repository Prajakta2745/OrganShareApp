package com.example.organshare.activities.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.models.OrganRequest;
import com.google.android.material.button.MaterialButton;

public class RequestDetailsActivity extends AppCompatActivity {

    private TextView tvDetailRequestId, tvDetailStatus, tvDetailOrganAndBlood, tvDetailHospital, tvDetailPatientRef, tvDetailRequiredTime, tvDetailNotes;
    private MaterialButton btnTrackFromRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        tvDetailRequestId = findViewById(R.id.tvDetailRequestId);
        tvDetailStatus = findViewById(R.id.tvDetailStatus);
        tvDetailOrganAndBlood = findViewById(R.id.tvDetailOrganAndBlood);
        tvDetailHospital = findViewById(R.id.tvDetailHospital);
        tvDetailPatientRef = findViewById(R.id.tvDetailPatientRef);
        tvDetailRequiredTime = findViewById(R.id.tvDetailRequiredTime);
        tvDetailNotes = findViewById(R.id.tvDetailNotes);
        btnTrackFromRequest = findViewById(R.id.btnTrackFromRequest);

        OrganRequest request = (OrganRequest) getIntent().getSerializableExtra("REQUEST_DATA");
        if (request != null) {
            tvDetailRequestId.setText(request.getRequestId());
            tvDetailStatus.setText("STATUS: " + request.getStatus());
            tvDetailOrganAndBlood.setText(request.getOrganRequired() + " (Blood Group: " + request.getBloodGroup() + ")");
            tvDetailHospital.setText("Hospital: " + request.getHospitalName());
            tvDetailPatientRef.setText("Patient Case ID: " + (request.getPatientRefId() != null ? request.getPatientRefId() : "N/A"));
            tvDetailRequiredTime.setText("Required By: " + request.getRequiredDate() + " (" + request.getRequiredTime() + ")");
            tvDetailNotes.setText("Notes: " + (request.getAdditionalNotes() != null ? request.getAdditionalNotes() : "Standard transplant requisition."));

            if (request.getAllocatedOrderId() != null) {
                btnTrackFromRequest.setVisibility(View.VISIBLE);
                btnTrackFromRequest.setOnClickListener(v -> {
                    Intent intent = new Intent(RequestDetailsActivity.this, TrackDeliveryMapActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}
