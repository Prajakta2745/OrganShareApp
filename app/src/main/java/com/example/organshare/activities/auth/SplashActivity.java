package com.example.organshare.activities.auth;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.auth.RoleRouter;
import com.example.organshare.firebase.FirestoreCollections;
import com.example.organshare.firebase.FirestoreHelper;
import com.example.organshare.utils.ThemeManager;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar pbSplashLoading;
    private TextView tvSplashLoadingStatus, tvErrorMessage;
    private LinearLayout layoutErrorRetry;
    private MaterialButton btnSplashRetry;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private int currentProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force light mode healthcare palette as default
        ThemeManager.applyTheme(this);
        setContentView(R.layout.activity_splash);

        if (com.example.organshare.BuildConfig.DEBUG) {
            com.example.organshare.OrganShareApplication.sendTestSentryEvent();
        }

        initViews();
        startRealInitialization();
    }

    private void initViews() {
        pbSplashLoading = findViewById(R.id.pbSplashLoading);
        tvSplashLoadingStatus = findViewById(R.id.tvSplashLoadingStatus);
        layoutErrorRetry = findViewById(R.id.layoutErrorRetry);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnSplashRetry = findViewById(R.id.btnSplashRetry);

        btnSplashRetry.setOnClickListener(v -> {
            layoutErrorRetry.setVisibility(View.GONE);
            tvSplashLoadingStatus.setVisibility(View.VISIBLE);
            pbSplashLoading.setVisibility(View.VISIBLE);
            currentProgress = 0;
            updateProgress(0, "Restarting initialization...");
            startRealInitialization();
        });
    }

    private void updateProgress(int targetProgress, String statusMessage) {
        if (isFinishing() || isDestroyed()) return;

        tvSplashLoadingStatus.setText(statusMessage);
        ObjectAnimator animation = ObjectAnimator.ofInt(pbSplashLoading, "progress", currentProgress, targetProgress);
        animation.setDuration(350);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        currentProgress = targetProgress;
    }

    /**
     * Real 5-step asynchronous initialization pipeline.
     * Guaranteed NO navigation occurs before progress reaches exactly 100%.
     */
    private void startRealInitialization() {
        // Step 1: 0% -> 25% Initialize Firebase & Core Services
        updateProgress(15, "Connecting to OrganShare cloud...");
        mainHandler.postDelayed(() -> {
            try {
                FirebaseFirestore db = FirestoreHelper.getFirestore();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                if (db == null || auth == null) {
                    showError("Firebase services could not be reached.");
                    return;
                }

                // Step 2: 25% -> 50% Validate Authentication & Local Session
                updateProgress(35, "Verifying secure session...");
                mainHandler.postDelayed(() -> verifyAuthAndSession(auth, db), 400);

            } catch (Exception e) {
                showError("Initialization error: " + e.getMessage());
            }
        }, 500);
    }

    private void verifyAuthAndSession(FirebaseAuth auth, FirebaseFirestore db) {
        AuthManager authManager = AuthManager.getInstance(this);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null || !authManager.isUserLoggedIn()) {
            // Not logged in -> Advance to 75% then 100% and route to LoginActivity
            updateProgress(75, "Preparing application...");
            mainHandler.postDelayed(() -> {
                updateProgress(100, "Ready!");
                mainHandler.postDelayed(() -> {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }, 350);
            }, 450);
        } else {
            // User is authenticated -> Step 3: 50% -> 75% Fetch verified role from Firestore
            updateProgress(60, "Retrieving role authorization...");
            String uid = currentUser.getUid();

            db.collection(FirestoreCollections.USERS).document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String role = authManager.getSessionManager().getUserRole();
                        if (documentSnapshot.exists() && documentSnapshot.getString("role") != null) {
                            role = documentSnapshot.getString("role");
                            authManager.getSessionManager().setUserRole(role);
                        }

                        final String finalRole = role;

                        // Step 4: 75% -> 100% Load user configurations & complete readiness
                        updateProgress(85, "Synchronizing authorized modules...");
                        mainHandler.postDelayed(() -> {
                            updateProgress(100, "Ready!");
                            mainHandler.postDelayed(() -> {
                                RoleRouter.navigateToRoleSpecificDashboard(SplashActivity.this, finalRole);
                                finish();
                            }, 350);
                        }, 400);
                    })
                    .addOnFailureListener(e -> {
                        // Fallback to locally cached session if offline but authenticated
                        String cachedRole = authManager.getSessionManager().getUserRole();
                        if (cachedRole != null && !cachedRole.isEmpty()) {
                            updateProgress(100, "Ready (Offline Mode)!");
                            mainHandler.postDelayed(() -> {
                                RoleRouter.navigateToRoleSpecificDashboard(SplashActivity.this, cachedRole);
                                finish();
                            }, 350);
                        } else {
                            showError("Failed to verify user credentials: " + e.getMessage());
                        }
                    });
        }
    }

    private void showError(String message) {
        if (isFinishing() || isDestroyed()) return;
        tvErrorMessage.setText(message);
        tvSplashLoadingStatus.setVisibility(View.GONE);
        layoutErrorRetry.setVisibility(View.VISIBLE);
    }
}
