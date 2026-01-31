package model;

public class Subject {

    private int id;
    private int semesterId;
    private String subjectName;
    private boolean hasPractical;
    private int theoryCreditHours;
    private Integer practicalCreditHours; // Integer (nullable) instead of int
    private String theoryGrade;
    private String practicalGrade;
    private Double theoryGradePoint;
    private Double practicalGradePoint;

    // Constructor with all fields
    public Subject(int id, int semesterId, String subjectName, boolean hasPractical,
            int theoryCreditHours, Integer practicalCreditHours,
            String theoryGrade, String practicalGrade,
            Double theoryGradePoint, Double practicalGradePoint) {
        this.id = id;
        this.semesterId = semesterId;
        this.subjectName = subjectName;
        this.hasPractical = hasPractical;
        this.theoryCreditHours = theoryCreditHours;
        this.practicalCreditHours = practicalCreditHours;
        this.theoryGrade = theoryGrade;
        this.practicalGrade = practicalGrade;
        this.theoryGradePoint = theoryGradePoint;
        this.practicalGradePoint = practicalGradePoint;
    }

    // Kept for backward compatibility if needed, or we can update callers.
    // Ideally we update all callers, but for safety in this refactor I'll keep the
    // old constructor
    // and chain it or set defaults.
    public Subject(int id, int semesterId, String subjectName, boolean hasPractical,
            int theoryCreditHours, Integer practicalCreditHours,
            String theoryGrade, String practicalGrade) {
        this(id, semesterId, subjectName, hasPractical, theoryCreditHours, practicalCreditHours,
                theoryGrade, practicalGrade, null, null);
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
        this.theoryGradePoint = null;
        this.practicalGradePoint = null;
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

    public Double getTheoryGradePoint() {
        return theoryGradePoint;
    }

    public void setTheoryGradePoint(Double theoryGradePoint) {
        this.theoryGradePoint = theoryGradePoint;
    }

    public Double getPracticalGradePoint() {
        return practicalGradePoint;
    }

    public void setPracticalGradePoint(Double practicalGradePoint) {
        this.practicalGradePoint = practicalGradePoint;
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

        String tDisplay = theoryGrade;
        if (theoryGradePoint != null) {
            tDisplay += " (" + String.format("%.2f", theoryGradePoint) + ")";
        }

        if (!hasPractical) {
            return tDisplay;
        }

        // Handle practical part
        String pDisplay = (practicalGrade == null) ? "Not Graded" : practicalGrade;
        if (practicalGrade != null && practicalGradePoint != null) {
            pDisplay += " (" + String.format("%.2f", practicalGradePoint) + ")";
        }

        return tDisplay + " / " + pDisplay;
    }

    public boolean isGraded() {
        if (theoryGrade == null)
            return false;
        if (hasPractical && practicalGrade == null)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return subjectName + " (" + getCreditHoursDisplay() + ")";
    }
}