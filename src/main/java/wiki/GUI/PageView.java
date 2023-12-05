package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Page;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class PageView extends PageBase {
    private JPanel PageViewPanel;
    private JLabel PageViewTitleLabel;
    private JScrollPane PageViewContentPanel;

    private JLabel AuthorLabel;
    private JLabel CreatedLabel;
    private JButton backBtn;

    private Page page = null;


    public PageView(WikiController wikiController, PageBase frame, int id) {
        super(wikiController, frame);
        initGUI();

        fetchData(id);


        add(PageViewPanel);

        backBtn.addActionListener(e -> {
            // Qui arriviamo sempre da WikiPages
            frame.setVisible(true);
            this.dispose();
        });
    }

    // public for testing !!!!!!
    public void fetchData(int id) {
        //  if (textIdField.getText().isEmpty()) {
        //      JOptionPane.showMessageDialog(null, "Inserisci un id", "Errore", JOptionPane.ERROR_MESSAGE);
        //      return;
        //  }

       this.page = wikiController.fetchPage(id);

       if (this.page == null) {
           JOptionPane.showMessageDialog(null, "Pagina non trovata", "Errore", JOptionPane.ERROR_MESSAGE);
           return;
       }


       PageViewTitleLabel.setText(this.page.getTitle());
       AuthorLabel.setText("Autore: " + this.page.getAuthor());
        //created label is formatted as "Creato il: dd/MM/yyyy"
        CreatedLabel.setText("Creato il: " + this.page.getCreation().toString().substring(0, 10));
        createUIComponents();
    }

    private void createUIComponents() {
        // Create a new panel to hold the labels
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Add a label for each content
        this.page.getContent().forEach(content -> {

            JLabel label = new JLabel(content.content);
            //if content is empty set the label as empty
            if (Objects.equals(content.content, ""))
                label.setText(" ");


            //label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            if (content.content.startsWith("{") && content.content.endsWith("}")) {
                //the link is formatte as {text:link}
                //set in the label only the text
                label.setText(content.content.substring(content.content.indexOf("{") + 1, content.content.indexOf(":")));

                label.setForeground(Color.BLUE.darker());
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                label.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        try {
                            //get link from the content and remove the { and }
                            String link = content.content.substring(content.content.indexOf(":") + 1, content.content.indexOf("}"));
                            //check if the link is a url or a page id
                            if (link.startsWith("http") || link.startsWith(" http")) {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().browse(java.net.URI.create(link));
                                }
                            }
                            else {
                                fetchData(page.getId());

                            }
                        } catch (java.io.IOException e) {
                            JOptionPane.showMessageDialog(null, "Errore durante l'apertura del link", "Errore", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            }

            contentPanel.add(label);
        });

        // Set the new panel as the view for the scroll pane
        PageViewContentPanel.setViewportView(contentPanel);

        // Revalidate and repaint the scroll pane
        PageViewContentPanel.revalidate();
        PageViewContentPanel.repaint();
    }


}
