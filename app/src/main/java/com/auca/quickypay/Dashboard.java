package com.auca.quickypay;

import android.content.Intent;
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
import com.auca.quickypay.sqlite.dbHelper;

import java.util.List;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    private TextView tvUserName, tvUserEmail, tvTotalUsers, tvRecentActivity;
    private Button btnManageUsers;
    private ImageButton btnEmail, btnPhone;
    private dbHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        View mainView = findViewById(R.id.main);
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

        databaseHelper = new dbHelper(this);

        // Get logged-in user
        Intent intent = getIntent();
        User user = (User) intent.getSerializableExtra("loggedIn");

        if (user == null) {
            Toast.makeText(this, "No user found. Please login again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display user info
        tvUserName.setText(user.getUsername());
        tvUserEmail.setText(user.getEmail());

        // Load total users
        List<User> allUsers = databaseHelper.getAllUsers();
        tvTotalUsers.setText(String.valueOf(allUsers.size()));

        // Dummy recent activity
        tvRecentActivity.setText("5 Transactions");

        // Manage Users button
        btnManageUsers.setOnClickListener(v -> {
            Intent intent1 = new Intent(Dashboard.this, UserManagementActivity.class);
            startActivity(intent1);
        });

        // Email button
        btnEmail.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(android.net.Uri.parse("mailto:" + user.getEmail()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello " + user.getUsername());
                startActivity(Intent.createChooser(emailIntent, "Send email via"));
            } catch (Exception e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Email intent error: " + e.getMessage());
            }
        });

        // Phone button (optional)
        btnPhone.setOnClickListener(v -> {
            String phone = "0791822315";
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(android.net.Uri.parse("tel:" + phone));
                startActivity(callIntent);
            } catch (Exception e) {
                Toast.makeText(this, "No phone app found", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Phone intent error: " + e.getMessage());
            }
        });
    }
}
