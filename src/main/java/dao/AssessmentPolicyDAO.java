package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.AssessmentPolicy;
import util.DBUtil;

public class AssessmentPolicyDAO {

    // Save an assessment policy
    public void save(AssessmentPolicy policy) {
        String sql = "INSERT INTO AssessmentPolicy(universityId, category, componentName, maxMarks) "
                + "VALUES(?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, policy.getUniversityId());
            ps.setString(2, policy.getCategory());
            ps.setString(3, policy.getComponentName());
            ps.setDouble(4, policy.getMaxMarks());
            ps.executeUpdate();

            System.out.println("DAO: Assessment policy saved - " + policy.getComponentName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save multiple assessment policies at once (for bulk insert)
    public void saveAll(List<AssessmentPolicy> policies) {
        for (AssessmentPolicy policy : policies) {
            save(policy);
        }
    }

    // Get assessment policies by university and category
    public List<AssessmentPolicy> getByUniversityAndCategory(int universityId, String category) {
        List<AssessmentPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM AssessmentPolicy WHERE universityId = ? AND category = ? ORDER BY id";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String componentName = rs.getString("componentName");
                double maxMarks = rs.getDouble("maxMarks");

                policies.add(new AssessmentPolicy(id, universityId, category, componentName, maxMarks));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return policies;
    }

    // Delete all assessment policies for a university and category
    public void deleteByUniversityAndCategory(int universityId, String category) {
        String sql = "DELETE FROM AssessmentPolicy WHERE universityId = ? AND category = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ps.executeUpdate();

            System.out.println("DAO: Assessment policies deleted for category: " + category);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get ALL assessment policies for a university (both Theory and Practical)
    public List<AssessmentPolicy> getByUniversity(int universityId) {
        List<AssessmentPolicy> policies = new ArrayList<>();
        String sql = "SELECT * FROM AssessmentPolicy WHERE universityId = ? ORDER BY category";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String category = rs.getString("category");
                String componentName = rs.getString("componentName");
                double maxMarks = rs.getDouble("maxMarks");

                policies.add(new AssessmentPolicy(id, universityId, category, componentName, maxMarks));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return policies;
    }

    // Calculate total marks for a category (Theory should = 100, Practical should =
    // 50)
    public double getTotalMarksByCategory(int universityId, String category) {
        String sql = "SELECT SUM(maxMarks) as total FROM AssessmentPolicy "
                + "WHERE universityId = ? AND category = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}