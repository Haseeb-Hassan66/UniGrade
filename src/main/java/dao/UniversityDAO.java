package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.University;
import util.DBUtil;

public class UniversityDAO {

    // Save a new university and return its generated ID
    public int save(University university) {
        String sql = "INSERT INTO University(name) VALUES(?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, university.getName());
            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("DAO: University saved with ID: " + generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if save failed
    }

    // Get all universities
    public List<University> getAll() {
        List<University> universities = new ArrayList<>();
        String sql = "SELECT * FROM University ORDER BY name";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                universities.add(new University(id, name));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return universities;
    }

    // Get university by ID
    public University getById(int id) {
        String sql = "SELECT * FROM University WHERE id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                return new University(id, name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if university name already exists
    public boolean exists(String name) {
        String sql = "SELECT COUNT(*) FROM University WHERE name = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Count total universities (useful for checking if DB is seeded)
    public int count() {
        String sql = "SELECT COUNT(*) FROM University";
        
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}