package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

/**
 * Listener responsible for decoding Unicode escape sequences (uXXXX) back to normal text.
 * Only processes the selected portion of the text for efficiency.
 *
 * Licensed under GPL v3.
 */
public class ConvertFromUnicodeActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public ConvertFromUnicodeActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // 1. Get only the selected portion of the text
    String selectedText = txt.getSelectedText();
    
    // 2. Stylish alert using HTML size 5 as requested
    if (selectedText == null || selectedText.isEmpty()) {
      statusLabel.setText("No selection found.");
      String warnMsg = "<html><body><font color=\"red\" size=\"5\">"
      + "<b>Selection Required:</b><br>"
      + "Please select the Unicode text you wish to decode..."
      + "</font></body></html>";
      
      JOptionPane.showMessageDialog(parent, warnMsg, "Conversion Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    String res = null;
    try {
      // 3. Convert only the selected Unicode string back to normal characters
      res = fromUnicode(selectedText);
      } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("Unicode decoding failed!");
    }
    
    // 4. Replace the selected block with the decoded text
    if (res != null) {
      txt.replaceSelection(res);
      statusLabel.setText("Selected Unicode form converted back to text successfully.");
    }
  }
  
  /**
   * Decodes Unicode escape sequences like \u00C7 into actual characters.
   */
  private String fromUnicode(String str) {
    if (str == null) return "";
    StringBuilder sb = new StringBuilder();
    int len = str.length();
    
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      
      // Check for u pattern and ensure there are 4 characters following it
      if (c == '\\' && i + 5 < len && str.charAt(i + 1) == 'u') {
        try {
          // Extract exactly 4 hex digits
          String hex = str.substring(i + 2, i + 6);
          int code = Integer.parseInt(hex, 16);
          sb.append((char) code);
          i += 5; // Move index forward to skip uXXXX
          } catch (NumberFormatException e) {
          // Not a valid hex code, treat u as literal text
          sb.append(c);
        }
        } else {
        // Standard ASCII or other characters
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
}
