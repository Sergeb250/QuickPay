package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.User;

public class register extends AppCompatActivity {

    private EditText RegUsername, RegEmail,RegPassword;
    private Button btnSubmit;
    private CheckBox cbTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RegUsername = findViewById(R.id.txtUsername);
        RegEmail = findViewById(R.id.txtEmail);
        RegPassword = findViewById(R.id.txtPassword);
        cbTerms = findViewById(R.id.cbTerms);
        btnSubmit=findViewById(R.id.btnRegister);


        cbTerms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnSubmit.setEnabled(isChecked);
            btnSubmit.setAlpha(isChecked ? 1f : 0.5f);
        });





        btnSubmit.setOnClickListener(v -> {
            String username = RegUsername.getText().toString().trim();
            String Email = RegEmail.getText().toString().trim();
            String password = RegPassword.getText().toString().trim();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(register.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }


            if (!cbTerms.isChecked()) {
                Toast.makeText(register.this, "Please agree to Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }


            User newUser = new User(username,Email, password);


            Intent Intent = new Intent(register.this, Login.class);
            Intent.putExtra("User", newUser);
            startActivity(Intent);
            finish();
        });
    }
}
