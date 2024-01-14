package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.UpdateContentString;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PageHistory extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JLabel infoLabel;
    private JLabel paginationLabel;
    private JScrollPane historyView;
    private JButton nextBtn;
    private JButton previousBtn;

    private int index = 0;
    private Page page = null;

    public PageHistory(WikiController wikiController, PageBase prevPageRef, Page page) {
        super(wikiController, prevPageRef);
        initGUI(true,new Dimension(550, 400));
        add(mainPanel);

        this.page = page;


        updatePaginationLabel();
        updateInfoLabel();
        createUIComponents();

        backBtn.addActionListener(e -> onBackPressed());
        nextBtn.addActionListener(e -> onNextPressed());
        previousBtn.addActionListener(e -> onPreviousPressed());

        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);

            prevPageRef.setVisible(true);
            this.dispose();
            return;
        }

        if (page.getUpdates().size() <= 1) {
            previousBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        }
    }

    private void onBackPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void onNextPressed() {
        if (index < page.getUpdates().size() - 1) {
            index++;
            updatePaginationLabel();
            updateInfoLabel();
            createUIComponents();
        }
    }

    private void onPreviousPressed() {
        if (index > 0) {
            index--;
            updatePaginationLabel();
            updateInfoLabel();
            createUIComponents();
        }
    }


    private void updatePaginationLabel() {
        paginationLabel.setText("Pagina " + (index + 1) + " di " + page.getUpdates().size());
    }

    private void updateInfoLabel() {
        infoLabel.setText("Autore: " + page.getUpdates().get(index).getAuthor() + " - " + page.getUpdates().get(index).getCreation().toString().substring(0, 10));
    }

    private void createUIComponents() {
        Update update = page.getUpdates().get(index);
        // Creazione del pannello principale
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Creazione del pannello per il testo
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1, 2));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiunta del bordo

        // Creazione delle etichette
        JLabel currentTextLabel = new JLabel("Testo Precedente");
        JLabel newTextLabel = new JLabel("Testo Nuovo");

        // Creazione delle aree di testo
        JTextPane currentText = new JTextPane();
        currentText.setContentType("text/html"); // Impostazione del tipo di contenuto su HTML
        currentText.setText(update.getOldTextFormatted()); // Utilizzo del contenuto della pagina come testo attuale
        currentText.setEditable(false);
        currentText.setFont(new Font("Arial", Font.PLAIN, 14)); // Impostazione del font

        JTextPane newText = new JTextPane();
        newText.setContentType("text/html"); // Impostazione del tipo di contenuto su HTML
        newText.setEditable(false);
        newText.setFont(new Font("Arial", Font.PLAIN, 14)); // Impostazione del font

        // Creazione dei pannelli per le etichette e le aree di testo
        JPanel currentTextPanel = new JPanel(new BorderLayout());
        currentTextPanel.add(currentTextLabel, BorderLayout.NORTH);
        currentTextPanel.add(currentText, BorderLayout.CENTER);

        JPanel newTextPanel = new JPanel(new BorderLayout());
        newTextPanel.add(newTextLabel, BorderLayout.NORTH);
        newTextPanel.add(newText, BorderLayout.CENTER);

        // Confronto del vecchio e del nuovo testo
        ArrayList<UpdateContentString> contentStrings = update.getContentStrings();
        StringBuilder allContent = new StringBuilder();
        for (UpdateContentString contentString : contentStrings) {
            String line = contentString.getText();

            if (contentString.getType() == 0 || contentString.getType() == 3) {
                try {
                    line = update.getOldTextLine(contentString.getOrder_num());
                } catch (Exception e) {
                    line = "";
                }
            }

            allContent.append(line).append("<br>");

            switch (contentString.getType()) {
                case 0: // Il testo è uguale, non colorare
                    break;
                case 2:  // Il testo è nuovo, quindi colora la riga di verde
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='#CCCC00'>");
                    allContent.append("</font>");
                    break;
                case 1:// Il testo è diverso, quindi colora la riga di giallo scuro
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='green'>");
                    allContent.append("</font>");
                    break;
                case 3: // Il testo è stato rimosso, quindi colora la riga di rosso
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='red'>");
                    allContent.append("</font>");
                    break;
            }
        }

        newText.setText(allContent.toString());

        // Aggiunta dei pannelli al pannello del testo
        textPanel.add(currentTextPanel);
        textPanel.add(newTextPanel);

        // Aggiunta dei pannelli al pannello principale
        mainPanel.add(textPanel, BorderLayout.CENTER);

        historyView.setViewportView(mainPanel);
    }
}
