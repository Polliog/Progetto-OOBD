package wiki.Controllers;

import wiki.Entities.DAOImplementations.UtenteDAO;
import wiki.GUI.Home;
import wiki.Models.Utente;

import javax.swing.*;
import java.sql.SQLException;

public class WikiController {
    private static Utente loggedUser = null;
    private static final UtenteDAO utenteDAO = new UtenteDAO();
    private static Home home = null;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                home = new Home();
            }
        });
    }

    private static void setLoggedUser(Utente utente) {
        loggedUser = utente;
        home.setLoginStatus(true, utente.getUsername());
    }

    public static void onTryLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (!utenteDAO.esisteUtente(username)) {
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
                Utente utente = new Utente(username, password);
                setLoggedUser(utente);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }


    public static void disconnectUser() {
        loggedUser = null;
        home.setLoginStatus(false, null);
        JOptionPane.showConfirmDialog(null, "Disconnessione effetuata", "Successo", JOptionPane.DEFAULT_OPTION);
    }
}
