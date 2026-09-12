package net.dilmerkezi.defter;

import java.io.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;

import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;

import java.text.Collator;
import java.text.Normalizer;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Custom
import net.dilmerkezi.filter.Help;

/**
 * Utilities - Central helper class for font operations and validations.
 * Automatically detects system fonts and manages style selections.
 *
 * Licensed under GPL v3.
 */
public final class Utilities {
  
  private final String[] fontNames;
  private final String[] styles;
  private String CHARCODE="UTF-8";
  
  public final String [] families = {
    "Arial",
    "Times New Roman",
    "Verdana",
    "Calibri",
    "FreeSans"
  };
  public final int lenf = families.length;
  
  public final Color [] colores = new Color [] {
    Color.BLACK,
    Color.RED,
    Color.GREEN,
    Color.BLUE
  };
  
  public final String colstrs [] = new String [] {
    "Black",
    "Red",
    "Green",
    "Blue"
  };
  
  /**
   * Parameterless constructor.
   * Automatically initializes system font names and predefined styles.
   */
  public Utilities() {
    // Fetching local graphics environment to get all system fonts
    GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
    this.fontNames = genv.getAvailableFontFamilyNames();
    
    // Setting predefined font styles as per requirement
    this.styles = new String[]{"Plain", "Bold", "Italic", "Bold+Italic"};
  }
  
  public String[] getFontNames() {
    return fontNames;
  }
  
  public String getCharCode() {
    return this.CHARCODE;
  }
  
  public void setCharCode(String ncode) {
    this.CHARCODE = ncode;
  }
  
  /**
   * Sets the character encoding for file operations.
   * Provides a list of common charsets and a custom input option.
   */
  public final void setCharCode(JTextPane txt, JLabel statusLabel, JComponent mb) {
    // Styling components
    Font uiFont = txt.getFont();
    Color uiColor = txt.getForeground();
    ButtonGroup group = new ButtonGroup();
    // 4 columns layout for 25 items: 6 rows of 4 buttons + 1 row for "Custom..."
    JPanel panel = new JPanel(new GridLayout(0, 4, 10, 5));
    
    JRadioButton[] buttons = new JRadioButton[Help.charsetslen];
    
    for (int i = 0; i < Help.charsetslen; i++) {
      buttons[i] = new JRadioButton(Help.charsets[i]);
      buttons[i].setFont(uiFont);
      buttons[i].setForeground(uiColor);
      
      // Auto-select the current active CHARCODE if it matches
      if (getCharCode().equalsIgnoreCase(Help.charsets[i])) {
        buttons[i].setSelected(true);
      }
      
      group.add(buttons[i]);
      panel.add(buttons[i]);
    }
    
    // Default to UTF-8 if no match found
    if (group.getSelection() == null) {
      buttons[0].setSelected(true);
    }
    
    Object[] params = {
      "<html><font color='blue' size='5'>Select Character Encoding:</font></html>",
      panel
    };
    
    int res = JOptionPane.showConfirmDialog(mb, params, "Encoding Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
    if (res == JOptionPane.OK_OPTION) {
      String selectedSet = "UTF-8"; // Fallback
      
      for (int i = 0; i < buttons.length; i++) {
        if (buttons[i].isSelected()) {
          selectedSet = Help.charsets[i];
          break;
        }
      }
      
      // Handle Custom encoding input
      if (selectedSet.equals("Custom...")) {
        String customMsg = "<html><font color='blue' size='5'>Enter Custom Charset (e.g. windows-1251):</font></html>";
        String customInput = JOptionPane.showInputDialog(mb, customMsg, "UTF-8");
        
        if (customInput != null && customInput.trim().length() >= 2) {
          setCharCode(customInput.trim().toUpperCase());
          } else {
          setCharCode("UTF-8"); // Fallback for invalid/empty custom input
        }
        } else {
        setCharCode(selectedSet);
      }
      
      // Update UI Label
      statusLabel.setText("Char Code: " + getCharCode());
    }
  }
  
  public void createSnippetsFile() {
    try {
      File dir = new File("npoperations");
      if (!dir.exists()) {
        dir.mkdir();
      }
      
      File sFile = new File(dir, "snippets.properties");
      
      if (!sFile.exists()) {
        PrintWriter writer = new PrintWriter(new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(sFile), "UTF-8")));
        
        writer.println("# SNIPPET DEFINITIONS");
        writer.println("# Structure: Category.Name=Content");
        writer.println("");
        writer.println("Java.Main_Method=public static void main(String[] args) {\\n\\t\\n}\\n");
        writer.println("Java.Print_Statement=System.out.println(\"\");\\n");
        writer.println("");
        writer.println("C.Hello_World=#include <stdio.h>\\n\\nint main() {\\n\\tprintf(\"Hello!\\\\n\");\\n\\treturn 0;\\n}\\n");
        writer.println("");
        writer.println("HTML.Skeleton=<!DOCTYPE html>\\n<html>\\n<body>\\n\\n</body>\\n</html>\\n");
        writer.println("");
        writer.println("General.Signature=Best Regards,\\n[Your Name]\\n");
        writer.println("");
        
        writer.flush();
        writer.close();
        System.out.println("\nDefault npoperations/snippets.properties created successfully.");
      }
      } catch (Exception e) {
      System.err.println("Could not create snippets file: " + e.getMessage());
    }
  }
  
  public void createTestFile() {
    try {
        File dir = new File("npoperations");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File tFile = new File(dir, "test.txt");
        if (tFile.exists()) return;
        
        // Using UTF-8 but providing special chars as Unicode escapes for maximum compatibility
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tFile), "UTF-8")));

        // 1. g\u00fcn b\u00f6yle ba\u015flad\u0131. (1. gün böyle başladı.)
        writer.println("1. g\u00fcn b\u00f6yle ba\u015flad\u0131.");
        writer.println("");
        
        // Maarif2 Bakanl\u0131\u011f\u0131n\u0131n talimat\u0131 mucibince3 tullab4 bug\u00fcn derse ba\u015flad\u0131.
        writer.println("Medrese1 a\u00e7\u0131ld\u0131. Maarif2 Bakanl\u0131\u011f\u0131n\u0131n talimat\u0131 mucibince3 tullab4 bug\u00fcn derse ba\u015flad\u0131.");
        writer.println("");
        
        writer.println("1. medrese: okul");
        writer.println("2. maarif: Eski dilde");
        // Mill\u00ee E\u011fitim
        writer.println("Mill\u00ee E\u011fitim");
        writer.println("anlam\u0131na gelen bir kelime");
        writer.println("3. mucibince: gere\u011fince");
        // 4. tullab: \u00f6\u011frenciler
        writer.println("4. tullab: \u00f6\u011frenciler");
        writer.println("");
        
        // Mesrur1 bir halde s\u0131n\u0131flara giren tullab\u0131 muallimler2 ne\u015fe ile kar\u015f\u0131lad\u0131lar.
        writer.println("Mesrur1 bir halde s\u0131n\u0131flara giren tullab\u0131 muallimler2 ne\u015fe ile kar\u015f\u0131lad\u0131lar.");
        writer.println("");
        
        // 1. mesrur: sevin\u00e7li
        writer.println("1. mesrur: sevin\u00e7li");
        // 2. muallimler: \u00f6\u011fretmenler
        writer.println("2. muallimler: \u00f6\u011fretmenler");

        writer.flush();
        writer.close();
        System.out.println("\nnpoperations/test.txt created successfully with Unicode escapes.");

    } catch (Exception e) {
        System.err.println("Could not create test file: " + e.getMessage());
    }
  }

  public void createExampleXMLFile() {
    try {
        File dir = new File("npoperations");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File xFile = new File(dir, "example.xml");
        if (xFile.exists()) return;
        
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(xFile), "UTF-8")));

        writer.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        writer.println("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"");
        writer.println("\t\tpackage=\"net.murat.mregex\"");
        writer.println("\t\tandroid:versionCode=\"1\"");
        writer.println("\t\tandroid:versionName=\"1.0\">");
        writer.println("\t");
        writer.println("\t<!--");
        writer.println("\t<uses-permission android:name=\"android.permission.READ_EXTERNAL_STORAGE\" />");
        writer.println("\t<uses-permission android:name=\"android.permission.WRITE_EXTERNAL_STORAGE\" />");
        writer.println("    -->");
        writer.println("    ");
        writer.println("\t<uses-sdk android:minSdkVersion=\"29\" />");
        writer.println("");
        writer.println("\t<application android:icon=\"@drawable/mregexlogo\"");
        writer.println("\t\t\t\tandroid:label=\"@string/program_name\"");
        writer.println("\t\t\t\tandroid:requestLegacyExternalStorage=\"true\">");
        writer.println("");
        writer.println("\t\t<activity android:name=\"net.murat.mregex.MRegex\"");
        writer.println("\t\t\t\t\tandroid:label=\"@string/program_name\"");
        writer.println("\t\t\t\t\tandroid:screenOrientation=\"portrait\"");
        writer.println("\t\t\t\t\tandroid:windowSoftInputMode=\"adjustResize\">");
        writer.println("\t\t\t\t\t<!-- screenOrientation is landscape OR portrait with lowerCase-->");
        writer.println("");
        writer.println("\t\t\t<intent-filter>");
        writer.println("\t\t\t\t<action android:name=\"android.intent.action.MAIN\" />");
        writer.println("\t\t\t\t<category android:name=\"android.intent.category.LAUNCHER\" />");
        writer.println("\t\t\t</intent-filter>");
        writer.println("");
        writer.println("\t\t</activity>");
        writer.println("");
        writer.println("\t</application>");
        writer.println("");
        writer.println("</manifest>");

        writer.flush();
        writer.close();
        System.out.println("\nnpoperations/example.xml created successfully in npoperations directory.");

    } catch (Exception e) {
        System.err.println("Could not create XML file: " + e.getMessage());
    }
  }

  public void createWordsFile() {
    try {
        File dir = new File("npoperations");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File tFile = new File(dir, "words.txt");
        if (tFile.exists()) return;
        
        // Using UTF-8 but providing special chars as Unicode escapes for maximum compatibility
        PrintWriter writer = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(tFile), "UTF-8")));

        writer.println("car: araba");
        writer.println("coche: araba");     
        writer.println("pencil: kalem");
        writer.println("school: okul");
        writer.println("escuela: okul");
        writer.println("street: cadde");
        writer.println("way: yol");
        
        writer.flush();
        writer.close();
        System.out.println("\nnpoperations/words.txt created successfully.");

    } catch (Exception e) {
        System.err.println("Could not create words file: " + e.getMessage());
    }
  }

  /**
   * Prompts the user to select a font family from a list of all system fonts.
   */
  public final Font getFamiliedFont(JTextPane c) {
    Font f = c.getFont();
    int stil = f.getStyle();
    int size = f.getSize();
    Object obj;
    
    try {
      String msg = "<html><body><font color=\"blue\" size=\"5\">Select text font family</font></body></html>";
      obj = JOptionPane.showInputDialog(
        c,
        msg,
        "Font Families",
        JOptionPane.QUESTION_MESSAGE,
        null,
        fontNames,
        fontNames[0]
      );
      } catch (Exception ex) {
      obj = null;
    }
    
    if (obj == null) {
      return f;
    }
    
    return new Font(obj.toString(), stil, size);
  }
  
  /**
   * Prompts the user for a numeric font size value via a dialog.
   */
  public final Font getSizedFont(JTextPane c) {
    Font f = c.getFont();
    String name = f.getFamily();
    int stil = f.getStyle();
    int size = f.getSize();
    String str;
    
    try {
      String msg = "<html><body><font color=\"blue\" size=\"5\"> A font size value</font></body></html>";
      str = JOptionPane.showInputDialog(c, msg);
      } catch (Exception ex) {
      return f;
    }
    
    if (str == null || str.trim().isEmpty()) {
      return f;
    }
    
    try {
      size = Integer.parseInt(str.trim());
      } catch (Exception ex) {
      return f;
    }
    
    return new Font(name, stil, size);
  }
  
  /**
   * Prompts the user to select a style (Plain, Bold, etc.) from the predefined list.
   */
  public final Font getStyledFont(JTextPane c) {
    Font f = c.getFont();
    String name = f.getFamily();
    int size = f.getSize();
    Object obj;
    
    try {
      String msg = "<html><body><font color=\"blue\" size=\"5\">Select text font style</font></body></html>";
      obj = JOptionPane.showInputDialog(
        c,
        msg,
        "Font Styles",
        JOptionPane.QUESTION_MESSAGE,
        null,
        styles,
        styles[0]
      );
      } catch (Exception ex) {
      obj = null;
    }
    
    if (obj == null) {
      return f;
    }
    
    String str = obj.toString();
    
    // Compatibility check for combined Bold+Italic style
    if (str.indexOf("+") != -1) {
      return new Font(name, Font.BOLD + Font.ITALIC, size);
    }
    
    if (str.equals("Bold")) {
      f = new Font(name, Font.BOLD, size);
      } else if (str.equals("Italic")) {
      f = new Font(name, Font.ITALIC, size);
      } else if (str.equals("Plain")) {
      f = new Font(name, Font.PLAIN, size);
    }
    
    return f;
  }
  
  public void saveContainsLines(JComponent mb, final String text, JLabel statusLabel) {
	if ((text == null) || text.isEmpty()) {
		statusLabel.setText("ERROR: Null or empty main text.");
		return;
	}
	
	String str = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter your expression:</font></body></html>");
	if ((str == null) || str.isEmpty()) {
		statusLabel.setText("ERROR: Null or empty expression.");
		return;
	}

	String fileName = convertToAscii(str);
	fileName = fileName.replace(" ", "_");
	fileName = fileName.replace(".", "_");
	fileName = fileName.replace("*", "_");
	fileName = fileName.replace(",", "-");
	if (fileName.length() > 28) {
		fileName = fileName.substring(0, 28);
	}

	fileName = "npoperations/" + fileName + ".txt";
	fileName = fileName.toLowerCase();
	File file = new File(fileName);

	// Create the directory if it doesn't exist
	File dir = file.getParentFile();
	if (dir != null && !dir.exists()) {
		dir.mkdirs();
	}

	PrintWriter out = null;
	Scanner scanner = null;
	int count = 0;

	try {
		out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		scanner = new Scanner(text);

		// Scan text line by line
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			// Check if the line contains the given expression
			if (line.contains(str)) {
				out.println(line);
				count++;
			}
		}
		statusLabel.setText("SUCCESS: " + count + " lines saved to " + fileName);
	} catch (IOException e) {
		// Handle file writing errors
		statusLabel.setText("ERROR: Could not write to file. " + e.getMessage());
	} finally {
		// Ensure resources are closed to prevent memory leaks
		if (out != null) {
			out.close();
		}
		if (scanner != null) {
			scanner.close();
		}
	}
  }
  
  public String convertToAscii(String text) {
    if (text == null) return null;
    
    // 1. Harfleri ve uzerindeki isaretleri birbirinden ayir (sh -> s + ̧)
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
    
    // 2. "Mark, Nonspacing" (isaretler) kategorisindeki her seyi temizle
    // Bu regex, harfin yanindaki o kucuk cengel, nokta ve aksanlari ucurur.
    String result = normalized.replaceAll("\\p{M}", "");
    
    // 3. Ozel durum: Normalizer bazen '0131' (küçük ii) harflerde takilabilir.
    // Turkçedeki 'ii' -> 'i' donusumu icin manuel bir dokunus gerekebilir:
    result = result.replace("\u0131", "i").replace("\u0130", "I");
    
    return result;
  }
  
  /**
   * Removes all empty or whitespace-only lines from the JTextPane.
   * Updates the status label with the result of the operation.
   *
   * @param txt The target JTextPane to process
   * @param statusLabel Label to display the outcome
   */
  public void removeEmptyLines(JTextPane txt, JLabel statusLabel) {
    try {
      String content = txt.getText();
      if (content == null || content.trim().length() == 0) {
        statusLabel.setText("Document is already empty.");
        return;
      }
      
      // Split the content by any line break (Windows, Linux, or Mac style)
      String[] lines = content.split("\\r?\\n");
      StringBuffer sb = new StringBuffer();
      int removedCount = 0;
      int originalLineCount = lines.length;
      
      for (int i = 0; i < lines.length; i++) {
        // Check if the line is not empty and contains more than just whitespace
        if (lines[i].trim().length() > 0) {
          sb.append(lines[i]);
          // Add a newline after each valid line except potentially the last one
          // but for text processing, keeping standard newlines is safer
          sb.append("\n");
          } else {
          removedCount++;
        }
      }
      
      // Apply the cleaned text back to the JTextPane
      txt.setText(sb.toString().trim());
      
      // Update the status label in English
      if (removedCount > 0) {
        statusLabel.setText("Success: Removed " + removedCount + " empty line(s).");
        } else {
        statusLabel.setText("No empty lines were found.");
      }
      
      } catch (Exception e) {
      statusLabel.setText("Error during line removal: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  public void sortSelectedLines(JTextPane txt, JComponent mb) {
    Document doc = txt.getDocument();
    int start = txt.getSelectionStart();
    int end = txt.getSelectionEnd();
    
    if (start == end) return;
    
    try {
      Element root = doc.getDefaultRootElement();
      int startLine = root.getElementIndex(start);
      int endLine = root.getElementIndex(end);
      
      // Start and end of lines
      int lineStartOffset = root.getElement(startLine).getStartOffset();
      int lineEndOffset = root.getElement(endLine).getEndOffset();
      
      // IMPORTANT: End of document control
      int docLength = doc.getLength();
      if (lineEndOffset > docLength) {
        lineEndOffset = docLength;
      }
      
      int lengthToReplace = lineEndOffset - lineStartOffset;
      if (lengthToReplace <= 0) return;
      
      // Split text to lines
      String selectedText = doc.getText(lineStartOffset, lengthToReplace);
      
      String[] linesArray = selectedText.split("\n", -1);
      java.util.List<String> lines = new ArrayList<>(Arrays.asList(linesArray));
      
      final Collator trCollator = Collator.getInstance(Locale.forLanguageTag("tr-TR"));		  Collections.sort(lines, new Comparator<String>() {
          @Override
          public int compare(String s1, String s2) {
            return trCollator.compare(s1, s2);
          }
      });
      
      String sortedText = String.join("\n", lines);
      
      if (doc instanceof AbstractDocument) {
        ((AbstractDocument) doc).replace(lineStartOffset, lengthToReplace, sortedText, null);
        } else {
        doc.remove(lineStartOffset, lengthToReplace);
        doc.insertString(lineStartOffset, sortedText, null);
      }
      
      txt.setSelectionStart(lineStartOffset);
      txt.setSelectionEnd(lineStartOffset + sortedText.length());
      
      } catch (BadLocationException ex) {
      System.err.println("Offset error: " + ex.getMessage());
      JOptionPane.showMessageDialog(mb, "Sorting error: " + ex.getMessage());
    }
  }
  
  /**
   * Validates if a string is strictly numeric.
   */
  public final boolean sfnum(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    
    boolean issf = true;
    int strlen = str.length();
    
    for (int i = 0; i < strlen; i++) {
      char ch = str.charAt(i);
      int chm = (int) ch;
      // 0x30 is '0', 0x39 is '9'
      if (!(chm >= 0x30 && chm <= 0x39)) {
        issf = false;
        break;
      }
    }
    
    return issf;
  }
  
  public final String fromUnicode(String text) throws Exception {
    if (text == null || text.isEmpty()) return text;
    
    int len = text.length();
    StringBuilder sb = new StringBuilder(len);
    int i = 0;
    
    while (i < len) {
      char ch = text.charAt(i);
      
      // Check for u or U pattern
      if (ch == '\\' && (i + 5) < len &&
        (text.charAt(i + 1) == 'u' || text.charAt(i + 1) == 'U')) {
        
        // Extract 4 hex digits manually for speed
        int v1 = Character.digit(text.charAt(i + 2), 16);
        int v2 = Character.digit(text.charAt(i + 3), 16);
        int v3 = Character.digit(text.charAt(i + 4), 16);
        int v4 = Character.digit(text.charAt(i + 5), 16);
        
        // If all 4 are valid hex digits
        if (v1 != -1 && v2 != -1 && v3 != -1 && v4 != -1) {
          int code = (v1 << 12) | (v2 << 8) | (v3 << 4) | v4;
          sb.append((char) code);
          i += 6; // Move past uXXXX
          } else {
          sb.append(ch);
          i++;
        }
        } else {
        sb.append(ch);
        i++;
      }
    }
    return sb.toString();
  }
  
  public final String toUnicode(String text) throws Exception {
    if (text == null || text.isEmpty()) return text;
    
    int len = text.length();
    // Capacity optimization: Unicode strings grow in size
    StringBuffer sb = new StringBuffer(len * 2);
    
    for (int i = 0; i < len; i++) {
      char ch = text.charAt(i);
      int m = (int) ch;
      
      // 1. Keep special safe characters and whitespaces as is
      if (ch == '}' || ch == '{' || ch == ',' || ch == '\"' || ch == '\n' ||
        ch == '\t' || ch == '\r' || Character.isWhitespace(ch)) {
        sb.append(ch);
        continue;
      }
      
      // 2. Escape non-ASCII or control characters
      if (m < 32 || m > 122) {
        sb.append("\\u");
        String hex = Integer.toHexString(m).toUpperCase();
        
        // Manual padding for 4 digits (Faster than String.format)
        int hexLen = hex.length();
        if (hexLen == 1) sb.append("000");
        else if (hexLen == 2) sb.append("00");
        else if (hexLen == 3) sb.append("0");
        
        sb.append(hex);
        } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }
  
  public static String escapeSequences(String input) {
    if (input == null) return null;
    
    // Replace escape sequences with actual characters
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++) {
      char c = input.charAt(i);
      if (c == '\\' && i + 1 < input.length()) {
        char next = input.charAt(i + 1);
        switch (next) {
          case 'n':
            sb.append('\n');
          i++;
          break;
          case 't':
            sb.append('\t');
          i++;
          break;
          case 'r':
            sb.append('\r');
          i++;
          break;
          case 'b':
            sb.append('\b');
          i++;
          break;
          case 'f':
            sb.append('\f');
          i++;
          break;
          case '\\':
            sb.append('\\');
          i++;
          break;
          default:
            sb.append(c);
          break;
        }
        } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
  public Map<String, String> parseFootnotesFromBlock(String footnoteBlock) {
    Map<String, String> footnoteMap = new LinkedHashMap<String, String>();
    String[] lines = footnoteBlock.split("\\n");
    Pattern footnotePattern = Pattern.compile("^(\\d+)\\.\\s*([^:]+):\\s*(.+)$");
    
    for (String line : lines) {
      Matcher matcher = footnotePattern.matcher(line.trim());
      if (matcher.matches()) {
        String number = matcher.group(1);
        String definition = matcher.group(3).trim();
        if (definition.endsWith(".")) {
          definition = definition.substring(0, definition.length() - 1);
        }
        if (!footnoteMap.containsKey(number)) {
          footnoteMap.put(number, definition);
        }
      }
    }
    
    return footnoteMap;
  }
  
  public SecretKey getSecretKey(String fullPassword) {
    byte[] keyBytes = fullPassword.getBytes(StandardCharsets.UTF_8);
    // Ensure 16 bytes for AES-128
    byte[] finalKey = new byte[16];
    System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, 16));
    return new SecretKeySpec(finalKey, "AES");
  }
  
  public byte[] encrypt(byte[] data, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    byte[] iv = new byte[12]; // GCM recommended 12 bytes IV
    SecureRandom random = new SecureRandom();
    random.nextBytes(iv);
    GCMParameterSpec spec = new GCMParameterSpec(128, iv);
    cipher.init(Cipher.ENCRYPT_MODE, key, spec);
    byte[] cipherText = cipher.doFinal(data);
    // Prepend IV to ciphertext
    byte[] result = new byte[iv.length + cipherText.length];
    System.arraycopy(iv, 0, result, 0, iv.length);
    System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);
    return result;
  }
  
  public byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
    // Extract IV (first 12 bytes)
    byte[] iv = new byte[12];
    byte[] cipherText = new byte[encryptedData.length - 12];
    System.arraycopy(encryptedData, 0, iv, 0, 12);
    System.arraycopy(encryptedData, 12, cipherText, 0, encryptedData.length - 12);
    
    Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    GCMParameterSpec spec = new GCMParameterSpec(128, iv);
    cipher.init(Cipher.DECRYPT_MODE, key, spec);
    return cipher.doFinal(cipherText);
  }
  
  public String applyFootnotesToText(String text, Map<String, String> footnoteMap) {
    if (footnoteMap.isEmpty()) {
      return text;
    }
    
    // Match: word (letters) + number, but number must be at the end of the word
    // Example: "Medrese1" but not "1. gün"
    Pattern pattern = Pattern.compile(Help.UNIREGEX, Pattern.UNICODE_CHARACTER_CLASS);
    Matcher matcher = pattern.matcher(text);
    StringBuffer result = new StringBuffer();
    
    while (matcher.find()) {
      String word = matcher.group(1);
      String number = matcher.group(2);
      String definition = footnoteMap.get(number);
      
      if (definition != null) {
        matcher.appendReplacement(result, Matcher.quoteReplacement(word + " [" + definition + "]"));
        } else {
        matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
      }
    }
    matcher.appendTail(result);
    
    return result.toString();
  }
  
  public String processBlocksSeparately(String text) {
    String[] lines = text.split("\\n");
    StringBuilder result = new StringBuilder();
    StringBuilder currentTextBuffer = new StringBuilder();
    StringBuilder currentFootnoteBuffer = new StringBuilder();
    boolean insideFootnoteBlock = false;
    
    for (String line : lines) {
      String trimmed = line.trim();
      
      if (trimmed.startsWith(Help.START_FN + ".")) {
        // Start of footnote block
        insideFootnoteBlock = true;
        currentFootnoteBuffer = new StringBuilder();
        currentFootnoteBuffer.append(line).append("\n");
      }
      else if (trimmed.startsWith(Help.END_FN + ".")) {
        // End of footnote block
        currentFootnoteBuffer.append(line).append("\n");
        
        // Parse footnotes from this block
        Map<String, String> footnoteMap = parseFootnotesFromBlock(currentFootnoteBuffer.toString());
        
        // Apply footnotes to the accumulated text
        String processedText = applyFootnotesToText(currentTextBuffer.toString(), footnoteMap);
        result.append(processedText);
        
        // Reset buffers
        currentTextBuffer = new StringBuilder();
        insideFootnoteBlock = false;
      }
      else if (insideFootnoteBlock) {
        // Inside footnote block (continuation lines)
        currentFootnoteBuffer.append(line).append("\n");
      }
      else {
        // Normal text outside footnote blocks
        currentTextBuffer.append(line).append("\n");
      }
    }
    
    // Handle any remaining text without footnote block
    if (currentTextBuffer.length() > 0) {
      result.append(currentTextBuffer.toString());
    }
    
    return result.toString();
  }
  
  public String replaceEscapeSequences(String input) {
    if (input == null) return null;
    return input.replace("\\t", "\t")
    .replace("\\n", "\n")
    .replace("\\r", "\r");
  }
  
  public String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
  
  /**
   * Converts escape sequences like \n, \t, \r to actual characters.
   * Preserves \b as word boundary (does NOT convert to backspace).
   * For use in regex patterns.
   *
   * @param input The string containing escape sequences
   * @return The string with escape sequences converted
   */
  public String escapeSequencesForRegex(String input)
  {
    if (input == null) return null;
    
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < input.length(); i++)
    {
      char c = input.charAt(i);
      if (c == '\\' && i + 1 < input.length())
      {
        char next = input.charAt(i + 1);
        switch (next)
        {
          case 'n':
            sb.append('\n');   // Newline
          i++;
          break;
          case 't':
            sb.append('\t');   // Tab
          i++;
          break;
          case 'r':
            sb.append('\r');   // Carriage return
          i++;
          break;
          case 'b':
            // IMPORTANT: Keep \b as word boundary for regex
          sb.append("\\b");
          i++;
          break;
          case '\\':
            sb.append('\\');   // Backslash
          i++;
          break;
          default:
            sb.append(c);      // Keep backslash as is
          break;
        }
      }
      else
      {
        sb.append(c);
      }
    }
    return sb.toString();
  }
  
}
