package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for finding a specific expression and converting it to lower case.
 * Runs the replacement process in a background thread to maintain UI performance.
 *
 * Licensed under GPL v3.
 */
public class FoundToLowerCaseActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public FoundToLowerCaseActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String content = txt.getText();
    
    // 1. Validation
    if (content == null || content.length() < 1) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">"
      + (content == null ? "Null Text For!" : "Zero Length Text For!")
      + "</font></body></html>";
      updateStatus(errorMsg);
      return;
    }
    
    // 2. Get target expression
    String inputMsg = "<html><body><font color=\"blue\" size=\"5\">Enter Expression For Replacing to Lower Case:</font></body></html>";
    final String target = JOptionPane.showInputDialog(parent, inputMsg);
    
    if (target == null || target.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">NOT REPLACED: NULL OR ZERO LENGTH!</font></body></html>");
      return;
    }
    
    // 3. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final String replacement = target.toLowerCase();
            // Original logic uses replaceAll (Regex support)
            final String result = content.replaceAll(target, replacement);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result);
                  txt.setCaretPosition(0);
                  statusLabel.setText("Replaced Selection to Lower Case Successfully!");
                }
            });
            } catch (final Exception e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  statusLabel.setText("<html><body><font color=\"red\" size=\"5\">WARNING: NOT REPLACED TO LOWER CASE!</font></body></html>");
                }
            });
          }
        }
    });
    t.start();
  }
  
  private void updateStatus(final String htmlMessage) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          statusLabel.setText(htmlMessage);
        }
    });
  }
  
}
