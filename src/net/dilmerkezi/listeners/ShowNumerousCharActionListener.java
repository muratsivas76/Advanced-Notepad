package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * ActionListener implementation to convert a numeric integer value
 * into its corresponding Unicode character representation.
 * Supports full Unicode range (including Surrogate Pairs) and reliable focus.
 */
public class ShowNumerousCharActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  /**
   * Constructor for ShowNumerousCharActionListener.
   * @param txt The text pane to return focus to.
   * @param statusLabel Label to display status and error messages.
   * @param parent Parent component for input and message dialogs.
   */
  public ShowNumerousCharActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // Prompt user for integer value
    String m = JOptionPane.showInputDialog(parent,
    "<html><body><font color=\"blue\" size=\"5\">Enter int value to see its char:</font></body></html>");
    
    if (m == null) {
      statusLabel.setText("Null Text For Show Numerous Char!");
      forceFocus();
      return;
    }
    
    if (m.trim().length() < 1) {
      statusLabel.setText("Zero Length Text For Show Numerous Char!");
      forceFocus();
      return;
    }
    
    int val = 0;
    try {
      val = Integer.parseInt(m.trim());
    } catch (Exception e) {
      statusLabel.setText("Error Entering Int Val!");
      forceFocus();
      return;
    }
    
    // Validate character range (0 to 1114111 to support emojis/surrogates)
    if (val < 0 || val > Character.MAX_CODE_POINT) {
      statusLabel.setText("Range Error (0 - " + Character.MAX_CODE_POINT + ")!");
      forceFocus();
      return;
    }
    
    // Convert to String using toChars to handle Surrogate Pairs (e.g. Emojis)
    String nc = new String(Character.toChars(val));
    
    // Show the result in a message dialog
    JOptionPane.showMessageDialog(parent,
    "<html><body><font color=\"blue\" size=\"5\">" + Integer.toString(val) + " -> " + nc + "</font></body></html>");
    
    forceFocus();
  }

  /**
   * Helper to ensure focus returns to text component 
   * after modal dialog disposal.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
}
