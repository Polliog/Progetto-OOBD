package wiki.Entities.DAOImplementations;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UtenteDAO implements wiki.Entities.DAO.IUtenteDAO {

    public void salvaUtente(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO utente (username, password) VALUES (?, ?)");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
    }

    public boolean esisteUtente(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM utente WHERE username = ?");
        pstmt.setString(1, username);
        return pstmt.executeQuery().next();
    }

    public boolean login(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM utente WHERE username = ? AND password = ?");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        return pstmt.executeQuery().next();
    }
}
