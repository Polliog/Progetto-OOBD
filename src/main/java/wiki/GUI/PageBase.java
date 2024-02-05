package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;

public abstract class PageBase extends JFrame {
    protected WikiController wikiController;
    protected PageBase prevPage;


    public PageBase(WikiController wikiController, PageBase prevPage) {
        this.wikiController = wikiController;
        this.prevPage = prevPage;

        // set the icon
        ImageIcon img = new ImageIcon("src/main/resources/icon.jpg");
        setIconImage(img.getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dimension = new Dimension(600, 600);
        setMinimumSize(dimension);
        setPreferredSize(dimension);

        setResizable(true);

        setLocationRelativeTo(null);

        if (prevPage != null)
            prevPage.setVisible(false);
        setVisible(true);
    }

    protected void goBackToPrevPage() {
        prevPage.setVisible(true);

        if (prevPage instanceof IUpdatable)
            ((IUpdatable) prevPage).updateGUI();

        this.dispose();
    }
}


