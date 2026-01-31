package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBInitializer {

    public static void initialize() {
        try (Connection conn = DBUtil.getConnection();
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
                    + "category TEXT NOT NULL," // 'Theory' or 'Practical'
                    + "gradeName TEXT NOT NULL," // 'A', 'A-', 'B+', etc.
                    + "gradePoint REAL NOT NULL," // 4.0, 3.67, etc.
                    + "minMarks REAL NOT NULL," // Minimum marks for this grade
                    + "maxMarks REAL NOT NULL," // Maximum marks for this grade
                    + "FOREIGN KEY(universityId) REFERENCES University(id)"
                    + ")";
            stmt.execute(gradingPolicyTable);

            // ==========================================
            // TABLE 4: AssessmentPolicy
            // ==========================================
            String assessmentPolicyTable = "CREATE TABLE IF NOT EXISTS AssessmentPolicy ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "universityId INTEGER NOT NULL,"
                    + "category TEXT NOT NULL," // 'Theory' or 'Practical'
                    + "componentName TEXT NOT NULL," // 'Mid', 'Final', 'Sessional', etc.
                    + "maxMarks REAL NOT NULL," // Maximum marks for this component
                    + "FOREIGN KEY(universityId) REFERENCES University(id)"
                    + ")";
            stmt.execute(assessmentPolicyTable);

            // ==========================================
            // TABLE 5: Semester (PHASE 3)
            // ==========================================
            String semesterTable = "CREATE TABLE IF NOT EXISTS Semester ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "userId INTEGER NOT NULL," // Link to UserProfile
                    + "semesterName TEXT NOT NULL," // "Fall 2024", "1st Semester", etc.
                    + "gpa REAL DEFAULT 0.0," // Computed GPA (auto-calculated)
                    + "FOREIGN KEY(userId) REFERENCES UserProfile(id)"
                    + ")";
            stmt.execute(semesterTable);

            // ==========================================
            // TABLE 6: Subject (PHASE 4)
            // ==========================================
            String subjectTable = "CREATE TABLE IF NOT EXISTS Subject ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "semesterId INTEGER NOT NULL,"
                    + "subjectName TEXT NOT NULL,"
                    + "hasPractical INTEGER NOT NULL," // 0 = false, 1 = true (SQLite boolean)
                    + "theoryCreditHours INTEGER NOT NULL,"
                    + "practicalCreditHours INTEGER," // NULL if no practical
                    + "theoryGrade TEXT," // Will be set in Phase 5-7
                    + "practicalGrade TEXT," // Will be set in Phase 5-7
                    + "theoryGradePoint REAL," // Added Phase 5 Step 3 (Grade Points)
                    + "practicalGradePoint REAL," // Added Phase 5 Step 3
                    + "FOREIGN KEY(semesterId) REFERENCES Semester(id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(subjectTable);

            // Check if new columns exist (Migration for existing databases)
            try {
                stmt.execute("ALTER TABLE Subject ADD COLUMN theoryGradePoint REAL");
                System.out.println("✅ Migrated Subject table: Added theoryGradePoint column");
            } catch (Exception e) {
                // Column likely exists, ignore
            }
            try {
                stmt.execute("ALTER TABLE Subject ADD COLUMN practicalGradePoint REAL");
                System.out.println("✅ Migrated Subject table: Added practicalGradePoint column");
            } catch (Exception e) {
                // Column likely exists, ignore
            }

            // ==========================================
            // TABLE 7: Marks (PHASE 5)
            // ==========================================
            String marksTable = "CREATE TABLE IF NOT EXISTS Marks ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "subjectId INTEGER NOT NULL,"
                    + "category TEXT NOT NULL," // 'Theory' or 'Practical'
                    + "componentName TEXT NOT NULL," // 'Mid', 'Final', 'Sessional', etc.
                    + "obtainedMarks REAL," // Marks student got
                    + "maxMarks REAL NOT NULL," // Maximum possible marks
                    + "FOREIGN KEY(subjectId) REFERENCES Subject(id) ON DELETE CASCADE"
                    + ")";
            stmt.execute(marksTable);

            // ==========================================
            // CREATE INDEXES (PHASE 8 fix - Issue #11)
            // ==========================================
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_semester_userId ON Semester(userId)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_subject_semesterId ON Subject(semesterId)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_marks_subjectId ON Marks(subjectId)");

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