package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Semester;
import util.DBUtil;

public class SemesterDAO {

    // Save a new semester
    public int save(Semester semester) {
        String sql = "INSERT INTO Semester(userId, semesterName, gpa) VALUES(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, semester.getUserId());
            ps.setString(2, semester.getSemesterName());
            ps.setDouble(3, semester.getGpa());
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
        return -1;  // Return -1 if save failed
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
                int id = rs.getInt("id");
                String semesterName = rs.getString("semesterName");
                double gpa = rs.getDouble("gpa");
                
                semesters.add(new Semester(id, userId, semesterName, gpa));
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
                int userId = rs.getInt("userId");
                String semesterName = rs.getString("semesterName");
                double gpa = rs.getDouble("gpa");
                
                return new Semester(id, userId, semesterName, gpa);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update semester GPA (will be used when subjects are added)
    public void updateGPA(int semesterId, double gpa) {
        String sql = "UPDATE Semester SET gpa = ? WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, gpa);
            ps.setInt(2, semesterId);
            ps.executeUpdate();

            System.out.println("DAO: Semester GPA updated to " + gpa);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}