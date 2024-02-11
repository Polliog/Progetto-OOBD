package wiki.Models.Utils;

import wiki.Models.Notification;

import java.util.ArrayList;

/**
 * La classe NotificationUtils fornisce metodi di utilità per lavorare con le notifiche.
 */
public final class NotificationUtils {
    /**
     * Restituisce il conteggio delle notifiche di tipo richiesta di aggiornamento.
     *
     * @param notifications La lista di notifiche.
     * @return Il conteggio delle notifiche di tipo richiesta di aggiornamento.
     */
    public static int getTypeRequestUpdateCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_REQUEST_UPDATE) {
                count++;
            }
        }
        return count;
    }

    /**
     * Restituisce il conteggio delle notifiche di tipo aggiornamento accettato.
     *
     * @param notifications La lista di notifiche.
     * @return Il conteggio delle notifiche di tipo aggiornamento accettato.
     */
    public static int getTypeUpdateAcceptedCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_UPDATE_ACCEPTED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Restituisce il conteggio delle notifiche di tipo aggiornamento rifiutato.
     *
     * @param notifications La lista di notifiche.
     * @return Il conteggio delle notifiche di tipo aggiornamento rifiutato.
     */
    public static int getTypeUpdateRejectedCount(ArrayList<Notification> notifications) {
        int count = 0;
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.TYPE_UPDATE_REJECTED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Restituisce il conteggio delle notifiche non visualizzate.
     *
     * @param notifications La lista di notifiche.
     * @return Il conteggio delle notifiche non visualizzate.
     */
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