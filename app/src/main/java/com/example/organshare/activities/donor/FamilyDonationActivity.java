package com.example.organshare.activities.donor;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.models.FamilyDonation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FamilyDonationActivity extends AppCompatActivity {

    private TextInputEditText etDeceasedName, etDeathDate, etDeathTime, etHospitalFacility, etFamilyMemberName, etFamilyContact;
    private AutoCompleteTextView actvRelationship;
    private CheckBox cbFamCornea, cbFamKidneys, cbFamLiver, cbFamHeart, cbFamAll, cbFamilyConsent;
    private MaterialButton btnSubmitFamilyDonation;

    private FirebaseFirestore db;

    private static final String[] RELATIONSHIPS = {
            "Spouse", "Son / Daughter", "Father / Mother", "Brother / Sister", "Legal Guardian / Executor"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_donation);

        db = FirestoreHelper.getFirestore();

        initViews();
        setupDateTimePickers();
        setupDropdown();

        btnSubmitFamilyDonation.setOnClickListener(v -> submitFamilyDonation());
    }

    private void initViews() {
        etDeceasedName = findViewById(R.id.etDeceasedName);
        etDeathDate = findViewById(R.id.etDeathDate);
        etDeathTime = findViewById(R.id.etDeathTime);
        etHospitalFacility = findViewById(R.id.etHospitalFacility);
        etFamilyMemberName = findViewById(R.id.etFamilyMemberName);
        etFamilyContact = findViewById(R.id.etFamilyContact);
        actvRelationship = findViewById(R.id.actvRelationship);

        cbFamCornea = findViewById(R.id.cbFamCornea);
        cbFamKidneys = findViewById(R.id.cbFamKidneys);
        cbFamLiver = findViewById(R.id.cbFamLiver);
        cbFamHeart = findViewById(R.id.cbFamHeart);
        cbFamAll = findViewById(R.id.cbFamAll);
        cbFamilyConsent = findViewById(R.id.cbFamilyConsent);

        btnSubmitFamilyDonation = findViewById(R.id.btnSubmitFamilyDonation);
    }

    private void setupDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, RELATIONSHIPS);
        actvRelationship.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        etDeathDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etDeathDate.setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etDeathTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                etDeathTime.setText(time);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void submitFamilyDonation() {
        String deceasedName = etDeceasedName.getText() != null ? etDeceasedName.getText().toString().trim() : "";
        String deathDate = etDeathDate.getText() != null ? etDeathDate.getText().toString().trim() : "";
        String hospital = etHospitalFacility.getText() != null ? etHospitalFacility.getText().toString().trim() : "";
        String familyName = etFamilyMemberName.getText() != null ? etFamilyMemberName.getText().toString().trim() : "";
        String contact = etFamilyContact.getText() != null ? etFamilyContact.getText().toString().trim() : "";

        if (deceasedName.isEmpty() || deathDate.isEmpty() || hospital.isEmpty() || familyName.isEmpty() || contact.isEmpty()) {
            Toast.makeText(this, "Please fill in all mandatory fields (*).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbFamilyConsent.isChecked()) {
            Toast.makeText(this, "Please confirm lawful family consent declaration.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> organs = new ArrayList<>();
        if (cbFamAll.isChecked()) {
            organs.add("All Medically Viable Organs");
        } else {
            if (cbFamCornea.isChecked()) organs.add("Corneas");
            if (cbFamKidneys.isChecked()) organs.add("Kidneys");
            if (cbFamLiver.isChecked()) organs.add("Liver");
            if (cbFamHeart.isChecked()) organs.add("Heart");
        }

        String donationId = "FMD-2026-" + (System.currentTimeMillis() % 100000);

        FamilyDonation donation = new FamilyDonation();
        donation.setDonationId(donationId);
        donation.setDeceasedName(deceasedName);
        donation.setDeathDateTime(deathDate + " " + (etDeathTime.getText() != null ? etDeathTime.getText().toString() : ""));
        donation.setHospitalName(hospital);
        donation.setFamilyMemberName(familyName);
        donation.setRelationship(actvRelationship.getText() != null ? actvRelationship.getText().toString() : "Family Representative");
        donation.setContactPhone(contact);
        donation.setSelectedOrgans(organs);
        donation.setConsentStatus("CONSENT_ATTACHED");
        donation.setVerificationStatus("SUBMITTED");
        donation.setRegisteredByUserId(AuthManager.getInstance(this).getSessionManager().getUserId());
        donation.setNotes("Post-mortem pledge submitted. Awaiting hospital & organ bank clinical coordinator verification.");

        btnSubmitFamilyDonation.setEnabled(false);
        db.collection(FirestoreCollections.FAMILY_DONATIONS).document(donationId)
                .set(donation)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(FamilyDonationActivity.this, "Post-Mortem donation registered! Case ID: " + donationId, Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSubmitFamilyDonation.setEnabled(true);
                    Toast.makeText(FamilyDonationActivity.this, "Submission failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
