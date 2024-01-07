package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Notification;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserNotifications extends PageBase {

    private JButton indietroButton;
    private JPanel notificationsViewPanel;
    private JLabel notificationsLabel;
    private JLabel notificationsCounterLabel;
    private JScrollPane notificationScrollPanel;


    public UserNotifications(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);
        initGUI( true,new Dimension(550, 400));
        add(notificationsViewPanel);
        setVisible(true);

        //auth guard
        if (!wikiController.isUserLogged()) {
            PageBase wikiPages = new WikiPages(wikiController, this);
            this.setVisible(false);
            this.dispose();
        }

        notificationsLabel.setText("Notifiche di " + wikiController.getLoggedUser().getUsername());
        notificationsCounterLabel.setText("Hai " + wikiController.getLoggedUser().getNotifications(-1).size() + " notifiche, di cui " + wikiController.getLoggedUser().getNotifications(0).size() + " in sospeso.");

        indietroButton.addActionListener(e -> onIndietro());
        createUIComponents();
    }

    private void onIndietro() {
        PageBase wikiPages = new WikiPages(wikiController, this);
        this.setVisible(false);
        this.dispose();
    }

    private void createUIComponents() {
        // Creare un JPanel per contenere tutte le notifiche
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        // Ottenere le notifiche dall'utente loggato
        ArrayList<Notification> notifications = wikiController.getLoggedUser().getNotifications(-1);

        for (Notification notification : notifications) {
            // Creare un JPanel per la notifica con layout verticale
            JPanel notificationPanel = new JPanel();
            notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
            notificationPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));


            // Creare un JTextArea per il testo della notifica

            JTextArea notificationTextArea = new JTextArea();

            switch (notification.getType()) {
                case 0:
                    notificationTextArea.setText("Aggiornamento della pagina \"" + notification.getUpdate().getPage().getTitle() + "\" da " + notification.getUpdate().getAuthor() + "");

                    switch (notification.getUpdate().getStatus()) {
                        case 2:
                            notificationTextArea.setText(notificationTextArea.getText() + " (in attesa di revisione)");
                            //color
                            notificationTextArea.setForeground(Color.BLUE);
                            break;
                        case 1:
                            notificationTextArea.setText(notificationTextArea.getText() + " (approvato)");
                            notificationTextArea.setForeground(Color.GREEN);
                            break;
                        case 0:
                            notificationTextArea.setText(notificationTextArea.getText() + " (rifiutato)");
                            notificationTextArea.setForeground(Color.RED);
                            break;
                    }

                    break;
                case 1:
                    notificationTextArea.setText("La tua modifica per la pagina \"" + notification.getUpdate().getPage().getTitle() + "\" e' stata approvata");
                    break;
                case 2:
                    notificationTextArea.setText("La tua modifica per la pagina \"" + notification.getUpdate().getPage().getTitle() + "\" e' stata rifiutata");
                    break;
                default:
                    notificationTextArea.setText("Aggiornamento della pagina \"" + notification.getUpdate().getPage().getTitle() + "\" da " + notification.getUpdate().getAuthor() + "");
                    break;
            }


            notificationTextArea.setLineWrap(true);
            notificationTextArea.setWrapStyleWord(true);
            notificationTextArea.setEditable(false);
            notificationPanel.add(notificationTextArea);

            // Creare un JPanel per i bottoni con layout orizzontale
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JButton viewButton = new JButton("Visualizza");
            viewButton.addActionListener(e -> {
                PageBase pageView = null;
                switch (notification.getType()) {
                    case 0:

                        if (notification.getUpdate().getStatus() != 2) {
                            pageView = new PageView(wikiController, this, notification.getUpdate().getPage().getId());
                            this.wikiController.setNotificationStatus(notification.getId(), 1);
                            this.setVisible(false);
                            this.dispose();
                            break;
                        }

                        pageView = new ReviewUpdate(wikiController, this, notification.getUpdate(), true);
                        this.wikiController.setNotificationStatus(notification.getId(), 1);
                        this.setVisible(false);
                        this.dispose();
                        break;
                    case 1:
                    case 2:
                    default:
                        pageView = new PageView(wikiController, this, notification.getUpdate().getPage().getId());
                        this.wikiController.setNotificationStatus(notification.getId(), 1);
                        this.setVisible(false);
                        this.dispose();
                        break;
                }
            });
            buttonsPanel.add(viewButton);

            if (notification.getStatus() == 0) {
                JButton markAsReadButton = new JButton("Segna come letta");
                markAsReadButton.addActionListener(e -> {
                    this.wikiController.setNotificationStatus(notification.getId(), 1);
                    markAsReadButton.setVisible(false);
                    notificationsCounterLabel.setText("Hai " + wikiController.getLoggedUser().getNotifications(-1).size() + " notifiche, di cui " + wikiController.getLoggedUser().getNotifications(0).size() + " in sospeso.");
                });
                buttonsPanel.add(markAsReadButton);
            }

            JButton deleteButton = new JButton("Elimina");
            deleteButton.addActionListener(e -> {
                if (this.wikiController.deleteNotification(notification)) {
                    notificationPanel.setVisible(false);
                    notificationsCounterLabel.setText("Hai " + wikiController.getLoggedUser().getNotifications(-1).size() + " notifiche, di cui " + wikiController.getLoggedUser().getNotifications(0).size() + " in sospeso.");
                }
            });
            buttonsPanel.add(deleteButton);

            // Aggiungere il JPanel dei bottoni al JPanel della notifica
            notificationPanel.add(buttonsPanel);

            // Aggiungere il JPanel della notifica al JPanel delle notifiche

            //aggiungi alla notifcationpanel una max height
            notificationPanel.setMaximumSize(new Dimension(4000, 90));
            notificationsPanel.add(notificationPanel);
        }

        // Impostare il JPanel delle notifiche come viewport del JScrollPane
        notificationScrollPanel.setViewportView(notificationsPanel);
        // Impostare la politica di visualizzazione della barra di scorrimento orizzontale
        notificationScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

}
