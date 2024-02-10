package wiki.GUI;

import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.PageUpdate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class NotificationPanel extends JPanel {
    private final JPanel internalPanel;
    private final Action onClick;
    private final Action onDelete;
    private final Action onView;

    private final boolean isNotificationViewed;
    private final boolean isNotificationTypeRequestUpdate;

    private Color backgroundColor;
    private String iconPath;



    @FunctionalInterface
    public interface Action {
        boolean performAction();
    }

    public NotificationPanel(Notification notification, String currentUser, Action onClick, Action onDelete, Action onView) {
        this.onClick = onClick;
        this.onDelete = onDelete;
        this.onView = onView;

        isNotificationViewed = notification.isViewed();
        isNotificationTypeRequestUpdate = notification.getType() == Notification.TYPE_REQUEST_UPDATE;
        backgroundColor = Color.white;

        if (!isNotificationViewed)
            backgroundColor = new Color(255, 245, 185, 255);

        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
        internalPanel.setBackground(backgroundColor);

        PageUpdate pageUpdate = notification.getUpdate();
        Page page = pageUpdate.getPage();

        String notificationContent = "";

        switch (notification.getType()) {
            case Notification.TYPE_REQUEST_UPDATE:
                notificationContent = "<html>Richiesta di modifica da parte dell'utente: <i>" + notification.getUpdate().getAuthor() + "</i></html>";
                iconPath = "src/main/resources/email.png";
                break;
            case Notification.TYPE_UPDATE_ACCEPTED:
                if (Objects.equals(page.getAuthorName(), currentUser))
                    notificationContent = "<html>Hai <font color='#32BA7C'><b>accettato</b></font> la richiesta di modifica da parte dell'utente: <i>" + notification.getUpdate().getAuthor() + "</i></html>";
                else
                    notificationContent = "<html>La richiesta di modifica è stata <font color='#32BA7C'><b>accettata</b></font></html>";
                iconPath = "src/main/resources/checked.png";
                break;
            case Notification.TYPE_UPDATE_REJECTED:
                if (Objects.equals(page.getAuthorName(), currentUser))
                    notificationContent = "<html>Hai <font color='#FF0000'><b>rifiutato</b></font> la richiesta di modifica da parte dell'utente: <i>" + notification.getUpdate().getAuthor() + "</i></html>";
                else
                    notificationContent = "<html>La richiesta di modifica è stata <font color='#FF0000'><b>rifiutata</b></font></html>";
                iconPath = "src/main/resources/x-button.png";
                break;
        }

        // Titolo
        JLabel pageTitleLabel = new JLabel(page.getTitle());
        pageTitleLabel.setFont(new Font(pageTitleLabel.getFont().getFontName(), Font.BOLD, 18));
        pageTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 2));

        // Icona
        Image icon = new ImageIcon(iconPath).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(icon));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 0));

        // Contenuto
        JLabel notificationContentLabel = new JLabel(notificationContent);
        notificationContentLabel.setFont(new Font(notificationContentLabel.getFont().getFontName(), Font.PLAIN, 13));
        notificationContentLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 2, 2));

        // Data e id
        JLabel dateAndIdLabel = new JLabel(notification.getCreationString() + "  -  PageID: " + page.getId());
        dateAndIdLabel.setFont(new Font(dateAndIdLabel.getFont().getFontName(), Font.PLAIN, 12));
        dateAndIdLabel.setForeground(Color.darkGray);
        dateAndIdLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 2));

        internalPanel.add(pageTitleLabel);
        internalPanel.add(notificationContentLabel);
        internalPanel.add(dateAndIdLabel);

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createMatteBorder(1, 5, 1, 3, Color.lightGray));
        this.setBackground(backgroundColor);
        this.setMaximumSize(new Dimension(getMaximumSize().width, 100));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Right click
                if (SwingUtilities.isRightMouseButton(e))
                    showPopupMenu(e.getX(), e.getY());
                // Left click
                else
                    onClick.performAction();
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                setVisualFeedback();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                revertVisualFeedback();
            }
        });

        this.add(iconLabel);
        this.add(internalPanel);
    }

    private void showPopupMenu(int x, int y) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem visualizzaItem = new JMenuItem();
        JMenuItem segnaComeLettoItem = new JMenuItem("Segna come letto");
        JMenuItem eliminaItem = new JMenuItem("Elimina");

        if (isNotificationTypeRequestUpdate)
            visualizzaItem.setText("Visualizza richiesta di modifica");
        else
            visualizzaItem.setText("Visualizza pagina");

        visualizzaItem.addActionListener(e -> {
            onClick.performAction();
            popupMenu.setVisible(false);
        });

        segnaComeLettoItem.addActionListener(e -> {
            if (onView.performAction()) {
                // aggiornamento della UI
                if (!isNotificationViewed) {
                    backgroundColor = Color.white;
                    revertVisualFeedback();
                }
            }
            popupMenu.setVisible(false);
        });

        eliminaItem.addActionListener(e -> {
            if (onDelete.performAction()) {
                // Rimuovi questo panel dal suo parent
                var parent = this.getParent();
                if (parent != null) {
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                }
            }
            popupMenu.setVisible(false);
        });

        popupMenu.add(visualizzaItem);

        if (!isNotificationViewed)
            popupMenu.add(segnaComeLettoItem);

        popupMenu.add(eliminaItem);
        popupMenu.show(this, x, y);
    }

    private void setVisualFeedback() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        internalPanel.setBackground(backgroundColor.darker());
        this.setBackground(backgroundColor.darker());
    }

    private void revertVisualFeedback() {
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        internalPanel.setBackground(backgroundColor);
        this.setBackground(backgroundColor);
    }
}
