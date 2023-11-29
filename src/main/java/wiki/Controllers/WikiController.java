package wiki.Controllers;

import wiki.Entities.DAO.IUserDAO;
import wiki.Entities.DAOImplementations.PageDAO;
import wiki.Entities.DAOImplementations.UserDAO;
import wiki.GUI.Accesso;
import wiki.GUI.Home;
import wiki.GUI.Registrazione;
import wiki.Models.Page;
import wiki.Models.PaginationPage;
import wiki.Models.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

// Cosa da sistemare - OO
//
// + Dobbiamo rimuovere il tabbedPane e allocare le GUI in modo migliore
// + Sistemare le chiamate tra una GUI
// + Creare una sottoclasse di tutti i JComponent che utilizziamo per gestire meglio le dependency injection con il controller
// + Ricontrollare le divisioni dei package

//


public class WikiController {
    // DAOs references
    private final IUserDAO userDAO = new UserDAO();
    private final PageDAO pageDAO = new PageDAO();

    // Attributes
    private User loggedUser = null;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                new Home(new WikiController())
        );
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
    }

    public boolean onTryLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (!userDAO.doesUserExist(username)) {
                JOptionPane.showMessageDialog(null, "Nome utente non esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            boolean loginResult = userDAO.login(username, password);

            if (!loginResult) {
                JOptionPane.showMessageDialog(null, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "Login effettuato", "Successo", JOptionPane.INFORMATION_MESSAGE);
                setLoggedUser(new User(username, password));
            }

            return loginResult;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public void disconnectUser() {
        loggedUser = null;
        JOptionPane.showConfirmDialog(null, "Disconnessione effettuata", "Successo", JOptionPane.DEFAULT_OPTION);
    }

    public boolean isUserLogged() {
        return loggedUser != null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void createPage(String title, ArrayList<String> content) {
        try {
            pageDAO.insertPage(title, content, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Page fetchPage(int id) {
        try {
            return pageDAO.fetchPage(id);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public PaginationPage fetchPages(String q, int page, int limit) {
        try {
            return pageDAO.fetchPages(q,page,limit);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle pagine", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
}
