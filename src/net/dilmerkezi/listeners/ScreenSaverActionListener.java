package net.dilmerkezi.listeners;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

/**
 * Listener responsible for capturing the JTextPane content as an image.
 * Saves screenshots as PNG with auto-incrementing formatted names.
 * 
 * Focus: Stabilized to ensure focus returns to JTextPane after save notification.
 *
 * Licensed under GPL v3.
 */
public class ScreenSaverActionListener implements ActionListener {
  
  private final JTextPane txt;
  private final JLabel statusLabel;
  private final Component parent;
  
  private static int imgscr = 0;
  private static final int CMIL = 4000;
  
  public ScreenSaverActionListener(JTextPane txt, JLabel statusLabel, Component parent) {
    this.txt = txt;
    this.statusLabel = statusLabel;
    this.parent = parent;
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    statusLabel.setText("");
    
    int mw = txt.getSize().width;
    int mh = txt.getSize().height;
    
    // 1. Dimension Validation
    if (mw > CMIL || mh > CMIL) {
      String warnMsg = "<html><body><font color=\"red\" size=\"5\">"
      + "Too Big Photo; Not Saved!"
      + "</font></body></html>";
      statusLabel.setText("Failed: Dimension limit exceeded.");
      JOptionPane.showMessageDialog(parent, warnMsg);
      forceFocus(); // Ensure focus returns even on failure
      return;
    }
    
    // 2. Prepare Buffer and Graphics
    boolean isbuf = txt.isDoubleBuffered();
    txt.setDoubleBuffered(false);
    
    BufferedImage bim = new BufferedImage(mw, mh, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bim.createGraphics();
    
    // Capture content
    txt.paint(g2);
    g2.dispose();
    
    txt.setDoubleBuffered(isbuf);
    
    // 3. File Naming Logic
    imgscr++;
    String fileName = String.format("screen_%03d.png", imgscr);
    
    File dir = new File("npoperations");
    if (!dir.exists()) {
      dir.mkdir();
    }
    
    File fscreen = new File(dir, fileName);
    
    // 4. Save and Notify
    try {
      ImageIO.write(bim, "png", fscreen);
      
      String successMsg = "<html><body><font color=\"#4CAF50\" size=\"5\">"
      + "npoperations/" + fscreen.getName() + " is ready!"
      + "</font></body></html>";
      JOptionPane.showMessageDialog(parent, successMsg);
      
      } catch (Exception ex) {
      String errorMsg = "<html><body><font color=\"red\" size=\"5\">"
      + "Error: " + ex.getMessage()
      + "</font></body></html>";
      JOptionPane.showMessageDialog(parent, errorMsg);
      statusLabel.setText("Screenshot Error!");
    } finally {
      forceFocus(); // Guaranteed focus recovery
    }
  }

  /**
   * Safe focus restoration after dialogs using invokeLater.
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
