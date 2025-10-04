package com.auca.quickypay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.User;
import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    private TextView tvUserName, tvUserEmail, tvTotalUsers, tvRecentActivity;
    private Button btnManageUsers;
    private ImageButton btnEmail, btnPhone;

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
        btnEmail = findViewById(R.id.btnEmail);
        btnPhone = findViewById(R.id.btnPhone);

        if (tvUserName == null || tvUserEmail == null || tvTotalUsers == null ||
                tvRecentActivity == null || btnManageUsers == null) {
            Log.e(TAG, "View initialization failed");
            Toast.makeText(this, "View initialization failed: One or more views not found", Toast.LENGTH_LONG).show();
            return;
        }

        //  Get logged-in user from Intent
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("loggedIn");

        // Default data
        String username = "Guest";
        String email = "guest@example.com";
        String phone = "0791822315"; // Default phone (can be replaced with user.getPhone())

        if (user != null) {
            if (user.getUsername() != null && !user.getUsername().isEmpty()) {
                username = user.getUsername();
            }
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                email = user.getEmail();
            }

        }

        tvUserName.setText(username);
        tvUserEmail.setText(email);
        tvTotalUsers.setText("5");
        tvRecentActivity.setText("10");

        //  Manage Users button
        btnManageUsers.setOnClickListener(v -> {
            Log.d(TAG, "Manage Users button clicked");
            try {
                Intent intent1 = new Intent(Dashboard.this, UserManagementActivity.class);
                startActivity(intent1);
            } catch (Exception e) {
                Log.e(TAG, "Failed to start UserManagementActivity: " + e.getMessage());
                Toast.makeText(this, "Failed to open User Management: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

        //  Email Intent
        if (btnEmail != null) {
            String finalEmail = email;
            btnEmail.setOnClickListener(v -> {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + finalEmail));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello " );
                    startActivity(Intent.createChooser(emailIntent, "Send email via"));
                    Toast.makeText(this, "Send E-Mail", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Email intent error: " + e.getMessage());
                }
            });
        }

        //  Phone Intent
        if (btnPhone != null) {
            String finalPhone = phone;
            btnPhone.setOnClickListener(v -> {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" +finalPhone ));
                    startActivity(callIntent);
                    Toast.makeText(this, "Opening Call", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Phone intent error: " + e.getMessage());
                }
            });
        }
    }
}
