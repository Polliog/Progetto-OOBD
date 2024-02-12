package wiki.Controllers;


import com.formdev.flatlaf.FlatIntelliJLaf;
import wiki.DAO.IPageDAO;
import wiki.DAO.IUserDAO;
import wiki.DAOImplementations.PageDAO;
import wiki.DAOImplementations.UserDAO;
import wiki.GUI.LoginPage;
import wiki.Models.*;
import wiki.Models.Utils.ContentStringsUtils;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Questa classe è il controller principale dell'applicazione.
 * Gestisce tutte le operazioni di interazione con il database e la logica di business.
 */
public class WikiController {
    // DAOs references
    private final IUserDAO userDAO = new UserDAO();
    private final IPageDAO pageDAO = new PageDAO();

    // Attributes
    private User loggedUser = null;


    public static void main(String[] args) {
        FlatIntelliJLaf.setup();

        SwingUtilities.invokeLater(() ->
                new LoginPage(new WikiController(), null)
        );
    }


    // User related methods

    /**
     * Questo metodo tenta di effettuare il login con le credenziali fornite.
     * Se il login ha successo, l'utente loggato viene salvato nel controller.
     * @param username Il nome utente dell'utente che vuole effettuare il login
     * @param password La password dell'utente che vuole effettuare il login
     * @return true se il login ha successo, false altrimenti
     */
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

    /**
     * Questo metodo tenta di registrare un nuovo utente con le credenziali fornite.
     * Se la registrazione ha successo, l'utente loggato viene salvato nel controller.
     * @param username Il nome utente dell'utente che vuole registrarsi
     * @param password La password dell'utente che vuole registrarsi
     * @return true se la registrazione ha successo, false altrimenti
     */
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

    /**
     * Questo metodo effettua il logout dell'utente loggato.
     */
    public void disconnectUser() {
        loggedUser = null;
        JOptionPane.showConfirmDialog(null, "Disconnessione effettuata", "Successo", JOptionPane.DEFAULT_OPTION);
    }

    /**
     * Questo metodo controlla se l'utente loggato ha delle notifiche non lette.
     * Se ci sono notifiche non lette, viene mostrato un messaggio di notifica.
     * @return true se l'utente ha delle notifiche non lette, false altrimenti
     */
    public boolean doesUserHaveNewNotifications() {
        if (loggedUser == null)
            return false;

        try {
            int res = userDAO.fetchUserUnviewedNotificationsCount(loggedUser.getUsername());

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

    /**
     * Questo metodo restituisce l'utente loggato.
     * @return l'utente loggato
     */
    public boolean isUserLogged() {
        return loggedUser != null;
    }

    /**
     * Questo metodo restituisce l'utente loggato.
     * @return l'utente loggato
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Questo metodo imposta l'utente loggato.
     * @param user l'utente da impostare come loggato
     */
    private void setLoggedUser(User user) {
        loggedUser = user;
    }

    // Page related methods

    /**
     * Questo metodo crea una nuova pagina con il titolo e il contenuto forniti.
     * @param title Il titolo della pagina
     * @param content Il contenuto della pagina
     * @return true se la creazione della pagina ha successo, false altrimenti
     */
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

    /**
     * Questo metodo restituisce la pagina con l'id fornito.
     * @param id L'id della pagina da restituire
     * @return la pagina con l'id fornito
     */
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


    /**
     * Questo metodo restituisce le pagine che corrispondono ai criteri di ricerca forniti.
     * @param search La stringa di ricerca
     * @param page Il numero di pagina
     * @param limit Il limite di pagine per pagina
     * @param type Il tipo di ricerca (0 = titolo, 1 = autore)
     * @return le pagine che corrispondono ai criteri di ricerca forniti
     */
    public PaginationPage fetchPages(String search, int page, int limit, int type) {
        try {
            return pageDAO.fetchPages(search, page, limit, type);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle pagine", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Questo metodo restituisce il contenuto della pagina con l'id fornito.
     * @param pageId L'id della pagina
     * @return il contenuto della pagina con l'id fornito
     */
    public ArrayList<PageContentString> fetchPageContentStrings(int pageId) {
        try {
            return pageDAO.fetchPageContentStrings(pageId);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Questo metodo restituisce il contenuto della pagina con l'id fornito.
     * @param pageId L'id della pagina
     * @return il contenuto della pagina con l'id fornito
     */
    public String fetchAllPageContent(int pageId) {
        try {
            return ContentStringsUtils.getAllPageContentStrings(pageDAO.fetchPageContentStrings(pageId));
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Questo metodo elimina la pagina fornita.
     * @param page La pagina da eliminare
     * @return true se l'eliminazione ha successo, false altrimenti
     */
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

    /**
     * Questo metodo restituisce le modifiche della pagina con l'id fornito.
     * @param pageId L'id della pagina
     * @param status lo stato della modifica
     * @return le modifiche della pagina con l'id fornito
     */
    public ArrayList<PageUpdate> fetchPageUpdates(int pageId, int status) {
        try {
            return pageDAO.fetchUpdates(pageId, status);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    /**
     * Questo metodo salva le modifiche della pagina fornite.
     * @param oldPage La pagina originale
     * @param newText Il nuovo testo della pagina
     */
    public void savePageUpdate(Page oldPage, String newText) {
        try {
            pageDAO.savePageUpdate(oldPage, newText, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Questo metodo accetta la modifica della pagina fornita.
     * @param pageUpdate La modifica della pagina
     * @return true se l'accettazione ha successo, false altrimenti
     */
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

    /**
     * Questo metodo rifiuta la modifica della pagina fornita.
     * @param pageUpdate La modifica della pagina
     * @return true se il rifiuto ha successo, false altrimenti
     */
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

    /**
     * Questo metodo accetta tutte le modifiche della pagina fornite.
     * @param pageUpdates Le modifiche della pagina
     * @return true se l'accettazione ha successo, false altrimenti
     */
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


    /** Controlla tutte le richieste di modifica in attesa per la pagina con l'id fornito
     * @param pageId l'id della pagina
     * @return il numero di richieste di modifica in attesa
     */
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
    /** Imposta una notifica come visualizzata
     * @param notificationId l'id della notifica
     * @return true se l'operazione ha successo, false altrimenti
     */
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

    /**
     * Questo metodo restituisce tutte le notifiche dell'utente loggato.
     * @param pageTitle il titolo della pagina
     * @param notificationType il tipo di notifica
     * @param notificationViewed lo stato della notifica
     * @return tutte le notifiche dell'utente loggato
     */
    public ArrayList<Notification> fetchAllUserNotifications(String pageTitle, Integer notificationType, Boolean notificationViewed) {
        try {
            return userDAO.fetchUserNotifications(loggedUser.getUsername(), pageTitle, notificationType, notificationViewed);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


    /**
     * Questo metodo restituisce tutte le notifiche dell'utente loggato.
     * @return tutte le notifiche dell'utente loggato
     */
    public ArrayList<Notification> fetchAllUserNotifications() {
        try {
            return userDAO.fetchUserNotifications(loggedUser.getUsername(), "", null, null);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }


    /**
     * Questo metodo elimina la notifica fornita.
     * @param notification La notifica da eliminare
     * @return true se l'eliminazione ha successo, false altrimenti
     */
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