package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.ContentShortcutsPanel;

import javax.swing.*;
import java.awt.*;

/** Questa classe rappresenta la pagina di creazione di una nuova pagina */
public class PageCreate extends PageBase {
    private JPanel mainPanel;
    private JTextField pageTitleField;
    private JTextArea pageContentArea;
    private JButton createPageBtn;
    private JButton backBtn;
    private ContentShortcutsPanel contentShortcutsPanel1;


    public PageCreate(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);
        add(mainPanel);

        setMinimumSize(new Dimension(700, 700));

        contentShortcutsPanel1.init(pageContentArea);

        createPageBtn.addActionListener(e -> onCreatePagePressed());
        backBtn.addActionListener(e -> onBackButtonPressed());
    }


    /** Metodo che viene chiamato quando viene premuto il pulsante di creazione di una pagina */
    private void onCreatePagePressed() {
        // -> Goes back to the Main Menu Page
        if (wikiController.createPage(pageTitleField.getText(), pageContentArea.getText())) {
            goBackToPrevPage();
        }
    }

    /** Metodo che viene chiamato quando viene premuto il pulsante di ritorno */
    private void onBackButtonPressed() {
        goBackToPrevPage();
    }

    private void createUIComponents() {
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
