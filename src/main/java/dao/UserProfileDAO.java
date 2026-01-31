package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.UserProfile;
import util.DBUtil;

public class UserProfileDAO {

    public void save(UserProfile user) {
        String sql = "INSERT INTO UserProfile(name, department, universityId) VALUES(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getDepartment());
            ps.setInt(3, user.getUniversityId()); // ← ADD THIS
            ps.executeUpdate();

            System.out.println("DAO: User inserted into DB");

        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.save: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save user profile: " + e.getMessage(), e);
        }
    }

    public boolean exists() {
        String sql = "SELECT COUNT(*) FROM UserProfile";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                var rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.getById: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to retrieve user profile: " + e.getMessage(), e);
        }
        return false;
    }

    public UserProfile getUser() {
        UserProfile user = null;
        String sql = "SELECT * FROM UserProfile LIMIT 1";
        try (Connection conn = DBUtil.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                int universityId = rs.getInt("universityId"); // ← ADD THIS
                user = new UserProfile(id, name, department, universityId); // ← UPDATE THIS
            }

        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.update: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update user profile: " + e.getMessage(), e);
        }
        return user;
    }

    public UserProfile getById(int id) {
        // Since we assume single user for now, this just wraps getUser() or queries by
        // ID.
        // For robustness, let's query by ID even if we only have one user typically.
        UserProfile user = null;
        String sql = "SELECT * FROM UserProfile WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String department = rs.getString("department");
                int universityId = rs.getInt("universityId");
                user = new UserProfile(id, name, department, universityId);
            }

        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.exists: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to check user existence: " + e.getMessage(), e);
        }
        return user;
    }

    public void update(UserProfile user) {
        String sql = "UPDATE UserProfile SET name = ?, department = ?, universityId = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getDepartment());
            ps.setInt(3, user.getUniversityId());
            ps.setInt(4, user.getId());

            ps.executeUpdate();
            System.out.println("DAO: User profile updated");

        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.getCount: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to count users: " + e.getMessage(), e);
        }
    }

    // ← ADD THIS NEW METHOD (for Phase 2)
    public void updateUniversityId(int userId, int universityId) {
        String sql = "UPDATE UserProfile SET universityId = ? WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, universityId);
            ps.setInt(2, userId);
            ps.executeUpdate();

            System.out.println("DAO: University linked to user");

        } catch (SQLException e) {
            System.err.println("Database error in UserProfileDAO.delete: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete user profile: " + e.getMessage(), e);
        }
    }
}