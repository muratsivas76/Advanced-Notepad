package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

/**
 * Listener responsible for converting the selected text into Unicode escape sequences.
 * Uses replaceSelection for efficient UI updates within the current JTextPane.
 *
 * Licensed under GPL v3.
 */
public class ConvertToUnicodeActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public ConvertToUnicodeActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // 1. Get only the selected text
    String selectedText = txt.getSelectedText();
    
    // 2. Alert if no text is selected with the requested HTML size 5 style
    if (selectedText == null || selectedText.isEmpty()) {
      statusLabel.setText("No selection found.");
      String warnMsg = "<html><body><font color=\"red\" size=\"5\">"
      + "<b>Selection Required:</b><br>"
      + "Please select at least one character to convert..."
      + "</font></body></html>";
      
      JOptionPane.showMessageDialog(parent, warnMsg, "Conversion Warning", JOptionPane.WARNING_MESSAGE);
      return;
    }
    
    String res = null;
    try {
      // 3. Process only the selected portion using the helper method
      res = toUnicode(selectedText);
      } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("Unicode conversion failed!");
    }
    
    // 4. Replace only the selected part with the converted text
    if (res != null) {
      // replaceSelection is the most efficient way to swap the selected block
      txt.replaceSelection(res);
      statusLabel.setText("Selected text converted to Unicode successfully.");
    }
  }
  
  /**
   * Converts a string into Unicode escape sequences (e.g., uXXXX).
   */
  private String toUnicode(String s) {
    if (s == null) return "";
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      
      // Check if the character is non-ASCII (greater than 127)
      if (c > 127) {
        sb.append("\\u");
        // Standard 4-digit hex format
        String hex = Integer.toHexString(c | 0x10000).substring(1).toUpperCase();
        sb.append(hex);
        } else {
        // Keep standard ASCII characters as they are
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
}
