package wiki.Entities.DAO;

import wiki.Models.User;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IPageDAO {
    void insertPage(String title, ArrayList<String> content, User utente) throws SQLException;
}
