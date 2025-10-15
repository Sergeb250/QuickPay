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
    private TextView tvUserName, tvUserEmail, tvTotalUsers, tvTotalFaculties, tvTotalStudents, tvTotalCourses;
    private Button btnManageUsers, btnManageFaculties, btnManageStudents, btnManageCourses,btnRegistrations;
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
        initializeViews();
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
        displayUserInfo(user);

        // Load statistics
        loadStatistics();

        // Setup button listeners
        setupButtonListeners(user);
    }

    private void initializeViews() {
        // User info views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);

        // Statistics views
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalFaculties = findViewById(R.id.tvTotalFaculties);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvTotalCourses = findViewById(R.id.tvTotalCourses);

        // Buttons
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageFaculties = findViewById(R.id.btnManageFaculties);
        btnManageStudents = findViewById(R.id.btnManageStudents);
        btnManageCourses = findViewById(R.id.btnManageCourses);
        btnRegistrations=findViewById(R.id.btnRegistrations);

        btnEmail = findViewById(R.id.btnEmail);
        btnPhone = findViewById(R.id.btnPhone);
    }

    private void displayUserInfo(User user) {
        tvUserName.setText(user.getUsername());
        tvUserEmail.setText(user.getEmail());
    }

    private void loadStatistics() {
        new Thread(() -> {
            try {
                // Get counts from database
                int userCount = databaseHelper.getAllUsers().size();
                int facultyCount = databaseHelper.getAllFaculties().size();
                int studentCount = databaseHelper.getAllStudents().size();
                int courseCount = databaseHelper.getCoursesWithFaculty().size();

                runOnUiThread(() -> {
                    tvTotalUsers.setText(String.valueOf(userCount));
                    tvTotalFaculties.setText(String.valueOf(facultyCount));
                    tvTotalStudents.setText(String.valueOf(studentCount));
                    tvTotalCourses.setText(String.valueOf(courseCount));
                });
            } catch (Exception e) {
                Log.e(TAG, "Error loading statistics: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(Dashboard.this, "Error loading statistics", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setupButtonListeners(User user) {
        // Manage Users button
        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, UserManagementActivity.class);
            startActivity(intent);
        });

        // Manage Faculties button
        btnManageFaculties.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, FacultyListActivity.class);
            startActivity(intent);
        });

        // Manage Students button
        btnManageStudents.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, StudentListActivity.class);
            startActivity(intent);
        });

        // Manage Courses button
        btnManageCourses.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, CourseListActivity.class);
            startActivity(intent);
        });


        btnRegistrations.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Registration.class);
            startActivity(intent);
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

        // Phone button
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

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh statistics when returning to dashboard
        loadStatistics();
    }
}