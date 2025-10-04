package com.auca.quickypay;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auca.quickypay.Model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private static final String TAG = "Register";
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase
        try {
            usersRef = FirebaseDatabase.getInstance().getReference("Users");
            Log.d(TAG, "Firebase initialized, usersRef: " + usersRef.toString());
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        if (etUsername == null || etEmail == null || etPassword == null || btnRegister == null) {
            Log.e(TAG, "View initialization failed");
            Toast.makeText(this, "View initialization failed: One or more views not found", Toast.LENGTH_LONG).show();
            return;
        }

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Log.w(TAG, "Registration failed: Empty fields");
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(username, email, password);
            String userId = usersRef.push().getKey();
            if (userId == null) {
                Log.e(TAG, "Failed to generate user ID");
                Toast.makeText(this, "Failed to generate user ID", Toast.LENGTH_LONG).show();
                return;
            }

            usersRef.child(userId).setValue(user)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "User registered: " + username);
                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to register user: " + e.getMessage());
                        Toast.makeText(this, "Failed to register user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}