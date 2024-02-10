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

    public static String getPageUpdateComparedContentHtml(ArrayList<UpdateContentString> contentStrings) {
        StringBuilder str = new StringBuilder();

        for (UpdateContentString contentString : contentStrings) {
            String line = contentString.getText();

            switch (contentString.getType()) {
                case 0: // Il testo è uguale, non colorare
                    str.append(line);
                    break;
                case 2:  // Il testo è nuovo, quindi colora la riga di verde
                    str.append("<font color='#CCCC00'>");
                    str.append(line);
                    str.append("</font>");
                    break;
                case 1:// Il testo è diverso, quindi colora la riga di giallo scuro
                    str.append("<font color='green'>");
                    str.append(line);
                    str.append("</font>");
                    break;
                case 3: // Il testo è stato rimosso, quindi colora la riga di rosso
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
