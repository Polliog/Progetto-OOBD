package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;

public class PageCreate extends JPanel {
    private JLabel creationLabel;
    private JPanel pageCreatePanel;
    private JTextField pageTitle;
    private JTextArea pageContent;
    private JButton createPageButton;

    public PageCreate() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        add(pageCreatePanel); // Aggiungi un componente al pannello

        createPageButton.addActionListener(e -> createPage());

        //label font size
        creationLabel.setFont(creationLabel.getFont().deriveFont(20.0f));

        setVisible(true);
    }


    //given a text from a textarea for every linebreak add a new element to an arraylist
    public static ArrayList<String> parseText(String text) {
        ArrayList<String> lines = new ArrayList<>();
        String[] splitted = text.split("\n");

        //delete empty lines
        for (String line : splitted) {
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }

        return lines;
    }


    private void createPage() {
        String title = pageTitle.getText();
        String content = pageContent.getText();
        ArrayList<String> lines = parseText(content);


        if (!WikiController.isUserLogged()) {
            JOptionPane.showMessageDialog(null, "Devi essere loggato per creare una pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }


        WikiController.createPage(title, lines);
    }
}
