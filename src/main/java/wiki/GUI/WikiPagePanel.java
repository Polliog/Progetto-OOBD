package wiki.GUI;

import wiki.Models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WikiPagePanel extends JPanel {
    private final JPanel internalPanel;
    private final Action onClick;
    private final Action onEdit;
    private final Action onDelete;

    private final boolean isCurrentUserAuthor;
    private final boolean isUserLoggedIn;
    private final Color backgroundColor;

    @FunctionalInterface
    public interface Action {
        boolean performAction();
    }

    public WikiPagePanel(String title, String contentPreview, String author, String date, User currentUser, Action onClick, Action onEdit, Action onDelete) {
        this.onClick = onClick;
        this.onEdit = onEdit;
        this.onDelete = onDelete;

        if (currentUser == null) {
            isUserLoggedIn = false;
            isCurrentUserAuthor = false;
        }
        else {
            isUserLoggedIn = true;
            isCurrentUserAuthor = currentUser.getUsername().equals(author);
        }

        backgroundColor = Color.white;

        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
        internalPanel.setBackground(backgroundColor);

        String iconPath;

        // Se la notifica è stata già letta
        if (isCurrentUserAuthor)
            iconPath = "src/main/resources/star.png";
        else
            iconPath = "src/main/resources/folder.png";

        // Titolo
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 2, 0));
        // Icona
        Image icon = new ImageIcon(iconPath).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(icon));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 0));
        // Contenuto
        JLabel contentPreviewLabel = new JLabel(contentPreview);
        contentPreviewLabel.setBorder(BorderFactory.createEmptyBorder(0, 17, 2, 0));
        // Autore e data
        JLabel authorLabel = new JLabel("Autore: " + author + " - Data: " + date);
        authorLabel.setBorder(BorderFactory.createEmptyBorder(0, 17, 2, 0));

        internalPanel.add(titleLabel);
        internalPanel.add(contentPreviewLabel);
        internalPanel.add(authorLabel);

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createMatteBorder(1, 3, 1, 2, Color.lightGray));
        this.setBackground(backgroundColor);
        this.setMaximumSize(new Dimension(getMaximumSize().width, 100));
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showPopupMenu(e.getX(), e.getY());
                }
                else {
                    onClick.performAction();
                }
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

        JMenuItem visualizzaItem = new JMenuItem("Visualizza");
        JMenuItem modificaItem = new JMenuItem();
        JMenuItem eliminaItem = new JMenuItem();

        if (isCurrentUserAuthor) {
            modificaItem.setText("Modifica");
            eliminaItem.setText("Elimina");
        }
        else {
            modificaItem.setText("Richiedi modifica");
            eliminaItem.setText("Proponi eliminazione"); // ???
        }

        visualizzaItem.addActionListener(e -> {
            onClick.performAction();
            popupMenu.setVisible(false);
        });

        modificaItem.addActionListener(e -> {
            onEdit.performAction();
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
        if (isUserLoggedIn)
            popupMenu.add(modificaItem);
        if (isCurrentUserAuthor)
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
