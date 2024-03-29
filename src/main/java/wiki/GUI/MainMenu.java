package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.GUI.Custom.WikiPagePanel;
import wiki.Models.Page;
import wiki.Models.PaginationPage;
import wiki.Models.User;

import javax.swing.*;
import java.util.ArrayList;


/** Classe che rappresenta la pagina principale dell'applicazione */
public class MainMenu extends PageBase implements IUpdatable {
    private final int PAGINATION_COUNT = 5;
    private final int MAX_CHARACTER_INTRO_DISPLAY = 75;

    private JPanel mainPanel;
    private JTextField searchField;
    private JButton searchPageBtn;
    private JButton previousPageBtn;
    private JButton nextPageBtn;
    private JLabel paginationLabel;
    private JPanel wikiSearchResultListPanel;
    private JButton logoutBtn;
    private JButton loginBtn;
    private JLabel usernameLabel;
    private JButton createPageBtn;
    private JButton notificationBtn;
    private JRadioButton authorRadio;
    private JRadioButton titleRadio;

    private int currentPage = 1;
    private int totalPages = 0;


    public MainMenu(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(mainPanel);

        ButtonGroup group = new ButtonGroup();
        group.add(authorRadio);
        group.add(titleRadio);

        // Action listeners
        loginBtn.addActionListener(e -> onLoginPressed());
        logoutBtn.addActionListener(e -> onLogoutPressed());
        createPageBtn.addActionListener(e -> onCreatePagePressed());
        notificationBtn.addActionListener(e -> onNotificationPagePressed());
        previousPageBtn.addActionListener(e -> previousPage());
        nextPageBtn.addActionListener(e -> nextPage());
        searchPageBtn.addActionListener(e -> fetchData());
        searchField.addActionListener(e -> fetchData());

        updateGUI();
    }

    @Override
    public void updateGUI() {
        updateUserLabel();
        fetchData();
    }

    /** Metodo che aggiorna la label dell'utente loggato */
    private void updateUserLabel() {
        User loggedUser = wikiController.getLoggedUser();

        if (loggedUser == null) {
            usernameLabel.setText("Utente non loggato");

            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);
            createPageBtn.setVisible(false);
            notificationBtn.setVisible(false);
        }
        else {
            usernameLabel.setText(String.format("<html>Bentornato <b>%s</b></html>", loggedUser.getUsername()));

            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
            createPageBtn.setVisible(true);
            notificationBtn.setVisible(true);
        }
    }

    /** Metodo che effettua il fetch dei dati */
    private void fetchData() {
        PaginationPage response = wikiController.fetchPages(searchField.getText(), currentPage, PAGINATION_COUNT, authorRadio.isSelected() ? 1 : 0);

        if (response == null) return;

        totalPages = response.getTotalPages();
        currentPage = response.getCurrentPage();

        updatePaginationUI();
        updateSearchResultListUI(response.getPages());
    }

    /** Metodo che aggiorna l'interfaccia grafica della paginazione */
    private void updatePaginationUI() {
        previousPageBtn.setEnabled(currentPage > 1);
        nextPageBtn.setEnabled(currentPage < totalPages);
        paginationLabel.setText("Pagina " + currentPage + " di " + totalPages);
    }

    /** Metodo che aggiorna l'interfaccia grafica dei risultati della ricerca */
    private void updateSearchResultListUI(ArrayList<Page> pages) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                wikiSearchResultListPanel.removeAll();
                wikiSearchResultListPanel.setLayout(new BoxLayout(wikiSearchResultListPanel, BoxLayout.Y_AXIS));

                JScrollPane wikiListScroll = new JScrollPane();
                JPanel panel = new JPanel();

                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                for (Page page : pages) {

                    var pageContentStrings = wikiController.fetchAllPageContent(page.getId());

                    String intro = pageContentStrings
                            .replaceAll("\n", " ")
                            .replaceAll("<.*?>", "")
                            .replaceAll("\\s+", " ");

                    // Truncate first MAX_CHARACTER_INTRO_DISPLAY characters
                    if (intro.length() > MAX_CHARACTER_INTRO_DISPLAY)
                        intro = intro.substring(0, MAX_CHARACTER_INTRO_DISPLAY) + "...";

                    panel.add(new WikiPagePanel(
                            page.getTitle(),
                            intro,
                            page.getAuthorName(),
                            page.getCreationDateString(),
                            wikiController.getLoggedUser(),
                            () -> viewPage(page),
                            () -> editPage(page),
                            () -> deletePage(page)
                    ));
                }

                wikiListScroll.setViewportView(panel);
                wikiSearchResultListPanel.add(wikiListScroll);

                return null;
            }

            @Override
            protected void done() {
                wikiSearchResultListPanel.revalidate();
                wikiSearchResultListPanel.repaint();
            }
        };

        worker.execute();
    }

    /** Metodo che gestisce l'evento di pressione del bottone di login */
    private void onLoginPressed() {
        new LoginPage(wikiController, this);
    }

    /** Metodo che gestisce l'evento di pressione del bottone di logout */
    private void onLogoutPressed() {
        wikiController.disconnectUser();
        updateUserLabel();
    }

    /** Metodo che gestisce l'evento di pressione del bottone delle notifiche */
    private void onNotificationPagePressed() {
        new UserNotifications(wikiController, this);
    }

    /** Metodo che gestisce l'evento di pressione del bottone di creazione di una nuova pagina */
    private void onCreatePagePressed() {
        new PageCreate(wikiController, this);
    }

    private void nextPage() {
        currentPage++;
        fetchData();
    }

    private void previousPage() {
        currentPage--;
        fetchData();
    }

    /** Metodo che gestisce l'evento di pressione del bottone di visualizzazione di una pagina */
    private boolean viewPage(Page p) {
        new PageView(wikiController, this, p);

        return true;
    }

    /** Metodo che gestisce l'evento di pressione del bottone di modifica di una pagina */
    private boolean editPage(Page page) {
        new PageEdit(wikiController, this, page, wikiController.fetchAllPageContent(page.getId()));
        return true;
    }

    /** Metodo che gestisce l'evento di pressione del bottone di eliminazione di una pagina */
    private boolean deletePage(Page page) {
        return wikiController.deletePage(page);
    }


}
