package net.dilmerkezi.defter;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Robust Regex Safety Checker.
 * Designed to catch exponential backtracking patterns (ReDoS).
 */
public class ProductionRegexSafetyCheck {
  
  private static final int MAX_REGEX_LENGTH = 100;
  private static final long LARGE_TEXT_THRESHOLD = 1024 * 1024; // 1MB
  
  /**
   * Strategic patterns to detect structural ReDoS risks.
   */
  private static final String[] DANGER_RADAR = {
    // 1. Nested Quantifiers: (a+)+ or (.*)* or ([a-z]+)*
    // Look for a quantifier inside a group, followed by another quantifier outside.
    "\\([^)]*[+*?]\\)[+*?]",
    
    // 2. Quantified Alternation: (a|b)+ or (.*|a)+
    // Look for a pipe symbol inside a group, followed by a quantifier outside.
    "\\([^)]*\\|[^)]*\\)[+*?]",
    
    // 3. Sequential Quantifiers: ++ or ** or +*
    "[+*?]{2,}",
    
    // 4. Grouped repetition with potential overlap: (a|bc)*
    "(\\([^)]+\\)[+*?]){2,}"
  };
  
  public static String checkForLargeText(String regex, long estimatedTextSize) {
    if (regex == null || regex.isEmpty()) return "SAFE";
    
    // Check 1: Maximum Length
    if (regex.length() > MAX_REGEX_LENGTH) return "WARNING";
    
    // Check 2: Structural Analysis (The Core Protection)
    // We use a simplified version of the regex to analyze its structure
    for (String dangerPattern : DANGER_RADAR) {
      Pattern p = Pattern.compile(dangerPattern);
      if (p.matcher(regex).find()) {
        return "DANGEROUS";
      }
    }
    
    // Check 3: Depth Check (Manual parsing for speed)
    if (getNestingDepth(regex) > 2) return "DANGEROUS";
    
    return (estimatedTextSize > LARGE_TEXT_THRESHOLD) ? "WARNING" : "SAFE";
  }
  
  private static int getNestingDepth(String regex) {
    int max = 0, current = 0;
    for (char c : regex.toCharArray()) {
      if (c == '(') {
        current++;
        if (current > max) max = current;
        } else if (c == ')') {
        current--;
      }
    }
    return max;
  }
  
}
