package com.auca.quickypay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auca.quickypay.Model.Student;
import com.auca.quickypay.sqlite.dbHelper;

import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    private Button addStudentBtn;
    private Button refreshBtn;
    private LinearLayout studentsContainer;
    private dbHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper
        databaseHelper = new dbHelper(this);

        initializeViews();
        setupClickListeners();
        loadStudents();
    }

    private void initializeViews() {
        addStudentBtn = findViewById(R.id.addStudentBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        studentsContainer = findViewById(R.id.studentsContainer);
    }

    private void setupClickListeners() {
        // Add Student Button
        addStudentBtn.setOnClickListener(v -> {
            Intent intent = new Intent(StudentListActivity.this, AddStudent.class);
            startActivity(intent);
        });

        // Refresh Button
        refreshBtn.setOnClickListener(v -> {
            loadStudents();
            Toast.makeText(this, "Students list refreshed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadStudents() {
        // Clear existing views
        studentsContainer.removeAllViews();

        // Get students from database
        List<dbHelper.StudentWithFaculty> students = databaseHelper.getStudentsWithFaculty();

        if (students.isEmpty()) {
            // Show message if no students found
            TextView noStudentsText = new TextView(this);
            noStudentsText.setText("No students found. Add some students first!");
            noStudentsText.setTextSize(16);
            noStudentsText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noStudentsText.setPadding(32, 32, 32, 32);
            noStudentsText.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            studentsContainer.addView(noStudentsText);
        } else {
            // Display each student
            for (dbHelper.StudentWithFaculty student : students) {
                addStudentCard(student);
            }
        }
    }

    private void addStudentCard(dbHelper.StudentWithFaculty student) {
        // Create card layout
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.card_background);
        card.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(16, 8, 16, 16);
        card.setLayoutParams(layoutParams);

        // Student ID
        TextView idText = new TextView(this);
        idText.setText("ID: " + student.getStudentId());
        idText.setTextSize(14);
        idText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        card.addView(idText);

        // Username
        TextView usernameText = new TextView(this);
        usernameText.setText("Username: " + student.getUsername());
        usernameText.setTextSize(16);
        usernameText.setTextColor(getResources().getColor(android.R.color.black));
        usernameText.setPadding(0, 8, 0, 0);
        card.addView(usernameText);

        // Email
        TextView emailText = new TextView(this);
        emailText.setText("Email: " + student.getEmail());
        emailText.setTextSize(14);
        emailText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        emailText.setPadding(0, 4, 0, 0);
        card.addView(emailText);

        // Faculty
        TextView facultyText = new TextView(this);
        facultyText.setText("Faculty: " + (student.getFacultyName() != null ? student.getFacultyName() : "N/A"));
        facultyText.setTextSize(14);
        facultyText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        facultyText.setPadding(0, 4, 0, 0);
        card.addView(facultyText);

        // Faculty ID
        TextView facultyIdText = new TextView(this);
        facultyIdText.setText("Faculty ID: " + student.getFacultyId());
        facultyIdText.setTextSize(12);
        facultyIdText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        facultyIdText.setPadding(0, 4, 0, 0);
        card.addView(facultyIdText);

        // Created By
        TextView createdByText = new TextView(this);
        createdByText.setText("Created By User ID: " + student.getCreatedBy());
        createdByText.setTextSize(12);
        createdByText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        createdByText.setPadding(0, 4, 0, 0);
        card.addView(createdByText);

        // Add click listener to card
        card.setOnClickListener(v -> {
            // You can add functionality here to view/edit student details
            Toast.makeText(this, "Clicked: " + student.getUsername(), Toast.LENGTH_SHORT).show();
        });

        studentsContainer.addView(card);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from AddStudent activity
        loadStudents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}