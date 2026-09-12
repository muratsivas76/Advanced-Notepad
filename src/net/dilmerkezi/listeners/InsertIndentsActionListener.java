package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for auto-indenting text based on brace { } levels.
 * Handles strings, line comments, and block comments to avoid false brace counting.
 *
 * Licensed under GPL v3.
 */
public class InsertIndentsActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  public InsertIndentsActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    statusLabel.setText("");
    final String input = txt.getText();
    
    if (input == null || input.isEmpty()) {
      return;
    }
    
    // Processing in a background thread to keep UI responsive during large text formatting
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          final String formatted = formatIndents(input);
          
          SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                txt.setText(formatted);
                txt.setCaretPosition(0);
                statusLabel.setText("<html><body><font color=\"green\" size=\"5\">Auto-Indent Applied Successfully.</font></body></html>");
              }
          });
        }
    });
    t.start();
  }
  
  /**
   * Core logic to calculate indentation levels line by line.
   */
  private String formatIndents(String input) {
    String[] lines = input.split("\n");
    StringBuilder formattedText = new StringBuilder();
    int indentLevel = 0;
    final String INDENT_STR = "    ";
    boolean inBlockComment = false;
    
    for (String rawLine : lines) {
      String line = rawLine.trim();
      
      if (line.isEmpty()) {
        formattedText.append("\n");
        continue;
      }
      
      int openCount = 0;
      int closeCount = 0;
      int leadingClosings = 0;
      boolean inString = false;
      boolean countingLeading = true;
      
      for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        
        // 1. Block Comment Check
        if (!inString && !inBlockComment && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '*') {
          inBlockComment = true; i++; continue;
        }
        if (inBlockComment && c == '*' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
          inBlockComment = false; i++; continue;
        }
        if (inBlockComment) continue;
        
        // 2. Line Comment Check
        if (!inString && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') break;
        
        // 3. String & Escape Check
        if (c == '\\' && i + 1 < line.length() && inString) { i++; continue; }
        if (c == '"') { inString = !inString; continue; }
          
        // 4. Brace Counting
        if (!inString) {
          if (c == '{') {
            openCount++;
            countingLeading = false;
            } else if (c == '}') {
            closeCount++;
            if (countingLeading) leadingClosings++;
            } else if (!Character.isWhitespace(c)) {
            countingLeading = false;
          }
        }
      }
      
      // --- Geometrik Hesaplama ---
      int currentLineIndent = indentLevel - leadingClosings;
      if (currentLineIndent < 0) currentLineIndent = 0;
      
      for (int j = 0; j < currentLineIndent; j++) {
        formattedText.append(INDENT_STR);
      }
      
      formattedText.append(line).append("\n");
      
      indentLevel += (openCount - closeCount);
      if (indentLevel < 0) indentLevel = 0;
    }
    return formattedText.toString();
  }
  
}
