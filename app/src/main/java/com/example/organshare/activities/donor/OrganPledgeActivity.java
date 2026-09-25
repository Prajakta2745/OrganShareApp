package com.example.organshare.activities.donor;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrganPledgeActivity extends AppCompatActivity {

    private CheckBox cbKidney, cbLiver, cbHeart, cbLungs, cbPancreas, cbEyes, cbBoneMarrow, cbSmallIntestine, cbFullBody;
    private MaterialButton btnSavePledge;
    private DonorRepository donorRepository;
    private DonorProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organ_pledge);

        donorRepository = new DonorRepository();

        initViews();
        setupListeners();
        loadExistingPledge();
    }

    private void initViews() {
        cbKidney = findViewById(R.id.cbKidney);
        cbLiver = findViewById(R.id.cbLiver);
        cbHeart = findViewById(R.id.cbHeart);
        cbLungs = findViewById(R.id.cbLungs);
        cbPancreas = findViewById(R.id.cbPancreas);
        cbEyes = findViewById(R.id.cbEyes);
        cbBoneMarrow = findViewById(R.id.cbBoneMarrow);
        cbSmallIntestine = findViewById(R.id.cbSmallIntestine);
        cbFullBody = findViewById(R.id.cbFullBody);
        btnSavePledge = findViewById(R.id.btnSavePledge);
    }

    private void setupListeners() {
        cbFullBody.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbKidney.setChecked(true);
                cbLiver.setChecked(true);
                cbHeart.setChecked(true);
                cbLungs.setChecked(true);
                cbPancreas.setChecked(true);
                cbEyes.setChecked(true);
                cbBoneMarrow.setChecked(true);
                cbSmallIntestine.setChecked(true);
            }
        });

        btnSavePledge.setOnClickListener(v -> savePledge());
    }

    private void loadExistingPledge() {
        String uid = AuthManager.getInstance(this).getSessionManager().getUserId();
        donorRepository.getDonorByUserId(uid, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null) {
                    currentProfile = profile;
                    List<String> organs = profile.getOrgansWillingToDonate();
                    if (organs != null) {
                        cbKidney.setChecked(organs.contains("Kidney"));
                        cbLiver.setChecked(organs.contains("Liver"));
                        cbHeart.setChecked(organs.contains("Heart"));
                        cbLungs.setChecked(organs.contains("Lungs"));
                        cbPancreas.setChecked(organs.contains("Pancreas"));
                        cbEyes.setChecked(organs.contains("Cornea") || organs.contains("Eyes"));
                        cbBoneMarrow.setChecked(organs.contains("Bone Marrow"));
                        cbSmallIntestine.setChecked(organs.contains("Small Intestine"));
                    }
                    cbFullBody.setChecked(profile.isFullBodyDonation());
                }
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(OrganPledgeActivity.this, "Failed to load pledge: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePledge() {
        if (currentProfile == null) {
            currentProfile = new DonorProfile();
            currentProfile.setUserId(AuthManager.getInstance(this).getSessionManager().getUserId());
            currentProfile.setDonorId("DNR-" + (System.currentTimeMillis() % 100000));
        }

        List<String> selectedOrgans = new ArrayList<>();
        if (cbKidney.isChecked()) selectedOrgans.add("Kidney");
        if (cbLiver.isChecked()) selectedOrgans.add("Liver");
        if (cbHeart.isChecked()) selectedOrgans.add("Heart");
        if (cbLungs.isChecked()) selectedOrgans.add("Lungs");
        if (cbPancreas.isChecked()) selectedOrgans.add("Pancreas");
        if (cbEyes.isChecked()) selectedOrgans.add("Cornea");
        if (cbBoneMarrow.isChecked()) selectedOrgans.add("Bone Marrow");
        if (cbSmallIntestine.isChecked()) selectedOrgans.add("Small Intestine");

        if (selectedOrgans.isEmpty() && !cbFullBody.isChecked()) {
            Toast.makeText(this, "Please select at least one organ to pledge or select Full Body Donation.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentProfile.setOrgansWillingToDonate(selectedOrgans);
        currentProfile.setFullBodyDonation(cbFullBody.isChecked());
        currentProfile.setDonationStatus("PLEDGED");
        currentProfile.setConsentGiven(true);

        btnSavePledge.setEnabled(false);
        donorRepository.saveDonorProfile(currentProfile, new DonorRepository.DataCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(OrganPledgeActivity.this, "Organ donation pledge updated successfully!", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                btnSavePledge.setEnabled(true);
                Toast.makeText(OrganPledgeActivity.this, "Error saving pledge: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
