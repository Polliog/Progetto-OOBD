package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;

public class PageEdit extends PageBase {
    private JPanel mainPanel;
    private JButton backBtn;
    private JLabel titleLabel;
    private JTextArea pageContentArea;
    private JButton saveBtn;
    private FontSizeComboBox fontSizeComboBox1;
    private ContentShortcutsPanel contentShortcutsPanel1;

    private Page page;
    private final int id;

    public PageEdit(WikiController wikiController, PageBase prevPageRef, int id) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        this.id = id;

        fontSizeComboBox1.init(pageContentArea);
        contentShortcutsPanel1.init(pageContentArea);

        backBtn.addActionListener(e -> onBackPressed());
        saveBtn.addActionListener(e -> onSavePressed());

        frameStart();
    }

    @Override
    protected void frameStart() {
        fetchData(id);
    }

    private void fetchData(int id) {
        page = wikiController.fetchPage(id);
        if (page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        titleLabel.setText(page.getTitle());
        pageContentArea.setText(page.getAllContent());
    }


    private void onBackPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void onSavePressed() {
        confirmSave();
    }

    private void confirmSave() {
        // Confirmation dialog
        int dialogResult = JOptionPane.showConfirmDialog (null, "Vuoi salvare le modifiche?","Conferma di salvataggio", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION){
            wikiController.compareAndSavePage(page, pageContentArea.getText());
        }
    }

    // Custom components
    private void createUIComponents() {
        fontSizeComboBox1 = new FontSizeComboBox();
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
