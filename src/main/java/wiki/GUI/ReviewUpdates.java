package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.UpdateContentString;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ReviewUpdates extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JScrollPane updatesView;
    private JButton indietroButton;
    private JButton avantiButton;
    private JButton accettaTuttoButton;
    private JLabel paginationLabel;

    private int updateIndex = 0;

    private boolean multipleUpdates = false;
    ArrayList<Update> updates;

    // GUI that rewies the pending updates of a page
    public ReviewUpdates(WikiController wikiController, PageBase prevPage, Page page) {
        super(wikiController, prevPage);
        add(mainPanel);

        updates = wikiController.fetchPendingPageUpdates(page.getId());
        if (updates == null) {
            // error message
            super.goBackToPrevPage();
        }

        Collections.reverse(updates);

        multipleUpdates = updates.size() > 1;

        backBtn.addActionListener(e -> onBackButtonPressed());

        // ? cambiare testo?
        titleLabel.setText("Revisione della richiesta di modifica dell'utente " + updates.getFirst().getAuthor() + " alla pagina \"" + page.getTitle() + "\"");


        if (multipleUpdates) {
            statusLabel.setText("Ci sono " + updates.size() + " modifiche in sospeso per questa pagina.");

            initPagination();

            indietroButton.addActionListener(e -> previousUpdate());
            avantiButton.addActionListener(e -> nextUpdate());
            indietroButton.setVisible(true);
            avantiButton.setVisible(true);
            accettaTuttoButton.setVisible(true);
            accettaTuttoButton.addActionListener(e -> onAcceptAllUpdates());
        }

        if (multipleUpdates) {
            //mostra un popup che dice che visto che ci sono più modifiche, verranno mostrate tutte di seguito
            JOptionPane.showMessageDialog(null, "Ci sono " + updates.size() + " modifiche in sospeso per questa pagina. Verranno mostrate tutte di seguito.", "Modifiche multiple", JOptionPane.INFORMATION_MESSAGE);
        }

        // ???
        createUIComponents(updates.get(updateIndex));
    }

    public void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void initPagination() {
        indietroButton.setEnabled(updateIndex != 0);
        avantiButton.setEnabled(updateIndex != updates.size() - 1);

        paginationLabel.setText("Modifica " + (updateIndex + 1) + " di " + updates.size());
    }

    private void nextUpdate() {
        updateIndex++;
        initPagination();
        createUIComponents(updates.get(updateIndex));
    }

    private void previousUpdate() {
        updateIndex--;
        initPagination();
        createUIComponents(updates.get(updateIndex));
    }

    private void createUIComponents(Update update) {
        // Creazione del pannello principale
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Creazione del pannello per il testo
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1, 2));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiunta del bordo

        // Creazione delle etichette
        JLabel currentTextLabel = new JLabel("Testo Attuale");
        JLabel newTextLabel = new JLabel("Testo Nuovo");

        // Creazione delle aree di testo
        JTextPane currentText = new JTextPane();
        currentText.setContentType("text/html"); // Impostazione del tipo di contenuto su HTML
        currentText.setText(update.getPage().getAllContentHtml()); // Utilizzo del contenuto della pagina come testo attuale
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
                line = update.getPage().getLine(contentString.getOrderNum());
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

        // Creazione del pannello per i pulsanti
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Creazione dei pulsanti
        JButton acceptButton = new JButton("Accetta");

        acceptButton.addActionListener(e -> onAcceptUpdate(update));

        JButton rejectButton = new JButton("Rifiuta");

        rejectButton.addActionListener(e -> onRejectUpdate(update));

        // Aggiunta dei pulsanti al pannello dei pulsanti
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        // Aggiunta dei pannelli al pannello principale
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        updatesView.setViewportView(mainPanel);
    }

    private void onAcceptAllUpdates() {
        if (wikiController.acceptAllPageUpdates(updates))
            super.goBackToPrevPage();
        // aggiornamento locale degli updates?
    }

    private void onAcceptUpdate(Update update) {
        if (wikiController.acceptPageUpdate(update)) {
            if (!multipleUpdates || updateIndex == updates.size() - 1)
                super.goBackToPrevPage();
            else
                nextUpdate();
        }
    }

    private void onRejectUpdate(Update update) {
        if (wikiController.rejectPageUpdate(update)) {
            // local update??
            // serve davvero?
            if (!multipleUpdates || updateIndex == updates.size() - 1)
                super.goBackToPrevPage();
            else
                nextUpdate();
        }
    }
}
