package net.dilmerkezi.defter;

/**
 * Data holder for Regex search results to be passed between threads.
 */
public class MatchResult {
  public final int start;
  public final int end;
  public final String matchedText;
  public final boolean found;
  
  public MatchResult(int start, int end, String matchedText, boolean found) {
    this.start = start;
    this.end = end;
    this.matchedText = matchedText;
    this.found = found;
  }
  
}
