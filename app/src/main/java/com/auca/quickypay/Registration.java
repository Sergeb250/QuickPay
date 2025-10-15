package com.auca.quickypay;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.auca.quickypay.Model.Student;
import com.auca.quickypay.Model.StudentCourse;
import com.auca.quickypay.sqlite.dbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Registration extends AppCompatActivity {

    private Spinner spinnerStudents, spinnerCourses, spinnerViewCourse;
    private Button btnRegister, btnViewStudents;
    private ImageButton btnBack;
    private ProgressBar progressBar;
    private TextView tvRegisteredStudents;

    private dbHelper databaseHelper;
    private List<Student> studentList;
    private List<dbHelper.CourseWithFaculty> courseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupClickListeners();
        loadSpinnerData();
    }

    private void initializeViews() {
        spinnerStudents = findViewById(R.id.spinnerStudents);
        spinnerCourses = findViewById(R.id.spinnerCourses);
        spinnerViewCourse = findViewById(R.id.spinnerViewCourse);
        btnRegister = findViewById(R.id.btnRegister);
        btnViewStudents = findViewById(R.id.btnViewStudents);

        // Fix: Changed to ImageButton since your XML uses ImageButton
        btnBack = findViewById(R.id.btnBack);

        progressBar = findViewById(R.id.progressBar);
        tvRegisteredStudents = findViewById(R.id.tvRegisteredStudents);

        databaseHelper = new dbHelper(this);
    }

    private void setupClickListeners() {
        // Add null checks for all buttons
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        } else {
            // If back button is not found, you can add a different navigation method
            Toast.makeText(this, "Back button not found", Toast.LENGTH_SHORT).show();
        }

        if (btnRegister != null) {
            btnRegister.setOnClickListener(v -> registerStudentForCourse());
        }

        if (btnViewStudents != null) {
            btnViewStudents.setOnClickListener(v -> viewRegisteredStudents());
        }

        // Auto-refresh when course selection changes
        if (spinnerViewCourse != null) {
            spinnerViewCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 && courseList != null && position < courseList.size()) {
                        viewRegisteredStudents();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }
    }

    private void loadSpinnerData() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        new Thread(() -> {
            // Load students and courses from database
            studentList = databaseHelper.getAllStudents();
            courseList = databaseHelper.getCoursesWithFaculty();

            runOnUiThread(() -> {
                setupStudentSpinner();
                setupCourseSpinners();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                // Show message if no data
                if (studentList.isEmpty()) {
                    Toast.makeText(Registration.this, "No students found. Please add students first.", Toast.LENGTH_LONG).show();
                }
                if (courseList.isEmpty()) {
                    Toast.makeText(Registration.this, "No courses found. Please add courses first.", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private void setupStudentSpinner() {
        if (spinnerStudents == null) return;

        if (studentList == null || studentList.isEmpty()) {
            // Add a placeholder if no students
            ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new String[]{"No students available"});
            placeholderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStudents.setAdapter(placeholderAdapter);
            spinnerStudents.setEnabled(false);
            return;
        }

        ArrayAdapter<Student> studentAdapter = new ArrayAdapter<Student>(this,
                android.R.layout.simple_spinner_item, studentList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (position >= 0 && position < studentList.size()) {
                    Student student = studentList.get(position);
                    textView.setText(student.getUsername() + " (" + student.getEmail() + ")");
                }
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (position >= 0 && position < studentList.size()) {
                    Student student = studentList.get(position);
                    textView.setText(student.getUsername() + " (" + student.getEmail() + ")");
                    textView.setPadding(16, 16, 16, 16);
                }
                return textView;
            }
        };

        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudents.setAdapter(studentAdapter);
        spinnerStudents.setEnabled(true);
    }

    private void setupCourseSpinners() {
        if (spinnerCourses == null || spinnerViewCourse == null) return;

        if (courseList == null || courseList.isEmpty()) {
            // Add a placeholder if no courses
            ArrayAdapter<String> placeholderAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, new String[]{"No courses available"});
            placeholderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCourses.setAdapter(placeholderAdapter);
            spinnerViewCourse.setAdapter(placeholderAdapter);
            spinnerCourses.setEnabled(false);
            spinnerViewCourse.setEnabled(false);
            return;
        }

        ArrayAdapter<dbHelper.CourseWithFaculty> courseAdapter = new ArrayAdapter<dbHelper.CourseWithFaculty>(this,
                android.R.layout.simple_spinner_item, courseList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (position >= 0 && position < courseList.size()) {
                    dbHelper.CourseWithFaculty course = courseList.get(position);
                    textView.setText(course.getCourseName());
                }
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (position >= 0 && position < courseList.size()) {
                    dbHelper.CourseWithFaculty course = courseList.get(position);
                    textView.setText(course.getCourseName() + " - " + course.getFacultyName());
                    textView.setPadding(16, 16, 16, 16);
                }
                return textView;
            }
        };

        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(courseAdapter);
        spinnerViewCourse.setAdapter(courseAdapter);
        spinnerCourses.setEnabled(true);
        spinnerViewCourse.setEnabled(true);
    }

    private void registerStudentForCourse() {
        if (studentList == null || studentList.isEmpty() || courseList == null || courseList.isEmpty()) {
            Toast.makeText(this, "No students or courses available for registration", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerStudents == null || spinnerCourses == null ||
                spinnerStudents.getSelectedItemPosition() < 0 || spinnerCourses.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Please select both student and course", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if placeholder items are selected
        if (spinnerStudents.getSelectedItem() instanceof String || spinnerCourses.getSelectedItem() instanceof String) {
            Toast.makeText(this, "Please select valid student and course", Toast.LENGTH_SHORT).show();
            return;
        }

        Student selectedStudent = (Student) spinnerStudents.getSelectedItem();
        dbHelper.CourseWithFaculty selectedCourse = (dbHelper.CourseWithFaculty) spinnerCourses.getSelectedItem();

        // Check if already registered
        if (databaseHelper.isStudentRegisteredForCourse(selectedStudent.getStudentId(), selectedCourse.getCourseId())) {
            Toast.makeText(this, "Student is already registered for this course", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        new Thread(() -> {
            // Create registration
            StudentCourse registration = new StudentCourse();
            registration.setStudentId(selectedStudent.getStudentId());
            registration.setCourseId(selectedCourse.getCourseId());
            registration.setRegistrationDate(getCurrentDateTime());

            long result = databaseHelper.registerStudentToCourse(registration);
            boolean success = result != -1;

            runOnUiThread(() -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (success) {
                    Toast.makeText(Registration.this,
                            "✓ " + selectedStudent.getUsername() + " registered for " + selectedCourse.getCourseName(),
                            Toast.LENGTH_LONG).show();

                    // Auto-select the same course in view spinner
                    if (spinnerViewCourse != null) {
                        int coursePosition = getCoursePosition(selectedCourse.getCourseId());
                        if (coursePosition != -1) {
                            spinnerViewCourse.setSelection(coursePosition);
                        }
                    }

                    // Refresh the view
                    viewRegisteredStudents();
                } else {
                    Toast.makeText(Registration.this,
                            "✗ Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void viewRegisteredStudents() {
        if (spinnerViewCourse == null || courseList == null || courseList.isEmpty() || spinnerViewCourse.getSelectedItemPosition() < 0) {
            if (tvRegisteredStudents != null) {
                tvRegisteredStudents.setText("No courses available to view registrations.");
                tvRegisteredStudents.setVisibility(View.VISIBLE);
            }
            return;
        }

        // Check if placeholder item is selected
        if (spinnerViewCourse.getSelectedItem() instanceof String) {
            if (tvRegisteredStudents != null) {
                tvRegisteredStudents.setText("Please select a valid course to view registrations.");
                tvRegisteredStudents.setVisibility(View.VISIBLE);
            }
            return;
        }

        dbHelper.CourseWithFaculty selectedCourse = (dbHelper.CourseWithFaculty) spinnerViewCourse.getSelectedItem();

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        new Thread(() -> {
            List<Student> registeredStudents = databaseHelper.getCourseStudents(selectedCourse.getCourseId());

            runOnUiThread(() -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                displayRegisteredStudents(registeredStudents, selectedCourse.getCourseName());
            });
        }).start();
    }

    private void displayRegisteredStudents(List<Student> students, String courseName) {
        if (tvRegisteredStudents == null) return;

        if (students.isEmpty()) {
            tvRegisteredStudents.setText("No students registered for:\n" + courseName + "\n\nClick 'REGISTER STUDENT' above to add students.");
            tvRegisteredStudents.setVisibility(View.VISIBLE);
            return;
        }

        StringBuilder studentListText = new StringBuilder();
        studentListText.append("📚 Students registered for:\n")
                .append(courseName).append("\n\n");

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            studentListText.append("👤 ").append(i + 1).append(". ").append(student.getUsername())
                    .append("\n   📧 ").append(student.getEmail())
                    .append("\n   🆔 ID: ").append(student.getStudentId()).append("\n\n");
        }

        studentListText.append("Total: ").append(students.size()).append(" student(s)");

        tvRegisteredStudents.setText(studentListText.toString());
        tvRegisteredStudents.setVisibility(View.VISIBLE);
    }

    private int getCoursePosition(int courseId) {
        if (courseList != null) {
            for (int i = 0; i < courseList.size(); i++) {
                if (courseList.get(i).getCourseId() == courseId) {
                    return i;
                }
            }
        }
        return -1;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to activity
        loadSpinnerData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}