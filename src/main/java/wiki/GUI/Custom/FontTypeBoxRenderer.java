
package wiki.GUI.Custom;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;


/** Rendered della ComboBox per la selezione del tipo di font */
public class FontTypeBoxRenderer extends DefaultListCellRenderer {
    private Map<String, String> fontTypeMap;

    public FontTypeBoxRenderer(Map<String, String> fontSizeMap) {
        this.fontTypeMap = fontSizeMap;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (Objects.equals(fontTypeMap.get(value), "<h1></h1>"))
            setText("<html>" + String.format("<h1>%s</h1>", value) + "</html>");
        else if (Objects.equals(fontTypeMap.get(value), "<h2></h2>"))
            setText("<html>" + String.format("<h2>%s</h2>", value) + "</html>");
        else
            setText("<html>" + value + "</html>");

        // Imposta il colore di sfondo quando l'elemento è selezionato
        setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

        return this;
    }

}
