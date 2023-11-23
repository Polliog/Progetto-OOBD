package wiki.GUI;

import javax.swing.*;
import wiki.Controllers.WikiController;

public class Accesso extends JPanel {
    private JPanel loginPanel;
    private JTextField loginName;
    private JPasswordField loginPassword;
    private JButton accediButton;

    public Accesso() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        accediButton.addActionListener(e -> submitLogin());
        add(loginPanel); // Aggiungi un componente al pannello
        setVisible(true);

        //event listener for enter key
        loginPassword.addActionListener(e -> submitLogin());
        loginName.addActionListener(e -> submitLogin());
    }

    public String getLoginName() {
        return loginName.getText();
    }

    public String getLoginPassword() {
        return loginPassword.getText();
    }

    public void submitLogin() {
        WikiController.onTryLogin(getLoginName(), getLoginPassword());
    }
}
