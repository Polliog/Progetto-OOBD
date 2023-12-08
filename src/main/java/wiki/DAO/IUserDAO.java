package wiki.DAO;

import wiki.Models.Update;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IUserDAO {
    void insertUser(String username, String password) throws SQLException;
    boolean doesUserExist(String username) throws SQLException;
    boolean login(String username, String password) throws SQLException;
    ArrayList<Update> getUserNotifications(String username, int status) throws SQLException;
}
