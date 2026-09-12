package net.dilmerkezi.filter;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ENCFileFilter extends FileFilter {
  
  @Override
  public boolean accept(File file) {
    if (file.isDirectory()) {
      return true;
    }
    
    String fileName = file.getName().toLowerCase();
    if (fileName.endsWith(".enc")) {
      return true;
    }
    
    return false;
  }
  
  @Override
  public String getDescription() {
    return "Encrypted Files";
  }
  
}
