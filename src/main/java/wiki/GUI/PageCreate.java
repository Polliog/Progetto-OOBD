package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;

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

        contentShortcutsPanel1.init(pageContentArea);

        createPageBtn.addActionListener(e -> onCreatePage());
        backBtn.addActionListener(e -> onBackButtonPressed());
    }

    @Override
    protected void frameStart() {

    }


    private void onCreatePage() {
        // -> Goes back to the Main Menu Page
        if (wikiController.createPage(pageTitleField.getText(), pageContentArea.getText())) {
            prevPageRef.setVisible(true);
            this.dispose();
        }
    }

    private void onBackButtonPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void createUIComponents() {
        contentShortcutsPanel1 = new ContentShortcutsPanel();
    }
}
