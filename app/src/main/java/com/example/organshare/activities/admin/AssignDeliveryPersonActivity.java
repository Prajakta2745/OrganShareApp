package com.example.organshare.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.models.DeliveryPersonnelProfile;
import com.example.organshare.models.OrderModel;
import com.example.organshare.repositories.OrganBankRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AssignDeliveryPersonActivity extends AppCompatActivity {

    private TextInputEditText etAssignOrderId, etAssignRequestId, etAssignOrgan, etAssignBlood;
    private TextInputEditText etAssignPickupLoc, etAssignDestHospital, etAssignExpectedTime, etAssignNotes;
    private AutoCompleteTextView actvAssignCourier, actvAssignPriority;
    private MaterialButton btnSubmitDeliveryAssignment;
    private ProgressBar progressBarAssignDel;
    private OrganBankRepository bankRepository;
    private final List<DeliveryPersonnelProfile> courierList = new ArrayList<>();
    private DeliveryPersonnelProfile selectedCourier;
    private OrderModel currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_delivery_person);

        bankRepository = new OrganBankRepository();
        currentOrder = (OrderModel) getIntent().getSerializableExtra("ORDER_DATA");

        initViews();
        setupFieldsFromOrder();
        loadCouriers();

        btnSubmitDeliveryAssignment.setOnClickListener(v -> submitAssignment());
    }

    private void initViews() {
        etAssignOrderId = findViewById(R.id.etAssignOrderId);
        etAssignRequestId = findViewById(R.id.etAssignRequestId);
        etAssignOrgan = findViewById(R.id.etAssignOrgan);
        etAssignBlood = findViewById(R.id.etAssignBlood);
        etAssignPickupLoc = findViewById(R.id.etAssignPickupLoc);
        etAssignDestHospital = findViewById(R.id.etAssignDestHospital);
        etAssignExpectedTime = findViewById(R.id.etAssignExpectedTime);
        etAssignNotes = findViewById(R.id.etAssignNotes);
        actvAssignCourier = findViewById(R.id.actvAssignCourier);
        actvAssignPriority = findViewById(R.id.actvAssignPriority);
        btnSubmitDeliveryAssignment = findViewById(R.id.btnSubmitDeliveryAssignment);
        progressBarAssignDel = findViewById(R.id.progressBarAssignDel);

        String[] priorities = {"EMERGENCY", "URGENT", "NORMAL"};
        actvAssignPriority.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priorities));
    }

    private void setupFieldsFromOrder() {
        if (currentOrder != null) {
            etAssignOrderId.setText(currentOrder.getOrderId());
            etAssignRequestId.setText(currentOrder.getRequestId());
            etAssignOrgan.setText(currentOrder.getOrganType());
            etAssignBlood.setText(currentOrder.getBloodGroup());
            etAssignPickupLoc.setText(currentOrder.getPickupAddress() != null ? currentOrder.getPickupAddress() : "Central Organ Bank Vault");
            etAssignDestHospital.setText(currentOrder.getHospitalName());
        }
    }

    private void loadCouriers() {
        bankRepository.getAvailableDeliveryPersonnel(new OrganBankRepository.DataCallback<List<DeliveryPersonnelProfile>>() {
            @Override
            public void onSuccess(List<DeliveryPersonnelProfile> result) {
                courierList.clear();
                if (result != null && !result.isEmpty()) {
                    courierList.addAll(result);
                } else {
                    DeliveryPersonnelProfile demo = new DeliveryPersonnelProfile();
                    demo.setDeliveryPersonId("DEL-USR-01");
                    demo.setFullName("Marcus Vance (Rapid Van)");
                    demo.setPhone("+1-555-700-1122");
                    courierList.add(demo);
                }

                List<String> names = new ArrayList<>();
                for (DeliveryPersonnelProfile p : courierList) {
                    names.add(p.getFullName() + " [" + p.getDeliveryPersonId() + "]");
                }

                ArrayAdapter<String> courierAdapter = new ArrayAdapter<>(AssignDeliveryPersonActivity.this, android.R.layout.simple_dropdown_item_1line, names);
                actvAssignCourier.setAdapter(courierAdapter);
                if (!courierList.isEmpty()) {
                    selectedCourier = courierList.get(0);
                    actvAssignCourier.setText(names.get(0), false);
                }

                actvAssignCourier.setOnItemClickListener((parent, view, position, id) -> {
                    selectedCourier = courierList.get(position);
                });
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(AssignDeliveryPersonActivity.this, "Error loading couriers: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitAssignment() {
        if (selectedCourier == null) {
            Toast.makeText(this, "Please select an authorized delivery courier", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentOrder == null) {
            currentOrder = new OrderModel();
            currentOrder.setOrderId(etAssignOrderId.getText().toString());
            currentOrder.setRequestId(etAssignRequestId.getText().toString());
            currentOrder.setOrganType(etAssignOrgan.getText().toString());
            currentOrder.setBloodGroup(etAssignBlood.getText().toString());
            currentOrder.setPickupAddress(etAssignPickupLoc.getText().toString());
            currentOrder.setHospitalName(etAssignDestHospital.getText().toString());
            currentOrder.setPickupLatitude(40.7128);
            currentOrder.setPickupLongitude(-74.0060);
            currentOrder.setHospitalLatitude(40.7306);
            currentOrder.setHospitalLongitude(-73.9352);
        }

        progressBarAssignDel.setVisibility(View.VISIBLE);
        btnSubmitDeliveryAssignment.setEnabled(false);

        bankRepository.assignDeliveryPersonnel(currentOrder, selectedCourier, new OrganBankRepository.DataCallback<String>() {
            @Override
            public void onSuccess(String deliveryId) {
                progressBarAssignDel.setVisibility(View.GONE);
                btnSubmitDeliveryAssignment.setEnabled(true);
                Toast.makeText(AssignDeliveryPersonActivity.this, "Delivery " + deliveryId + " successfully assigned to " + selectedCourier.getFullName(), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                progressBarAssignDel.setVisibility(View.GONE);
                btnSubmitDeliveryAssignment.setEnabled(true);
                Toast.makeText(AssignDeliveryPersonActivity.this, "Assignment failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
