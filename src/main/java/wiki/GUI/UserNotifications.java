package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.NotificationPanel;
import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.Utils.NotificationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * La classe UserNotifications estende PageBase e implementa IUpdatable.
 * Rappresenta la pagina delle notifiche dell'utente.
 * Ogni UserNotifications ha vari componenti dell'interfaccia utente e una lista di notifiche.
 */
public class UserNotifications extends PageBase implements IUpdatable {
    private JPanel mainPanel; // Il pannello principale della pagina delle notifiche
    private JButton backBtn; // Il pulsante per tornare indietro
    private JButton searchBtn; // Il pulsante per la ricerca
    private JLabel usernameLabel; // L'etichetta per il nome utente
    private JLabel notificationsCounterLabel; // L'etichetta per il conteggio delle notifiche
    private JScrollPane notificationScrollPanel; // Il pannello di scorrimento per la visualizzazione delle notifiche
    private JTextField textField1; // Il campo di testo per la ricerca

    private JRadioButton requestUpdateRadBtn; // Il pulsante radio per la richiesta di aggiornamento
    private JRadioButton updateAcceptedRadBtn; // Il pulsante radio per l'aggiornamento accettato
    private JRadioButton updateRejectedRadBtn; // Il pulsante radio per l'aggiornamento rifiutato
    private JRadioButton isViewedRadBtn; // Il pulsante radio per le notifiche visualizzate
    private JRadioButton notViewedRadBtn; // Il pulsante radio per le notifiche non visualizzate
    private JRadioButton anyTypeRadBtn; // Il pulsante radio per qualsiasi tipo di notifica
    private JRadioButton anyViewedRadBtn; // Il pulsante radio per qualsiasi stato di visualizzazione

    private ArrayList<Notification> notifications; // La lista di notifiche

    /**
     * Costruisce una nuova UserNotifications con i dettagli specificati.
     *
     * @param wikiController Il controller del wiki.
     * @param prevPageRef La pagina precedente.
     */
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
        createNotificationPanels();
    }

    /**
     * Aggiorna l'interfaccia utente.
     */
    @Override
    public void updateGUI() {
        fetchData();
    }

    // Metodi privati per gestire le azioni dell'utente e aggiornare l'interfaccia utente
    private void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void createNotificationPanels() {
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
        createNotificationPanels();
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

        if (p == null)
            return false;

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
        // Aggiorna il conteggio delle notifiche
        notifications = wikiController.fetchAllUserNotifications();

        notificationsCounterLabel.setText("<html>Hai <b>" + notifications.size() +
                "</b> notifiche, di cui <b>" + NotificationUtils.getTypeRequestUpdateCount(notifications) +
                "</b> in sospeso e <b>" + NotificationUtils.getUnviewedCount(notifications) + "</b> da leggere.</html>");

    }
}