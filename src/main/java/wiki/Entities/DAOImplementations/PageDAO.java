package wiki.Entities.DAOImplementations;

import wiki.Entities.DAO.IPageDAO;
import wiki.Models.Utente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import database.DatabaseConnection;

import javax.swing.*;


public class PageDAO implements IPageDAO {

    public void insertPage(String title, ArrayList<String> content, Utente utente) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // Insert page
            var pstmt = conn.prepareStatement("INSERT INTO Page (title, author, creation) VALUES (?, ?, ?)");
            pstmt.setString(1, title);
            pstmt.setString(2, utente.getUsername());
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
                pstmt.setString(4, utente.getUsername());
                pstmt.executeUpdate();


                for (String word : content.get(i).split(" ")) {
                    if (word.startsWith("{") && word.endsWith("}")) {
                        //get text id
                        var pstmt2 = conn.prepareStatement("SELECT id FROM PageText WHERE text = ? AND page_id = ? AND order_num = ?");
                        pstmt2.setString(1, content.get(i));
                        pstmt2.setInt(2, pageId);
                        pstmt2.setInt(3, i);
                        rs = pstmt2.executeQuery();
                        rs.next();
                        int textId = rs.getInt("id");

                        String link = word.substring(1, word.length() - 1);
                        //save only the part after the :
                        if (link.contains(":")) {
                            link = link.split(":")[1];
                        }
                        pstmt2 = conn.prepareStatement("INSERT INTO TextLink (pagetext_id, link) VALUES (?, ?)");
                        pstmt2.setInt(1, textId);
                        pstmt2.setString(2, link);
                        pstmt2.executeUpdate();
                        pstmt2.close();
                    }
                }
            }
            pstmt.close();
            conn.commit();


            JOptionPane.showMessageDialog(null, "Pagina creata con successo", "Successo", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            conn.rollback();
            JOptionPane.showMessageDialog(null, "Errore durante la creazione della pagina", "Errore", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw e;
        }
        finally {
            conn.setAutoCommit(true);
        }
    }
}
