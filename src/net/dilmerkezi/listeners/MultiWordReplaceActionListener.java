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
 * Listener responsible for performing multiple word replacements at once.
 * Input format: src1=dst1,src2=dst2
 * Performs processing in a background thread.
 *
 * Licensed under GPL v3.
 */
public class MultiWordReplaceActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public MultiWordReplaceActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String text = txt.getText();
    
    // 1. Content Validation
    if (text == null) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Null Text For!</font></body></html>");
      return;
    }
    
    if (text.length() < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">VERY SHORT LENGTH TEXT!</font></body></html>");
      return;
    }
    
    // 2. Get replacement pairs from user
    String inputMsg = "<html><body><font color=\"blue\" size=\"5\">"
    + "Enter words in src=dst format<p>separated with comma:<p>"
    + "Example:<p>selam=merhaba,belde=Sivas</font></body></html>";
    
    String input = JOptionPane.showInputDialog(parent, inputMsg);
    
    if (input == null) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Null Text For!</font></body></html>");
      return;
    }
    
    if (input.length() < 3) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">Zero Text For!</font></body></html>");
      return;
    }
    
    // 3. Parsing logic preserved from original
    if (input.indexOf(",") < 0) {
      input = input + ",";
    }
    
    final String[] words = input.split(",");
    if (words == null || words.length < 1) {
      updateStatus("<html><body><font color=\"red\" size=\"5\">There is not a word for replace.</font></body></html>");
      return;
    }
    
    final String finalInput = input;
    
    // 4. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            final String result = processReplacement(text, words);
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result);
                  txt.setCaretPosition(0);
                  statusLabel.setText("Replaced words: " + finalInput);
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
   * Iterates through lines and applies all replacement pairs.
   */
  private String processReplacement(String content, String[] pairs) throws IOException {
    StringReader sr = new StringReader(content);
    BufferedReader br = new BufferedReader(sr);
    StringBuffer sb = new StringBuffer();
    String line;
    
    while ((line = br.readLine()) != null) {
      String xline = line.trim();
      
      if (xline.length() < 1) {
        sb.append("\n");
        continue;
      }
      
      // Apply each pair to the current line
      for (int i = 0; i < pairs.length; i++) {
        String pair = pairs[i];
        if (pair.indexOf("=") < 0) continue;
        
        String[] splitPair = pair.split("=");
        if (splitPair == null || splitPair.length < 2) continue;
        
        String src = splitPair[0];
        String dst = splitPair[1];
        
        if (src.length() < 1) continue;
        
        // Replace all occurrences of src with dst in the line
        xline = xline.replace(src, dst);
      }
      
      sb.append(xline).append("\n");
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
