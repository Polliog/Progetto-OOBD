package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PageUpdate;
import wiki.Models.Utils.ContentStringsUtils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class ReviewUpdates extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton acceptAllBtn;
    private JLabel infoLabel;
    private JLabel statusLabel;
    private JLabel paginationLabel;
    private JScrollPane updatesViewScrollPane;
    private FontSizeComboBox fontSizeComboBox;

    private int updateIndex = 0;
    private boolean multipleUpdates;
    private ArrayList<PageUpdate> pendingPageUpdates;


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
        String currentText = wikiController.fetchAllPageContent(pageUpdate.getPage().getId()).replace("\n", "<br>");;
        String newText = ContentStringsUtils.getPageUpdateComparedContentHtml(wikiController.fetchPageUpdateContentStrings(pageUpdate.getId()));

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
    private void createUIComponents() {
        fontSizeComboBox = new FontSizeComboBox();
    }
}
