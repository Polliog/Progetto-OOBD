package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;

public abstract class PageBase extends JFrame {
    protected WikiController wikiController;
    protected PageBase prevPageRef;


    public PageBase(WikiController wikiController, PageBase prevPageRef) {
        this.wikiController = wikiController;
        this.prevPageRef = prevPageRef;

        // set the icon
        ImageIcon img = new ImageIcon("src/main/resources/icon.jpg");
        setIconImage(img.getImage());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Dimension dimension = new Dimension(600, 600);
        setMinimumSize(dimension);
        setPreferredSize(dimension);

        setResizable(true);
        setVisible(true);
    }

    protected abstract void frameStart();
}


