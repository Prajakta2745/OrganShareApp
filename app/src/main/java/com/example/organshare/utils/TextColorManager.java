package com.example.organshare.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class TextColorManager {

    private static final String PREF_NAME = "organshare_color_prefs";
    private static final String KEY_TEXT_COLOR = "key_text_color";

    public static final String COLOR_DEFAULT = "DEFAULT";
    public static final String COLOR_BLUE = "BLUE";
    public static final String COLOR_GREEN = "GREEN";
    public static final String COLOR_PURPLE = "PURPLE";

    public static void setTextColorPreference(Context context, String colorCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TEXT_COLOR, colorCode).apply();
    }

    public static String getTextColorPreference(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TEXT_COLOR, COLOR_DEFAULT);
    }

    public static int getResolvedAccentColor(Context context) {
        String pref = getTextColorPreference(context);
        switch (pref) {
            case COLOR_BLUE:
                return Color.parseColor("#2563EB");
            case COLOR_GREEN:
                return Color.parseColor("#16A34A");
            case COLOR_PURPLE:
                return Color.parseColor("#9333EA");
            case COLOR_DEFAULT:
            default:
                return Color.parseColor("#006A6A"); // Teal Default
        }
    }
}
