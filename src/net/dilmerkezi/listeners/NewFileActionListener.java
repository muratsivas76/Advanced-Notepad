package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;

/**
 * Listener responsible for clearing the current document and
 * resetting the editor state for a new file session.
 * 
 * Performance: Uses targeted requestFocus calls within invokeLater 
 * to ensure focus returns after modal dialogs are closed.
 */
public class NewFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public NewFileActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    String message = "<html><body><font color=\"#0000FF\" size=\"5\">"
        + "Do you want to start a new file?<br><br>"
        + "e/y/t/yes OR h/n/no</font></body></html>";
    
    // Show modal dialog
    String input = JOptionPane.showInputDialog(parent, message);
    
    // 1. Case: Cancel or Close
    if (input == null) {
      forceFocus();
      return;
    }
    
    // 2. Case: Valid Confirmation
    if (isConfirmed(input.trim().toLowerCase())) {
      createNewFile();
    } else {
      // 3. Case: Negative input (h/n/no)
      forceFocus();
    }
  }
  
  private boolean isConfirmed(String input) {
    return input.equals("e") || input.equals("y") ||
           input.equals("t") || input.equals("yes");
  }
  
  private void createNewFile() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          // Reset document content
          txt.setText("");
          
          // Re-apply editor kit
          txt.setEditorKit(new StyledEditorKit());
          
          // Re-attach Undo listener
          if (UndoRedoConstants.ulis != null) {
            txt.getDocument().addUndoableEditListener(UndoRedoConstants.ulis);
          }
          
          statusLabel.setText("New file ready.");
          
          // Focus at the end of the update sequence
          txt.setCaretPosition(0);
          txt.requestFocus();
        }
    });
  }

  /**
   * Helper to ensure focus returns to text component 
   * after dialog disposal.
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
