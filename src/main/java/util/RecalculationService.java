package util;

import dao.GradingPolicyDAO;
import dao.MarksDAO;
import dao.SemesterDAO;
import dao.SubjectDAO;
import model.GradingPolicy;
import model.Marks;
import model.Semester;
import model.Subject;

import java.util.List;

/**
 * Service responsible for recalculating all grades and GPAs
 * when a user's grading policy changes (e.g., after editing
 * grading ranges or switching universities).
 *
 * Replicates the exact same calculation logic used in MarksEntryController:
 * 1. Sum obtained/max marks per category
 * 2. Calculate percentage
 * 3. Look up grade via GradingPolicyDAO.findGradeForMarks()
 * 4. Update subject grades
 * 5. Recalculate semester GPA
 */
public class RecalculationService {

    private final SemesterDAO semesterDAO;
    private final SubjectDAO subjectDAO;
    private final MarksDAO marksDAO;
    private final GradingPolicyDAO gradingDAO;

    public RecalculationService() {
        this.semesterDAO = new SemesterDAO();
        this.subjectDAO = new SubjectDAO();
        this.marksDAO = new MarksDAO();
        this.gradingDAO = new GradingPolicyDAO();
    }

    /**
     * Main entry point: recalculates all grades and GPAs
     * for every semester and subject belonging to the given user,
     * using the current university's grading policy.
     *
     * @param userId       The user whose data should be recalculated
     * @param universityId The university whose grading policy to use
     * @return RecalculationResult containing summary of what changed
     */
    public RecalculationResult recalculateAll(int userId, int universityId) {
        RecalculationResult result = new RecalculationResult();

        List<Semester> semesters = semesterDAO.getAllByUser(userId);

        for (Semester semester : semesters) {
            List<Subject> subjects = subjectDAO.getAllBySemester(semester.getId());

            for (Subject subject : subjects) {
                boolean changed = recalculateSubject(subject, universityId);
                if (changed) {
                    result.incrementChangedSubjects();
                }
                result.incrementTotalSubjects();
            }

            // Recalculate semester GPA after all subjects are updated
            semesterDAO.calculateAndUpdateGPA(semester.getId());
            result.incrementProcessedSemesters();
        }

        System.out.println("Recalculation complete: " + result);
        return result;
    }

    /**
     * Recalculates grades for a single subject using the given university's policy.
     * Mirrors the exact logic from MarksEntryController.calculateTotals()
     *
     * @return true if the grade actually changed, false otherwise
     */
    private boolean recalculateSubject(Subject subject, int universityId) {
        // --- Recalculate Theory ---
        String newTheoryGrade = null;
        Double newTheoryGradePoint = null;

        List<Marks> theoryMarks = marksDAO.getMarksBySubjectAndCategory(subject.getId(), "Theory");
        if (!theoryMarks.isEmpty()) {
            double[] theoryTotals = sumMarks(theoryMarks);
            double theoryObtained = theoryTotals[0];
            double theoryMax = theoryTotals[1];

            if (theoryMax > 0) {
                double theoryPercentage = (theoryObtained / theoryMax) * 100;
                GradingPolicy theoryPolicy = gradingDAO.findGradeForMarks(universityId, "Theory", theoryPercentage);

                newTheoryGrade = (theoryPolicy != null) ? theoryPolicy.getGradeName() : "F";
                newTheoryGradePoint = (theoryPolicy != null) ? theoryPolicy.getGradePoint() : 0.0;
            }
        }

        // --- Recalculate Practical (if applicable) ---
        String newPracticalGrade = null;
        Double newPracticalGradePoint = null;

        if (subject.isHasPractical()) {
            List<Marks> practicalMarks = marksDAO.getMarksBySubjectAndCategory(subject.getId(), "Practical");
            if (!practicalMarks.isEmpty()) {
                double[] practicalTotals = sumMarks(practicalMarks);
                double practicalObtained = practicalTotals[0];
                double practicalMax = practicalTotals[1];

                if (practicalMax > 0) {
                    double practicalPercentage = (practicalObtained / practicalMax) * 100;
                    GradingPolicy practicalPolicy = gradingDAO.findGradeForMarks(universityId, "Practical",
                            practicalPercentage);

                    newPracticalGrade = (practicalPolicy != null) ? practicalPolicy.getGradeName() : "F";
                    newPracticalGradePoint = (practicalPolicy != null) ? practicalPolicy.getGradePoint() : 0.0;
                }
            }
        }

        // --- Check if anything changed ---
        boolean changed = !nullSafeEquals(subject.getTheoryGrade(), newTheoryGrade)
                || !nullSafeEquals(subject.getTheoryGradePoint(), newTheoryGradePoint)
                || !nullSafeEquals(subject.getPracticalGrade(), newPracticalGrade)
                || !nullSafeEquals(subject.getPracticalGradePoint(), newPracticalGradePoint);

        // --- Persist only if something changed ---
        if (changed) {
            subjectDAO.updateGrades(subject.getId(), newTheoryGrade, newPracticalGrade,
                    newTheoryGradePoint, newPracticalGradePoint);

            System.out.println("Recalculated: " + subject.getSubjectName()
                    + " | Theory: " + newTheoryGrade + " | Practical: " + newPracticalGrade);
        }

        return changed;
    }

    /**
     * Sums obtained marks and max marks from a list of Marks.
     * Only includes components where obtainedMarks is not null
     * (mirrors MarksEntryController behavior: empty fields are skipped).
     *
     * @return double[2] where [0] = totalObtained, [1] = totalMax
     */
    private double[] sumMarks(List<Marks> marksList) {
        double totalObtained = 0;
        double totalMax = 0;

        for (Marks m : marksList) {
            if (m.getObtainedMarks() != null) {
                totalObtained += m.getObtainedMarks();
                totalMax += m.getMaxMarks();
            }
        }

        return new double[] { totalObtained, totalMax };
    }

    /**
     * Null-safe equality check for Objects (String, Double, etc.)
     */
    private boolean nullSafeEquals(Object a, Object b) {
        if (a == null && b == null)
            return true;
        if (a == null || b == null)
            return false;
        return a.equals(b);
    }

    // ===================================================================
    // Inner class: holds summary of recalculation results
    // ===================================================================
    public static class RecalculationResult {
        private int totalSubjects = 0;
        private int changedSubjects = 0;
        private int processedSemesters = 0;

        public void incrementTotalSubjects() {
            totalSubjects++;
        }

        public void incrementChangedSubjects() {
            changedSubjects++;
        }

        public void incrementProcessedSemesters() {
            processedSemesters++;
        }

        public int getTotalSubjects() {
            return totalSubjects;
        }

        public int getChangedSubjects() {
            return changedSubjects;
        }

        public int getProcessedSemesters() {
            return processedSemesters;
        }

        @Override
        public String toString() {
            return "Semesters: " + processedSemesters
                    + " | Total Subjects: " + totalSubjects
                    + " | Changed: " + changedSubjects;
        }
    }
}