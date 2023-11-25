package wiki.Controllers;

import wiki.Entities.DAO.IUserDAO;
import wiki.Entities.DAOImplementations.PageDAO;
import wiki.Entities.DAOImplementations.UserDAO;
import wiki.GUI.Accesso;
import wiki.GUI.Home;
import wiki.GUI.Registrazione;
import wiki.Models.Page;
import wiki.Models.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class WikiController {
    // DAOs references
    private final IUserDAO userDAO = new UserDAO();
    private final PageDAO pageDAO = new PageDAO();

    // Attributes
    private User loggedUser = null;

    // TODO
    // Bisogna gestire meglio le GUI
    private static Home home;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                home = new Home(new WikiController())
        );
    }

    private void setLoggedUser(User user) {
        loggedUser = user;
        home.setLoginStatus(true, user.getUsername());
    }

    public void onTryLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!userDAO.doesUserExist(username)) {
                JOptionPane.showMessageDialog(null, "Nome utente non esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
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
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnectUser() {
        loggedUser = null;
        home.setLoginStatus(false, null);
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
}
