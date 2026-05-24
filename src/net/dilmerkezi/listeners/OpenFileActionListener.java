package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.StyledEditorKit;

/**
 * Listener responsible for opening files in a background thread.
 * Optimized for performance by using direct stream reading to prevent EDT freezing.
 * Compatible with Java 6/7/8.
 *
 * Licensed under GPL v3.
 */
public class OpenFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser jfc;
  private final Component parent;
  private final String charCode;
  
  public OpenFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser jfc, Component parent, String charCode) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.jfc = jfc;
    this.parent = parent;
    this.charCode = charCode;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // 1. Show the dialog on the Event Dispatch Thread
    int rep = jfc.showOpenDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      forceFocus();
      return;
    }
    
    final File f = jfc.getSelectedFile();
    if (f == null) {
      forceFocus();
      return;
    }
    
    // 2. Start background thread for File I/O setup
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          BufferedReader br = null;
          try {
            // Create reader with specified charset
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), charCode));
            final BufferedReader finalReader = br;
            
            // 3. Update UI and Load Document on EDT
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  try {
                    // Clear existing content and reset EditorKit
                    txt.setEditorKit(new StyledEditorKit());
                    
                    /**
                     * IMPORTANT PERFORMANCE NOTE:
                     * Using txt.read() is significantly faster than txt.setText().
                     */
                    txt.read(finalReader, f);
                    
                    /**
                     * Attach UndoableEditListener AFTER loading content.
                     */
                    if (UndoRedoConstants.ulis != null) {
                      txt.getDocument().addUndoableEditListener(UndoRedoConstants.ulis);
                    }
                    
                    txt.setCaretPosition(0);
                    txt.requestFocus();
                    statusLabel.setText("Opened " + f.getName() + " successfully! Charset: " + charCode);
                    } catch (IOException ex) {
                    statusLabel.setText("Error while displaying content.");
                    ex.printStackTrace();
                    txt.requestFocus(); // Return focus even on display error
                    } finally {
                    try {
                      if (finalReader != null) finalReader.close();
                    } catch (IOException ignored) {}
                  }
                }
            });
            
            } catch (final IOException e) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  statusLabel.setText("Error: Could not read file.");
                  e.printStackTrace();
                  txt.requestFocus(); // Return focus on I/O error
                }
            });
          }
        }
    });
    t.start();
  }

  /**
   * Forces focus back to the JTextPane after dialog disposal
   * using SwingUtilities to ensure it's processed at the end of the EDT queue.
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
