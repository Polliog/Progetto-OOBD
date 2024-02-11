package wiki.DAO;

import wiki.Models.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Questa interfaccia definisce i metodi per interagire con le pagine nel database.
 * Include operazioni CRUD (Create, Read, Update, Delete) per le pagine.
 */
public interface IPageDAO {
    /**
     * Inserisce una nuova pagina nel database.
     *
     * @param pageTitle   Il titolo della pagina
     * @param pageContent Il contenuto della pagina
     * @param user        L'utente che ha creato la pagina
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    void insertPage(String pageTitle, String pageContent, User user) throws SQLException;

    /**
     * Restituisce una pagina dal database.
     *
     * @param id L'ID della pagina
     * @return La pagina con l'ID specificato
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    Page fetchPage(int id) throws SQLException;

    /**
     * Approva la modifica di una pagina.
     *
     * @param pageUpdate La modifica da approvare
     * @param loggedUser L'utente che ha effettuato l'accesso
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    void approveChanges(PageUpdate pageUpdate, User loggedUser) throws SQLException;

    /**
     * Restituisce il numero di richieste di modifica per una pagina da parte di un utente per una pagina specifica.
     *
     * @param username Il nome utente dell'utente
     * @param pageId   L'ID della pagina
     * @return Il numero di richieste di modifica per una pagina da parte di un utente per una pagina specifica
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     **/
    int getUpdateRequestCount(String username, int pageId) throws SQLException;

    /**
     * Restituisce il contenuto della pagina dal database.
     *
     * @param pageId L'ID della pagina
     * @return Il contenuto della pagina
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    ArrayList<PageContentString> fetchPageContentStrings(int pageId) throws SQLException;

    /**
     * Restituisce le pagine dal database.
     *
     * @param search La stringa di ricerca
     * @param page   Il numero di pagina
     * @param limit  Il limite di pagine per pagina
     * @param type   Il tipo di pagina
     * @return Le pagine dal database
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    PaginationPage fetchPages(String search, int page, int limit, int type) throws SQLException;

    /** Elimina una pagina dal database.
     *
     * @param page       La pagina da eliminare
     * @param loggedUser L'utente che ha effettuato l'accesso
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    void deletePage(Page page, User loggedUser) throws SQLException;

    /**
     * Restituisce le richieste di modifica per una pagina dal database.
     *
     * @param pageId L'ID della pagina
     * @param status Lo stato della richiesta di modifica
     * @return Le richieste di modifica per una pagina dal database
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    ArrayList<PageUpdate> fetchUpdates(int pageId, int status) throws SQLException;

    /** Salva una modifica di pagina nel database.
     *
     * @param oldPage   La vecchia pagina
     * @param newText   Il nuovo testo
     * @param loggedUser L'utente che ha effettuato l'accesso
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    void savePageUpdate(Page oldPage, String newText, User loggedUser) throws SQLException;

    /**
     * Rifiuta la modifica di una pagina.
     *
     * @param pageUpdate La modifica da rifiutare
     * @param loggedUser L'utente che ha effettuato l'accesso
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    void refuseChanges(PageUpdate pageUpdate, User loggedUser) throws SQLException;

    /**
     * Restituisce il contenuto della pagina di aggiornamento dal database.
     *
     * @param pageUpdateId L'ID dell'aggiornamento della pagina
     * @return Il contenuto della pagina di aggiornamento
     * @throws SQLException Se c'è un errore durante l'interazione con il database
     */
    ArrayList<UpdateContentString> fetchPageUpdateContentStrings(int pageUpdateId) throws SQLException;
}
