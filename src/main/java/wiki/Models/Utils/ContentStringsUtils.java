package wiki.Models.Utils;

import wiki.Models.PageContentString;
import wiki.Models.UpdateContentString;

import java.util.ArrayList;

/**
 * La classe ContentStringsUtils fornisce metodi di utilità per lavorare con le stringhe di contenuto delle pagine.
 */
public final class ContentStringsUtils {
    /**
     * Restituisce tutte le stringhe di contenuto di una pagina.
     *
     * @param contentStrings Le stringhe di contenuto della pagina.
     * @return Una stringa con tutte le stringhe di contenuto della pagina.
     */
    public static String getAllPageContentStrings(ArrayList<PageContentString> contentStrings) {
        StringBuilder content = new StringBuilder();
        for (PageContentString contentString : contentStrings) {
            content.append(contentString.getText()).append("\n");
        }
        return content.toString();
    }

    /**
     * Restituisce tutte le stringhe di contenuto di un aggiornamento.
     *
     * @param updateContentStrings Le stringhe di contenuto dell'aggiornamento.
     * @return Una stringa con tutte le stringhe di contenuto dell'aggiornamento.
     */
    public static String getAllUpdateContentStrings(ArrayList<UpdateContentString> updateContentStrings) {
        StringBuilder content = new StringBuilder();
        for (UpdateContentString updateContentString : updateContentStrings) {
            content.append(updateContentString.getText()).append("\n");
        }
        return content.toString();
    }

    /**
     * Restituisce la stringa di contenuto di una pagina a un indice specifico.
     *
     * @param lineIndex L'indice della stringa di contenuto.
     * @param contentStrings Le stringhe di contenuto della pagina.
     * @return La stringa di contenuto alla posizione specificata.
     */
    public static String getPageContentLine(int lineIndex, ArrayList<PageContentString> contentStrings) {
        for (PageContentString c : contentStrings) {
            if (c.getOrderNum() == lineIndex) {
                return c.getText();
            }
        }
        return null;
    }

    /**
     * Restituisce tutte le stringhe di contenuto di una pagina in formato HTML.
     *
     * @param contentStrings Le stringhe di contenuto della pagina.
     * @return Una stringa con tutte le stringhe di contenuto della pagina in formato HTML.
     */
    public static String getAllPageContentHtml(ArrayList<PageContentString> contentStrings) {
        StringBuilder sb = new StringBuilder();
        for (PageContentString contentString : contentStrings) {
            sb.append(contentString.getText()).append("<br>");
        }
        return sb.toString();
    }

    /**
     * Restituisce le stringhe di contenuto di un aggiornamento confrontate e formattate in HTML.
     *
     * @param contentStrings Le stringhe di contenuto dell'aggiornamento.
     * @return Una stringa con le stringhe di contenuto dell'aggiornamento confrontate e formattate in HTML.
     */
    public static String getUpdateComparedContentHtml(ArrayList<UpdateContentString> contentStrings) {
        StringBuilder str = new StringBuilder();

        for (UpdateContentString contentString : contentStrings) {
            String line = contentString.getText();

            switch (contentString.getType()) {
                // Il testo è uguale, non colorare
                case UpdateContentString.TYPE_SAME:
                    str.append(line);
                    break;
                // Il testo è diverso, quindi colora la riga di giallo scuro
                case UpdateContentString.TYPE_DIFFERENT:
                    str.append("<font color='green'>");
                    str.append(line);
                    str.append("</font>");
                    break;
                // Il testo è nuovo, quindi colora la riga di verde
                case UpdateContentString.TYPE_ADDED:
                    str.append("<font color='#CCCC00'>");
                    str.append(line);
                    str.append("</font>");
                    break;
                // Il testo è stato rimosso, quindi colora la riga di rosso
                case UpdateContentString.TYPE_REMOVED:
                    str.append("<font color='red'>");
                    str.append(line);
                    str.append("</font>");
                    break;
            }
            str.append("<br>");
        }

        return str.toString();
    }
}