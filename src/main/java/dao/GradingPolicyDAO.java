package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.GradingPolicy;
import util.DBUtil;

public class GradingPolicyDAO {

    // Save a grading policy
    public void save(GradingPolicy policy) {
        String sql = "INSERT INTO GradingPolicy(universityId, category, gradeName, gradePoint, minMarks, maxMarks) "
                + "VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, policy.getUniversityId());
            ps.setString(2, policy.getCategory());
            ps.setString(3, policy.getGradeName());
            ps.setDouble(4, policy.getGradePoint());
            ps.setDouble(5, policy.getMinMarks());
            ps.setDouble(6, policy.getMaxMarks());
            ps.executeUpdate();

            System.out.println("DAO: Grading policy saved - " + policy.getGradeName());

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.save: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save grading policy: " + e.getMessage(), e);
        }
    }

    // Save multiple grading policies at once (for bulk insert)
    public void saveAll(List<GradingPolicy> policies) {
        for (GradingPolicy policy : policies) {
            save(policy);
        }
    }

    // Get all grading policies for a university by category
    public List<GradingPolicy> getByUniversityAndCategory(int universityId, String category) {
        List<GradingPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM GradingPolicy WHERE universityId = ? AND category = ? ORDER BY maxMarks DESC";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String gradeName = rs.getString("gradeName");
                double gradePoint = rs.getDouble("gradePoint");
                double minMarks = rs.getDouble("minMarks");
                double maxMarks = rs.getDouble("maxMarks");

                policies.add(new GradingPolicy(id, universityId, category, gradeName,
                        gradePoint, minMarks, maxMarks));
            }

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.getAllByUniversity: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve grading policies: " + e.getMessage(), e);
        }
        return policies;
    }

    // Get ALL grading policies for a university (both Theory and Practical)
    public List<GradingPolicy> getByUniversity(int universityId) {
        List<GradingPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM GradingPolicy WHERE universityId = ? ORDER BY category, maxMarks DESC";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category");
                String gradeName = rs.getString("gradeName");
                double gradePoint = rs.getDouble("gradePoint");
                double minMarks = rs.getDouble("minMarks");
                double maxMarks = rs.getDouble("maxMarks");

                policies.add(new GradingPolicy(id, universityId, category, gradeName,
                        gradePoint, minMarks, maxMarks));
            }

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.getGradePoint: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to get grade point: " + e.getMessage(), e);
        }
        return policies;
    }

    /**
     * Find grade for given marks and category.
     * 
     * Strategy:
     * 1. First try to find a grade where marks falls within [minMarks, maxMarks]
     * 2. If no exact match (gap in policy), find the highest grade whose minMarks
     * <= marks
     * 
     * This handles gaps in grading policies gracefully.
     */
    public GradingPolicy findGradeForMarks(int universityId, String category, double marks) {
        // Strategy 1: Try exact range match first
        String exactMatchSql = "SELECT * FROM GradingPolicy "
                + "WHERE universityId = ? AND category = ? AND minMarks <= ? AND maxMarks >= ? "
                + "ORDER BY minMarks DESC "
                + "LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(exactMatchSql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ps.setDouble(3, marks);
            ps.setDouble(4, marks);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("DEBUG: Exact match found for " + marks + "%");
                return extractGradingPolicy(rs, universityId, category);
            }

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.findGradeForMarks: " + e.getMessage());
            e.printStackTrace();
        }

        // Strategy 2: No exact match - find closest grade below
        String closestMatchSql = "SELECT * FROM GradingPolicy "
                + "WHERE universityId = ? AND category = ? AND minMarks <= ? "
                + "ORDER BY minMarks DESC "
                + "LIMIT 1";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(closestMatchSql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ps.setDouble(3, marks);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                GradingPolicy policy = extractGradingPolicy(rs, universityId, category);
                System.out.println(
                        "DEBUG: Gap detected! Using closest grade below " + marks + "% -> " + policy.getGradeName());
                return policy;
            }

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.findGradeForMarks (fallback): " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("DEBUG: No grade found for " + marks + "% (not even a fallback)");
        return null; // Truly no grade available (marks might be negative or policies completely
                     // missing)
    }

    // Helper method to extract GradingPolicy from ResultSet (DRY principle)
    private GradingPolicy extractGradingPolicy(ResultSet rs, int universityId, String category) throws SQLException {
        int id = rs.getInt("id");
        String gradeName = rs.getString("gradeName");
        double gradePoint = rs.getDouble("gradePoint");
        double minMarks = rs.getDouble("minMarks");
        double maxMarks = rs.getDouble("maxMarks");

        return new GradingPolicy(id, universityId, category, gradeName, gradePoint, minMarks, maxMarks);
    }

    // Delete all grading policies for a university and category
    public void deleteByUniversityAndCategory(int universityId, String category) {
        String sql = "DELETE FROM GradingPolicy WHERE universityId = ? AND category = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ps.executeUpdate();

            System.out.println("DAO: Grading policies deleted for category: " + category);

        } catch (SQLException e) {
            System.err.println("Database error in GradingPolicyDAO.count: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to count grading policies: " + e.getMessage(), e);
        }
    }
}