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
import com.example.organshare.activities.organbank.AllocateOrganActivity;
import com.example.organshare.adapters.OrganRequestAdapter;
import com.example.organshare.auth.AuthManager;
import com.example.organshare.models.OrganRequest;
import com.example.organshare.repositories.OrganBankRepository;
import com.example.organshare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class BankRequestsFragment extends Fragment {

    private RecyclerView rvBankRequests;
    private OrganRequestAdapter adapter;
    private final List<OrganRequest> requestList = new ArrayList<>();
    private OrganBankRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_requests, container, false);

        repository = new OrganBankRepository();
        rvBankRequests = view.findViewById(R.id.rvBankRequests);
        rvBankRequests.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new OrganRequestAdapter(requireContext(), requestList, true, new OrganRequestAdapter.OnRequestClickListener() {
            @Override
            public void onViewDetails(OrganRequest request) {}

            @Override
            public void onAllocate(OrganRequest request) {
                Intent intent = new Intent(requireActivity(), AllocateOrganActivity.class);
                intent.putExtra("REQUEST_DATA", request);
                startActivity(intent);
            }

            @Override
            public void onReject(OrganRequest request) {
                String userId = AuthManager.getInstance(requireContext()).getSessionManager().getUserId();
                String userName = AuthManager.getInstance(requireContext()).getSessionManager().getUserName();
                repository.updateRequestStatus(request.getRequestId(), Constants.STATUS_REJECTED, userId, userName, new OrganBankRepository.DataCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(requireContext(), "Request Rejected.", Toast.LENGTH_SHORT).show();
                        loadRequests();
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        rvBankRequests.setAdapter(adapter);

        loadRequests();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRequests();
    }

    private void loadRequests() {
        repository.getAllRequests(new OrganBankRepository.DataCallback<List<OrganRequest>>() {
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
}
