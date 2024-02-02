package wiki.Models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class Page {
    private int id;
    private String title;
    private ArrayList<PageContentString> content = new ArrayList<>();
    private String author;
    private Timestamp date;

    private ArrayList<Update> updates = new ArrayList<>();

    public Page(int id, String title, String author, Timestamp date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.date = date;
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
        return date.toString().substring(0, 16);
    }

    public void setContent(ArrayList<PageContentString> content) {
        this.content = content;
    }

    public void addContent(PageContentString content) {
        this.content.add(content);
    }

    public void removeContent(int index) {
        this.content.remove(index);
    }

    public void removeContent(PageContentString content) {
        this.content.remove(content);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getAllContent() {
        StringBuilder sb = new StringBuilder();

        this.content.forEach(content -> {
            sb.append(content.content);
            sb.append("\n");
        });

        return sb.toString();
    }

    public String getAllContentHtml() {
        StringBuilder sb = new StringBuilder();

        this.content.forEach(content -> {
            sb.append(content.content);
            sb.append("<br>");
        });

        return sb.toString();
    }

    public String getLine(int line) {
        for (PageContentString content : this.content) {
            if (content.order_num == line) {
                return content.content;
            }
        }
        return null;
    }

    public String toString() {
        return "Page{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content=" + content +
                ", author='" + author + '\'' +
                ", creation=" + date + '\'' +
                ", updates=" + updates +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Update> getUpdates() {
        return updates;
    }

    public void setUpdates(ArrayList<Update> updates) {
        this.updates = updates;
    }
}
