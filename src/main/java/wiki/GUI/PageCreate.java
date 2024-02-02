package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;

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

        createPageBtn.addActionListener(e -> onCreatePage());
        backBtn.addActionListener(e -> onBackButtonPressed());
    }


    private void onCreatePage() {
        // -> Goes back to the Main Menu Page
        if (wikiController.createPage(pageTitleField.getText(), pageContentArea.getText())) {
            goBackToPrevPage();
        }
    }

    private void onBackButtonPressed() {
        goBackToPrevPage();
    }

    private void createUIComponents() {
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
