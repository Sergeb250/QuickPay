package com.auca.quickypay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.User;
import com.auca.quickypay.sqlite.dbHelper;

public class Login extends AppCompatActivity {

    private EditText etLoginEmail, etLoginPassword;
    private TextView gotoAuca;
    private Button btnLogin, btnRegister;
    private dbHelper dbHelper;

    // SharedPreferences constants
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_LAST_EMAIL = "last_email";
    private static final String KEY_LAST_PASSWORD = "last_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new dbHelper(this);

        etLoginEmail = findViewById(R.id.etLoginUsername); // You can rename in XML to etLoginEmail for clarity
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        gotoAuca = findViewById(R.id.gotoAuca);

        // Load last logged-in user
        loadLastUser();

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {
            String email = etLoginEmail.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean userExists = dbHelper.checkUser(email, password);

            if (userExists) {
                Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                // Save last logged-in user
                saveLastUser(email, password);

                // Create a dummy user object to pass to next activity
                User loggedInUser = new User();
                loggedInUser.setEmail(email);
                loggedInUser.setPassword(password);

                Intent dash = new Intent(Login.this, Dashboard.class);
                dash.putExtra("loggedIn", loggedInUser);
                startActivity(dash);
                finish();
            } else {
                Toast.makeText(Login.this, "Invalid credentials. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // REGISTER BUTTON
        btnRegister.setOnClickListener(v -> {
            Intent reg = new Intent(Login.this, register.class);
            startActivity(reg);
        });

        // LINK TO AUCA SITE
        gotoAuca.setOnClickListener(v -> {
            Intent gotosite = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.auca.ac.rw"));
            startActivity(gotosite);
            Toast.makeText(Login.this, "Redirecting...", Toast.LENGTH_SHORT).show();
        });
    }

    // Save last logged-in user
    private void saveLastUser(String email, String password) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LAST_EMAIL, email);
        editor.putString(KEY_LAST_PASSWORD, password);
        editor.apply();
    }

    // Load last logged-in user
    private void loadLastUser() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lastEmail = prefs.getString(KEY_LAST_EMAIL, "");
        String lastPassword = prefs.getString(KEY_LAST_PASSWORD, "");

        if (!lastEmail.isEmpty()) etLoginEmail.setText(lastEmail);
        if (!lastPassword.isEmpty()) etLoginPassword.setText(lastPassword);
    }
}
