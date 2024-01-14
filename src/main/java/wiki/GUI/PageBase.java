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
        ImageIcon img = new ImageIcon("src/main/resources/icon.jpeg");
        this.setIconImage(img.getImage());
    }

    public void initGUI(boolean isResizable, Dimension dimension) {
        this.setResizable(isResizable);

        //set minimum size
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void initGUI(boolean isResizable) {
        this.setResizable(isResizable);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Dimension dimension = new Dimension(600, 600);
        this.setMinimumSize(dimension);
        this.setPreferredSize(dimension);

        setVisible(true);
    }

    public void initGUI() {
        this.initGUI(true, new Dimension(500, 500));
    }



}


