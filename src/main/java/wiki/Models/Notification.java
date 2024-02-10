package wiki.Models;


import java.sql.Timestamp;

public class Notification {
    public static final int TYPE_REQUEST_UPDATE = 0;
    public static final int TYPE_UPDATE_ACCEPTED = 1;
    public static final int TYPE_UPDATE_REJECTED = 2;


    private final int id;
    private final int type;
    private final PageUpdate pageUpdate;
    private final Timestamp creation;
    private final boolean viewed;


    public Notification(int id, int type, boolean viewed, PageUpdate pageUpdate, Timestamp creation) {
        this.id = id;
        this.type = type;
        this.viewed = viewed;
        this.pageUpdate = pageUpdate;
        this.creation = creation;
    }


    // Getters
    public int getId() {
        return id;
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
    public PageUpdate getUpdate() {
        return pageUpdate;
    }
}
