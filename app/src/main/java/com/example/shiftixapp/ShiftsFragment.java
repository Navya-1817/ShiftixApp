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

public class ShiftsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ShiftAdapter adapter;
    private FirebaseFirestore db;
    private String userId;
    private String userRole;

    public static ShiftsFragment newInstance(String userId, String userRole) {
        ShiftsFragment fragment = new ShiftsFragment();
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
        View view = inflater.inflate(R.layout.fragment_shifts, container, false);
        recyclerView = view.findViewById(R.id.shiftRecyclerView);
        adapter = new ShiftAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        fetchShifts();
        return view;
    }

    private void fetchShifts() {
        List<Shift> shiftList = new ArrayList<>();
        if (userRole != null && userRole.equals("Admin")) {
            db.collection("shifts").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Shift shift = document.toObject(Shift.class);
                            shiftList.add(shift);
                        }
                        adapter.setShiftList(shiftList);
                    });
        } else {
            db.collection("shifts").whereEqualTo("userId", userId).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Shift shift = document.toObject(Shift.class);
                            shiftList.add(shift);
                        }
                        adapter.setShiftList(shiftList);
                    });
        }
    }
}