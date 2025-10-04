package com.auca.quickypay;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private TextView gotoAuca;
    private Button btnLogin, btnRegister;

    private DatabaseReference dbRef;  // Firebase DB reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etLoginUsername = findViewById(R.id.etLoginUsername);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        gotoAuca = findViewById(R.id.gotoAuca);

        // Firebase DB reference
        dbRef = FirebaseDatabase.getInstance().getReference("Users");

        // LOGIN BUTTON
        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check Firebase DB
            dbRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null && password.equals(user.getPassword())) {
                            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();

                            Intent dash = new Intent(Login.this, Dashboard.class);
                            dash.putExtra("loggedIn", user);
                            startActivity(dash);
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Login.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // REGISTER BUTTON
        btnRegister.setOnClickListener(v -> {
            Intent reg = new Intent(Login.this, register.class);
            startActivity(reg);
        });

        // Link to AUCA site
        gotoAuca.setOnClickListener(v -> {
            Intent gotosite = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.auca.ac.rw"));
            startActivity(gotosite);
            Toast.makeText(Login.this, "Redirecting", Toast.LENGTH_SHORT).show();
        });
    }
}
