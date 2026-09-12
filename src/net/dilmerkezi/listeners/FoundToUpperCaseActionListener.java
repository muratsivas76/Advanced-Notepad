package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for finding a specific expression and converting it to upper case.
 * Processes the replacement in a background thread for UI stability.
 *
 * Licensed under GPL v3.
 */
public class FoundToUpperCaseActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public FoundToUpperCaseActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String mm = txt.getText();
    
    // 1. Validation
    if (mm == null) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Null Text For Found to Upper Case!</font></body></html>");
      return;
    }
    
    if (mm.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Zero Length Text For Found to Upper Case!</font></body></html>");
      return;
    }
    
    // 2. Get the expression from user
    String inputMsg = "<html><body><font color=\"blue\" size=\"5\">Enter Expression For Replacing to Upper Case:</font></body></html>";
    final String srcreps = JOptionPane.showInputDialog(parent, inputMsg);
    
    if (srcreps == null || srcreps.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">NOT REPLACED: NULL OR ZERO LENGTH!</font></body></html>");
      return;
    }
    
    // 3. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final String dest = srcreps.toUpperCase();
            // Using replaceAll (Regex support preserved from original code)
            final String result = mm.replaceAll(srcreps, dest);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result);
                  txt.setCaretPosition(0);
                  statusLabel.setText("Replaced Selection to Upper Case Successfully!");
                }
            });
            } catch (final Exception e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  statusLabel.setText("<html><body><font color=\"red\" size=\"5\">WARNING: NOT REPLACED TO UPPER CASE!</font></body></html>");
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
