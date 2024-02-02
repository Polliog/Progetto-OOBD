package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Notification;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserNotifications extends PageBase {
    private JButton backBtn;
    private JPanel mainPanel;
    private JLabel usernameLabel;
    private JLabel notificationsCounterLabel;
    private JScrollPane notificationScrollPanel;

    public UserNotifications(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        setMinimumSize(new Dimension(600, 700));

        usernameLabel.setText("<html>Utente: <b>" + wikiController.getLoggedUser().getUsername() + "</b></html>");

        // Event Listeners
        backBtn.addActionListener(e -> onBackButtonPressed());

        updateNotificationCounterLabel();
        createUIComponents();
    }

    private void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void createUIComponents() {
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        // Ottenere le notifiche dall'utente loggato
        ArrayList<Notification> notifications = wikiController.fetchUserNotifications();

        for (Notification notification : notifications) {
            notificationsPanel.add(new NotificationPanel(
                    notification,
                    wikiController.getLoggedUser().getUsername(),
                    () -> openNotification(notification),
                    () -> deleteNotification(notification),
                    () -> viewNotification(notification)
            ));
        }

        // Imposta il JPanel delle notifiche come viewport del JScrollPane
        notificationScrollPanel.setViewportView(notificationsPanel);
    }

    private boolean openNotification(Notification notification) {
        if (notification.getType() == Notification.TYPE_REQUEST_UPDATE)
            new ReviewUpdates(wikiController, this, notification.getUpdate().getPage());
        else
            new PageView(wikiController, this, notification.getUpdate().getPage().getId());

        // Se la notifica non è stata letta
        // Segna come letta quando viene aperta per la prima volta
        if (!notification.isViewed())
            viewNotification(notification);

        return true;
    }

    private boolean deleteNotification(Notification notification) {
        if (wikiController.deleteNotification(notification)) {
            updateNotificationCounterLabel();
            return true;
        }
        return false;
    }

    private boolean viewNotification(Notification notification) {
        if (wikiController.setNotificationViewed(notification.getId())) {
            updateNotificationCounterLabel();
            return true;
        }
        return false;
    }

    private void updateNotificationCounterLabel() {
        notificationsCounterLabel.setText("<html>Hai <b>" + wikiController.getLoggedUser().getNotifications(-1).size() + "</b> notifiche, di cui <b>" + wikiController.getLoggedUser().getNotifications(0).size() + "</b> in sospeso.</html>");
    }
}