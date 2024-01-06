package wiki.DAOImplementations;

import database.DatabaseConnection;
import wiki.DAO.IUserDAO;
import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.UpdateContentString;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

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

    public ArrayList<Notification> getUserNotifications(String username, int status) throws SQLException {
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
        ArrayList<Notification> updates = new ArrayList<>();
        while (rs.next()) {
            //Ignora serve solo per debuggare
            //for (int i = 1; i <= columnsNumber; i++) {
            //    String columnValue = rs.getString(i);
            //    System.out.println(rsmd.getColumnName(i) + ": " + columnValue);
            //}
            //System.out.println("");


            int id = rs.getInt("id");
            int pageId = rs.getInt("page_id");
            int updateId = rs.getInt("update_id");
            //get the second author
            int notificationType = rs.getInt("type");
            String author = rs.getString("author");
            int notStatus = rs.getInt("status");
            Integer updateStatus = rs.getObject(9, Integer.class);
            String pageTitle = rs.getString("title");
            String pageAuthor = rs.getNString(14);
            java.sql.Timestamp updateCreation = rs.getTimestamp("creation");
            java.sql.Timestamp creation = rs.getTimestamp(13);
            Page page = new Page(pageId, pageTitle, pageAuthor, creation);

            //get update text
            String query2 = "SELECT * FROM updatedText WHERE update_id = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setInt(1, updateId);
            ResultSet rs2 = pstmt2.executeQuery();
            ArrayList<UpdateContentString> contentStrings = new ArrayList<>();

            while (rs2.next()) {
                int contentId = rs2.getInt("id");
                String content = rs2.getString("text");
                int order = rs2.getInt("order_num");
                int type = rs2.getInt("type");
                contentStrings.add(new UpdateContentString(contentId, content, order, type));
            }

            Update update = new Update(updateId, page, author, updateStatus == null ? 2 : updateStatus, contentStrings, creation);
            Notification notification = new Notification(id, notStatus, update, notificationType);
            updates.add(notification);
        }

        return updates;
    }

    public void setNotificationStatus(int notificationId, int status, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        //controlla se la notifica appartiene all'utente
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM notifications WHERE id = ? AND user = ?");
        pstmt.setInt(1, notificationId);
        pstmt.setString(2, username);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            throw new SQLException("La notifica non appartiene all'utente");
        }

        pstmt = conn.prepareStatement("UPDATE notifications SET status = ? WHERE id = ? and user = ?");
        pstmt.setInt(1, status);
        pstmt.setInt(2, notificationId);
        pstmt.setString(3, username);
        pstmt.executeUpdate();
    }

    public void deleteNotification(Notification notification, String username) throws SQLException {
        //also update the status of the update to 0

        Connection conn = DatabaseConnection.getConnection();

        //controlla se la notifica appartiene all'utente
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM notifications WHERE id = ? AND user = ?");
        pstmt.setInt(1, notification.getId());
        pstmt.setString(2, username);

        ResultSet rs = pstmt.executeQuery();
        if (!rs.next()) {
            throw new SQLException("La notifica non appartiene all'utente");
        }

        if (notification.getType() == 0) {
            //check if the update is pending
            pstmt = conn.prepareStatement("SELECT * FROM `update` WHERE id = ? AND status IS NULL");
            pstmt.setInt(1, notification.getUpdate().getId());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pstmt = conn.prepareStatement("UPDATE `update` SET status = 0 WHERE id = ?");
                pstmt.setInt(1, notification.getUpdate().getId());
                pstmt.executeUpdate();
            }
        }

        pstmt = conn.prepareStatement("DELETE FROM notifications WHERE id = ?");
        pstmt.setInt(1, notification.getId());
        pstmt.executeUpdate();
    }
    
    

}