package wiki.GUI;

import org.intellij.lang.annotations.Flow;
import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PageUpdate;
import wiki.Models.UpdateContentString;
import wiki.Models.Utils.ContentStringsUtils;

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
    ArrayList<PageUpdate> pendingPageUpdates;

    // GUI that rewies the pending pageUpdates of a page
    public ReviewUpdates(WikiController wikiController, PageBase prevPage, Page page) {
        super(wikiController, prevPage);
        add(mainPanel);

        pendingPageUpdates = wikiController.fetchPageUpdates(page.getId(), PageUpdate.STATUS_PENDING);

        if (pendingPageUpdates == null) {
            // error message
            super.goBackToPrevPage();
        }

        Collections.reverse(pendingPageUpdates);

        multipleUpdates = pendingPageUpdates.size() > 1;

        backBtn.addActionListener(e -> onBackButtonPressed());

        // ? cambiare testo?
        // tetris titolo
        titleLabel.setText("Revisione della richiesta di modifica dell'utente " + pendingPageUpdates.get(0).getAuthor() + " alla pagina " + page.getTitle());


        if (multipleUpdates) {
            statusLabel.setText("Ci sono " + pendingPageUpdates.size() + " modifiche in sospeso per questa pagina.");

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
            JOptionPane.showMessageDialog(null, "Ci sono " + pendingPageUpdates.size() + " modifiche in sospeso per questa pagina. Verranno mostrate tutte di seguito.", "Modifiche multiple", JOptionPane.INFORMATION_MESSAGE);
        }

        // ???
        createUIComponents(pendingPageUpdates.get(updateIndex));
    }

    public void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    private void initPagination() {
        indietroButton.setEnabled(updateIndex != 0);
        avantiButton.setEnabled(updateIndex != pendingPageUpdates.size() - 1);

        paginationLabel.setText("Modifica " + (updateIndex + 1) + " di " + pendingPageUpdates.size());
    }

    private void nextUpdate() {
        updateIndex++;
        initPagination();
        createUIComponents(pendingPageUpdates.get(updateIndex));
    }

    private void previousUpdate() {
        updateIndex--;
        initPagination();
        createUIComponents(pendingPageUpdates.get(updateIndex));
    }

    private void createUIComponents(PageUpdate pageUpdate) {
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

        updatesView.setViewportView(panel);
    }

    private void onAcceptAllUpdates() {
        if (wikiController.acceptAllPageUpdates(pendingPageUpdates))
            super.goBackToPrevPage();
        // aggiornamento locale degli pageUpdates?
    }

    private void onAcceptUpdate(PageUpdate pageUpdate) {
        if (wikiController.acceptPageUpdate(pageUpdate)) {
            if (!multipleUpdates || updateIndex == pendingPageUpdates.size() - 1)
                super.goBackToPrevPage();
            else
                nextUpdate();
        }
    }

    private void onRejectUpdate(PageUpdate pageUpdate) {
        if (wikiController.rejectPageUpdate(pageUpdate)) {
            if (!multipleUpdates || updateIndex == pendingPageUpdates.size() - 1)
                super.goBackToPrevPage();
            else
                nextUpdate();
        }
    }
}
