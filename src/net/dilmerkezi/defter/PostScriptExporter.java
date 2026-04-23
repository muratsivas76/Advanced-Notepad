package net.dilmerkezi.defter;

import java.io.*;
import javax.swing.JTextPane;
import javax.swing.text.*;

/**
 * A simple PostScript exporter for educational purposes.
 * Supports only: tab (9), LF (10), CR (13), and printable ASCII (32-126).
 * No Turkish characters, no complex encoding.
 */
public class PostScriptExporter {
  
  private final int MARGIN_LEFT = 72;
  private final int START_Y = 750;
  private final int LINE_HEIGHT = 14;
  private final int PAGE_BOTTOM = 50;
  private final int TAB_SIZE = 4;
  
  private int fnum = 0;
  
  /**
   * Exports plain text to a PostScript (.ps) file.
   * Only handles: tab, newline, and printable ASCII (32-126).
   */
  public void exportToPS(final String content) throws IOException {
    final String fileName = "npoperations/postscript_" + (String.format("%03d", ++fnum)) + ".ps";
    
    File dir = new File("npoperations");
    if (!dir.exists()) {
      dir.mkdirs();
    }
    
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "US-ASCII"));
      
      writePSHeader(writer);
      
      BufferedReader reader = new BufferedReader(new StringReader(content));
      String line;
      int currentY = START_Y;
      
      while ((line = reader.readLine()) != null) {
        // New page if needed
        if (currentY < PAGE_BOTTOM) {
          writer.write("showpage\n");
          writer.write("/Helvetica findfont 12 scalefont setfont\n");
          currentY = START_Y;
        }
        
        writer.write(MARGIN_LEFT + " " + currentY + " moveto\n");
        writer.write("(" + escapeText(line) + ") show\n");
        
        currentY -= LINE_HEIGHT;
      }
      
      writer.write("showpage\n");
      writer.flush();
      
      } finally {
      if (writer != null) {
        try { writer.close(); } catch (IOException ignored) {}
        }
    }
  }
  
  /**
   * Writes PostScript header.
   */
  private void writePSHeader(BufferedWriter writer) throws IOException {
    writer.write("%!PS-Adobe-3.0\n");
    writer.write("/Helvetica findfont 12 scalefont setfont\n");
  }
  
  /**
   * Escapes PostScript special characters and converts tabs to spaces.
   * Only handles printable ASCII (32-126) plus tab conversion.
   */
  private String escapeText(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }
    
    StringBuilder sb = new StringBuilder();
    
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      int code = (int) c;
      
      // Tab -> spaces
      if (code == 9) {
        for (int t = 0; t < TAB_SIZE; t++) {
          sb.append(' ');
        }
        continue;
      }
      
      // Skip CR and LF (already handled by readLine)
      if (code == 10 || code == 13) {
        continue;
      }
      
      // Only allow printable ASCII (32-126)
      if (code >= 32 && code <= 126) {
        // Escape PostScript special characters
        if (c == '(' || c == ')' || c == '\\') {
          sb.append('\\').append(c);
          } else {
          sb.append(c);
        }
      }
      // Any other character becomes a space
      else {
        sb.append(' ');
      }
    }
    
    return sb.toString();
  }
  
  /**
   * Convenience method for JTextPane.
   */
  public void exportToPS(final JTextPane textPane) throws IOException, BadLocationException {
    exportToPS(textPane.getText());
  }
  
  /**
   * Resets file counter.
   */
  public void resetCounter() {
    fnum = 0;
  }
  
  /*
   * Reference for Turkish Unicode characters handled by this class:
   * \u015F = sh, \u015E = SH, \u011F = gg, \u011E = GG, \u0131 = ii, \u0130 = II
   * \u00E7 = ch, \u00C7 = CH, \u00F6 = oo, \u00D6 = OO, \u00FC = uu, \u00DC = UU
   * \u00E2 = aSapka, \u00EE = iSapka, \u00FB = uSapka
   */
  
}
