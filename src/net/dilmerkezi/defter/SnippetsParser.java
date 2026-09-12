package net.dilmerkezi.defter;

import java.io.*;
import java.util.*;

public class SnippetsParser {
  
  /**
   * Parses the snippets file manually to ensure robust escape character handling.
   * Compatible with Java 6, 7, and 8.
   */
  public static Map parseSnippets(File file) {
    Map<String, String> snippets = new LinkedHashMap<>();
    // LinkedHashMap preserves order
    BufferedReader reader = null;
    
    try {
      // Using explicit encoding to prevent character issues
      reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
      String line;
      
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        
        // Skip empty lines and comments
        if (line.isEmpty() || line.startsWith("#") || line.startsWith("!")) {
          continue;
        }
        
        // Split at the first '=' to allow '=' inside the snippet content
        int separatorIndex = line.indexOf('=');
        if (separatorIndex != -1) {
          String key = line.substring(0, separatorIndex).trim();
          String value = line.substring(separatorIndex + 1);
          
          // Process custom escape sequences
          String processedValue = processEscapes(value);
          snippets.put(key, processedValue);
        }
      }
      } catch (IOException e) {
      e.printStackTrace();
      } finally {
      if (reader != null) {
        try { reader.close(); } catch (IOException ignored) {}
        }
    }
    return snippets;
  }
  
  /**
   * Converts literal "\n" and "\t" strings into actual newline and tab characters.
   */
  private static String processEscapes(String input) {
    if (input == null) return "";
    
    // Manual replacement to avoid regex issues in older Java versions
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '\\' && i + 1 < input.length()) {
        char next = input.charAt(i + 1);
        if (next == 'n') {
          sb.append('\n');
          i++;
          } else if (next == 't') {
          sb.append('\t');
          i++;
          } else if (next == '\\') {
          sb.append('\\');
          i++;
          } else {
          sb.append(c);
        }
        } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
}
