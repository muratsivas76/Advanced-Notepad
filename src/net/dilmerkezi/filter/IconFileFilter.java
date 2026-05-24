package net.dilmerkezi.filter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class IconFileFilter extends FileFilter {
  
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    
    String fileName = file.getName().toLowerCase();
    for (String ext : Help.imageTypes) {
      if (fileName.endsWith(ext)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public String getDescription() {
    return "Image Files";
  }
  
}
