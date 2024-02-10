package wiki.Models;

import java.util.Date;

public class PageContentString {
    private final int id;
    private final String content;
    private final int order_num;
    private final String author;

    public PageContentString(int id, String content, int order_num, String author) {
        this.content = content;
        this.order_num = order_num;
        this.author = author;
        this.id = id;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getText() {
        return content;
    }
    public int getOrderNum() {
        return order_num;
    }
    public String getAuthor() {
        return author;
    }
}
