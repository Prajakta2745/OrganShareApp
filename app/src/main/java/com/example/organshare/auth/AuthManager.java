package com.example.organshare.auth;

import android.content.Context;
import com.example.organshare.repositories.AuthRepository;
import com.example.organshare.utils.SessionManager;

public class AuthManager {
    private static AuthManager instance;
    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private AuthManager(Context context) {
        this.authRepository = new AuthRepository();
        this.sessionManager = new SessionManager(context.getApplicationContext());
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    public boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn() && authRepository.getCurrentFirebaseUser() != null;
    }

    public void logout(Context context) {
        authRepository.logout();
        sessionManager.clearSession();
    }
}
