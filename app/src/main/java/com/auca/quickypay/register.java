package com.auca.quickypay;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auca.quickypay.Model.User;
import com.auca.quickypay.sqlite.dbHelper;

public class register extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etUsername, etEmail, etPassword;
    private Button btnRegister;
    private dbHelper dbHelper;

    // SharedPreferences constants
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_LAST_EMAIL = "last_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new dbHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Load last registered email
        loadLastEmail();

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(username, email, password);
            boolean success = dbHelper.insertUser(user);

            if (success) {
                Log.d(TAG, "User registered successfully: " + username);
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();

                // Save email to SharedPreferences
                saveLastEmail(email);

                clearFields();
                finish();
            } else {
                Log.e(TAG, "Failed to register user (maybe email exists)");
                Toast.makeText(this, "Registration failed! Try a different email.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void clearFields() {
        etUsername.setText("");
        etEmail.setText("");
        etPassword.setText("");
    }

    // Save email in SharedPreferences
    private void saveLastEmail(String email) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAST_EMAIL, email);
        editor.apply();
    }

    // Load email from SharedPreferences
    private void loadLastEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastEmail = prefs.getString(KEY_LAST_EMAIL, "");
        if (!lastEmail.isEmpty()) {
            etEmail.setText(lastEmail);
        }
    }
}
