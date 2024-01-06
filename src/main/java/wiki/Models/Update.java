package wiki.Models;

import java.util.ArrayList;
import java.util.Date;

public class Update {
    private int id;
    private Page page;
    private String author;
    private int status; //2 = pending, 0 = rejected, 1 = accepted
    private ArrayList<UpdateContentString> contentStrings = new ArrayList<>();
    private String oldText;

    private Date creation;


    public Update(int id, Page page, String author, int status, ArrayList<UpdateContentString> contentStrings, Date creation) {
        this.id = id;
        this.page = page;
        this.author = author;
        this.status = status;
        this.contentStrings = contentStrings;
        this.creation = creation;
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

    public void setOldText(String oldText) {
        this.oldText = oldText;
    }

    public Date getCreation() {
        return creation;
    }

    public void setCreation(Date creation) {
        this.creation = creation;
    }
}

