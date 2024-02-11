package wiki.Controllers;


import com.formdev.flatlaf.FlatIntelliJLaf;
import wiki.DAO.IUserDAO;
import wiki.DAOImplementations.PageDAO;
import wiki.DAOImplementations.UserDAO;
import wiki.GUI.LoginPage;
import wiki.Models.*;
import wiki.Models.Utils.ContentStringsUtils;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

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


    // User related methods
    public boolean onTryLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (username.contains(" ") || password.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        username = username.toLowerCase();

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
            User userResult = userDAO.login(username, password);

            if (userResult == null) {
                JOptionPane.showMessageDialog(null, "Password errata", "Errore", JOptionPane.ERROR_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(null, "Login effettuato", "Successo", JOptionPane.INFORMATION_MESSAGE);
                setLoggedUser(userResult);
                return true;
            }
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
    public boolean doesUserHaveNewNotifications() {
        if (loggedUser == null)
            return false;

        try {
            int res = userDAO.getUserUnviewedNotificationsCount(loggedUser.getUsername());

            if (res != 0) {
                String message = "Hai (" + res + ") nuove notifiche da leggere";
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
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }
    public boolean isUserLogged() {
        return loggedUser != null;
    }
    public User getLoggedUser() {
        return loggedUser;
    }
    private void setLoggedUser(User user) {
        loggedUser = user;
    }

    // Page related methods
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
    public ArrayList<PageContentString> fetchPageContentStrings(int pageId) {
        try {
            return pageDAO.fetchPageContentStrings(pageId);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public String fetchAllPageContent(int pageId) {
        try {
            return ContentStringsUtils.getAllPageContentStrings(pageDAO.fetchPageContentStrings(pageId));
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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


    // Page Update related methods
    public ArrayList<PageUpdate> fetchPageUpdates(int pageId, int status) {
        try {
            return pageDAO.fetchUpdates(pageId, status);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    public void savePageUpdate(Page oldPage, String newText) {
        try {
            pageDAO.savePageUpdate(oldPage, newText, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }
    public boolean acceptPageUpdate(PageUpdate pageUpdate) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare la modifica?", "Accetta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.approveChanges(pageUpdate, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica accettata con successo", "Modifica accettata", JOptionPane.INFORMATION_MESSAGE);

                // ?????
                //pageUpdate.setStatus(PageUpdate.STATUS_ACCEPTED);
                //System.out.println("1: pageUpdate status: " + pageUpdate.getStatus());

                return true;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'accettazione della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    public boolean rejectPageUpdate(PageUpdate pageUpdate) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler rifiutare la modifica?", "Rifiuta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.refuseChanges(pageUpdate, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica rifiutata con successo", "Modifica rifiutata", JOptionPane.INFORMATION_MESSAGE);

                // pageUpdate.setStatus(PageUpdate.STATUS_REJECTED);

                return true;
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il rifiuto della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    public boolean acceptAllPageUpdates(ArrayList<PageUpdate> pageUpdates) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare tutte le modifiche?", "Accetta tutte le modifiche", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                for (PageUpdate pageUpdate : pageUpdates) {
                    pageDAO.approveChanges(pageUpdate, loggedUser);
                    //pageUpdate.setStatus(PageUpdate.STATUS_ACCEPTED);
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
    public ArrayList<UpdateContentString> fetchPageUpdateContentStrings(int pageUpdateId) {
        try {
            return pageDAO.fetchPageUpdateContentStrings(pageUpdateId);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public String fetchAllUpdatePageContent(int pageUpdateId) {
        try {
            return ContentStringsUtils.getAllUpdateContentStrings(pageDAO.fetchPageUpdateContentStrings(pageUpdateId));
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    public int fetchUpdateRequestCount(int pageId) {
        try {
            return pageDAO.getUpdateRequestCount(loggedUser.getUsername(), pageId);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle richieste di modifica", "Errore", JOptionPane.ERROR_MESSAGE);
            return 0;
        }
    }

    // Notification related methods
    public boolean setNotificationViewed(int notificationId) {
        try {
            userDAO.setNotificationViewed(notificationId, loggedUser.getUsername());
            return true;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    public ArrayList<Notification> fetchAllUserNotifications(String pageTitle, Integer notificationType, Boolean notificationViewed) {
        try {
            return userDAO.fetchUserNotifications(loggedUser.getUsername(), pageTitle, notificationType, notificationViewed);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    public ArrayList<Notification> fetchAllUserNotifications() {
        try {
            return userDAO.fetchUserNotifications(loggedUser.getUsername(), "", null, null);
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
                    //loggedUser.getNotifications(-1).remove(notification);
                    return true;
                }
            }
            else {
                userDAO.deleteNotification(notification, this.loggedUser.getUsername());
                //loggedUser.getNotifications(-1).remove(notification);
                return true;
            }

        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }



}