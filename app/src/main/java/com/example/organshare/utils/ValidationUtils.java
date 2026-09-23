package com.example.organshare.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && phone.trim().length() >= 7;
    }

    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text) && !text.trim().isEmpty();
    }
}
