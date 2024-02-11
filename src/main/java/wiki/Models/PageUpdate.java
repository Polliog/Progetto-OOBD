package wiki.Models;

import java.sql.Timestamp;

/**
 * La classe PageUpdate rappresenta un aggiornamento di una pagina.
 * Ogni aggiornamento di una pagina ha un id, una pagina, un nome autore, uno stato, un vecchio testo e una data di creazione.
 */
public class PageUpdate {
    public static final int STATUS_REJECTED = 0; // Stato per indicare che l'aggiornamento è stato rifiutato
    public static final int STATUS_ACCEPTED = 1; // Stato per indicare che l'aggiornamento è stato accettato
    public static final int STATUS_PENDING = -1; // Stato per indicare che l'aggiornamento è in attesa

    private final int id; // L'ID dell'aggiornamento della pagina
    private final Page page; // La pagina dell'aggiornamento
    private final String authorName; // Il nome dell'autore dell'aggiornamento
    private final int status; // Lo stato dell'aggiornamento
    private final String oldText; // Il vecchio testo dell'aggiornamento
    private final Timestamp creationDate; // La data di creazione dell'aggiornamento

    /**
     * Costruisce un nuovo aggiornamento di pagina con i dettagli specificati.
     *
     * @param id L'ID dell'aggiornamento della pagina.
     * @param page La pagina dell'aggiornamento.
     * @param authorName Il nome dell'autore dell'aggiornamento.
     * @param status Lo stato dell'aggiornamento.
     * @param creationDate La data di creazione dell'aggiornamento.
     * @param oldText Il vecchio testo dell'aggiornamento.
     */
    public PageUpdate(int id, Page page, String authorName, int status, Timestamp creationDate, String oldText) {
        this.id = id;
        this.page = page;
        this.authorName = authorName;
        this.status = status;
        this.creationDate = creationDate;
        this.oldText = oldText;
    }

    /**
     * Restituisce l'ID di questo aggiornamento di pagina.
     *
     * @return L'ID di questo aggiornamento di pagina.
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce la pagina di questo aggiornamento.
     *
     * @return La pagina di questo aggiornamento.
     */
    public Page getPage() {
        return page;
    }

    /**
     * Restituisce il nome dell'autore di questo aggiornamento.
     *
     * @return Il nome dell'autore di questo aggiornamento.
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Restituisce lo stato di questo aggiornamento.
     *
     * @return Lo stato di questo aggiornamento.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Restituisce il vecchio testo di questo aggiornamento.
     *
     * @return Il vecchio testo di questo aggiornamento.
     */
    public String getOldText() {
        return oldText;
    }

    /**
     * Restituisce il vecchio testo di questo aggiornamento formattato.
     *
     * @return Il vecchio testo di questo aggiornamento formattato.
     */
    public String getOldTextFormatted() {
        return oldText.replaceAll("\n", "<br>");
    }

    /**
     * Restituisce una linea specifica del vecchio testo di questo aggiornamento.
     *
     * @param index L'indice della linea da restituire.
     * @return La linea del vecchio testo di questo aggiornamento.
     */
    public String getOldTextLine(int index) {
        return oldText.split("\n")[index];
    }

    /**
     * Restituisce la data di creazione di questo aggiornamento come stringa.
     *
     * @return La data di creazione di questo aggiornamento come stringa.
     */
    public String getCreationDateString() {
        return creationDate.toString().substring(0, 16);
    }
}