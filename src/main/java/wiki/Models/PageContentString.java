package wiki.Models;

/**
 * La classe PageContentString rappresenta il contenuto di una pagina.
 * Ogni contenuto di pagina ha un id, un contenuto, un numero d'ordine e un nome autore.
 */
public class PageContentString {
    private final int id; // L'ID del contenuto della pagina
    private final String content; // Il contenuto della pagina
    private final int order_num; // Il numero d'ordine del contenuto della pagina
    private final String authorName; // Il nome dell'autore del contenuto della pagina

    /**
     * Costruisce un nuovo contenuto di pagina con i dettagli specificati.
     *
     * @param id L'ID del contenuto della pagina.
     * @param content Il contenuto della pagina.
     * @param order_num Il numero d'ordine del contenuto della pagina.
     * @param authorName Il nome dell'autore del contenuto della pagina.
     */
    public PageContentString(int id, String content, int order_num, String authorName) {
        this.content = content;
        this.order_num = order_num;
        this.authorName = authorName;
        this.id = id;
    }

    /**
     * Restituisce l'ID di questo contenuto di pagina.
     *
     * @return L'ID di questo contenuto di pagina.
     */
    public int getId() {
        return id;
    }

    /**
     * Restituisce il testo di questo contenuto di pagina.
     *
     * @return Il testo di questo contenuto di pagina.
     */
    public String getText() {
        return content;
    }

    /**
     * Restituisce il numero d'ordine di questo contenuto di pagina.
     *
     * @return Il numero d'ordine di questo contenuto di pagina.
     */
    public int getOrderNum() {
        return order_num;
    }

    /**
     * Restituisce il nome dell'autore di questo contenuto di pagina.
     *
     * @return Il nome dell'autore di questo contenuto di pagina.
     */
    public String getAuthorName() {
        return authorName;
    }
}