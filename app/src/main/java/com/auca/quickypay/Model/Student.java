package com.auca.quickypay.Model;


public class Student {
    private int studentId;
    private String username;
    private String email;
    private String password;
    private int facultyId;
    private int createdBy;

    public Student() {}

    public Student(String username, String email, String password, int facultyId, int createdBy) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.facultyId = facultyId;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}