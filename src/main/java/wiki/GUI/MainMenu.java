package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PaginationPage;
import wiki.Models.User;

import javax.swing.*;
import java.awt.*;
import java.security.PrivateKey;
import java.util.ArrayList;

// TODO
//  rinominare come MainMenu

public class MainMenu extends PageBase {
    private JPanel WikiPagesView;
    private JTextField searchField;
    private JButton searchPageBtn;
    private JButton previousPageBtn;
    private JButton nextPageBtn;
    private JLabel paginationLabel;
    private JPanel WikiListContent;
    private JButton logoutBtn;
    private JButton loginBtn;
    private JLabel usernameLabel;
    private JButton createPageBtn;
    private JButton notificationBtn;

    private final int PAGINATION_COUNT = 5;
    private final int MAX_CHARACTER_INTRO_DISPLAY = 50;

    private int currentPage = 1;
    private int totalPages = 0;



    public MainMenu(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(WikiPagesView);
        initGUI(true, new Dimension(650, 500));

        // Buttons Action listeners
        loginBtn.addActionListener(e -> onLoginPressed());
        logoutBtn.addActionListener(e -> onLogoutPressed());
        createPageBtn.addActionListener(e -> onCreatePagePressed());
        notificationBtn.addActionListener(e -> onNotificationPressed());
        searchPageBtn.addActionListener(e -> fetchData());
        previousPageBtn.addActionListener(e -> previousPage());
        nextPageBtn.addActionListener(e -> nextPage());
        // Enter key
        searchField.addActionListener(e -> fetchData());

        updateUserTab();
        fetchData();
    }

    private void updateUserTab() {
        User loggedUser = wikiController.getLoggedUser();

        if (loggedUser == null) {
            usernameLabel.setText("Utente non loggato");

            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);

            createPageBtn.setVisible(false);
            notificationBtn.setVisible(false);
        }
        else {
            usernameLabel.setText("Bentornato " + loggedUser.getUsername());

            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);

            createPageBtn.setVisible(true);
            notificationBtn.setVisible(true);
        }
    }

    private void fetchData() {
        PaginationPage response = wikiController.fetchPages(searchField.getText(), currentPage, PAGINATION_COUNT);

        if (response == null) return;

        totalPages = response.totalPages;
        currentPage = response.currentPage;
        updatePaginationUI();
        updateListView(response.pages);
    }

    private void onLoginPressed() {
        new LoginPage(wikiController, this);
        this.setVisible(false);
    }

    private void onLogoutPressed() {
        wikiController.disconnectUser();
        updateUserTab();
    }

    private void onNotificationPressed() {
        new UserNotifications(wikiController, this);
        this.setVisible(false);
    }

    private void onCreatePagePressed() {
        new PageCreate(wikiController, this);
        this.setVisible(false);
    }

    private void viewPage(int id) {
        new PageView(wikiController, this, id);
        this.setVisible(false);
    }

    private void updatePaginationUI() {
        previousPageBtn.setEnabled(currentPage > 1);
        nextPageBtn.setEnabled(currentPage < totalPages);
        paginationLabel.setText("Pagina " + currentPage + " di " + totalPages);
    }


    private void updateListView(ArrayList<Page> pages) {
        WikiListContent.removeAll();
        WikiListContent.setLayout(new BoxLayout(WikiListContent, BoxLayout.Y_AXIS));

        JScrollPane wikiListScroll = new JScrollPane();

        JPanel panel = new JPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (Page page : pages) {
            StringBuilder intro = new StringBuilder(page.getAllContent().replaceAll("\n", " "));

            // Tronca i primi 50 caratteri
            if (intro.length() > MAX_CHARACTER_INTRO_DISPLAY)
                intro = new StringBuilder(intro.substring(0, MAX_CHARACTER_INTRO_DISPLAY));

            intro.append("...");

            panel.add(new WikiPageSearchDisplay(
                    page.getTitle(),
                    intro.toString(),
                    page.getAuthor(),
                    page.getCreation().toString().substring(0, 10),
                    () -> viewPage(page.getId())));
        }

        wikiListScroll.setViewportView(panel);
        wikiListScroll.revalidate();
        wikiListScroll.repaint();

        WikiListContent.add(wikiListScroll);
    }


    private void nextPage() {
        currentPage++;
        fetchData();
    }

    private void previousPage() {
        currentPage--;
        fetchData();
    }
}
