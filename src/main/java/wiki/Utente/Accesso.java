package wiki.Utente;

import javax.swing.*;

public class Accesso extends JPanel {
    private JButton ciaoButton;
    private JPanel panel1;

    public Accesso() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        add(panel1); // Aggiungi un componente al pannello
        setVisible(true);
    }
}
