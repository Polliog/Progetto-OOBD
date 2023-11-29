package wiki.Entities.DAOImplementations;

import wiki.Entities.DAO.IPageDAO;
import wiki.Models.PaginationPage;
import wiki.Models.User;
import wiki.Models.Page;
import wiki.Models.PageContentString;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

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
}
