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
 * Listener responsible for removing lines that start with a specific user-defined string.
 * Performs the filtering in a background thread for better UI performance.
 *
 * Licensed under GPL v3.
 */
public class RemoveLinesStartingWithActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public RemoveLinesStartingWithActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String text = txt.getText();
    
    // 1. Basic Content Validation
    if (text == null) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Null Text For!</font></body></html>");
      return;
    }
    
    if (text.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">VERY SHORT LENGTH TEXT!</font></body></html>");
      return;
    }
    
    // 2. Get the prefix to filter from user
    String inputMsg = "<html><body><font color=\"blue\" size=\"5\">Enter character or string:</font></body></html>";
    final String input = JOptionPane.showInputDialog(parent, inputMsg);
    
    if (input == null || input.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Zero or Null Input!</font></body></html>");
      return;
    }
    
    // 3. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final String result = processText(text, input);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result);
                  txt.setCaretPosition(0);
                  statusLabel.setText("Removed Lines Starts With: " + input);
                }
            });
            } catch (IOException ioe) {
            ioe.printStackTrace();
            updateStatus("<html><body><font color=\"red\" size=\"5\">Error during processing!</font></body></html>");
          }
        }
    });
    t.start();
  }
  
  /**
   * Filters the text by removing lines that start with the given input string.
   */
  private String processText(String content, String prefix) throws IOException {
    StringReader sr = new StringReader(content);
    BufferedReader br = new BufferedReader(sr);
    StringBuffer sb = new StringBuffer();
    String line;
    
    while ((line = br.readLine()) != null) {
      String trimmedLine = line.trim();
      
      if (trimmedLine.length() < 1) {
        sb.append("\n");
        continue;
      }
      
      // Orijinal mantık: Eğer satır girdiği metinle başlıyorsa atla (skip)
      if (trimmedLine.startsWith(prefix)) {
        continue;
      }
      
      sb.append(line).append("\n");
    }
    
    br.close();
    sr.close();
    return sb.toString();
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
