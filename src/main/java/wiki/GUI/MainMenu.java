package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PaginationPage;
import wiki.Models.User;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


public class MainMenu extends PageBase {
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

    private final int PAGINATION_COUNT = 5;
    private final int MAX_CHARACTER_INTRO_DISPLAY = 75;

    private int currentPage = 1;
    private int totalPages = 0;



    public MainMenu(WikiController wikiController, PageBase prevPageRef) {
        super(wikiController, prevPageRef);
        add(mainPanel);


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


        ButtonGroup group = new ButtonGroup();
        group.add(authorRadio);
        group.add(titleRadio);

        updateUserLabel();
        fetchData();
    }

    @Override
    protected void frameStart() {

    }

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

    private void fetchData() {
        int type = authorRadio.isSelected() ? 1 : 0;

        PaginationPage response = wikiController.fetchPages(searchField.getText(), currentPage, PAGINATION_COUNT, type);

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
        updateUserLabel();
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                wikiSearchResultListPanel.removeAll();
                wikiSearchResultListPanel.setLayout(new BoxLayout(wikiSearchResultListPanel, BoxLayout.Y_AXIS));

                JScrollPane wikiListScroll = new JScrollPane();
                JPanel panel = new JPanel();

                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                for (Page page : pages) {
                    String intro = page.getAllContent()
                            .replaceAll("\n", " ")
                            .replaceAll("\\<.*?\\>", "")
                            .replaceAll("\\s+", " ");

                    // Truncate first MAX_CHARACTER_INTRO_DISPLAY characters
                    if (intro.length() > MAX_CHARACTER_INTRO_DISPLAY)
                        intro = intro.substring(0, MAX_CHARACTER_INTRO_DISPLAY) + "...";

                    panel.add(new WikiPageSearchDisplay(
                            page.getTitle(),
                            intro,
                            page.getAuthorName(),
                            page.getDateString(),
                            () -> viewPage(page.getId())));
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

    private void nextPage() {
        currentPage++;
        fetchData();
    }

    private void previousPage() {
        currentPage--;
        fetchData();
    }


}
