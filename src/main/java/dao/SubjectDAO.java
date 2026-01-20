package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Subject;
import util.DBUtil;

public class SubjectDAO {

    // Save a new subject
    public int save(Subject subject) {
        String sql = "INSERT INTO Subject(semesterId, subjectName, hasPractical, theoryCreditHours, practicalCreditHours) "
                + "VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, subject.getSemesterId());
            ps.setString(2, subject.getSubjectName());
            ps.setInt(3, subject.isHasPractical() ? 1 : 0); // Boolean to int
            ps.setInt(4, subject.getTheoryCreditHours());

            // Handle nullable practicalCreditHours
            if (subject.getPracticalCreditHours() != null) {
                ps.setInt(5, subject.getPracticalCreditHours());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

            // Get generated ID
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int generatedId = rs.getInt(1);
                System.out.println("DAO: Subject saved with ID: " + generatedId);
                return generatedId;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Get all subjects for a semester
    public List<Subject> getAllBySemester(int semesterId) {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM Subject WHERE semesterId = ? ORDER BY id";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Subject subject = extractSubjectFromResultSet(rs);
                subjects.add(subject);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    // Get subject by ID
    public Subject getById(int id) {
        String sql = "SELECT * FROM Subject WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractSubjectFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update subject (name and credit hours)
    public void update(Subject subject) {
        String sql = "UPDATE Subject SET subjectName = ?, hasPractical = ?, theoryCreditHours = ?, practicalCreditHours = ? "
                + "WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, subject.getSubjectName());
            ps.setInt(2, subject.isHasPractical() ? 1 : 0);
            ps.setInt(3, subject.getTheoryCreditHours());

            if (subject.getPracticalCreditHours() != null) {
                ps.setInt(4, subject.getPracticalCreditHours());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }

            ps.setInt(5, subject.getId());
            ps.executeUpdate();

            System.out.println("DAO: Subject updated with ID: " + subject.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update grades and points
    public void updateGrades(int subjectId, String theoryGrade, String practicalGrade, Double theoryGradePoint,
            Double practicalGradePoint) {
        String sql = "UPDATE Subject SET theoryGrade = ?, practicalGrade = ?, theoryGradePoint = ?, practicalGradePoint = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, theoryGrade);
            ps.setString(2, practicalGrade);

            if (theoryGradePoint != null)
                ps.setDouble(3, theoryGradePoint);
            else
                ps.setNull(3, java.sql.Types.REAL);

            if (practicalGradePoint != null)
                ps.setDouble(4, practicalGradePoint);
            else
                ps.setNull(4, java.sql.Types.REAL);

            ps.setInt(5, subjectId);
            ps.executeUpdate();

            System.out.println("DAO: Subject grades updated for ID: " + subjectId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete subject
    public void delete(int subjectId) {
        String sql = "DELETE FROM Subject WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.executeUpdate();

            System.out.println("DAO: Subject deleted with ID: " + subjectId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get total credit hours for a semester
    public int getTotalCreditHours(int semesterId) {
        String sql = "SELECT SUM(theoryCreditHours + COALESCE(practicalCreditHours, 0)) as total "
                + "FROM Subject WHERE semesterId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get theory credit hours for a semester
    public int getTheoryCreditHours(int semesterId) {
        String sql = "SELECT SUM(theoryCreditHours) as total FROM Subject WHERE semesterId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get practical credit hours for a semester
    public int getPracticalCreditHours(int semesterId) {
        String sql = "SELECT SUM(COALESCE(practicalCreditHours, 0)) as total FROM Subject WHERE semesterId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Count subjects in a semester
    public int countBySemester(int semesterId) {
        String sql = "SELECT COUNT(*) FROM Subject WHERE semesterId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, semesterId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Helper method to extract Subject from ResultSet
    private Subject extractSubjectFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int semesterId = rs.getInt("semesterId");
        String subjectName = rs.getString("subjectName");
        boolean hasPractical = rs.getInt("hasPractical") == 1;
        int theoryCreditHours = rs.getInt("theoryCreditHours");

        // Handle nullable practicalCreditHours
        Integer practicalCreditHours = null;
        int practical = rs.getInt("practicalCreditHours");
        if (!rs.wasNull()) {
            practicalCreditHours = practical;
        }

        String theoryGrade = rs.getString("theoryGrade");
        String practicalGrade = rs.getString("practicalGrade");

        // Handle nullable grade points (Check needed because older rows might be null)
        Double theoryGradePoint = null;
        try {
            double tgp = rs.getDouble("theoryGradePoint");
            if (!rs.wasNull())
                theoryGradePoint = tgp;
        } catch (SQLException e) {
            // Column might not exist yet if migration failed, but in Phase 5 it should.
            // Ignore to be safe during dev.
        }

        Double practicalGradePoint = null;
        try {
            double pgp = rs.getDouble("practicalGradePoint");
            if (!rs.wasNull())
                practicalGradePoint = pgp;
        } catch (SQLException e) {
            // Ignore
        }

        return new Subject(id, semesterId, subjectName, hasPractical,
                theoryCreditHours, practicalCreditHours,
                theoryGrade, practicalGrade,
                theoryGradePoint, practicalGradePoint);
    }
}