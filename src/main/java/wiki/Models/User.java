package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * La classe User rappresenta un utente nel sistema.
 * Ogni utente ha un nome utente, uno stato di amministratore e una data di creazione.
 */
public class User {
    //attributes
    private final String username;
    private final boolean isAdmin;
    private final Timestamp creationDate;

    /**
     * Costruisce un nuovo oggetto User con i dettagli specificati.
     *
     * @param username Il nome utente dell'utente.
     * @param isAdmin Un booleano che indica se l'utente è un amministratore.
     * @param creationDate La data di creazione dell'utente.
     */
    public User(String username, boolean isAdmin, Timestamp creationDate) {
        this.username = username.toLowerCase();
        this.isAdmin = isAdmin;
        this.creationDate = creationDate;
    }

    /**
     * Restituisce il nome utente di questo utente.
     *
     * @return Il nome utente di questo utente.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Restituisce se questo utente è un amministratore.
     *
     * @return true se questo utente è un amministratore, false altrimenti.
     */
    public boolean isAdmin() {
        return isAdmin;
    }
}