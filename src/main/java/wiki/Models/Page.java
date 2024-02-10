package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Page {
    private final int id;
    private final String title;
    private final String author;
    private final Timestamp creationDate;


    public Page(int id, String title, String author, Timestamp creationDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.creationDate = creationDate;
    }


    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthorName() {
        return author;
    }
    public String getDateString() {
        return creationDate.toString().substring(0, 16);
    }
}
