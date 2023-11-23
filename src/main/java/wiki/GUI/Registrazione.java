package wiki.GUI;

import wiki.Entities.DAOImplementations.UtenteDAO;

import javax.swing.*;
import java.sql.SQLException;


public class Registrazione extends JPanel {
    private JPanel registerPanel;
    private JTextField registerName;
    private JPasswordField registerPassword;
    private JButton registerBtn;

    public Registrazione() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        add(registerPanel); // Aggiungi un componente al pannello
        setVisible(true);


        registerBtn.addActionListener(e -> registraUtente());

        //event listener for enter key
        registerName.addActionListener(e -> registraUtente());
        registerPassword.addActionListener(e -> registraUtente());
    }

    public String getRegisterName() {
        return registerName.getText();
    }

    public String getRegisterPassword() {
        return registerPassword.getText();
    }

    public void registraUtente() {

        if (getRegisterName().equals("") || getRegisterPassword().equals("")) {
            JOptionPane.showMessageDialog(null, "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (getRegisterName().contains(" ") || getRegisterPassword().contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nome utente e password non possono contenere spazi", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UtenteDAO utente = new UtenteDAO();
            if (utente.esisteUtente(getRegisterName())) {
                JOptionPane.showMessageDialog(null, "Nome utente già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        try {
            UtenteDAO utente = new UtenteDAO();
            utente.salvaUtente(getRegisterName(), getRegisterPassword());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Registrazione effettuata con successo, accedi per continuare", "Successo", JOptionPane.INFORMATION_MESSAGE);

        registerName.setText("");
        registerPassword.setText("");

        //go on login tab
        JTabbedPane tabbedPane = (JTabbedPane) getParent();
        tabbedPane.setSelectedIndex(1);
    }
}