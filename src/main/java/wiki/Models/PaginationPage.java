package wiki.Models;

import java.util.ArrayList;

public class PaginationPage {
    public ArrayList<Page> pages;
    public int currentPage;
    public int totalPages;

    public int limit = 10;

    public PaginationPage(ArrayList<Page> pages, int count, int currentPage, int limit) {
        this.pages = pages;
        this.currentPage = currentPage;
        this.limit = limit;
        this.totalPages = (int) Math.ceil((double) count / limit);
    }
}
