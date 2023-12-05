package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;
import java.awt.*;

public abstract class PageBase extends JFrame {
    protected WikiController wikiController;
    protected PageBase frame;

    public PageBase(WikiController wikiController, PageBase frame) {
        this.wikiController = wikiController;
        this.frame = frame;
    }

    public void initGUI(Dimension dimension, boolean isResizable) {
        this.setSize(dimension);
        this.setResizable(isResizable);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void initGUI() {
        this.initGUI(new Dimension(550, 400), false);
    }

}


