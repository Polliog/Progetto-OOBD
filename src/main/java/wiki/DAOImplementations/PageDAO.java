package wiki.DAOImplementations;

import wiki.DAO.IPageDAO;
import wiki.Models.PaginationPage;
import wiki.Models.User;
import wiki.Models.Page;
import wiki.Models.PageContentString;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;

import database.DatabaseConnection;

import javax.swing.*;


public class PageDAO implements IPageDAO {
    public void insertPage(String title, ArrayList<String> content, User user) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // Insert page
            var pstmt = conn.prepareStatement("INSERT INTO Page (title, author, creation) VALUES (?, ?, ?)");
            pstmt.setString(1, title);
            pstmt.setString(2, user.getUsername());
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));

            // Execute query and get id
            pstmt.executeUpdate();
            pstmt.close();

            // Get id
            pstmt = conn.prepareStatement("SELECT id FROM Page WHERE title = ?");
            pstmt.setString(1, title);
            var rs = pstmt.executeQuery();
            rs.next();

            int pageId = rs.getInt("id");

            // Insert content
            pstmt = conn.prepareStatement("INSERT INTO PageText (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
            for (int i = 0; i < content.size(); i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2, content.get(i));
                pstmt.setInt(3, pageId);
                pstmt.setString(4, user.getUsername());
                pstmt.executeUpdate();
            }
            pstmt.close();
            conn.commit();

            JOptionPane.showMessageDialog(null, "Pagina creata con successo", "Successo", JOptionPane.INFORMATION_MESSAGE);

        }
        catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public Page fetchPage(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            var pstmt = conn.prepareStatement("SELECT * FROM Page WHERE id = ?");
            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();
            rs.next();

            if (rs.getRow() == 0)
                return null;

            String title = rs.getString("title");
            String author = rs.getString("author");
            java.sql.Timestamp creation = rs.getTimestamp("creation");

            Page page = new Page(id, title, author, creation);

            //get text and links if present
            pstmt = conn.prepareStatement("SELECT * FROM PageText WHERE page_id = ? ORDER BY order_num");
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int textId = rs.getInt("id");
                String text = rs.getString("text");
                String textAuthor = rs.getString("author");
                int order = rs.getInt("order_num");

                page.addContent(new PageContentString(textId, text,order, textAuthor));

            }
            return page;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
    }

    public PaginationPage fetchPages(String q, int page, int limit) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            var pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM Page WHERE title LIKE ?");
            pstmt.setString(1, "%" + q + "%");
            var rs = pstmt.executeQuery();
            rs.next();

            int count = rs.getInt("count");

            pstmt = conn.prepareStatement("SELECT * FROM Page WHERE title LIKE ? ORDER BY creation DESC LIMIT ? OFFSET ?");
            pstmt.setString(1, "%" + q + "%");
            pstmt.setInt(2, limit);
            pstmt.setInt(3, (page - 1) * limit);
            rs = pstmt.executeQuery();

            ArrayList<Page> pages = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt("id");
                pages.add(fetchPage(id));
            }

            return new PaginationPage(pages, count, page, limit);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle pagine", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
    }

    public void compareAndSaveEdit(Page oldPage, String newText, User user) throws SQLException {

        //se l'autore e' uguale all'ultimo autore a chi ha modificato la pagina
        if (oldPage.getAuthor().equals(user.getUsername())) {
            //aggiorna direttamente il testo

            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            //rimuovi il vecchio testo e inserisci il nuovo

            try {
                var pstmt = conn.prepareStatement("DELETE FROM PageText WHERE page_id = ?");
                pstmt.setInt(1, oldPage.getId());
                pstmt.executeUpdate();
                pstmt.close();

                for (int i = 0; i < newText.split("\n").length; i++) {
                    pstmt = conn.prepareStatement("INSERT INTO PageText (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
                    pstmt.setInt(1, i);
                    pstmt.setString(2, newText.split("\n")[i]);
                    pstmt.setInt(3, oldPage.getId());
                    pstmt.setString(4, user.getUsername());
                    pstmt.executeUpdate();
                    pstmt.close();
                }

                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            catch (Exception e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } else {
            // Split the old and new text into lines
            String[] oldLines = oldPage.getAllContent().split("\n");
            String[] newLines = newText.split("\n");

            // Create a new record in the Update table
            int updateId = insertUpdate(oldPage.getId(), user.getUsername(), null);

            // Iterate over each line in the new text
            for (int i = 0; i < newLines.length; i++) {
                // If the line exists in the old text and is different, save it
                if (i < oldLines.length && !oldLines[i].equals(newLines[i])) {
                    System.out.println("Modified line " + (i + 1) + ": " + newLines[i]);
                    // Save the modified line in the database
                    insertUpdatedText(updateId, oldPage.getContent().get(i).getId(), newLines[i], i);
                }
                // If the line does not exist in the old text (i.e., it's a new line), save it
                else if (i >= oldLines.length) {
                    System.out.println("New line " + (i + 1) + ": " + newLines[i]);
                    // Save the new line in the database
                    insertUpdatedText(updateId, -1, newLines[i], i);
                }
            }

            // Check for lines that have been moved up or down
            for (int i = 0; i < oldLines.length; i++) {
                // If the line exists in the new text and is different, save it
                if (i < newLines.length && !oldLines[i].equals(newLines[i])) {
                    // Check the previous and next lines
                    if (i > 0 && oldLines[i].equals(newLines[i - 1])) {
                        System.out.println("Line " + (i + 1) + " moved up: " + oldLines[i]);
                        // Save the moved line in the database
                        insertUpdatedText(updateId, oldPage.getContent().get(i).getId(), oldLines[i], i);
                    } else if (i < oldLines.length - 1 && i + 1 < newLines.length && oldLines[i].equals(newLines[i + 1])) {
                        System.out.println("Line " + (i + 1) + " moved down: " + oldLines[i]);
                        // Save the moved line in the database
                        insertUpdatedText(updateId, oldPage.getContent().get(i).getId(), oldLines[i], i);
                    }
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Modifiche salvate", "Successo", JOptionPane.INFORMATION_MESSAGE);
    }

    private int insertUpdate(int pageId, String username, Integer status) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        int updateId = -1;
        try {
            var pstmt = conn.prepareStatement("INSERT INTO `update` (page_id, author, status) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pageId);
            pstmt.setString(2, username);
            pstmt.setObject(3, status); // setObject allows for null values
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                updateId = rs.getInt(1);
            }
            pstmt.close();
        }
        catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante l'inserimento dell'aggiornamento", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
        return updateId;
    }

    private void insertUpdatedText(int updateId, int textId, String text, int orderNum) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            var pstmt = conn.prepareStatement("INSERT INTO UpdatedText (update_id, text_id, text, order_num) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, updateId);
            pstmt.setInt(2, textId == -1 ? null : textId);
            pstmt.setString(3, text);
            pstmt.setInt(4, orderNum);
            pstmt.executeUpdate();
            pstmt.close();
        }
        catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante l'inserimento del testo aggiornato", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
        finally {
            conn.setAutoCommit(true);
        }

    }
}
