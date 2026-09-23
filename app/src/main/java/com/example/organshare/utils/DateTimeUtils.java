package com.example.organshare.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String FULL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDate() {
        return new SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat(FULL_DATETIME_FORMAT, Locale.getDefault()).format(new Date());
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date);
    }

    public static String formatDate(Date date) {
        if (date == null) return "N/A";
        return new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date);
    }
}
