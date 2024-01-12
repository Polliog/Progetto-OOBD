package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ColorBoxRenderer extends DefaultListCellRenderer {
    private Map<String, Color> colorMap;

    public ColorBoxRenderer(Map<String, Color> colorMap) {
        this.colorMap = colorMap;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel();

        // Applica il colore personalizzato all'elemento corrente
        if (colorMap.containsKey(value)) {
            Color color = colorMap.get(value);

            label.setForeground(color);

            // Aggiungi un quadratino di colore al pannello
            JPanel colorSquare = new JPanel();
            colorSquare.setBackground(color);

            panel.add(colorSquare);
        }

        label.setText(value.toString());
        panel.add(label);

        // Imposta il colore di sfondo quando l'elemento è selezionato
        panel.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());

        return panel;
    }

}
