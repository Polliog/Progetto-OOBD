package wiki.Utente;

import database.Database;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Utente implements IUtenteDAO {
    //unsername e password
    private String username;
    private String password;

    //costruttore

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //metodi

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void salvaUtente(String username, String password) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO utente (username, password) VALUES (?, ?)");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
    }

    public static boolean esisteUtente(String username) throws SQLException {
        Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM utente WHERE username = ?");
        pstmt.setString(1, username);
        return pstmt.executeQuery().next();
    }
}
