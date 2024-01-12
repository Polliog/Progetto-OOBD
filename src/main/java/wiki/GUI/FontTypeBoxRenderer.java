
package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FontTypeBoxRenderer extends DefaultListCellRenderer {
    private Map<String, String> fontTypeMap;

    public FontTypeBoxRenderer(Map<String, String> fontSizeMap) {
        this.fontTypeMap = fontSizeMap;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (fontTypeMap.get(value) == "<h1></h1>")
            setText("<html>" + String.format("<h1>%s</h1>", value) + "</html>");
        else if (fontTypeMap.get(value) == "<h2></h2>")
            setText("<html>" + String.format("<h2>%s</h2>", value) + "</html>");
        else
            setText("<html>" + value + "</html>");

        return this;
    }

}
