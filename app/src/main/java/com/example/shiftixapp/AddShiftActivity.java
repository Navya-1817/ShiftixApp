package com.example.shiftixapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
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

        // Save shift button click
        saveShiftButton.setOnClickListener(v -> {
            String department = departmentEditText.getText().toString().trim();
            String startTimeStr = startTimeEditText.getText().toString().trim();
            String endTimeStr = endTimeEditText.getText().toString().trim();

            if (department.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
                Toast.makeText(AddShiftActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                Date startTime = sdf.parse(startTimeStr);
                Date endTime = sdf.parse(endTimeStr);

                if (endTime.before(startTime)) {
                    Toast.makeText(AddShiftActivity.this, "End time must be after start time", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = mAuth.getCurrentUser().getUid();
                String shiftId = UUID.randomUUID().toString();
                Shift shift = new Shift(shiftId, userId, department, startTime, endTime);

                db.collection("shifts").document(shiftId).set(shift)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddShiftActivity.this, "Shift added", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(AddShiftActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } catch (Exception e) {
                Toast.makeText(AddShiftActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
            }
        });
    }
}