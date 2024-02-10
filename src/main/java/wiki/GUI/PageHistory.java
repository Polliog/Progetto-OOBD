package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PageUpdate;
import wiki.Models.UpdateContentString;
import wiki.Models.Utils.ContentStringsUtils;

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
    private final Page page;

    ArrayList<PageUpdate> acceptedPageUpdates;

    public PageHistory(WikiController wikiController, PageBase prevPageRef, Page page) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        setMinimumSize(new Dimension(700, 700));

        this.page = page;
        acceptedPageUpdates = wikiController.fetchPageUpdates(page.getId(), PageUpdate.STATUS_ACCEPTED);


        updatePaginationLabel();
        updateInfoLabel();
        createUIComponents();

        backBtn.addActionListener(e -> onBackPressed());
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

    private void onBackPressed() {
        prevPage.setVisible(true);
        this.dispose();
    }

    private void onNextPressed() {
        if (index < acceptedPageUpdates.size() - 1) {
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
        paginationLabel.setText("Pagina " + (index + 1) + " di " + acceptedPageUpdates.size());
    }

    private void updateInfoLabel() {
        infoLabel.setText("<html>Modifica proposta da: <b>" + acceptedPageUpdates.get(index).getAuthor() + "</b>  -  " + acceptedPageUpdates.get(index).getCreationDateString() + "</html>");
    }

    private void createUIComponents() {
        PageUpdate pageUpdate = acceptedPageUpdates.get(index);

        String currentText = pageUpdate.getOldTextFormatted();
        String newText = ContentStringsUtils.getPageUpdateComparedContentHtml(wikiController.fetchPageUpdateContentStrings(pageUpdate.getId()));

        var textCompPanel = new UpdateTextComparatorPanel(newText, currentText);
        historyView.setViewportView(textCompPanel);
    }
}
