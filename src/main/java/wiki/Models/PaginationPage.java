package wiki.Models;

import java.util.ArrayList;

/**
 * La classe PaginationPage rappresenta una pagina di paginazione.
 * Ogni pagina di paginazione ha una lista di pagine, il numero della pagina corrente, il numero totale di pagine e un limite.
 */
public class PaginationPage {
    public ArrayList<Page> pages; // La lista di pagine
    public int currentPage; // Il numero della pagina corrente
    public int totalPages; // Il numero totale di pagine

    public int limit = 10; // Il limite di pagine

    /**
     * Costruisce una nuova pagina di paginazione con i dettagli specificati.
     *
     * @param pages La lista di pagine.
     * @param count Il conteggio totale delle pagine.
     * @param currentPage Il numero della pagina corrente.
     * @param limit Il limite di pagine.
     */
    public PaginationPage(ArrayList<Page> pages, int count, int currentPage, int limit) {
        this.pages = pages;
        this.currentPage = currentPage;
        this.limit = limit;
        this.totalPages = (int) Math.ceil((double) count / limit);
    }
}