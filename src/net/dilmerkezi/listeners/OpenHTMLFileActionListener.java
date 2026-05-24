package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for opening and displaying HTML files.
 * Uses a JEditorPane within a scrollable dialog to render the content.
 *
 * Licensed under GPL v3.
 */
public class OpenHTMLFileActionListener implements ActionListener {
  
  private final JEditorPane helpTextArea;
  private final JLabel statusLabel;
  private final JFileChooser htmljfc;
  private final Component parent;
  
  public OpenHTMLFileActionListener(JEditorPane helpTextArea, JLabel statusLabel, JFileChooser htmljfc, Component parent) {
    this.helpTextArea = helpTextArea;
    this.statusLabel = statusLabel;
    this.htmljfc = htmljfc;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    openHTML();
  }
  
  /**
   * Executes the HTML file loading and display logic.
   */
  public void openHTML() {
    statusLabel.setText("");
    
    // 1. Show open dialog
    int rep = htmljfc.showOpenDialog(parent);
    if (rep != JFileChooser.APPROVE_OPTION) {
      statusLabel.setText("Cancelled Opening HTML File!");
      return;
    }
    
    final File file = htmljfc.getSelectedFile();
    
    // Check if file exists
    if (file == null || !file.exists()) {
      statusLabel.setText("File is Null or Does not Exist!");
      return;
    }
    
    // 2. Validate file extension (.html or .htm)
    String name = file.getName().toLowerCase();
    if (!name.endsWith(".html") && !name.endsWith(".htm")) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">" + file.getName()
      + "</font><font color=\"blue\" size=\"5\"> is not a supported HTML File!</font></body></html>";
      JOptionPane.showMessageDialog(parent, errorMsg);
      return;
    }
    
    // 3. Perform loading and rendering
    Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            // Set content type and load page (Java 6+ URI/URL handling)
            final java.net.URL url = file.toURI().toURL();
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  try {
                    helpTextArea.setContentType("text/html");
                    helpTextArea.setPage(url);
                    helpTextArea.setCaretPosition(0);
                    
                    // Create scrollable viewer for the HTML content
                    JScrollPane scrollPane = new JScrollPane(helpTextArea);
                    scrollPane.setPreferredSize(new Dimension(900, 600));
                    
                    // Display in a professional dialog
                    JOptionPane.showMessageDialog(parent, scrollPane, "HTML Viewer", JOptionPane.INFORMATION_MESSAGE);
                    
                    statusLabel.setText("HTML Viewer closed.");
                    } catch (Exception e) {
                    showErrorMessage("Rendering Error: " + e.getMessage());
                  }
                }
            });
            
            } catch (final Exception ex) {
            ex.printStackTrace();
            showErrorMessage("Error opening HTML: " + ex.getMessage());
          }
        }
    });
    t.start();
  }
  
  /**
   * Helper to show formatted error messages on the EDT.
   */
  private void showErrorMessage(final String message) {
    SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          String msg = "<html><body><font color=\"red\" size=\"5\">" + message + "</font></body></html>";
          JOptionPane.showMessageDialog(parent, msg);
        }
    });
  }
  
}
