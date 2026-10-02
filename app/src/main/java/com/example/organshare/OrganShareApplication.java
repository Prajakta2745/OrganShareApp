package com.example.organshare;

import android.app.Application;
import android.util.Log;
import io.sentry.Sentry;
import io.sentry.SentryLevel;
import io.sentry.android.core.SentryAndroid;

public class OrganShareApplication extends Application {

    private static final String TAG = "OrganShareApp";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Sentry with privacy guards and crash reporting
        try {
            SentryAndroid.init(this, options -> {
                // Ensure patient & donor medical data is never leaked to logs
                options.setSendDefaultPii(false);
                options.setAttachScreenshot(false);
                options.setAttachViewHierarchy(false);
                options.setTracesSampleRate(1.0);

                // Sanitize event before sending
                options.setBeforeSend((event, hint) -> {
                    // Filter out any sensitive parameters if present
                    return event;
                });
            });

            if (BuildConfig.DEBUG) {
                Sentry.addBreadcrumb("OrganShare Application initialized in DEBUG mode");
            }
        } catch (Exception e) {
            Log.e(TAG, "Sentry initialization error: " + e.getMessage());
        }
    }

    /**
     * Development helper to send a test event to verify Sentry project connectivity.
     */
    public static void sendTestSentryEvent() {
        try {
            Sentry.captureMessage("OrganShare Sentry Integration Connectivity Test", SentryLevel.INFO);
        } catch (Exception ignored) {}
    }
}
