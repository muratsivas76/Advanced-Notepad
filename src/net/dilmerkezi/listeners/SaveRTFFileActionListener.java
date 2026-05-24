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
import javax.swing.text.rtf.RTFEditorKit;

/**
 * Listener responsible for saving the JTextPane content as an RTF file.
 * Includes overwrite confirmation and automatic extension handling.
 * 
 * Focus: Stabilized using invokeLater to ensure reliable focus return 
 * after multiple modal dialogs and background thread completion.
 *
 * Licensed under GPL v3.
 */
public class SaveRTFFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser rtfjfc;
  private final Component parent;
  
  public SaveRTFFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser rtfjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.rtfjfc = rtfjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    saveRTF();
  }
  
  /**
   * Executes the RTF saving logic with mandatory user confirmations.
   */
  public void saveRTF() {
    statusLabel.setText("");
    
    // 1. Show save dialog
    int rep = rtfjfc.showSaveDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      statusLabel.setText("Cancelled Saving File!");
      forceFocus(); // Ensure focus returns on cancel
      return;
    }
    
    File file = rtfjfc.getSelectedFile();
    if (file == null) {
      statusLabel.setText("Null or Non-Existent File For Saving!");
      forceFocus();
      return;
    }
    
    // 2. Overwrite Confirmation Logic
    if (file.exists()) {
      String confirmMsg = "<html><body><font color=\"blue\" size=\"5\">"
      + "This file exists. Enter Y/y/E/e For Overwrite!"
      + "</font></body></html>";
      
      String m = JOptionPane.showInputDialog(parent, confirmMsg);
      
      if (m == null || m.trim().equals("")) {
        statusLabel.setText("Cancelled Overwrite RTF Saving Operation!");
        forceFocus();
        return;
      }
      
      if (!m.equalsIgnoreCase("y") && !m.equalsIgnoreCase("e")) {
        statusLabel.setText("Cancelled Overwrite RTF Saving Operation!");
        forceFocus();
        return;
      }
    }
    
    // 3. Extension Handling
    String name = file.getName().toLowerCase();
    if (!name.endsWith(".rtf")) {
      file = new File(file.getAbsolutePath() + ".rtf");
    }
    
    final File finalFile = file;
    
    // 4. Background Writing Process
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          FileOutputStream fos = null;
          try {
            RTFEditorKit rtfKit = new RTFEditorKit();
            fos = new FileOutputStream(finalFile);
            
            // Write the styled content
            rtfKit.write(fos, txt.getDocument(), 0, txt.getDocument().getLength());
            fos.flush();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  String successMsg = "<html><body><font color=\"#4CAF50\" size=\"5\">"
                  + "Saved successfully as RTF!"
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
                  + "Error saving RTF: " + ex.getMessage()
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
   * after JFileChooser or JOptionPane is disposed.
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
