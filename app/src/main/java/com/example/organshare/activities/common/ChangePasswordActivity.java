package com.example.organshare.activities.common;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmNewPassword;
    private MaterialButton btnSubmitChangePassword;
    private ProgressBar progressBarChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmNewPassword = findViewById(R.id.etConfirmNewPassword);
        btnSubmitChangePassword = findViewById(R.id.btnSubmitChangePassword);
        progressBarChangePwd = findViewById(R.id.progressBarChangePwd);

        btnSubmitChangePassword.setOnClickListener(v -> performPasswordChange());
    }

    private void performPasswordChange() {
        String currentPass = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString().trim() : "";
        String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
        String confirmPass = etConfirmNewPassword.getText() != null ? etConfirmNewPassword.getText().toString().trim() : "";

        if (currentPass.isEmpty()) {
            etCurrentPassword.setError("Current password required");
            return;
        }

        if (!ValidationUtils.isValidPassword(newPass)) {
            etNewPassword.setError("New password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            etConfirmNewPassword.setError("Passwords do not match");
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Session invalid. Please re-login.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarChangePwd.setVisibility(View.VISIBLE);
        btnSubmitChangePassword.setEnabled(false);

        // Re-authenticate user first
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPass);
        user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
            // Update password
            user.updatePassword(newPass).addOnSuccessListener(aVoid1 -> {
                progressBarChangePwd.setVisibility(View.GONE);
                btnSubmitChangePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                progressBarChangePwd.setVisibility(View.GONE);
                btnSubmitChangePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Failed to update: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        }).addOnFailureListener(e -> {
            progressBarChangePwd.setVisibility(View.GONE);
            btnSubmitChangePassword.setEnabled(true);
            Toast.makeText(ChangePasswordActivity.this, "Current password incorrect: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
