package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.DAOImplementations.UserDAO;

import javax.swing.*;
import java.sql.SQLException;


public class RegisterPage extends PageBase {
    private JPanel registerPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerBtn;
    private JButton loginBtn;

    public RegisterPage(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);

        initGUI();

        add(registerPanel); // Aggiungi un componente al pannello

        registerBtn.addActionListener(e -> registraUtente());
        loginBtn.addActionListener(e -> {
            //go on login tab
            PageBase login = new LoginPage(wikiController, this);
            this.setVisible(false);
            this.dispose();
        });

        //event listener for enter key
        usernameField.addActionListener(e -> registraUtente());
        passwordField.addActionListener(e -> registraUtente());
    }

    public String getRegisterName() {
        return usernameField.getText();
    }

    public String getRegisterPassword() {
        return passwordField.getText();
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
            UserDAO utente = new UserDAO();
            if (utente.doesUserExist(getRegisterName())) {
                JOptionPane.showMessageDialog(null, "Nome utente già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il controllo dell'esistenza dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        try {
            UserDAO utente = new UserDAO();
            utente.insertUser(getRegisterName(), getRegisterPassword());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante la registrazione dell'utente", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Registrazione effettuata con successo, accedi per continuare", "Successo", JOptionPane.INFORMATION_MESSAGE);

        //go on login tab
        PageBase login = new LoginPage(wikiController, this);
        this.setVisible(false);
        this.dispose();
    }
}