package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

/**
 * Listener responsible for decoding Percent-Encoded (URL) strings back to normal text.
 * Only processes the selected text for efficiency and supports UTF-8.
 *
 * Licensed under GPL v3.
 */
public class FCodeURLListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public FCodeURLListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public final void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    try {
      // 1. Get the selected portion of the text
      String s = txt.getSelectedText();
      
      // 2. Stylish alert using HTML size 5 for visibility
      if (s == null || s.isEmpty()) {
        statusLabel.setText("No selection found.");
        String warnMsg = "<html><body><font color=\"red\" size=\"5\">"
        + "Please select the text you want to decode!"
        + "</font></body></html>";
        
        JOptionPane.showMessageDialog(parent, warnMsg, "Selection Required", JOptionPane.WARNING_MESSAGE);
        return;
      }
      
      // 3. Process the selected text (Symmetry limit removed)
      String mm = fdecode(s);
      
      // 4. Efficiently replace only the highlighted part with the decoded result
      if (mm != null) {
        txt.replaceSelection(mm);
        statusLabel.setText("Decoding completed successfully.");
      }
      
      } catch (Exception e) {
      e.printStackTrace();
      statusLabel.setText("Error: Decoding failed.");
    }
  }
  
  /**
   * Internal decoder for percent-encoded characters using UTF-8.
   */
  private final String fdecode(String s) throws Exception {
    if (s == null || s.isEmpty()) {
      return s;
    }
    
    int len = s.length();
    // Use ByteArrayOutputStream to collect bytes and convert them back to UTF-8
    ByteArrayOutputStream baos = new ByteArrayOutputStream(len);
    
    for (int i = 0; i < len; i++) {
      char c = s.charAt(i);
      
      // Check for percent encoding pattern: %HH
      if (c == '%' && i + 2 < len) {
        char h1 = s.charAt(i + 1);
        char h2 = s.charAt(i + 2);
        
        // Convert hex characters to integer values
        int v1 = Character.digit(h1, 16);
        int v2 = Character.digit(h2, 16);
        
        // If both characters are valid hex digits (0-9, A-F)
        if (v1 != -1 && v2 != -1) {
          int b = (v1 << 4) | v2; // Bitwise operation for performance
          baos.write(b);
          i += 2; // Skip hex digits
          } else {
          // Not a valid hex sequence, treat '%' as a normal character
          baos.write(c);
        }
        } else {
        // Keep the character as is
        baos.write(c);
      }
    }
    
    // Convert the collected bytes back to String using UTF-8 encoding
    String result = baos.toString("UTF-8");
    baos.close();
    return result;
  }
  
}
