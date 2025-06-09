package com.example.shiftixapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Locale;

public class RequestSwapActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Spinner shiftFromSpinner, shiftToSpinner;
    private List<Shift> userShifts = new ArrayList<>();
    private List<Shift> allShifts = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_swap);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        shiftFromSpinner = findViewById(R.id.shiftFromSpinner);
        shiftToSpinner = findViewById(R.id.shiftToSpinner);
        Button submitSwapButton = findViewById(R.id.submitSwapButton);

        // Get current user
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch user's shifts
        db.collection("shifts").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    userShifts.clear();
                    List<String> userShiftStrings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Shift shift = document.toObject(Shift.class);
                        userShifts.add(shift);
                        userShiftStrings.add(shift.getDepartment() + " (" + dateFormat.format(shift.getStartTime()) + ")");
                    }
                    ArrayAdapter<String> userAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userShiftStrings);
                    userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    shiftFromSpinner.setAdapter(userAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching your shifts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Fetch all shifts (for desired shift)
        db.collection("shifts").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allShifts.clear();
                    List<String> allShiftStrings = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Shift shift = document.toObject(Shift.class);
                        allShifts.add(shift);
                        allShiftStrings.add(shift.getDepartment() + " (" + dateFormat.format(shift.getStartTime()) + ")");
                    }
                    ArrayAdapter<String> allAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allShiftStrings);
                    allAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    shiftToSpinner.setAdapter(allAdapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching shifts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Submit swap request
        submitSwapButton.setOnClickListener(v -> {
            if (shiftFromSpinner.getSelectedItemPosition() == -1 || shiftToSpinner.getSelectedItemPosition() == -1) {
                Toast.makeText(this, "Please select both shifts", Toast.LENGTH_SHORT).show();
                return;
            }

            Shift shiftFrom = userShifts.get(shiftFromSpinner.getSelectedItemPosition());
            Shift shiftTo = allShifts.get(shiftToSpinner.getSelectedItemPosition());

            String requestId = UUID.randomUUID().toString();
            SwapRequest swapRequest = new SwapRequest(requestId, shiftFrom.getShiftId(), shiftTo.getShiftId(), "Pending", new Date());

            db.collection("swapRequests").document(requestId).set(swapRequest)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RequestSwapActivity.this, "Swap request submitted", Toast.LENGTH_SHORT).show();
                        // Create notification for the user of the desired shift
                        db.collection("shifts").document(shiftTo.getShiftId()).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Shift shift = documentSnapshot.toObject(Shift.class);
                                    if (shift != null) {
                                        String notificationId = UUID.randomUUID().toString();
                                        String message = "New swap request for your shift in " + shift.getDepartment();
                                        Notification notification = new Notification(notificationId, shift.getUserId(), message, new Date(), false);
                                        db.collection("notifications").document(notificationId).set(notification);
                                    }
                                });
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RequestSwapActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}