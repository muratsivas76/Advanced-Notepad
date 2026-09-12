package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

/**
 * Listener responsible for loading RTF documents into the JTextPane.
 * Optimized to perform heavy RTF parsing in a background thread to keep UI responsive.
 * 
 * Focus: Uses invokeLater to ensure reliable focus recovery after file selection.
 *
 * Licensed under GPL v3.
 */
public class OpenRTFFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser rtfjfc;
  private final Component parent;
  
  public OpenRTFFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser rtfjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.rtfjfc = rtfjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    openRTF();
  }
  
  public void openRTF() {
    statusLabel.setText("");
    
    // 1. Show dialog on the Event Dispatch Thread (EDT)
    int rep = rtfjfc.showOpenDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      statusLabel.setText("Cancelled Opening File!");
      forceFocus(); // Guaranteed focus recovery
      return;
    }
    
    final File file = rtfjfc.getSelectedFile();
    if (file == null || !file.exists()) {
      forceFocus(); // Guaranteed focus recovery
      return;
    }
    
    // Extension validation
    if (!file.getName().toLowerCase().endsWith(".rtf")) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">Not a supported RTF file!</font></body></html>";
      JOptionPane.showMessageDialog(parent, errorMsg);
      forceFocus(); // Focus after error dialog
      return;
    }
    
    // 2. Load and Parse RTF in a background thread
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          FileInputStream fis = null;
          try {
            final RTFEditorKit rtfKit = new RTFEditorKit();
            final DefaultStyledDocument doc = new DefaultStyledDocument();
            fis = new FileInputStream(file);
            
            // Background parsing
            rtfKit.read(fis, doc, 0);
            fis.close();
            
            // 3. UI Update on EDT
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setEditorKit(rtfKit);
                  txt.setDocument(doc);
                  
                  if (UndoRedoConstants.ulis != null) {
                    txt.getDocument().addUndoableEditListener(UndoRedoConstants.ulis);
                  }
                  
                  txt.setCaretPosition(0);
                  txt.requestFocus();
                  statusLabel.setText("RTF Loaded: " + file.getName());
                }
            });
            
            } catch (final Exception e) {
            e.printStackTrace();
            updateErrorStatus("Error Parsing RTF: " + e.getMessage());
            } finally {
            try {
              if (fis != null) fis.close();
            } catch (IOException ignored) {}
          }
        }
    });
    t.start();
  }

  /**
   * Restores focus safely using SwingUtilities queue.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
  private void updateErrorStatus(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          statusLabel.setText(message);
          txt.requestFocus(); // Focus back after error status update
        }
    });
  }
  
}
