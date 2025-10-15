package com.auca.quickypay.Model;

public class Course {
    private int courseId;
    private String courseName;
    private int facultyId;
    private int createdBy;

    public Course() {}

    public Course(String courseName, int facultyId, int createdBy) {
        this.courseName = courseName;
        this.facultyId = facultyId;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}
