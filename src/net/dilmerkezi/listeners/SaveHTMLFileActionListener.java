package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

/**
 * Listener responsible for converting JTextPane content to HTML and saving it.
 * Includes overwrite confirmation and automatic .html extension handling.
 * 
 * Focus Management: Uses invokeLater to stabilize focus recovery after 
 * modal dialogs and background I/O tasks.
 *
 * Licensed under GPL v3.
 */
public class SaveHTMLFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser htmljfc;
  private final Component parent;
  
  public SaveHTMLFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser htmljfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.htmljfc = htmljfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    saveHTML();
  }
  
  public void saveHTML() {
    statusLabel.setText("");
    
    // 1. Show save dialog
    int rep = htmljfc.showSaveDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      statusLabel.setText("Cancelled Saving HTML File!");
      forceFocus();
      return;
    }
    
    File file = htmljfc.getSelectedFile();
    if (file == null) {
      statusLabel.setText("Null File For Saving!");
      forceFocus();
      return;
    }
    
    // 2. Overwrite Confirmation Logic
    if (file.exists()) {
      String confirmMsg = "<html><body><font color=\"blue\" size=\"5\">"
      + "This file exists. Enter Y/y/E/e For Overwrite!"
      + "</font></body></html>";
      
      String m = JOptionPane.showInputDialog(parent, confirmMsg);
      
      if (m == null || m.trim().isEmpty()) {
        statusLabel.setText("Cancelled Overwrite HTML Saving Operation!");
        forceFocus();
        return;
      }
      
      if (!m.equalsIgnoreCase("y") && !m.equalsIgnoreCase("e")) {
        statusLabel.setText("Cancelled Overwrite HTML Saving Operation!");
        forceFocus();
        return;
      }
    }
    
    // 3. Extension Handling
    String name = file.getName().toLowerCase();
    if (!name.endsWith(".html") && !name.endsWith(".htm")) {
      file = new File(file.getAbsolutePath() + ".html");
    }
    
    final File finalFile = file;
    
    // 4. Background Writing Process
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          FileOutputStream fos = null;
          try {
            HTMLEditorKit htmlKit = new HTMLEditorKit();
            fos = new FileOutputStream(finalFile);
            
            // Convert and write JTextPane content to HTML
            htmlKit.write(fos, txt.getDocument(), 0, txt.getDocument().getLength());
            fos.flush();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  String successMsg = "<html><body><font color=\"#4CAF50\" size=\"5\">"
                  + "Saved successfully as HTML!"
                  + "</font></body></html>";
                  JOptionPane.showMessageDialog(parent, successMsg);
                  txt.requestFocus(); // Focus after success dialog
                }
            });
            
            } catch (final Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  String errMsg = "<html><body><font color=\"red\" size=\"5\">"
                  + "Error saving HTML: " + ex.getMessage()
                  + "</font></body></html>";
                  JOptionPane.showMessageDialog(parent, errMsg);
                  txt.requestFocus(); // Focus after error dialog
                }
            });
            } finally {
              try {
                if (fos != null) fos.close();
              } catch (Exception e) {
                // Silent close
              }
          }
        }
    });
    t.start();
  }

  /**
   * Helper to ensure focus returns to text component 
   * after modal dialog disposal.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
}
