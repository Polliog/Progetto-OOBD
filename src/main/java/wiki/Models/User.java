package wiki.Models;

import java.util.ArrayList;

public class User {
    //attributes
    private String username;
    private String password;

    private ArrayList<Notification> notifications = new ArrayList<>();


    //constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    //metodi

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getNotificationCount() {
        return notifications.size();
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

}
