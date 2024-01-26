package wiki.GUI;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

public class FontSizeComboBox extends JComboBox {
    public final int[] fontSizeArray = {12, 14, 16, 18, 20, 24, 28, 32, 36};

    private JTextComponent textComponent;

    public FontSizeComboBox() {
        for (int size : fontSizeArray)
            addItem(size);

        setSelectedItem(fontSizeArray[3]);
        setRenderer(new FontSizeBoxRenderer());
    }

    public void init(JTextComponent textComponent) {
        this.textComponent = textComponent;
        addActionListener(e -> onFontSizeSelected());

        onFontSizeSelected();
    }

    private void onFontSizeSelected() {
        textComponent.setFont(new Font(textComponent.getFont().getFamily(), Font.PLAIN, (int)this.getSelectedItem()));
    }
}
