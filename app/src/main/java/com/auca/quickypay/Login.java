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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    private EditText etLoginUsername, etLoginPassword;
    private TextView gotoAuca;
    private Button btnLogin, btnRegister;



    private String registeredUsername, registeredPassword;

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
        gotoAuca=findViewById(R.id.gotoAuca);


        Intent intent = getIntent();
        registeredUsername = intent.getStringExtra("USERNAME");
        registeredPassword = intent.getStringExtra("PASSWORD");


        btnLogin.setOnClickListener(v -> {
            String username = etLoginUsername.getText().toString().trim();
            String password = etLoginPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(Login.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.equals(registeredUsername) && password.equals(registeredPassword)) {
                Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                Intent dash = new Intent(Login.this, Dashboard.class);
                startActivity(dash);
                finish();
            } else {
                Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                Intent gotosite=new Intent(Intent.ACTION_VIEW);
                gotosite.setData(Uri.parse("https://www.auca.ac.rw"));
                startActivity(gotosite);

            }
        });


        btnRegister.setOnClickListener(v -> {
            Intent reg = new Intent(Login.this, register.class);
            startActivity(reg);
        });

        gotoAuca.setOnClickListener(v -> {

            Intent gotosite=new Intent(Intent.ACTION_VIEW);
            gotosite.setData(Uri.parse("https://www.auca.ac.rw"));
            startActivity(gotosite);
            Toast.makeText(Login.this,"Redirecting",Toast.LENGTH_SHORT).show();

        });


    }
}
