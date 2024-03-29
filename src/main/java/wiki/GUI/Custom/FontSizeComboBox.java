package wiki.GUI.Custom;

import wiki.GUI.Custom.FontSizeBoxRenderer;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;


/** ComboBox per la selezione della grandezza del font */
public class FontSizeComboBox extends JComboBox {
    private final int[] fontSizeArray = {10, 12, 14, 16, 18, 20, 24, 28, 32, 36};

    private ArrayList<JTextComponent> textComponents;

    public FontSizeComboBox() {
        textComponents = new ArrayList<>();

        for (int size : fontSizeArray)
            addItem(size);

        setSelectedItem(fontSizeArray[3]);
        setRenderer(new FontSizeBoxRenderer());
    }

    public void init(JTextComponent textComponent) {
        textComponents.add(textComponent);
        addActionListener(e -> onFontSizeSelected());

        onFontSizeSelected();
    }

    public void init(ArrayList<JTextComponent> textComponents) {
        this.textComponents.addAll(textComponents);
        addActionListener(e -> onFontSizeSelected());

        onFontSizeSelected();
    }


    private void onFontSizeSelected() {
        for (JTextComponent textComponent : textComponents)
            textComponent.setFont(new Font(textComponent.getFont().getFamily(), Font.PLAIN, (int)this.getSelectedItem()));
    }

}
