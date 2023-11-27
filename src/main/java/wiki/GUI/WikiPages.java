package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;
import wiki.Models.PaginationPage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class WikiPages extends JPanel {
    private WikiController wikiController;
    private PageView pageView;
    private JPanel WikiPagesView;
    private JTextField searchField;
    private JButton cercaButton;
    private JButton previousPageBtn;
    private JButton nextPageBtn;
    private JLabel paginationLabel;
    private JPanel WikiListContent;
    private JScrollPane wikiListScroll;

    private int currentPage = 1;
    private int totalPages = 0;

    public WikiPages(WikiController wikiController, PageView pageView) {
        this.wikiController = wikiController;
        this.pageView = pageView;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Imposta un layout manager
        add(WikiPagesView); // Aggiungi un componente al pannello

        cercaButton.addActionListener(e -> fetchData());
        previousPageBtn.addActionListener(e -> previousPage());
        nextPageBtn.addActionListener(e -> nextPage());
        searchField.addActionListener(e -> fetchData());
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
                      int finalI1 = i;
                      contentLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                          public void mouseClicked(java.awt.event.MouseEvent evt) {
                              try {
                                  //check if the link is a url or a page id
                                  if (page.getContent().get(finalI).link.startsWith("http") || page.getContent().get(finalI).link.startsWith(" http")) {
                                      if (Desktop.isDesktopSupported()) {
                                          Desktop.getDesktop().browse(java.net.URI.create(page.getContent().get(finalI1).link));
                                      }
                                  }
                                  else {
                                      //pageView.textIdField.setText(content.link);
                                      pageView.fetchData(page.getId());

                                      JTabbedPane tabbedPane = (JTabbedPane) getParent();
                                      tabbedPane.setSelectedIndex(3);
                                  }
                              } catch (java.io.IOException e) {
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
                pageView.fetchData(page.getId());

                JTabbedPane tabbedPane = (JTabbedPane) getParent();
                tabbedPane.setSelectedIndex(3);
            });
            pagePanel.add(pageButton);

            panel.add(pagePanel);
        }

        wikiListScroll.setViewportView(panel);
        WikiListContent.add(wikiListScroll);

        //wikiListScroll.revalidate();
        //wikiListScroll.repaint();
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
