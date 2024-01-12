package wiki.GUI;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class FontSizeComboBox extends JComboBox {
    private JTextComponent textComponent;
    private int[] fontSizeArray = {12, 14, 16, 18, 20, 24, 28, 32, 36};


    public FontSizeComboBox(JTextComponent textComponent) {
        this.textComponent = textComponent;

        for (int size : fontSizeArray)
            addItem(size);

        setSelectedItem(fontSizeArray[3]);
        setRenderer(new FontSizeBoxRenderer());
        addActionListener(e -> onFontSizeSelected());
    }

    private void onFontSizeSelected() {
        textComponent.setFont(new Font(textComponent.getFont().getFamily(), Font.PLAIN, (int)this.getSelectedItem()));
    }
}
