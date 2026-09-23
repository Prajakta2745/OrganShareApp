package com.example.organshare.activities.admin;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.adapters.UserManagementAdapter;
import com.example.organshare.models.UserModel;
import com.example.organshare.repositories.AdminRepository;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView rvUserManagement;
    private UserManagementAdapter adapter;
    private final List<UserModel> userList = new ArrayList<>();
    private AdminRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        repository = new AdminRepository();
        rvUserManagement = findViewById(R.id.rvUserManagement);
        rvUserManagement.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserManagementAdapter(this, userList, new UserManagementAdapter.OnUserActionListener() {
            @Override
            public void onApprove(UserModel user) {
                repository.updateUserVerification(user.getUid(), true, new AdminRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(UserManagementActivity.this, user.getDisplayName() + " approved & verified!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(UserManagementActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onToggleSuspend(UserModel user) {
                boolean newStatus = !user.isSuspended();
                repository.updateUserSuspension(user.getUid(), newStatus, new AdminRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(UserManagementActivity.this, "User status updated!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(UserManagementActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        rvUserManagement.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {
        repository.getAllUsers(new AdminRepository.DataCallback<List<UserModel>>() {
            @Override
            public void onSuccess(List<UserModel> result) {
                userList.clear();
                if (result != null) {
                    userList.addAll(result);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(UserManagementActivity.this, "Error loading users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
