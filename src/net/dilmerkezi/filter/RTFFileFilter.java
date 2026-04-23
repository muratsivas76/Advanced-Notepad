package net.dilmerkezi.filter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class RTFFileFilter extends FileFilter {
  
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    
    String fileName = file.getName().toLowerCase();
    if (fileName.endsWith(".rtf")) {
      return true;
    }
    
    return false;
  }
  
  @Override
  public String getDescription() {
    return "RTF Files";
  }
  
}
