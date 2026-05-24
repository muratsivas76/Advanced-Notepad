package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for handling application exit with user confirmation.
 * Ensures focus returns to the editor if the exit is cancelled.
 *
 * Licensed under GPL v3.
 */
public class ExitActionListener implements ActionListener {
  
  private final JTextPane txt; // Added to handle focus recovery
  private final JLabel statusLabel;
  private final Component parent;
  
  /**
   * @param txt The main text pane to return focus to if exit is cancelled
   * @param statusLabel Label to reset on action
   * @param parent Parent component for positioning the dialog
   */
  public ExitActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    String msg = "<html><body><font color=\"blue\" size=\"5\">Enter Y/y/E/e For Exit!</font></body></html>";
    String input = JOptionPane.showInputDialog(parent, msg);
    
    // If user cancels (null) or provides empty input
    if (input == null || input.trim().isEmpty()) {
      forceFocus();
      return;
    }
    
    // Confirm exit or return focus
    if (isConfirmed(input.trim())) {
      System.exit(0);
    } else {
      forceFocus();
    }
  }
  
  /**
   * Validates if the user input matches exit keywords.
   */
  private boolean isConfirmed(String input) {
    return input.equalsIgnoreCase("y") || input.equalsIgnoreCase("e");
  }

  /**
   * Safely restores focus to the text pane using the EDT queue.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          if (txt != null) {
            txt.requestFocus();
          }
        }
    });
  }
  
}
