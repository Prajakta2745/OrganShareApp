package com.example.organshare.activities.delivery;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.CheckpointAdapter;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.repositories.DeliveryRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private TextView tvDelivDetailId, tvDelivDetailStatus, tvDelivOrganInfo, tvDelivPickupAddress, tvDelivDestAddress, tvDelivEstArrival;
    private MaterialButton btnAddCheckpointBtn, btnReportDelayBtn, btnMarkDeliveredBtn;
    private RecyclerView rvCheckpointTimeline;
    private CheckpointAdapter adapter;
    private DeliveryRepository repository;
    private DeliveryModel currentDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        repository = new DeliveryRepository();
        currentDelivery = (DeliveryModel) getIntent().getSerializableExtra("DELIVERY_DATA");

        initViews();
        setupListeners();
        setupRecyclerView();
    }

    private void initViews() {
        tvDelivDetailId = findViewById(R.id.tvDelivDetailId);
        tvDelivDetailStatus = findViewById(R.id.tvDelivDetailStatus);
        tvDelivOrganInfo = findViewById(R.id.tvDelivOrganInfo);
        tvDelivPickupAddress = findViewById(R.id.tvDelivPickupAddress);
        tvDelivDestAddress = findViewById(R.id.tvDelivDestAddress);
        tvDelivEstArrival = findViewById(R.id.tvDelivEstArrival);
        btnAddCheckpointBtn = findViewById(R.id.btnAddCheckpointBtn);
        btnReportDelayBtn = findViewById(R.id.btnReportDelayBtn);
        btnMarkDeliveredBtn = findViewById(R.id.btnMarkDeliveredBtn);
        rvCheckpointTimeline = findViewById(R.id.rvCheckpointTimeline);

        if (currentDelivery != null) {
            tvDelivDetailId.setText(currentDelivery.getDeliveryId());
            tvDelivDetailStatus.setText("STATUS: " + currentDelivery.getCurrentStatus());
            tvDelivOrganInfo.setText("Transporting: " + currentDelivery.getOrganType() + " (" + currentDelivery.getBloodGroup() + ")");
            tvDelivPickupAddress.setText("Pickup: " + currentDelivery.getPickupAddress());
            tvDelivDestAddress.setText("Destination: " + currentDelivery.getDestinationHospitalName());
            tvDelivEstArrival.setText("Est. Arrival: " + (currentDelivery.getEstimatedArrival() != null ? currentDelivery.getEstimatedArrival() : "In Transit"));
        }
    }

    private void setupListeners() {
        btnAddCheckpointBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DeliveryDetailsActivity.this, UpdateCheckpointActivity.class);
            intent.putExtra("DELIVERY_DATA", currentDelivery);
            startActivity(intent);
        });

        btnReportDelayBtn.setOnClickListener(v -> showReportDelayDialog());

        btnMarkDeliveredBtn.setOnClickListener(v -> {
            if (currentDelivery == null) return;
            repository.markDelivered(currentDelivery.getDeliveryId(), currentDelivery.getOrderId(), currentDelivery.getRequestId(), new DeliveryRepository.DataCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Toast.makeText(DeliveryDetailsActivity.this, "Delivery marked as successfully completed!", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onFailure(String error) {
                    Toast.makeText(DeliveryDetailsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupRecyclerView() {
        rvCheckpointTimeline.setLayoutManager(new LinearLayoutManager(this));
        if (currentDelivery != null && currentDelivery.getCheckpoints() != null) {
            adapter = new CheckpointAdapter(this, currentDelivery.getCheckpoints());
            rvCheckpointTimeline.setAdapter(adapter);
        }
    }

    private void showReportDelayDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_report_delay);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextInputEditText etDelayReason = dialog.findViewById(R.id.etDelayReason);
        MaterialButton btnCancelDelay = dialog.findViewById(R.id.btnCancelDelay);
        MaterialButton btnSubmitDelay = dialog.findViewById(R.id.btnSubmitDelay);

        btnCancelDelay.setOnClickListener(v -> dialog.dismiss());

        btnSubmitDelay.setOnClickListener(v -> {
            String reason = etDelayReason.getText() != null ? etDelayReason.getText().toString().trim() : "Road congestion / Weather delay";
            if (reason.isEmpty()) reason = "Unforeseen traffic delay on green corridor";

            if (currentDelivery != null) {
                repository.reportDelay(currentDelivery.getDeliveryId(), reason, new DeliveryRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Delay alert broadcasted to hospital & coordinator.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(DeliveryDetailsActivity.this, "Error reporting delay: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        dialog.show();
    }
}
