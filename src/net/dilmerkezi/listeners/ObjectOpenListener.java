package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

/**
 * Listener responsible for opening serialized Document objects from .object files.
 * Optimized to handle file selection on EDT and heavy deserialization in a background thread.
 *
 * Licensed under GPL v3.
 */
public class ObjectOpenListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser objjfc;
  private final Component parent;
  
  public ObjectOpenListener(JTextPane txt, JLabel statusLabel, JFileChooser objjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.objjfc = objjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // 1. ALWAYS show JFileChooser on the Event Dispatch Thread (EDT)
    int rep = objjfc.showOpenDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      forceFocus(); // Updated for focus stability
      return;
    }
    
    final File f = objjfc.getSelectedFile();
    if (f == null || !f.exists()) {
      forceFocus(); // Updated for focus stability
      return;
    }
    
    // File extension validation
    if (!f.getName().toLowerCase().endsWith(".object")) {
      JOptionPane.showMessageDialog(parent, "Not a supported Object file!");
      forceFocus(); // Ensure focus returns after dialog
      return;
    }
    
    // 2. Start background thread ONLY for heavy I/O (Deserialization)
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          ObjectInputStream ois = null;
          try {
            ois = new ObjectInputStream(new FileInputStream(f));
            // Read the serialized document object from file
            final Document d = (Document) ois.readObject();
            
            if (d != null) {
              // 3. Update UI components back on the EDT
              SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                    // Reset kit to handle potential style differences
                    txt.setEditorKit(new StyledEditorKit());
                    txt.setDocument(d);
                    
                    /**
                     * Attach UndoableEditListener AFTER setting the document.
                     * This prevents the initial load from being recorded as an undoable action.
                     */
                    if (UndoRedoConstants.ulis != null) {
                      txt.getDocument().addUndoableEditListener(UndoRedoConstants.ulis);
                    }
                    
                    txt.setCaretPosition(0);
                    txt.requestFocus();
                    statusLabel.setText("Object Document Loaded: " + f.getName());
                  }
              });
            }
            } catch (final Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  statusLabel.setText("Error loading object file.");
                  txt.requestFocus(); // Focus back on error
                }
            });
            } finally {
            // Ensure the stream is closed to prevent memory leaks
            try {
              if (ois != null) ois.close();
            } catch (IOException ignored) {}
          }
        }
    });
    t.start();
  }

  /**
   * Helper to ensure focus returns to text component 
   * after JFileChooser or JOptionPane is closed.
   */
  private void forceFocus() {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          txt.requestFocus();
        }
    });
  }
  
  @Override
  public String toString() {
    return "Object Open Listener";
  }
  
}
