package wiki.GUI;

import javax.swing.*;

import wiki.Controllers.WikiController;

public class LoginPage extends PageBase {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;

    public LoginPage(WikiController wikiController, PageBase nextPageRef) {
        super(wikiController, nextPageRef);
        add(mainPanel);

        // event listeners for buttons
        loginBtn.addActionListener(e -> onLoginPressed());
        registerBtn.addActionListener(e -> onRegisterPressed());
        // event listener for enter key
        passwordField.addActionListener(e -> onLoginPressed());
        usernameField.addActionListener(e -> onLoginPressed());

        usernameField.requestFocusInWindow();
        // on back (?)
    }

    @Override
    protected void frameStart() {

    }

    private String getLoginName() {
        return usernameField.getText();
    }
    private String getLoginPassword() {
        return passwordField.getText();
    }

    private void onRegisterPressed() {
        // -> Goes to RegisterPage
        // From that point you can only come back to the LoginPage
        // so there is no dispose needed
        new RegisterPage(wikiController, this);
        this.setVisible(false);
    }

    private void onLoginPressed() {
        // -> Goes to MainMenu on Login Success
        if (wikiController.onTryLogin(getLoginName(), getLoginPassword())) {
            var mainMenu = new MainMenu(wikiController, this);
            this.setVisible(false);

            // Check for any new notification
            if (wikiController.fetchNotifications()) {
                new UserNotifications(wikiController, mainMenu);
                mainMenu.setVisible(false);
            }

            // No longer use for the LoginPage GUI
            this.dispose();
        }
    }
}
