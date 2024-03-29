package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.FontSizeComboBox;
import wiki.Models.Page;
import wiki.Models.PageUpdate;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

/**
 * La classe PageView estende PageBase e implementa IUpdatable.
 * Rappresenta la pagina di visualizzazione di una pagina specifica.
 * Ogni PageView ha vari componenti dell'interfaccia utente, una pagina e il contenuto di una pagina.
 */
public class PageView extends PageBase implements IUpdatable {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel authorLabel;
    private JLabel dateLabel;
    private JLabel pageIdLabel;
    private JButton backBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton historyBtn;
    private JEditorPane editorPane;
    private JScrollPane pageContentScrollPane;
    private FontSizeComboBox fontSizeComboBox;

    private Page page;
    private String pageAllContent;


    /**
     * Costruisce una nuova PageView con i dettagli specificati.
     *
     * @param wikiController Il controller del wiki.
     * @param prevPageRef La pagina precedente.
     * @param page La pagina da visualizzare.
     */
    public PageView(WikiController wikiController, PageBase prevPageRef, Page page) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        fontSizeComboBox.init(editorPane);

        this.page = page;

        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            super.goBackToPrevPage();
        }

        backBtn.addActionListener(e -> onBackButtonPressed());
        editBtn.addActionListener(e -> onEditButtonPressed());
        historyBtn.addActionListener(e -> onPageHistoryButtonPressed());
        deleteBtn.addActionListener(e -> onDeleteButtonPressed());
        editorPane.addHyperlinkListener(this::onHyperlinkPressed);

        updateGUI();
    }

    /**
     * Aggiorna l'interfaccia utente.
     */
    @Override
    public void updateGUI() {
        // Fetch il contenuto della pagina di nuovo per ottenere i dati più recenti
        pageAllContent = wikiController.fetchAllPageContent(page.getId());

        if (pageAllContent == null) {
            JOptionPane.showMessageDialog(null, "Errore durante il fetch del contenuto della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            super.goBackToPrevPage();
        }

        initViewComponents();
    }

    // Metodi privati per gestire le azioni dell'utente e aggiornare l'interfaccia utente

    private void initViewComponents() {
        boolean isLoggedUserAuthor =
                wikiController.getLoggedUser() != null &&
                        page != null &&
                        wikiController.getLoggedUser().getUsername().equals(page.getAuthorName());

        boolean isThereAnyAcceptedUpdates =
                !wikiController.fetchPageUpdates(page.getId(), PageUpdate.STATUS_ACCEPTED).isEmpty();

        boolean didLoggedUserRequestUpdate =
                wikiController.getLoggedUser() != null &&
                        wikiController.fetchUpdateRequestCount(page.getId()) > 0;

        // Buttons Visibility
        historyBtn.setEnabled((isLoggedUserAuthor || didLoggedUserRequestUpdate) && isThereAnyAcceptedUpdates);
        editBtn.setVisible(wikiController.getLoggedUser() != null);
        deleteBtn.setVisible(isLoggedUserAuthor);

        editBtn.setText(isLoggedUserAuthor ? "Modifica": "Proponi modifica");

        // Page Text
        titleLabel.setText(page.getTitle());
        authorLabel.setText("<html>Autore: <b>" + page.getAuthorName() + "</b></html>");
        dateLabel.setText("Creato il: " + page.getCreationDateString());
        pageIdLabel.setText("ID Pagina: " + page.getId());

        setPageContentText();
    }

    private void onBackButtonPressed() {
        goBackToPrevPage();
    }

    private void onEditButtonPressed() {
        new PageEdit(wikiController, this, page, pageAllContent);
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
        String pageContent = pageAllContent.replace("\n", "<br>");

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

            new PageView(wikiController, this, wikiController.fetchPage(id));
        }
    }

    /**
     * Crea i componenti dell'interfaccia utente personalizzati.
     */
    private void createUIComponents() {
        fontSizeComboBox = new FontSizeComboBox();
    }
}
