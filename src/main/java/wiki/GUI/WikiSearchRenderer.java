package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WikiSearchRenderer extends JPanel {

    public WikiSearchRenderer(String title, String contentPreview) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));

        JLabel contentPreviewLabel = new JLabel();
        contentPreviewLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 2, 0));
        contentPreviewLabel.setText(contentPreview);

        add(titleLabel);
        add(contentPreviewLabel);
    }
    private JLabel titleLabel;
    private JLabel introLabel;
    private JLabel authorLabel;
    private JLabel dateLabel;
    public WikiSearchRenderer(String title, String intro, String author, String date) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        contentPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);

        introLabel = new JLabel("<html><i>" + intro + "</i></html>");
        contentPanel.add(introLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);

        authorLabel = new JLabel("Autore: " + author + " - Data: " + date);
        contentPanel.add(authorLabel, gbc);

        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0, 15, 2, 0));
        add(contentPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Aggiungi qui il codice di disegno personalizzato
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
