package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;


public class Home {
    private JTabbedPane tabbedPane;
    private JPanel viewPanel;
    private JLabel userStatusLabel;
    private JButton registratiButton;
    private JButton accediButton;
    private JButton disconnettitiButton;
    public Home() {
        tabbedPane.add("Registrazione", new Registrazione());
        tabbedPane.add("Accesso", new Accesso());
        tabbedPane.add("Crea pagina", new PageCreate());


        // Frame Settings
        JFrame frame = new JFrame("Wiki");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setSize(750, 500);
        frame.setResizable(false);

        userStatusLabel.setText("Utente non loggato");

        registratiButton.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        accediButton.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        disconnettitiButton.addActionListener(e -> logout());

        frame.setContentPane(viewPanel);
        frame.setVisible(true);
    }

    public void setLoginStatus(boolean logged, String username) {
        if (logged) {
            userStatusLabel.setText("Utente loggato: " + username);
            disconnettitiButton.setVisible(true);
            accediButton.setVisible(false);
            registratiButton.setVisible(false);
        } else {
            userStatusLabel.setText("Utente non loggato");
            disconnettitiButton.setVisible(false);
            accediButton.setVisible(true);
            registratiButton.setVisible(true);
        }
    }

    private void logout() {
        WikiController.disconnectUser();
    }

}
