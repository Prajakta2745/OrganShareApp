package com.example.organshare.activities.hospital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.models.OrderModel;
import com.google.android.material.button.MaterialButton;

public class HospitalOrderDetailsActivity extends AppCompatActivity {

    private TextView tvOrderHeaderId, tvOrderStatusBadge, tvOrderOrganInfo, tvOrderBankInfo, tvOrderHospitalInfo, tvCourierContactInfo;
    private MaterialButton btnOpenMapTracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_order_details);

        tvOrderHeaderId = findViewById(R.id.tvOrderHeaderId);
        tvOrderStatusBadge = findViewById(R.id.tvOrderStatusBadge);
        tvOrderOrganInfo = findViewById(R.id.tvOrderOrganInfo);
        tvOrderBankInfo = findViewById(R.id.tvOrderBankInfo);
        tvOrderHospitalInfo = findViewById(R.id.tvOrderHospitalInfo);
        tvCourierContactInfo = findViewById(R.id.tvCourierContactInfo);
        btnOpenMapTracking = findViewById(R.id.btnOpenMapTracking);

        OrderModel order = (OrderModel) getIntent().getSerializableExtra("ORDER_DATA");
        if (order != null) {
            tvOrderHeaderId.setText(order.getOrderId());
            tvOrderStatusBadge.setText("STATUS: " + order.getOrderStatus());
            tvOrderOrganInfo.setText(order.getOrganType() + " (Blood: " + order.getBloodGroup() + ")");
            tvOrderBankInfo.setText("Origin Bank: " + order.getOrganBankName());
            tvOrderHospitalInfo.setText("Destination: " + order.getHospitalName());
            tvCourierContactInfo.setText("Assigned Courier: " + (order.getDeliveryPersonName() != null ? order.getDeliveryPersonName() + " (" + order.getDeliveryPersonPhone() + ")" : "Assigning Courier..."));

            btnOpenMapTracking.setOnClickListener(v -> {
                Intent intent = new Intent(HospitalOrderDetailsActivity.this, TrackDeliveryMapActivity.class);
                intent.putExtra("DELIVERY_ID", order.getDeliveryId());
                intent.putExtra("ORDER_DATA", order);
                startActivity(intent);
            });
        }
    }
}
