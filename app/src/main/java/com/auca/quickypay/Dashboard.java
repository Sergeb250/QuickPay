package com.auca.quickypay;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.User;

public class Dashboard extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);


        User loggedInUser = (User) getIntent().getSerializableExtra("loggedIn");


        if (loggedInUser != null) {
            if (loggedInUser.getUsername() != null) {
                tvUserName.setText(loggedInUser.getUsername());
            }
            if (loggedInUser.getEmail() != null) {
                tvUserEmail.setText(loggedInUser.getEmail());
            }
        } else {
            tvUserName.setText("Guest");
            tvUserEmail.setText("No email");
        }



    }
}
