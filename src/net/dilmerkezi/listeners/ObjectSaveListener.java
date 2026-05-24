package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

/**
 * Listener responsible for saving the JTextPane Document as a serialized .object file.
 * Maintains overwrite confirmation and automatic extension handling.
 * 
 * Focus Management: Uses invokeLater to ensure focus returns to editor after
 * multiple modal dialog sequences.
 *
 * Licensed under GPL v3.
 */
public class ObjectSaveListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser objjfc;
  private final Component parent;
  
  public ObjectSaveListener(JTextPane txt, JLabel statusLabel, JFileChooser objjfc, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.objjfc = objjfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    saveAtr();
  }
  
  private boolean saveAtr() {
    int rep = objjfc.showSaveDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      statusLabel.setText("Cancelled Saving File!");
      forceFocus();
      return false;
    }
    
    File f = objjfc.getSelectedFile();
    if (f == null) {
      statusLabel.setText("Null or Non-Existent File For Saving!");
      forceFocus();
      return false;
    }
    
    // 1. Overwrite Confirmation Logic
    if (f.exists()) {
      String m = JOptionPane.showInputDialog(parent,
      "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
      
      if (m == null || m.equals("")) {
        statusLabel.setText("Cancelled Overwrite Object Saving Operation!");
        forceFocus();
        return false;
      }
      
      if (!m.equalsIgnoreCase("y") && !m.equalsIgnoreCase("e")) {
        statusLabel.setText("Cancelled Overwrite Object Saving Operation!");
        forceFocus();
        return false;
      }
    }
    
    // 2. Extension Handling
    String name = f.getName().toLowerCase();
    if (!name.endsWith(".object")) {
      f = new File(f.getAbsolutePath() + ".object");
    }
    
    final File finalFile = f;
    
    // 3. Serialization Process
    Thread saveThread = new Thread(new Runnable() {
        @Override
        public void run() {
          FileOutputStream fos = null;
          ObjectOutputStream oos = null;
          try {
            fos = new FileOutputStream(finalFile);
            oos = new ObjectOutputStream(fos);
            
            Document d = txt.getDocument();
            if (d != null) {
              oos.writeObject(d);
            }
            
            oos.flush();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  JOptionPane.showMessageDialog(parent,
                    "<html><body><font color=\"red\" size=\"5\">" + finalFile.getName() +
                  "</font><font color=\"blue\" size=\"4\"> saved successfully!</font></body></html>");
                  txt.requestFocus(); // Focus after success message
                }
            });
            
            } catch (final Exception ex) {
            ex.printStackTrace();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  statusLabel.setText(ex.getMessage() + " error during save");
                  txt.requestFocus(); // Focus on error
                }
            });
            } finally {
            try {
              if (oos != null) oos.close();
              if (fos != null) fos.close();
              } catch (Exception e) {
              // Silent close
            }
          }
        }
    });
    saveThread.start();
    
    return true;
  }

  /**
   * Safe focus restoration after dialogs.
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
    return "Object Save Listener";
  }
  
}
