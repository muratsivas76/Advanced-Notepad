package net.dilmerkezi.filter;

import javax.swing.*;
import java.awt.*;
import java.beans.*;
import java.io.File;

public class ImagePreview extends JComponent implements PropertyChangeListener {
    private ImageIcon thumbnail = null;
    private File file = null;

    public ImagePreview(JFileChooser fc) {
        setPreferredSize(new Dimension(150, 150));
        fc.addPropertyChangeListener(this);
    }

    public void loadImage() {
        if (file == null) {
            thumbnail = null;
            return;
        }
        ImageIcon tmpIcon = new ImageIcon(file.getPath());
        if (tmpIcon != null) {
            if (tmpIcon.getIconWidth() > 140) {
                thumbnail = new ImageIcon(tmpIcon.getImage().getScaledInstance(140, -1, Image.SCALE_DEFAULT));
            } else {
                thumbnail = tmpIcon;
            }
        }
    }

    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
            file = (File) e.getNewValue();
            if (isShowing()) {
                loadImage();
                repaint();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (thumbnail == null) {
            loadImage();
        }
        if (thumbnail != null) {
            int x = getWidth()/2 - thumbnail.getIconWidth()/2;
            int y = getHeight()/2 - thumbnail.getIconHeight()/2;

            if (y < 0) y = 0;
            if (x < 0) x = 0;

            thumbnail.paintIcon(this, g, x, y);
        }
    }
    
}
