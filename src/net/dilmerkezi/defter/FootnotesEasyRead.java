package net.dilmerkezi.defter;

import java.util.*;
import java.util.regex.*;

/**
 * FootnotesEasyRead - Converts numbered footnotes to inline annotations
 *
 * This class processes text with numbered footnotes in the format:
 *   245. Medrese: okul
 *
 * And converts occurrences of "medrese245" in the main text to "medrese [okul]"
 * The number must be attached to the word (no space between word and number).
 *
 * Usage from within Notepad:
 *   String result = FootnotesEasyRead.process(srcText);
 */
public class FootnotesEasyRead {
  
  /**
   * Process text with numbered footnotes and return text with inline annotations
   *
   * @param srcText The full text including footnotes in "245. Term: definition" format
   * @return Processed text with inline annotations (term [definition])
   */
  public static String process(String srcText) {
    if (srcText == null || srcText.trim().isEmpty()) {
      return srcText;
    }
    
    // Split text into lines
    String[] linesArray = srcText.split("\n");
    List<String> lines = new ArrayList<String>();
    for (String line : linesArray) {
      lines.add(line);
    }
    
    // Parse footnotes by number from the text
    Map<String, String> footnotesByNumber = parseFootnotesByNumber(lines);
    
    if (footnotesByNumber.isEmpty()) {
      return srcText;
    }
    
    // Apply footnotes to the main text (word + number pattern)
    return applyFootnotesByNumber(srcText, footnotesByNumber);
  }
  
  /**
   * Parse footnotes by number from lines (format: "245. Term: definition")
   *
   * @param lines List of text lines
   * @return Map of number to definition
   */
  private static Map<String, String> parseFootnotesByNumber(List<String> lines) {
    Map<String, String> footnotes = new LinkedHashMap<String, String>();
    Pattern footnotePattern = Pattern.compile("^(\\d+)\\.\\s*([^:]+):\\s*(.+)$");
    
    for (String line : lines) {
      Matcher matcher = footnotePattern.matcher(line.trim());
      if (matcher.matches()) {
        String number = matcher.group(1);
        String definition = matcher.group(3).trim();
        
        // Keep only the first definition if duplicate numbers exist
        if (!footnotes.containsKey(number)) {
          footnotes.put(number, definition);
        }
      }
    }
    
    return footnotes;
  }
  
  /**
   * Apply footnotes to text - replaces "word123" with "word [definition]"
   * The number must be attached to the word (no space between word and number)
   *
   * @param text The original text
   * @param footnotesByNumber Map of number to definition
   * @return Text with inline annotations
   */
  private static String applyFootnotesByNumber(String text, Map<String, String> footnotesByNumber) {
    if (footnotesByNumber.isEmpty()) {
      return text;
    }
    
    // Pattern: word (letters) + numbers (footnote number)
    // Captures words ending with numbers
    Pattern wordWithNumberPattern = Pattern.compile("(\\p{L}+)(\\d+)", Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = wordWithNumberPattern.matcher(text);
    StringBuffer result = new StringBuffer();
    
    while (matcher.find()) {
      String word = matcher.group(1);      // word: "medrese"
      String number = matcher.group(2);    // number: "245"
      
      // Check if this number has a footnote
      String definition = footnotesByNumber.get(number);
      
      if (definition != null) {
        definition = definition.trim();
        // Footnote exists: word + [definition] + space
        // Space added to prevent merging with next word
        if (definition.endsWith(".")) {
          definition = definition.substring(0, definition.length()-1);
        }
        matcher.appendReplacement(result, Matcher.quoteReplacement(word + " [" + definition + "] "));
        } else {
        // No footnote: keep as is
        matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
      }
    }
    matcher.appendTail(result);
    
    return result.toString();
  }
  
}
