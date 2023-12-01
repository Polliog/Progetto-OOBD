package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;

public final class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/oo";
    private static final String DATABASE_USER = "root";
    private static final String DATABASE_PASSWORD = "";

    private static Connection connection = null;

    // private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("db connecting...");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }
        return connection;
    }
}