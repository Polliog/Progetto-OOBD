package wiki.Entities.DAO;

import java.sql.SQLException;

public interface IUserDAO {
    void insertUser(String username, String password) throws SQLException;
    boolean doesUserExist(String username) throws SQLException;
    boolean login(String username, String password) throws SQLException;
}
