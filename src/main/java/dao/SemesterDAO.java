package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Semester;
import model.Subject;
import util.DBUtil;
import util.ResultCalculator;

public class SemesterDAO {

    // Save a new semester
    public int save(Semester semester) {
        String sql = "INSERT INTO Semester(userId, semesterName, gpa) VALUES(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, semester.getUserId());
            ps.setString(2, semester.getSemesterName());

            // Handle nullable GPA
            if (semester.getGpa() != null) {
                ps.setDouble(3, semester.getGpa());
            } else {
                ps.setNull(3, java.sql.Types.REAL);
            }

            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("DAO: Semester saved with ID: " + generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if save failed
    }

    // Get all semesters for a user
    public List<Semester> getAllByUser(int userId) {
        List<Semester> semesters = new ArrayList<>();
        String sql = "SELECT * FROM Semester WHERE userId = ? ORDER BY id";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Semester semester = extractSemesterFromResultSet(rs);
                semesters.add(semester);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semesters;
    }

    // Get semester by ID
    public Semester getById(int id) {
        String sql = "SELECT * FROM Semester WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractSemesterFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update semester GPA (will be used when subjects are added)
    public void updateGPA(int semesterId, Double gpa) {
        String sql = "UPDATE Semester SET gpa = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (gpa != null) {
                ps.setDouble(1, gpa);
            } else {
                ps.setNull(1, java.sql.Types.REAL);
            }
            ps.setInt(2, semesterId);
            ps.executeUpdate();

            System.out.println("DAO: Semester GPA updated to " + gpa);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ===== PHASE 6: NEW METHODS FOR RESULT CALCULATION =====

    /**
     * Calculate GPA for a semester and save it to database
     * This is the main method to call after entering grades
     */
    public void calculateAndUpdateGPA(int semesterId) {
        // Get all subjects for this semester
        SubjectDAO subjectDAO = new SubjectDAO();
        List<Subject> subjects = subjectDAO.getAllBySemester(semesterId);

        // Calculate GPA using ResultCalculator
        Double gpa = ResultCalculator.calculateGPA(subjects);

        // Update in database
        updateGPA(semesterId, gpa);

        System.out.println("DAO: GPA calculated and updated for semester ID: " + semesterId + " -> " + gpa);
    }

    /**
     * Get CGPA (Cumulative GPA) for a user across all semesters
     * Weighted average of all semester GPAs
     */
    public Double getCGPA(int userId) {
        List<Semester> semesters = getAllByUser(userId);

        List<Double> gpaList = new ArrayList<>();
        List<Integer> creditHoursList = new ArrayList<>();

        SubjectDAO subjectDAO = new SubjectDAO();

        for (Semester semester : semesters) {
            // Only include semesters with calculated GPA
            if (semester.getGpa() != null) {
                int totalCredits = subjectDAO.getTotalCreditHours(semester.getId());
                if (totalCredits > 0) {
                    gpaList.add(semester.getGpa());
                    creditHoursList.add(totalCredits);
                }
            }
        }

        // Calculate CGPA using ResultCalculator
        return ResultCalculator.calculateCGPA(gpaList, creditHoursList);
    }

    /**
     * Get total credit hours completed by a user (across all semesters)
     */
    public int getTotalCreditHoursCompleted(int userId) {
        List<Semester> semesters = getAllByUser(userId);
        SubjectDAO subjectDAO = new SubjectDAO();

        int totalCredits = 0;
        for (Semester semester : semesters) {
            // Only count semesters with GPA (completed semesters)
            if (semester.getGpa() != null) {
                totalCredits += subjectDAO.getTotalCreditHours(semester.getId());
            }
        }

        return totalCredits;
    }

    // Delete semester
    public void delete(int semesterId) {
        String sql = "DELETE FROM Semester WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ps.executeUpdate();

            System.out.println("DAO: Semester deleted with ID: " + semesterId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if semester name exists for a user (prevent duplicates)
    public boolean exists(int userId, String semesterName) {
        String sql = "SELECT COUNT(*) FROM Semester WHERE userId = ? AND semesterName = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, semesterName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Count total semesters for a user
    public int countByUser(int userId) {
        String sql = "SELECT COUNT(*) FROM Semester WHERE userId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to extract Semester from ResultSet
    private Semester extractSemesterFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("userId");
        String semesterName = rs.getString("semesterName");

        // Handle nullable GPA
        Double gpa = null;
        double gpaValue = rs.getDouble("gpa");
        if (!rs.wasNull()) {
            gpa = gpaValue;
        }

        return new Semester(id, userId, semesterName, gpa);
    }
}