package com.auca.quickypay.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.auca.quickypay.Model.User;
import com.auca.quickypay.Model.Faculty;
import com.auca.quickypay.Model.Student;
import com.auca.quickypay.Model.Course;
import com.auca.quickypay.Model.StudentCourse;
import java.util.ArrayList;
import java.util.List;

public class dbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuickyPay.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_USER = "users";
    private static final String TABLE_FACULTY = "faculties";
    private static final String TABLE_STUDENT = "students";
    private static final String TABLE_COURSE = "courses";
    private static final String TABLE_STUDENT_COURSES = "student_courses";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String KEY_FACULTY_ID = "faculty_id";
    private static final String KEY_FACULTY_NAME = "faculty_name";
    private static final String KEY_DEAN_NAME = "dean_name";
    private static final String KEY_CREATED_BY = "created_by";

    private static final String KEY_STUDENT_ID = "student_id";
    private static final String KEY_STUDENT_USERNAME = "username";
    private static final String KEY_STUDENT_EMAIL = "email";
    private static final String KEY_STUDENT_PASSWORD = "password";
    private static final String KEY_STUDENT_FACULTY_ID = "faculty_id";

    private static final String KEY_COURSE_ID = "course_id";
    private static final String KEY_COURSE_NAME = "course_name";
    private static final String KEY_COURSE_FACULTY_ID = "faculty_id";

    private static final String KEY_REGISTRATION_ID = "registration_id";
    private static final String KEY_REGISTRATION_STUDENT_ID = "student_id";
    private static final String KEY_REGISTRATION_COURSE_ID = "course_id";
    private static final String KEY_REGISTRATION_DATE = "registration_date";

    public dbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_FACULTY_TABLE = "CREATE TABLE " + TABLE_FACULTY + "("
                + KEY_FACULTY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FACULTY_NAME + " TEXT NOT NULL,"
                + KEY_DEAN_NAME + " TEXT,"
                + KEY_CREATED_BY + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + KEY_CREATED_BY + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_FACULTY_TABLE);

        String CREATE_STUDENT_TABLE = "CREATE TABLE " + TABLE_STUDENT + "("
                + KEY_STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_STUDENT_USERNAME + " TEXT,"
                + KEY_STUDENT_EMAIL + " TEXT,"
                + KEY_STUDENT_PASSWORD + " TEXT,"
                + KEY_STUDENT_FACULTY_ID + " INTEGER,"
                + KEY_CREATED_BY + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + KEY_STUDENT_FACULTY_ID + ") REFERENCES " + TABLE_FACULTY + "(" + KEY_FACULTY_ID + "),"
                + "FOREIGN KEY(" + KEY_CREATED_BY + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_STUDENT_TABLE);

        String CREATE_COURSE_TABLE = "CREATE TABLE " + TABLE_COURSE + "("
                + KEY_COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_COURSE_NAME + " TEXT NOT NULL,"
                + KEY_COURSE_FACULTY_ID + " INTEGER,"
                + KEY_CREATED_BY + " INTEGER NOT NULL,"
                + "FOREIGN KEY(" + KEY_COURSE_FACULTY_ID + ") REFERENCES " + TABLE_FACULTY + "(" + KEY_FACULTY_ID + "),"
                + "FOREIGN KEY(" + KEY_CREATED_BY + ") REFERENCES " + TABLE_USER + "(" + COLUMN_ID + "))";
        db.execSQL(CREATE_COURSE_TABLE);

        String CREATE_STUDENT_COURSE_TABLE = "CREATE TABLE " + TABLE_STUDENT_COURSES + "("
                + KEY_REGISTRATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_REGISTRATION_STUDENT_ID + " INTEGER NOT NULL,"
                + KEY_REGISTRATION_COURSE_ID + " INTEGER NOT NULL,"
                + KEY_REGISTRATION_DATE + " TEXT NOT NULL,"
                + "FOREIGN KEY(" + KEY_REGISTRATION_STUDENT_ID + ") REFERENCES " + TABLE_STUDENT + "(" + KEY_STUDENT_ID + "),"
                + "FOREIGN KEY(" + KEY_REGISTRATION_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + KEY_COURSE_ID + "),"
                + "UNIQUE(" + KEY_REGISTRATION_STUDENT_ID + ", " + KEY_REGISTRATION_COURSE_ID + "))";
        db.execSQL(CREATE_STUDENT_COURSE_TABLE);

        // Auto-insert default faculties
        insertDefaultFaculties(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACULTY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    // Method to insert default faculties
    private void insertDefaultFaculties(SQLiteDatabase db) {
        // First, check if we have at least one admin user
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + TABLE_USER + " LIMIT 1", null);
        int adminUserId = 1; // Default admin user ID

        if (cursor.moveToFirst()) {
            adminUserId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        } else {
            // If no users exist, create a default admin user first
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USERNAME, "admin");
            userValues.put(COLUMN_EMAIL, "admin@auca.ac.rw");
            userValues.put(COLUMN_PASSWORD, "admin123");
            long userId = db.insert(TABLE_USER, null, userValues);
            if (userId != -1) {
                adminUserId = (int) userId;
            }
        }
        cursor.close();

        // Insert default faculties
        String[] defaultFaculties = {"BUSINESS", "INFORMATION TECH", "THEOLOGY"};

        for (String facultyName : defaultFaculties) {
            ContentValues values = new ContentValues();
            values.put(KEY_FACULTY_NAME, facultyName);
            values.put(KEY_DEAN_NAME, "Dean of " + facultyName);
            values.put(KEY_CREATED_BY, adminUserId);
            db.insert(TABLE_FACULTY, null, values);
        }
    }

    // Method to manually trigger faculty insertion (useful for testing)
    public void insertDefaultFaculties() {
        SQLiteDatabase db = this.getWritableDatabase();
        insertDefaultFaculties(db);
        // Don't close the database here
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        long result = db.insert(TABLE_USER, null, values);
        // REMOVED: db.close();
        return result != -1;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_EMAIL}, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        // REMOVED: db.close();
        return exists;
    }

    public boolean updateUser(String originalEmail, User updatedUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, updatedUser.getUsername());
        values.put(COLUMN_EMAIL, updatedUser.getEmail());
        values.put(COLUMN_PASSWORD, updatedUser.getPassword());
        int rows = db.update(TABLE_USER, values, COLUMN_EMAIL + "=?", new String[]{originalEmail});
        // REMOVED: db.close();
        return rows > 0;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_EMAIL + "=?", new String[]{email}, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        }
        cursor.close();
        // REMOVED: db.close();
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
                users.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return users;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        // REMOVED: db.close();
        return exists;
    }

    public boolean updatePassword(String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rows = db.update(TABLE_USER, values, COLUMN_EMAIL + "=?", new String[]{email});
        // REMOVED: db.close();
        return rows > 0;
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_USER, COLUMN_EMAIL + "=?", new String[]{email});
        // REMOVED: db.close();
        return rows > 0;
    }

    public long addFaculty(Faculty faculty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FACULTY_NAME, faculty.getFacultyName());
        values.put(KEY_DEAN_NAME, faculty.getDeanName());
        values.put(KEY_CREATED_BY, faculty.getCreatedBy());
        long result = db.insert(TABLE_FACULTY, null, values);
        // REMOVED: db.close();
        return result;
    }

    public Faculty getFaculty(int facultyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FACULTY, new String[]{KEY_FACULTY_ID, KEY_FACULTY_NAME, KEY_DEAN_NAME, KEY_CREATED_BY}, KEY_FACULTY_ID + "=?", new String[]{String.valueOf(facultyId)}, null, null, null);
        Faculty faculty = null;
        if (cursor != null && cursor.moveToFirst()) {
            faculty = new Faculty();
            faculty.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FACULTY_ID)));
            faculty.setFacultyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY_NAME)));
            faculty.setDeanName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEAN_NAME)));
            faculty.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
            cursor.close();
        }
        // REMOVED: db.close();
        return faculty;
    }

    public List<Faculty> getAllFaculties() {
        List<Faculty> facultyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FACULTY, null);
        if (cursor.moveToFirst()) {
            do {
                Faculty faculty = new Faculty();
                faculty.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FACULTY_ID)));
                faculty.setFacultyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY_NAME)));
                faculty.setDeanName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DEAN_NAME)));
                faculty.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                facultyList.add(faculty);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return facultyList;
    }

    public int updateFaculty(Faculty faculty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_FACULTY_NAME, faculty.getFacultyName());
        values.put(KEY_DEAN_NAME, faculty.getDeanName());
        int result = db.update(TABLE_FACULTY, values, KEY_FACULTY_ID + " = ?", new String[]{String.valueOf(faculty.getFacultyId())});
        // REMOVED: db.close();
        return result;
    }

    public void deleteFaculty(int facultyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FACULTY, KEY_FACULTY_ID + " = ?", new String[]{String.valueOf(facultyId)});
        // REMOVED: db.close();
    }

    public long addStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_USERNAME, student.getUsername());
        values.put(KEY_STUDENT_EMAIL, student.getEmail());
        values.put(KEY_STUDENT_PASSWORD, student.getPassword());
        values.put(KEY_STUDENT_FACULTY_ID, student.getFacultyId());
        values.put(KEY_CREATED_BY, student.getCreatedBy());
        long result = db.insert(TABLE_STUDENT, null, values);
        // REMOVED: db.close();
        return result;
    }

    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STUDENT, null);
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_ID)));
                student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_USERNAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_EMAIL)));
                student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_PASSWORD)));
                student.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_FACULTY_ID)));
                student.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return studentList;
    }

    public List<StudentWithFaculty> getStudentsWithFaculty() {
        List<StudentWithFaculty> studentList = new ArrayList<>();
        String selectQuery = "SELECT s." + KEY_STUDENT_ID + ", s." + KEY_STUDENT_USERNAME + ", s." + KEY_STUDENT_EMAIL +
                ", s." + KEY_STUDENT_PASSWORD + ", s." + KEY_STUDENT_FACULTY_ID + ", s." + KEY_CREATED_BY +
                ", f." + KEY_FACULTY_NAME + " FROM " + TABLE_STUDENT + " s " +
                "LEFT JOIN " + TABLE_FACULTY + " f ON s." + KEY_STUDENT_FACULTY_ID + " = f." + KEY_FACULTY_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                StudentWithFaculty student = new StudentWithFaculty();
                student.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_ID)));
                student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_USERNAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_EMAIL)));
                student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_PASSWORD)));
                student.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_FACULTY_ID)));
                student.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                student.setFacultyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY_NAME)));
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return studentList;
    }

    public List<Student> getStudentsByFaculty(int facultyId) {
        List<Student> studentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT, new String[]{KEY_STUDENT_ID, KEY_STUDENT_USERNAME, KEY_STUDENT_EMAIL, KEY_STUDENT_PASSWORD, KEY_STUDENT_FACULTY_ID, KEY_CREATED_BY}, KEY_STUDENT_FACULTY_ID + "=?", new String[]{String.valueOf(facultyId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_ID)));
                student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_USERNAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_EMAIL)));
                student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_PASSWORD)));
                student.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_FACULTY_ID)));
                student.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                studentList.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return studentList;
    }

    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STUDENT_USERNAME, student.getUsername());
        values.put(KEY_STUDENT_EMAIL, student.getEmail());
        values.put(KEY_STUDENT_PASSWORD, student.getPassword());
        values.put(KEY_STUDENT_FACULTY_ID, student.getFacultyId());
        int result = db.update(TABLE_STUDENT, values, KEY_STUDENT_ID + " = ?", new String[]{String.valueOf(student.getStudentId())});
        // REMOVED: db.close();
        return result;
    }

    public void deleteStudent(int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_COURSES, KEY_REGISTRATION_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        db.delete(TABLE_STUDENT, KEY_STUDENT_ID + " = ?", new String[]{String.valueOf(studentId)});
        // REMOVED: db.close();
    }

    public long addCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_NAME, course.getCourseName());
        values.put(KEY_COURSE_FACULTY_ID, course.getFacultyId());
        values.put(KEY_CREATED_BY, course.getCreatedBy());
        long result = db.insert(TABLE_COURSE, null, values);
        // REMOVED: db.close();
        return result;
    }

    public List<Course> getCoursesByFaculty(int facultyId) {
        List<Course> courseList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_COURSE, new String[]{KEY_COURSE_ID, KEY_COURSE_NAME, KEY_COURSE_FACULTY_ID, KEY_CREATED_BY}, KEY_COURSE_FACULTY_ID + "=?", new String[]{String.valueOf(facultyId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID)));
                course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COURSE_NAME)));
                course.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_FACULTY_ID)));
                course.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                courseList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return courseList;
    }

    public List<CourseWithFaculty> getCoursesWithFaculty() {
        List<CourseWithFaculty> courseList = new ArrayList<>();
        String selectQuery = "SELECT c." + KEY_COURSE_ID + ", c." + KEY_COURSE_NAME +
                ", c." + KEY_COURSE_FACULTY_ID + ", c." + KEY_CREATED_BY +
                ", f." + KEY_FACULTY_NAME + " FROM " + TABLE_COURSE + " c " +
                "LEFT JOIN " + TABLE_FACULTY + " f ON c." + KEY_COURSE_FACULTY_ID + " = f." + KEY_FACULTY_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                CourseWithFaculty course = new CourseWithFaculty();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID)));
                course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COURSE_NAME)));
                course.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_FACULTY_ID)));
                course.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                course.setFacultyName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_FACULTY_NAME)));
                courseList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return courseList;
    }

    public int updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_COURSE_NAME, course.getCourseName());
        values.put(KEY_COURSE_FACULTY_ID, course.getFacultyId());
        int result = db.update(TABLE_COURSE, values, KEY_COURSE_ID + " = ?", new String[]{String.valueOf(course.getCourseId())});
        // REMOVED: db.close();
        return result;
    }

    public void deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_STUDENT_COURSES, KEY_REGISTRATION_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        db.delete(TABLE_COURSE, KEY_COURSE_ID + " = ?", new String[]{String.valueOf(courseId)});
        // REMOVED: db.close();
    }

    public long registerStudentToCourse(StudentCourse studentCourse) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REGISTRATION_STUDENT_ID, studentCourse.getStudentId());
        values.put(KEY_REGISTRATION_COURSE_ID, studentCourse.getCourseId());
        values.put(KEY_REGISTRATION_DATE, studentCourse.getRegistrationDate());
        long result = db.insert(TABLE_STUDENT_COURSES, null, values);
        // REMOVED: db.close();
        return result;
    }

    public boolean isStudentRegisteredForCourse(int studentId, int courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_STUDENT_COURSES, new String[]{KEY_REGISTRATION_ID}, KEY_REGISTRATION_STUDENT_ID + "=? AND " + KEY_REGISTRATION_COURSE_ID + "=?", new String[]{String.valueOf(studentId), String.valueOf(courseId)}, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        // REMOVED: db.close();
        return exists;
    }

    public List<Course> getStudentCourses(int studentId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.* FROM " + TABLE_COURSE + " c " +
                "INNER JOIN " + TABLE_STUDENT_COURSES + " sc ON c." + KEY_COURSE_ID +
                " = sc." + KEY_REGISTRATION_COURSE_ID +
                " WHERE sc." + KEY_REGISTRATION_STUDENT_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course();
                course.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_ID)));
                course.setCourseName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_COURSE_NAME)));
                course.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_COURSE_FACULTY_ID)));
                course.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                courses.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return courses;
    }

    public List<Student> getCourseStudents(int courseId) {
        List<Student> students = new ArrayList<>();
        String query = "SELECT s.* FROM " + TABLE_STUDENT + " s " +
                "INNER JOIN " + TABLE_STUDENT_COURSES + " sc ON s." + KEY_STUDENT_ID +
                " = sc." + KEY_REGISTRATION_STUDENT_ID +
                " WHERE sc." + KEY_REGISTRATION_COURSE_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(courseId)});
        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setStudentId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_ID)));
                student.setUsername(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_USERNAME)));
                student.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_EMAIL)));
                student.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(KEY_STUDENT_PASSWORD)));
                student.setFacultyId(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_STUDENT_FACULTY_ID)));
                student.setCreatedBy(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CREATED_BY)));
                students.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // REMOVED: db.close();
        return students;
    }

    public boolean unregisterStudentFromCourse(int studentId, int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_STUDENT_COURSES, KEY_REGISTRATION_STUDENT_ID + " = ? AND " + KEY_REGISTRATION_COURSE_ID + " = ?", new String[]{String.valueOf(studentId), String.valueOf(courseId)});
        // REMOVED: db.close();
        return result > 0;
    }

    public static class StudentWithFaculty extends Student {
        private String facultyName;
        public String getFacultyName() { return facultyName; }
        public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    }

    public static class CourseWithFaculty extends Course {
        private String facultyName;
        public String getFacultyName() { return facultyName; }
        public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    }
}