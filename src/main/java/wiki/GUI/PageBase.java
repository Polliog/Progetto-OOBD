package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;

/** Classe astratta che rappresenta una pagina della GUI */
public abstract class PageBase extends JFrame {
    protected WikiController wikiController;
    protected PageBase prevPage;


    public PageBase(WikiController wikiController, PageBase prevPage) {
        this.wikiController = wikiController;
        this.prevPage = prevPage;

        // Icona della finestra
        ImageIcon img = new ImageIcon("src/main/resources/icon.jpg");
        setIconImage(img.getImage());

        // Imposta la dimensione minima e preferita della finestra
        Dimension dimension = new Dimension(600, 600);
        setMinimumSize(dimension);
        setPreferredSize(dimension);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        if (prevPage != null)
            prevPage.setVisible(false);
        setVisible(true);
    }


    /** Metodo che permette di tornare alla pagina precedente */
    protected void goBackToPrevPage() {
        prevPage.setVisible(true);

        if (prevPage instanceof IUpdatable)
            ((IUpdatable) prevPage).updateGUI();

        this.dispose();
    }
}


