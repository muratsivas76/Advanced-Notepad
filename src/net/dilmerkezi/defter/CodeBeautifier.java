package net.dilmerkezi.defter;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

/**
 * High-Reliability Java Code Beautifier Engine
 * Implements a strict lexer state-machine to handle escape sequences and multi-line literals.
 */
public class CodeBeautifier {
  
    private static class Context {
        boolean inString = false;
        boolean inChar = false;
        boolean inMultiComment = false;
    }
    
    /**
     * Executes the primary formatting routine with strict character escape evaluation.
     * Processes input text and returns the beautified code as a String.
     */
    public static String format(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
		
		final int spaces = 2;
		
        BufferedReader reader = new BufferedReader(new StringReader(text));
        StringBuilder output = new StringBuilder();
        
        try {
            String line;
            int level = 0;
            Context ctx = new Context(); // Maintained globally across rows to handle multi-line blocks
            boolean first = true;
            
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                
                // Preserve completely vacant structural rows natively
                if (trimmed.isEmpty()) {
                    if (!first) output.append("\n");
                    first = false;
                    continue;
                }
                
                int open = 0;
                int close = 0;
                boolean startsWithClose = false;
                boolean foundFirstMeaningful = false;
                
                // Track active state at the precise start of this physical text line
                boolean initialInComment = ctx.inMultiComment;
                boolean initialInLiteral = ctx.inString || ctx.inChar;
                
                for (int i = 0; i < trimmed.length(); i++) {
                    char c = trimmed.charAt(i);
                    char next = (i + 1 < trimmed.length()) ? trimmed.charAt(i + 1) : '\0';
                    
                    // 1. Handle Multi-line Comment state mutations
                    if (ctx.inMultiComment) {
                        if (c == '*' && next == '/') {
                            ctx.inMultiComment = false;
                            i++; // Skip the slash token
                        }
                        continue; // Ignore everything inside comments completely
                    }
                    
                    // 2. Handle Single-line Comment circuit breaker instantly
                    if (!ctx.inString && !ctx.inChar && c == '/' && next == '/') {
                        break; // Terminate line loop processing; ignore trailing data
                    }
                    
                    if (!ctx.inString && !ctx.inChar && c == '/' && next == '*') {
                        ctx.inMultiComment = true;
                        i++; // Skip the asterisk token
                        continue;
                    }
                    
                    // 3. Advanced Escape Sequence Counter (Prevents backslash duplication faults)
                    boolean isEscaped = false;
                    if (ctx.inString || ctx.inChar) {
                        int backslashCount = 0;
                        int checkIndex = i - 1;
                        while (checkIndex >= 0 && trimmed.charAt(checkIndex) == '\\') {
                            backslashCount++;
                            checkIndex--;
                        }
                        // An odd number of preceding backslashes means this active character is fully escaped
                        if (backslashCount % 2 != 0) {
                            isEscaped = true;
                        }
                    }
                    
                    // 4. Handle String boundary maps safely
                    if (!ctx.inChar && c == '"' && !isEscaped) {
                        ctx.inString = !ctx.inString;
                        continue;
                    }
                    
                    // 5. Handle Character literal boundary maps safely
                    if (!ctx.inString && c == '\'' && !isEscaped) {
                        ctx.inChar = !ctx.inChar;
                        continue;
                    }
                    
                    // 6. Calculate braces only when structurally exposed outside literals or comments
                    if (!ctx.inString && !ctx.inChar) {
                        if (c == '{') {
                            open++;
                            if (!foundFirstMeaningful) {
                                foundFirstMeaningful = true;
                            }
                        } else if (c == '}') {
                            close++;
                            if (!foundFirstMeaningful) {
                                startsWithClose = true;
                                foundFirstMeaningful = true;
                            }
                        } else if (!Character.isWhitespace(c) && !foundFirstMeaningful) {
                            foundFirstMeaningful = true;
                        }
                    }
                }
                
                // Calculate immediate indentation offset levels based on layout flags
                int lineLevel = level;
                
                // Adjust index only if the row cleanly opens/closes context relative to multi-line states
                if (startsWithClose && !initialInComment && !initialInLiteral) {
                    lineLevel = Math.max(0, level - 1);
                }
                
                // Persist the freshly formatted code string out to buffer
                if (!first) output.append("\n");
                output.append(indent(lineLevel, spaces)).append(trimmed);
                
                // Calibrate structural depth boundaries for the next iteration sequence
                level = level + open - close;
                if (level < 0) level = 0;
                
                first = false;
            }
            
            output.append("\n");
            
        } catch (IOException ignored) {
            // StringReader ile çalışırken IOException oluşması beklenmez
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {}
        }
        
        return output.toString();
    }
  
    /**
     * Synthesizes trailing white space tokens matching target indentation criteria.
     */
    private static String indent(int level, int spaces) {
        if (level <= 0) return "";
        StringBuilder sb = new StringBuilder();
        int totalSpaces = level * spaces;
        for (int i = 0; i < totalSpaces; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
    
}
