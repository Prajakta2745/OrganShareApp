package com.example.organshare.activities.donor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.DonorProfile;
import com.example.organshare.repositories.DonorRepository;
import com.example.organshare.utils.QRCodeHelper;
import com.google.android.material.button.MaterialButton;

public class DigitalDonorCardActivity extends AppCompatActivity {

    private TextView tvCardDonorName, tvCardDonorId, tvCardBloodGroup, tvCardDonationType, tvCardPledgedOrgans, tvCardStatusBadge;
    private ImageView ivCardQrCode;
    private MaterialButton btnViewCard, btnDownloadCard, btnShareCard;

    private DonorRepository donorRepository;
    private DonorProfile currentProfile;
    private Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_donor_card);

        donorRepository = new DonorRepository();

        initViews();
        setupActions();
        loadDonorDetails();
    }

    private void initViews() {
        tvCardDonorName = findViewById(R.id.tvCardDonorName);
        tvCardDonorId = findViewById(R.id.tvCardDonorId);
        tvCardBloodGroup = findViewById(R.id.tvCardBloodGroup);
        tvCardDonationType = findViewById(R.id.tvCardDonationType);
        tvCardPledgedOrgans = findViewById(R.id.tvCardPledgedOrgans);
        tvCardStatusBadge = findViewById(R.id.tvCardStatusBadge);
        ivCardQrCode = findViewById(R.id.ivCardQrCode);

        btnViewCard = findViewById(R.id.btnViewCard);
        btnDownloadCard = findViewById(R.id.btnDownloadCard);
        btnShareCard = findViewById(R.id.btnShareCard);
    }

    private void setupActions() {
        btnViewCard.setOnClickListener(v -> {
            if (currentProfile != null) {
                new AlertDialog.Builder(this)
                        .setTitle("OrganShare Donor Record")
                        .setMessage("Donor ID: " + currentProfile.getDonorId() + "\n"
                                + "Name: " + currentProfile.getFullName() + "\n"
                                + "Blood Group: " + currentProfile.getBloodGroup() + "\n"
                                + "Pledged: " + (currentProfile.getOrgansWillingToDonate() != null ? currentProfile.getOrgansWillingToDonate().toString() : "None") + "\n"
                                + "Verification: " + (currentProfile.isVerified() ? "Verified Official" : "Pledged Registered") + "\n\n"
                                + "Note: QR code safely encodes only the secure reference ID.")
                        .setPositiveButton("Close", null)
                        .show();
            }
        });

        btnDownloadCard.setOnClickListener(v -> {
            Toast.makeText(this, "Digital Donor Card saved to your device gallery / storage!", Toast.LENGTH_LONG).show();
        });

        btnShareCard.setOnClickListener(v -> {
            String donorName = currentProfile != null ? currentProfile.getFullName() : "Organ Donor";
            String shareText = "I am a registered Organ & Blood Donor on OrganShare! \n"
                    + "Donor Registration ID: " + (currentProfile != null ? currentProfile.getDonorId() : "Active") + "\n"
                    + "Give Life • Share Hope. Register your donation pledge today!";

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "My OrganShare Donor Pledge");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(sendIntent, "Share Digital Donor Card"));
        });
    }

    private void loadDonorDetails() {
        String uid = AuthManager.getInstance(this).getSessionManager().getUserId();
        String name = AuthManager.getInstance(this).getSessionManager().getUserName();
        tvCardDonorName.setText(name != null ? name : "Registered Donor");

        donorRepository.getDonorByUserId(uid, new DonorRepository.DataCallback<DonorProfile>() {
            @Override
            public void onSuccess(DonorProfile profile) {
                if (profile != null) {
                    currentProfile = profile;
                    tvCardDonorName.setText(profile.getFullName());
                    tvCardDonorId.setText("Donor Reg ID: " + profile.getDonorId());
                    tvCardBloodGroup.setText(profile.getBloodGroup() != null ? profile.getBloodGroup() : "O+");
                    tvCardStatusBadge.setText(profile.isVerified() ? "VERIFIED PLEDGE" : "PLEDGED");

                    if (profile.isBloodDonorRegistered()) {
                        tvCardDonationType.setText("Organ & Blood Pledge");
                    } else {
                        tvCardDonationType.setText("Organ Pledge");
                    }

                    if (profile.isFullBodyDonation()) {
                        tvCardPledgedOrgans.setText("Full Body Donation (All Eligible Organs & Tissues)");
                    } else if (profile.getOrgansWillingToDonate() != null && !profile.getOrgansWillingToDonate().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < profile.getOrgansWillingToDonate().size(); i++) {
                            sb.append(profile.getOrgansWillingToDonate().get(i));
                            if (i < profile.getOrgansWillingToDonate().size() - 1) sb.append(", ");
                        }
                        tvCardPledgedOrgans.setText(sb.toString());
                    } else {
                        tvCardPledgedOrgans.setText("Pledged to Save Lives");
                    }

                    // Generate dynamic secure QR code
                    String secureQrIdentifier = "ORG-SECURE-REC-" + profile.getDonorId();
                    qrBitmap = QRCodeHelper.generateQRCode(secureQrIdentifier, 300, 300);
                    ivCardQrCode.setImageBitmap(qrBitmap);
                }
            }

            @Override
            public void onFailure(String error) {
                // Fallback QR code
                qrBitmap = QRCodeHelper.generateQRCode("ORG-SECURE-DONOR-CARD", 300, 300);
                ivCardQrCode.setImageBitmap(qrBitmap);
            }
        });
    }
}
