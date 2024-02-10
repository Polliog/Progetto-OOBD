package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.Utils.NotificationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserNotifications extends PageBase {
    private JButton backBtn;
    private JPanel mainPanel;
    private JLabel usernameLabel;
    private JLabel notificationsCounterLabel;
    private JScrollPane notificationScrollPanel;
    private JTextField textField1;
    private JButton searchBtn;
    private JRadioButton requestUpdateRadBtn;
    private JRadioButton updateAcceptedRadBtn;
    private JRadioButton updateRejectedRadBtn;
    private JRadioButton isViewedRadBtn;
    private JRadioButton notViewedRadBtn;
    private JRadioButton anyTypeRadBtn;
    private JRadioButton anyViewedRadBtn;

    ArrayList<Notification> notifications;


    public UserNotifications(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(mainPanel);
        setMinimumSize(new Dimension(600, 700));

        notifications = wikiController.fetchAllUserNotifications();

        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(anyTypeRadBtn);
        typeGroup.add(requestUpdateRadBtn);
        typeGroup.add(updateAcceptedRadBtn);
        typeGroup.add(updateRejectedRadBtn);
        typeGroup.setSelected(anyTypeRadBtn.getModel(), true);

        ButtonGroup viewedGroup = new ButtonGroup();
        viewedGroup.add(anyViewedRadBtn);
        viewedGroup.add(isViewedRadBtn);
        viewedGroup.add(notViewedRadBtn);
        viewedGroup.setSelected(anyViewedRadBtn.getModel(), true);

        usernameLabel.setText("<html>Utente: <b>" + wikiController.getLoggedUser().getUsername() + "</b></html>");



        // Event Listeners
        backBtn.addActionListener(e -> onBackButtonPressed());
        searchBtn.addActionListener(e -> fetchData());
        textField1.addActionListener(e -> fetchData());

        updateNotificationCounterLabel();
        updateOrCreateNotificationPanels();
    }

    private void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void updateOrCreateNotificationPanels() {
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

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

    private void fetchData() {
        notifications = wikiController.fetchAllUserNotifications(textField1.getText(), getSelectedType(), getSelectedViewed());
        updateNotificationCounterLabel();
        updateOrCreateNotificationPanels();
    }



    private Integer getSelectedType() {
        if (requestUpdateRadBtn.isSelected())
            return Notification.TYPE_REQUEST_UPDATE;
        if (updateAcceptedRadBtn.isSelected())
            return Notification.TYPE_UPDATE_ACCEPTED;
        if (updateRejectedRadBtn.isSelected())
            return Notification.TYPE_UPDATE_REJECTED;
        return null;
    }

    private Boolean getSelectedViewed() {
        if (isViewedRadBtn.isSelected())
            return true;
        if (notViewedRadBtn.isSelected())
            return false;
        return null;
    }



    private boolean openNotification(Notification notification) {
        Page p = notification.getUpdate().getPage();

        if (notification.getType() == Notification.TYPE_REQUEST_UPDATE)
            new ReviewUpdates(wikiController, this, p);
        else
            new PageView(wikiController, this, p);

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
        notificationsCounterLabel.setText("<html>Hai <b>" + notifications.size() +
                "</b> notifiche, di cui <b>" + NotificationUtils.getTypeRequestUpdateCount(notifications) +
                "</b> in sospeso e <b>" + NotificationUtils.getUnviewedCount(notifications) + "</b> da leggere.</html>");
    }
}