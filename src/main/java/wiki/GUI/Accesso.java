package wiki.GUI;

import javax.swing.*;
import wiki.Controllers.WikiController;

public class Accesso extends JPanel {
    private WikiController wikiController;
    private Home homeGUI;
    private JPanel loginPanel;
    private JTextField loginName;
    private JPasswordField loginPassword;
    private JButton accediButton;
    private JLabel loginLabel;


    public Accesso(WikiController wikiController, Home homeGUI) {
        // dependency injection
        this.wikiController = wikiController;
        this.homeGUI = homeGUI;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        accediButton.addActionListener(e -> submitLogin());
        add(loginPanel); // Aggiungi un componente al pannello

        //label font size
        loginLabel.setFont(loginLabel.getFont().deriveFont(20.0f));

        //event listener for enter key
        loginPassword.addActionListener(e -> submitLogin());
        loginName.addActionListener(e -> submitLogin());

        setVisible(true);
    }

    public String getLoginName() {
        return loginName.getText();
    }

    public String getLoginPassword() {
        return loginPassword.getText();
    }

    public void submitLogin() {
        if (wikiController.onTryLogin(getLoginName(), getLoginPassword())) {
            homeGUI.setLoginStatus(true, getLoginName());
        }
        else {
            homeGUI.setLoginStatus(false, "");
        }
    }
}
