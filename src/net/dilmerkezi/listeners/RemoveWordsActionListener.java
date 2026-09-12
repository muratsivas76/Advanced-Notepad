package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

/**
 * ActionListener to remove user-defined words from a text component.
 * Supports comma-separated input and regex patterns.
 * Compatible with Java 6 and above.
 */
public class RemoveWordsActionListener implements ActionListener {
  
  private final JTextComponent txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  /**
   * Constructor for RemoveWordsActionListener.
   * @param txt The text component to process.
   * @param statusLabel Label for status updates.
   * @param parent Parent component for the input dialog.
   */
  public RemoveWordsActionListener(JTextComponent txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    String text = txt.getText();
    
    if (text == null) {
      statusLabel.setText("Null Text For!");
      return;
    }
    
    if (text.length() < 1) {
      statusLabel.setText("VERY SHORT LENGTH TEXT!");
      return;
    }
    
    // HTML formatted input dialog for user guidance
    String message = "<html><body><font color=\"blue\" size=\"4\">Enter words separated with comma:</font>"
    + "<p><p>These chars starts with <b><font color=\"blue\" size=\"5\">\\ (92)</font></b> sign:<p>"
    + "<b><font color=\"blue\" size=\"5\">. * ? ^ + ( ) \\ $ { } | [ ]</font></b></body></html>";
    
    String input = JOptionPane.showInputDialog(parent, message);
    
    if (input == null) {
      statusLabel.setText("Null Text For!");
      txt.requestFocus();
      return;
    }
    
    if (input.trim().length() < 1) {
      statusLabel.setText("Zero Text For!");
      txt.requestFocus();
      return;
    }
    
    // Ensure at least one comma exists to support the split logic if necessary
    if (input.indexOf(",") < 0) {
      input = input + ",";
    }
    
    String[] words = input.split(",");
    
    if (words == null || words.length < 1) {
      statusLabel.setText("There is not a word for remove.");
      txt.requestFocus();
      return;
    }
    
    BufferedReader br = null;
    try {
      StringBuilder sb = new StringBuilder();
      br = new BufferedReader(new StringReader(text));
      String line;
      
      while ((line = br.readLine()) != null) {
        String xline = line.trim();
        
        if (xline.length() < 1) {
          sb.append("\n");
          continue;
        }
        
        // Remove each word from the current line
        for (int i = 0; i < words.length; i++) {
          String word = words[i];
          if (word != null && !word.isEmpty()) {
            xline = xline.replaceAll(word, "");
          }
        }
        
        sb.append(xline).append("\n");
      }
      txt.setText(sb.toString());
      statusLabel.setText("Removed words: " + input);
      
      } catch (IOException ioe) {
      ioe.printStackTrace();
      statusLabel.setText("Error during processing!");
      } finally {
      if (br != null) {
        try {
          br.close();
          } catch (IOException e) {
          // Ignore close exception
        }
      }
    }
    
    // Return focus to the text component
    txt.requestFocus();
  }
  
}
