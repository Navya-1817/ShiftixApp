package com.example.shiftixapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView welcomeTextView, roleTextView;
    private Button addShiftButton, requestSwapButton;
    private String userRole;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        welcomeTextView = findViewById(R.id.welcomeTextView);
        roleTextView = findViewById(R.id.roleTextView);
        addShiftButton = findViewById(R.id.addShiftButton);
        requestSwapButton = findViewById(R.id.requestSwapButton);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Get current user
        if (mAuth.getCurrentUser() == null) {
            android.util.Log.e("ShiftixApp", "No user logged in");
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        userId = mAuth.getCurrentUser().getUid();
        android.util.Log.d("ShiftixApp", "Logged in userId: " + userId);

        // Fetch user data
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        userRole = documentSnapshot.getString("role");
                        android.util.Log.d("ShiftixApp", "User data: name=" + name + ", role=" + userRole);
                        welcomeTextView.setText("Welcome, " + name);
                        roleTextView.setText("Role: " + userRole);

                        // Show buttons for Doctors/Interns
                        if (userRole != null && (userRole.equals("Doctor") || userRole.equals("Intern"))) {
                            addShiftButton.setVisibility(View.VISIBLE);
                            requestSwapButton.setVisibility(View.VISIBLE);
                            android.util.Log.d("ShiftixApp", "Buttons visible for " + userRole);
                        }

                        // Set up ViewPager and TabLayout
                        ViewPagerAdapter adapter = new ViewPagerAdapter(this, userId, userRole);
                        viewPager.setAdapter(adapter);
                        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                            switch (position) {
                                case 0:
                                    tab.setText("Notifications");
                                    break;
                                case 1:
                                    tab.setText("Shifts");
                                    break;
                                case 2:
                                    tab.setText("Swap Requests");
                                    break;
                            }
                        }).attach();
                    } else {
                        android.util.Log.e("ShiftixApp", "User document not found for userId: " + userId);
                        Toast.makeText(HomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("ShiftixApp", "Error fetching user data: " + e.getMessage());
                    Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Add shift button click
        addShiftButton.setOnClickListener(v -> {
            android.util.Log.d("ShiftixApp", "Add Shift button clicked");
            startActivity(new Intent(HomeActivity.this, AddShiftActivity.class));
        });

        // Request swap button click
        requestSwapButton.setOnClickListener(v -> {
            android.util.Log.d("ShiftixApp", "Request Swap button clicked");
            startActivity(new Intent(HomeActivity.this, RequestSwapActivity.class));
        });

        // Logout button click
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            android.util.Log.d("ShiftixApp", "Logout button clicked");
            mAuth.signOut();
            Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        });
    }
}