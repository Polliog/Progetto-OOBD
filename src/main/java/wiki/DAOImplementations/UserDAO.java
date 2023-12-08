package wiki.DAOImplementations;

import database.DatabaseConnection;
import wiki.DAO.IUserDAO;
import wiki.Models.Page;
import wiki.Models.Update;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserDAO implements IUserDAO {
    public void insertUser(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO utente (username, password) VALUES (?, ?)");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
    }

    public boolean doesUserExist(String username) throws SQLException {
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

    public ArrayList<Update> getUserNotifications(String username, int status) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query;
        if (status == 3) {
            query = "SELECT u.*, p.title, p.author as page_author, p.creation FROM `update` u JOIN Page p ON u.page_id = p.id WHERE p.author = ?";
        } else if (status == 4) {
            query = "SELECT u.*, p.title, p.author as page_author, p.creation FROM `update` u JOIN Page p ON u.page_id = p.id WHERE p.author = ? AND u.status IS NULL";
        } else {
            query = "SELECT u.*, p.title, p.author as page_author, p.creation FROM `update` u JOIN Page p ON u.page_id = p.id WHERE p.author = ? AND u.status = ?";
        }

        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, username);
        if (status != 3 && status != 4) {
            pstmt.setInt(2, status);
        }

        ResultSet rs = pstmt.executeQuery();
        ArrayList<Update> updates = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int pageId = rs.getInt("page_id");
            String author = rs.getString("author");
            Integer updateStatus = rs.getObject("status", Integer.class);
            String pageTitle = rs.getString("title");
            String pageAuthor = rs.getString("page_author");
            java.sql.Timestamp creation = rs.getTimestamp("creation");
            Page page = new Page(pageId, pageTitle, pageAuthor, creation);
            Update update = new Update(id, page, author, updateStatus == null ? 4 : updateStatus);
            updates.add(update);
        }

        return updates;
    }
}