package wiki.GUI;

import wiki.Controllers.WikiController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PageCreate extends PageBase {
    private JPanel pageCreatePanel;
    private JTextField pageTitleField;
    private JTextArea pageContentArea;
    private JButton createPageBtn;
    private JButton backBtn;
    private JButton linkBtn;
    private JButton underlineBtn;
    private JButton italicBtn;
    private JButton boldBtn;
    private JComboBox colorBox;
    private JComboBox fontTypeBox;

    private Map<String, Color> colorMap;
    private Map<String, String> fontTypeMap;


    public PageCreate(WikiController wikiController, PageBase frame) {
        super(wikiController, frame);
        add(pageCreatePanel);
        initGUI(true);

        createPageBtn.addActionListener(e -> onCreatePage());
        backBtn.addActionListener(e -> onBackButtonPressed());

        linkBtn.addActionListener(e -> onLinkPressed());
        underlineBtn.addActionListener(e -> onUnderlinePressed());
        italicBtn.addActionListener(e -> onItalicPressed());
        boldBtn.addActionListener(e -> onBoldPressed());

        colorMap = createColorMap();
        for (String option : colorMap.keySet())
            colorBox.addItem(option);

        colorBox.setSelectedItem("Black");
        colorBox.setRenderer(new ColorBoxRenderer(colorMap));
        colorBox.addActionListener(e -> onColorSelected());

        fontTypeMap = createFontTypeMap();
        for (String option : fontTypeMap.keySet())
            fontTypeBox.addItem(option);

        fontTypeBox.setSelectedItem("Paragrafo");
        fontTypeBox.setRenderer(new FontTypeBoxRenderer(fontTypeMap));
        fontTypeBox.addActionListener(e -> onFontTypeSelected());

        setVisible(true);
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

    private void onCreatePage() {
        // -> Goes back to the Main Menu Page
        if (wikiController.createPage(pageTitleField.getText(), pageContentArea.getText())) {
            prevPageRef.setVisible(true);
            this.dispose();
        }
    }

    private void onBackButtonPressed() {
        prevPageRef.setVisible(true);
        this.dispose();
    }

    private void onLinkPressed() {
        writeOnCaretPos("<a href=''></a>", 11);
    }

    private void onUnderlinePressed() {
        writeOnCaretPos("<u></u>", 3);
    }

    private void onItalicPressed() {
        writeOnCaretPos("<i></i>", 3);
    }

    private void onBoldPressed() {
        writeOnCaretPos("<b></b>", 3);
    }

    private void onColorSelected() {
        Color selectedColor = colorMap.get(colorBox.getSelectedItem());
        String colorFormatted = String.format("#%02X%02X%02X", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());

        colorBox.setForeground(selectedColor);

        writeOnCaretPos("<font color='" + colorFormatted + "'></font color>", 22);
    }

    private void onFontTypeSelected() {
        String selectedFontType = fontTypeMap.get(fontTypeBox.getSelectedItem());
        writeOnCaretPos(selectedFontType, 4);
    }

    private void writeOnCaretPos(String str, int caretPosOffset) {
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
        int newCaretPosition = caretPosition + caretPosOffset;  // Posizione al centro di <i></i>
        pageContentArea.setCaretPosition(newCaretPosition);
    }
}
