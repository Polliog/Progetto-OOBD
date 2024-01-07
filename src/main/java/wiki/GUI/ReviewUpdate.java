package wiki.GUI;

import wiki.Controllers.WikiController;
import wiki.Models.Notification;
import wiki.Models.Page;
import wiki.Models.Update;
import wiki.Models.UpdateContentString;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ReviewUpdate extends PageBase {
    private JPanel reviewUpdateView;
    private JButton backBtn;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JScrollPane updatesView;
    private JButton indietroButton;
    private JButton avantiButton;
    private JButton accettaTuttoButton;
    private JLabel paginationLabel;

    private int updateIndex = 0;

    private boolean multipleUpdates = false;
    ArrayList<Update> updates = new ArrayList<>();

    Page actualPage;

    public ReviewUpdate(WikiController wikiController, PageBase frame, Update update, boolean updatable) {
        super(wikiController, frame);


        backBtn.addActionListener(e -> onBack());

        actualPage = wikiController.fetchPage(update.getPage().getId());
        //check if there are multiple updates for the same page
        ArrayList<Notification> notifications = wikiController.getLoggedUser().getNotifications(-1);

        for (Notification notification : notifications) {
            if (notification.getUpdate().getStatus() != 2) {
                continue;
            }

            if (notification.getUpdate().getPage().getId() == update.getPage().getId()) {
                updates.add(notification.getUpdate());

                if (updates.size() > 1) {
                    multipleUpdates = true;
                }
            }
        }


        if (multipleUpdates) {
            statusLabel.setText("Ci sono " + updates.size() + " modifiche in sospeso per questa pagina.");
            titleLabel.setText("Revisione delle modifiche alla pagina \"" + update.getPage().getTitle() + "\"");


            initPagination();

            indietroButton.addActionListener(e -> previousUpdate());
            avantiButton.addActionListener(e -> nextUpdate());
            indietroButton.setVisible(true);
            avantiButton.setVisible(true);
            accettaTuttoButton.setVisible(true);
            accettaTuttoButton.addActionListener(e -> acceptAllUpdates());
        } else {
            titleLabel.setText("Revisione della modifica di " + update.getAuthor() + " alla pagina \"" + update.getPage().getTitle() + "\"");
            updates.add(update);
        }

        if (multipleUpdates) {
            //mostra un popup che dice che visto che ci sono più modifiche, verranno mostrate tutte di seguito
            JOptionPane.showMessageDialog(null, "Ci sono " + updates.size() + " modifiche in sospeso per questa pagina. Verranno mostrate tutte di seguito.", "Modifiche multiple", JOptionPane.INFORMATION_MESSAGE);
        }


        initGUI( true,new Dimension(550, 400));
        add(reviewUpdateView);
        setVisible(true);

        //reverse updates order
        ArrayList<Update> reversedUpdates = new ArrayList<>();
        for (int i = updates.size() - 1; i >= 0; i--) {
            reversedUpdates.add(updates.get(i));
        }
        updates = reversedUpdates;

        createUIComponents(updates.get(updateIndex));
    }


    private void onBack() {
        PageBase Notifications = new UserNotifications(wikiController, this);
        this.setVisible(false);
        this.dispose();
    }

    private void initPagination() {
        if (updateIndex == 0) {
            indietroButton.setEnabled(false);
        } else {
            indietroButton.setEnabled(true);
        }

        if (updateIndex == updates.size() - 1) {
            avantiButton.setEnabled(false);
        } else {
            avantiButton.setEnabled(true);
        }

        paginationLabel.setText("Modifica " + (updateIndex + 1) + " di " + updates.size());
    }

    private void nextUpdate() {
        updateIndex++;
        initPagination();
        createUIComponents(updates.get(updateIndex));
    }

    private void previousUpdate() {
        updateIndex--;
        initPagination();
        createUIComponents(updates.get(updateIndex));
    }

    private void createUIComponents(Update update) {
        // Creazione del pannello principale
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Creazione del pannello per il testo
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(1, 2));
        textPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Aggiunta del bordo

        // Creazione delle etichette
        JLabel currentTextLabel = new JLabel("Testo Attuale");
        JLabel newTextLabel = new JLabel("Testo Nuovo");

        // Creazione delle aree di testo
        JTextPane currentText = new JTextPane();
        currentText.setContentType("text/html"); // Impostazione del tipo di contenuto su HTML
        currentText.setText(actualPage.getAllContentHtml()); // Utilizzo del contenuto della pagina come testo attuale
        currentText.setEditable(false);
        currentText.setFont(new Font("Arial", Font.PLAIN, 14)); // Impostazione del font

        JTextPane newText = new JTextPane();
        newText.setContentType("text/html"); // Impostazione del tipo di contenuto su HTML
        newText.setEditable(false);
        newText.setFont(new Font("Arial", Font.PLAIN, 14)); // Impostazione del font

        // Creazione dei pannelli per le etichette e le aree di testo
        JPanel currentTextPanel = new JPanel(new BorderLayout());
        currentTextPanel.add(currentTextLabel, BorderLayout.NORTH);
        currentTextPanel.add(currentText, BorderLayout.CENTER);

        JPanel newTextPanel = new JPanel(new BorderLayout());
        newTextPanel.add(newTextLabel, BorderLayout.NORTH);
        newTextPanel.add(newText, BorderLayout.CENTER);

        // Confronto del vecchio e del nuovo testo
        ArrayList<UpdateContentString> contentStrings = update.getContentStrings();
        StringBuilder allContent = new StringBuilder();
        for (UpdateContentString contentString : contentStrings) {
            String line = contentString.getText();

            if (contentString.getType() == 0 || contentString.getType() == 3) {
                line = actualPage.getLine(contentString.getOrder_num());
            }

            allContent.append(line).append("<br>");

            switch (contentString.getType()) {
                case 0: // Il testo è uguale, non colorare
                    break;
                case 2:  // Il testo è nuovo, quindi colora la riga di verde
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='#CCCC00'>");
                    allContent.append("</font>");
                    break;
                case 1:// Il testo è diverso, quindi colora la riga di giallo scuro
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='green'>");
                    allContent.append("</font>");
                    break;
                case 3: // Il testo è stato rimosso, quindi colora la riga di rosso
                    allContent.insert(allContent.length() - line.length() - 4, "<font color='red'>");
                    allContent.append("</font>");
                    break;
            }
        }

        newText.setText(allContent.toString());

        // Aggiunta dei pannelli al pannello del testo
        textPanel.add(currentTextPanel);
        textPanel.add(newTextPanel);

        // Creazione del pannello per i pulsanti
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Creazione dei pulsanti
        JButton acceptButton = new JButton("Accetta");


        acceptButton.addActionListener(e -> {
            boolean result = wikiController.acceptUpdate(update, false);

            if (result) {
                actualPage = wikiController.fetchPage(update.getPage().getId()); // Aggiornamento della pagina
                update.setStatus(1);
                update.setPage(actualPage);
                if (!multipleUpdates) {

                    PageBase pageView = new UserNotifications(wikiController, this);
                    this.setVisible(false);
                    this.dispose();
                } else {

                    if (updateIndex == updates.size() - 1) {
                        PageBase pageView = new UserNotifications(wikiController, this);
                        this.setVisible(false);
                        this.dispose();
                    } else {
                        nextUpdate();
                    }

                }
            }

        });

        JButton rejectButton = new JButton("Rifiuta");

        rejectButton.addActionListener(e -> {
            boolean result = wikiController.refuseUpdate(update);

            if (result) {
                actualPage = wikiController.fetchPage(update.getPage().getId()); // Aggiornamento della pagina
                update.setStatus(0);
                update.setPage(actualPage);
                if (!multipleUpdates) {

                    PageBase pageView = new UserNotifications(wikiController, this);
                    this.setVisible(false);
                    this.dispose();
                } else {

                    if (updateIndex == updates.size() - 1) {
                        PageBase pageView = new UserNotifications(wikiController, this);
                        this.setVisible(false);
                        this.dispose();
                    } else {
                        nextUpdate();
                    }

                }
            }
        });

        // Aggiunta dei pulsanti al pannello dei pulsanti
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);

        // Aggiunta dei pannelli al pannello principale
        mainPanel.add(textPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        updatesView.setViewportView(mainPanel);
    }

    private void acceptAllUpdates() {
        int n = JOptionPane.showConfirmDialog(null, "Sei sicuro di voler accettare tutte le modifiche?", "Accetta tutte le modifiche", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            for (Update update : updates) {
                boolean result = wikiController.acceptUpdate(update, true);

                if (result) {
                    actualPage = wikiController.fetchPage(update.getPage().getId()); // Aggiornamento della pagina
                    update.setStatus(1);
                    update.setPage(actualPage);
                }
            }

            PageBase pageView = new UserNotifications(wikiController, this);
            this.setVisible(false);
            this.dispose();
        }
    }
}
