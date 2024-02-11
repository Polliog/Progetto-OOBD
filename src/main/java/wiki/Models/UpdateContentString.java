package wiki.Models;

/**
 * La classe UpdateContentString rappresenta una stringa di contenuto aggiornata.
 * Ogni stringa di contenuto aggiornata ha un id, un testo, un numero d'ordine e un tipo.
 */
public class UpdateContentString {
    public static final int TYPE_SAME = 0; // Tipo per indicare che il contenuto è rimasto lo stesso
    public static final int TYPE_DIFFERENT = 1; // Tipo per indicare che il contenuto è diverso
    public static final int TYPE_ADDED = 2; // Tipo per indicare che il contenuto è stato aggiunto
    public static final int TYPE_REMOVED = 3; // Tipo per indicare che il contenuto è stato rimosso

    private final int id; // L'ID della stringa di contenuto aggiornata
    private final String text; // Il testo della stringa di contenuto aggiornata
    private final int orderNum; // Il numero d'ordine della stringa di contenuto aggiornata
    private final int type; // Il tipo della stringa di contenuto aggiornata

    /**
     * Costruisce una nuova stringa di contenuto aggiornata con i dettagli specificati.
     *
     * @param id L'ID della stringa di contenuto aggiornata.
     * @param text Il testo della stringa di contenuto aggiornata.
     * @param orderNum Il numero d'ordine della stringa di contenuto aggiornata.
     * @param type Il tipo della stringa di contenuto aggiornata.
     */
    public UpdateContentString(int id, String text, int orderNum, int type) {
        this.id = id;
        this.text = text;
        this.orderNum = orderNum;
        this.type = type;
    }

    /**
     * Restituisce l'ID di questa stringa di contenuto aggiornata.
     *
     * @return L'ID di questa stringa di contenuto aggiornata.
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il testo di questa stringa di contenuto aggiornata.
     *
     * @return Il testo di questa stringa di contenuto aggiornata.
     */
    public String getText() {
        return text;
    }

    /**
     * Restituisce il numero d'ordine di questa stringa di contenuto aggiornata.
     *
     * @return Il numero d'ordine di questa stringa di contenuto aggiornata.
     */
    public int getOrderNum() {
        return orderNum;
    }

    /**
     * Restituisce il tipo di questa stringa di contenuto aggiornata.
     *
     * @return Il tipo di questa stringa di contenuto aggiornata.
     */
    public int getType() {
        return type;
    }
}