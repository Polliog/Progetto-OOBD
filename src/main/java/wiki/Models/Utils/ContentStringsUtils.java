package wiki.Models.Utils;

import wiki.Models.PageContentString;
import wiki.Models.UpdateContentString;

import java.util.ArrayList;

public final class ContentStringsUtils {
    public static String getAllPageContentStrings(ArrayList<PageContentString> contentStrings) {
        StringBuilder content = new StringBuilder();
        for (PageContentString contentString : contentStrings) {
            content.append(contentString.getText()).append("\n");
        }
        return content.toString();
    }

    public static String getAllUpdateContentStrings(ArrayList<UpdateContentString> updateContentStrings) {
        StringBuilder content = new StringBuilder();
        for (UpdateContentString updateContentString : updateContentStrings) {
            content.append(updateContentString.getText()).append("\n");
        }
        return content.toString();
    }

    public static String getPageContentLine(int lineIndex, ArrayList<PageContentString> contentStrings) {
        for (PageContentString c : contentStrings) {
            if (c.getOrderNum() == lineIndex) {
                return c.getText();
            }
        }
        return null;
    }

    public static String getAllPageContentHtml(ArrayList<PageContentString> contentStrings) {
        StringBuilder sb = new StringBuilder();
        for (PageContentString contentString : contentStrings) {
            sb.append(contentString.getText()).append("<br>");
        }
        return sb.toString();
    }

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
