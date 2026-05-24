package net.dilmerkezi.defter;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A professional Line Number component for JTextPane.
 * Handles Document swaps, HTML rendering delays, and heavy files efficiently.
 * Compatible with Java 6, 7, and 8+.
 */
public class LineNumberView extends JComponent {
  private final JTextPane textPane;
  private int lastLineCount = -1;
  private int lastWidth = -1;
  
  // Visual Constants
  private final Color BACKGROUND_COLOR = new Color(255, 255, 210); // Soft Yellow
  private final Color BORDER_COLOR = new Color(210, 210, 210);     // Light Grey
  private final Color TEXT_COLOR = new Color(100, 100, 100);       // Professional Dark Grey
  
  public LineNumberView(final JTextPane textPane) {
    this.textPane = textPane;
    
    // Listen for document content changes
    attachDocumentListener(textPane.getDocument());
    
    // Listen for JTextPane property changes (Font or Document swaps)
    textPane.addPropertyChangeListener(new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          String prop = evt.getPropertyName();
          if ("font".equals(prop) || "document".equals(prop)) {
            if ("document".equals(prop)) {
              attachDocumentListener((Document) evt.getNewValue());
            }
            updateSize();
          }
        }
    });
    
    // Repaint on caret movement to ensure synchronization during edits
    textPane.addCaretListener(new CaretListener() {
        public void caretUpdate(CaretEvent e) {
          repaint();
        }
    });
  }
  
  /**
   * Attaches a DocumentListener to the current document.
   */
  private void attachDocumentListener(Document doc) {
    if (doc == null) return;
    doc.addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e) { updateSize(); }
        public void removeUpdate(DocumentEvent e) { updateSize(); }
        public void changedUpdate(DocumentEvent e) { updateSize(); }
    });
  }
  
  /**
   * Updates the component's preferred size based on the line count.
   * Uses invokeLater to ensure the UI hierarchy is ready for calculation.
   */
  public void updateSize() {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          if (textPane == null || textPane.getFont() == null) return;
          
          Element root = textPane.getDocument().getDefaultRootElement();
          int lineCount = root.getElementCount();
          
          // If line count and font haven't changed, skip revalidation to save resources
          FontMetrics metrics = textPane.getFontMetrics(textPane.getFont());
          int width = metrics.stringWidth(String.valueOf(lineCount)) + 20;
          
          if (width != lastWidth || lineCount != lastLineCount) {
            lastWidth = width;
            lastLineCount = lineCount;
            
            // Use a large height to prevent layout conflicts with ScrollPane
            setPreferredSize(new Dimension(width, Integer.MAX_VALUE / 2));
            
            if (getParent() != null) {
              getParent().revalidate();
            }
          }
          repaint();
        }
    });
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    if (textPane == null || textPane.getFont() == null) return;
    
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    // 1. Paint Background
    g2.setColor(BACKGROUND_COLOR);
    g2.fillRect(0, 0, getWidth(), getHeight());
    
    // 2. Paint Right-side Border
    g2.setColor(BORDER_COLOR);
    g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
    
    // 3. Setup Font
    g2.setFont(textPane.getFont());
    FontMetrics metrics = g2.getFontMetrics();
    int ascent = metrics.getAscent();
    
    Element root = textPane.getDocument().getDefaultRootElement();
    int lineCount = root.getElementCount();
    
    // Performance: Only paint lines visible in the current viewport
    Rectangle clip = g.getClipBounds();
    
    for (int i = 0; i < lineCount; i++) {
      try {
        // Determine the Y coordinate of the line's start offset
        // Using modelToView is the only reliable way for HTML/Wrapped text
        @SuppressWarnings("deprecation")
        Rectangle r = textPane.modelToView(root.getElement(i).getStartOffset());
        
        if (r != null) {
          // Check visibility before drawing
          if (r.y + r.height >= clip.y && r.y <= clip.y + clip.height) {
            g2.setColor(TEXT_COLOR);
            String number = String.valueOf(i + 1);
            int x = getWidth() - metrics.stringWidth(number) - 8;
            g2.drawString(number, x, r.y + ascent);
          }
        }
        } catch (BadLocationException e) {
        // Safe to ignore during active document edits
      }
    }
  }
  
}
