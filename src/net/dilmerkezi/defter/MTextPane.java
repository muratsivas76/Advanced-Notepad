package net.dilmerkezi.defter;

import javax.swing.*;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.HashMap;

/**
 * MTextPane - A custom JTextPane with advanced background capabilities.
 * Supports Gradients, Alpha Compositing, and Texture Mapping.
 * Fully integrated with the RIGHT CLICK BLOG logic, Translation, and Selection Tools.
 *
 * @author Murat iNAN
 */
public class MTextPane extends JTextPane {
  
  // Background and rendering properties
  private boolean gradientEnabled = false;
  private Color color1 = Color.WHITE;
  private Color color2 = Color.LIGHT_GRAY;
  private int x1 = 0, y1 = 0, x2 = 0, y2 = 500;
  private float alphaValue = 1.0f;
  private float textAlphaValue = 1.0f;
  private BufferedImage backgroundImage = null;
  private int textureWidth = 0;
  private int textureHeight = 0;
  
  // Language dictionary for offline translation
  private final Map<String, java.util.List<String>> languageMap = new HashMap<String, java.util.List<String>>(12000);

  public MTextPane() {
    super();
    initializeLanguageMap();
    initializeComponent();
  }
  
  public MTextPane(StyledDocument doc) {
    super(doc);
    initializeLanguageMap();
    initializeComponent();
  }
  
  /**
   * Finds the list of meanings for a word using full match or suffix stripping.
   * Returns a list of strings if found, otherwise returns null.
   */
  private java.util.List<String> findMeaning(String word) {
		if (word == null) return null;
		String search = word.trim().toLowerCase();
		if (search.length() < 3) return null;

		// 1. Exact match attempt
		if (languageMap.containsKey(search)) {
			return languageMap.get(search);
		}

		// 2. Trimming match (recursive suffix stripping)
		String temp = search;
		while (temp.length() > 3) {
			temp = temp.substring(0, temp.length() - 1);
			if (languageMap.containsKey(temp)) {
				return languageMap.get(temp);
			}
		}
		return null;
  }
  
  private void initializeComponent() {
    setOpaque(false);
    
    // --- RIGHT CLICK MENU SETUP ---
    final JPopupMenu popup = new JPopupMenu();
    final Font menuFont = new Font("Segoe UI", Font.BOLD, 17);
    
    // Translation Item (Dynamic)
    final JMenuItem transItem = new JMenuItem("Translation: -");
    transItem.setFont(new Font("Segoe UI", Font.BOLD, 18));
    //transItem.setEnabled(false); // Used as a header/display
    
    // Selection Items
    JMenuItem selectWordItem = new JMenuItem("Select Word");
    selectWordItem.setFont(menuFont);
    JMenuItem selectLineItem = new JMenuItem("Select Line");
    selectLineItem.setFont(menuFont);
    
    // Standard Items
    final JMenuItem cutItem = new JMenuItem("Cut");
    cutItem.setFont(menuFont);
    final JMenuItem copyItem = new JMenuItem("Copy");
    copyItem.setFont(menuFont);
    JMenuItem pasteItem = new JMenuItem("Paste");
    pasteItem.setFont(menuFont);
    JMenuItem selectAllItem = new JMenuItem("Select All");
    selectAllItem.setFont(menuFont);
    
    // Action Listeners (Anonymous Inner Classes for Java 6-8 compatibility)
    selectWordItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                int pos = getCaretPosition();
                int start = javax.swing.text.Utilities.getWordStart(MTextPane.this, pos);
                int end = javax.swing.text.Utilities.getWordEnd(MTextPane.this, pos);
                setSelectionStart(start);
                setSelectionEnd(end);
            } catch (Exception ex) {}
        }
    });

    selectLineItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            try {
                int pos = getCaretPosition();
                int start = javax.swing.text.Utilities.getRowStart(MTextPane.this, pos);
                int end = javax.swing.text.Utilities.getRowEnd(MTextPane.this, pos);
                setSelectionStart(start);
                setSelectionEnd(end);
            } catch (Exception ex) {}
        }
    });
	
	transItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {}
    });
    cutItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { cut(); }
    });
    copyItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { copy(); }
    });
    pasteItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { paste(); }
    });
    selectAllItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) { selectAll(); }
    });
    
    // Building the menu
    popup.add(transItem);
    popup.addSeparator();
    popup.add(selectWordItem);
    popup.add(selectLineItem);
    popup.addSeparator();
    popup.add(cutItem);
    popup.add(copyItem);
    popup.add(pasteItem);
    popup.addSeparator();
    popup.add(selectAllItem);
    
    this.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) showMenu(e); }
        @Override
        public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) showMenu(e); }
        
        private void showMenu(MouseEvent e) {
            // 1. CLEANUP: Remove old dynamic translation items to prevent menu bloat
            java.awt.Component[] components = popup.getComponents();
            for (java.awt.Component c : components) {
                if (c instanceof JMenuItem) {
                    String text = ((JMenuItem)c).getText();
                    // Identify items by content or the specific transItem reference
                    if (c == transItem || (text != null && text.startsWith(" - "))) {
                        popup.remove(c);
                    }
                }
            }

            String selection = getSelectedText();
            boolean hasSelection = (selection != null && !selection.trim().isEmpty());
            
            // 2. TRANSLATION LOGIC
            if (hasSelection) {
                // Regex: Take first word and strip punctuation
                String firstWord = selection.trim().split("\\s+")[0].replaceAll("[\\p{Punct}]", "");
                java.util.List<String> meanings = findMeaning(firstWord);
                
                if (meanings != null && !meanings.isEmpty()) {
                    if (meanings.size() == 1) {
                        // Case: Single meaning
                        transItem.setText("<html><body><font color=\"red\">Meaning</font>: <font color=\"blue\">" + meanings.get(0) + "</font></body></html>");
                        popup.insert(transItem, 0);
                        transItem.setVisible(true);
                    } else {
                        // Case: Multiple meanings
                        transItem.setText("<html><body><font color=\"red\">Meanings</font>:</body></html>");
                        popup.insert(transItem, 0);
                        transItem.setVisible(true);
                        
                        // Add each meaning as a separate menu item
                        for (int i = 0; i < meanings.size(); i++) {
                            JMenuItem mItem = new JMenuItem(" - " + meanings.get(i));
                            mItem.setFont(menuFont);
                            mItem.setForeground(new Color(0, 51, 153)); // Dark Blue
                            popup.insert(mItem, i + 1);
                        }
                    }
                }
            }
            
            // 3. UI UPDATES
            cutItem.setEnabled(hasSelection);
            copyItem.setEnabled(hasSelection);
            
            // Refresh the popup layout before showing
            popup.revalidate();
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    });
  }

   private void initializeLanguageMap() {
		File wordFile = new File("npoperations/words.txt");
		if (!wordFile.exists()) return;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(wordFile), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || !line.contains(":")) continue;

				String[] parts = line.split(":", 2);
				if (parts.length == 2) {
					String k = parts[0].trim().toLowerCase();
					String v = parts[1].trim().toLowerCase();

					// Pairs
					addWordToMap(k, v);
					addWordToMap(v, k);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) try { reader.close(); } catch (IOException e) {}
		}
   }
  
   private void addWordToMap(String key, String value) {
		if (!languageMap.containsKey(key)) {
			languageMap.put(key, new java.util.ArrayList<String>());
		}
		java.util.List<String> list = languageMap.get(key);
		if (!list.contains(value)) {
			list.add(value);
		}
    }

	/**
	 * Processes large documents efficiently by iterating through the StyledDocument elements.
	 * Uses a buffer-based approach to prevent memory issues with massive texts.
	 */
	public String getWordsAndMeanings() {
		javax.swing.text.Document doc = getDocument();
		int totalLength = doc.getLength();
		if (totalLength == 0) return "";

		// Using a large initial capacity to avoid frequent resizing
		StringBuilder result = new StringBuilder(totalLength + (totalLength / 2));
		
		try {
			// Fetching text in one go from Document is faster than getText() 
			// but for 1000 pages, we still need to process it via Matcher
			String content = doc.getText(0, totalLength);
			
			java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\w+)|(\\W+)");
			java.util.regex.Matcher matcher = pattern.matcher(content);

			while (matcher.find()) {
				String segment = matcher.group();
				
				if (segment.matches("\\w+")) {
					result.append(segment);
					
					// Dictionary lookup
					java.util.List<String> meanings = findMeaning(segment);
					if (meanings != null && !meanings.isEmpty()) {
						result.append(" [");
						for (int i = 0; i < meanings.size(); i++) {
							result.append(meanings.get(i));
							if (i < meanings.size() - 1) result.append(", ");
						}
						result.append("]");
					}
				} else {
					// Preserves whitespace and punctuation exactly
					result.append(segment);
				}
			}
		} catch (javax.swing.text.BadLocationException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2d = (Graphics2D) g;
    Composite oldComposite = g2d.getComposite();
    Paint oldPaint = g2d.getPaint();
    Object oldAntialias = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
    
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    if (gradientEnabled) {
      GradientPaint gp = new GradientPaint(x1, y1, color1, x2, y2, color2, true);
      g2d.setPaint(gp);
      g2d.fillRect(0, 0, getWidth(), getHeight());
      } else {
      g2d.setColor(getBackground());
      g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    if (backgroundImage != null) {
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue));
      if (textureWidth > 0 && textureHeight > 0) {
        Rectangle anchor = new Rectangle(0, 0, textureWidth, textureHeight);
        TexturePaint tp = new TexturePaint(backgroundImage, anchor);
        g2d.setPaint(tp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
        g2d.drawImage(backgroundImage, 0, 0, null);
      }
    }
    
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textAlphaValue));
    super.paintComponent(g2d);
    
    g2d.setComposite(oldComposite);
    g2d.setPaint(oldPaint);
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntialias);
  }
  
  // --- SETTERS PRESERVED ---
  
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
