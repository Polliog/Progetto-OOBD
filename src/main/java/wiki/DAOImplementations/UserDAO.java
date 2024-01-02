package wiki.DAOImplementations;

import database.DatabaseConnection;
import wiki.DAO.IUserDAO;
import wiki.Models.Page;
import wiki.Models.Update;

import java.sql.*;
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
        if (status == -1) {
            query = "SELECT * FROM notifications WHERE user = ?";
        } else {
            query = "SELECT * FROM notifications WHERE user = ? AND status = ?";
        }

        //in query join update e poi da update join page tramite update.page_id = page.id
        query = "SELECT * FROM (" + query + ") AS notifications JOIN `update` AS upd ON notifications.update_id = upd.id JOIN page ON upd.page_id = page.id";


        PreparedStatement pstmt = conn.prepareStatement(query);

        pstmt.setString(1, username);
        if (status != -1) {
            pstmt.setInt(2, status);
        }

        ResultSet rs = pstmt.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnsNumber = rsmd.getColumnCount();
        ArrayList<Update> updates = new ArrayList<>();
        while (rs.next()) {
            //for (int i = 1; i <= columnsNumber; i++) {
            //    String columnValue = rs.getString(i);
            //    System.out.println(rsmd.getColumnName(i) + ": " + columnValue);
            //}
            //System.out.println("");


            int id = rs.getInt("id");
            int pageId = rs.getInt("page_id");
            //get the second author
            String author = rs.getString("author");
            Integer updateStatus = rs.getObject(8, Integer.class);
            String pageTitle = rs.getString("title");
            String pageAuthor = rs.getNString(12);
            java.sql.Timestamp creation = rs.getTimestamp("creation");
            Page page = new Page(pageId, pageTitle, pageAuthor, creation);
            Update update = new Update(id, page, author, updateStatus);
            updates.add(update);
        }

        return updates;
    }
}