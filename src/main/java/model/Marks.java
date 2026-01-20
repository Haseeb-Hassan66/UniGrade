package model;

public class Marks {

    private int id;
    private int subjectId;
    private String category; // 'Theory' or 'Practical'
    private String componentName; // 'Mid', 'Final', etc.
    private Double obtainedMarks; // Nullable (if not entered yet)
    private double maxMarks;

    // Default Constructor
    public Marks() {
    }

    // Constructor without ID (for new entries)
    public Marks(int subjectId, String category, String componentName, Double obtainedMarks, double maxMarks) {
        this.subjectId = subjectId;
        this.category = category;
        this.componentName = componentName;
        this.obtainedMarks = obtainedMarks;
        this.maxMarks = maxMarks;
    }

    // Constructor with ID
    public Marks(int id, int subjectId, String category, String componentName, Double obtainedMarks, double maxMarks) {
        this.id = id;
        this.subjectId = subjectId;
        this.category = category;
        this.componentName = componentName;
        this.obtainedMarks = obtainedMarks;
        this.maxMarks = maxMarks;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
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

    public Double getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(Double obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    @Override
    public String toString() {
        return componentName + ": " + obtainedMarks + " / " + maxMarks;
    }
}
