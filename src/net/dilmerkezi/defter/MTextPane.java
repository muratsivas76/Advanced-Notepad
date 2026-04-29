package net.dilmerkezi.defter;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * MTextPane - A custom JTextPane with advanced background capabilities.
 * Supports Gradients, Alpha Compositing, and Texture Mapping.
 * Compatible with Java 6, 7, and 8.
 * 
 * @author Murat
 */
public class MTextPane extends JTextPane {

    // Gradient properties
    private boolean gradientEnabled = false;
    private Color color1 = Color.WHITE;
    private Color color2 = Color.LIGHT_GRAY;
    private int x1 = 0, y1 = 0, x2 = 0, y2 = 500;

    // Alpha/Opacity properties (0.0f to 1.0f)
    private float alphaValue = 1.0f;
    private float textAlphaValue = 1.0f;
    
    // Image/Texture properties
    private BufferedImage backgroundImage = null;
    private int textureWidth = 0;
    private int textureHeight = 0;

    public MTextPane() {
        super();
        setOpaque(false); // Required for custom background rendering
    }

    public MTextPane(StyledDocument doc) {
        super(doc);
        setOpaque(false);
    }

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		// Backup original states to restore after painting
		Composite oldComposite = g2d.getComposite();
		Paint oldPaint = g2d.getPaint();
		Object oldAntialias = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

		// Enable Anti-Aliasing for smoother gradients
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 1. Draw Gradient Background
		if (gradientEnabled) {
			GradientPaint gp = new GradientPaint(x1, y1, color1, x2, y2, color2);
			g2d.setPaint(gp);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		} else {
			// Default background color if gradient is off
			g2d.setColor(getBackground());
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		// 2. Draw Image or Texture with Alpha Composite
		if (backgroundImage != null) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
			
			if (textureWidth > 0 && textureHeight > 0) {
				// Render as a tiled texture
				Rectangle anchor = new Rectangle(0, 0, textureWidth, textureHeight);
				TexturePaint tp = new TexturePaint(backgroundImage, anchor);
				g2d.setPaint(tp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			} else {
				// Render as a single image at top-left
				g2d.drawImage(backgroundImage, 0, 0, null);
			}
		}

		// 3. Render the text content
		// Set alpha for text and selection rendering
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlphaValue));
		
		// Using the main graphics object directly to ensure proper mouse selection and event handling
		super.paintComponent(g2d);

		// Restore original graphics state to prevent UI glitches in other components
		g2d.setComposite(oldComposite);
		g2d.setPaint(oldPaint);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
	}

    // --- Getters and Setters for Menu Items ---

    public void setGradientEnabled(boolean enabled) {
        this.gradientEnabled = enabled;
        repaint();
    }

    public void setGradientColors(Color c1, Color c2) {
        this.color1 = c1;
        this.color2 = c2;
        repaint();
    }

    public void setGradientCoords(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        repaint();
    }

    public void setAlphaValue(float alpha) {
        if (alpha < 0.01f) alpha = 0.01f;
        if (alpha > 1.0f) alpha = 1.0f;
        this.alphaValue = alpha;
        repaint();
    }

    public void setTextAlphaValue(float alpha) {
        if (alpha < 0.01f) alpha = 0.01f;
        if (alpha > 1.0f) alpha = 1.0f;
        this.textAlphaValue = alpha;
        repaint();
    }

    public void setBackgroundImage(BufferedImage img, int w, int h) {
        this.backgroundImage = img;
        this.textureWidth = w;
        this.textureHeight = h;
        repaint();
    }
    
}
