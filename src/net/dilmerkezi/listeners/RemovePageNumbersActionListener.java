package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for removing page numbers from the text.
 * It identifies lines consisting only of digits and dots (within 1-6 characters).
 *
 * Licensed under GPL v3.
 */
public class RemovePageNumbersActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public RemovePageNumbersActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String content = txt.getText();
    
    // 1. Basic Validations
    if (content == null) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Null Text Error!</font></body></html>");
      return;
    }
    
    if (content.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Insufficient Text Error!</font></body></html>");
      return;
    }
    
    // 2. Processing in a separate thread for UI responsiveness
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            String result = processText(content);
            updateUI(result);
            } catch (IOException ioe) {
            ioe.printStackTrace();
            updateStatus("<html><body><font color=\"red\" size=\"5\">IO Error during processing!</font></body></html>");
          }
        }
    });
    t.start();
  }
  
  /**
   * Core logic to filter out lines that represent page numbers.
   */
  private String processText(String input) throws IOException {
    StringReader sr = new StringReader(input);
    BufferedReader br = new BufferedReader(sr);
    StringBuffer sb = new StringBuffer();
    String line;
    
    while ((line = br.readLine()) != null) {
      String trimmedLine = line.trim();
      int length = trimmedLine.length();
      
      // Original rule: check lines between 1 and 6 characters
      if (length >= 1 && length <= 6) {
        String cleanLine = trimmedLine.replaceAll(" ", "");
        if (isPageNumber(cleanLine)) {
          continue; // Skip this line (remove page number)
        }
      }
      
      sb.append(line).append("\n");
    }
    
    br.close();
    sr.close();
    return sb.toString();
  }
  
  /**
   * Checks if a string consists only of digits and dots.
   */
  private boolean isPageNumber(String str) {
    if (str.isEmpty()) return false;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c != '.' && !Character.isDigit(c)) {
        return false;
      }
    }
    return true;
  }
  
  private void updateUI(final String newText) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.setText(newText);
          txt.setCaretPosition(0);
          statusLabel.setText("Removed Page Numbers Successfully.");
        }
    });
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
