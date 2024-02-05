package wiki.DAOImplementations;

import database.DatabaseConnection;
import wiki.DAO.IUserDAO;
import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.UpdateContentString;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

public class UserDAO implements IUserDAO {
    public void insertUser(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // TODO
        //  INSERT WITH COL ADMIN TRUE IF COUNT(*) User IS 0
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO \"User\" (username, password) VALUES (?, ?)");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
    }

    public boolean doesUserExist(String username) throws SQLException {
        // TODO
        //  REPLACE SELECT WITH COUNT
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM \"User\" WHERE username = ?");
        pstmt.setString(1, username);
        return pstmt.executeQuery().next();
    }

    public boolean login(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM \"User\" WHERE username = ? AND password = ?");
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        return pstmt.executeQuery().next();
    }

    public ArrayList<Notification> getUserNotifications(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        ArrayList<Notification> notifications = new ArrayList<>();

        // TODO
        //  REPLACE WITH SUBQUERY AND RENAME RETURN VALUES
        PreparedStatement pstmt = conn.prepareStatement(
                "SELECT * " +
                "FROM (SELECT * FROM \"Notification\" WHERE \"user\" = ?) AS notif " +
                "JOIN \"PageUpdate\" AS upd ON notif.update_id = upd.id " +
                "JOIN \"Page\" AS page ON upd.page_id = page.id " +
                "ORDER BY upd.creation_date DESC");

        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            // Notification
            int notificationId = rs.getInt("id");
            int notificationStatus = rs.getInt("status");
            int notificationType = rs.getInt("type");
            Timestamp notificationCreation = rs.getTimestamp("creation_date");
            boolean viewed = rs.getBoolean("viewed");
            // Update
            int updateId = rs.getInt("update_id");
            String updateAuthor = rs.getString("author");
            int updateStatus = rs.getObject(11 , Integer.class);
            Timestamp updateCreation = rs.getObject(12 , Timestamp.class);
            // Page
            int pageId = rs.getInt("page_id");
            String pageTitle = rs.getString("title");
            Timestamp creation_date = rs.getObject(16, Timestamp.class);
            String pageAuthor = rs.getObject(17, String.class);
            // PageUpdateText
            PreparedStatement pstmt2 = conn.prepareStatement("SELECT * FROM \"UpdatedText\" WHERE update_id = ?");
            pstmt2.setInt(1, updateId);
            ResultSet rs2 = pstmt2.executeQuery();

            ArrayList<UpdateContentString> contentStrings = new ArrayList<>();
            while (rs2.next())
                contentStrings.add(new UpdateContentString(rs2.getInt("id"), rs2.getString("text"), rs2.getInt("order_num"), rs2.getInt("type")));

            Page page = new Page(pageId, pageTitle, pageAuthor, creation_date);

            Update update = new Update(
                    updateId,
                    page,
                    updateAuthor,
                    updateStatus,
                    updateCreation,
                    null,
                    contentStrings);

            Notification notification = new Notification(notificationId, notificationType, viewed, update, notificationCreation, notificationStatus);
            notifications.add(notification);
        }

        return notifications;
    }

    public void setNotificationViewed(int notificationId, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        //controlla se la notifica appartiene all'utente

        PreparedStatement pstmt = conn.prepareStatement("UPDATE \"Notification\" SET viewed = ? WHERE id = ? AND \"user\" = ?");
        pstmt.setBoolean(1, true);
        pstmt.setInt(2, notificationId);
        pstmt.setString(3, username);
        pstmt.executeUpdate();
    }

    public void deleteNotification(Notification notification, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        // Controlla se la notifica appartiene all'utente
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM \"Notification\" WHERE id = ? AND \"user\" = ?");
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
        }

        pstmt = conn.prepareStatement("DELETE FROM \"Notification\" WHERE id = ?");
        pstmt.setInt(1, notification.getId());
        pstmt.executeUpdate();
    }
}