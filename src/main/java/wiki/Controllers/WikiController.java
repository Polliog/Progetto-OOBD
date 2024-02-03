package wiki.Controllers;


import com.formdev.flatlaf.FlatIntelliJLaf;
import wiki.DAO.IUserDAO;
import wiki.DAOImplementations.PageDAO;
import wiki.DAOImplementations.UserDAO;
import wiki.GUI.LoginPage;
import wiki.Models.*;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

// TODO
//  da correggere tante cose..
//  ricerca di una notifica, paginazione delle notifiche
//  bug fix: caricamento pagina


public class WikiController {
    // DAOs references
    private final IUserDAO userDAO = new UserDAO();
    private final PageDAO pageDAO = new PageDAO();

    // Attributes
    private User loggedUser = null;


    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        SwingUtilities.invokeLater(() ->
                new LoginPage(new WikiController(), null)
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
            e.printStackTrace();
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
                setLoggedUser(new User(username));
            }

            return loginResult;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    public boolean onTryRegister(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (userDAO.doesUserExist(username)) {
                JOptionPane.showMessageDialog(null, "Nome utente già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }

        try {
            userDAO.insertUser(username, password);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        JOptionPane.showMessageDialog(null, "Registrazione effettuata con successo, accedi per continuare", "Successo", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    public void disconnectUser() {
        loggedUser = null;
        JOptionPane.showConfirmDialog(null, "Disconnessione effettuata", "Successo", JOptionPane.DEFAULT_OPTION);
    }

    public boolean fetchNotifications() {
        var notifications = fetchUserNotifications();

        if (notifications == null)
            return false;

        // Reverse notifications
        loggedUser.setNotifications(notifications);

        // Shows 'new notification' dialog if there are any non read notifications
        if (!loggedUser.getNotifications(false).isEmpty()) {
            String message = "Hai (" + loggedUser.getNotifications(false).size() + ") nuove notifiche da leggere";
            Object[] options = {"Visualizza", "Chiudi"};

            int n = JOptionPane.showOptionDialog(null,
                    message,        // the dialog message
                    "Notifiche",    // the title of the dialog window
                    JOptionPane.YES_NO_OPTION,      // option type
                    JOptionPane.QUESTION_MESSAGE,   // message type
                    null,       // optional icon, use null to use the default icon
                    options,    // options string array, will be made into buttons
                    options[0]  // option that should be made into a default button
            );
            return n == JOptionPane.YES_OPTION;
        }
        else
            return false;
    }


    public boolean isUserLogged() {
        return loggedUser != null;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public boolean createPage(String title, String content) {
        if (title.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isUserLogged()) {
            JOptionPane.showMessageDialog(null, "Devi essere loggato per creare una pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            pageDAO.insertPage(title, content, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
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

    //type 0 = titolo, type 1 = autore
    public PaginationPage fetchPages(String search, int page, int limit, int type) {
        try {
            return pageDAO.fetchPages(search, page, limit, type);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle pagine", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void savePageUpdate(Page oldPage, String newText) {
        try {
            pageDAO.savePageUpdate(oldPage, newText, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean setNotificationViewed(int notificationId) {
        try {
            userDAO.setNotificationViewed(notificationId, loggedUser.getUsername());

            // local?
            for (Notification notification : loggedUser.getNotifications(-1)) {
                if (notification.getId() == notificationId) {
                    //notification.setViewed(true);
                    break;
                }
            }
            return true;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public ArrayList<Notification> fetchUserNotifications() {
        try {
            return userDAO.getUserNotifications(loggedUser.getUsername());
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean deleteNotification(Notification notification) {
        try {
            if (notification.getType() == Notification.TYPE_REQUEST_UPDATE) {
                int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare la notifica? La richiesta verra' rifiutata automaticamente", "Elimina pagina", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    userDAO.deleteNotification(notification, this.loggedUser.getUsername());
                    loggedUser.getNotifications(-1).remove(notification);
                    return true;
                }
            }
            else {
                userDAO.deleteNotification(notification, this.loggedUser.getUsername());
                loggedUser.getNotifications(-1).remove(notification);
                return true;
            }

        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean acceptPageUpdate(Update update) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare la modifica?", "Accetta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.approveChanges(update, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica accettata con successo", "Modifica accettata", JOptionPane.INFORMATION_MESSAGE);

                // ?????
                update.setStatus(Update.STATUS_ACCEPTED);
                System.out.println("1: update status: " + update.getStatus());

                return true;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'accettazione della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean rejectPageUpdate(Update update) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler rifiutare la modifica?", "Rifiuta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.refuseChanges(update, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica rifiutata con successo", "Modifica rifiutata", JOptionPane.INFORMATION_MESSAGE);

                update.setStatus(Update.STATUS_REJECTED);

                return true;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il rifiuto della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean acceptAllPageUpdates(ArrayList<Update> updates) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare tutte le modifiche?", "Accetta tutte le modifiche", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                for (Update update : updates) {
                    pageDAO.approveChanges(update, loggedUser);
                    update.setStatus(Update.STATUS_ACCEPTED);
                }
                JOptionPane.showMessageDialog(null, "Modifiche accettate con successo", "Modifiche accettate", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'accettazione della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public ArrayList<Update> fetchPendingPageUpdates(int pageId) {
        try {
            return pageDAO.fetchPendingUpdates(pageId);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public boolean deletePage(Page page) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare la pagina?", "Elimina pagina", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.deletePage(page, loggedUser);
                JOptionPane.showMessageDialog(null, "Pagina eliminata con successo", "Pagina eliminata", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }
}