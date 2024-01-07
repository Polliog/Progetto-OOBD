package wiki.DAOImplementations;

import wiki.DAO.IPageDAO;
import wiki.Models.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Statement;
import java.util.Objects;

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

            // Iterate over each line in the new text and compare it with the old text to find the differences
            // if the line is equal to the old one save with type 0
            // if the line is new save with type 1
            // if the line is modified save with type 2
            // if the line is deleted save with type 3

            //use insertUpdatedText(updateId, line, i, type) to insert the new record
            //type is an integer

            for (int i = 0; i < newLines.length; i++) {
                String line = newLines[i];
                if (i < oldLines.length) {
                    // Check if the line is equal to the old one
                    if (line.equals(oldLines[i])) {
                        // Save with type 0
                        insertUpdatedText(updateId, line, i, 0);
                    } else {
                        // Save with type 2
                        insertUpdatedText(updateId, line, i, 2);
                    }
                } else {
                    // Save with type 1
                    insertUpdatedText(updateId, line, i, 1);
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

    private void insertUpdatedText(int updateId, String text, int orderNum, int type) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            var pstmt = conn.prepareStatement("INSERT INTO UpdatedText (update_id, text, order_num, type) VALUES (?, ?, ?, ?)");
            pstmt.setInt(1, updateId);
            pstmt.setString(2, text);
            pstmt.setInt(3, orderNum);
            pstmt.setInt(4, type);
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

    public void approveChanges(Update update, User user) throws SQLException {
        // Update the page with the new text and save the old text in the oldText table
        // Set the status of the update to 1

        if (!Objects.equals(user.getUsername(), update.getPage().getAuthor())) {
            JOptionPane.showMessageDialog(null, "Non sei l'autore della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the old page
        int oldPageId = update.getPage().getId();
        Page oldPage = fetchPage(oldPageId);
        // Get the new text
        String newText = update.getAllContentStrings();
        // Get the old text
        String oldText = oldPage.getAllContent();


        // Save the old text in the oldText table
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("INSERT INTO OldText (text, update_id) VALUES (?, ?)");
                pstmt.setString(1, oldText);
                pstmt.setInt(2,update.getId());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio del vecchio testo", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Update the page with the new text
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("DELETE FROM PageText WHERE page_id = ?");
                pstmt.setInt(1, oldPage.getId());
                pstmt.executeUpdate();
                pstmt.close();

                for (int i = 0; i < newText.split("\n").length; i++) {
                    pstmt = conn.prepareStatement("INSERT INTO PageText (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
                    pstmt.setInt(1, i);
                    //check if text type is 0
                    if (update.getContentStrings().get(i).getType() == 0) {
                        String text = oldPage.getLine(i);
                        //check if text is null
                        if (text == null) {
                            text = "";
                        }
                        pstmt.setString(2, text);
                    } else {
                        String text = newText.split("\n")[i];
                        //check if text is null
                        if (text == null) {
                            text = oldPage.getLine(i);
                            if (text == null) {
                                text = "";
                            }
                        }
                        pstmt.setString(2, text);
                    }
                    pstmt.setInt(3, oldPage.getId());
                    pstmt.setString(4, update.getAuthor());
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
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the status of the update to 1
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE `update` SET status = 1 WHERE id = ?");
                pstmt.setInt(1, update.getId());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato dell'aggiornamento", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        //for every notification with update_id = update.getId() set status to 1
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE notifications SET status = 1 WHERE update_id = ? AND type = 0 AND user = ?");
                pstmt.setInt(1, update.getId());
                pstmt.setString(2, user.getUsername());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refuseChanges(Update update, User user) throws SQLException {
        if (!Objects.equals(user.getUsername(), update.getPage().getAuthor())) {
            JOptionPane.showMessageDialog(null, "Non sei l'autore della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the old page
        int oldPageId = update.getPage().getId();
        Page oldPage = fetchPage(oldPageId);
        String oldText = oldPage.getAllContent();

        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("INSERT INTO OldText (text, update_id) VALUES (?, ?)");
                pstmt.setString(1, oldText);
                pstmt.setInt(2, update.getId());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio del vecchio testo", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the status of the update to 0
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE `update` SET status = 0 WHERE id = ?");
                pstmt.setInt(1, update.getId());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato dell'aggiornamento", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE notifications SET status = 1 WHERE update_id = ? AND type = 0 AND user = ?");
                pstmt.setInt(1, update.getId());
                pstmt.setString(2, user.getUsername());
                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato della notifica", "Errore", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
