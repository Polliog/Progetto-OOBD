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

        // Listeners
        registerBtn.addActionListener(e -> onRegisterPressed());
        loginBtn.addActionListener(e -> onLoginPressed());

        //event listener for enter key
        usernameField.addActionListener(e -> onRegisterPressed());
        passwordField.addActionListener(e -> onRegisterPressed());

        usernameField.requestFocusInWindow();
    }

    @Override
    protected void frameStart() {

    }

    private String getRegisterName() {
        return usernameField.getText();
    }

    private String getRegisterPassword() {
        return passwordField.getText();
    }

    private void onRegisterPressed() {
        // -> Goes back to Login Page on Register Success
        if (wikiController.onTryRegister(getRegisterName(), getRegisterPassword())) {
            prevPageRef.setVisible(true);
            this.setVisible(false);
            this.dispose();
        }
    }

    private void onLoginPressed() {
        // -> Goes back to Login Page
        prevPageRef.setVisible(true);
        this.setVisible(false);
        this.dispose();
    }
}