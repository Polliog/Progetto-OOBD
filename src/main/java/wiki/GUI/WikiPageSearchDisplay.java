package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WikiPageSearchDisplay extends JPanel {
    private final JPanel internalPanel;

    @FunctionalInterface
    public interface Action {
        void performAction();
    }

    public WikiPageSearchDisplay(String title, String contentPreview, String author, String date, Action action) {
        internalPanel = new JPanel();
        internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
        internalPanel.setBackground(Color.white);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));

        JLabel contentPreviewLabel = new JLabel(contentPreview);
        contentPreviewLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 2, 0));

        JLabel authorLabel = new JLabel("Autore: " + author + " - Data: " + date);
        authorLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 2, 0));

        internalPanel.add(titleLabel);
        internalPanel.add(contentPreviewLabel);
        internalPanel.add(authorLabel);

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createMatteBorder(1, 3, 1, 2, Color.lightGray));
        this.setBackground(Color.white);
        this.setMaximumSize(new Dimension(getMaximumSize().width, 90));
        this.add(internalPanel);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Azioni da eseguire quando il pannello viene cliccato
                action.performAction();
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
    }

    private void setVisualFeedback() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        internalPanel.setBackground(Color.lightGray);
        this.setBackground(Color.lightGray);
    }

    private void revertVisualFeedback() {
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        internalPanel.setBackground(Color.white);
        this.setBackground(Color.white);
    }
}
