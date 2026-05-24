package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import net.dilmerkezi.defter.Utilities;

/**
 * ConvertListener - Generates HTML with numeric entities (&#0000;),
 * plain TXT, and Latin-friendly HTML.
 * Java 6-8 Compatible.
 */
public class ConvertListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  private final String charCode;
  private final Utilities utilities;
  private final boolean isConvert;
  
  private final char[] KYN;
  private final int KYNLEN;
  private final String[] TXTD;
  
  public ConvertListener(JTextPane txt, JLabel statusLabel, Component parent, String charCode, Utilities utilities, boolean isConvert) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
    this.charCode = charCode;
    this.utilities = utilities;
    this.isConvert = isConvert;
    
    // Turkish Source Characters
    KYN = new char[]{
      '\u0130', '\u0131', '\u00D6', '\u00F6',
      '\u00DC', '\u00FC', '\u00C7', '\u00E7',
      '\u011E', '\u011F', '\u015E', '\u015F',
      (char) 221, (char) 253, (char) 222, (char) 254, (char) 240
    };
    KYNLEN = KYN.length;
    
    // Latin mapping for TXT and _lt files
    TXTD = new String[]{
      "I", "i", "O", "o",
      "U", "u", "C", "c",
      "G", "g", "S", "s",
      "I", "i", "S", "s", "g"
    };
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("Processing...");
    final String fullText = txt.getText();
    
    if (fullText == null || fullText.trim().length() == 0) return;
    
    Thread processThread = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            // Normalization
            String processedText = fullText.replace("\u2019", "'")
            .replace("\u201c", "\"")
            .replace("\u201d", "\"")
            .replace(String.valueOf((char) 0x60), "...");
            
            String suffix = "output";
            String input = JOptionPane.showInputDialog(parent, "<html><body><font color=\"blue\" size=\"5\">Enter Suffix:</font></body></html>");
            if (input != null && input.trim().length() > 0) suffix = input.trim();
            
            runConversion(processedText, suffix);
            
            statusLabel.setText("Done: " + suffix);
            JOptionPane.showMessageDialog(parent, "<html><body><font color=\"blue\" size=\"5\">HTML Entities Generated Successfully!</font></body></html>");
            txt.requestFocus();
            } catch (Exception e) {
            e.printStackTrace();
          }
        }
    });
    processThread.start();
  }
  
  private void runConversion(String content, String suffix) throws IOException {
    File opDir = new File("npoperations");
    if (!opDir.exists()) opDir.mkdir();
    
    PrintStream psHtml = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(opDir, suffix + ".html"))), true, charCode);
    PrintStream psTxt = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(opDir, suffix + ".txt"))), true, charCode);
    PrintStream psHtmlLt = new PrintStream(new BufferedOutputStream(new FileOutputStream(new File(opDir, suffix + "_lt.html"))), true, charCode);
    
    psHtml.print("<html>\n<body>\n");
    psHtmlLt.print("<html>\n<body>\n");
    
    String[] lines = content.split("\n");
    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.length() < 1) {
        String pTag = "\n<p><p>\n";
        psHtml.print(pTag); psHtmlLt.print(pTag); psTxt.print("\n\n");
        continue;
      }
      
      if (utilities.sfnum(trimmed)) continue;
      
      StringBuffer sbHtml = new StringBuffer();
      StringBuffer sbLatin = new StringBuffer();
      
      for (int k = 0; k < line.length(); k++) {
        char c = line.charAt(k);
        int cval = (int) c;
        
        // 1. LATIN CONVERSION (for _lt.html and .txt)
        boolean foundLatin = false;
        for (int j = 0; j < KYNLEN; j++) {
          if (c == KYN[j]) {
            sbLatin.append(TXTD[j]);
            foundLatin = true;
            break;
          }
        }
        if (!foundLatin) sbLatin.append(c);
        
        // 2. HTML ENTITY CONVERSION (for .html)
        // If isConvert is true, we force non-ASCII characters into &#0000; format
        if (isConvert && cval > 127) {
          sbHtml.append("&#").append(String.format("%04d", cval)).append(";");
          } else {
          sbHtml.append(c);
        }
      }
      
      // Write results
      psHtml.print(sbHtml.toString().trim() + " ");
      if (!Character.isLetter(trimmed.charAt(trimmed.length() - 1)) || trimmed.length() < 52) {
        psHtml.print("\n<p><p>\n");
      }
      
      psHtmlLt.print(sbLatin.toString() + " ");
      psTxt.print(sbLatin.toString() + " ");
    }
    
    psHtml.print("\n</body>\n</html>");
    psHtmlLt.print("\n</body>\n</html>");
    
    psHtml.close(); psTxt.close(); psHtmlLt.close();
  }
  
}
