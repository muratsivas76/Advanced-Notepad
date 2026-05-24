package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for saving file content in a background thread.
 * Optimized to handle dialogs on EDT and I/O on background thread.
 *
 * Licensed under GPL v3.
 */
public class SaveFileActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final JFileChooser jfc;
  private final Component parent;
  private final String charCode;
  
  public SaveFileActionListener(JTextPane txt, JLabel statusLabel, JFileChooser jfc, Component parent, String charCode) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.jfc = jfc;
    this.parent = parent;
    this.charCode = charCode;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    // 1. Show Save Dialog on EDT (This is mandatory for Swing)
    int rep = jfc.showSaveDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      forceFocus();
      return;
    }
    
    final File f = jfc.getSelectedFile();
    if (f == null) {
      forceFocus();
      return;
    }
    
    // 2. Overwrite check on EDT
    if (f.exists()) {
      String m = JOptionPane.showInputDialog(parent,
          "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
      
      if (m == null || m.trim().equals("")) {
        updateStatus("Cancelled Overwrite Saving Operation!");
        forceFocus();
        return;
      }
      
      if (!m.trim().equalsIgnoreCase("y") && !m.trim().equalsIgnoreCase("e")) {
        updateStatus("Cancelled Overwrite Saving Operation!");
        forceFocus();
        return;
      }
    }
    
    // 3. Perform heavy File I/O on background thread
    final String textToSave = txt.getText(); // Get text on EDT before starting thread
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          saveFile(f, textToSave);
        }
    });
    t.start();
  }
  
  private void saveFile(File file, String text) {
    OutputStream fos = null;
    OutputStreamWriter osw = null;
    try {
      fos = new FileOutputStream(file);
      osw = new OutputStreamWriter(fos, charCode);
      
      osw.write(text != null ? text : "");
      osw.flush();
      
      final String fileName = file.getName();
      SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(parent,
              "<html><body><font color=\"red\" size=\"5\">" + fileName + "</font>" +
              "<font color=\"blue\" size=\"5\"> saved successfully!<br><br>Char code: " + charCode + "</font></body></html>");
            txt.requestFocus();
          }
      });
      
    } catch (IOException io) {
      io.printStackTrace();
      updateStatus("Error: Could not save file.");
    } finally {
      try {
        if (osw != null) osw.close();
        if (fos != null) fos.close();
      } catch (IOException ignored) {}
    }
  }
  
  private void updateStatus(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          statusLabel.setText(message);
        }
    });
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
