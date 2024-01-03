package wiki.GUI;

import javax.swing.*;
import wiki.Controllers.WikiController;

import java.awt.*;

public class LoginPage extends PageBase {
    private JPanel loginPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    private JButton guestBtn;
    private JButton registerBtn;


    public LoginPage(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);

        initGUI();
        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager

        loginBtn.addActionListener(e -> onSubmitLogin());
        registerBtn.addActionListener(e -> onRegisterLogin());
        guestBtn.addActionListener(e -> onGuestLogin());

        add(loginPanel); // Aggiungi un componente al pannello

        //event listener for enter key
        passwordField.addActionListener(e -> onSubmitLogin());
        usernameField.addActionListener(e -> onSubmitLogin());

    }

    public String getLoginName() {
        return usernameField.getText();
    }

    public String getLoginPassword() {
        return passwordField.getText();
    }

    private void onRegisterLogin() {
        // -> WikiPages
        PageBase register = new RegisterPage(wikiController, this);
        this.setVisible(false);
    }

    private void onSubmitLogin() {
        // Login Success
        if (wikiController.onTryLogin(getLoginName(), getLoginPassword())) {
            //homeGUI.setLoginStatus(true, getLoginName());

            // -> WikiPages

            if (wikiController.fetchNotifications()) {
                PageBase notifications = new UserNotifications(wikiController, this);
                this.setVisible(false);
                this.dispose();
            } else {
                PageBase wikiPages = new WikiPages(wikiController, this);
                this.setVisible(false);
                this.dispose();
            }

        }
        // Login Fail
        else {
            //homeGUI.setLoginStatus(false, "");
        }
    }

    private void onGuestLogin() {
        // -> WikiPages
        PageBase wikiPages = new WikiPages(wikiController, this);
        this.setVisible(false);
        this.dispose();
    }



    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
