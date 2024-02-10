package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class PageUpdate {
    public static final int STATUS_REJECTED = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_PENDING = -1;

    private final int id;
    private final Page page;
    private final String author;
    private final int status;
    private final String oldText;
    private final Timestamp creation;


    public PageUpdate(int id, Page page, String author, int status, Timestamp creation, String oldText) {
        this.id = id;
        this.page = page;
        this.author = author;
        this.status = status;
        this.creation = creation;
        this.oldText = oldText;
    }


    public int getId() {
        return id;
    }
    public Page getPage() {
        return page;
    }
    public String getAuthor() {
        return author;
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
        return creation.toString().substring(0, 16);
    }
}