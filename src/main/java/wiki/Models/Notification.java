package wiki.Models;


public class Notification {
    private int id;
    private int type; //0 = updateCreated, 1 = updateAccepted, 2 = updateRejected
    private int status; //0 = non letta, 1 = letta
    private Update update;

    public Notification(int id, int status, Update update, int type) {
        this.id = id;
        this.status = status;
        this.update = update;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
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
