package wiki.DAO;

import wiki.Models.Notification;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUserDAO {
    void insertUser(String username, String password) throws SQLException;
    boolean doesUserExist(String username) throws SQLException;
    boolean login(String username, String password) throws SQLException;
    ArrayList<Notification> getUserNotifications(String username, int status) throws SQLException;
}
