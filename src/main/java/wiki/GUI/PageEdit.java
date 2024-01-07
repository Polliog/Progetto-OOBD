package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;

public class PageEdit extends PageBase {
    private JPanel PageEditView;
    private JButton BackBtn;
    private JLabel PageTitleLabel;
    private JTextArea PageContent;
    private JButton SaveBtn;
    private Page page = null;
    private int id;


    public PageEdit(WikiController wikiController, PageBase prevPageRef, int id) {
        super(wikiController, prevPageRef);
        add(PageEditView);
        initGUI();

        fetchData(id);

        this.id = id;
        this.PageTitleLabel.setFont(this.PageTitleLabel.getFont().deriveFont(20.0f));

        BackBtn.addActionListener(e -> onBackPressed());
        SaveBtn.addActionListener(e -> onSavePressed());
    }

    public void fetchData(int id) {
        this.page = wikiController.fetchPage(id);
        if (this.page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PageTitleLabel.setText(this.page.getTitle());
        PageContent.setText(this.page.getAllContent());
        PageContent.setLineWrap(true);
        PageContent.setWrapStyleWord(true);
    }

    private void onBackPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void onSavePressed() {
        confirmSave();
    }

    private void confirmSave() {
        //SHOW A CONFIRMATION DIALOG
        int dialogResult = JOptionPane.showConfirmDialog (null, "Vuoi salvare le modifiche?","Conferma di salvataggio",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            wikiController.compareAndSavePage(this.page, PageContent.getText());
        }
    }
}
