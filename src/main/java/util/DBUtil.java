package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String DB_URL = "jdbc:sqlite:unigrade.db";

    public static Connection getConnection() throws SQLException {
        try {
            // ⚠️ Force the driver to load in JDK 9+
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(DB_URL);
    }
}