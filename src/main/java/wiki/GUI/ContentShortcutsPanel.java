package wiki.GUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ContentShortcutsPanel extends JPanel {
    private final Map<String, Color> colorMap;
    private final Map<String, String> fontTypeMap;
    private JTextArea pageContentArea;

    private final JButton linkBtn;
    private final JButton underlineBtn;
    private final JButton italicBtn;
    private final JButton boldBtn;
    private final JComboBox colorComboBox;
    private final JComboBox fontTypeComboBox;


    public ContentShortcutsPanel() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));

        linkBtn = new JButton("<html><font color='#2596BE'>Link</font color></html>");
        this.add(linkBtn);

        underlineBtn = new JButton("<html><u>Underline</u></html>");
        this.add(underlineBtn);

        italicBtn = new JButton("<html><i>Italic</i></html>");
        this.add(italicBtn);

        boldBtn = new JButton("<html><b>Bold</b></html>");
        this.add(boldBtn);

        colorComboBox = new JComboBox();
        colorMap = createColorMap();
        for (String option : colorMap.keySet())
            colorComboBox.addItem(option);

        colorComboBox.setSelectedIndex(0);
        colorComboBox.setRenderer(new ColorBoxRenderer(colorMap));
        this.add(colorComboBox);

        fontTypeComboBox = new JComboBox();
        fontTypeMap = createFontTypeMap();
        for (String option : fontTypeMap.keySet())
            fontTypeComboBox.addItem(option);

        fontTypeComboBox.setSelectedIndex(2);
        fontTypeComboBox.setRenderer(new FontTypeBoxRenderer(fontTypeMap));
        this.add(fontTypeComboBox);
    }

    public void init(JTextArea pageContentArea) {
        this.pageContentArea = pageContentArea;
        linkBtn.addActionListener(e -> onLinkPressed());
        underlineBtn.addActionListener(e -> onUnderlinePressed());
        italicBtn.addActionListener(e -> onItalicPressed());
        boldBtn.addActionListener(e -> onBoldPressed());
        colorComboBox.addActionListener(e -> onColorSelected());
        fontTypeComboBox.addActionListener(e -> onFontTypeSelected());
    }

    private void onLinkPressed() {
        writeOnCaretPos("<a href=''></a>");
    }

    private void onUnderlinePressed() {
        writeOnCaretPos("<u></u>");
    }

    private void onItalicPressed() {
        writeOnCaretPos("<i></i>");
    }

    private void onBoldPressed() {
        writeOnCaretPos("<b></b>");
    }

    private void onColorSelected() {
        Color selectedColor = colorMap.get(colorComboBox.getSelectedItem());
        String colorFormatted = String.format("#%02X%02X%02X", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());

        colorComboBox.setForeground(selectedColor);

        writeOnCaretPos("<font color='" + colorFormatted + "'></font color>");
    }

    private void onFontTypeSelected() {
        String selectedFontType = fontTypeMap.get(fontTypeComboBox.getSelectedItem());
        writeOnCaretPos(selectedFontType);
    }

    private void writeOnCaretPos(String str) {
        int caretPosition = pageContentArea.getCaretPosition();
        String currentText = pageContentArea.getText();

        // Inserisci <i></i> nella posizione del cursore
        String newText = currentText.substring(0, caretPosition) + str +
                currentText.substring(caretPosition);

        // Reimposta il focus sulla JTextArea
        pageContentArea.requestFocusInWindow();

        // Imposta il nuovo testo nell'area di testo
        pageContentArea.setText(newText);

        // Posiziona il cursore al centro di <i></i>


        int newCaretPosition = 0;  // Posizione al centro di <></>
        for (char c : str.toCharArray()) {
            if (c == '>')
                // Se viene trovato il simbolo '>', termina il conteggio
                break;

            newCaretPosition++;
        }

        newCaretPosition += caretPosition + 1;

        pageContentArea.setCaretPosition(newCaretPosition);
    }

    private Map<String, Color> createColorMap() {
        Map<String, Color> colorMap = new HashMap<>();
        colorMap.put("Nero", new Color(0, 0, 0));
        colorMap.put("Rosso", new Color(255, 0, 0));
        colorMap.put("Blu", new Color(0, 0, 255));
        colorMap.put("Verde", new Color(0, 255, 0));
        colorMap.put("Arancione", new Color(255, 165, 0));
        colorMap.put("Viola", new Color(128, 0, 128));
        colorMap.put("Azzurro", new Color(0, 191, 255));
        colorMap.put("Ciano", new Color(0, 255, 255));
        colorMap.put("Marrone", new Color(139, 69, 19));

        return colorMap;
    }

    private Map<String, String> createFontTypeMap() {
        Map<String, String> fontTypeMap = new HashMap<>();
        fontTypeMap.put("Paragrafo", "");
        fontTypeMap.put("Sottotitolo", "<h2></h2>");
        fontTypeMap.put("Titolo", "<h1></h1>");

        return fontTypeMap;
    }
}
