package com.example.organshare.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.auth.RoleRouter;
import com.example.organshare.models.UserModel;
import com.example.organshare.repositories.AuthRepository;
import com.example.organshare.utils.SampleDataSeeder;
import com.example.organshare.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin, btnSeedDemoData;
    private TextView tvForgotPassword, tvRegister;
    private ProgressBar progressBar;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = AuthManager.getInstance(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSeedDemoData = findViewById(R.id.btnSeedDemoData);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnSeedDemoData.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            SampleDataSeeder.seedDemoData(new SampleDataSeeder.SeedCallback() {
                @Override
                public void onSuccess(String message) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(String error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Seed error: " + error, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void performLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        authManager.getAuthRepository().login(email, password, new AuthRepository.AuthCallback<UserModel>() {
            @Override
            public void onSuccess(UserModel user) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                authManager.getSessionManager().createLoginSession(
                        user.getUid(),
                        user.getEmail(),
                        user.getDisplayName(),
                        user.getRole()
                );

                Toast.makeText(LoginActivity.this, "Welcome back, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                RoleRouter.navigateToDashboard(LoginActivity.this, user.getRole());
            }

            @Override
            public void onFailure(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login Failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
