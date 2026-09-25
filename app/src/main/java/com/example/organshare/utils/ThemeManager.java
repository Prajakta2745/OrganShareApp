package com.example.organshare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    private static final String PREF_NAME = "organshare_theme_prefs";
    private static final String KEY_THEME_MODE = "key_theme_mode";

    public static final int THEME_LIGHT = 1;
    public static final int THEME_DARK = 2;
    public static final int THEME_SYSTEM = 3;

    public static void applyTheme(Context context) {
        int themeMode = getThemeMode(context);
        switch (themeMode) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public static void setThemeMode(Context context, int themeMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_THEME_MODE, themeMode).apply();
        applyTheme(context);
    }

    public static int getThemeMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_THEME_MODE, THEME_LIGHT);
    }
}
