package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Marks;
import util.DBUtil;

public class MarksDAO {

    /**
     * Save or Update marks.
     * Checks if a mark entry exists for the given subject, category, and component.
     * If yes, updates it. If no, inserts a new record.
     */
    public void saveOrUpdate(Marks marks) {
        // First, check if the record exists
        int existingId = getMarksId(marks.getSubjectId(), marks.getCategory(), marks.getComponentName());

        if (existingId != -1) {
            // Update existing
            update(existingId, marks.getObtainedMarks(), marks.getMaxMarks());
        } else {
            // Insert new
            insert(marks);
        }
    }

    private int getMarksId(int subjectId, String category, String componentName) {
        String sql = "SELECT id FROM Marks WHERE subjectId = ? AND category = ? AND componentName = ?";
        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setString(2, category);
            ps.setString(3, componentName);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void insert(Marks marks) {
        String sql = "INSERT INTO Marks(subjectId, category, componentName, obtainedMarks, maxMarks) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, marks.getSubjectId());
            ps.setString(2, marks.getCategory());
            ps.setString(3, marks.getComponentName());

            if (marks.getObtainedMarks() != null) {
                ps.setDouble(4, marks.getObtainedMarks());
            } else {
                ps.setObject(4, null);
            }

            ps.setDouble(5, marks.getMaxMarks());
            ps.executeUpdate();

            System.out.println("DAO: Marks inserted for " + marks.getComponentName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(int id, Double obtainedMarks, double maxMarks) {
        String sql = "UPDATE Marks SET obtainedMarks = ?, maxMarks = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            if (obtainedMarks != null) {
                ps.setDouble(1, obtainedMarks);
            } else {
                ps.setObject(1, null);
            }

            ps.setDouble(2, maxMarks);
            ps.setInt(3, id);

            ps.executeUpdate();
            System.out.println("DAO: Marks updated for ID " + id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve all marks for a specific subject.
     */
    public List<Marks> getMarksBySubject(int subjectId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT * FROM Marks WHERE subjectId = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToMarks(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Retrieve marks for a subject filtered by category (Theory/Practical).
     */
    public List<Marks> getMarksBySubjectAndCategory(int subjectId, String category) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT * FROM Marks WHERE subjectId = ? AND category = ?";

        try (Connection conn = DBUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, subjectId);
            ps.setString(2, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToMarks(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Marks mapResultSetToMarks(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int subjectId = rs.getInt("subjectId");
        String category = rs.getString("category");
        String componentName = rs.getString("componentName");

        Double obtainedMarks = (Double) rs.getObject("obtainedMarks"); // Handle null safely
        double maxMarks = rs.getDouble("maxMarks");

        return new Marks(id, subjectId, category, componentName, obtainedMarks, maxMarks);
    }
}
