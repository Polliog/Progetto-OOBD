package wiki.Models;

public class UpdateContentString {
    private final int id;
    private final String text;
    private final int orderNum;
    private final int type; //0 = uguali, 1 = diversi, 2 = aggiunto, 3 = rimosso


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
