package wiki.Models;

import java.util.Date;

public class PageContentString {
    public int id;
    public String content;
    public int order_num;
    public String link;

    public String author;

    public PageContentString(int id, String content, int order_num, String link, String author) {
        this.content = content;
        this.order_num = order_num;
        this.link = link;
        this.author = author;
        this.id = id;
    }
}
