package wiki.DAO;

import wiki.Models.Notification;
import wiki.Models.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUserDAO {
    void insertUser(String username, String password) throws SQLException;
    boolean doesUserExist(String username) throws SQLException;
    User login(String username, String password) throws SQLException;
    int getUserUnviewedNotificationsCount(String username) throws SQLException;
    void deleteNotification(Notification notification, String username) throws SQLException;
    void setNotificationViewed(int notificationId, String username) throws SQLException;
    ArrayList<Notification> fetchUserNotifications(String username, String pageText, Integer notificationType, Boolean notificationViewed) throws SQLException;
}
