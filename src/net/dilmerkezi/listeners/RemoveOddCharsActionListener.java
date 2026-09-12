package net.dilmerkezi.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

/**
 * ActionListener implementation to remove or replace non-standard characters
 * and normalize specific symbols in a text component.
 * Compatible with Java 6 and above.
 */
public class RemoveOddCharsActionListener implements ActionListener {
  
  private final JTextComponent txt;
  private final JLabel statusLabel;
  
  /**
   * Constructor for the action listener.
   * @param txt The text component to be cleaned.
   * @param statusLabel Label to display process status.
   */
  public RemoveOddCharsActionListener(JTextComponent txt, JLabel statusLabel) {
    this.txt = txt;
    this.statusLabel = statusLabel;
  }
  
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    String text = txt.getText();
    
    if (text == null || text.length() < 1) {
      statusLabel.setText("Text is null or too short!");
      return;
    }
    
    BufferedReader br = null;
    try {
      // Use StringBuilder with initial capacity to optimize memory usage
      StringBuilder sb = new StringBuilder(text.length());
      
      // Process the text using BufferedReader to handle lines correctly
      br = new BufferedReader(new StringReader(text));
      String line;
      boolean firstLine = true;
      
      while ((line = br.readLine()) != null) {
        // Keep the exact line structure by adding newline before subsequent lines
        if (!firstLine) {
          sb.append("\n");
        }
        firstLine = false;
        
        // Process every single character in the line
        int lineLen = line.length();
        for (int i = 0; i < lineLen; i++) {
          char c = line.charAt(i);
          int n = (int) c;
          
          // 1. Handle critical control characters 0-31
          // Keep: 9-TAB, 10-LF, 13-CR. Remove others like Null, Bell, Esc etc.
          if (n < 32 && n != 9 && n != 10 && n != 13) {
            continue; // Skip problematic control characters
          }
          
          // 2. Mapping known problematic characters to clean equivalents
          switch (c) {
            case '`':   sb.append('\''); break;
            case (char) 95:  sb.append('-'); break;
            case (char) 127: sb.append(' '); break;
            case (char) 160: sb.append(' '); break; // Non-breaking space
              case (char) 166: sb.append(':'); break;
            case (char) 168: sb.append('~'); break;
            case (char) 170: sb.append('a'); break;
            case (char) 171: sb.append('"'); break;
            case (char) 173: sb.append('-'); break;
            case (char) 175: sb.append('~'); break;
            case (char) 176: sb.append("o:"); break;
            case (char) 180: sb.append('\''); break;
            case (char) 182: sb.append('~'); break;
            case (char) 183: sb.append(' '); break;
            case (char) 184: sb.append(' '); break;
            case (char) 186: sb.append("o:"); break;
            case (char) 187: sb.append('"'); break;
            case (char) 247: sb.append('/'); break;
            case (char) 8210: case (char) 8211: case (char) 8212: sb.append('-'); break; // Hyphens
            case (char) 8213: sb.append("--"); break; // Horizontal bar
            case (char) 8214: break; // Double vertical line removed
            case (char) 8215: sb.append('='); break; // Double low line
            case (char) 8216: case (char) 8217: sb.append('\''); break; // Smart quotes
            case (char) 8218: sb.append(','); break; // Single low-9 quote
            case (char) 8219: sb.append('/'); break; // Reversed quote
            case (char) 8220: case (char) 8221: case (char) 8223: sb.append('"'); break; // Smart double quotes
            case (char) 8222: sb.append(','); break; // Double low-9 quote
            case (char) 8224: case (char) 8225: sb.append('+'); break; // Daggers
            case (char) 8226: sb.append("--"); break; // Bullet
            case (char) 8230: sb.append("..."); break; // Ellipsis
            case (char) 8592: sb.append("<--"); break; // Left arrow
            case (char) 8594: sb.append("-->"); break; // Right arrow
            case (char) 9474: sb.append('-'); break; // Box drawings
              case (char) 61694: sb.append('~'); break;
            case (char) 65533: sb.append(' '); break; // Replacement char
            case (char) 128: sb.append("EUR"); break; // Euro sign
            case (char) 153: sb.append("(TM)"); break; // Trademark
            case (char) 169: sb.append("(c)"); break; // Copyright
            case (char) 174: sb.append("(r)"); break; // Registered
              
            default: sb.append(c); break; // Keep the original character
            }
        }
      }
      
      // Update the text area with cleaned content
      txt.setText(sb.toString());
      txt.requestFocus();
      statusLabel.setText("Text cleaned successfully (No trim).");
      } catch (IOException ioe) {
      ioe.printStackTrace();
      statusLabel.setText("Error during cleaning process!");
      } finally {
      if (br != null) {
        try {
          br.close();
          } catch (IOException e) {
          // Silently ignore close exception
        }
      }
    }
  }
  
}
