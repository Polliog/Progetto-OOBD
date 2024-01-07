package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PageCreate extends PageBase {
    private JLabel creationLabel;
    private JPanel pageCreatePanel;
    private JTextField pageTitle;
    private JTextArea pageContent;
    private JButton createPageButton;

    public PageCreate(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);
        add(pageCreatePanel);
        initGUI(true, new Dimension(550, 400));

        createPageButton.addActionListener(e -> onCreatePage());

        //label font size
        creationLabel.setFont(creationLabel.getFont().deriveFont(20.0f));

        setVisible(true);
    }

    private void onCreatePage() {
        // -> Goes back to the Main Menu Page
        if (wikiController.createPage(pageTitle.getText(), pageContent.getText())) {
            prevPageRef.setVisible(true);
            this.dispose();
        }
    }
}
