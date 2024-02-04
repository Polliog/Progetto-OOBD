package wiki.Models;


import java.sql.Timestamp;

public class Notification {
    public static final int TYPE_REQUEST_UPDATE = 0;
    public static final int TYPE_UPDATE_ACCEPTED = 1;
    public static final int TYPE_UPDATE_REJECTED = 2;
    //

    public static final int STATUS_OPEN = 0;
    public static final int STATUS_CLOSED = 1;

    private int id;
    private int type;
    // change to read bool
    private int status;         //0 = APERTO 1 = CHIUSO
    private Update update;
    private Timestamp creation;
    private boolean viewed;

    public Notification(int id, int type, boolean viewed, Update update, Timestamp creation, int status) {
        this.id = id;
        this.type = type;
        this.viewed = viewed;
        this.update = update;
        this.creation = creation;
        this.status = status;
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

    public boolean isViewed() {
        return viewed;
    }

    public String getCreationString() {
        return creation.toString().substring(0, 16);
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
