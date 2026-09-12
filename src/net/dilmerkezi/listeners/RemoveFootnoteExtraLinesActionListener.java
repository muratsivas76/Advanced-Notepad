package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for removing extra newlines before footnote numbers.
 * Normalizes patterns like ".\n\n1" to ".\n1" for all digits and a custom end marker.
 *
 * Licensed under GPL v3.
 */
public class RemoveFootnoteExtraLinesActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final String endFn;
  
  /**
   * @param txt The text pane to process
   * @param statusLabel Label for status updates
   * @param endFn The custom footnote end marker (END_FN)
   */
  public RemoveFootnoteExtraLinesActionListener(JTextPane txt, JLabel statusLabel, String endFn) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.endFn = endFn;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    final String text = txt.getText();
    
    // 1. Validation
    if (text == null || text.length() < 1) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">"
      + (text == null ? "Null Text For!" : "VERY SHORT LENGTH TEXT!")
      + "</font></body></html>";
      statusLabel.setText(errorMsg);
      return;
    }
    
    // 2. Background Processing
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          String mext = text;
          
          // Replace double newlines before each digit (0-9)
          for (int i = 0; i <= 9; i++) {
            mext = mext.replaceAll("\\.\\n\\n" + i, ".\\n" + i);
          }
          
          // Replace double newlines before the custom END_FN marker
          if (endFn != null) {
            mext = mext.replaceAll("\\.\\n\\n" + endFn + "\\.", ".\\n" + endFn + ".");
          }
          
          final String result = mext;
          
          SwingUtilities.invokeLater(new Runnable() {
              @Override
              public void run() {
                txt.setText(result);
                txt.setCaretPosition(0);
                statusLabel.setText("<html><body><font color=\"green\" size=\"5\">Is okey required replaces.</font></body></html>");
              }
          });
        }
    });
    t.start();
  }
  
}
