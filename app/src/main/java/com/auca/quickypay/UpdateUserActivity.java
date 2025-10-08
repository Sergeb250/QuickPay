package com.auca.quickypay;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.auca.quickypay.Model.User;
import com.auca.quickypay.sqlite.dbHelper;

public class UpdateUserActivity extends AppCompatActivity {

    private static final String TAG = "UpdateUserActivity";

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnUpdateUser, btnCancel;
    private Toolbar toolbar;
    private dbHelper databaseHelper;

    private String originalEmail;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        databaseHelper = new dbHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdateUser = findViewById(R.id.btnUpdateUser);
        btnCancel = findViewById(R.id.btnCancel);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Check for edit mode
        if (getIntent().hasExtra("editUserEmail")) {
            isEditMode = true;
            originalEmail = getIntent().getStringExtra("editUserEmail");

            User user = databaseHelper.getUserByEmail(originalEmail);
            if (user != null) {
                etUsername.setText(user.getUsername());
                etEmail.setText(user.getEmail());
                etPassword.setText(user.getPassword());
                etConfirmPassword.setText(user.getPassword());
            }

            setTitle("Edit User");
            btnUpdateUser.setText("Update User");
        } else {
            setTitle("Add New User");
            btnUpdateUser.setText("Add User");
        }

        btnUpdateUser.setOnClickListener(v -> {
            if (isEditMode) updateUser();
            else addUser();
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void addUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(username, email, password, confirmPassword)) return;

        if (databaseHelper.emailExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        if (databaseHelper.insertUser(newUser)) {
            Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add user.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!validateInputs(username, email, password, confirmPassword)) return;

        if (!email.equals(originalEmail) && databaseHelper.emailExists(email)) {
            etEmail.setError("Email already registered");
            etEmail.requestFocus();
            Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
            return;
        }

        User updatedUser = new User();
        updatedUser.setUsername(username);
        updatedUser.setEmail(email);
        updatedUser.setPassword(password);

        if (databaseHelper.updateUser(originalEmail, updatedUser)) {
            Toast.makeText(this, "User updated successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update user.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || username.length() < 3) {
            etUsername.setError("Username must be at least 3 characters");
            etUsername.requestFocus();
            return false;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email");
            etEmail.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }
        if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) databaseHelper.close();
    }
}
