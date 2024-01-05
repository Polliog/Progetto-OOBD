package wiki.Controllers;

import wiki.DAO.IUserDAO;
import wiki.DAOImplementations.PageDAO;
import wiki.DAOImplementations.UserDAO;
import wiki.GUI.Home;
import wiki.GUI.LoginPage;
import wiki.Models.Notification;
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


// TODO LIST
//
// & lista pagine
// &
//



public class WikiController {
    // DAOs references
    private final IUserDAO userDAO = new UserDAO();
    private final PageDAO pageDAO = new PageDAO();

    // Attributes
    private User loggedUser = null;

    public static void main(String[] args) {
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

    public boolean fetchNotifications() {
        try {
            this.loggedUser.setNotifications(userDAO.getUserNotifications(this.loggedUser.getUsername(), -1));
            if (this.loggedUser.getNotifications(0).size() > 0) {
                //se ci sono notifiche mostra un dialog con 2 bottoni (visualizza e chiudi)
                String message = "";

                if (this.loggedUser.getNotifications(0).size() == 1) {
                    message = "Hai una nuova notifica";
                }
                else {
                    message = "Hai " + this.loggedUser.getNotifications(0).size() + " nuove notifiche";
                }

                Object[] options = {"Visualizza", "Chiudi"};
                int n = JOptionPane.showOptionDialog(null,
                        message, // the dialog message
                        "Notifiche", // the title of the dialog window
                        JOptionPane.YES_NO_OPTION, // option type
                        JOptionPane.QUESTION_MESSAGE, // message type
                        null, // optional icon, use null to use the default icon
                        options, // options string array, will be made into buttons
                        options[0] // option that should be made into a default button
                );

                return n == JOptionPane.YES_OPTION;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle notifiche", "Errore", JOptionPane.ERROR_MESSAGE);
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
            if (notification.getType() == 0) {
                n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare la notifica? La richiesta verra' rifiutata automaticamente", "Elimina pagina", JOptionPane.YES_NO_OPTION);
            } else {
                n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler eliminare la notifica?", "Elimina notifica", JOptionPane.YES_NO_OPTION);
            }
            if (n == JOptionPane.YES_OPTION) {
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
}