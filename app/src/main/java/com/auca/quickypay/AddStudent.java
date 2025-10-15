package com.auca.quickypay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.Student;
import com.auca.quickypay.sqlite.dbHelper;
import com.google.android.material.textfield.TextInputEditText;

public class AddStudent extends AppCompatActivity {

    private TextInputEditText usernameEditText, emailEditText, passwordEditText, facultyEditText, createdByEditText;
    private Button submitButton;
    private dbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_student);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize dbHelper
        dbHelper = new dbHelper(this);

        initializeViews();
        setupSubmitButton();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        facultyEditText = findViewById(R.id.facultyEditText);
        createdByEditText = findViewById(R.id.createdByEditText);
        submitButton = findViewById(R.id.submitButton);
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                createStudent();
            }
        });
    }

    private boolean validateInputs() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String facultyIdStr = facultyEditText.getText().toString().trim();
        String createdByStr = createdByEditText.getText().toString().trim();

        // Username validation
        if (username.isEmpty()) {
            usernameEditText.setError("Username is required");
            usernameEditText.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            usernameEditText.setError("Username must be at least 3 characters");
            usernameEditText.requestFocus();
            return false;
        }

        // Email validation
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            emailEditText.requestFocus();
            return false;
        }

        // Password validation
        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        // Faculty ID validation
        if (facultyIdStr.isEmpty()) {
            facultyEditText.setError("Faculty ID is required");
            facultyEditText.requestFocus();
            return false;
        }

        try {
            int facultyId = Integer.parseInt(facultyIdStr);
            if (facultyId <= 0) {
                facultyEditText.setError("Faculty ID must be a positive number");
                facultyEditText.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            facultyEditText.setError("Faculty ID must be a valid number");
            facultyEditText.requestFocus();
            return false;
        }

        // Created By validation
        if (createdByStr.isEmpty()) {
            createdByEditText.setError("Created By is required");
            createdByEditText.requestFocus();
            return false;
        }

        try {
            int createdBy = Integer.parseInt(createdByStr);
            if (createdBy <= 0) {
                createdByEditText.setError("Created By must be a positive number");
                createdByEditText.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            createdByEditText.setError("Created By must be a valid number");
            createdByEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void createStudent() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        int facultyId = Integer.parseInt(facultyEditText.getText().toString().trim());
        int createdBy = Integer.parseInt(createdByEditText.getText().toString().trim());

        // Create Student object
        Student student = new Student(username, email, password, facultyId, createdBy);

        // Save student to database
        saveStudent(student);
    }

    private void saveStudent(Student student) {
        long result = dbHelper.addStudent(student);

        if (result != -1) {
            // Success - result contains the studentId generated by database
            String successMessage = String.format(
                    "Student created successfully!\nUsername: %s\nEmail: %s\nFaculty ID: %d\nCreated By: %d\nStudent ID: %d",
                    student.getUsername(),
                    student.getEmail(),
                    student.getFacultyId(),
                    student.getCreatedBy(),
                    result
            );

            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();
            clearForm();

        } else {
            // Error occurred
            Toast.makeText(this, "Failed to create student. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearForm() {
        usernameEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
        facultyEditText.setText("");
        createdByEditText.setText("");
        usernameEditText.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close database connection when activity is destroyed
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}