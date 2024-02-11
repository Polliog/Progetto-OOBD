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
import java.util.Collections;

/**
 * La classe ReviewUpdates estende PageBase e rappresenta la pagina di revisione degli aggiornamenti.
 * Ogni ReviewUpdates ha un indice, un flag per indicare se ci sono aggiornamenti multipli, una lista di aggiornamenti di pagina in sospeso e vari componenti dell'interfaccia utente.
 */
public class ReviewUpdates extends PageBase {
    private JPanel mainPanel; // Il pannello principale della pagina di revisione degli aggiornamenti
    private JButton backBtn; // Il pulsante per tornare indietro
    private JButton prevBtn; // Il pulsante per andare all'aggiornamento precedente
    private JButton nextBtn; // Il pulsante per andare all'aggiornamento successivo
    private JButton acceptAllBtn; // Il pulsante per accettare tutti gli aggiornamenti
    private JLabel infoLabel; // L'etichetta per le informazioni sulla pagina
    private JLabel statusLabel; // L'etichetta per lo stato degli aggiornamenti
    private JLabel paginationLabel; // L'etichetta per la paginazione degli aggiornamenti
    private JScrollPane updatesViewScrollPane; // Il pannello di scorrimento per la visualizzazione degli aggiornamenti
    private FontSizeComboBox fontSizeComboBox; // La combo box per la selezione della dimensione del font
    private int updateIndex = 0; // L'indice corrente nella lista di aggiornamenti di pagina
    private boolean multipleUpdates; // Flag per indicare se ci sono aggiornamenti multipli
    private ArrayList<PageUpdate> pendingPageUpdates; // La lista di aggiornamenti di pagina in sospeso

    /**
     * Costruisce una nuova ReviewUpdates con i dettagli specificati.
     *
     * @param wikiController Il controller del wiki.
     * @param prevPage La pagina precedente.
     * @param page La pagina di cui revisionare gli aggiornamenti.
     */
    public ReviewUpdates(WikiController wikiController, PageBase prevPage, Page page) {
        super(wikiController, prevPage);
        add(mainPanel);

        // Prende le modifiche in sospeso per la pagina
        pendingPageUpdates = wikiController.fetchPageUpdates(page.getId(), PageUpdate.STATUS_PENDING);

        // Se non ci sono modifiche in sospeso, torna alla pagina precedente
        if (pendingPageUpdates == null)
            super.goBackToPrevPage();

        // Ordina le modifiche in modo che la più recente sia la prima
        Collections.reverse(pendingPageUpdates);

        multipleUpdates = pendingPageUpdates.size() > 1;

        infoLabel.setText("<html>Titolo pagina: <b>" + page.getTitle() + "</b>  -  PageID: <b>" + page.getId() + "</b></html>");

        if (multipleUpdates) {
            statusLabel.setText("<html>Ci sono <b>" + pendingPageUpdates.size() + "</b> modifiche in sospeso per questa pagina.</html>");
            initPaginationButtons();
            updatePaginationLabel();

            // Mostra un popup
            JOptionPane.showMessageDialog(null, "Ci sono " + pendingPageUpdates.size() + " modifiche in sospeso per questa pagina. Verranno mostrate tutte di seguito.", "Modifiche multiple", JOptionPane.INFORMATION_MESSAGE);
        }

        backBtn.addActionListener(e -> onBackButtonPressed());

        createComparatorPanel(pendingPageUpdates.get(updateIndex));
    }


    // Metodi privati per gestire le azioni dell'utente e aggiornare l'interfaccia utente
    private void initPaginationButtons() {
        prevBtn.addActionListener(e -> onPrevButtonPressed());
        nextBtn.addActionListener(e -> onNextButtonPressed());
        acceptAllBtn.addActionListener(e -> onAcceptAllButtonPressed());
        prevBtn.setVisible(true);
        nextBtn.setVisible(true);
        acceptAllBtn.setVisible(true);
    }

    private void updatePaginationLabel() {
        prevBtn.setEnabled(updateIndex != 0);
        nextBtn.setEnabled(updateIndex != pendingPageUpdates.size() - 1);

        paginationLabel.setText("Modifica " + (updateIndex + 1) + " di " + pendingPageUpdates.size());
    }

    public void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void onAcceptAllButtonPressed() {
        if (wikiController.acceptAllPageUpdates(pendingPageUpdates))
            super.goBackToPrevPage();
    }

    private void onNextButtonPressed() {
        updateIndex++;
        updatePaginationLabel();
        createComparatorPanel(pendingPageUpdates.get(updateIndex));
    }

    private void onPrevButtonPressed() {
        updateIndex--;
        updatePaginationLabel();
        createComparatorPanel(pendingPageUpdates.get(updateIndex));
    }

    private void onAcceptUpdate(PageUpdate pageUpdate) {
        if (wikiController.acceptPageUpdate(pageUpdate)) {
            if (!multipleUpdates || updateIndex == pendingPageUpdates.size() - 1)
                super.goBackToPrevPage();
            else
                onNextButtonPressed();
        }
    }

    private void onRejectUpdate(PageUpdate pageUpdate) {
        if (wikiController.rejectPageUpdate(pageUpdate)) {
            if (!multipleUpdates || updateIndex == pendingPageUpdates.size() - 1)
                super.goBackToPrevPage();
            else
                onNextButtonPressed();
        }
    }

    private void createComparatorPanel(PageUpdate pageUpdate) {
        // Creazione del pannello principale
        String currentText = wikiController.fetchAllPageContent(pageUpdate.getPage().getId()).replace("\n", "<br>");
        ;
        String newText = ContentStringsUtils.getUpdateComparedContentHtml(wikiController.fetchPageUpdateContentStrings(pageUpdate.getId()));

        // Creazione dei pulsanti
        JButton acceptButton = new JButton("Accetta");
        acceptButton.addActionListener(e -> onAcceptUpdate(pageUpdate));
        JButton rejectButton = new JButton("Rifiuta");
        rejectButton.addActionListener(e -> onRejectUpdate(pageUpdate));

        // Panel per i bottoni
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout());
        buttonsPanel.add(acceptButton);
        buttonsPanel.add(rejectButton);

        // Creazione del pannello di comparazione
        var panel = new UpdateTextComparatorPanel(newText, currentText);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        // ComboBox per la dimensione del testo
        ArrayList<JTextComponent> textPanes = new ArrayList<>();
        textPanes.add(panel.getFirstTextPane());
        textPanes.add(panel.getSecondTextPane());

        fontSizeComboBox.init(textPanes);

        updatesViewScrollPane.setViewportView(panel);
    }


    /**
     * Crea i componenti dell'interfaccia utente personalizzati.
     */
    private void createUIComponents() {
        fontSizeComboBox = new FontSizeComboBox();
    }
}
