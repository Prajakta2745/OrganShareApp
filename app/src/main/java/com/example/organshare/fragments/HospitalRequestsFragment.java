package com.example.organshare.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.organshare.R;
import com.example.organshare.activities.hospital.CreateRequestActivity;
import com.example.organshare.activities.hospital.RequestDetailsActivity;
import com.example.organshare.adapters.OrganRequestAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.HospitalProfile;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.HospitalRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HospitalRequestsFragment extends Fragment {

    private RecyclerView rvHospitalRequests;
    private FloatingActionButton fabNewRequest;
    private OrganRequestAdapter adapter;
    private final List<OrganRequest> requestList = new ArrayList<>();
    private HospitalRepository hospitalRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hospital_requests, container, false);

        hospitalRepository = new HospitalRepository();
        rvHospitalRequests = view.findViewById(R.id.rvHospitalRequests);
        fabNewRequest = view.findViewById(R.id.fabNewRequest);

        rvHospitalRequests.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new OrganRequestAdapter(requireContext(), requestList, false, new OrganRequestAdapter.OnRequestClickListener() {
            @Override
            public void onViewDetails(OrganRequest request) {
                Intent intent = new Intent(requireActivity(), RequestDetailsActivity.class);
                intent.putExtra("REQUEST_DATA", request);
                startActivity(intent);
            }

            @Override
            public void onAllocate(OrganRequest request) {}

            @Override
            public void onReject(OrganRequest request) {}
        });
        rvHospitalRequests.setAdapter(adapter);

        fabNewRequest.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CreateRequestActivity.class);
            startActivity(intent);
        });

        loadRequests();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequests();
    }

    private void loadRequests() {
        String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
        hospitalRepository.getHospitalByUserId(userId, new HospitalRepository.DataCallback<HospitalProfile>() {
            @Override
            public void onSuccess(HospitalProfile profile) {
                String hospitalId = profile != null ? profile.getHospitalId() : "HOSP-001";
                hospitalRepository.getHospitalRequests(hospitalId, new HospitalRepository.DataCallback<List<OrganRequest>>() {
                    @Override
                    public void onSuccess(List<OrganRequest> result) {
                        requestList.clear();
                        if (result != null) {
                            requestList.addAll(result);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {
                        if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                if (isAdded()) Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
