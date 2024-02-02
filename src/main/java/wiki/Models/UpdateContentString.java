package wiki.Models;

public class UpdateContentString {
    private int id;
    private String text;
    private int orderNum;

    private int type; //0 = uguali, 1 = diversi, 2 = aggiunto, 3 = rimosso


    public UpdateContentString(int id, String text, int orderNum, int type) {
        this.id = id;
        this.text = text;
        this.orderNum = orderNum;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
