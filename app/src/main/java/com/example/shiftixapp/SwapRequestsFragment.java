package com.example.shiftixapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class SwapRequestsFragment extends Fragment implements SwapRequestAdapter.OnSwapRequestActionListener {
    private RecyclerView recyclerView;
    private SwapRequestAdapter adapter;
    private FirebaseFirestore db;
    private String userId;
    private String userRole;
    private List<Shift> shiftList;

    public static SwapRequestsFragment newInstance(String userId, String userRole) {
        SwapRequestsFragment fragment = new SwapRequestsFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        args.putString("userRole", userRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
            userRole = getArguments().getString("userRole");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swap_requests, container, false);
        recyclerView = view.findViewById(R.id.swapRequestRecyclerView);
        adapter = new SwapRequestAdapter(userRole != null && userRole.equals("Admin"), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fetchShiftsAndSwapRequests();
        return view;
    }

    private void fetchShiftsAndSwapRequests() {
        shiftList = new ArrayList<>();
        if (userRole != null && userRole.equals("Admin")) {
            db.collection("shifts").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Shift shift = document.toObject(Shift.class);
                            shiftList.add(shift);
                        }
                        fetchSwapRequests();
                    });
        } else {
            db.collection("shifts").whereEqualTo("userId", userId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Shift shift = document.toObject(Shift.class);
                            shiftList.add(shift);
                        }
                        fetchSwapRequests();
                    });
        }
    }

    private void fetchSwapRequests() {
        List<SwapRequest> swapRequestList = new ArrayList<>();
        if (userRole != null && userRole.equals("Admin")) {
            db.collection("swapRequests").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            SwapRequest request = document.toObject(SwapRequest.class);
                            swapRequestList.add(request);
                        }
                        adapter.setSwapRequestList(swapRequestList, shiftList);
                    });
        } else {
            db.collection("shifts").whereEqualTo("userId", userId).get()
                    .addOnSuccessListener(shiftSnapshots -> {
                        List<String> userShiftIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : shiftSnapshots) {
                            userShiftIds.add(document.getId());
                        }
                        if (!userShiftIds.isEmpty()) {
                            db.collection("swapRequests")
                                    .whereIn("shiftFromId", userShiftIds)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                            SwapRequest request = document.toObject(SwapRequest.class);
                                            swapRequestList.add(request);
                                        }
                                        adapter.setSwapRequestList(swapRequestList, shiftList);
                                    });
                        } else {
                            adapter.setSwapRequestList(swapRequestList, shiftList);
                        }
                    });
        }
    }

    @Override
    public void onApprove(String requestId) {
        db.collection("swapRequests").document(requestId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    SwapRequest request = documentSnapshot.toObject(SwapRequest.class);
                    if (request != null) {
                        db.collection("swapRequests").document(requestId).update("status", "Approved")
                                .addOnSuccessListener(aVoid -> {
                                    notifyUsers(request, "Swap request approved");
                                    fetchShiftsAndSwapRequests();
                                });
                    }
                });
    }

    @Override
    public void onReject(String requestId) {
        db.collection("swapRequests").document(requestId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    SwapRequest request = documentSnapshot.toObject(SwapRequest.class);
                    if (request != null) {
                        db.collection("swapRequests").document(requestId).update("status", "Rejected")
                                .addOnSuccessListener(aVoid -> {
                                    notifyUsers(request, "Swap request rejected");
                                    fetchShiftsAndSwapRequests();
                                });
                    }
                });
    }

    private void notifyUsers(SwapRequest request, String message) {
        db.collection("shifts").document(request.getShiftFromId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Shift shiftFrom = documentSnapshot.toObject(Shift.class);
                    if (shiftFrom != null) {
                        String notificationId = java.util.UUID.randomUUID().toString();
                        Notification notification = new Notification(notificationId, shiftFrom.getUserId(), message, new java.util.Date(), false);
                        db.collection("notifications").document(notificationId).set(notification);
                    }
                });
        db.collection("shifts").document(request.getShiftToId()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Shift shiftTo = documentSnapshot.toObject(Shift.class);
                    if (shiftTo != null) {
                        String notificationId = java.util.UUID.randomUUID().toString();
                        Notification notification = new Notification(notificationId, shiftTo.getUserId(), message, new java.util.Date(), false);
                        db.collection("notifications").document(notificationId).set(notification);
                    }
                });
    }
}