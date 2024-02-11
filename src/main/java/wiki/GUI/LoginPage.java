package wiki.GUI;

import javax.swing.*;

import wiki.Controllers.WikiController;

/** Classe che rappresenta la pagina di login */
public class LoginPage extends PageBase implements IUpdatable {
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton guestLoginBtn;

    public LoginPage(WikiController wikiController, PageBase nextPageRef) {
        super(wikiController, nextPageRef);
        add(mainPanel);

        // Event Listeners
        loginBtn.addActionListener(e -> onLoginPressed());
        registerBtn.addActionListener(e -> onRegisterPressed());
        passwordField.addActionListener(e -> onLoginPressed());
        usernameField.addActionListener(e -> onLoginPressed());
        guestLoginBtn.addActionListener(e -> onGuestLoginPressed());

        updateGUI();
    }

    @Override
    public void updateGUI() {
        usernameField.requestFocusInWindow();
    }

    /** Metodo che gestisce l'evento di pressione del bottone di registrazione */
    private void onRegisterPressed() {
        // -> Goes to RegisterPage
        // From that page you can only come back to the LoginPage
        new RegisterPage(wikiController, this);
    }

    /** Metodo che gestisce l'evento di pressione del bottone di login */
    private void onLoginPressed() {
        // -> Goes to MainMenu on Login Success
        if (wikiController.onTryLogin(usernameField.getText(), passwordField.getText())) {
            if (prevPage == null) {
                var mainMenu = new MainMenu(wikiController, this);

                // Check for any new notification
                if (wikiController.doesUserHaveNewNotifications())
                    new UserNotifications(wikiController, mainMenu);

                // No longer need for the LoginPage GUI
                this.dispose();
            }
            else {
                if (wikiController.doesUserHaveNewNotifications()) {
                    new UserNotifications(wikiController, prevPage);

                    // No longer need for the LoginPage GUI
                    this.dispose();
                }
                else
                    super.goBackToPrevPage();
            }
        }
    }

    /** Metodo che gestisce l'evento di pressione del bottone di login come ospite */
    private void onGuestLoginPressed() {
        new MainMenu(wikiController, this);
    }
}
