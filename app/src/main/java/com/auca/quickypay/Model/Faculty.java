package com.auca.quickypay.Model;

public class Faculty {
    private int facultyId;
    private String facultyName;
    private String deanName;
    private int createdBy;

    public Faculty() {}

    public Faculty(String facultyName, String deanName, int createdBy) {
        this.facultyName = facultyName;
        this.deanName = deanName;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }
    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }
    public String getDeanName() { return deanName; }
    public void setDeanName(String deanName) { this.deanName = deanName; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
}