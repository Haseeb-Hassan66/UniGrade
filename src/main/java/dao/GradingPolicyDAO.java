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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return policies;
    }

    // Find grade for given marks and category
    public GradingPolicy findGradeForMarks(int universityId, String category, double marks) {
        String sql = "SELECT * FROM GradingPolicy "
                   + "WHERE universityId = ? AND category = ? AND minMarks <= ? AND maxMarks >= ? "
                   + "LIMIT 1";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ps.setDouble(3, marks);
            ps.setDouble(4, marks);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String gradeName = rs.getString("gradeName");
                double gradePoint = rs.getDouble("gradePoint");
                double minMarks = rs.getDouble("minMarks");
                double maxMarks = rs.getDouble("maxMarks");

                return new GradingPolicy(id, universityId, category, gradeName, 
                                        gradePoint, minMarks, maxMarks);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // No grade found (marks out of range)
    }
}