package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

/**
 * ActionListener implementation to append a character to the current caret position
 * based on a numeric integer or hexadecimal input.
 * Compatible with Java 6 and above.
 */
public class AppendNumerousCharActionListener implements ActionListener {
  
  private final JTextComponent txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  /**
   * Constructor for AppendNumerousCharActionListener.
   * @param txt The text component where the character will be appended.
   * @param statusLabel Label to display status messages.
   * @param parent Parent component for the input dialog.
   */
  public AppendNumerousCharActionListener(JTextComponent txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // Prompt for decimal or hex input
    String message = "<html><body><font color=\"blue\" size=\"4\">Enter int value for append its char to caret pos:</font>"
    + "<font color=\"red\" size=\"4\"><br><br>You can enter like 0xNN hexadecimeal format too.</font></body></html>";
    
    String m = JOptionPane.showInputDialog(parent, message);
    
    if (m == null || m.isEmpty()) {
      statusLabel.setText("Zero Length Text For Show Numerous Char!");
      txt.requestFocus();
      return;
    }
    
    int val = 0;
    try {
      // Handle hexadecimal input starting with 0x
      if (m.startsWith("0x")) {
        String hexPart = m.substring(2);
        val = Integer.parseInt(hexPart, 16);
        } else {
        // Handle standard decimal input
        val = Integer.parseInt(m);
      }
      } catch (Exception e) {
      statusLabel.setText("Error Entering Int Val!");
      txt.requestFocus();
      return;
    }
    
    // Validate Unicode character range
    if (val < 0 || val > 65535) {
      statusLabel.setText("Range Error!");
      txt.requestFocus();
      return;
    }
    
    char chr = (char) val;
    String nc = String.valueOf(chr);
    
    try {
      // Insert character at current caret position
      int pos = txt.getCaretPosition();
      txt.getDocument().insertString(pos, nc, null);
      statusLabel.setText("Appended: " + m);
      } catch (BadLocationException e) {
      JOptionPane.showMessageDialog(parent, e.getMessage());
      e.printStackTrace();
      } finally {
      txt.requestFocus();
    }
  }
  
}
