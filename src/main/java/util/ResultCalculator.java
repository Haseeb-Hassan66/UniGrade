package util;

import java.util.List;
import model.Subject;

/**
 * Utility class for calculating GPA and CGPA
 * Handles both theory and practical grade points
 */
public class ResultCalculator {

    /**
     * Calculate GPA for a list of subjects in a semester
     * Formula: GPA = Total Quality Points / Total Credit Hours
     * 
     * Quality Points = (Theory Grade Point × Theory Credit Hours) +
     * (Practical Grade Point × Practical Credit Hours)
     */
    public static Double calculateGPA(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return null;
        }

        double totalQualityPoints = 0.0;
        int totalCreditHours = 0;

        for (Subject subject : subjects) {
            // Theory component
            if (subject.getTheoryGradePoint() != null) {
                double theoryQP = subject.getTheoryGradePoint() * subject.getTheoryCreditHours();
                totalQualityPoints += theoryQP;
                totalCreditHours += subject.getTheoryCreditHours();
            }

            // Practical component (if exists)
            if (subject.isHasPractical() &&
                    subject.getPracticalGradePoint() != null &&
                    subject.getPracticalCreditHours() != null) {

                double practicalQP = subject.getPracticalGradePoint() * subject.getPracticalCreditHours();
                totalQualityPoints += practicalQP;
                totalCreditHours += subject.getPracticalCreditHours();
            }
        }

        // Avoid division by zero
        if (totalCreditHours == 0) {
            return null;
        }

        // Calculate and round to 2 decimal places
        double gpa = totalQualityPoints / totalCreditHours;
        return Math.round(gpa * 100.0) / 100.0;
    }

    /**
     * Calculate CGPA across multiple semesters
     * Takes a list of GPA values and their corresponding credit hours
     */
    public static Double calculateCGPA(List<Double> gpaList, List<Integer> creditHoursList) {
        if (gpaList == null || creditHoursList == null ||
                gpaList.isEmpty() || gpaList.size() != creditHoursList.size()) {
            return null;
        }

        double totalWeightedGPA = 0.0;
        int totalCreditHours = 0;

        for (int i = 0; i < gpaList.size(); i++) {
            Double gpa = gpaList.get(i);
            Integer creditHours = creditHoursList.get(i);

            // Skip semesters without GPA
            if (gpa == null || creditHours == null) {
                continue;
            }

            totalWeightedGPA += (gpa * creditHours);
            totalCreditHours += creditHours;
        }

        if (totalCreditHours == 0) {
            return null;
        }

        // Calculate and round to 2 decimal places
        double cgpa = totalWeightedGPA / totalCreditHours;
        return Math.round(cgpa * 100.0) / 100.0;
    }

    /**
     * Convert grade letter to grade point
     * Used for validation and reference
     */
    public static Double gradeToPoint(String grade) {
        if (grade == null)
            return null;

        switch (grade.toUpperCase()) {
            case "A+":
                return 4.0;
            case "A":
                return 3.7;
            case "B+":
                return 3.3;
            case "B":
                return 3.0;
            case "C+":
                return 2.7;
            case "C":
                return 2.3;
            case "D":
                return 2.0;
            case "F":
                return 0.0;
            default:
                return null;
        }
    }

    /**
     * Get grade classification based on GPA/CGPA
     */
    public static String getGradeClass(Double gpa) {
        if (gpa == null)
            return "N/A";

        if (gpa >= 3.7)
            return "First Class (Distinction)";
        if (gpa >= 3.3)
            return "First Class";
        if (gpa >= 3.0)
            return "Second Class (Upper)";
        if (gpa >= 2.7)
            return "Second Class (Lower)";
        if (gpa >= 2.3)
            return "Third Class";
        if (gpa >= 2.0)
            return "Pass";
        return "Fail";
    }

    /**
     * Check if all subjects in a semester have grades
     */
    public static boolean allSubjectsGraded(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) {
            return false;
        }

        for (Subject subject : subjects) {
            // Theory must always have a grade
            if (subject.getTheoryGrade() == null || subject.getTheoryGrade().trim().isEmpty()) {
                return false;
            }

            // If has practical, practical must have a grade too
            if (subject.isHasPractical()) {
                if (subject.getPracticalGrade() == null || subject.getPracticalGrade().trim().isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}