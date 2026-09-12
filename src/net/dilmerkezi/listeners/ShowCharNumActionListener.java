package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/**
 * Professional Character Analysis Listener.
 * Features: Padded Unicode (\\u0000 format), Surrogate detail, 
 * and color-coded Key-Value pairs.
 */
public class ShowCharNumActionListener implements ActionListener {
  
  private final JTextComponent txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public ShowCharNumActionListener(JTextComponent txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    String selectedText = txt.getSelectedText();
    
    if (selectedText == null || selectedText.isEmpty()) {
      statusLabel.setText("No selection found.");
      JOptionPane.showMessageDialog(parent, "Select at least one character...", "Warning", JOptionPane.WARNING_MESSAGE);
      forceFocus();
      return;
    }
    
    StringBuffer sb = new StringBuffer("<html><body>");
    sb.append("<h2 style='color:blue; font-size:18pt; margin-bottom:5px;'>Character Details</h2>");
    sb.append("<table border='1' cellpadding='8' cellspacing='0' width='500'>");
    
    int processedCount = 0;
    int index = 0;
    
    while (index < selectedText.length() && processedCount < 3) {
      int codePoint = selectedText.codePointAt(index);
      int charCount = Character.charCount(codePoint);
      
      String display = getDisplayName(codePoint);
      String hex = Integer.toHexString(codePoint).toUpperCase();
      
      // Fixed Unicode Padding: \\u0061 format
      // Uses 4 digits for standard, more if necessary for supplemental planes
      String mainUni = (codePoint <= 0xFFFF) 
                       ? String.format("\\u%04X", codePoint) 
                       : String.format("\\u%05X", codePoint);
      
      String surrogateDetail = "";
      if (charCount > 1) {
        String s1 = String.format("%04X", (int)selectedText.charAt(index));
        String s2 = String.format("%04X", (int)selectedText.charAt(index + 1));
        surrogateDetail = " <font color='gray' size='4'>(\\u" + s1 + "\\u" + s2 + ")</font>";
      }
      
      String name = Character.getName(codePoint);
      String binary = padBinary(Integer.toBinaryString(codePoint), charCount > 1 ? 21 : 16);
      String b64 = Base64.getEncoder().encodeToString(new String(Character.toChars(codePoint)).getBytes());

      String k = "<b><font color='#006400' size='5'>"; 
      String v = "<b><font color='blue' size='5'>";       
      String end = "</font></b>";

      sb.append("<tr bgcolor='#eeeeee'>");
      sb.append("<td align='center' width='80' rowspan='2'><b><font size='30' color='red'>").append(display).append("</font></b></td>");
      sb.append("<td>").append(k).append("Dec: ").append(end).append(v).append(codePoint).append("</font></td>");
      sb.append("<td>").append(k).append("Hex: ").append(end).append(v).append("0x").append(hex).append("</font></td>");
      sb.append("</tr>");
      
      sb.append("<tr><td colspan='2'>");
      sb.append(k).append("Uni: ").append(end).append(v).append(mainUni).append("</font>").append(surrogateDetail);
      sb.append("</td></tr>");
      
      sb.append("<tr bgcolor='#f9f9f9'>");
      sb.append("<td colspan='3'>");
      sb.append(k).append("Name: ").append(end).append(v).append(name != null ? name : "N/A").append("</font><br>");
      sb.append(k).append("Bin: ").append(end).append("<font color='blue' size='4'>").append(binary).append("</font> | ");
      sb.append(k).append("B64: ").append(end).append(v).append(b64).append("</font>");
      sb.append("</td></tr>");
      
      sb.append("<tr><td colspan='3' bgcolor='#cccccc' height='3'></td></tr>");
      
      index += charCount; 
      processedCount++;
    }
    
    sb.append("</table></body></html>");
    JOptionPane.showMessageDialog(parent, sb.toString(), "Analysis Result", JOptionPane.INFORMATION_MESSAGE);
    forceFocus();
  }

  private String getDisplayName(int codePoint) {
    if (codePoint == 10) return "LF";
    if (codePoint == 13) return "CR";
    if (codePoint == 9)  return "TAB";
    if (codePoint == 32) return "SP";
    return new String(Character.toChars(codePoint));
  }

  private String padBinary(String bin, int bits) {
    StringBuffer res = new StringBuffer();
    for (int i = 0; i < bits - bin.length(); i++) res.append("0");
    return res.append(bin).toString();
  }

  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
}
