package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PageEdit extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JLabel titleLabel;
    private JTextArea pageContentArea;
    private JButton saveBtn;
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

        backBtn.addActionListener(e -> onBackPressed());
        saveBtn.addActionListener(e -> onSavePressed());

        fetchData();
    }


    private void fetchData() {
        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleLabel.setText(page.getTitle());

        pageContentArea.setText(pageContent);

        saveBtn.setText(Objects.equals(page.getAuthorName(), wikiController.getLoggedUser().getUsername()) ? "Salva Modifiche" : "Richiedi Modifiche");
    }

    private void onBackPressed() {
        goBackToPrevPage();
    }

    private void onSavePressed() {
        // Confirmation dialog
        int dialogResult = JOptionPane.showConfirmDialog (null, "Vuoi salvare le modifiche?","Conferma di salvataggio", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION){
            wikiController.savePageUpdate(page, pageContentArea.getText());
        }
    }

    // Custom components
    private void createUIComponents() {
        fontSizeComboBox1 = new FontSizeComboBox();
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
