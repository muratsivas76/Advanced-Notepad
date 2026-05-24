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
 * Listener responsible for removing characters with a Unicode value lower than a user-defined limit.
 * Complements the HighCode removal by filtering out standard characters and keeping higher scripts.
 *
 * Licensed under GPL v3.
 */
public class RemoveLowCodeCharsActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public RemoveLowCodeCharsActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
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
    
    // 2. Get the character limit from user
    String inputMsg = "<html><body><font color=\"blue\" size=\"5\">Enter character num (Ex: 1000):</font></body></html>";
    String input = JOptionPane.showInputDialog(parent, inputMsg);
    
    if (input == null || input.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Operation Cancelled or Zero Input!</font></body></html>");
      return;
    }
    
    int limitValue;
    try {
      limitValue = Integer.parseInt(input);
      } catch (NumberFormatException nfe) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Invalid Number Error!</font></body></html>");
      return;
    }
    
    final int MIL = limitValue;
    
    // 3. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final String result = processText(text, MIL);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result);
                  txt.setCaretPosition(0);
                  statusLabel.setText("Removed Chars Lesser Than: " + MIL);
                }
            });
            } catch (IOException ioe) {
            ioe.printStackTrace();
          }
        }
    });
    t.start();
  }
  
  /**
   * Filters the text by keeping only characters above or equal to the limit.
   */
  private String processText(String content, int limit) throws IOException {
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
      
      for (int i = 0; i < line.length(); i++) {
        char ch = line.charAt(i);
        int code = (int) ch;
        
        // Keep only characters ABOVE or equal to the limit
        if (code >= limit) {
          sb.append(ch);
        }
      }
      sb.append("\n");
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
