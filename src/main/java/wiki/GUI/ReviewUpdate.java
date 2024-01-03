package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Update;

import javax.swing.*;
import java.awt.*;

public class ReviewUpdate extends PageBase {
    private JPanel reviewUpdateView;

    public ReviewUpdate(WikiController wikiController, PageBase frame, Update update, boolean updatable) {
        super(wikiController, frame);
        initGUI(new Dimension(550, 400), true);
        add(reviewUpdateView);
        setVisible(true);
    }
}
