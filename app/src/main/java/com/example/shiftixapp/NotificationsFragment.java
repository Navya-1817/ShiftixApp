package com.example.shiftixapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private FirebaseFirestore db;
    private String userId;
    private ListenerRegistration listenerRegistration;

    public static NotificationsFragment newInstance(String userId) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString("userId");
        }
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.notificationRecyclerView);
        adapter = new NotificationAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fetchNotifications();
        return view;
    }

    private void fetchNotifications() {
        List<Notification> notificationList = new ArrayList<>();
        listenerRegistration = db.collection("notifications")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        android.util.Log.e("ShiftixApp", "Error fetching notifications: " + e.getMessage());
                        return;
                    }
                    notificationList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Notification notification = document.toObject(Notification.class);
                            notificationList.add(notification);
                        }
                    }
                    adapter.setNotificationList(notificationList);
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }
}