package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/oo";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "";

    private static Connection connection = null;

    private DatabaseConnection() {
        // private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }
        return connection;
    }
}