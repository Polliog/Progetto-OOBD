
package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class FontSizeBoxRenderer extends DefaultListCellRenderer {

    public FontSizeBoxRenderer() {}

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(String.format("<html><b>%s px</b></html>", value));
        return this;
    }
}