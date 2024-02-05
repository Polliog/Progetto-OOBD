package wiki.Models;

import java.util.ArrayList;

public class User {
    //attributes
    private final String username;
    private final boolean isAdmin;
    private ArrayList<Notification> notifications = new ArrayList<>();



    //constructor
    public User(String username, boolean isAdmin) {
        this.username = username.toLowerCase();
        this.isAdmin = isAdmin;
    }

    //metodi

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() { return isAdmin; }

    //getter and setter for updates

    public ArrayList<Notification> getNotifications(int status) {
        if (status == -1) {
            return this.notifications;
        } else {
            ArrayList<Notification> notifications = new ArrayList<>();

            for (Notification notification : this.notifications) {
                if (notification.getStatus() == status) {
                    notifications.add(notification);
                }
            }

            return notifications;
        }
    }

    public ArrayList<Notification> getNotifications(boolean viewed) {
        ArrayList<Notification> notifications = new ArrayList<>();

        for (Notification notification : this.notifications) {
            if (notification.isViewed() == viewed) {
                notifications.add(notification);
            }
        }

        return notifications;
    }



    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
}
