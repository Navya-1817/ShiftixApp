package com.example.shiftixapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddShiftActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText departmentEditText, startTimeEditText, endTimeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shift);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        departmentEditText = findViewById(R.id.departmentEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        endTimeEditText = findViewById(R.id.endTimeEditText);
        Button saveShiftButton = findViewById(R.id.saveShiftButton);

        // Get username from Intent
        String userName = getIntent().getStringExtra("userName");
        if (userName == null || userName.isEmpty()) {
            Toast.makeText(this, "User name not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Save shift button click
        saveShiftButton.setOnClickListener(v -> {
            String department = departmentEditText.getText().toString().trim();
            String startTimeStr = startTimeEditText.getText().toString().trim();
            String endTimeStr = endTimeEditText.getText().toString().trim();

            if (department.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                Toast.makeText(AddShiftActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user is logged in
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(AddShiftActivity.this, "You must be logged in", Toast.LENGTH_SHORT).show();
                return;
            }
            String userId = currentUser.getUid();

            try {
                // Use a flexible date format (adjust based on your input expectations)
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                sdf.setLenient(false); // Strict parsing to avoid invalid dates
                Date startTime = sdf.parse(startTimeStr);
                Date endTime = sdf.parse(endTimeStr);

                if (endTime.before(startTime)) {
                    Toast.makeText(AddShiftActivity.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }

                String shiftId = UUID.randomUUID().toString();
                Shift shift = new Shift(shiftId, userId, department, startTime, endTime, userName);

                // Save shift to Firestore
                db.collection("shifts").document(shiftId).set(shift)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddShiftActivity.this, "Shift added successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddShiftActivity.this, "Failed to add shift: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            } catch (Exception e) {
                Toast.makeText(AddShiftActivity.this, "Invalid date format. Use yyyy-MM-dd HH:mm:ss", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
