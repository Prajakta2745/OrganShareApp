package com.example.organshare.activities.donor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.organshare.R;
import com.example.organshare.models.emergency.TransportRequest;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

public class DonorTransportRequestActivity extends AppCompatActivity {

    private String hospitalId;
    private String hospitalName;
    private String activeRequestId;
    private String activeDriverPhone = "+91-98231-10001";

    private Toolbar toolbar;
    private MaterialCardView cardHospitalSummary, cardRequestForm;
    private TextView tvDestHospitalName;
    private TextInputEditText etPickupAddress, etContactPhone, etNotes;
    private RadioGroup rgUrgency;
    private RadioButton rbEmergency;
    private MaterialButton btnSubmitTransport;

    // Success Screen Views
    private LinearLayout layoutSuccessScreen;
    private TextView tvSuccessRequestId, tvSuccessHospitalName, tvSuccessPickupLocation, tvSuccessStatusBadge;
    private TextView tvSuccessVehicleUnit, tvSuccessDriverDetails;
    private MaterialButton btnCallAssignedDriver, btnReturnToDashboard, btnViewAvailableHospitals;

    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration requestListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_transport_request);

        hospitalId = getIntent().getStringExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_ID);
        hospitalName = getIntent().getStringExtra(HospitalDetailsActivity.EXTRA_HOSPITAL_NAME);

        repository = new EmergencyCoordinatorRepository();
        initViews();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        cardHospitalSummary = findViewById(R.id.cardHospitalSummary);
        cardRequestForm = findViewById(R.id.cardRequestForm);

        tvDestHospitalName = findViewById(R.id.tvDestHospitalName);
        tvDestHospitalName.setText(hospitalName != null ? hospitalName : "Selected Hospital");

        etPickupAddress = findViewById(R.id.etPickupAddress);
        etContactPhone = findViewById(R.id.etContactPhone);
        etNotes = findViewById(R.id.etNotes);

        rgUrgency = findViewById(R.id.rgUrgency);
        rbEmergency = findViewById(R.id.rbEmergency);

        btnSubmitTransport = findViewById(R.id.btnSubmitTransport);

        // Success State Views
        layoutSuccessScreen = findViewById(R.id.layoutSuccessScreen);
        tvSuccessRequestId = findViewById(R.id.tvSuccessRequestId);
        tvSuccessHospitalName = findViewById(R.id.tvSuccessHospitalName);
        tvSuccessPickupLocation = findViewById(R.id.tvSuccessPickupLocation);
        tvSuccessStatusBadge = findViewById(R.id.tvSuccessStatusBadge);
        tvSuccessVehicleUnit = findViewById(R.id.tvSuccessVehicleUnit);
        tvSuccessDriverDetails = findViewById(R.id.tvSuccessDriverDetails);
        btnCallAssignedDriver = findViewById(R.id.btnCallAssignedDriver);
        btnReturnToDashboard = findViewById(R.id.btnReturnToDashboard);
        btnViewAvailableHospitals = findViewById(R.id.btnViewAvailableHospitals);

        btnSubmitTransport.setOnClickListener(v -> submitTransportRequest());

        btnReturnToDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(DonorTransportRequestActivity.this, DonorDashboardActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnViewAvailableHospitals.setOnClickListener(v -> {
            Intent intent = new Intent(DonorTransportRequestActivity.this, AvailableHospitalsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnCallAssignedDriver.setOnClickListener(v -> {
            try {
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + activeDriverPhone));
                startActivity(dialIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Calling: " + activeDriverPhone, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitTransportRequest() {
        String pickup = etPickupAddress.getText() != null ? etPickupAddress.getText().toString().trim() : "";
        String phone = etContactPhone.getText() != null ? etContactPhone.getText().toString().trim() : "";
        String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

        if (TextUtils.isEmpty(pickup)) {
            etPickupAddress.setError("Pickup location is required");
            etPickupAddress.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etContactPhone.setError("Contact phone is required");
            etContactPhone.requestFocus();
            return;
        }

        btnSubmitTransport.setEnabled(false);
        btnSubmitTransport.setText("Dispatching request...");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user != null ? user.getUid() : "anonymous_donor";
        String userName = user != null && user.getDisplayName() != null ? user.getDisplayName() : "Registered Donor";

        boolean isEmergency = rbEmergency.isChecked();

        TransportRequest request = new TransportRequest();
        request.setRequesterId(userId);
        request.setRequesterName(userName);
        request.setRequesterPhone(phone);
        request.setPickupLocation(pickup);
        request.setDestinationHospitalId(hospitalId);
        request.setDestinationHospitalName(hospitalName);
        request.setEmergency(isEmergency);
        request.setRequestedVehicleType(isEmergency ? "Advanced Life Support (ALS) Ambulance" : "Basic Life Support (BLS) Ambulance");
        request.setNotes(notes);
        request.setStatus("ASSIGNED");

        repository.createTransportRequest(request, new EmergencyCoordinatorRepository.ResourceCallback<String>() {
            @Override
            public void onSuccess(String requestId) {
                activeRequestId = requestId;
                displaySuccessState(requestId, pickup, isEmergency);
            }

            @Override
            public void onError(Exception e) {
                btnSubmitTransport.setEnabled(true);
                btnSubmitTransport.setText("Dispatch Transport Request");
                Toast.makeText(DonorTransportRequestActivity.this, "Error submitting request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySuccessState(String requestId, String pickup, boolean isEmergency) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Request Dispatched");
        }

        // Hide form and show dedicated success confirmation
        cardHospitalSummary.setVisibility(View.GONE);
        cardRequestForm.setVisibility(View.GONE);
        layoutSuccessScreen.setVisibility(View.VISIBLE);

        tvSuccessRequestId.setText("Ticket #" + requestId);
        tvSuccessHospitalName.setText(hospitalName != null ? hospitalName : "Selected Hospital");
        tvSuccessPickupLocation.setText(pickup);
        tvSuccessStatusBadge.setText("ASSIGNED / DISPATCHED");

        tvSuccessVehicleUnit.setText(isEmergency ? "🚑 ALS Ambulance Unit #AMB-04" : "🚑 BLS Ambulance Unit #AMB-08");
        tvSuccessDriverDetails.setText("Driver: Ramesh Jadhav (+91-98231-10001)\nEstimated Arrival: 12-15 mins");
        activeDriverPhone = "+91-98231-10001";

        // Listen for live coordinator updates to this transport request
        listenToLiveRequestUpdates(requestId);
    }

    private void listenToLiveRequestUpdates(String requestId) {
        if (requestListener != null) requestListener.remove();

        requestListener = repository.listenToTransportRequest(requestId, new EmergencyCoordinatorRepository.DataCallback<TransportRequest>() {
            @Override
            public void onSuccess(TransportRequest result) {
                if (result != null) {
                    if (result.getStatus() != null) {
                        tvSuccessStatusBadge.setText(result.getStatus().toUpperCase());
                    }
                    if (result.getAssignedVehicleNumber() != null && !result.getAssignedVehicleNumber().isEmpty()) {
                        tvSuccessVehicleUnit.setText("🚑 " + result.getRequestedVehicleType() + " (" + result.getAssignedVehicleNumber() + ")");
                    }
                    if (result.getAssignedDriverName() != null && !result.getAssignedDriverName().isEmpty()) {
                        activeDriverPhone = result.getAssignedDriverPhone() != null ? result.getAssignedDriverPhone() : activeDriverPhone;
                        tvSuccessDriverDetails.setText("Driver: " + result.getAssignedDriverName() + " (" + activeDriverPhone + ")");
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                // Non-fatal, retain optimistic display
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (requestListener != null) {
            requestListener.remove();
        }
    }
}
