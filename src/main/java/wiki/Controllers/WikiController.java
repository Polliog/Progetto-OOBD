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
import java.util.Collections;

// TODO
//  colorbox del page creation, deve essere dimensionato correttamente
//  Bottone accedi nel login, deve essere dimensionato correttamente
//  !GRAVE appena modificata la pagina o cancellata la pageview deve immediatamente refresharsi, e anche la ricerca
//  !GRAVE Aggiustare un BUG riguardante "le tue modifiche sono state accettate", il tasto visualizza apre una page view e fa perdere il riferimento al main menu, in questo modo resti bloccato
//  creare una classe di utils per i dialog Si/No
//  (on back?) nella pagina di login e register il primo text field deve essere sempre il primo in focus
//  aggiungere icone nelle notifiche per indicarne lo stato
//  sistemare la gui delle notifiche
//  Inserire i bottoni la tab bold italic ecc anche nella modifica di un testo

/*
// TODO
//  colorbox del page creation, deve essere dimensionato correttamente
//  Bottone accedi nel login, deve essere dimensionato correttamente
//  Titoli colorati (?)
//  FATTO - ID della pagine nella visualizzazione della pagine
//  FATTO - Data e ora nella visualizzazione della pagina
//  FATTO - e ora in tutte le visualizzazioni
//  data e ora nel db
//  !GRAVE Dimensionamento dei form
//     Exception in thread "AWT-EventQueue-0" java.lang.NullPointerException: Cannot invoke "wiki.Models.Page.getUpdates()" because "this.page" is null
//  da risolvere quando la pagina che si apre è buona
//  FATTO - mettere una combobox per cambiare la grandezza del font visualizzato
//  FATTO - mettere una combobox per inserire una grandezza diversa (paragrafo, titolo, sottotitolo)
//  !GRAVE Premere il bottone cerca nel main menu due volte blocca l'applicazione
//  !GRAVE appena modificata la pagina o cancellata la pageview deve immediatamente refresharsi, e anche la ricerca
//  Inseire le ricerche per autore, per data
//  Inserire una visualizzazione solo dei propri lavori
//  !GRAVE Aggiustare un BUG riguardante "le tue modifiche sono state accettate", il tasto visualizza apre una page view e fa perdere il riferimento al main menu, in questo modo resti bloccato
//  creare una classe di utils per i dialog Si/No
//  FATTO - nello storico della pagina i tasti prossimo e precedende non devono essere cliccabili se la pagina ha una sola modifica
//  (on back?) nella pagina di login e register il primo text field deve essere sempre il primo in focus
//  FATTO - migliore messaggio di benvenuto nel menu principale
//  aggiungere un icona all app
//  aggiungere icone nelle notifiche per indicarne lo stato
//  sistemare la gui delle notifiche
//  Inserire i bottoni la tab bold italic ecc anche nella modifica di un testo
 */

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
        if (!isUserLogged())
            return false;

        try {
            ArrayList<Notification> notifications = userDAO.getUserNotifications(loggedUser.getUsername(), -1);

            // Reverse notifications
            Collections.reverse(notifications);

            loggedUser.setNotifications(notifications);

            // Shows 'new notification' dialog if there are any non read notifications
            if (!loggedUser.getNotifications(0).isEmpty()) {
                String message = "Hai (" + loggedUser.getNotifications(0).size() + ") nuove notifiche";
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
        catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
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

    public void compareAndSavePage(Page oldPage, String newText) {
        try {
            pageDAO.compareAndSaveEdit(oldPage, newText, loggedUser);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setNotificationStatus(int notificationId, int status) {
        try {
            userDAO.setNotificationStatus(notificationId, status, this.loggedUser.getUsername());
            for (Notification notification : loggedUser.getNotifications(-1)) {
                if (notification.getId() == notificationId) {
                    notification.setStatus(status);
                    break;
                }
            }
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean deleteNotification(Notification notification) {
        try {

            int n;
            if (notification.getType() == 0 && notification.getUpdate().getStatus() == 2) {
                n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare la notifica? La richiesta verra' rifiutata automaticamente", "Elimina pagina", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    userDAO.deleteNotification(notification, this.loggedUser.getUsername());
                    loggedUser.getNotifications(-1).remove(notification);
                    return true;
                }
            } else {
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

    public boolean acceptUpdate(Update update, boolean force) {
        if (force) {
            try {
                pageDAO.approveChanges(update, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica accettata con successo", "Modifica accettata", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Errore durante l'accettazione della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare la modifica?", "Accetta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.approveChanges(update, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica accettata con successo", "Modifica accettata", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante l'accettazione della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
    }

    public boolean refuseUpdate(Update update) {
        try {
            int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler rifiutare la modifica?", "Rifiuta modifica", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                pageDAO.refuseChanges(update, loggedUser);
                JOptionPane.showMessageDialog(null, "Modifica rifiutata con successo", "Modifica rifiutata", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il rifiuto della modifica", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return false;
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