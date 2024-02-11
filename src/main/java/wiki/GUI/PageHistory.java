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

    private void createUIComponents() {
        fontSizeComboBox = new FontSizeComboBox();
    }
}
