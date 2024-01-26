package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;

public class PageView extends PageBase {
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

        fontSizeComboBox1.init(editorPane);

        backBtn.addActionListener(e -> onBackPressed());
        editBtn.addActionListener(e -> onEditPressed());
        historyBtn.addActionListener(e -> onHistoryPressed());
        deleteBtn.addActionListener(e -> onDeletePressed());

        // Fetch the page data
        fetchData(id);

        boolean isLoggedUserAuthor =
                wikiController.getLoggedUser() != null &&
                page != null &&
                wikiController.getLoggedUser().getUsername().equals(page.getAuthorName());

        editBtn.setText(isLoggedUserAuthor ? "Modifica": "Proponi modifica");

        // Visibility
        historyBtn.setEnabled(!page.getUpdates().isEmpty());
        editBtn.setVisible(wikiController.getLoggedUser() != null);
        deleteBtn.setVisible(isLoggedUserAuthor);
    }


    private void fetchData(int id) {
        page = wikiController.fetchPage(id);
        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);

            prevPageRef.setVisible(true);
            this.dispose();
            return;
        }

        titleLabel.setText(page.getTitle());
        authorLabel.setText("<html>Autore: <b>" + page.getAuthorName() + "</b></html>");
        dateLabel.setText("Creato il: " + page.getDateString());
        pageIdLabel.setText("ID Pagina: " + page.getId());
        setText();
    }

    private void onBackPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void onEditPressed() {
        new PageEdit(wikiController, this, page.getId()).setVisible(true);
        this.dispose();
    }

    private void onHistoryPressed() {
        new PageHistory(wikiController, this, page);
        this.setVisible(false);
    }

    private void onDeletePressed() {
        if (wikiController.deletePage(page)) {
            prevPageRef.setVisible(true);
            this.dispose();
        }
    }

    private void onHyperlinkPressed(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            openHyperlink(e.getDescription());
    }


    private void setText() {
        // Replace the newline with the HTML correspondent
        String pageContent = page.getAllContent().replace("\n", "<br>");

        // Imposta il testo formattato nella JEditorPane
        editorPane.setText(pageContent);

        // Aggiungi un gestore di eventi per gestire i clic sui link
        editorPane.addHyperlinkListener(e -> onHyperlinkPressed(e));

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
            this.setVisible(false);
        }
    }

    private void createUIComponents() {
        fontSizeComboBox1 = new FontSizeComboBox();
    }

    @Override
    protected void frameStart() {

    }
}
