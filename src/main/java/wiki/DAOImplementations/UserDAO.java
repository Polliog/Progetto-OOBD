package wiki.DAOImplementations;

import database.DatabaseConnection;
import wiki.DAO.IUserDAO;
import wiki.Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class UserDAO implements IUserDAO {
    // User related methods
    public void insertUser(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // check if there are no users in the database
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM \"User\"");
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        boolean firstUser = (rs.getInt("count") == 0);

        if (firstUser) {
            pstmt = conn.prepareStatement("INSERT INTO \"User\" (username, password, admin) VALUES (?, ?, true)");
        }
        else {
            pstmt = conn.prepareStatement("INSERT INTO \"User\" (username, password) VALUES (?, ?)");
        }

        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
    }
    public boolean doesUserExist(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM \"User\" WHERE username = ?");
        pstmt.setString(1, username);
        return pstmt.executeQuery().next();
    }
    public User login(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM \"User\" WHERE username = ? AND password = ?");
        pstmt.setString(1, username);
        pstmt.setString(2, password);

        ResultSet rs = pstmt.executeQuery();
        if (!rs.next())
            return null;

        return new User(rs.getString("username"), rs.getBoolean("admin"), rs.getTimestamp("creation_date"));
    }

    // Notification related methods
    public int getUserUnviewedNotificationsCount(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM \"Notification\" WHERE \"user\" = ? AND viewed = false");
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt("count");
    }
    public ArrayList<Notification> fetchUserNotifications(String username, String pageText, Integer notificationType, Boolean notificationViewed) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        ArrayList<Notification> notifications = new ArrayList<>();
        PreparedStatement pstmt;

        if (username == null || pageText == null)
            return null;

        if (notificationType == null && notificationViewed == null) {
            pstmt = conn.prepareStatement("SELECT * FROM \"search_notifications\"(?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, pageText);
        }
        else if (notificationType != null && notificationViewed == null) {
            pstmt = conn.prepareStatement("SELECT * FROM \"search_notifications\"(?, ?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, pageText);
            pstmt.setInt(3, notificationType);
        }
        else if (notificationType == null && notificationViewed != null) {
            pstmt = conn.prepareStatement("SELECT * FROM \"search_notifications\"(?, ?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, pageText);
            pstmt.setBoolean(3, notificationViewed);
        }
        else {
            pstmt = conn.prepareStatement("SELECT * FROM \"search_notifications\"(?, ?, ?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, pageText);
            pstmt.setInt(3, notificationType);
            pstmt.setBoolean(4, notificationViewed);
        }

        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            // Notification
            int nId = rs.getInt("id");
            int nType = rs.getInt("notification_type");
            boolean nViewed = rs.getBoolean("viewed");
            Timestamp nCreationDate = rs.getTimestamp("creation_date");
            int updateId = rs.getInt("update_id");

            // Get the page update
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM \"PageUpdate\" WHERE id = ?");
            pstmt2.setInt(1, updateId);
            ResultSet rs2 = pstmt2.executeQuery();
            rs2.next();

            // PageUpdate
            String updateAuthor = rs2.getString("author");
            int updateStatus = rs2.getInt("status");
            Timestamp updateCreation = rs2.getTimestamp("creation_date");
            int pageId = rs2.getInt("page_id");

            // Get the page
            PreparedStatement pstmt3 = conn.prepareStatement("SELECT * FROM \"Page\" WHERE id = ?");
            pstmt3.setInt(1, pageId);
            ResultSet rs3 = pstmt3.executeQuery();
            rs3.next();

            // Page
            String pageTitle = rs3.getString("title");
            Timestamp creation_date = rs3.getTimestamp("creation_date");
            String pageAuthor = rs3.getString("author");

            PageUpdate pageUpdate = new PageUpdate(updateId, new Page(pageId, pageTitle, pageAuthor, creation_date), updateAuthor, updateStatus, updateCreation, null);

            Notification notification = new Notification(nId, nType, nViewed, pageUpdate, nCreationDate);
            notifications.add(notification);
        }

        Collections.reverse(notifications);
        return notifications;
    }
    public void setNotificationViewed(int notificationId, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        //controlla se la notifica appartiene all'utente

        PreparedStatement pstmt = conn.prepareStatement("UPDATE \"Notification\" SET viewed = ? WHERE id = ? and \"user\" = ?");
        pstmt.setBoolean(1, true);
        pstmt.setInt(2, notificationId);
        pstmt.setString(3, username);
        pstmt.executeUpdate();
    }
    public void deleteNotification(Notification notification, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Controlla se la notifica appartiene all'utente
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM \"Notification\" WHERE id = ? AND \"user\" = ?");
        pstmt.setInt(1, notification.getId());
        pstmt.setString(2, username);

        ResultSet rs = pstmt.executeQuery();
        boolean notificationBelongsToUser = (rs.next() && rs.getInt("count") > 0);

        if (!notificationBelongsToUser)
            throw new SQLException("La notifica non appartiene all'utente");

        // Rifiuta le notifiche di richiesta di modifica
        if (notification.getType() == Notification.TYPE_REQUEST_UPDATE) {
            pstmt = conn.prepareStatement("UPDATE \"PageUpdate\" SET status = 0 WHERE id = ?");
            pstmt.setInt(1, notification.getUpdate().getId());
            pstmt.executeUpdate();

            System.out.println("Rifiutata la richiesta di modifica");
        }

        pstmt = conn.prepareStatement("DELETE FROM \"Notification\" WHERE id = ?");
        pstmt.setInt(1, notification.getId());
        pstmt.executeUpdate();
    }
}