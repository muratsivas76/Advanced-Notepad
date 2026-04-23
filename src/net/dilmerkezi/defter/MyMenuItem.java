package net.dilmerkezi.defter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Custom JMenuItem for a dark-themed UI.
 * Fully compatible with Java 6, 7, and 8.
 */
public class MyMenuItem extends JMenuItem {
  
  private boolean isEntered = false;
  private String accelText = "";
  
  // UI Style Constants
  private static final Color NORMAL_BG = Color.decode("#FFFFFF");
  private static final Color HOVER_BG = Color.decode("#4B6EAF");
  private static final Color NORMAL_FG = Color.decode("#000000");
  private static final Color HOVER_FG = Color.decode("#CCCC00");
  private static final Color ACCE_COLOR = Color.decode("#0033DD");
  private static final Font ITEM_FONT = new Font("Segoe UI", Font.PLAIN, 18);
  private static final Font ACCE_FONT = new Font("SansSerif", Font.BOLD|Font.ITALIC, 13);
  
  public MyMenuItem(final String text, int acceleratorKey, String accelText) {
    // Initialize JMenuItem with text
    super(text);
    
    this.accelText = accelText;
    
    // Remove default Swing painting to gain full control
    //setOpaque(false);
    //setContentAreaFilled(false);
    //setBorderPainted(false);
    //setFocusable(false);
    
    // Manage individual hover state
    addMouseListener(new MouseListener() {
        @Override
        public void mouseEntered(MouseEvent e) {
          isEntered = true;
          repaint();
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
          isEntered = false;
          repaint();
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
          isEntered = false;
          repaint();
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
          isEntered = true;
          repaint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
          isEntered = false;
          repaint();
        }
    });
    
    // Set Accelerator (e.g., CTRL + S)
    if (acceleratorKey != -1) {
      setAccelerator(KeyStroke.getKeyStroke(acceleratorKey, ActionEvent.CTRL_MASK));
    }
  }
  
  @Override
  public Dimension getPreferredSize() {
    Dimension d = super.getPreferredSize();
    return new Dimension(d.width+33, 28);
    // 25px yukseklik daha ferah bir gorunum saglar
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    //super.paintComponent(g);
    
    Graphics2D g2d = (Graphics2D) g;
    
    // Enable Anti-Aliasing for smooth text and shapes
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    
    // 1. Draw Background
    if (isEntered) {
      g2d.setColor(HOVER_BG);
      } else {
      g2d.setColor(NORMAL_BG);
    }
    g2d.fillRect(0, 0, getWidth(), getHeight());
    
    // 2. Setup Font and Color for Text
    g2d.setFont(ITEM_FONT);
    if (isEntered) {
      g2d.setColor(HOVER_FG);
      } else {
      g2d.setColor(NORMAL_FG);
    }
    
    // 3. Draw Text (Vertically Centered)
    FontMetrics fm = g2d.getFontMetrics();
    int textX = 7; // Left margin
    int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent() - 3;
    
    // Use the text from JMenuItem's getText() method
    g2d.drawString(getText(), textX, textY);
    
    if (accelText.length () >= 6) {
      g2d.setFont(ACCE_FONT);
      g2d.setColor(ACCE_COLOR);
      g2d.drawString(accelText, (getPreferredSize().width) - (fm.stringWidth(accelText)) - 10, textY);
    }
  }
  
}
