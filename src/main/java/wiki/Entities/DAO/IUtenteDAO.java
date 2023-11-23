package wiki.Entities.DAO;

import java.sql.SQLException;

public interface IUtenteDAO {
    public void salvaUtente(String username, String password) throws SQLException;
    public boolean esisteUtente(String username) throws SQLException;
}
