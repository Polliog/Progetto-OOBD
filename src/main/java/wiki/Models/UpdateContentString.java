package wiki.Models;

public class UpdateContentString {
    private static final int TYPE_SAME = 0;
    private static final int TYPE_DIFFERENT = 1;
    private static final int TYPE_ADDED = 2;
    private static final int TYPE_REMOVED = 3;


    private final int id;
    private final String text;
    private final int orderNum;
    private final int type;


    public UpdateContentString(int id, String text, int orderNum, int type) {
        this.id = id;
        this.text = text;
        this.orderNum = orderNum;
        this.type = type;
    }

    // Getters
    public int getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public int getOrderNum() {
        return orderNum;
    }
    public int getType() {
        return type;
    }
}
