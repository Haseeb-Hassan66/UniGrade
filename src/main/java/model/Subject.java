package model;

public class Subject {

    private int id;
    private int semesterId;
    private String subjectName;
    private boolean hasPractical;
    private int theoryCreditHours;
    private Integer practicalCreditHours;  // Integer (nullable) instead of int
    private String theoryGrade;
    private String practicalGrade;

    // Constructor with all fields
    public Subject(int id, int semesterId, String subjectName, boolean hasPractical,
                   int theoryCreditHours, Integer practicalCreditHours,
                   String theoryGrade, String practicalGrade) {
        this.id = id;
        this.semesterId = semesterId;
        this.subjectName = subjectName;
        this.hasPractical = hasPractical;
        this.theoryCreditHours = theoryCreditHours;
        this.practicalCreditHours = practicalCreditHours;
        this.theoryGrade = theoryGrade;
        this.practicalGrade = practicalGrade;
    }

    // Constructor without ID (for creating new records)
    public Subject(int semesterId, String subjectName, boolean hasPractical,
                   int theoryCreditHours, Integer practicalCreditHours) {
        this.semesterId = semesterId;
        this.subjectName = subjectName;
        this.hasPractical = hasPractical;
        this.theoryCreditHours = theoryCreditHours;
        this.practicalCreditHours = practicalCreditHours;
        this.theoryGrade = null;
        this.practicalGrade = null;
    }

    // No-arg constructor
    public Subject() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isHasPractical() {
        return hasPractical;
    }

    public void setHasPractical(boolean hasPractical) {
        this.hasPractical = hasPractical;
    }

    public int getTheoryCreditHours() {
        return theoryCreditHours;
    }

    public void setTheoryCreditHours(int theoryCreditHours) {
        this.theoryCreditHours = theoryCreditHours;
    }

    public Integer getPracticalCreditHours() {
        return practicalCreditHours;
    }

    public void setPracticalCreditHours(Integer practicalCreditHours) {
        this.practicalCreditHours = practicalCreditHours;
    }

    public String getTheoryGrade() {
        return theoryGrade;
    }

    public void setTheoryGrade(String theoryGrade) {
        this.theoryGrade = theoryGrade;
    }

    public String getPracticalGrade() {
        return practicalGrade;
    }

    public void setPracticalGrade(String practicalGrade) {
        this.practicalGrade = practicalGrade;
    }

    // Helper method: Get total credit hours
    public int getTotalCreditHours() {
        int total = theoryCreditHours;
        if (practicalCreditHours != null) {
            total += practicalCreditHours;
        }
        return total;
    }

    // Helper method: Format credit hours for display
    public String getCreditHoursDisplay() {
        if (hasPractical && practicalCreditHours != null) {
            return theoryCreditHours + " + " + practicalCreditHours;
        } else {
            return String.valueOf(theoryCreditHours);
        }
    }

    // Helper method: Format grade for display
    public String getGradeDisplay() {
        if (theoryGrade == null) {
            return "Not yet entered";
        }
        if (hasPractical && practicalGrade != null) {
            return theoryGrade + " / " + practicalGrade;
        } else {
            return theoryGrade;
        }
    }

    @Override
    public String toString() {
        return subjectName + " (" + getCreditHoursDisplay() + ")";
    }
}