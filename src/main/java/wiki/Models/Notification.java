package wiki.Models;


import java.sql.Timestamp;

/**
 * La classe Notification rappresenta una notifica.
 * Ogni notifica ha un id, un tipo, un aggiornamento di pagina, una data di creazione e uno stato di visualizzazione.
 */
public class Notification {
    public static final int TYPE_REQUEST_UPDATE = 0; // Tipo per indicare una richiesta di aggiornamento
    public static final int TYPE_UPDATE_ACCEPTED = 1; // Tipo per indicare un aggiornamento accettato
    public static final int TYPE_UPDATE_REJECTED = 2; // Tipo per indicare un aggiornamento rifiutato

    private final int id; // L'ID della notifica
    private final int type; // Il tipo della notifica
    private final PageUpdate pageUpdate; // L'aggiornamento di pagina della notifica
    private final Timestamp creation; // La data di creazione della notifica
    private final boolean viewed; // Lo stato di visualizzazione della notifica

    /**
     * Costruisce una nuova notifica con i dettagli specificati.
     *
     * @param id L'ID della notifica.
     * @param type Il tipo della notifica.
     * @param viewed Lo stato di visualizzazione della notifica.
     * @param pageUpdate L'aggiornamento di pagina della notifica.
     * @param creation La data di creazione della notifica.
     */
    public Notification(int id, int type, boolean viewed, PageUpdate pageUpdate, Timestamp creation) {
        this.id = id;
        this.type = type;
        this.viewed = viewed;
        this.pageUpdate = pageUpdate;
        this.creation = creation;
    }

    /**
     * Restituisce l'ID di questa notifica.
     *
     * @return L'ID di questa notifica.
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il tipo di questa notifica.
     *
     * @return Il tipo di questa notifica.
     */
    public int getType() {
        return type;
    }

    /**
     * Restituisce lo stato di visualizzazione di questa notifica.
     *
     * @return Lo stato di visualizzazione di questa notifica.
     */
    public boolean isViewed() {
        return viewed;
    }

    /**
     * Restituisce la data di creazione di questa notifica come stringa.
     *
     * @return La data di creazione di questa notifica come stringa.
     */
    public String getCreationString() {
        return creation.toString().substring(0, 16);
    }

    /**
     * Restituisce l'aggiornamento di pagina di questa notifica.
     *
     * @return L'aggiornamento di pagina di questa notifica.
     */
    public PageUpdate getUpdate() {
        return pageUpdate;
    }
}