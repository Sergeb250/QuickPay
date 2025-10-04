package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail, tvTotalUsers, tvRecentActivity;
    private Button btnManageUsers;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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

        // Initialize Firebase reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Get logged in user from intent
        User loggedInUser = (User) getIntent().getSerializableExtra("loggedIn");
        if (loggedInUser != null) {
            tvUserName.setText(loggedInUser.getUsername());
            tvUserEmail.setText(loggedInUser.getEmail());
        }

        // Set up Manage Users button click listener
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, UserManagement.class);
            startActivity(intent);
        });

        // Load statistics
        loadUserStatistics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh statistics when returning to dashboard
        loadUserStatistics();
    }

    private void loadUserStatistics() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                long totalUsers = snapshot.getChildrenCount();
                tvTotalUsers.setText(String.valueOf(totalUsers));

                // For now, set recent activity same as total
                // You can implement actual logic based on last login time
                tvRecentActivity.setText(String.valueOf(totalUsers));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle error silently or show toast
                tvTotalUsers.setText("0");
                tvRecentActivity.setText("0");
            }
        });
    }
}