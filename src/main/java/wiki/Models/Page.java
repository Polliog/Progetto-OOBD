package wiki.Models;

import java.sql.Timestamp;

/**
 * La classe Page rappresenta una pagina.
 * Ogni pagina ha un id, un titolo, un nome autore e una data di creazione.
 */
public class Page {
    private final int id; // L'ID della pagina
    private final String title; // Il titolo della pagina
    private final String authorName; // Il nome dell'autore della pagina
    private final Timestamp creationDate; // La data di creazione della pagina

    /**
     * Costruisce una nuova pagina con i dettagli specificati.
     *
     * @param id L'ID della pagina.
     * @param title Il titolo della pagina.
     * @param authorName Il nome dell'autore della pagina.
     * @param creationDate La data di creazione della pagina.
     */
    public Page(int id, String title, String authorName, Timestamp creationDate) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.creationDate = creationDate;
    }

    /**
     * Restituisce l'ID di questa pagina.
     *
     * @return L'ID di questa pagina.
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il titolo di questa pagina.
     *
     * @return Il titolo di questa pagina.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Restituisce il nome dell'autore di questa pagina.
     *
     * @return Il nome dell'autore di questa pagina.
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Restituisce la data di creazione di questa pagina come stringa.
     *
     * @return La data di creazione di questa pagina come stringa.
     */
    public String getCreationDateString() {
        return creationDate.toString().substring(0, 16);
    }
}