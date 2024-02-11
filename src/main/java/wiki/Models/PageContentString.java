package wiki.Models;

public class PageContentString {
    private final int id;
    private final String content;
    private final int order_num;
    private final String authorName;

    public PageContentString(int id, String content, int order_num, String authorName) {
        this.content = content;
        this.order_num = order_num;
        this.authorName = authorName;
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
    public String getAuthorName() {
        return authorName;
    }
}
