package database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/Wiki";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "";

    private static Connection connection = null;

    // private constructor to prevent instantiation
    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("db connecting...");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);

            if (connection != null)
                System.out.println("db connected");
        }
        return connection;
    }
}