package wiki;

import wiki.Utente.Accesso;
import wiki.Utente.Registrazione;
import javax.swing.*;


public class Home {
    private JTabbedPane tabbedPane;
    private Registrazione registrazione = new Registrazione();
    private Accesso accesso = new Accesso();

    public Home() {
        tabbedPane.add("Registrazione", registrazione);
        tabbedPane.add("Accesso", accesso);

        // Frame Settings
        JFrame frame = new JFrame("Wiki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(750, 500);
        frame.setResizable(false);
        frame.setContentPane(tabbedPane);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Home();
            }
        });
    }
}
