package wiki.Models;

import java.sql.Timestamp;

public class Page {
    private final int id;
    private final String title;
    private final String authorName;
    private final Timestamp creationDate;


    public Page(int id, String title, String authorName, Timestamp creationDate) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.creationDate = creationDate;
    }


    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthorName() {
        return authorName;
    }
    public String getCreationDateString() {
        return creationDate.toString().substring(0, 16);
    }
}
