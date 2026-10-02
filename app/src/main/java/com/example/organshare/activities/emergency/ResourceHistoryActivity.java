package com.example.organshare.activities.emergency;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.organshare.R;
import com.example.organshare.adapters.emergency.ResourceHistoryAdapter;
import com.example.organshare.repositories.EmergencyCoordinatorRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class ResourceHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private ResourceHistoryAdapter adapter;
    private EmergencyCoordinatorRepository repository;
    private ListenerRegistration historyListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_history);

        repository = new EmergencyCoordinatorRepository();
        initViews();
        setupListener();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResourceHistoryAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setupListener() {
        progressBar.setVisibility(View.VISIBLE);
        historyListener = repository.listenToResourceHistory(list -> {
            progressBar.setVisibility(View.GONE);
            if (list == null || list.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
                adapter.setHistoryList(new ArrayList<>());
            } else {
                tvEmptyState.setVisibility(View.GONE);
                adapter.setHistoryList(list);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (historyListener != null) historyListener.remove();
    }
}
