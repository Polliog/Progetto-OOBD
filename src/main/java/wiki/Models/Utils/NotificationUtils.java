package wiki.Models.Utils;

import wiki.Models.Notification;

import java.util.ArrayList;

public final class NotificationUtils {
    public static int getTypeRequestUpdateCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_REQUEST_UPDATE) {
                count++;
            }
        }
        return count;
    }

    public static int getTypeUpdateAcceptedCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_UPDATE_ACCEPTED) {
                count++;
            }
        }
        return count;
    }

    public static int getTypeUpdateRejectedCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_UPDATE_REJECTED) {
                count++;
            }
        }
        return count;
    }

    public static int getUnviewedCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.isViewed()) {
                count++;
            }
        }
        return count;
    }
}
