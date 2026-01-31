package util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // Portable database path using user's home directory
    private static final String DB_DIR = System.getProperty("user.home")
            + File.separator + ".unigrade";
    private static final String DB_PATH = DB_DIR + File.separator + "unigrade.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    static {
        // Ensure database directory exists
        File dbDirectory = new File(DB_DIR);
        if (!dbDirectory.exists()) {
            boolean created = dbDirectory.mkdirs();
            if (created) {
                System.out.println("✅ Created database directory: " + DB_DIR);
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            // ⚠️ Force the driver to load in JDK 9+
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Get a connection with auto-commit disabled for transaction management.
     * Remember to call commitTransaction() or rollbackTransaction() and close the
     * connection.
     */
    public static Connection getTransactionConnection() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        return conn;
    }

    /**
     * Commit the transaction on the given connection.
     */
    public static void commitTransaction(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.commit();
        }
    }

    /**
     * Rollback the transaction on the given connection.
     */
    public static void rollbackTransaction(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.rollback();
                System.out.println("Transaction rolled back");
            }
        } catch (SQLException e) {
            System.err.println("Error during rollback: " + e.getMessage());
            e.printStackTrace();
        }
    }
}