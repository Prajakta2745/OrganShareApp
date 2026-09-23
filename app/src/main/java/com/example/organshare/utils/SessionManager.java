package com.example.organshare.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String email, String name, String role) {
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.putString(Constants.KEY_USER_EMAIL, email);
        editor.putString(Constants.KEY_USER_NAME, name);
        editor.putString(Constants.KEY_USER_ROLE, role);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(Constants.KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return pref.getString(Constants.KEY_USER_ID, "");
    }

    public String getUserEmail() {
        return pref.getString(Constants.KEY_USER_EMAIL, "");
    }

    public String getUserName() {
        return pref.getString(Constants.KEY_USER_NAME, "User");
    }

    public String getUserRole() {
        return pref.getString(Constants.KEY_USER_ROLE, "");
    }

    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}
