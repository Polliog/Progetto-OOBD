package wiki.DAO;

import wiki.Models.Notification;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUserDAO {
    void insertUser(String username, String password) throws SQLException;
    boolean doesUserExist(String username) throws SQLException;
    boolean login(String username, String password) throws SQLException;
    ArrayList<Notification> getUserNotifications(String username) throws SQLException;
    void deleteNotification(Notification notification, String username) throws SQLException;
    void setNotificationViewed(int notificationId, String username) throws SQLException;
}
