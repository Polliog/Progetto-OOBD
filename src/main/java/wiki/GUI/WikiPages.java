package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PaginationPage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WikiPages extends PageBase {
    private JPanel WikiPagesView;
    private JTextField searchField;
    private JButton cercaButton;
    private JButton previousPageBtn;
    private JButton nextPageBtn;
    private JLabel paginationLabel;
    private JPanel WikiListContent;
    private JButton logoutBtn;
    private JButton loginBtn;
    private JButton registerBtn;
    private JLabel usernameLabel;
    private JButton createPageBtn;

    private int currentPage = 1;
    private int totalPages = 0;




    public WikiPages(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);

        initGUI(new Dimension(550, 400), false);
        updateUserTab();

        logoutBtn.addActionListener(e -> {
            wikiController.disconnectUser();
            updateUserTab();
        });

        loginBtn.addActionListener(e -> {
            PageBase login = new LoginPage(wikiController, this);
            this.setVisible(false);
            this.dispose();
        });

        registerBtn.addActionListener(e -> {
            PageBase register = new RegisterPage(wikiController, this);
            this.setVisible(false);
            this.dispose();
        });

        createPageBtn.addActionListener(e -> {
            PageBase createPage = new PageCreate(wikiController, this);
            this.setVisible(false);
            this.dispose();
        });

        //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager

        add(WikiPagesView); // Aggiungi un componente al pannello

        cercaButton.addActionListener(e -> fetchData());
        previousPageBtn.addActionListener(e -> previousPage());
        nextPageBtn.addActionListener(e -> nextPage());
        searchField.addActionListener(e -> fetchData());

        fetchData();
    }

    private void updateUserTab() {
        if (wikiController.getLoggedUser() == null) {
            usernameLabel.setText("Utente non loggato");
            logoutBtn.setVisible(false);
            loginBtn.setVisible(true);
            registerBtn.setVisible(true);
        }
        else {
            usernameLabel.setText("Bentornato " + wikiController.getLoggedUser().getUsername());
            logoutBtn.setVisible(true);
            loginBtn.setVisible(false);
            registerBtn.setVisible(false);
            createPageBtn.setVisible(true);
        }
    }


    public void fetchData() {
        PaginationPage response = this.wikiController.fetchPages(searchField.getText(), currentPage, 3);

        if (response == null) return;

        this.totalPages = response.totalPages;
        this.currentPage = response.currentPage;
        this.updatePaginationUI();
        this.updateListView(response.pages);
    }

    private void updatePaginationUI() {
        previousPageBtn.setEnabled(currentPage > 1);
        nextPageBtn.setEnabled(currentPage < totalPages);
        paginationLabel.setText("Pagina " + currentPage + " di " + totalPages);
    }

    private void viewPage(int id) {
        PageBase pageView = new PageView(wikiController, this, id);
        this.setVisible(false);
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
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (Page page : pages) {
            JPanel pagePanel = new JPanel();
            pagePanel.setLayout(new BoxLayout(pagePanel, BoxLayout.Y_AXIS));
            pagePanel.setVisible(true);

            JLabel titleLabel = new JLabel(page.getTitle());
            titleLabel.setFont(titleLabel.getFont().deriveFont(20.0f));
            //margin 10 to top
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
