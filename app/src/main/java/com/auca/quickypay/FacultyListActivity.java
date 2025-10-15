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

import com.auca.quickypay.Model.Faculty;
import com.auca.quickypay.sqlite.dbHelper;

import java.util.List;

public class FacultyListActivity extends AppCompatActivity {

    private Button addFacultyBtn;
    private Button refreshBtn;
    private LinearLayout facultiesContainer;
    private dbHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize DatabaseHelper
        databaseHelper = new dbHelper(this);

        initializeViews();
        setupClickListeners();
        loadFaculties();
    }

    private void initializeViews() {
        addFacultyBtn = findViewById(R.id.addFacultyBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        facultiesContainer = findViewById(R.id.facultiesContainer);
    }

    private void setupClickListeners() {
        // Add Faculty Button
        addFacultyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(FacultyListActivity.this, AddFuculty.class);
            startActivity(intent);
        });

        // Refresh Button
        refreshBtn.setOnClickListener(v -> {
            loadFaculties();
            Toast.makeText(this, "Faculties list refreshed!", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadFaculties() {
        // Clear existing views
        facultiesContainer.removeAllViews();

        // Get faculties from database
        List<Faculty> faculties = databaseHelper.getAllFaculties();

        if (faculties.isEmpty()) {
            // Show message if no faculties found
            TextView noFacultiesText = new TextView(this);
            noFacultiesText.setText("No faculties found. Add some faculties first!");
            noFacultiesText.setTextSize(16);
            noFacultiesText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            noFacultiesText.setPadding(32, 32, 32, 32);
            noFacultiesText.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
            facultiesContainer.addView(noFacultiesText);
        } else {
            // Display each faculty
            for (Faculty faculty : faculties) {
                addFacultyCard(faculty);
            }
        }
    }

    private void addFacultyCard(Faculty faculty) {
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

        // Faculty ID
        TextView idText = new TextView(this);
        idText.setText("ID: " + faculty.getFacultyId());
        idText.setTextSize(14);
        idText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        card.addView(idText);

        // Faculty Name
        TextView nameText = new TextView(this);
        nameText.setText("Faculty: " + faculty.getFacultyName());
        nameText.setTextSize(18);
        nameText.setTextColor(getResources().getColor(android.R.color.black));
        nameText.setPadding(0, 8, 0, 0);
        card.addView(nameText);

        // Dean Name
        TextView deanText = new TextView(this);
        deanText.setText("Dean: " + faculty.getDeanName());
        deanText.setTextSize(16);
        deanText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        deanText.setPadding(0, 8, 0, 0);
        card.addView(deanText);

        // Created By
        TextView createdByText = new TextView(this);
        createdByText.setText("Created By User ID: " + faculty.getCreatedBy());
        createdByText.setTextSize(14);
        createdByText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        createdByText.setPadding(0, 8, 0, 0);
        card.addView(createdByText);

        // Action Buttons
        LinearLayout actionsLayout = new LinearLayout(this);
        actionsLayout.setOrientation(LinearLayout.HORIZONTAL);
        actionsLayout.setPadding(0, 16, 0, 0);

        // Edit Button
        Button editBtn = new Button(this);
        editBtn.setText("Edit");
        editBtn.setBackgroundResource(R.drawable.button_blue_background);
        editBtn.setTextColor(getResources().getColor(android.R.color.white));
        editBtn.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        editParams.setMarginEnd(8);
        editBtn.setLayoutParams(editParams);
        editBtn.setOnClickListener(v -> {
            editFaculty(faculty);
        });

        // Delete Button
        Button deleteBtn = new Button(this);
        deleteBtn.setText("Delete");
        deleteBtn.setBackgroundResource(R.drawable.button_red_background);
        deleteBtn.setTextColor(getResources().getColor(android.R.color.white));
        deleteBtn.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
        );
        deleteParams.setMarginStart(8);
        deleteBtn.setLayoutParams(deleteParams);
        deleteBtn.setOnClickListener(v -> {
            deleteFaculty(faculty);
        });

        actionsLayout.addView(editBtn);
        actionsLayout.addView(deleteBtn);
        card.addView(actionsLayout);

        facultiesContainer.addView(card);
    }

    private void editFaculty(Faculty faculty) {
        // Navigate to Edit Faculty activity or show edit dialog
        Toast.makeText(this, "Edit: " + faculty.getFacultyName(), Toast.LENGTH_SHORT).show();
        // You can implement edit functionality here
        // Intent intent = new Intent(this, EditFacultyActivity.class);
        // intent.putExtra("FACULTY_ID", faculty.getFacultyId());
        // startActivity(intent);
    }

    private void deleteFaculty(Faculty faculty) {
        // Show confirmation dialog before deleting
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Delete Faculty");
        builder.setMessage("Are you sure you want to delete " + faculty.getFacultyName() + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            databaseHelper.deleteFaculty(faculty.getFacultyId());
            Toast.makeText(this, faculty.getFacultyName() + " deleted successfully!", Toast.LENGTH_SHORT).show();
            loadFaculties(); // Refresh the list
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when returning from AddFaculty activity
        loadFaculties();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}