package model;

public class Semester {

    private int id;
    private int userId;
    private String semesterName;  // "Fall 2024", "1st Semester", etc.
    private double gpa;           // Computed GPA (auto-calculated)

    // Constructor with all fields
    public Semester(int id, int userId, String semesterName, double gpa) {
        this.id = id;
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = gpa;
    }

    // Constructor without ID (for creating new records)
    public Semester(int userId, String semesterName, double gpa) {
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = gpa;
    }

    // Constructor without ID and GPA (for initial creation)
    public Semester(int userId, String semesterName) {
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = 0.0;  // Default GPA
    }

    // No-arg constructor
    public Semester() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    // Override toString for display
    @Override
    public String toString() {
        return semesterName + " (GPA: " + String.format("%.2f", gpa) + ")";
    }
}