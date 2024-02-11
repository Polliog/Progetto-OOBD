package wiki.GUI;

import javax.swing.*;
import java.awt.*;

public class UpdateTextComparatorPanel extends JPanel {

    private final JPanel internalPanel;
    private JTextPane firstTextPane;
    private JTextPane secondTextPane;

    public UpdateTextComparatorPanel(String firstTextHtml, String secondTextHtml) {
        internalPanel = new JPanel();
        internalPanel.setLayout(new GridLayout(1, 2));
        internalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel currentTextLabel = new JLabel("Testo Nuovo");
        currentTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentTextLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel newTextLabel = new JLabel("Testo Precedente");
        newTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newTextLabel.setFont(new Font("Arial", Font.BOLD, 16));

        firstTextPane = new JTextPane();
        firstTextPane.setContentType("text/html");
        firstTextPane.setEditable(false);
        firstTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        firstTextPane.setText(firstTextHtml);

        secondTextPane = new JTextPane();
        secondTextPane.setContentType("text/html");
        secondTextPane.setEditable(false);
        secondTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        secondTextPane.setText(secondTextHtml);

        JPanel currentTextPanel = new JPanel(new BorderLayout());
        currentTextPanel.add(currentTextLabel, BorderLayout.NORTH);
        currentTextPanel.add(firstTextPane, BorderLayout.CENTER);
        currentTextPanel.setBackground(new Color(203, 203, 203));
        currentTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 1));

        JPanel newTextPanel = new JPanel(new BorderLayout());
        newTextPanel.add(newTextLabel, BorderLayout.NORTH);
        newTextPanel.add(secondTextPane, BorderLayout.CENTER);
        newTextPanel.setBackground(new Color(203, 203, 203));
        newTextPanel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));


        internalPanel.add(currentTextPanel);
        internalPanel.add(newTextPanel);

        setLayout(new BorderLayout());
        add(internalPanel);
    }

    public JTextPane getFirstTextPane() {
        return firstTextPane;
    }

    public JTextPane getSecondTextPane() {
        return secondTextPane;
    }
}

