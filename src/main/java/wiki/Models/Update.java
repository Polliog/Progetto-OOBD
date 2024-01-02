package wiki.Models;

public class Update {
    private int id;
    private Page page;
    private String author;
    private int status; //2 = pending, 0 = rejected, 1 = accepted

    public Update(int id, Page page, String author, int status) {
        this.id = id;
        this.page = page;
        this.author = author;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

