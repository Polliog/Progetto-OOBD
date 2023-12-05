package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;

public class PageEdit extends JPanel {
    private JPanel PageEditView;
    private JButton BackBtn;
    private JLabel PageTitleLabel;
    private JTextArea PageContent;
    private JButton SaveBtn;
    private WikiController wikiController;
    private Page page = null;
    private int id;


    public PageEdit(WikiController wikiController, int id) {
        // dependency injection
        this.id = id;
        this.wikiController = wikiController;


        this.PageTitleLabel.setFont(this.PageTitleLabel.getFont().deriveFont(20.0f));

        fetchData(5);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        add(PageEditView); // Aggiungi un componente al pannello'
        setVisible(true);
    }

    public void fetchData(int id) {
        this.page = wikiController.fetchPage(id);

        if (this.page == null) {
            JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PageTitleLabel.setText(this.page.getTitle());
        PageContent.setText(this.page.getAllContent());
    }


}
