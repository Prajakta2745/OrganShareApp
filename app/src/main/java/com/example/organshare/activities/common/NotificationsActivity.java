package com.example.organshare.activities.common;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.NotificationAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.AppNotification;
import com.example.organshare.repositories.NotificationRepository;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private final List<AppNotification> notificationList = new ArrayList<>();
    private NotificationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        repository = new NotificationRepository();
        rvNotifications = findViewById(R.id.rvNotifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(this, notificationList);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        AuthManager auth = AuthManager.getInstance(this);
        String userId = auth.getSessionManager().getUserId();
        String role = auth.getSessionManager().getUserRole();

        repository.getNotificationsForUser(userId, role, new NotificationRepository.DataCallback<List<AppNotification>>() {
            @Override
            public void onSuccess(List<AppNotification> result) {
                notificationList.clear();
                if (result != null && !result.isEmpty()) {
                    notificationList.addAll(result);
                } else {
                    // Show a helpful default notification
                    notificationList.add(new AppNotification("DEFAULT-1", userId, role,
                            "Welcome to OrganShare", "Your account is active and connected to the organ allocation network.", "SYSTEM", ""));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(NotificationsActivity.this, "Failed to load notifications: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
