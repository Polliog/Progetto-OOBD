package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.FontSizeComboBox;
import wiki.GUI.Custom.UpdateTextComparatorPanel;
import wiki.Models.Page;
import wiki.Models.PageUpdate;
import wiki.Models.Utils.ContentStringsUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

/**
 * La classe PageHistory estende PageBase e rappresenta la pagina di visualizzazione della storia di una pagina.
 * Ogni PageHistory ha un indice, una lista di aggiornamenti di pagina accettati e vari componenti dell'interfaccia utente.
 */
public class PageHistory extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JButton nextBtn;
    private JButton previousBtn;

    private JLabel infoLabel;
    private JLabel paginationLabel;
    private JScrollPane historyScrollPane;
    private FontSizeComboBox fontSizeComboBox;

    private int index = 0;
    private ArrayList<PageUpdate> acceptedPageUpdates;

    /**
     * Costruisce una nuova PageHistory con i dettagli specificati.
     *
     * @param wikiController Il controller del wiki.
     * @param prevPageRef La pagina precedente.
     * @param page La pagina di cui visualizzare la storia.
     */
    public PageHistory(WikiController wikiController, PageBase prevPageRef, Page page) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        setMinimumSize(new Dimension(700, 700));

        acceptedPageUpdates = wikiController.fetchPageUpdates(page.getId(), PageUpdate.STATUS_ACCEPTED);


        updatePaginationLabel();
        updateInfoLabel();
        createTextComparatorPanel();

        backBtn.addActionListener(e -> onBackButtonPressed());
        nextBtn.addActionListener(e -> onNextPressed());
        previousBtn.addActionListener(e -> onPreviousPressed());

        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);

            super.goBackToPrevPage();
        }

        if (acceptedPageUpdates.size() <= 1) {
            previousBtn.setEnabled(false);
            nextBtn.setEnabled(false);
        }
    }

    private void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    // Metodi privati per gestire le azioni dell'utente e aggiornare l'interfaccia utente
    private void onNextPressed() {
        if (index < acceptedPageUpdates.size() - 1) {
            index++;
            updatePaginationLabel();
            updateInfoLabel();
            createTextComparatorPanel();
        }
    }

    private void onPreviousPressed() {
        if (index > 0) {
            index--;
            updatePaginationLabel();
            updateInfoLabel();
            createTextComparatorPanel();
        }
    }


    private void updatePaginationLabel() {
        paginationLabel.setText("Pagina " + (index + 1) + " di " + acceptedPageUpdates.size());
    }

    private void updateInfoLabel() {
        infoLabel.setText("<html>Modifica proposta da: <b>" + acceptedPageUpdates.get(index).getAuthorName() + "</b>  -  " + acceptedPageUpdates.get(index).getCreationDateString() + "</html>");
    }

    private void createTextComparatorPanel() {
        PageUpdate pageUpdate = acceptedPageUpdates.get(index);

        String currentText = pageUpdate.getOldTextFormatted();
        String newText = ContentStringsUtils.getUpdateComparedContentHtml(wikiController.fetchPageUpdateContentStrings(pageUpdate.getId()));

        var textCompPanel = new UpdateTextComparatorPanel(newText, currentText);
        ArrayList<JTextComponent> textComponents = new ArrayList<>();
        textComponents.add(textCompPanel.getFirstTextPane());
        textComponents.add(textCompPanel.getSecondTextPane());

        // Inizializza la combo box per la selezione della grandezza del font
        fontSizeComboBox.init(textComponents);

        historyScrollPane.setViewportView(textCompPanel);
    }


    /**
     * Crea i componenti dell'interfaccia utente personalizzati.
     */
    private void createUIComponents() {
        fontSizeComboBox = new FontSizeComboBox();
    }
}
