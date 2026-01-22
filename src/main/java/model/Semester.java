package model;

public class Semester {

    private int id;
    private int userId;
    private String semesterName; // "Fall 2024", "1st Semester", etc.
    private Double gpa; // Computed GPA (nullable - can be null if not calculated yet)

    // Constructor with all fields
    public Semester(int id, int userId, String semesterName, Double gpa) {
        this.id = id;
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = gpa;
    }

    // Constructor without ID (for creating new records)
    public Semester(int userId, String semesterName, Double gpa) {
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = gpa;
    }

    // Constructor without ID and GPA (for initial creation)
    public Semester(int userId, String semesterName) {
        this.userId = userId;
        this.semesterName = semesterName;
        this.gpa = null; // GPA not calculated yet
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

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    // Override toString for display
    @Override
    public String toString() {
        if (gpa != null) {
            return semesterName + " (GPA: " + String.format("%.2f", gpa) + ")";
        } else {
            return semesterName + " (GPA: Not Calculated)";
        }
    }
}