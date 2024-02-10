package wiki.GUI;

import javax.swing.*;
import java.awt.*;

public class UpdateTextComparatorPanel extends JPanel {

    private final JPanel internalPanel;


    public UpdateTextComparatorPanel(String firstTextHtml, String secondTextHtml) {
        internalPanel = new JPanel();
        internalPanel.setLayout(new GridLayout(1, 2));
        internalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel currentTextLabel = new JLabel("Testo Nuovo");
        //currentTextLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel newTextLabel = new JLabel("Testo Precedente");

        JTextPane firstTextPane = new JTextPane();
        firstTextPane.setContentType("text/html");
        firstTextPane.setEditable(false);
        firstTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        firstTextPane.setText(firstTextHtml);

        JTextPane secondTextPane = new JTextPane();
        secondTextPane.setContentType("text/html");
        secondTextPane.setEditable(false);
        secondTextPane.setFont(new Font("Arial", Font.PLAIN, 14));
        secondTextPane.setText(secondTextHtml);

        JPanel currentTextPanel = new JPanel(new BorderLayout());
        currentTextPanel.add(currentTextLabel, BorderLayout.NORTH);
        currentTextPanel.add(firstTextPane, BorderLayout.CENTER);
        currentTextPanel.setBackground(Color.red);

        JPanel newTextPanel = new JPanel(new BorderLayout());
        newTextPanel.add(newTextLabel, BorderLayout.NORTH);
        newTextPanel.add(secondTextPane, BorderLayout.CENTER);
        newTextPanel.setBackground(Color.blue);


        internalPanel.add(currentTextPanel);
        internalPanel.add(newTextPanel);


        setLayout(new BorderLayout());
        add(internalPanel);
    }
}

