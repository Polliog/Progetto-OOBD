package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;

/**
 * La classe RegisterPage estende PageBase e rappresenta la pagina di registrazione.
 * Ogni RegisterPage ha vari componenti dell'interfaccia utente per la registrazione.
 */
public class RegisterPage extends PageBase {
    private JPanel mainPanel; // Il pannello principale della pagina di registrazione
    private JTextField usernameField; // Il campo di testo per l'username
    private JPasswordField passwordField; // Il campo di testo per la password
    private JButton registerBtn; // Il pulsante per la registrazione
    private JButton loginBtn; // Il pulsante per il login

    /**
     * Costruisce una nuova RegisterPage con i dettagli specificati.
     *
     * @param wikiController Il controller del wiki.
     * @param prevPageRef La pagina precedente.
     */
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

    /**
     * Gestisce l'evento di pressione del pulsante di registrazione.
     * Se la registrazione ha successo, torna alla pagina di login.
     */
    private void onRegisterPressed() {
        if (wikiController.onTryRegister(usernameField.getText(), passwordField.getText())) {
            goBackToPrevPage();
        }
    }

    /**
     * Gestisce l'evento di pressione del pulsante di login.
     * Torna alla pagina di login.
     */
    private void onLoginPressed() {
        goBackToPrevPage();
    }
}