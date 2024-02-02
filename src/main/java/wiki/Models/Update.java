package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Update {
    public static final int STATUS_REJECTED = 0;
    public static final int STATUS_ACCEPTED = 1;
    public static final int STATUS_PENDING = -1;

    private int id;
    private Page page;
    private String author;
    private int status; //-1 = pending, 0 = rejected, 1 = accepted
    private ArrayList<UpdateContentString> contentStrings;
    private String oldText;

    private Date creation;


    public Update(int id, Page page, String author, int status, Timestamp creation, String oldText, ArrayList<UpdateContentString> contentStrings) {
        this.id = id;
        this.page = page;
        this.author = author;
        this.status = status;
        this.creation = creation;
        this.oldText = oldText;
        this.contentStrings = contentStrings;
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

    public ArrayList<UpdateContentString> getContentStrings() {
        return contentStrings;
    }

    public void setContentStrings(ArrayList<UpdateContentString> contentStrings) {
        this.contentStrings = contentStrings;
    }

    public String getAllContentStrings() {
        String content = "";
        for (UpdateContentString contentString : contentStrings) {
            content += contentString.getText() + "\n";
        }
        return content;
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

    public void setOldText(String oldText) {
        this.oldText = oldText;
    }

    public Date getCreation() {
        return creation;
    }

    public String getCreationDateString() {
        return creation.toString().substring(0, 16);
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
}