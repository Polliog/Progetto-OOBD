package wiki.DAO;

import wiki.Models.Notification;
import wiki.Models.User;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Questa interfaccia definisce i metodi che devono essere implementati da una classe che si occupa di interagire con il database per quanto riguarda gli utenti.
 */
public interface IUserDAO {

    /**
     * Inserisce un nuovo utente nel database.
     * @param username Username dell'utente.
     * @param password Password dell'utente.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    void insertUser(String username, String password) throws SQLException;

    /**
     * Controlla se un utente esiste nel database.
     * @param username Username dell'utente.
     * @return True se l'utente esiste, false altrimenti.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    boolean doesUserExist(String username) throws SQLException;

    /**
     * Effettua il login di un utente.
     * @param username Username dell'utente.
     * @param password Password dell'utente.
     * @return Oggetto User se il login è avvenuto con successo, null altrimenti.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    User login(String username, String password) throws SQLException;

    /**
     * Restituisce il numero di notifiche non lette di un utente.
     * @param username Username dell'utente.
     * @return Numero di notifiche non lette.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    int fetchUserUnviewedNotificationsCount(String username) throws SQLException;

    /**
     * ELimina una notifica.
     * @param notification Notifica da eliminare.
     * @param username Username dell'utente.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    void deleteNotification(Notification notification, String username) throws SQLException;

    /**
     * Imposta una notifica come letta.
     * @param notificationId Id della notifica.
     * @param username Username dell'utente.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    void setNotificationViewed(int notificationId, String username) throws SQLException;

    /**
     * Restituisce le notifiche di un utente.
     * @param username Username dell'utente.
     * @param pageText Testo della pagina.
     * @param notificationType Tipo di notifica.
     * @param notificationViewed True se la notifica è stata letta, false altrimenti.
     * @return Lista di notifiche.
     * @throws SQLException Eccezione lanciata in caso di errore di connessione al database.
     */
    ArrayList<Notification> fetchUserNotifications(String username, String pageText, Integer notificationType, Boolean notificationViewed) throws SQLException;
}
