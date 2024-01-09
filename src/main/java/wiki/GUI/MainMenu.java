package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PaginationPage;
import wiki.Models.User;

import javax.swing.*;
import java.awt.*;
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
        PaginationPage response = wikiController.fetchPages(searchField.getText(), currentPage, 3);

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
        //in the scroll panel add all the pages formatted like:
        //label that contain the title
        //another label that contain 3 lines of the content
        //a button that link to the page
        //wikiListScroll.removeAll();

        WikiListContent.removeAll();
        WikiListContent.setLayout(new BoxLayout(WikiListContent, BoxLayout.Y_AXIS));

        JScrollPane wikiListScroll = new JScrollPane();



        // Codice Semplificato
        /*
        for (Page page : pages) {
            JPanel pagePanel = new JPanel();
            pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.Y_AXIS));
            pagePanel.setVisible(true);

            JLabel titleLabel = new JLabel(page.getTitle());
            titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));
            pagePanel.add(titleLabel);


            for (int i = 0; i < page.getContent().size() && i < 3; i++) {
                  JLabel contentLabel = new JLabel();
                  //margin top 5 margin left 10
                  contentLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 2, 0));
                  String text = page.getContent().get(i).content;

                  if (text.startsWith("{") && text.endsWith("}")) {
                      text = text.substring(1, text.length() - 1);
                      text = text.split(":")[0];
                      contentLabel.setForeground(Color.BLUE.darker());
                      contentLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                      int finalI = i;

                      contentLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                          @Override
                          public void mouseClicked(java.awt.event.MouseEvent evt) {
                              try {
                                  //check if the link is a url or a page id
                                  //get the link from the content {text:link}
                                  String link = page.getContent().get(finalI).content;
                                  link = link.substring(link.indexOf(":") + 1, link.length() - 1);
                                  System.out.println(link);

                                  if (link.startsWith("http") || link.startsWith(" http")) {
                                      if (Desktop.isDesktopSupported()) {
                                          Desktop.getDesktop().browse(java.net.URI.create(link));
                                      }
                                  }
                                  else {
                                      // !!!!!!!!!!!
                                      //pageView.fetchData(page.getId());
                                      viewPage(Integer.parseInt(link));
                                  }
                              }
                              catch (java.io.IOException e) {
                                  JOptionPane.showMessageDialog(null, "Errore durante l'apertura del link", "Errore", JOptionPane.ERROR_MESSAGE);
                              }
                          }
                      });
                  }

                  contentLabel.setText(text);

                  pagePanel.add(contentLabel);
            }


            JButton pageButton = new JButton("Continua a leggere");
            //10 margin top 10 margin left 10 margin bottom 10
            pageButton.addActionListener(e -> {
                // !!!!!!!!!!!!!!!!!!!!
                //pageView.fetchData(page.getId());
                viewPage(page.getId());

            });
            pagePanel.add(pageButton);
            panel.add(pagePanel);
        }
         */



        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 1; i <= 10; i++) {
            panel.add(new WikiSearchRenderer("Titolo " + i, "introoo"));
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
