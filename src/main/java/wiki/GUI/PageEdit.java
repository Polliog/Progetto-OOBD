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


    public PageEdit(WikiController wikiController, PageBase frame, int id) {
        super(wikiController, frame);
        initGUI();
        // dependency injection
        this.id = id;

        this.PageTitleLabel.setFont(this.PageTitleLabel.getFont().deriveFont(20.0f));

        fetchData(this.id);
        add(PageEditView);

        BackBtn.addActionListener(e -> {
            frame.setVisible(true);
            this.dispose();
        });

        SaveBtn.addActionListener(e -> {
            confirmSave();
        });
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

    private void confirmSave() {
        //SHOW A CONFIRMATION DIALOG
        int dialogResult = JOptionPane.showConfirmDialog (null, "Vuoi salvare le modifiche?","Conferma di salvataggio",JOptionPane.YES_NO_OPTION);
        if(dialogResult == JOptionPane.YES_OPTION){
            wikiController.compareAndSavePage(this.page, PageContent.getText());
        }
    }
}
