package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInitializer {

    private static final String DB_URL = "jdbc:sqlite:unigrade.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // ==========================================
            // TABLE 1: UserProfile
            // ==========================================
            String userProfileTable = "CREATE TABLE IF NOT EXISTS UserProfile ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       + "name TEXT NOT NULL,"
                       + "department TEXT NOT NULL,"
                       + "universityId INTEGER DEFAULT 0"
                       + ")";
            stmt.execute(userProfileTable);

            // ==========================================
            // TABLE 2: University
            // ==========================================
            String universityTable = "CREATE TABLE IF NOT EXISTS University ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       + "name TEXT NOT NULL UNIQUE"
                       + ")";
            stmt.execute(universityTable);

            // ==========================================
            // TABLE 3: GradingPolicy
            // ==========================================
            String gradingPolicyTable = "CREATE TABLE IF NOT EXISTS GradingPolicy ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       + "universityId INTEGER NOT NULL,"
                       + "category TEXT NOT NULL,"          // 'Theory' or 'Practical'
                       + "gradeName TEXT NOT NULL,"         // 'A', 'A-', 'B+', etc.
                       + "gradePoint REAL NOT NULL,"        // 4.0, 3.67, etc.
                       + "minMarks REAL NOT NULL,"          // Minimum marks for this grade
                       + "maxMarks REAL NOT NULL,"          // Maximum marks for this grade
                       + "FOREIGN KEY(universityId) REFERENCES University(id)"
                       + ")";
            stmt.execute(gradingPolicyTable);

            // ==========================================
            // TABLE 4: AssessmentPolicy
            // ==========================================
            String assessmentPolicyTable = "CREATE TABLE IF NOT EXISTS AssessmentPolicy ("
                       + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                       + "universityId INTEGER NOT NULL,"
                       + "category TEXT NOT NULL,"          // 'Theory' or 'Practical'
                       + "componentName TEXT NOT NULL,"     // 'Mid', 'Final', 'Sessional', etc.
                       + "maxMarks REAL NOT NULL,"          // Maximum marks for this component
                       + "FOREIGN KEY(universityId) REFERENCES University(id)"
                       + ")";
            stmt.execute(assessmentPolicyTable);

            System.out.println("✅ Database initialized with all tables.");

            // ==========================================
            // SEED UNIVERSITIES
            // ==========================================
            UniversitySeeder.seedUniversities();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}