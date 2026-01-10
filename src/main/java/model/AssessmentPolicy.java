package model;

public class AssessmentPolicy {

    private int id;
    private int universityId;
    private String category;        // "Theory" or "Practical"
    private String componentName;   // "Mid", "Final", "Sessional", "Lab", "Viva", etc.
    private double maxMarks;        // Maximum marks for this component

    // Constructor with all fields
    public AssessmentPolicy(int id, int universityId, String category, 
                           String componentName, double maxMarks) {
        this.id = id;
        this.universityId = universityId;
        this.category = category;
        this.componentName = componentName;
        this.maxMarks = maxMarks;
    }

    // Constructor without ID (for creating new records)
    public AssessmentPolicy(int universityId, String category, 
                           String componentName, double maxMarks) {
        this.universityId = universityId;
        this.category = category;
        this.componentName = componentName;
        this.maxMarks = maxMarks;
    }

    // No-arg constructor
    public AssessmentPolicy() {
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

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }
}