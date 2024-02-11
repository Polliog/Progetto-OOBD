package wiki.Models;

import java.sql.Timestamp;

public class PageUpdate {
    public static final int STATUS_REJECTED = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_PENDING = -1;

    private final int id;
    private final Page page;
    private final String authorName;
    private final int status;
    private final String oldText;
    private final Timestamp creationDate;


    public PageUpdate(int id, Page page, String authorName, int status, Timestamp creationDate, String oldText) {
        this.id = id;
        this.page = page;
        this.authorName = authorName;
        this.status = status;
        this.creationDate = creationDate;
        this.oldText = oldText;
    }


    public int getId() {
        return id;
    }
    public Page getPage() {
        return page;
    }
    public String getAuthorName() {
        return authorName;
    }
    public int getStatus() {
        return status;
    }
    public String getOldText() {
        return oldText;
    }
    public String getOldTextFormatted() {
        return oldText.replaceAll("\n", "<br>");
    }
    public String getOldTextLine(int index) {
        return oldText.split("\n")[index];
    }
    public String getCreationDateString() {
        return creationDate.toString().substring(0, 16);
    }
}