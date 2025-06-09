package com.example.shiftixapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText nameEditText, emailEditText, passwordEditText;
    private Spinner roleSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        Button registerButton = findViewById(R.id.registerButton);
        Button loginButton = findViewById(R.id.loginButton);

        // Set up role spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Register button click
        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            // Store user data in Firestore
                            User user = new User(userId, name, role);
                            db.collection("users").document(userId).set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(RegisterActivity.this, "Failed to save user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Login button click
        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    // User class for Firestore
    private static class User {
        private String userId;
        private String name;
        private String role;

        public User() {} // Empty constructor for Firestore

        public User(String userId, String name, String role) {
            this.userId = userId;
            this.name = name;
            this.role = role;
        }

        public String getUserId() { return userId; }
        public String getName() { return name; }
        public String getRole() { return role; }
    }
}