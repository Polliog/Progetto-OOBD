package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;


public class RegisterPage extends PageBase {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerBtn;
    private JButton loginBtn;


    public RegisterPage(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        // Event Listeners
        registerBtn.addActionListener(e -> onRegisterPressed());
        loginBtn.addActionListener(e -> onLoginPressed());
        usernameField.addActionListener(e -> onRegisterPressed());
        passwordField.addActionListener(e -> onRegisterPressed());

        usernameField.requestFocusInWindow();
    }

    private void onRegisterPressed() {
        // -> Goes back to Login Page on Register Success
        if (wikiController.onTryRegister(usernameField.getText(), passwordField.getText())) {
            goBackToPrevPage();
        }
    }

    private void onLoginPressed() {
        // -> Goes back to Login Page
        goBackToPrevPage();
    }
}