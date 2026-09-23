package com.example.organshare.activities.delivery;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.models.Checkpoint;
import com.example.organshare.models.DeliveryModel;
import com.example.organshare.repositories.DeliveryRepository;
import com.example.organshare.utils.DateTimeUtils;
import com.example.organshare.utils.IdGenerator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UpdateCheckpointActivity extends AppCompatActivity {

    private TextInputEditText etCheckpointLocation, etCheckpointNotes;
    private AutoCompleteTextView actvDeliveryStage;
    private MaterialButton btnSubmitCheckpoint;
    private DeliveryRepository repository;
    private DeliveryModel currentDelivery;

    private final String[] stages = {
            "PICKUP_STARTED",
            "PICKED_UP",
            "IN_TRANSIT",
            "CHECKPOINT_1",
            "CHECKPOINT_2",
            "NEAR_DESTINATION"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_checkpoint);

        repository = new DeliveryRepository();
        currentDelivery = (DeliveryModel) getIntent().getSerializableExtra("DELIVERY_DATA");

        initViews();
        setupDropdown();

        btnSubmitCheckpoint.setOnClickListener(v -> submitCheckpoint());
    }

    private void initViews() {
        etCheckpointLocation = findViewById(R.id.etCheckpointLocation);
        etCheckpointNotes = findViewById(R.id.etCheckpointNotes);
        actvDeliveryStage = findViewById(R.id.actvDeliveryStage);
        btnSubmitCheckpoint = findViewById(R.id.btnSubmitCheckpoint);
    }

    private void setupDropdown() {
        actvDeliveryStage.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stages));
    }

    private void submitCheckpoint() {
        if (currentDelivery == null) return;

        String loc = etCheckpointLocation.getText() != null ? etCheckpointLocation.getText().toString().trim() : "";
        String stage = actvDeliveryStage.getText().toString().trim();
        String notes = etCheckpointNotes.getText() != null ? etCheckpointNotes.getText().toString().trim() : "";

        if (loc.isEmpty()) {
            etCheckpointLocation.setError("Location landmark is required");
            etCheckpointLocation.requestFocus();
            return;
        }

        // Slightly nudge coordinates along demo path
        double lat = currentDelivery.getLastKnownLatitude() != 0 ? currentDelivery.getLastKnownLatitude() + 0.008 : currentDelivery.getPickupLatitude() + 0.008;
        double lng = currentDelivery.getLastKnownLongitude() != 0 ? currentDelivery.getLastKnownLongitude() + 0.008 : currentDelivery.getPickupLongitude() + 0.008;

        Checkpoint cp = new Checkpoint(
                IdGenerator.generateCheckpointId(),
                currentDelivery.getDeliveryId(),
                loc,
                lat,
                lng,
                DateTimeUtils.getCurrentTime(),
                stage,
                notes
        );

        repository.addCheckpoint(currentDelivery.getDeliveryId(), cp, stage, new DeliveryRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(UpdateCheckpointActivity.this, "Checkpoint recorded and live map updated!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UpdateCheckpointActivity.this, "Failed to save: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
