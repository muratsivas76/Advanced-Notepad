package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for converting specific character patterns into
 * Latin, Turkish, and Norwegian special characters.
 *
 * Licensed under GPL v3.
 */
public class LatinConverterActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  
  public LatinConverterActionListener(JTextPane txt, JLabel statusLabel) {
    this.txt = txt;
    this.statusLabel = statusLabel;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String text = txt.getText();
    
    // 1. Validation
    if (text == null || text.length() < 3) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">"
      + (text == null ? "Null Text For!" : "VERY SHORT LENGTH TEXT!")
      + "</font></body></html>";
      statusLabel.setText(errorMsg);
      return;
    }
    
    // 2. Background Processing for heavy string replacements
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          String processedText = convert(text);
          
          final String result = processedText;
          SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                txt.setText(result);
                txt.setCaretPosition(0);
                String successMsg = "<html><body><font color=\"green\" size=\"5\">"
                + "The Text has Converted to Latin Form Successfully."
                + "</font></body></html>";
                statusLabel.setText(successMsg);
              }
          });
        }
    });
    t.start();
  }
  
  /**
   * Internal method to handle all pattern replacements.
   */
  private String convert(String t) {
    // Norwegian (5-char patterns)
    t = t.replace("AAAAA", "\u00C5");  // Å
    t = t.replace("aaaaa", "\u00E5");  // å
    
    // Turkish/Circumflex (4-char patterns)
    t = t.replace("AAAA", "\u00C2");
    t = t.replace("IIII", "\u00CE");
    t = t.replace("UUUU", "\u00DB");
    t = t.replace("aaaa", "\u00E2");
    t = t.replace("iiii", "\u00EE");
    t = t.replace("uuuu", "\u00FB");
    
    // Latin/Accents (3-char patterns)
    t = t.replace("AAA", "\u00C1");
    t = t.replace("EEE", "\u00C9");
    t = t.replace("III", "\u00CD");
    t = t.replace("OOO", "\u00D3");
    t = t.replace("UUU", "\u00DA");
    t = t.replace("NNN", "\u00D1");
    t = t.replace("\\?\\?\\?", "\u00BF");
    t = t.replace("!!!", "\u00A1");
    t = t.replace("aaa", "\u00E1");
    t = t.replace("eee", "\u00E9");
    t = t.replace("iii", "\u00ED");
    t = t.replace("ooo", "\u00F3");
    t = t.replace("uuu", "\u00FA");
    t = t.replace("nnn", "\u00F1");
    t = t.replace("KAVISLIAC", "{");
    t = t.replace("KAVISLIKAPA", "}");
    
    // Norwegian characters (Special combinations)
    t = t.replace("AAEE", "\u00C6");   // Æ
    t = t.replace("aaee", "\u00E6");   // æ
    t = t.replace("OOEE", "\u00D8");   // Ø
    t = t.replace("ooee", "\u00F8");   // ø
    
    t = t.replace("\"\"", "'");
    t = t.replace("KOSELIAC", "[");
    t = t.replace("KOSELIKAPA", "]");
    
    // Turkish (2-char patterns)
    t = t.replace("CC", "\u00C7");
    t = t.replace("GG", "\u011E");
    t = t.replace("II", "\u0130");
    t = t.replace("OO", "\u00D6");
    t = t.replace("SS", "\u015E");
    t = t.replace("UU", "\u00DC");
    t = t.replace("cc", "\u00E7");
    t = t.replace("gg", "\u011F");
    t = t.replace("ii", "\u0131");
    t = t.replace("oo", "\u00F6");
    t = t.replace("ss", "\u015F");
    t = t.replace("uu", "\u00FC");
    
    return t;
  }
}
