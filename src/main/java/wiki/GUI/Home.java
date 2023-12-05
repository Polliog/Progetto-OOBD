package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;


public class Home {
    private WikiController wikiController;
    private JTabbedPane tabbedPane;
    private JPanel viewPanel;
    private JLabel userStatusLabel;
    private JButton registerButton;
    private JButton loginButton;
    private JButton logoutButton;

    public Home(WikiController wikiController) {
        this.wikiController = wikiController;

        // TODO
        // Bisogna istanziare le GUI solo quando si utilizzano
        // Passando il controllo dalla GUI corrente alla prossima
        // Qui il tabbedPane non va bene per la complessita' del codice e la memoria utilizzata
        // (pag.209 slide Java - tramontana)

        tabbedPane.add("Registrazione",     new Registrazione(wikiController));
        tabbedPane.add("Accesso",           new Accesso(wikiController, this));
        tabbedPane.add("Crea pagina",       new PageCreate(wikiController));

        var pageView = new PageView(wikiController);
        tabbedPane.add("Visualizza pagina", pageView);

        var wikiPages = new WikiPages(wikiController, pageView);
        tabbedPane.add("Pagine", wikiPages);
        wikiPages.fetchData();

        tabbedPane.add("Modifica", new PageEdit(wikiController));

        // Frame Settings
        JFrame frame = new JFrame("Wiki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setSize(750, 500);
        frame.setResizable(false);

        userStatusLabel.setText("Utente non loggato");

        registerButton.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        loginButton.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        logoutButton.addActionListener(e -> logout());

        frame.setContentPane(viewPanel);
        frame.setVisible(true);
    }

    public void setLoginStatus(boolean logged, String username) {
        if (logged) {
            userStatusLabel.setText("Utente loggato: " + username);
            logoutButton.setVisible(true);
            loginButton.setVisible(false);
            registerButton.setVisible(false);
        }
        else {
            userStatusLabel.setText("Utente non loggato");
            logoutButton.setVisible(false);
            loginButton.setVisible(true);
            registerButton.setVisible(true);
        }
    }

    private void logout() {
        wikiController.disconnectUser();

        setLoginStatus(false, "");
    }
}
