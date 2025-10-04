package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    private TextView tvUserName, tvUserEmail, tvTotalUsers, tvRecentActivity;
    private Button btnManageUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize Firebase
        try {
            FirebaseDatabase.getInstance();
            Log.d(TAG, "Firebase initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Firebase initialization failed: " + e.getMessage());
            Toast.makeText(this, "Firebase initialization failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Handle system bars padding
        View mainView = findViewById(R.id.main);
        if (mainView == null) {
            Log.e(TAG, "Main layout (R.id.main) not found");
            Toast.makeText(this, "Layout initialization failed: Main layout not found", Toast.LENGTH_LONG).show();
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvRecentActivity = findViewById(R.id.tvRecentActivity);
        btnManageUsers = findViewById(R.id.btnManageUsers);

        if (tvUserName == null || tvUserEmail == null || tvTotalUsers == null ||
                tvRecentActivity == null || btnManageUsers == null) {
            Log.e(TAG, "View initialization failed");
            Toast.makeText(this, "View initialization failed: One or more views not found", Toast.LENGTH_LONG).show();
            return;
        }

        // Set sample data (replace with actual user data, e.g., from Firebase Auth)
        tvUserName.setText("Serge Benit");
        tvUserEmail.setText("sergeb@gmail.com");
        tvTotalUsers.setText("5");
        tvRecentActivity.setText("10");

        // Manage Users button
        btnManageUsers.setOnClickListener(v -> {
            Log.d(TAG, "Manage Users button clicked");
            try {
                Intent intent = new Intent(Dashboard.this, UserManagementActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Failed to start UserManagementActivity: " + e.getMessage());
                Toast.makeText(this, "Failed to open User Management: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}