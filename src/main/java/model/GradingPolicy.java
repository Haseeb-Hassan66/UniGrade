package model;

public class GradingPolicy {

    private int id;
    private int universityId;
    private String category;      // "Theory" or "Practical"
    private String gradeName;     // "A", "A-", "B+", etc.
    private double gradePoint;    // 4.0, 3.67, etc.
    private double minMarks;      // Minimum marks for this grade
    private double maxMarks;      // Maximum marks for this grade

    // Constructor with all fields
    public GradingPolicy(int id, int universityId, String category, String gradeName, 
                        double gradePoint, double minMarks, double maxMarks) {
        this.id = id;
        this.universityId = universityId;
        this.category = category;
        this.gradeName = gradeName;
        this.gradePoint = gradePoint;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
    }

    // Constructor without ID (for creating new records)
    public GradingPolicy(int universityId, String category, String gradeName, 
                        double gradePoint, double minMarks, double maxMarks) {
        this.universityId = universityId;
        this.category = category;
        this.gradeName = gradeName;
        this.gradePoint = gradePoint;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
    }

    // No-arg constructor
    public GradingPolicy() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUniversityId() {
        return universityId;
    }

    public void setUniversityId(int universityId) {
        this.universityId = universityId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }

    public double getMinMarks() {
        return minMarks;
    }

    public void setMinMarks(double minMarks) {
        this.minMarks = minMarks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }
}