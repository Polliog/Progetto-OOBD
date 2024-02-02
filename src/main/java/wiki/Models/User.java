package wiki.Models;

import java.util.ArrayList;

public class User {
    //attributes
    private String username;

    private ArrayList<Notification> notifications = new ArrayList<>();



    //constructor
    public User(String username) {
        this.username = username.toLowerCase();
    }

    //metodi

    public String getUsername() {
        return username;
    }

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

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }
}
