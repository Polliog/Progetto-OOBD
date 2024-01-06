package wiki.Models;

public class UpdateContentString {
    private int id;
    private String text;
    private int order_num;

    private int type; //0 = uguali, 1 = diversi, 2 = aggiunto, 3 = rimosso


    public UpdateContentString(int id, String text, int order_num, int type) {
        this.id = id;
        this.text = text;
        this.order_num = order_num;
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

    public int getOrder_num() {
        return order_num;
    }

    public void setOrder_num(int order_num) {
        this.order_num = order_num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
