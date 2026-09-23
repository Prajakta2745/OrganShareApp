package com.example.organshare.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.organshare.R;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.auth.RoleRouter;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AuthManager authManager = AuthManager.getInstance(this);
            if (authManager.isUserLoggedIn()) {
                String role = authManager.getSessionManager().getUserRole();
                RoleRouter.navigateToDashboard(this, role);
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
