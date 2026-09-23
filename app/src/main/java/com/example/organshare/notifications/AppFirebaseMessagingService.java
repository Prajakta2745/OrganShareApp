package com.example.organshare.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.organshare.R;
import com.example.organshare.activities.common.NotificationsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    public static final String CHANNEL_ID = "organshare_channel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = "OrganShare Alert";
        String body = "You have an update regarding an organ request or delivery.";

        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        } else if (remoteMessage.getData().size() > 0) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("message");
        }

        NotificationHelper.showNotification(this, title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Token update handling
    }
}
