package wiki.DAO;

import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IPageDAO {
    void insertPage(String pageTitle, String pageContent, User user) throws SQLException;
    Page fetchPage(int id) throws SQLException;

    void approveChanges(Update update, User loggedUser) throws SQLException;
}
