package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class PageView extends PageBase implements IUpdatable {
    private final int id;

    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel dateLabel;
    private JButton backBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton historyBtn;
    private JLabel pageIdLabel;
    private JEditorPane editorPane;
    private JScrollPane pageContentScrollPane;
    private FontSizeComboBox fontSizeComboBox1;

    private Page page = null;


    public PageView(WikiController wikiController, PageBase prevPageRef, int id) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        this.id = id;

        fontSizeComboBox1.init(editorPane);

        backBtn.addActionListener(e -> onBackButtonPressed());
        editBtn.addActionListener(e -> onEditButtonPressed());
        historyBtn.addActionListener(e -> onPageHistoryButtonPressed());
        deleteBtn.addActionListener(e -> onDeleteButtonPressed());

        editorPane.addHyperlinkListener(this::onHyperlinkPressed);

        updateGUI();
    }

    @Override
    public void updateGUI() {
        fetchData(id);
    }

    private void fetchData(int id) {
        page = wikiController.fetchPage(id);
        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);

            System.out.println("dio cane");

            super.goBackToPrevPage();
        }
        else {
            boolean isLoggedUserAuthor =
                    wikiController.getLoggedUser() != null &&
                            page != null &&
                            wikiController.getLoggedUser().getUsername().equals(page.getAuthorName());

            // Buttons Visibility
            historyBtn.setEnabled(!page.getUpdates().isEmpty());
            editBtn.setVisible(wikiController.getLoggedUser() != null);
            deleteBtn.setVisible(isLoggedUserAuthor);

            editBtn.setText(isLoggedUserAuthor ? "Modifica": "Proponi modifica");

            // Page Text
            titleLabel.setText(page.getTitle());
            authorLabel.setText("<html>Autore: <b>" + page.getAuthorName() + "</b></html>");
            dateLabel.setText("Creato il: " + page.getDateString());
            pageIdLabel.setText("ID Pagina: " + page.getId());

            setPageContentText();
        }
    }

    private void onBackButtonPressed() {
        goBackToPrevPage();
    }

    private void onEditButtonPressed() {
        new PageEdit(wikiController, this, page);
    }

    private void onPageHistoryButtonPressed() {
        new PageHistory(wikiController, this, page);
    }

    private void onDeleteButtonPressed() {
        if (wikiController.deletePage(page)) {
            goBackToPrevPage();
        }
    }

    private void onHyperlinkPressed(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            openHyperlink(e.getDescription());
    }

    private void setPageContentText() {
        // Replace the newline with the HTML correspondent
        String pageContent = page.getAllContent().replace("\n", "<br>");

        // Imposta il testo formattato nella JEditorPane
        editorPane.setText(pageContent);

        pageContentScrollPane.setViewportView(editorPane);
    }

    private void openHyperlink(String pageID) {
        Object[] options = {"Visualizza", "Chiudi"};
        int n = JOptionPane.showOptionDialog(null,
                "Sei sicuro di voler aprire questo link?",        // the dialog message
                "Hyperlink",    // the title of the dialog window
                JOptionPane.YES_NO_OPTION,      // option type
                JOptionPane.QUESTION_MESSAGE,   // message type
                null,       // optional icon, use null to use the default icon
                options,    // options string array, will be made into buttons
                options[0]  // option that should be made into a default button
        );

        if (n == JOptionPane.YES_OPTION) {
            int id = -1;

            try {
                id = Integer.parseInt(pageID);
            }
            catch (NumberFormatException ignored) {}

            new PageView(wikiController, this, id);
        }
    }

    private void createUIComponents() {
        fontSizeComboBox1 = new FontSizeComboBox();
    }
}
