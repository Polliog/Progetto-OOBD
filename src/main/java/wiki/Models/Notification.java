package wiki.Models;


public class Notification {
    private int id;

    private int status; //0 = non letta, 1 = letta
    private Update update;

    public Notification(int id, int status, Update update) {
        this.id = id;
        this.status = status;
        this.update = update;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public Update getUpdate() {
        return update;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", status=" + status +
                ", update=" + update +
                '}';
    }
}
