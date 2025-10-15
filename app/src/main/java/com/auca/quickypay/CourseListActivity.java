package com.auca.quickypay;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.auca.quickypay.Model.Course;
import com.auca.quickypay.Model.Faculty;
import com.auca.quickypay.Model.Student;
import com.auca.quickypay.sqlite.dbHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView courseRecyclerView;
    private CourseAdapter courseAdapter;
    private List<dbHelper.CourseWithFaculty> courseList;
    private List<dbHelper.CourseWithFaculty> filteredCourseList;
    private dbHelper databaseHelper;
    private SearchView searchView;
    private ProgressBar progressBar;
    private LinearLayout emptyState;

    // Dialog views
    private CardView courseDialog;
    private TextInputEditText etCourseName;
    private Spinner spinnerCourseFaculty;
    private Button btnCourseSave, btnCourseCancel, btnCourseDelete;
    private TextView courseDialogTitle;

    private List<Faculty> facultyList;
    private ArrayAdapter<Faculty> facultyAdapter;
    private int currentCourseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupRecyclerView();
        loadCourses();
        setupSearch();
    }

    private void initializeViews() {
        courseRecyclerView = findViewById(R.id.courseRecyclerView);
        searchView = findViewById(R.id.searchView);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyCourseState);

        // Initialize database helper
        databaseHelper = new dbHelper(this);

        // Initialize dialog views
        courseDialog = findViewById(R.id.courseDialog);
        etCourseName = findViewById(R.id.etCourseName);
        spinnerCourseFaculty = findViewById(R.id.spinnerCourseFaculty);
        btnCourseSave = findViewById(R.id.btnCourseSave);
        btnCourseCancel = findViewById(R.id.btnCourseCancel);
        btnCourseDelete = findViewById(R.id.btnCourseDelete);
        courseDialogTitle = findViewById(R.id.courseDialogTitle);

        // Setup faculty spinner
        facultyList = databaseHelper.getAllFaculties();
        facultyAdapter = new ArrayAdapter<Faculty>(this, android.R.layout.simple_spinner_item, facultyList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position >= 0 && position < facultyList.size()) {
                    textView.setText(facultyList.get(position).getFacultyName());
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position >= 0 && position < facultyList.size()) {
                    textView.setText(facultyList.get(position).getFacultyName());
                }
                return view;
            }
        };
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseFaculty.setAdapter(facultyAdapter);

        // Setup dialog buttons
        setupDialogListeners();

        // Add course button
        Button btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAddCourse.setOnClickListener(v -> showAddCourseDialog());

        // Back button
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        courseList = new ArrayList<>();
        filteredCourseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(filteredCourseList);
        courseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseRecyclerView.setAdapter(courseAdapter);
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCourses(newText);
                return true;
            }
        });
    }

    private void filterCourses(String query) {
        filteredCourseList.clear();
        if (query.isEmpty()) {
            filteredCourseList.addAll(courseList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (dbHelper.CourseWithFaculty course : courseList) {
                if (course.getCourseName().toLowerCase().contains(lowerCaseQuery) ||
                        course.getFacultyName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredCourseList.add(course);
                }
            }
        }
        courseAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadCourses() {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<dbHelper.CourseWithFaculty> courses = databaseHelper.getCoursesWithFaculty();

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                courseList.clear();
                courseList.addAll(courses);
                filteredCourseList.clear();
                filteredCourseList.addAll(courses);
                courseAdapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }).start();
    }

    private void updateEmptyState() {
        if (filteredCourseList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            courseRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            courseRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupDialogListeners() {
        btnCourseSave.setOnClickListener(v -> saveCourse());
        btnCourseCancel.setOnClickListener(v -> hideCourseDialog());
        btnCourseDelete.setOnClickListener(v -> deleteCourse());
    }

    private void showAddCourseDialog() {
        currentCourseId = -1;
        courseDialogTitle.setText("Add Course");
        etCourseName.setText("");
        spinnerCourseFaculty.setSelection(0);
        btnCourseDelete.setVisibility(View.GONE);
        courseDialog.setVisibility(View.VISIBLE);
    }

    private void showEditCourseDialog(dbHelper.CourseWithFaculty course) {
        currentCourseId = course.getCourseId();
        courseDialogTitle.setText("Edit Course");
        etCourseName.setText(course.getCourseName());

        // Set faculty selection
        for (int i = 0; i < facultyList.size(); i++) {
            if (facultyList.get(i).getFacultyId() == course.getFacultyId()) {
                spinnerCourseFaculty.setSelection(i);
                break;
            }
        }

        btnCourseDelete.setVisibility(View.VISIBLE);
        courseDialog.setVisibility(View.VISIBLE);
    }

    private void hideCourseDialog() {
        courseDialog.setVisibility(View.GONE);
    }

    private void saveCourse() {
        String courseName = etCourseName.getText().toString().trim();
        Faculty selectedFaculty = (Faculty) spinnerCourseFaculty.getSelectedItem();

        if (courseName.isEmpty()) {
            Toast.makeText(this, "Please enter course name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedFaculty == null) {
            Toast.makeText(this, "Please select a faculty", Toast.LENGTH_SHORT).show();
            return;
        }

        Course course = new Course();
        course.setCourseName(courseName);
        course.setFacultyId(selectedFaculty.getFacultyId());
        course.setCreatedBy(1); // Assuming current user ID is 1

        new Thread(() -> {
            boolean success;
            if (currentCourseId == -1) {
                success = databaseHelper.addCourse(course) != -1;
            } else {
                course.setCourseId(currentCourseId);
                success = databaseHelper.updateCourse(course) > 0;
            }

            runOnUiThread(() -> {
                if (success) {
                    Toast.makeText(CourseListActivity.this,
                            currentCourseId == -1 ? "Course added successfully" : "Course updated successfully",
                            Toast.LENGTH_SHORT).show();
                    hideCourseDialog();
                    loadCourses();
                } else {
                    Toast.makeText(CourseListActivity.this, "Operation failed", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void deleteCourse() {
        if (currentCourseId == -1) return;

        new AlertDialog.Builder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    new Thread(() -> {
                        databaseHelper.deleteCourse(currentCourseId);
                        runOnUiThread(() -> {
                            Toast.makeText(CourseListActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                            hideCourseDialog();
                            loadCourses();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Course Adapter class
    private class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

        private List<dbHelper.CourseWithFaculty> courses;

        public CourseAdapter(List<dbHelper.CourseWithFaculty> courses) {
            this.courses = courses;
        }

        @NonNull
        @Override
        public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_with_students, parent, false);
            return new CourseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
            dbHelper.CourseWithFaculty course = courses.get(position);
            holder.bind(course);
        }

        @Override
        public int getItemCount() {
            return courses.size();
        }

        class CourseViewHolder extends RecyclerView.ViewHolder {
            private TextView tvCourseName, tvFacultyName, tvStudentCount;
            private Button btnEdit, btnDelete, btnViewStudents;
            private LinearLayout studentsLayout;

            public CourseViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCourseName = itemView.findViewById(R.id.tvCourseName);
                tvFacultyName = itemView.findViewById(R.id.tvFacultyName);
                tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                btnViewStudents = itemView.findViewById(R.id.btnViewStudents);
                studentsLayout = itemView.findViewById(R.id.studentsLayout);
            }

            public void bind(dbHelper.CourseWithFaculty course) {
                tvCourseName.setText(course.getCourseName());
                tvFacultyName.setText("Faculty: " + course.getFacultyName());

                // Load students for this course
                loadStudentsForCourse(course.getCourseId());

                btnEdit.setOnClickListener(v -> showEditCourseDialog(course));
                btnDelete.setOnClickListener(v -> confirmDeleteCourse(course));
                btnViewStudents.setOnClickListener(v -> toggleStudentsVisibility());
            }

            private void loadStudentsForCourse(int courseId) {
                new Thread(() -> {
                    // Note: You'll need to create a method in dbHelper to get students by course
                    // For now, we'll get all students and filter by faculty
                    List<Student> allStudents = databaseHelper.getAllStudents();
                    List<Student> courseStudents = new ArrayList<>();

                    for (Student student : allStudents) {
                        // Assuming student faculty matches course faculty
                        // You might want to create a proper student-course relationship table
                        if (student.getFacultyId() == courseId) {
                            courseStudents.add(student);
                        }
                    }

                    runOnUiThread(() -> {
                        tvStudentCount.setText("Students: " + courseStudents.size());
                        displayStudents(courseStudents);
                    });
                }).start();
            }

            private void displayStudents(List<Student> students) {
                studentsLayout.removeAllViews();

                for (Student student : students) {
                    View studentView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_student_simple, studentsLayout, false);

                    TextView tvStudentName = studentView.findViewById(R.id.tvStudentName);
                    TextView tvStudentEmail = studentView.findViewById(R.id.tvStudentEmail);

                    tvStudentName.setText(student.getUsername());
                    tvStudentEmail.setText(student.getEmail());

                    studentsLayout.addView(studentView);
                }
            }

            private void toggleStudentsVisibility() {
                int visibility = studentsLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                studentsLayout.setVisibility(visibility);
            }

            private void confirmDeleteCourse(dbHelper.CourseWithFaculty course) {
                new AlertDialog.Builder(itemView.getContext())
                        .setTitle("Delete Course")
                        .setMessage("Are you sure you want to delete " + course.getCourseName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            new Thread(() -> {
                                databaseHelper.deleteCourse(course.getCourseId());
                                runOnUiThread(() -> {
                                    Toast.makeText(itemView.getContext(), "Course deleted", Toast.LENGTH_SHORT).show();
                                    loadCourses();
                                });
                            }).start();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        }
    }
}