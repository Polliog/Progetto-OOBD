package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:postgresql://172.17.0.3:5432/Wiki";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "root";
    private static Connection connection = null;

    /**
     * Questo metodo restituisce una connessione al database.
     * Se la connessione è chiusa o non è mai stata aperta, viene aperta una nuova connessione.
     * @return la connessione al database
     * @throws SQLException se si verifica un errore durante la connessione al database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            System.out.println("db connecting...");
            connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
        }
        return connection;
    }
}