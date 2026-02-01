package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Service responsible for resetting all user data.
 * 
 * Deletion order respects foreign key dependencies:
 * 1. Marks → depends on Subject (CASCADE handles this)
 * 2. Subject → depends on Semester (CASCADE handles this)
 * 3. Semester → depends on UserProfile
 * 4. UserProfile → root user table
 * 
 * Preserved (system/template data):
 * - University
 * - GradingPolicy
 * - AssessmentPolicy
 */
public class ResetDataService {

    /**
     * Deletes all user data from the database.
     * Due to ON DELETE CASCADE on Subject and Marks,
     * deleting Semester automatically removes its Subjects and their Marks.
     * 
     * @return ResetResult summary of what was deleted
     */
    public static ResetResult resetAllData() {
        ResetResult result = new ResetResult();

        try (Connection conn = DBUtil.getConnection()) {

            // Step 1: Count records before deletion (for summary)
            result.setMarksDeleted(countRows(conn, "Marks"));
            result.setSubjectsDeleted(countRows(conn, "Subject"));
            result.setSemestersDeleted(countRows(conn, "Semester"));

            // Step 2: Delete Semester rows (CASCADE deletes Subject + Marks automatically)
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Semester")) {
                ps.executeUpdate();
                System.out.println("ResetDataService: Deleted all semesters (+ subjects + marks via CASCADE)");
            }

            // Step 3: Delete UserProfile
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM UserProfile")) {
                ps.executeUpdate();
                System.out.println("ResetDataService: Deleted user profile");
            }

            result.setSuccess(true);
            System.out.println("ResetDataService: Reset complete - " + result);

        } catch (SQLException e) {
            System.err.println("ResetDataService: Failed to reset data: " + e.getMessage());
            e.printStackTrace();
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    private static int countRows(Connection conn, String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            var rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // ===================================================================
    // Inner class: holds summary of reset results
    // ===================================================================
    public static class ResetResult {
        private boolean success = false;
        private String errorMessage = null;
        private int semestersDeleted = 0;
        private int subjectsDeleted = 0;
        private int marksDeleted = 0;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public int getSemestersDeleted() {
            return semestersDeleted;
        }

        public void setSemestersDeleted(int semestersDeleted) {
            this.semestersDeleted = semestersDeleted;
        }

        public int getSubjectsDeleted() {
            return subjectsDeleted;
        }

        public void setSubjectsDeleted(int subjectsDeleted) {
            this.subjectsDeleted = subjectsDeleted;
        }

        public int getMarksDeleted() {
            return marksDeleted;
        }

        public void setMarksDeleted(int marksDeleted) {
            this.marksDeleted = marksDeleted;
        }

        @Override
        public String toString() {
            return "Semesters: " + semestersDeleted
                    + " | Subjects: " + subjectsDeleted
                    + " | Marks: " + marksDeleted;
        }
    }
}