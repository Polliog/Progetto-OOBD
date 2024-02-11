package wiki.Models;

public class UpdateContentString {
    public static final int TYPE_SAME = 0;
    public static final int TYPE_DIFFERENT = 1;
    public static final int TYPE_ADDED = 2;
    public static final int TYPE_REMOVED = 3;


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
