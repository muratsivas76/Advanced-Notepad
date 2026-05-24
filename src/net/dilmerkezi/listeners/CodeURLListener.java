package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

/**
 * Listener responsible for Percent-Encoding (URL) strings.
 * Safely handles non-ASCII characters using UTF-8 and updates selection.
 *
 * Licensed under GPL v3.
 */
public class CodeURLListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public CodeURLListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public final void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    try {
      // 1. Fetch only the selected text
      String s = txt.getSelectedText();
      
      // 2. Alert the user if no selection is made
      if (s == null || s.isEmpty()) {
        statusLabel.setText("No selection found.");
        String warnMsg = "<html><body><font color=\"red\" size=\"5\">"
        + "Please select some text to encode!"
        + "</font></body></html>";
        
        JOptionPane.showMessageDialog(parent, warnMsg, "Selection Required", JOptionPane.WARNING_MESSAGE);
        return;
      }
      
      // 3. Process the selected text (Limit removed for large data)
      String mm = code(s);
      
      // 4. Update only the selected portion
      if (mm != null) {
        txt.replaceSelection(mm);
        statusLabel.setText("The selected text has been encoded successfully.");
      }
      
      } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("Encoding failed due to an error.");
    }
  }
  
  /**
   * Encodes a string into percent-encoding based on UTF-8 bytes.
   */
  private final String code(String ch) throws Exception {
    if (ch == null || ch.isEmpty()) {
      return ch;
    }
    
    // Use UTF-8 bytes for correct non-ASCII handling
    byte[] bits = ch.getBytes("UTF-8");
    int blen = bits.length;
    
    // Use StringBuffer for Java 6-8 compatibility
    StringBuffer sb = new StringBuffer(blen * 2);
    
    for (int i = 0; i < blen; i++) {
      int b = bits[i] & 0xFF; // Unsigned byte
      
      // Safe ASCII range (up to 'z')
      if (b < 123) {
        sb.append((char) b);
        } else {
        // Percent encoding
        sb.append("%");
        String hex = Integer.toHexString(b);
        // Ensure two-digit hex format
        if (hex.length() < 2) {
          sb.append('0');
        }
        sb.append(hex);
      }
    }
    
    return sb.toString();
  }
  
}
