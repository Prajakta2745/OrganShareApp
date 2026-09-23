package com.example.organshare.activities.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.repositories.AuthRepository;
import com.example.organshare.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etResetEmail;
    private MaterialButton btnSendReset;
    private ProgressBar progressBarReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etResetEmail = findViewById(R.id.etResetEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        progressBarReset = findViewById(R.id.progressBarReset);

        btnSendReset.setOnClickListener(v -> {
            String email = etResetEmail.getText() != null ? etResetEmail.getText().toString().trim() : "";
            if (!ValidationUtils.isValidEmail(email)) {
                etResetEmail.setError("Please enter a valid registered email");
                etResetEmail.requestFocus();
                return;
            }

            progressBarReset.setVisibility(View.VISIBLE);
            btnSendReset.setEnabled(false);

            AuthManager.getInstance(this).getAuthRepository().resetPassword(email, new AuthRepository.AuthCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    progressBarReset.setVisibility(View.GONE);
                    btnSendReset.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Password reset instructions sent to your email.", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onFailure(String errorMessage) {
                    progressBarReset.setVisibility(View.GONE);
                    btnSendReset.setEnabled(true);
                    Toast.makeText(ForgotPasswordActivity.this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
