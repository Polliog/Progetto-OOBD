package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.ContentShortcutsPanel;
import wiki.GUI.Custom.FontSizeComboBox;
import wiki.Models.Page;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/** Questa classe rappresenta la pagina di modifica di una pagina */
public class PageEdit extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JButton saveBtn;
    private JLabel titleLabel;
    private JTextArea pageContentArea;
    private FontSizeComboBox fontSizeComboBox1;
    private ContentShortcutsPanel contentShortcutsPanel1;

    private Page page;
    private String pageContent;

    public PageEdit(WikiController wikiController, PageBase prevPageRef, Page page, String pageContent) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        setMinimumSize(new Dimension(700, 700));

        this.page = page;
        this.pageContent = pageContent;

        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);

            super.goBackToPrevPage();
        }

        fontSizeComboBox1.init(pageContentArea);
        contentShortcutsPanel1.init(pageContentArea);

        backBtn.addActionListener(e -> onBackButtonPressed());
        saveBtn.addActionListener(e -> onSaveButtonPressed());

        fetchData();
    }



    /* fetch dei dati della pagina */
    private void fetchData() {
        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleLabel.setText(page.getTitle());

        pageContentArea.setText(pageContent);

        saveBtn.setText(Objects.equals(page.getAuthorName(), wikiController.getLoggedUser().getUsername()) ? "Salva Modifiche" : "Richiedi Modifiche");
    }

    /* Metodo che viene chiamato quando viene premuto il pulsante di ritorno */
    private void onBackButtonPressed() {
        super.goBackToPrevPage();
    }

    /* Metodo che viene chiamato quando viene premuto il pulsante di salvataggio */
    private void onSaveButtonPressed() {
        // Confirmation dialog
        int dialogResult = JOptionPane.showConfirmDialog (null, "Vuoi salvare le modifiche?","Conferma di salvataggio", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION){
            wikiController.savePageUpdate(page, pageContentArea.getText());
            super.goBackToPrevPage();
        }
    }

    // Custom components
    private void createUIComponents() {
        fontSizeComboBox1 = new FontSizeComboBox();
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
