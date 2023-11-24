package wiki.Controllers;

import wiki.Entities.DAO.IUserDAO;
import wiki.Entities.DAOImplementations.PageDAO;
import wiki.Entities.DAOImplementations.UserDAO;
import wiki.GUI.Home;
import wiki.Models.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class WikiController {
    private User loggedUser = null;
    private final IUserDAO utenteDAO = new UserDAO();
    private final PageDAO pageDAO = new PageDAO();

    private static Home home;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
                home = new Home(new WikiController())
        );
    }

    private void setLoggedUser(User utente) {
        loggedUser = utente;
        home.setLoginStatus(true, utente.getUsername());
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
            if (!utenteDAO.doesUserExist(username)) {
                JOptionPane.showMessageDialog(null, "Nome utente non esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean loginResult = utenteDAO.login(username, password);

            if (!loginResult) {
                JOptionPane.showMessageDialog(null, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Login effettuato", "Successo", JOptionPane.INFORMATION_MESSAGE);
                User utente = new User(username, password);
                setLoggedUser(utente);
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void disconnectUser() {
        loggedUser = null;
        home.setLoginStatus(false, null);
        JOptionPane.showConfirmDialog(null, "Disconnessione effetuata", "Successo", JOptionPane.DEFAULT_OPTION);
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
}
