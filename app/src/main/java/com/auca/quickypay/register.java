package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class register extends AppCompatActivity {

    private EditText RegUsername, RegEmail, RegPassword;
    private Button btnSubmit;
    private CheckBox cbTerms;

    FirebaseDatabase fbDb;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Handle padding for status/navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        RegUsername = findViewById(R.id.txtUsername);
        RegEmail = findViewById(R.id.txtEmail);
        RegPassword = findViewById(R.id.txtPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnSubmit = findViewById(R.id.btnRegister);

        // Firebase setup
        fbDb = FirebaseDatabase.getInstance();
        dbRef = fbDb.getReference("Users");

        // Disable register button until Terms are checked
        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnSubmit.setEnabled(isChecked);
            btnSubmit.setAlpha(isChecked ? 1f : 0.5f);
        });

        // Register button logic
        btnSubmit.setOnClickListener(v -> {
            String username = RegUsername.getText().toString().trim();
            String email = RegEmail.getText().toString().trim();
            String password = RegPassword.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
                Toast.makeText(register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!cbTerms.isChecked()) {
                Toast.makeText(register.this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create user object
            User newUser = new User(username, email, password);

            // Save user in Firebase under "Users/username"
            dbRef.child(username).setValue(newUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(register.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    // Move to Login activity
                    Intent intent = new Intent(register.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(register.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }

}
