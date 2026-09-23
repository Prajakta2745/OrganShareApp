package com.example.organshare.activities.hospital;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.maps.MapTrackingHelper;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.repositories.DeliveryRepository;
import com.example.organshare.utils.Constants;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.button.MaterialButton;

public class TrackDeliveryMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TextView tvMapDeliveryId, tvMapStatus, tvOrganInTransit, tvEstimatedArrival, tvDelayReasonText;
    private LinearLayout layoutDelayNotice;
    private MaterialButton btnCallCourier, btnCallCoordinator;
    private DeliveryRepository deliveryRepository;
    private DeliveryModel currentDelivery;
    private String deliveryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_delivery_map);

        deliveryRepository = new DeliveryRepository();
        deliveryId = getIntent().getStringExtra("DELIVERY_ID");
        if (deliveryId == null || deliveryId.isEmpty()) {
            deliveryId = "DEL-2026-9001"; // Fallback to demo delivery
        }

        initViews();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void initViews() {
        tvMapDeliveryId = findViewById(R.id.tvMapDeliveryId);
        tvMapStatus = findViewById(R.id.tvMapStatus);
        tvOrganInTransit = findViewById(R.id.tvOrganInTransit);
        tvEstimatedArrival = findViewById(R.id.tvEstimatedArrival);
        tvDelayReasonText = findViewById(R.id.tvDelayReasonText);
        layoutDelayNotice = findViewById(R.id.layoutDelayNotice);
        btnCallCourier = findViewById(R.id.btnCallCourier);
        btnCallCoordinator = findViewById(R.id.btnCallCoordinator);

        btnCallCourier.setOnClickListener(v -> {
            if (currentDelivery != null && currentDelivery.getDeliveryPersonPhone() != null) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + currentDelivery.getDeliveryPersonPhone()));
                startActivity(callIntent);
            } else {
                Toast.makeText(this, "Courier contact not available", Toast.LENGTH_SHORT).show();
            }
        });

        btnCallCoordinator.setOnClickListener(v -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+1-555-400-9999"));
            startActivity(callIntent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        loadDeliveryData();
    }

    private void loadDeliveryData() {
        deliveryRepository.getDeliveryById(deliveryId, new DeliveryRepository.DataCallback<DeliveryModel>() {
            @Override
            public void onSuccess(DeliveryModel delivery) {
                currentDelivery = delivery;
                updateUI(delivery);
                if (mMap != null) {
                    MapTrackingHelper.displayDeliveryRoute(mMap, delivery);
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(TrackDeliveryMapActivity.this, "Delivery info: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(DeliveryModel delivery) {
        tvMapDeliveryId.setText(delivery.getDeliveryId());
        tvMapStatus.setText(delivery.getCurrentStatus());
        tvOrganInTransit.setText("Transporting: " + delivery.getOrganType() + " (" + delivery.getBloodGroup() + ")");
        tvEstimatedArrival.setText("Estimated Arrival: " + (delivery.getEstimatedArrival() != null ? delivery.getEstimatedArrival() : "In Transit"));

        if (Constants.DEL_DELAYED.equalsIgnoreCase(delivery.getCurrentStatus())) {
            layoutDelayNotice.setVisibility(View.VISIBLE);
            tvDelayReasonText.setText("Reason: " + (delivery.getDelayReason() != null ? delivery.getDelayReason() : "Unforeseen transit delay. Contact coordinator for updates."));
        } else {
            layoutDelayNotice.setVisibility(View.GONE);
        }
    }
}
