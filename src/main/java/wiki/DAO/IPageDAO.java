package wiki.DAO;

import wiki.Models.Page;
import wiki.Models.PageUpdate;
import wiki.Models.User;

import java.sql.SQLException;

public interface IPageDAO {
    void insertPage(String pageTitle, String pageContent, User user) throws SQLException;
    Page fetchPage(int id) throws SQLException;
    void approveChanges(PageUpdate pageUpdate, User loggedUser) throws SQLException;
    int getUpdateRequestCount(String username, int pageId) throws SQLException;
}
