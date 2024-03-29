package wiki.DAOImplementations;

import wiki.DAO.IPageDAO;
import wiki.Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import database.DatabaseConnection;
import wiki.Models.Utils.ContentStringsUtils;

import javax.swing.*;


public class PageDAO implements IPageDAO {
    // Page related methods

    public void insertPage(String pageTitle, String pageContent, User user) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Create ArrayList of the pageContent text lines
            ArrayList<String> text = new ArrayList<>();
            Collections.addAll(text, pageContent.split("\n"));

            // Insert page
            var pstmt = conn.prepareStatement("INSERT INTO \"Page\" (title, author) VALUES (?, ?)");
            pstmt.setString(1, pageTitle);
            pstmt.setString(2, user.getUsername());

            // Execute query and get id
            pstmt.executeUpdate();
            pstmt.close();

            // Get id
            pstmt = conn.prepareStatement("SELECT id FROM \"Page\" WHERE title = ?");
            pstmt.setString(1, pageTitle);
            var rs = pstmt.executeQuery();
            rs.next();

            int pageId = rs.getInt("id");

            // Insert content
            pstmt = conn.prepareStatement("INSERT INTO \"PageText\" (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
            for (int i = 0; i < text.size(); i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2, text.get(i));
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
            throw e;
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
    public Page fetchPage(int id) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            var pstmt = conn.prepareStatement("SELECT * FROM \"Page\" WHERE id = ?");
            pstmt.setInt(1, id);
            var rs = pstmt.executeQuery();
            rs.next();

            if (rs.getRow() == 0)
                return null;

            String title = rs.getString("title");
            String author = rs.getString("author");
            java.sql.Timestamp creation_date = rs.getTimestamp("creation_date");

            return new Page(id, title, author, creation_date);
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    public void deletePage(Page page, User user) throws SQLException {
        if (!Objects.equals(user.getUsername(), page.getAuthorName())) {
            JOptionPane.showMessageDialog(null, "Non sei l'autore della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            var pstmt = conn.prepareStatement("DELETE FROM \"Page\" WHERE id = ?");
            pstmt.setInt(1, page.getId());
            pstmt.executeUpdate();
            pstmt.close();
            conn.commit();
        }
        catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante l'eliminazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
    public ArrayList<PageContentString> fetchPageContentStrings(int pageId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            var pstmt = conn.prepareStatement("SELECT * FROM \"PageText\" WHERE page_id = ? ORDER BY order_num");
            pstmt.setInt(1, pageId);
            var rs = pstmt.executeQuery();

            ArrayList<PageContentString> contentStrings = new ArrayList<>();

            while (rs.next()) {
                int textId = rs.getInt("id");
                String text = rs.getString("text");
                int order = rs.getInt("order_num");
                String author = rs.getString("author");
                contentStrings.add(new PageContentString(textId, text, order, author));
            }

            return contentStrings;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento del testo", "Errore", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    public PaginationPage fetchPages(String q, int page, int limit, int type) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            java.sql.PreparedStatement pstmt;

            if (type == 0) {
                pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM \"Page\" WHERE title LIKE ?");
            } else {
                pstmt = conn.prepareStatement("SELECT COUNT(*) AS count FROM \"Page\" WHERE author LIKE ?");
            }

            pstmt.setString(1, "%" + q + "%");
            var rs = pstmt.executeQuery();
            rs.next();

            int count = rs.getInt("count");

            if (type == 0)
                pstmt = conn.prepareStatement("SELECT * FROM \"Page\" WHERE title LIKE ? ORDER BY creation_date DESC LIMIT ? OFFSET ?");
            else
                pstmt = conn.prepareStatement("SELECT * FROM \"Page\" WHERE author LIKE ? ORDER BY creation_date DESC LIMIT ? OFFSET ?");


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
            throw e;
        }
    }

    // PageUpdate related methods
    public ArrayList<PageUpdate> fetchUpdates(int pageId, int status) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        try {
            var pstmt = conn.prepareStatement("SELECT * FROM \"PageUpdate\" WHERE page_id = ? AND status = ? ORDER BY creation_date DESC");
            pstmt.setInt(1, pageId);
            pstmt.setInt(2, status);
            var rs = pstmt.executeQuery();

            ArrayList<PageUpdate> pageUpdates = new ArrayList<>();

            while (rs.next()) {
                int updateId = rs.getInt("id");
                String updateAuthor = rs.getString("author");
                int updateStatus = rs.getInt("status");
                Timestamp updateCreation = rs.getTimestamp("creation_date");
                String updateOldText = rs.getString("old_text");

                //fetch updated text
                pstmt = conn.prepareStatement("SELECT * FROM \"UpdatedText\" WHERE update_id = ? ORDER BY order_num");
                pstmt.setInt(1, updateId);

                var rs2 = pstmt.executeQuery();

                ArrayList<UpdateContentString> contentStrings = new ArrayList<>();

                rs2 = pstmt.executeQuery();
                while (rs2.next()) {
                    contentStrings.add(new UpdateContentString(
                            rs2.getInt("id"),
                            rs2.getString("text"),
                            rs2.getInt("order_num"),
                            rs2.getInt("type")));
                }

                pageUpdates.add(new PageUpdate(
                        updateId,
                        fetchPage(pageId),
                        updateAuthor,
                        updateStatus,
                        updateCreation,
                        updateOldText));
            }

            return pageUpdates;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento delle modifiche", "Errore", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }
    public void savePageUpdate(Page oldPage, String newText, User user) throws SQLException {
        // Aggiorna direttamente il testo della pagina se è l'autore a tentare una modifica
        if (oldPage.getAuthorName().equals(user.getUsername())) {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Rimozione del vecchio testo e aggiornamento con il nuovo
            try {
                var pstmt = conn.prepareStatement("DELETE FROM \"PageText\" WHERE page_id = ?");
                pstmt.setInt(1, oldPage.getId());
                pstmt.executeUpdate();
                pstmt.close();

                for (int i = 0; i < newText.split("\n").length; i++) {
                    pstmt = conn.prepareStatement("INSERT INTO \"PageText\" (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
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
                throw e;
            }
            catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                throw e;
            }
            finally {
                conn.setAutoCommit(true);
            }

            return;
        }
        // Altrimenti, crea un nuovo aggiornamento

        // Inserisci il testo della modifica
        var oldLinesStrings = fetchPageContentStrings(oldPage.getId());

        String[] oldLines = ContentStringsUtils.getAllPageContentStrings(oldLinesStrings).split("\n");
        String[] newLines = newText.split("\n");

        // Crea un nuovo aggiornamento
        int updateId = insertUpdate(oldPage.getId(), user.getUsername());

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

        JOptionPane.showMessageDialog(null, "Modifiche salvate", "Successo", JOptionPane.INFORMATION_MESSAGE);
    }
    public ArrayList<UpdateContentString> fetchPageUpdateContentStrings(int updateId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        try {
            var pstmt = conn.prepareStatement("SELECT * FROM \"UpdatedText\" WHERE update_id = ? ORDER BY order_num");
            pstmt.setInt(1, updateId);
            var rs = pstmt.executeQuery();

            ArrayList<UpdateContentString> contentStrings = new ArrayList<>();

            while (rs.next()) {
                int updatedTextId = rs.getInt("id");
                String updatedText = rs.getString("text");
                int order = rs.getInt("order_num");
                int type = rs.getInt("type");
                contentStrings.add(new UpdateContentString(updatedTextId, updatedText, order, type));
            }

            return contentStrings;
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Errore durante il caricamento del testo aggiornato", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
    }
    public int getUpdateRequestCount(String username, int pageId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) as count FROM \"PageUpdate\" WHERE page_id = ? AND author = ?");
        pstmt.setInt(1, pageId);
        pstmt.setString(2, username);
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        return rs.getInt("count");
    }

    /** Inserisce un nuovo aggiornamento nella tabella "PageUpdate" e restituisce l'id dell'aggiornamento *
     * @param pageId id della pagina
     * @param username username dell'utente che ha richiesto l'aggiornamento
     */
    private int insertUpdate(int pageId, String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        int updateId = -1;
        try {
            var pstmt = conn.prepareStatement("INSERT INTO \"PageUpdate\" (page_id, author) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pageId);
            pstmt.setString(2, username);
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

    /** Inserisce un nuovo testo aggiornato nella tabella "UpdatedText" *
     * @param updateId id dell'aggiornamento
     * @param text testo aggiornato
     * @param orderNum numero di riga
     * @param type tipo di aggiornamento
     */
    private void insertUpdatedText(int updateId, String text, int orderNum, int type) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            var pstmt = conn.prepareStatement("INSERT INTO \"UpdatedText\" (update_id, text, order_num, type) VALUES (?, ?, ?, ?)");
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
            throw e;
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
    public void approveChanges(PageUpdate pageUpdate, User user) throws SQLException {
        // PageUpdate the page with the new text and save the old text in the oldText table
        // Set the status of the pageUpdate to 1
        if (!Objects.equals(user.getUsername(), pageUpdate.getPage().getAuthorName())) {
            JOptionPane.showMessageDialog(null, "Non sei l'autore della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the old page
        int oldPageId = pageUpdate.getPage().getId();
        Page oldPage = fetchPage(oldPageId); // ?
        // Get the new text

        ArrayList<UpdateContentString> updateContentStrings = fetchPageUpdateContentStrings(pageUpdate.getId());
        ArrayList<PageContentString> pageContentStrings = fetchPageContentStrings(oldPageId);

        // The new text in a single string
        String newText = ContentStringsUtils.getAllUpdateContentStrings(updateContentStrings);
        // The old text in a single string
        String oldText = ContentStringsUtils.getAllPageContentStrings(pageContentStrings);


        // PageUpdate the page with the new text
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("DELETE FROM \"PageText\" WHERE page_id = ?");
                pstmt.setInt(1, oldPage.getId());
                pstmt.executeUpdate();
                pstmt.close();

                for (int i = 0; i < newText.split("\n").length; i++) {
                    pstmt = conn.prepareStatement("INSERT INTO \"PageText\" (order_num, text, page_id, author) VALUES (?, ?, ?, ?)");
                    pstmt.setInt(1, i);
                    //check if text type is 0
                    if (updateContentStrings.get(i).getType() == 0) {
                        String text = ContentStringsUtils.getPageContentLine(i, pageContentStrings);
                        //check if text is null
                        if (text == null) {
                            text = "";
                        }
                        pstmt.setString(2, text);
                    } else {
                        String text = newText.split("\n")[i];
                        //check if text is null
                        if (text == null) {
                            text = ContentStringsUtils.getPageContentLine(i, pageContentStrings);
                            if (text == null) {
                                text = "";
                            }
                        }
                        pstmt.setString(2, text);
                    }
                    pstmt.setInt(3, oldPage.getId());
                    pstmt.setString(4, pageUpdate.getAuthorName());
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

        // Set the status of the pageUpdate to 1 and save the oldtext in old_text column
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE \"PageUpdate\" SET status = ?, old_text = ? WHERE id = ?");
                pstmt.setInt(1, PageUpdate.STATUS_ACCEPTED);
                pstmt.setString(2, oldText);
                pstmt.setInt(3, pageUpdate.getId());

                pstmt.executeUpdate();
                pstmt.close();
                conn.commit();
            }
            catch (SQLException e) {
                e.printStackTrace();
                conn.rollback();
                JOptionPane.showMessageDialog(null, "Errore durante il salvataggio dello stato dell'aggiornamento", "Errore", JOptionPane.ERROR_MESSAGE);
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
    public void refuseChanges(PageUpdate pageUpdate, User user) throws SQLException {
        if (!Objects.equals(user.getUsername(), pageUpdate.getPage().getAuthorName())) {
            JOptionPane.showMessageDialog(null, "Non sei l'autore della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int oldPageId = pageUpdate.getPage().getId();
        Page oldPage = fetchPage(oldPageId);

        ArrayList<UpdateContentString> updateContentStrings = fetchPageUpdateContentStrings(pageUpdate.getId());
        ArrayList<PageContentString> pageContentStrings = fetchPageContentStrings(oldPageId);

        // The new text in a single string
        String newText = ContentStringsUtils.getAllUpdateContentStrings(updateContentStrings);
        // The old text in a single string
        String oldText = ContentStringsUtils.getAllPageContentStrings(pageContentStrings);


        // Set the status of the pageUpdate to 0 and save the oldtext in old_text column
        try {
            Connection conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            try {
                var pstmt = conn.prepareStatement("UPDATE \"PageUpdate\" SET status = 0, old_text = ? WHERE id = ?");
                pstmt.setString(1, oldText);
                pstmt.setInt(2, pageUpdate.getId());
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
    }
}
