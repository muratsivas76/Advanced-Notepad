package net.dilmerkezi.filter;

public class Help {
  
  public static final String DIYEZREGEX = "^\\d+[\\.\\-:]\\s*.*";
  public static final String SPACEREGEX = "\\s+";
  public static final String XREGEX = "[\\.\\-:]$";
  public static final String YREGEX = "[\\.\\-:]";
  public static final String UNIREGEX = "\\b(\\p{L}+)(\\d+)\\b";
  
  public static final String[] charsets = {
		// Column 1: Standard & Turkish
		"UTF-8", "ISO-8859-9", "windows-1254", "ISO-8859-1", "US-ASCII", "UTF-16",
		
		// Column 2: Western & Central Europe
		"windows-1252", "ISO-8859-2", "windows-1250", "ISO-8859-15", "UTF-16BE", "UTF-16LE",
		
		// Column 3: Cyrillic & Greek & Hebrew
		"ISO-8859-5", "windows-1251", "ISO-8859-7", "windows-1253", "ISO-8859-8", "windows-1255",
		
		// Column 4: Arabic & Asian Styles
		"ISO-8859-6", "windows-1256", "Big5", "Shift_JIS", "EUC-JP", "EUC-KR",
		
		// Final Row (The 25th Element)
		"Custom..."
  };
  public static final int charsetslen = charsets.length;
  		
  public static final String[] textTypes = {
    ".txt", ".log", ".md",
    ".shtml", ".dhtml", ".xhtml", ".html", ".htm", ".xhtm", ".dhtm", ".shtm",
    ".xml", ".css", ".js", ".mf",
    ".java", ".jsp",
    ".c", ".cpp", ".h", ".hpp",
    ".py", ".rb", ".php",
    ".cs", ".go", ".rs",
    ".sh", ".bash", ".bat", ".prop", ".properties",
    ".json", ".yaml", ".yml", ".ini", ".cfg",
    ".sql", ".tex"
  };
  
  public static final String[] imageTypes = {
    ".jpg", ".jpeg", ".png"
  };
  
  public static final String IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n";

  public static final String rtfHint = "\nNote that: If you want to see bg colored words in RTF file \nselect all text and pick bg white first.\n";
  
  public static final String foreignCharsGuide =
  "\n\n===== FOREIGN CHARACTERS GUIDE =====\n\n" +
  "This guide shows how to type special characters using simple patterns.\n" +
  "Just type the pattern and the program will automatically convert it.\n\n" +
  "=====================================\n" +
  "NORWEGIAN CHARACTERS\n" +
  "=====================================\n\n" +
  "AAAAA  ->  \u00C5  (A with ring above)\n" +
  "aaaaa  ->  \u00E5  (a with ring above)\n\n" +
  "AAEE   ->  \u00C6  (AE ligature)\n" +
  "aaee   ->  \u00E6  (ae ligature)\n\n" +
  "OOEE   ->  \u00D8  (O with stroke)\n" +
  "ooee   ->  \u00F8  (o with stroke)\n\n" +
  "=====================================\n" +
  "TURKISH CHARACTERS\n" +
  "=====================================\n\n" +
  "AAAA   ->  \u00C2  (A with circumflex)\n" +
  "aaaa   ->  \u00E2  (a with circumflex)\n\n" +
  "IIII   ->  \u00CE  (I with circumflex)\n" +
  "iiii   ->  \u00EE  (i with circumflex)\n\n" +
  "UUUU   ->  \u00DB  (U with circumflex)\n" +
  "uuuu   ->  \u00FB  (u with circumflex)\n\n" +
  "CC     ->  \u00C7  (C with cedilla)\n" +
  "cc     ->  \u00E7  (c with cedilla)\n\n" +
  "GG     ->  \u011E  (G with breve)\n" +
  "gg     ->  \u011F  (g with breve)\n\n" +
  "II     ->  \u0130  (I with dot above)\n" +
  "ii     ->  \u0131  (dotless i)\n\n" +
  "OO     ->  \u00D6  (O with diaeresis)\n" +
  "oo     ->  \u00F6  (o with diaeresis)\n\n" +
  "SS     ->  \u015E  (S with cedilla)\n" +
  "ss     ->  \u015F  (s with cedilla)\n\n" +
  "UU     ->  \u00DC  (U with diaeresis)\n" +
  "uu     ->  \u00FC  (u with diaeresis)\n\n" +
  "=====================================\n" +
  "LATIN/SPANISH CHARACTERS\n" +
  "=====================================\n\n" +
  "AAA    ->  \u00C1  (A with acute)\n" +
  "aaa    ->  \u00E1  (a with acute)\n\n" +
  "EEE    ->  \u00C9  (E with acute)\n" +
  "eee    ->  \u00E9  (e with acute)\n\n" +
  "III    ->  \u00CD  (I with acute)\n" +
  "iii    ->  \u00ED  (i with acute)\n\n" +
  "OOO    ->  \u00D3  (O with acute)\n" +
  "ooo    ->  \u00F3  (o with acute)\n\n" +
  "UUU    ->  \u00DA  (U with acute)\n" +
  "uuu    ->  \u00FA  (u with acute)\n\n" +
  "NNN    ->  \u00D1  (N with tilde)\n" +
  "nnn    ->  \u00F1  (n with tilde)\n\n" +
  "???    ->  \u00BF  (inverted question mark)\n" +
  "!!!    ->  \u00A1  (inverted exclamation)\n\n" +
  "=====================================\n" +
  "SYMBOLS\n" +
  "=====================================\n\n" +
  "KAVISLIAC    ->  {  (curly brace open)\n" +
  "KAVISLIKAPA  ->  }  (curly brace close)\n\n" +
  "KOSELIAC     ->  [  (square bracket open)\n" +
  "KOSELIKAPA   ->  ]  (square bracket close)\n\n" +
  "\"\"           ->  '  (double quote to single quote)\n\n" +
  "//           ->  \\\\  (backslash)\n\n" +
  "=====================================\n" +
  "IMPORTANT NOTES\n" +
  "=====================================\n\n" +
  "1. Patterns are case-sensitive!\n" +
  "   - Use uppercase for uppercase letters\n" +
  "   - Use lowercase for lowercase letters\n\n" +
  "2. Longer patterns MUST be processed before shorter ones.\n" +
  "   Example: AAAAA (\u00C5) before AAAA (\u00C2) before AAA (\u00C1)\n\n" +
  "3. These patterns are designed for plain text editing.\n" +
  "   Just type naturally and the conversion happens automatically.\n\n" +
  "=====================================\n" +
  "EXAMPLE USAGE\n" +
  "=====================================\n\n" +
  "Type:  MedreseAAAA acildi.\n" +
  "Result: Medrese\u00C2 acildi.\n\n" +
  "Type:  AAAAA lesund\n" +
  "Result: \u00C5 lesund\n\n" +
  "Type:  AAEErlighet\n" +
  "Result: \u00C6rlighet\n\n" +
  "Type:  OOEEl\n" +
  "Result: \u00D8l\n\n" +
  "=====================================\n" +
  "END OF GUIDE\n" +
  "=====================================\n";
  
  public static final String helpString =
  "===== REGEX HELP FOR FOOTNOTE PROCESSING =====\n\n" +
  
  "From: (?m)^(\\d+\\.\\s+)([^:]*)$\n" +
  "To:   ABCDEFXYZ$1$2\n\n" +
  
  "From: \\n(\\d+)\\.\\s\n" +
  "To:   .\\n$1. \n\n" +
  
  "From: (?m)^\\.$\n" +
  "To:   (leave empty)\n\n" +
  
  "From: (\\d+\\.\\s+[^:]+:.*?)\\n([^\\d\\n][^\\n]+)\n" +
  "To:   $1 $2\n" +
  "Note: This regex may need to be applied multiple times.\n" +
  "Each execution fixes one breakline per footnote.\n\n" +
  
  "Then run: Numbered Footnotes to Between Brackets\n\n" +
  
  "From: ABCDEFXYZ\n" +
  "To:   (leave empty)\n\n" +
  
  "===== COMPREHENSIVE REGEX GUIDE (JAVA COMPATIBLE) =====\n\n" +
  
  "--- 1. BASIC CHARACTER CLASSES ---\n" +
  "• [abc]             : Any of a, b, or c\n" +
  "• [^abc]            : Any character EXCEPT a, b, or c\n" +
  "• [a-zA-ZşığĞÜŞİÖÇ] : All letters including Turkish characters\n" +
  "• \\\\d                : Digit (0-9)\n" +
  "• \\\\w                : Word character (Letter, digit, underscore)\n" +
  "• \\\\s                : Whitespace (space, tab, newline)\n" +
  "• .                 : Any character except newline\n\n" +
  "• Note that you can enter unicode vals like \\u005C etc.\n\n" +
  "--- 2. QUANTIFIERS ---\n" +
  "• *     : 0 or more\n" +
  "• +     : 1 or more\n" +
  "• ?     : 0 or 1 (Optional)\n" +
  "• {n}   : Exactly n times\n" +
  "• {n,}  : At least n times\n\n" +
  
  "--- 3. USEFUL SCENARIOS & EXAMPLES ---\n" +
  "• Delete all empty lines:          (?m)^\\\\s*$\\\\n? → (empty)\n" +
  "• Multiple spaces to single:       \\\\s{2,} → (single space)\n" +
  "• Delete leading spaces:            (?m)^\\\\s+ → (empty)\n" +
  "• Delete trailing spaces:           \\\\s+$ → (empty)\n" +
  "• Remove HTML tags:                 <[^>]*> → (empty)\n" +
  "• Case-insensitive search:          (?i)word\n" +
  "• Duplicate word finder:            \\\\b(\\\\w+)\\\\s+\\\\1\\\\b\n" +
  "• Find email addresses:             [\\\\w.%+-]+@[\\\\w.-]+\\\\.[a-zA-Z]{2,}\n" +
  "• Numbers before currency:          \\\\d+(?=[$€])\n" +
  "• Numbers at word start/end:       \\\\b\\\\d+ (start) or \\\\d+\\\\b (end)\n" +
  "• Swap words:                       (\\\\w+) (\\\\w+) → $2 $1\n" +
  "• Capitalize first letter:          (?<=[.!?]\\\\s)([a-z]) → \\\\u$1\n" +
  "• Find numbers at line start:       (?m)^\\\\d+\\\\.\\\\s+.*\n\n" +
  
  "--- 4. GOLDEN RULES FOR JAVA ---\n" +
  "// Always escape backslashes in Java strings: use \\\\d instead of \\d\n" +
  "// Dots are special characters; use \\\\. for a literal period\n" +
  "// Use | for OR logic: (apple|orange|banana)\n" +
  "// Lookarounds (?<=...) and (?=...) act as markers and are not consumed\n" +
  "// Group captures like (\\\\w+) can be referenced via $1, $2, etc.\n";
  
} // Class end
