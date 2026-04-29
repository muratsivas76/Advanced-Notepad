package net.dilmerkezi.defter;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.UserPrincipal;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import java.security.MessageDigest;
import java.security.SecureRandom;

import java.text.Collator;
import java.text.Normalizer;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.undo.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import javax.imageio.ImageIO;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;

// custom
import net.dilmerkezi.filter.*;
// End of import blog

// Start of class
public final class NotDefteriX
extends JPanel
implements ActionListener
{
  private String CHARCODE="UTF-8";
  private final String SALT = "5EmrE4325Emr"; // 12 chars constant prefix
  
  private final JMenuBar mb = new JMenuBar ();
  
  private final MTextPane txt = new MTextPane ();
  
  private final PostScriptExporter PSPRINTER = new PostScriptExporter();
  
  private String CANKURTARAN="";
  
  private final String [] USEDCHARS={"10", "13", "42", "95", "96", "127", "160", "161", "166", "168", "170", "171", "173", "175", "176", "180", "182", "183", "184", "186", "187", "191", "193", "199", "201", "205", "209", "211", "214", "218", "220", "225", "226", "231", "233", "237", "238", "241", "243", "246", "247", "250" , "251", "252", "286", "287", "304", "305", "350", "351", "8210", "8211", "8212", "8213", "8214", "8215", "8216", "8217", "8218", "8219", "8220", "8221", "8222", "8223", "8224", "8226", "8230", "8592", "8594", "9474", "61694", "65533"};
  
  private final int USEDCHARSLEN=USEDCHARS.length;
  
  private final JLabel statusLabel = new JLabel("", JLabel.LEFT);
  
  /** Graphics environment*/
  private final GraphicsEnvironment genv =
  GraphicsEnvironment.getLocalGraphicsEnvironment ();
  
  /** All font names in this computer*/
  private final String [] fontNames = genv.getAvailableFontFamilyNames ();
  
  private final String [] styles={"Plain", "Bold", "Italic", "Bold+Italic"};
  
  private final Vector <String> KONTROLVEC = new Vector <String> ();
  private final Vector <String> ACTUALVEC = new Vector <String> ();
  
  private final Font TXTFONT = new Font ("Monospaced", Font.PLAIN, 23);
    
  private final String START_FN = "#################@";
  private final String END_FN = "#################&";
  
  private final JFileChooser jfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser iconjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser objjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser rtfjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser encjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser htmljfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser commonjfc = new JFileChooser (new File ("npoperations"));
  
  private final SimpleAttributeSet ariaset=new SimpleAttributeSet ();
  private final SimpleAttributeSet timeset=new SimpleAttributeSet ();
  private final SimpleAttributeSet scheset=new SimpleAttributeSet ();
  private final SimpleAttributeSet verdset=new SimpleAttributeSet ();
  private final SimpleAttributeSet comcset=new SimpleAttributeSet ();
  private final SimpleAttributeSet seleset=new SimpleAttributeSet ();
  
  private final SimpleAttributeSet [] familySAS =
  {
    ariaset, timeset, scheset, verdset, comcset, seleset
  };
  
  private final SimpleAttributeSet boldset=new SimpleAttributeSet ();
  private final SimpleAttributeSet itlcset=new SimpleAttributeSet ();
  private final SimpleAttributeSet undlset=new SimpleAttributeSet ();
  
  private final SimpleAttributeSet sz18set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz20set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz22set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz24set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz26set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz28set=new SimpleAttributeSet ();
  private final SimpleAttributeSet sz30set=new SimpleAttributeSet ();
  
  private final SimpleAttributeSet [] sizeSAS =
  {
    sz18set, sz20set, sz22set,
    sz24set, sz26set, sz28set, sz30set,
  };
  
  private final SimpleAttributeSet blckset=new SimpleAttributeSet ();
  private final SimpleAttributeSet reddset=new SimpleAttributeSet ();
  private final SimpleAttributeSet grenset=new SimpleAttributeSet ();
  private final SimpleAttributeSet blueset=new SimpleAttributeSet ();
  private final SimpleAttributeSet orngset=new SimpleAttributeSet ();
  
  private final SimpleAttributeSet [] colorSAS =
  {
    blckset, reddset, grenset, blueset, orngset
  };
  
  private String CONFIND="";
  
  /** Undo manager*/
  protected final UndoManager umngr = new UndoManager ();
  
  /** Undoable listener*/
  protected final UndoableEditListener ulis = new OwnUndoListener ();
  
  /** Undo action*/
  protected final UndoAction uaction = new UndoAction ();
  
  /** Redo action*/
  protected final RedoAction raction = new RedoAction ();
  
  private final JEditorPane helpTextArea = new JEditorPane();
  
  private final SimpleAttributeSet emptset=new SimpleAttributeSet ();
    
  private int imgscr=0;
  
  //START OF CONSTRUCTOR METHOD
  private NotDefteriX ()
  {
    super (new BorderLayout ());
    
    //mb.setMnemonic (KeyEvent.VK_ALT);
    
    setMezayaConstants ();
    
    jfc.setFileFilter(new TextFileFilter());
    iconjfc.setFileFilter(new IconFileFilter());
    iconjfc.setAccessory(new ImagePreview(iconjfc)); // Setup the preview accessory
    objjfc.setFileFilter(new ObjectFileFilter());
    rtfjfc.setFileFilter(new RTFFileFilter());
    encjfc.setFileFilter(new ENCFileFilter());
    htmljfc.setFileFilter(new HTMLFileFilter());
    //jfc, iconjfc, objjfc, rtfjfc, encjfc, htmljfc
    
    helpTextArea.setEditable(true);
    helpTextArea.setFont(new Font("Monospaced", Font.BOLD, 18));
    helpTextArea.setForeground(Color.BLUE);
    helpTextArea.setPreferredSize(new Dimension(864, 432));
    
    statusLabel.setBackground(new Color(0xCCCC22));
    statusLabel.setForeground(Color.BLUE);
    statusLabel.setFont(new Font("SansSerif", Font.PLAIN | Font.ITALIC, 18));
    
    for (int i=0; i<USEDCHARSLEN; i++)
    {
      KONTROLVEC.add (USEDCHARS [i]);
    }
    
    txt.setMargin(new Insets(10, 10, 10, 10));
    txt.setDragEnabled (true);
    txt.setFont (TXTFONT);
    txt.setBackground(new java.awt.Color(252, 252, 252));
    txt.setCaretColor (Color.RED);
    txt.setSelectionColor (Color.ORANGE);
    txt.getDocument ().addUndoableEditListener (ulis);
    
    /////////////// RIGHT CLICK //////////
    // 1. Create the Popup Menu object
    final JPopupMenu popup = new JPopupMenu();
    
    Font menuFont = new Font("Segoe UI", Font.PLAIN, 18);
    
    // 2. Create the Menu Items
    JMenuItem cutItem = new JMenuItem("Cut");
    cutItem.setFont(menuFont);
    JMenuItem copyItem = new JMenuItem("Copy");
    copyItem.setFont(menuFont);
    JMenuItem pasteItem = new JMenuItem("Paste");
    pasteItem.setFont(menuFont);
    JMenuItem selectAllItem = new JMenuItem("Select All");
    selectAllItem.setFont(menuFont);
    
    // 3. Bind actions using Anonymous Inner Classes
    cutItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          txt.cut();
        }
    });
    
    copyItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          txt.copy();
        }
    });
    
    pasteItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          txt.paste();
        }
    });
    
    selectAllItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          txt.selectAll();
        }
    });
    
    // 4. Build the menu structure
    popup.add(cutItem);
    popup.add(copyItem);
    popup.add(pasteItem);
    popup.addSeparator();
    popup.add(selectAllItem);
    
    // 5. Handle Right-Click using MouseListener (isPopupTrigger is required for cross-platform support)
    txt.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
          if (e.isPopupTrigger()) {
            showMenu(e);
          }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
          if (e.isPopupTrigger()) {
            showMenu(e);
          }
        }
        
        /**
         * Positions and displays the context menu.
         * Checks text selection state to enable/disable specific items.
         */
        private void showMenu(MouseEvent e) {
          // Enable 'Cut' and 'Copy' only if there is selected text
          boolean hasSelection = (txt.getSelectedText() != null);
          
          // Accessing popup items by index (0: Cut, 1: Copy)
          popup.getComponent(0).setEnabled(hasSelection);
          popup.getComponent(1).setEnabled(hasSelection);
          
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
    });
    ////////////// end of right click ////
    
    JMenu m = getMenu ("File");
    m.setMnemonic ('F');
    
    JMenuItem nfile = getMenuItem("New", KeyEvent.VK_N, "CTRL-N");
    nfile.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          String s = JOptionPane.showInputDialog(mb, "<html><body><font color=\"#0000FF\" size=\"5\">Dou you want to start a new file?<br><br>e/y/t/yes OR h/n/no</font></body></html>");
          if (s == null) return;
          
          s = s.toLowerCase();
          if (s.equals("e") == false &&
            s.equals("yes") == false &&
            s.equals("y") == false &&
            s.equals("t") == false) {
            return; // İşlemi baslatma
          }
          
          txt.setText("");
          txt.setEditorKit(new StyledEditorKit());
          
          return;
        }
    });
    m.add(nfile);
    
    m.addSeparator();
    
    JMenuItem ac = getMenuItem ("Open", KeyEvent.VK_O, "CTRL-O");
    ac.addActionListener (this);
    m.add (ac);
    
    JMenuItem kaydet = getMenuItem ("Save", KeyEvent.VK_S, "CTRL-S");
    kaydet.addActionListener (new KaydetListener ());
    m.add (kaydet);
    
    m.addSeparator();
    
    JMenuItem openEnc = getMenuItem("Open Encrypted", KeyEvent.VK_0, "CTRL-0");
    openEnc.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          // Prepare the dialog title
          encjfc.setDialogTitle("Open Encrypted File");
          
          // Show dialog on EDT
          if (encjfc.showOpenDialog(mb) == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = encjfc.getSelectedFile();
            final String fileName = selectedFile.getName();
            
            // 1. Validation check
            if (!fileName.toLowerCase().endsWith(".enc")) {
              JOptionPane.showMessageDialog(mb, "This is not an encrypted file (.enc)!");
              return;
            }
            
            // 2. Ask for password on EDT
            final String fullPassword = getFullPassword();
            if (fullPassword == null) return;
            
            // 3. Perform heavy decryption in a background thread
            Thread decryptionThread = new Thread() {
              public void run() {
                try {
                  SecretKey key = getSecretKey(fullPassword);
                  
                  // Read and decrypt
                  byte[] encryptedData = java.nio.file.Files.readAllBytes(selectedFile.toPath());
                  byte[] decryptedData = decrypt(encryptedData, key);
                  
                  // Convert bytes to string (UTF-8)
                  final String content = new String(decryptedData, "UTF-8");
                  
                  // 4. Update UI back on the Event Dispatch Thread
                  SwingUtilities.invokeLater(new Runnable() {
                      public void run() {
                        // Set kit only when we are sure we have the data
                        txt.setEditorKit(new StyledEditorKit());
                        txt.setText(content);
                        myfocus();
                        txt.setCaretPosition(0);
                        statusLabel.setText("Opened and Decrypted: " + fileName);
                      }
                  });
                  
                  } catch (final Exception ex) {
                  // Handle errors on the EDT for the dialog
                  SwingUtilities.invokeLater(new Runnable() {
                      public void run() {
                        JOptionPane.showMessageDialog(mb, "Decryption failed: " + ex.getMessage());
                      }
                  });
                }
              }
            };
            decryptionThread.start();
          }
        }
    });
    m.add(openEnc);
    
    JMenuItem saveEnc = getMenuItem("Save Encrypted", KeyEvent.VK_K, "CTRL-K");
    saveEnc.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          encjfc.setDialogTitle("Save Encrypted File");
          //encjfc.setSelectedFile(new File("untitled.enc"));
          
          if (encjfc.showSaveDialog(mb) == JFileChooser.APPROVE_OPTION) {
            try {
              File selectedFile = encjfc.getSelectedFile();
              String filePath = selectedFile.getAbsolutePath();
              
              // Add .enc extension if not present
              if (!filePath.toLowerCase().endsWith(".enc")) {
                filePath = filePath + ".enc";
                selectedFile = new File(filePath);
              }
              
              String fullPassword = getFullPassword();
              if (fullPassword == null) return;
              
              SecretKey key = getSecretKey(fullPassword);
              byte[] data = txt.getText().getBytes(StandardCharsets.UTF_8);
              byte[] encryptedData = encrypt(data, key);
              java.nio.file.Files.write(selectedFile.toPath(), encryptedData);
              
              JOptionPane.showMessageDialog(mb, "Saved encrypted file: " + selectedFile.getName());
              } catch (Exception ex) {
              JOptionPane.showMessageDialog(mb, "Encryption failed: " + ex.getMessage());
            }
          }
        }
    });
    m.add(saveEnc);
    
    m.addSeparator();
    
    JMenuItem openObjectItem=getMenuItem ("Open Object File", -1, "");
    openObjectItem.addActionListener (new ObjectOpenListener ());
    m.add (openObjectItem);
    
    JMenuItem saveObjectItem=getMenuItem ("Save Object File", -1, "");
    saveObjectItem.addActionListener (new ObjectSaveListener ());
    m.add (saveObjectItem);
    
    m.addSeparator();
    
    JMenuItem openRTFItem = getMenuItem ("Open RTF", -1, "");
    openRTFItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          openRTF();
        }
    });
    m.add (openRTFItem);
    
    JMenuItem saveRTFItem = getMenuItem ("Save RTF", -1, "");
    saveRTFItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          saveRTF();
        }
    });
    m.add (saveRTFItem);
    
    m.addSeparator();
    
    JMenuItem openHTMLItem = getMenuItem ("Open HTML", -1, "");
    openHTMLItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          openHTML();
        }
    });
    m.add (openHTMLItem);
    
    JMenuItem saveHTMLItem = getMenuItem ("Save HTML", -1, "");
    saveHTMLItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          saveHTML();
        }
    });
    m.add (saveHTMLItem);
    
    m.addSeparator();
    
    JMenuItem undoItem = getMenuItem ("Undo", KeyEvent.VK_7, "CTRL-7");
    undoItem.addActionListener (uaction);
    m.add (undoItem);
    
    JMenuItem redoItem = getMenuItem ("Redo", -1, "");
    redoItem.addActionListener (raction);
    m.add (redoItem);
    
    m.addSeparator();
    
    JMenuItem screenItem = getMenuItem ("Screen", -1, "");
    screenItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          int mw = txt.getSize ().width;
          int mh = txt.getSize ().height;
          
          int CMIL=4000;
          
          if (mw>CMIL || mh>CMIL)
          {
            statusLabel.setText ("Too Very Vig Photo; Not Saved!");
            return;
          }
          
          boolean isbuf = txt.isDoubleBuffered ();
          txt.setDoubleBuffered (false);
          
          BufferedImage bim = new BufferedImage (mw, mh, 1);
          Graphics2D g2 = bim.createGraphics ();
          
          txt.paint ((Graphics2D)g2);
          
          txt.setDoubleBuffered (isbuf);
          
          File fscreen = new File ("npoperations/screen" + (++imgscr) + ".jpg");
          
          try
          {
            ImageIO.write (bim, "jpg", fscreen);
          }
          catch (Exception ex)
          {
            statusLabel.setText (""+ex.getMessage ()+ " Error!");
            return;
          }
          
          JOptionPane.showMessageDialog(mb, "npoperations/"+fscreen.getName () + " is ready!");
          
          return;
        }
    });
    m.add (screenItem);
    
    m.addSeparator();
    
    JMenuItem printPSItem = getMenuItem("ASCII Plain Text to PS", -1, "");
    printPSItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          final String text = txt.getText();
          if (text == null) return;
          
          final int tlen = text.length();
          if (tlen < 1) {
            JOptionPane.showMessageDialog(mb, "No text!");
            return;
          }
          
          try {
            PSPRINTER.exportToPS(text);
            } catch (IOException ioe) {
            statusLabel.setText("Error: " + ioe.getMessage());
            return;
          }
          
          statusLabel.setText("Saved ps to npoperations folder.");
        }
    });
    m.add(printPSItem);
    
    JMenuItem printObjectItem = getMenuItem("Print", KeyEvent.VK_P, "CTRL-P");
    printObjectItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          printObj();
        }
    });
    m.add(printObjectItem);
    
    m.addSeparator();
    
    JMenuItem git = getMenuItem ("Exit", KeyEvent.VK_Q, "CTRL-Q");
    git.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String m=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter Y/y/E/e For Exit!</font></body></html>");
          
          if (m==null) return;
          
          if (m.equals ("")) return;
          
          if (m.equalsIgnoreCase ("y")==false && m.equalsIgnoreCase ("e")==false)
          {
            return;
          }
          
          System.exit (0x0);
        }
    });
    m.add (git);
    
    mb.add (m);
    
    JMenu beatm=getMenu ("Beautify");
    
	// Create the menu item for cleaning problematic/odd characters
	JMenuItem harapcaSil = getMenuItem("Remove Odd Chars", -1, "");
	harapcaSil.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			String text = txt.getText();

			if (text == null || text.length() < 1) {
				statusLabel.setText("Text is null or too short!");
				return;
			}

			try {
				// Use StringBuilder with initial capacity to optimize memory usage
				StringBuilder sb = new StringBuilder(text.length());
				
				// Process the text using BufferedReader to handle lines correctly
				BufferedReader br = new BufferedReader(new StringReader(text));
				String line;
				boolean firstLine = true;

				while ((line = br.readLine()) != null) {
					// Keep the exact line structure by adding newline before subsequent lines
					if (!firstLine) {
						sb.append("\n");
					}
					firstLine = false;

					// Process every single character in the line (trim removed as requested)
					int lineLen = line.length();
					for (int i = 0; i < lineLen; i++) {
						char c = line.charAt(i);
						int n = (int) c;

						// 1. Handle critical control characters (0-31)
						// Keep: 9 (TAB), 10 (LF), 13 (CR). Remove others like Null, Bell, Esc etc.
						if (n < 32 && n != 9 && n != 10 && n != 13) {
							continue; // Skip problematic control characters
						}

						// 2. Mapping known problematic characters to clean equivalents
						switch (c) {
							case '`':   sb.append('\''); break;
							case (char)95:  sb.append('-'); break;
							case (char)127: sb.append(' '); break;
							case (char)160: sb.append(' '); break; // Non-breaking space
							case (char)166: sb.append(':'); break;
							case (char)168: sb.append('~'); break;
							case (char)170: sb.append('a'); break;
							case (char)171: sb.append('"'); break;
							case (char)173: sb.append('-'); break;
							case (char)175: sb.append('~'); break;
							case (char)176: sb.append("o:"); break;
							case (char)180: sb.append('\''); break;
							case (char)182: sb.append('~'); break;
							case (char)183: sb.append(' '); break;
							case (char)184: sb.append(' '); break;
							case (char)186: sb.append("o:"); break;
							case (char)187: sb.append('"'); break;
							case (char)247: sb.append('/'); break;
							case (char)8210: case (char)8211: case (char)8212: sb.append('-'); break; // Hyphens
							case (char)8213: sb.append("--"); break; // Horizontal bar
							case (char)8214: break; // Double vertical line (removed)
							case (char)8215: sb.append('='); break; // Double low line
							case (char)8216: case (char)8217: sb.append('\''); break; // Smart quotes
							case (char)8218: sb.append(','); break; // Single low-9 quote
							case (char)8219: sb.append('/'); break; // Reversed quote
							case (char)8220: case (char)8221: case (char)8223: sb.append('"'); break; // Smart double quotes
							case (char)8222: sb.append(','); break; // Double low-9 quote
							case (char)8224: case (char)8225: sb.append('+'); break; // Daggers
							case (char)8226: sb.append("--"); break; // Bullet
							case (char)8230: sb.append("..."); break; // Ellipsis
							case (char)8592: sb.append("<--"); break; // Left arrow
							case (char)8594: sb.append("-->"); break; // Right arrow
							case (char)9474: sb.append('-'); break; // Box drawings
							case (char)61694: sb.append('~'); break;
							case (char)65533: sb.append(' '); break; // Replacement char
							case (char)128: sb.append("EUR"); break; // Euro sign
							case (char)153: sb.append("(TM)"); break; // Trademark
							case (char)169: sb.append("(c)"); break; // Copyright
							case (char)174: sb.append("(r)"); break; // Registered
							
							default: sb.append(c); break; // Keep the original character
						}
					}
				}

				// Update the text area with cleaned content
				txt.setText(sb.toString());
				statusLabel.setText("Text cleaned successfully (No trim).");
				br.close();

			} catch (IOException ioe) {
				ioe.printStackTrace();
				statusLabel.setText("Error during cleaning process!");
			}

			myfocus();
		}
	});
	// Add the menu item to the specific menu group
	beatm.add(harapcaSil);
    
    beatm.addSeparator ();
    
    JMenuItem controlCharsItem = getMenuItem ("Control Chars", KeyEvent.VK_G, "CTRL-G");
    controlCharsItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          final String metin=txt.getText ();
          if (metin==null) return;
          
          final int metinlen=metin.length ();
          if (metinlen<1) return;
          
          CANKURTARAN=metin;
          
          ACTUALVEC.removeAllElements ();
          
          final int MIN=32;
          final int MAX=186;
          
          char chrchr='~';
          int  chrnum=0;
          String chrstr="";
          
          boolean existing=false;
          
          for (int i=0; i<metinlen; i++)
          {
            chrchr=metin.charAt (i);
            chrnum=(int)(chrchr);
            
            if (chrnum >= MIN && chrnum <= MAX) continue;
            
            chrstr=Integer.toString (chrnum);
            
            existing=KONTROLVEC.contains (chrstr);
            if (existing==true) continue;
            
            existing=ACTUALVEC.contains (chrstr);
            if (existing==true) continue;
            
            ACTUALVEC.add (chrstr);
          }
          
          int vsize=ACTUALVEC.size ();
          
          if (vsize<1)
          {
            statusLabel.setText ("THERE IS NOT CONTENT!");
            return;
          }
          
          String [] dizi=new String [vsize];
          vsize=dizi.length;
          
          for (int i=0; i<vsize; i++)
          {
            dizi [i]=ACTUALVEC.elementAt (i);
          }
          
          Arrays.sort (dizi);
          
          vsize=dizi.length;
          
          StringBuffer sb=new StringBuffer (metin);
          sb.append ("\n\n\n");
          sb.append ("===================\n");
          String ssm="";
          char ccm='i';
          
          for (int i=0; i<vsize; i++)
          {
            ssm=dizi [i];
            sb.append (ssm);
            sb.append (" --> ");
            ccm=(char)(Integer.parseInt (ssm));
            sb.append (ccm);
            sb.append ("\n\n");
          }
          
          sb.append ("===================\n");
          
          ssm=sb.toString ();
          
          int klen=ssm.length ();
          
          txt.setText (ssm);
                
          statusLabel.setText ("Control Chars Operation is Okey!");
          
          myfocus();
          
          return;
        }
    });
    beatm.add (controlCharsItem);
    
    beatm.addSeparator ();
    
    JMenuItem farapcaSil = getMenuItem ("Remove Words", -1, "");
    farapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"4\">Enter words separated with comma:</font><p><p>These chars starts with <b><font color=\"blue\" size=\"5\">\\ (92)</font></b> sign:<p><b><font color=\"blue\" size=\"5\">. * ? ^ + ( ) \\ $ { } | [ ]</font></b></body></html>");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          if (input.indexOf (",") < 0x0000)
          {
            input=(""+input+",");
          }
          
          String [] words=input.split (",");
          
          if (words == null)
          {
            statusLabel.setText ("Null Words For!");
            return;
          }
          
          final int wlen=words.length;
          
          if (wlen < 1)
          {
            statusLabel.setText ("There is not a word for remove.");
            return;
          }
          
          String word="";
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              for (int i=0; i<wlen; i++)
              {
                word=words [i];
                xline=xline.replaceAll (word, "");
              }
              
              sb.append (xline);
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
  
            txt.setText (mline);
            
            statusLabel.setText ("Removed words: "+input+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (farapcaSil);
    
    beatm.addSeparator();
    
    JMenuItem removePageNumbersItem = getMenuItem ("Remove Page Numbers", -1, "");
    removePageNumbersItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String m=txt.getText ();
          if (m==null)
          {
            statusLabel.setText ("Null Text Error!");
            return;
          }
          
          if (m.length () < 1)
          {
            statusLabel.setText ("Insufficient Text Error!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (m);
            BufferedReader br=new BufferedReader (sr);
            
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            String linex="";
            
            while ( (line=br.readLine ()) != null)
            {
              linex=line.trim ();
              
              int llen=linex.length ();
              
              if ((llen < 1) || (llen > 6))
              {
                sb.append (line);
                sb.append ("\n");
                
                continue;
              }
              
              linex=linex.replaceAll(" ", "");
              llen=linex.length ();
              
              boolean isSayi=true;
              
              char chr='a';
              
              for (int i=0; i<llen; i++)
              {
                chr=linex.charAt (i);
                if (chr == '.') continue;
                if ((Character.isDigit (chr)) == false)
                {
                  isSayi=false;
                  break;
                }
              }//end for
              
              if (isSayi)
              {
                continue;
              }
              else
              {
                sb.append (line);
                sb.append ("\n");
              }
            }//end while
            
            br.close ();
            sr.close ();
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
                        
            txt.setText (mline);
            
            statusLabel.setText ("Removed Page Numbers.");
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add(removePageNumbersItem);
    
    JMenuItem removeDuplicateLinesItem = getMenuItem ("Remove Duplicate Lines", -1, "");
    removeDuplicateLinesItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String m=txt.getText ();
          if (m==null)
          {
            statusLabel.setText ("Null Text Error!");
            return;
          }
          
          if (m.length () < 3)
          {
            statusLabel.setText ("Insufficient Text Error!");
            return;
          }
          
          removeLinesDuplicate(m);
        }
    });
    beatm.add(removeDuplicateLinesItem);
    
    JMenuItem addDiyez = getMenuItem("Add # for Footnotes", -1, "");
    addDiyez.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          String text = txt.getText();
          StringBuffer result = new StringBuffer();
          String[] lines = text.split("\\n");
          
          java.util.Map<String, String> footnoteMap = new java.util.HashMap<String, String>();
          java.util.List<Integer> footnoteLines = new java.util.ArrayList<Integer>();
          
          for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            
            if (line.matches(Help.DIYEZREGEX)) {
              footnoteLines.add(i);
              
              String[] parts = line.split(Help.SPACEREGEX, 2);
              if (parts.length == 2) {
                String key = parts[0].replaceAll(Help.XREGEX, "");
                String value = parts[1].trim();
                footnoteMap.put(key, value);
              }
            }
          }
          
          boolean inFootnoteBlock = false;
          
          for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            
            if (footnoteLines.contains(i)) {
              if (!inFootnoteBlock) {
                result.append("\n" + START_FN + ".\n");
                inFootnoteBlock = true;
              }
              
              String[] parts = line.trim().split(Help.YREGEX, 2);
              
              if (parts.length == 2) {
                String number = parts[0];
                String description = parts[1].trim();
                if (!description.endsWith(".")) {
                  description = description + ".";
                }
                result.append(number).append(". ").append(description).append("\n");
                } else {
                result.append(line).append("\n");
              }
              
              if (i + 1 >= lines.length || !footnoteLines.contains(i + 1)) {
                result.append(END_FN + ".\n");
                inFootnoteBlock = false;
              }
              } else {
              result.append(line).append("\n");
            }
          }
          
          txt.setText(result.toString());
        }
    });
    beatm.add(addDiyez);
    
    beatm.addSeparator();
    
    JMenuItem arapcaSil = getMenuItem ("Remove Chars Bigger Than", -1, "");
    arapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "Enter character num (Ex: 1000)");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          int MIL=1000;
          
          try
          {
            MIL=Integer.parseInt (input);
          }
          catch (NumberFormatException nfe)
          {
            statusLabel.setText ("Invalid Number Error!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              char ch='a';
              int m=0x0000;
              
              for (int i=0; i<lenz; i++)
              {
                ch=xline.charAt (i);
                m=(int)(ch);
                
                if (m > MIL) continue;
                
                sb.append (ch);
              }
              
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
    
            txt.setText (mline);           
            
            statusLabel.setText ("Removed Chars Bigger Than: "+(Integer.toString (MIL))+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (arapcaSil);
    
    JMenuItem marapcaSil = getMenuItem ("Remove Chars Lesser Than", -1, "");
    marapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "Enter character num (Ex: 1000)");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          int MIL=1000;
          
          try
          {
            MIL=Integer.parseInt (input);
          }
          catch (NumberFormatException nfe)
          {
            statusLabel.setText ("Invalid Number Error!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              char ch='a';
              int m=0x0000;
              
              for (int i=0; i<lenz; i++)
              {
                ch=xline.charAt (i);
                m=(int)(ch);
                
                if (m < MIL) continue;
                
                sb.append (ch);
              }
              
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
              
            txt.setText (mline);
                    
            statusLabel.setText ("Removed Chars Lesser Than: "+(Integer.toString (MIL))+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (marapcaSil);
    
    JMenuItem karapcaSil = getMenuItem ("Remove StartsWith Lines", -1, "");
    karapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "<html><body><font size=\"5\">Enter character or string:</font></body></html>");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              if (xline.startsWith (input))
              {
                continue;
              }
              
              sb.append (line);
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
                    
            txt.setText (mline);

            statusLabel.setText ("Removed Lines Starts With: "+input+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (karapcaSil);
    
    JMenuItem tarapcaSil = getMenuItem ("Remove Contains Lines", -1, "");
    tarapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "<html><body><font size=\"5\">Enter character or string:</font></body></html>");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            int indo=-1;
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              indo=xline.indexOf (input);
              
              if (indo >= 0x0000)
              {
                continue;
              }
              
              sb.append (line);
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
               
            txt.setText (mline);     
            
            statusLabel.setText ("Removed Lines Contains: "+input+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (tarapcaSil);
    
    JMenuItem jjarapcaSil = getMenuItem ("Remove Not Contains Lines", -1, "");
    jjarapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "<html><body><font size=\"5\">Enter character or string:</font></body></html>");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 1)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            int indo=-1;
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              indo=xline.indexOf (input);
              
              if (indo < 0x0000)
              {
                continue;
              }
              
              sb.append (line);
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            
            int klen=mline.length ();
               
            txt.setText (mline); 
            
            statusLabel.setText ("Removed Lines Contains: "+input+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (jjarapcaSil);
    
    beatm.addSeparator ();
    
    JMenuItem narapcaSil = getMenuItem ("Replace Multi Words", -1, "");
    narapcaSil.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String input=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter words in src=dst format<p>separated with comma:<p>Example:<p>selam=merhaba,belde=Sivas</font></body></html>");
          
          if (input == null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          int inlen=input.length ();
          if (inlen < 3)
          {
            statusLabel.setText ("Zero Text For!");
            return;
          }
          
          if (input.indexOf (",") < 0x0000)
          {
            input=(""+input+",");
          }
          
          String [] words=input.split (",");
          
          if (words == null)
          {
            statusLabel.setText ("Null Words For!");
            return;
          }
          
          final int wlen=words.length;
          
          if (wlen < 1)
          {
            statusLabel.setText ("There is not a word for replace.");
            return;
          }
          
          String word="";
          
          String wsrc="";
          String wdst="";
          
          try
          {
            Reader sr=new StringReader (text);
            BufferedReader br=new BufferedReader (sr);
            
            String xline="";
            String line=null;
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null )
            {
              xline=line.trim ();
              
              final int lenz=xline.length ();
              
              if (lenz < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              for (int i=0; i<wlen; i++)
              {
                word=words [i];
                
                if (word.indexOf ("=") < 0x0000) continue;
                
                String [] mplit=word.split ("=");
                
                if (mplit == null) continue;
                if (mplit.length < 2) continue;
                
                wsrc=mplit [0x0000];
                wdst=mplit [0x0001];
                
                if (wsrc.length () < 1) continue;
                
                xline=xline.replace (wsrc, wdst);
              }
              
              sb.append (xline);
              sb.append ("\n");
            }
            
            String mline=sb.toString ();
            int klen=mline.length ();
                   
            txt.setText (mline);
     
            statusLabel.setText ("Replaced words: "+input+"");
            
            br.close ();
            sr.close ();
          }
          catch (IOException ioe)
          {
            ioe.printStackTrace ();
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (narapcaSil);
    
    beatm.addSeparator ();
    
    JMenuItem replaceItem = getMenuItem ("Replace", KeyEvent.VK_R, "CTRL-R");
    replaceItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null text for Replace!");
            return;
          }
          
          if (mm.length () < 1)
          {
            statusLabel.setText ("Zero Length Text for Replace!!");
            return;
          }
          
          CANKURTARAN=mm;
          
          String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src string for replace:</font></body></html>");
          
          if (srcreps == null)
          {
            statusLabel.setText ("Not Replaced Null Value!");
            return;
          }
          
          if ((srcreps.length ()) < 1)
          {
            statusLabel.setText ("Not Replaced Zero Length Value!");
            return;
          }
          
          String dstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst string for replace:</font></body></html>");
          
          if (dstreps == null)
          {
            statusLabel.setText ("Not Replaced Null Value!");
            return;
          }
          
          try
          {
            String k=mm.replace (srcreps, dstreps);
            int klen=k.length ();
            
            txt.setText (k);
       
            statusLabel.setText ("Replace Operation is Okey: "+srcreps+" --> "+dstreps+"");
          }
          catch (Exception e)
          {
            statusLabel.setText ("Warning: Not Replaced!");
            return;
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (replaceItem);
    
    JMenuItem sreplaceItem = getMenuItem ("Specific Replace", KeyEvent.VK_J, "CTRL-J");
    sreplaceItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null text for Specific Replace!");
            return;
          }
          
          if (mm.length () < 1)
          {
            statusLabel.setText ("Zero Length Text for Specific Replace!");
            return;
          }
          
          CANKURTARAN=mm;
          
          String xsrcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src char num for replace like: 10;10</font></body></html>");
          
          if (xsrcreps == null)
          {
            statusLabel.setText ("Not Specific Replaced Src Null Value!");
            return;
          }
          
          if ((xsrcreps.length ()) < 1)
          {
            statusLabel.setText ("Not Specific Replaced Src Zero Length!");
            return;
          }
          
          String xdstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst char num for replace like: 32;<br><br>[0(cero is empty character.)]</font></body></html>");
          
          if (xdstreps == null)
          {
            statusLabel.setText ("Not Specific Replaced Dst Null Value!");
            return;
          }
          
          if ((xdstreps.length ()) < 1)
          {
            statusLabel.setText ("Not Specific Replaced Dst Zero Length!");
            return;
          }
          
          String menba [] = xsrcreps.split (";");
          String zenba [] = xdstreps.split (";");
          
          int menbalen=menba.length;
          int zenbalen=zenba.length;
          
          StringBuffer fm=new StringBuffer ("");
          
          for (int i=0; i<menbalen; i++)
          {
            int r=Integer.parseInt (menba[i]);
            fm.append ((char)r);
          }
          
          String srcreps=fm.toString ();
          
          fm=new StringBuffer ();
          
          for (int i=0; i<zenbalen; i++)
          {
            int r=Integer.parseInt (zenba[i]);
            fm.append ((char)r);
          }
          
          String dstreps=fm.toString ();
          //if (dstreps.equals("0")) dstreps = "";
          
          try
          {
            dstreps = dstreps.replace("\0", "");
            String k=mm.replace (srcreps, dstreps);
            int klen=k.length ();
               
            txt.setText (k);
            
            statusLabel.setText ("Specific Replace Operation is Okey: "+xsrcreps+" --> "+xdstreps+"");
          }
          catch (Exception e)
          {
            statusLabel.setText ("Warning: Not Specific Replaced!");
            //e.printStackTrace ();
            return;
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (sreplaceItem);
    
    JMenuItem multiSreplaceItem = getMenuItem ("Multi Specific Replace", -1, "");
    multiSreplaceItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm = txt.getText();
          
          if (mm == null || mm.length() < 1)
          {
            statusLabel.setText("Null or Zero Length Text for Multi Specific Replace!");
            return;
          }
          
          CANKURTARAN = mm;
          
          // Varsayilan: 0-9 rakamlarini 0. 1. 2. ... 9. seklinde degistir
          String defaultSrc = "48;10-49;10-50;10-51;10-52;10-53;10-54;10-55;10-56;10-57;10";
          String defaultDst = "48;46;32-49;46;32-50;46;32-51;46;32-52;46;32-53;46;32-54;46;32-55;46;32-56;46;32-57;46;32";
          
          String xsrcreps = JOptionPane.showInputDialog(mb,
            "<html><body><font color=\"blue\" size=\"5\">" +
            "Enter srcs for multi replace:<br>" +
            "Format: src1;dst1-src2;dst2-src3;dst3 ...<br>" +
            "Example: 48;10-49;10 (0 and 1 satır to newline)<br>" +
            "Note: ; sign of src-dst separate, - sign different replaces split." +
          "</font></body></html>", defaultSrc);
          
          if (xsrcreps == null || xsrcreps.length() < 1)
          {
            statusLabel.setText("Not Multi Specific Replaced - Src Null or Zero!");
            return;
          }
          
          String xdstreps = JOptionPane.showInputDialog(mb,
            "<html><body><font color=\"blue\" size=\"5\">" +
            "Enter dsts for multi replace:<br>" +
            "Format: src1;dst1-src2;dst2-src3;dst3 ...<br>" +
            "Example: 48;46;32;10-49;46;32;10 <br><br>" +
            "0(Cero) is empty character." +
          "</font></body></html>", defaultDst);
          
          if (xdstreps == null || xdstreps.length() < 1)
          {
            statusLabel.setText("Not Multi Specific Replaced - Dst Null or Zero!");
            return;
          }
          
          // Replace gruplarini ayir (- isareti ile)
          String[] srcGroups = xsrcreps.split("-");
          String[] dstGroups = xdstreps.split("-");
          
          // Grup sayilari esit mi kontrol et
          if (srcGroups.length != dstGroups.length)
          {
            statusLabel.setText("Error: Source and Destination group counts don't match!");
            return;
          }
          
          String result = mm;
          
          // Her bir replace grubunu isle
          for (int g = 0; g < srcGroups.length; g++)
          {
            String srcGroup = srcGroups[g];
            String dstGroup = dstGroups[g];
            
            // src ve dst'yi ; ile ayir
            String[] srcParts = srcGroup.split(";");
            String[] dstParts = dstGroup.split(";");
            
            if (srcParts.length == 0 || dstParts.length == 0)
            {
              statusLabel.setText("Warning: Empty group at index " + g);
              continue;
            }
            
            // src stringini olustur
            StringBuffer srcBuf = new StringBuffer();
            for (int i = 0; i < srcParts.length; i++)
            {
              try
              {
                int r = Integer.parseInt(srcParts[i].trim());
                srcBuf.append((char) r);
              }
              catch (NumberFormatException e)
              {
                statusLabel.setText("Warning: Invalid number in src group " + g + ": " + srcParts[i]);
              }
            }
            
            // dst stringini oluştur
            StringBuffer dstBuf = new StringBuffer();
            for (int i = 0; i < dstParts.length; i++)
            {
              try
              {
                int r = Integer.parseInt(dstParts[i].trim());
                dstBuf.append((char) r);
              }
              catch (NumberFormatException e)
              {
                statusLabel.setText("Warning: Invalid number in dst group " + g + ": " + dstParts[i]);
              }
            }
            
            String srcReps = srcBuf.toString();
            String dstReps = dstBuf.toString();
            //if (dstReps.equals("0")) dstReps = "";
            
            // Replace islemini yap
            try
            {
              dstReps = dstReps.replace("\0", "");
              result = result.replace(srcReps, dstReps);
              statusLabel.setText("Multi Replace Group " + (g+1) + ": " + srcGroup + " --> " + dstGroup);
            }
            catch (Exception e)
            {
              statusLabel.setText("Warning: Replace failed for group " + (g+1));
            }
          }
          
          // Sonucu text alanina yaz
          try
          {
            int klen = result.length();
    
            txt.setText(result);
   
            statusLabel.setText("Multi Specific Replace Operation Completed Successfully!");
          }
          catch (Exception e)
          {
            statusLabel.setText("Error: Failed to set text!");
            e.printStackTrace();
          }
          
          myfocus();
        }
    });
    beatm.add(multiSreplaceItem);
    
    JMenuItem excreplaceItem = getMenuItem ("Excepcional Replace", KeyEvent.VK_L, "CTRL-L");
    excreplaceItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null text for Excepcional Replace!");
            return;
          }
          
          if (mm.length () < 1)
          {
            statusLabel.setText ("Zero Length Text for Excepcional Replace!!");
            return;
          }
          
          CANKURTARAN=mm;
          
          String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src string for replace:</font></body></html>");
          
          if (srcreps==null)
          {
            statusLabel.setText ("Not Replaced Src Null Value!");
            return;
          }
          
          if ((srcreps.length ()) < 1)
          {
            statusLabel.setText ("Not Replaced Zero Src Length Value!");
            return;
          }
          
          String dstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst string for replace:</font></body></html>");
          
          if (dstreps==null)
          {
            statusLabel.setText ("Not Replaced Dst Null Value!");
            return;
          }
          
          String excreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter excepcional strings separated with comma:</font></body></html>");
          
          if (excreps==null)
          {
            statusLabel.setText ("Not Replaced Exc Null Value!");
            return;
          }
          
          if ((excreps.indexOf (",")) < 0x0000)
          {
            excreps=""+excreps+", ";
          }
          
          String [] excos=excreps.split (",");
          
          if (excos == null)
          {
            statusLabel.setText ("Not Replaced for Null Excepcionales!");
            return;
          }
          
          final int exclen=excos.length;
          if (exclen < 1)
          {
            statusLabel.setText ("Not Replaced for Cero Excepcionales!");
            return;
          }
          
          try
          {
            String k="";
            
            Reader sr=new StringReader (mm);
            BufferedReader br=new BufferedReader (sr);
            
            String line=null;
            String xline="";
            
            String word="";
            
            String excword="";
            String miniword="";
            
            StringBuffer sb=new StringBuffer ();
            
            while ( (line=br.readLine ()) != null)
            {
              line=line.trim ();
              
              if (line.length () < 1)
              {
                sb.append ("\n");
                continue;
              }
              
              for (int i=0; i<10; i++)
              {
                line=line.replace ("  ", " ");
              }
              
              xline=line;
              
              String [] kelimes=xline.split (" ");
              if (kelimes == null)
              {
                sb.append (xline);
                sb.append ("\n");
                continue;
              }
              
              int kelen=kelimes.length;
              if (kelen < 1)
              {
                sb.append (xline);
                sb.append ("\n");
                continue;
              }
              
              boolean found=false;
              
              for (int i=0; i<kelen; i++)
              {
                word=(kelimes [i]).trim ();
                if (word.length () < 1)
                {
                  continue;
                }
                
                if ((word.indexOf (srcreps)) < 0x0000)
                {
                  sb.append (word);
                  sb.append (" ");
                  continue;
                }
                
                miniword=word.toLowerCase ();
                
                found=false;
                
                for (int j=0; j<exclen; j++)
                {
                  excword=excos [j];
                  
                  if ((miniword.indexOf (excword)) >= 0x0000)
                  {
                    found=true;
                    break;
                  }
                }
                
                if (found)
                {
                  sb.append (word);
                  sb.append (" ");
                }
                else
                {
                  word=word.replace (srcreps, dstreps);
                  sb.append (word);
                  sb.append (" ");
                }
              }//for i<kelen
              
              sb.append ("\n");
            }//end while
            
            br.close ();
            sr.close ();
            
            k=sb.toString ();
            
            int klen=k.length ();
                  
            txt.setText (k);

            statusLabel.setText ("Excepcional Replace Operation is Okey.");
          }
          catch (Exception e)
          {
            statusLabel.setText ("Warning: Not Excepcional Replaced!");
            return;
          }
          
          myfocus();
          
          return;
        }
    });
    beatm.add (excreplaceItem);
    
    JMenuItem regexReplaceItem = getMenuItem("Regex Replace", KeyEvent.VK_M, "CTRL-M");
    regexReplaceItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          final String text = txt.getText();
          final int textlength = text.length();
          
          // Basic validation for content
          if (text == null || textlength < 1) {
            statusLabel.setText("No content available for Regex Replace.");
            return;
          }
          
          // Default patterns for user guidance
          String defaultFrom = "(\\d+):";
          String defaultTo = "$1.";
          
          // Input dialog for FROM pattern
          final String from = (String) JOptionPane.showInputDialog(
            null,
            "<html><font color='#0066CC' size='5'><b>Enter Regex Pattern (From):</b></font><br>" +
            "<font color='#666666' size='4'>Example: \\d+, \\w+, (\\p{L}+)(\\d+)</font></html>",
            "Regex Replace - From",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            defaultFrom
          );
          
          if (from == null || from.trim().isEmpty()) return;
          
          // Input dialog for TO pattern
          final String to = (String) JOptionPane.showInputDialog(
            null,
            "<html><font color='#0066CC' size='5'><b>Enter Replacement (To):</b></font><br>" +
            "<font color='#666666' size='4'>Example: $1, $2, or plain text</font></html>",
            "Regex Replace - To",
            JOptionPane.QUESTION_MESSAGE,
            null,
            null,
            defaultTo
          );
          
          if (to == null) {
			  myfocus();
			  return;
          }
          
          String safeDangerous = ProductionRegexSafetyCheck.checkForLargeText(from, (long)textlength);
          statusLabel.setText("REGEX SAFETY LEVEL: " + safeDangerous);
          if ("DANGEROUS".equals(safeDangerous)) {
            String s = JOptionPane.showInputDialog(mb,
            "<html><body><font color=\"#0000BB\" size=\"5\">This regex may be dangerous.<br>Do you want to continue(ignoreCase e/yes/y/t || h/no/n)?</font></body></html>");
            if (s == null) {
				myfocus();
				return;
            }
            s = s.toLowerCase();
            if (s.equals("e") == false &&
              s.equals("yes") == false &&
              s.equals("y") == false &&
              s.equals("t") == false) {
			  myfocus();
              return; // İşlemi baslatma
            }
          }
          
          // Start Background Task using SwingWorker
          SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            
            @Override
            protected String doInBackground() throws Exception {
              // Change cursor to wait mode on the main component
              txt.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              
              // Process escape sequences (e.g., \n to newline character)
              String processedFrom = escapeSequences(from);
              String processedTo = escapeSequences(to);
              
              // Perform the regex replacement in the background thread
              Pattern pattern = Pattern.compile(processedFrom, Pattern.UNICODE_CHARACTER_CLASS);
              Matcher matcher = pattern.matcher(text);
              return matcher.replaceAll(processedTo);
            }
            
            @Override
            protected void done() {
              try {
                // Task finished, get the result
                String result = get();
                
                // Update UI with the new text
                txt.setText(result);
                myfocus();
                
                statusLabel.setText("Regex Replace (SwingWorker) completed successfully.");
              }
              catch (java.util.concurrent.ExecutionException e) {
                // Specific handling for Regex syntax errors
                Throwable cause = e.getCause();
                String msg = (cause != null) ? cause.getMessage() : "Unknown Error";
                
                JOptionPane.showMessageDialog(mb,
                  "<html><font color='red'><b>Regex Error:</b></font><br>" + msg + "</html>",
                "Regex Syntax Error", JOptionPane.ERROR_MESSAGE);
              }
              catch (Exception e) {
                e.printStackTrace();
              }
              finally {
				myfocus();
                // Always restore the default cursor
                txt.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
              }
            }
          };
          
          // Execute the background thread to keep GUI responsive
          worker.execute();
        }
    });
    beatm.add(regexReplaceItem);
    
    beatm.addSeparator();
    
    JMenuItem insertIndentsItem = getMenuItem("Insert Indents", -1, "");
    insertIndentsItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          String input = txt.getText();
          if (input == null || input.isEmpty()) return;
          
          String[] lines = input.split("\n");
          StringBuilder formattedText = new StringBuilder();
          int indentLevel = 0;
          final String INDENT_STR = "    ";
          boolean inBlockComment = false;
          
          for (String rawLine : lines) {
            String line = rawLine.trim();
            
            if (line.isEmpty()) {
              formattedText.append("\n");
              continue;
            }
            
            int openCount = 0;
            int closeCount = 0;
            int leadingClosings = 0;
            boolean inString = false;
            boolean countingLeading = true;
            
            for (int i = 0; i < line.length(); i++) {
              char c = line.charAt(i);
              
              // 1. Block Comment Check
              if (!inString && !inBlockComment && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '*') {
                inBlockComment = true; i++; continue;
              }
              if (inBlockComment && c == '*' && i + 1 < line.length() && line.charAt(i + 1) == '/') {
                inBlockComment = false; i++; continue;
              }
              if (inBlockComment) continue;
              
              // 2. Line Comment Check
              if (!inString && c == '/' && i + 1 < line.length() && line.charAt(i + 1) == '/') break;
              
              // 3. String & Escape Check
              if (c == '\\' && i + 1 < line.length() && inString) { i++; continue; }
              if (c == '"') { inString = !inString; continue; }
                
              // 4. Brace Counting
              if (!inString) {
                if (c == '{') {
                  openCount++;
                  countingLeading = false;
                  } else if (c == '}') {
                  closeCount++;
                  if (countingLeading) leadingClosings++;
                  } else if (!Character.isWhitespace(c)) {
                  countingLeading = false;
                }
              }
            }
            
            // --- Geometrik Hesaplama ---
            
            int currentLineIndent = indentLevel - leadingClosings;
            if (currentLineIndent < 0) currentLineIndent = 0;
            
            for (int j = 0; j < currentLineIndent; j++) {
              formattedText.append(INDENT_STR);
            }
            
            formattedText.append(line).append("\n");
            
            indentLevel += (openCount - closeCount);
            if (indentLevel < 0) indentLevel = 0;
          }
          
          txt.setText(formattedText.toString());
          txt.setCaretPosition(0);
        }
    });
    beatm.add(insertIndentsItem);
    
    JMenuItem prettyXMLItem = getMenuItem("Pretty XML", -1, "");
    prettyXMLItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          prettyFormatXML();
        }
    });
    beatm.add(prettyXMLItem);
    
    beatm.addSeparator ();
    
	JMenuItem replaceTodoItem = getMenuItem("Beautify Text", KeyEvent.VK_T, "CTRL-T");
	replaceTodoItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			final String m = txt.getText(); 
			// final keyword for access inside inner class

			if (m == null) {
				statusLabel.setText("Null Text for Beutify Operation!");
				return;
			}

			if (m.length() < 1) {
				statusLabel.setText("Zero Length Text for Beautify Operation!");
				return;
			}

			CANKURTARAN = m;
			V2Utils.setBorrowNums(false);

			String movitext = null;
			boolean error = false;

			try {
				String ntext = V2UTFToISO.cnt(m);
				if (error == false) {
					movitext = ntext;
				}
			} catch (IOException ioe) {
				error = true;
				movitext = null;
			}

			if (error) {
				statusLabel.setText("An error occurred V2UTFToISO.");
				return;
			}

			if (movitext != null) {
				// Define final variable for thread safety inside Runnable
				final String finalMovitext = movitext;

				// Start the UI update safely on the Event Dispatch Thread
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						txt.setText(finalMovitext);
						txt.setCaretPosition(0);
						
						// Force the layout manager to recalculate word wraps correctly
						myfocus();
					}
				});
			}
		}
	});
	beatm.add(replaceTodoItem);
    
    mb.add (beatm);
    
    JMenu m2a=getMenu ("Show");
    
    JMenuItem changeEncodingItem = getMenuItem ("Change Encoding", -1, "");
    changeEncodingItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          setCharCode();
        }
    });
    m2a.add(changeEncodingItem);
    
    m2a.addSeparator();
    
	// Create the menu item with "Show Char Num" label
	JMenuItem sayCharItem = getMenuItem("Show Char Num", -1, "");
	sayCharItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			String m = txt.getSelectedText();

			// 1. Alert if no text is selected
			if (m == null || m.isEmpty()) {
				statusLabel.setText("No selection found.");
				JOptionPane.showMessageDialog(mb, "Select at least one character...", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// 2. Limit the process to the first 7 characters
			int MX = m.length();
			if (MX > 7) MX = 7;

			// 3. Prepare HTML with explicit font sizes for every cell
			// We use 18pt for general data and 24pt for the character itself
			StringBuffer sb = new StringBuffer("<html><body>");
			sb.append("<h2 style='color:blue; font-size:20pt;'>Character Analysis-First 7 chars</h2>");
			sb.append("<table border='1' cellpadding='10' cellspacing='0'>");
			sb.append("<tr bgcolor='#eeeeee'>");
			sb.append("<th><font size='6'>Chr</font></th>");
			sb.append("<th><font size='6'>Dec</font></th>");
			sb.append("<th><font size='6'>Hex</font></th>");
			sb.append("<th><font size='6'>Uni</font></th>");
			sb.append("<th><font size='6'>Binary (16-bit)</font></th>");
			sb.append("</tr>");

			for (int i = 0; i < MX; i++) {
				char chr = m.charAt(i);
				int n = (int) chr;

				// Handle special characters
				String display;
				if (n == 10) display = "[LF]";
				else if (n == 13) display = "[CR]";
				else if (n == 9) display = "[TAB]";
				else if (n == 32) display = "[SP]";
				else display = String.valueOf(chr);

				// Conversions
				String hex = Integer.toHexString(n).toUpperCase();
				String bin = Integer.toBinaryString(n);
				
				// Manual padding for 16-bit binary
				String binPadded = "0000000000000000" + bin;
				binPadded = binPadded.substring(binPadded.length() - 16);
				
				// Manual padding for Unicode
				String uni = "0000" + hex;
				uni = "\\u" + uni.substring(uni.length() - 4);

				// Add row with explicit font tags for Java 6/7 compatibility
				sb.append("<tr>");
				sb.append("<td align='center'><b><font size='7' color='red'>").append(display).append("</font></b></td>");
				sb.append("<td align='center'><font size='6'>").append(n).append("</font></td>");
				sb.append("<td align='center'><font size='6'>0x").append(hex).append("</font></td>");
				sb.append("<td align='center'><font size='6'>").append(uni).append("</font></td>");
				sb.append("<td align='center'><font size='5' color='blue'>").append(binPadded).append("</font></td>");
				sb.append("</tr>");
			}

			sb.append("</table></body></html>");

			// 4. Show the dialog
			JOptionPane.showMessageDialog(mb, sb.toString(), "Character Details", JOptionPane.INFORMATION_MESSAGE);
			myfocus();
		}
	});
	// Add to menu
	m2a.add(sayCharItem);
    
    m2a.addSeparator ();
    
    JMenuItem sayCharItemY = getMenuItem ("Show Numerous Char", -1, "");
    sayCharItemY.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String m=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter int value for see its char:</font></body></html>");
          
          if (m==null)
          {
            statusLabel.setText ("Null Text For Show Numerous Char!");
            return;
          }
          
          if (m.length () < 1)
          {
            statusLabel.setText ("Zero Length Text For Show Numerous Char!");
            return;
          }
          
          int val=0;
          try
          {
            val=Integer.parseInt (m);
          }
          catch (Exception e)
          {
            statusLabel.setText ("Error Entering Int Val!");
            return;
          }
          
          if (val < 0 || val > 65535)
          {
            statusLabel.setText ("Range Error!");
            return;
          }
          
          char chr=(char)(val);
          
          StringBuffer sb=new StringBuffer ();
          sb.append (chr);
          String nc=sb.toString ();
          
          JOptionPane.showMessageDialog(mb, ("<html><body><font color=\"blue\" size=\"5\">"+Integer.toString (val)+" -> "+nc+"</font></body></html>"));
          myfocus();
          return;
        }
    });
    m2a.add (sayCharItemY);
    
    JMenuItem appendNumerousChar = getMenuItem ("Append Numerous Char", -1, "");
    appendNumerousChar.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String m=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"4\">Enter int value for append its char to caret pos:</font><font color=\"red\" size=\"4\"><br><br>You can enter like 0xNN hexadecimeal format too.</font></body></html>");
          
          if (m==null)
          {
            statusLabel.setText ("Null Text For Show Numerous Char!");
            return;
          }
          
          if (m.length () < 1)
          {
            statusLabel.setText ("Zero Length Text For Show Numerous Char!");
            return;
          }
          
          int val=0;
          try
          {
            if (m.startsWith("0x")) {
              m = m.substring(2);
              val = Integer.parseInt(m, 0x10);
              } else {
              val = Integer.parseInt (m);
            }
          }
          catch (Exception e)
          {
            statusLabel.setText ("Error Entering Int Val!");
            return;
          }
          
          if (val < 0 || val > 65535)
          {
            statusLabel.setText ("Range Error!");
            return;
          }
          
          char chr=(char)(val);
          
          StringBuffer sb=new StringBuffer ();
          sb.append (chr);
          String nc=sb.toString ();
          
          try {
            int pos = txt.getCaretPosition();
            txt.getDocument().insertString(pos, nc, null);
            myfocus();
            return;
            } catch (BadLocationException e) {
            JOptionPane.showMessageDialog(mb, e.getMessage());
            e.printStackTrace();
            return;
          }
        }
    });
    m2a.add (appendNumerousChar);
    
    JMenuItem charMapItem = getMenuItem("CharMap", -1, "");
    charMapItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          XCharacterMap map = new XCharacterMap(txt);
          map.setVisible(true);
        }
    });
    m2a.add(charMapItem);
    
    JMenuItem timeStampItem = getMenuItem("Append Time Stamp", -1, "");
    timeStampItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          // Java 8 Date formatter
          java.time.LocalDateTime now = java.time.LocalDateTime.now();
          java.time.format.DateTimeFormatter formatter =
          java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
          String formatlanmisZaman = now.format(formatter);
          
          // Append text to current caret pos.
          txt.replaceSelection(formatlanmisZaman);
          myfocus();
        }
    });
    m2a.add(timeStampItem);
    
    JMenuItem insertIconItem = getMenuItem("Insert Icon", -1, "");
    insertIconItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          //txt.setEditorKit(new StyledEditorKit());
          // Open the file chooser (iconjfc is already defined as per your request)
          int result = iconjfc.showOpenDialog(mb);
          
          if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = iconjfc.getSelectedFile();
            
            // Ask the user for dimensions in Width*Height format
            String sizeInput = JOptionPane.showInputDialog(
              null,
              "Enter dimensions (Width*|xHeight):\nExample: 250*|x150",
              "Set Image Size",
              JOptionPane.QUESTION_MESSAGE
            );
            
            sizeInput = sizeInput.replace("x", "*");
            
            // Proceed if input is not null and contains the asterisk separator
            if (sizeInput != null && sizeInput.contains("*")) {
              try {
                // Split the input string and trim any accidental spaces
                String[] parts = sizeInput.split("\\*");
                int width = Integer.parseInt(parts[0].trim());
                int height = Integer.parseInt(parts[1].trim());
                
                // Create the icon from the selected file path
                ImageIcon rawIcon = new ImageIcon(selectedFile.getAbsolutePath());
                
                // Scale the image using smooth algorithm for better quality in Java 7
                Image rawImg = rawIcon.getImage();
                Image scaledImg = rawImg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                ImageIcon finalIcon = new ImageIcon(scaledImg);
                
                // Insert the scaled icon at the current caret position
                txt.insertIcon(finalIcon);
                
                // Add a trailing space after the icon for better text flow
                txt.getDocument().insertString(txt.getCaretPosition(), " ", null);
                myfocus();
                } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(mb, "Invalid number! Please use '300*200' format.");
                } catch (Exception ex) {
                JOptionPane.showMessageDialog(mb, "Error while inserting image: " + ex.getMessage());
                ex.printStackTrace();
              }
            }
          }
        }
    });
    m2a.add(insertIconItem);
    
    m2a.addSeparator ();
    
	JMenuItem ipsumItem = getMenuItem("Insert Ipsum", -1, "");
	ipsumItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			try {
				int pos = txt.getCaretPosition();
				txt.getDocument().insertString(pos, Help.IPSUM, null);
			} catch (Exception e) {
				statusLabel.setText("Error: Could not append ipsum; " + e.getMessage());
			}
		}
	});
	m2a.add(ipsumItem);
    
    JMenuItem enterEmojiItem = getMenuItem ("Convert Hex to Emoji", -1, "");
    enterEmojiItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          convertAllHexToUnicode();
        }
    });
    m2a.add (enterEmojiItem);
    
    m2a.addSeparator();
    
    JMenuItem ltrItem = getMenuItem ("Left-to-Right", -1, "");
    ltrItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          txt.setComponentOrientation (ComponentOrientation.LEFT_TO_RIGHT);
        }
    });
    m2a.add (ltrItem);
    
    JMenuItem rtlItem = getMenuItem ("Right-to-Left", -1, "");
    rtlItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          txt.setComponentOrientation (ComponentOrientation.RIGHT_TO_LEFT);
        }
    });
    m2a.add (rtlItem);
    
    m2a.addSeparator();
    
    JMenuItem sortSelectedLinesItem = getMenuItem("Sort Selected Lines", -1, "");
    sortSelectedLinesItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          sortSelectedLines(txt);
        }
    });
    m2a.add(sortSelectedLinesItem);
    
    mb.add (m2a);
    
    JMenu m2b=getMenu ("Case");
    
    JMenuItem convertToAsciiItem = getMenuItem("Convert Selected to ASCII", -1, "");
    convertToAsciiItem.addActionListener(new ActionListener () {
        public void actionPerformed(ActionEvent evt)
        {
          statusLabel.setText("");
          String selected=txt.getSelectedText();
          if (selected==null)
          {
            statusLabel.setText ("There is Not Selected Text!");
            return;
          }
          
          if (selected.length() < 1)
          {
            statusLabel.setText ("There is Not Selected Text!");
            return;
          }
          
          String content=convertToAscii(selected);
          
          txt.replaceSelection(content);
          myfocus();
          
          return;
        }
      }
    );
    m2b.add (convertToAsciiItem);
    
    m2b.addSeparator();
    
	// Item creation with custom helper method
	JMenuItem caseMagicItem = getMenuItem("Case Magic", -1, "");
	caseMagicItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			// Get the current text selection
			final String selected = txt.getSelectedText();

			// Validate selection: must not be null or empty
			if (selected == null || selected.trim().isEmpty()) {
				JOptionPane.showMessageDialog(mb, 
					"<html><b style='color:red;'>Warning:</b> Please select some text first!</html>", 
					"No Selection", 
					JOptionPane.WARNING_MESSAGE);
				myfocus();
				return;
			}

			// Define fonts and colors from the text pane for UI consistency
			Font uiFont = txt.getFont();
			Color uiColor = txt.getForeground();

			// Initialize Radio Buttons with consistent styling
			JRadioButton camelBtn = new JRadioButton("camelCase");
			JRadioButton snakeBtn = new JRadioButton("snake_case");
			JRadioButton upperBtn = new JRadioButton("UPPER CASE");
			JRadioButton lowerBtn = new JRadioButton("lower case");
			JRadioButton firstUpperBtn = new JRadioButton("First Letters Upper"); // New
			JRadioButton firstLowerBtn = new JRadioButton("First Letters Lower"); // New
			JRadioButton reverseBtn = new JRadioButton("Reverse Text");           // New
			JRadioButton desireBtn = new JRadioButton("Custom Replacement");

			JRadioButton[] buttons = {
				camelBtn, snakeBtn, upperBtn, lowerBtn, 
				firstUpperBtn, firstLowerBtn, reverseBtn, desireBtn
			};
			ButtonGroup group = new ButtonGroup();

			// Apply styles and group buttons
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setFont(uiFont);
				buttons[i].setForeground(uiColor);
				group.add(buttons[i]);
			}
			upperBtn.setSelected(true); // Default action

			// Construct the message panel with HTML title for better readability
			Object[] params = {
				"<html><div style='margin-bottom:5px;'><b style='font-size:14px; color:#2980b9;'>Transformation Magic</b><br>" 
				+ "<b style='font-size:14px; color:#0000ff;'>Choose your target format:</b></div></html>",
				camelBtn, snakeBtn, upperBtn, lowerBtn, 
				firstUpperBtn, firstLowerBtn, reverseBtn, desireBtn
			};

			// Show the dialog
			int res = JOptionPane.showConfirmDialog(mb, params, "Case Magic", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (res == JOptionPane.OK_OPTION) {
				String result = selected;
				int len = selected.length();

				if (upperBtn.isSelected()) {
					result = selected.toUpperCase();
				} else if (lowerBtn.isSelected()) {
					result = selected.toLowerCase();
				} else if (firstUpperBtn.isSelected()) {
					// Logic: Capitalize first letter of each word. Limit: 25,000 chars
					if (len > 25000) {
						statusLabel.setText("WARNING: Selection too long for Capitalization (Max 25k)!");
						return;
					}
					char[] chars = selected.toLowerCase().toCharArray();
					boolean found = false;
					for (int i = 0; i < chars.length; i++) {
						if (!found && Character.isLetter(chars[i])) {
							chars[i] = Character.toUpperCase(chars[i]);
							found = true;
						} else if (Character.isWhitespace(chars[i])) {
							found = false;
						}
					}
					result = new String(chars);
				} else if (firstLowerBtn.isSelected()) {
					// Logic: Lowercase first letter of each word. Limit: 25,000 chars
					if (len > 25000) {
						statusLabel.setText("WARNING: Selection too long for First Letter Lower (Max 25k)!");
						return;
					}
					char[] chars = selected.toUpperCase().toCharArray();
					boolean found = false;
					for (int i = 0; i < chars.length; i++) {
						if (!found && Character.isLetter(chars[i])) {
							chars[i] = Character.toLowerCase(chars[i]);
							found = true;
						} else if (Character.isWhitespace(chars[i])) {
							found = false;
						}
					}
					result = new String(chars);
				} else if (reverseBtn.isSelected()) {
					// Logic: Reverse the entire string. Limit: 100,000 chars
					if (len > 100000) {
						statusLabel.setText("WARNING: Selection too long for Reverse (Max 100k)!");
						return;
					}
					result = new StringBuilder(selected).reverse().toString();
				} else if (snakeBtn.isSelected()) {
					result = selected.trim().toLowerCase().replaceAll("[\\s-]+", "_");
				} else if (camelBtn.isSelected()) {
					String[] words = selected.trim().split("[\\s_-]+");
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < words.length; i++) {
						String word = words[i].toLowerCase();
						if (i > 0 && word.length() > 0) {
							word = Character.toUpperCase(word.charAt(0)) + word.substring(1);
						}
						sb.append(word);
					}
					result = sb.toString();
				} else if (desireBtn.isSelected()) {
					String toReplace = JOptionPane.showInputDialog(mb, 
						"<html><body style='width:200px;'><font color='blue'><b>Enter custom text:</b></font></body></html>");
					if (toReplace != null) {
						result = toReplace; 
					}
				}

				// Apply the transformation to the document
				txt.replaceSelection(result);
				statusLabel.setText("Transformation applied successfully.");
			}

			// Always return focus to the editor
			myfocus();
		}
	});
	m2b.add(caseMagicItem);

    m2b.addSeparator ();
    
    JMenuItem selStartItem = getMenuItem ("Start Selection With", -1, "");
	selStartItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			String selected = txt.getSelectedText();

			if (selected == null || selected.isEmpty()) {
				statusLabel.setText("There is No Selected Text!");
				return;
			}

			String input = JOptionPane.showInputDialog(mb, "...");
			
			if (input == null || input.isEmpty()) {
				statusLabel.setText("Input cancelled or empty.");
				myfocus();
				return;
			}

			input = replaceEscapeSequences(input);
			StringBuilder sb = new StringBuilder(); // Mpre fast

			try (BufferedReader br = new BufferedReader(new StringReader(selected))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.trim().isEmpty()) {
						sb.append("\n");
					} else {
						sb.append(input).append(line).append("\n");
					}
				}
				
				if (sb.length() > 0) sb.setLength(sb.length() - 1);
				
				txt.replaceSelection(sb.toString());
				myfocus();
			} catch (IOException ioe) {
				statusLabel.setText("Error occurred!");
			}

			myfocus();
		}
	});
    m2b.add (selStartItem);
    
	JMenuItem selEndItem = getMenuItem("End Selection With", -1, "");
	selEndItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			String selected = txt.getSelectedText();

			// 1. Validation for selection
			if (selected == null || selected.isEmpty()) {
				statusLabel.setText("There is No Selected Text!");
				return;
			}

			// 2. UI Input
			String prompt = "<html><body><font color=\"#0000EE\" size=\"5\">Enter char or word (incluse \\t \\n \\r) for append<p>to </font><font color=\"#EE0000\" size=\"5\">end</font><font color=\"#0000EE\" size=\"4\"> of each selected line:</font></body></html>";
			String input = JOptionPane.showInputDialog(mb, prompt);

			if (input == null || input.isEmpty()) {
				statusLabel.setText("Input cancelled or empty.");
				myfocus();
				return;
			}

			// 3. Process input and text
			input = replaceEscapeSequences(input);
			StringBuilder sb = new StringBuilder(); // More efficient than StringBuffer

			// 4. Line processing with Auto-Closeable resources
			try (BufferedReader br = new BufferedReader(new StringReader(selected))) {
				String line;
				while ((line = br.readLine()) != null) {
					// If line is empty or just whitespace
					if (line.trim().isEmpty()) {
						sb.append("\n");
						continue;
					}
					
					// Append input at the end of the line
					sb.append(line).append(input).append("\n");
				}

				// Remove the last extra newline if added
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n') {
					sb.setLength(sb.length() - 1);
				}

				txt.replaceSelection(sb.toString());
				txt.requestFocus ();
				statusLabel.setText("Appended to end of lines successfully.");

			} catch (IOException ioe) {
				ioe.printStackTrace();
				statusLabel.setText("An error occurred during processing!");
			}

			// 5. Final Focus Management (Non-lambda version)
			myfocus();
		}
	});
	m2b.add(selEndItem);
    
    m2b.addSeparator ();
    
	JMenuItem xfindItem = getMenuItem("Change Find Word", KeyEvent.VK_B, "CTRL-B");
	xfindItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			String content = JOptionPane.showInputDialog(mb,
				"<html><body>" +
				"<font color=\"blue\" size=\"5\">Text For Find:</font><br><br>" +
				"<font color=\"blue\" size=\"4\">" +
				"You can enter normal text, ASCII codes (;), or Unicode (\\uXXXX).<br>" +
				"Example 1: apple<br>" +
				"Example 2: 97;112;112;108;101 (ASCII)<br>" +
				"Example 3: \\u0061\\u0070\\u0070\\u006c\\u0065 (Unicode)<br><br>" +
				"<font color=\"red\">" +
				"NOTE: Regex patterns are supported (e.g., \\d+).<br>" +
				"The normal 'Find' menu works with plain text only.<br>" +
				"Use regexFind for regex contains expressions." + 
				"</font>" +
				"</font>" +
				"</body></html>");

			if (content == null) {
				statusLabel.setText("Find word input cancelled!");
				myfocus();
				return;
			}

			if (content.length() < 1) {
				statusLabel.setText("Find word is empty!");
				CONFIND = "";
				myfocus();
				return;
			}

			String processedWord = content;

			// 1. Check for Unicode format (uXXXX)
			if (content.contains("\\u") || content.contains("\\U")) {
				try {
					processedWord = fromUnicode(content); 
					// Using your existing professional fromUnicode method
					statusLabel.setText("Find word (from Unicode): " + processedWord);
				} catch (Exception e) {
					processedWord = content;
					statusLabel.setText("Unicode parse error! Using raw text.");
				}
			}
			// 2. Check for ASCII format (97;98;99)
			else {
				boolean isAsciiFormat = true;
				for (int i = 0; i < content.length(); i++) {
					char c = content.charAt(i);
					if (!Character.isDigit(c) && c != ';') {
						isAsciiFormat = false;
						break;
					}
				}

				if (isAsciiFormat && content.contains(";")) {
					try {
						String[] codes = content.split(";");
						StringBuffer decoded = new StringBuffer();
						for (String code : codes) {
							String trimmed = code.trim();
							if (trimmed.length() > 0) {
								decoded.append((char) Integer.parseInt(trimmed));
							}
						}
						processedWord = decoded.toString();
						statusLabel.setText("Find word (from ASCII): " + processedWord);
					} catch (NumberFormatException e) {
						processedWord = content;
						statusLabel.setText("ASCII parse error! Using raw text.");
					}
				}
			}

			CONFIND = processedWord;
			statusLabel.setText("Current find word/regex: [" + CONFIND + "]");
			myfocus();
		}
	});
	m2b.add(xfindItem);
    
    JMenuItem findItem = getMenuItem ("Find", KeyEvent.VK_F, "CTRL-F");
    findItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          //String content=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"4\"> Text For Find: </font></body></html>");
          
          String content=CONFIND;
          
          if (content==null)
          {
            statusLabel.setText ("There is Not Content!");
            return;
          }
          
          if (content.length () < 1)
          {
            statusLabel.setText ("There is Not Content!");
            return;
          }
          
          String metin=txt.getText ();
          
          if (metin==null)
          {
            statusLabel.setText ("There is Not Text!");
            return;
          }
          
          if (metin.length () < 1)
          {
            statusLabel.setText ("There is Not Text!");
            return;
          }
          
          CANKURTARAN=metin;
          
          int caret=txt.getCaretPosition ();
          
          int index = metin.indexOf (content, caret);
          
          if (index < 0)
          {
            statusLabel.setText (""+content+" --> Not Found!");
            return;
          }
          
          int strt=index;
          int endd=index+(content.length ());
          
          txt.setCaretPosition (endd);
          
          txt.select (strt, endd);
          metin=txt.getText ();
          
          return;
        }
      }
    );
    m2b.add (findItem);
    
	JMenuItem regexFindItem = getMenuItem("Regex Find", KeyEvent.VK_Y, "CTRL-Y");
	regexFindItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		  final String patternStr = CONFIND;
		  final String text = txt.getText();
		  final int caret = txt.getCaretPosition();
		  
		  // --- 1. PRE-CHECK & SAFETY ---
		  if (patternStr == null || patternStr.isEmpty()) {
			statusLabel.setText("Error: Pattern is empty.");
			return;
		  }
		  
		  final String processedPattern = escapeSequencesForRegex(patternStr);
		  String safeLevel = ProductionRegexSafetyCheck.checkForLargeText(processedPattern, (long) text.length());
		  
		  if ("DANGEROUS".equals(safeLevel)) {
			String confirm = JOptionPane.showInputDialog(mb,
			  "<html><body>" +
			  "<font color='blue' size='5'><b>Regex Warning:</b> Potential freeze detected!</font><br>" +
			  "<font color='gray' size='3'>This pattern might be slow on large text.</font><br><br>" +
			  "<font color='red' size='4'>Do you want to continue? (y/e/t/yes/ok)</font>" +
			"</body></html>");
			
			if (confirm == null) return;
			
			String input = confirm.trim().toLowerCase();
			
			// Original logic preserved: y (yes), e (evet), t (true), yes, ok
			boolean shouldContinue = input.equals("y") ||
			input.equals("e") ||
			input.equals("t") ||
			input.equals("yes") ||
			input.equals("ok");
			
			if (!shouldContinue) {
			  statusLabel.setText("Search aborted by user.");
			  return;
			}
		  }
		  
		  // --- 2. SWINGWORKER TASK ---
		  // Defined as a final variable so the Cancel Action can access it
		  final SwingWorker<MatchResult, Void> worker = new SwingWorker<MatchResult, Void>() {
			
			@Override
			protected MatchResult doInBackground() throws Exception {
			  Pattern pattern = Pattern.compile(processedPattern, Pattern.UNICODE_CHARACTER_CLASS);
			  
			  // Using a custom CharSequence to allow interruption inside the regex engine
			  CharSequence interruptibleText = new CharSequence() {
				@Override public int length() { return text.length(); }
				@Override public char charAt(int index) {
				  // Check if worker was cancelled
				  if (Thread.currentThread().isInterrupted()) {
					throw new RuntimeException("UserInterrupted");
				  }
				  return text.charAt(index);
				}
				@Override public CharSequence subSequence(int start, int end) {
				  return text.subSequence(start, end);
				}
				@Override public String toString() { return text; }
			  };

			  Matcher matcher = pattern.matcher(interruptibleText);
			  
			  try {
				if (matcher.find(caret)) {
				  return new MatchResult(matcher.start(), matcher.end(), matcher.group(), true);
				}
			  } catch (RuntimeException e) {
				if ("UserInterrupted".equals(e.getMessage())) return null;
				throw e;
			  }
			  return new MatchResult(0, 0, null, false);
			}
			
			@Override
			protected void done() {
			  // Cleanup: Remove the Escape-to-Cancel shortcut once task is finished
			  txt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));

			  try {
				if (isCancelled()) {
				  statusLabel.setText("Regex Search Cancelled.");
				  return;
				}
				
				MatchResult result = get();
				if (result == null) return; // Means interrupted
				
				if (result.found) {
				  txt.setCaretPosition(result.end);
				  txt.select(result.start, result.end);
				  statusLabel.setText("Found: \"" + result.matchedText + "\"");
				} else {
				  statusLabel.setText("No matches found.");
				  JOptionPane.showMessageDialog(mb, "Pattern not found!", "Regex Find", JOptionPane.INFORMATION_MESSAGE);
				}
				//myfocus();
			  } catch (Exception e) {
				statusLabel.setText("Regex Error: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
			  }
			}
		  };
		  
		  // --- 3. ABORT SHORTCUT (ESC) ---
		  // Register temporary Escape key action without Lambda
		  txt.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelSearch");
		  txt.getActionMap().put("cancelSearch", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
			  worker.cancel(true);
			  statusLabel.setText("Aborting Regex Search...");
			}
		  });
		  
		  // --- 4. EXECUTION ---
		  statusLabel.setText("Searching: " + patternStr + " (Press ESC to cancel)");
		  worker.execute();
		}
	});
	m2b.add(regexFindItem);
    
    m2b.addSeparator ();
    
    JMenuItem ftUpperItem = getMenuItem ("Found to Upper Case", KeyEvent.VK_5, "CTRL-5");
    ftUpperItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null Text For Found to Upper Case!");
            return;
          }
          
          if (mm.length () < 1)
          {
            statusLabel.setText ("Zero Length Text For Found to Upper Case!");
            return;
          }
          
          CANKURTARAN=mm;
          
          String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter Expresion For Replacing to Upper Case:</font></body></html>");
          
          if (srcreps==null)
          {
            statusLabel.setText ("NOT REPLACED NULL VALUE!");
            myfocus();
            return;
          }
          
          if (srcreps.length () < 1)
          {
            statusLabel.setText ("NOT REPLACED ZERO LENGTH!");
            myfocus();
            return;
          }
          
          try
          {
            final String dest=srcreps.toUpperCase ();
            String k=mm.replaceAll (srcreps, dest);
            txt.setText ("");
            txt.setText (k);
            myfocus();
            statusLabel.setText ("Replaced Selection to Upper Case Successfully!");
          }
          catch (Exception e)
          {
            statusLabel.setText ("WARNING: NOT REPLACED TO UPPER CASE!");
            myfocus();
            return;
          }
          
          return;
        }
    });
    m2b.add (ftUpperItem);
    
    JMenuItem ftLowerItem = getMenuItem ("Found to Lower Case", KeyEvent.VK_6, "CTRL-6");
    ftLowerItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          if (mm.length () < 1)
          {
            statusLabel.setText ("Zero Length Text For!");
            return;
          }
          
          CANKURTARAN=mm;
          
          String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter Expresion For Replacing to Lower Case:</font></body></html>");
          
          if (srcreps==null)
          {
            statusLabel.setText ("NOT REPLACED NULL VALUE!");
            myfocus();
            return;
          }
          
          if (srcreps.length () < 1)
          {
            statusLabel.setText ("NOT REPLACED ZERO LENGTH!");
            myfocus();
            return;
          }
          
          try
          {
            final String dest=srcreps.toLowerCase ();
            String k=mm.replaceAll (srcreps, dest);
            txt.setText ("");
            txt.setText (k);
            myfocus();
            statusLabel.setText ("Replaced Selection to Lower Case Successfully!");
          }
          catch (Exception e)
          {
            statusLabel.setText ("WARNING: NOT REPLACED TO LOWER CASE!");
            myfocus();
            return;
          }
          
          return;
        }
    });
    m2b.add (ftLowerItem);
    
    m2b.addSeparator ();
    
    JMenuItem toSimplyNumsItem = getMenuItem ("Remove ExtraLines of Footnotes", -1, "");
    toSimplyNumsItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          String mext = text.replaceAll("\\.\n\n0", ".\n0");
          
          mext = mext.replaceAll("\\.\n\n1", ".\n1");
          mext = mext.replaceAll("\\.\n\n2", ".\n2");
          mext = mext.replaceAll("\\.\n\n3", ".\n3");
          mext = mext.replaceAll("\\.\n\n4", ".\n4");
          mext = mext.replaceAll("\\.\n\n5", ".\n5");
          mext = mext.replaceAll("\\.\n\n6", ".\n6");
          mext = mext.replaceAll("\\.\n\n7", ".\n7");
          mext = mext.replaceAll("\\.\n\n8", ".\n8");
          mext = mext.replaceAll("\\.\n\n9", ".\n9");
          mext = mext.replaceAll("\\.\n\n" + END_FN + "\\.", ".\n" + END_FN + ".");
          
          txt.setText (mext);
          txt.setCaretPosition (0x0000);
          
          statusLabel.setText ("Is okey required replaces.");
          
          return;
        }
    });
    m2b.add (toSimplyNumsItem);
    
    JMenuItem easyRead1 = getMenuItem("Numbered Footnotes to Between Brackets", -1, "");
    easyRead1.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          String currentText = txt.getText();
          
          if (currentText == null || currentText.length() < 1) {
            statusLabel.setText("No text to process!");
            return;
          }
          
          try {
            // Process each START_FN/END_FN block separately
            String processedText = processBlocksSeparately(currentText);
            
            int pos = txt.getCaretPosition();
            
            txt.setText(processedText);
            
            myfocus();
            
            statusLabel.setText("Numbered footnotes applied successfully.");
            } catch (Exception e) {
            statusLabel.setText("Error applying numbered footnotes: " + e.getMessage());
          }
          
          myfocus();
        }
    });
    m2b.add(easyRead1);
    
    JMenuItem correctFalseFootnotesItem = getMenuItem ("Correct False Footnotes", -1, "");
    correctFalseFootnotesItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          String text = txt.getText();
          if (text == null || text.length() < 20) return;
          
          String result = text;
          
          result = result.replaceAll(START_FN+"\\.", "\n" + START_FN + ".\n");
          result = result.replaceAll(END_FN+"\\.", "\n" + END_FN + ".\n");
          result = result.replaceAll("\n\n###", "\n###");
          result = result.replaceAll("@\\.\n\n", "@.\n");
          result = result.replaceAll("&\\.\n\n", "&.\n");
          result = result.replaceAll("\n ", "\n");
          
          txt.setText(result);
          myfocus();
          
          statusLabel.setText ("Corrected False Footnotes.");
        }
    });
    m2b.add(correctFalseFootnotesItem);
    
    m2b.addSeparator ();
    
    JMenuItem convertLatinItem = getMenuItem ("Convert To Latin General", KeyEvent.VK_9, "CTRL-9");
    convertLatinItem.addActionListener (new ActionListener ()
      {
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          String text=txt.getText ();
          
          if (text==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=text.length ();
          
          if (len < 3)
          {
            statusLabel.setText ("VERY SHORT LENGTH TEXT!");
            return;
          }
          
          CANKURTARAN=text;
          
          // Norwegian
          text = text.replaceAll("AAAAA", "\u00C5");  // Å
          text = text.replaceAll("aaaaa", "\u00E5");  // å
          
          // Turkish
          text=text.replaceAll ("AAAA", "\u00C2");
          text=text.replaceAll ("IIII", "\u00CE");
          text=text.replaceAll ("UUUU", "\u00DB");
          text=text.replaceAll ("aaaa", "\u00E2");
          text=text.replaceAll ("iiii", "\u00EE");
          text=text.replaceAll ("uuuu", "\u00FB");
          
          // Latin
          text=text.replaceAll ("AAA", "\u00C1");
          text=text.replaceAll ("EEE", "\u00C9");
          text=text.replaceAll ("III", "\u00CD");
          text=text.replaceAll ("OOO", "\u00D3");
          text=text.replaceAll ("UUU", "\u00DA");
          text=text.replaceAll ("NNN", "\u00D1");
          text=text.replaceAll ("\\?\\?\\?", "\u00BF");
          text=text.replaceAll ("!!!", "\u00A1");
          text=text.replaceAll ("aaa", "\u00E1");
          text=text.replaceAll ("eee", "\u00E9");
          text=text.replaceAll ("iii", "\u00ED");
          text=text.replaceAll ("ooo", "\u00F3");
          text=text.replaceAll ("uuu", "\u00FA");
          text=text.replaceAll ("nnn", "\u00F1");
          text=text.replaceAll ("KAVISLIAC", "{");
          text=text.replaceAll ("KAVISLIKAPA", "}");
          
          // Norwegian characters
          text = text.replaceAll("AAEE", "\u00C6");   // Æ
          text = text.replaceAll("aaee", "\u00E6");   // æ
          text = text.replaceAll("OOEE", "\u00D8");   // Ø
          text = text.replaceAll("ooee", "\u00F8");   // ø
          
          text=text.replaceAll ("\"\"", "\'");
          text=text.replaceAll ("KOSELIAC", "[");
          text=text.replaceAll ("KOSELIKAPA", "]");
          //text=text.replaceAll ("//", "\\");
          
          text=text.replaceAll ("CC", "\u00C7");
          text=text.replaceAll ("GG", "\u011E");
          text=text.replaceAll ("II", "\u0130");
          text=text.replaceAll ("OO", "\u00D6");
          text=text.replaceAll ("SS", "\u015E");
          text=text.replaceAll ("UU", "\u00DC");
          text=text.replaceAll ("cc", "\u00E7");
          text=text.replaceAll ("gg", "\u011F");
          text=text.replaceAll ("ii", "\u0131");
          text=text.replaceAll ("oo", "\u00F6");
          text=text.replaceAll ("ss", "\u015F");
          text=text.replaceAll ("uu", "\u00FC");
          
          txt.setText (text);
          
          myfocus();
          
          statusLabel.setText ("The Text has Converted to Latin Form Successfully.");
          
          return;
        }
    });
    m2b.add (convertLatinItem);
    
	// Create the menu item for converting text to Unicode escape sequences
	JMenuItem convertUnicode = getMenuItem("Convert To Unicode", -1, "");
	convertUnicode.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			
			// 1. Get only the selected text
			String selectedText = txt.getSelectedText();
			
			// 2. Alert if no text is selected with a stylish dialog
			if (selectedText == null || selectedText.isEmpty()) {
				statusLabel.setText("No selection found.");
				JOptionPane.showMessageDialog(mb, 
					"<html><b style='color:red; font-size:18pt;'>Selection Required:</b><br>Please select at least one character to convert...</html>", 
					"Conversion Warning", 
					JOptionPane.WARNING_MESSAGE);
				myfocus();
				return;
			}

			// Save original full text as backup (as per your CANKURTARAN logic)
			CANKURTARAN = txt.getText();
			
			String res = null;
			try {
				// 3. Process only the selected portion
				res = toUnicode(selectedText);
			} catch (Exception e) {
				e.printStackTrace();
				statusLabel.setText("Unicode conversion failed!");
				res = null;
			}

			// 4. Replace only the selected part with the converted text
			if (res != null) {
				// replaceSelection is the most efficient way to swap the selected block
				txt.replaceSelection(res);
				myfocus();
				statusLabel.setText("Selected text converted to Unicode successfully.");
			}
		}
	});
	// Add the item to the specific menu group
	m2b.add(convertUnicode);
    
	// Create the menu item for converting Unicode escape sequences back to characters
	JMenuItem convertFromUnicode = getMenuItem("Convert From Unicode", -1, "");
	convertFromUnicode.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			
			// 1. Get only the selected portion of the text
			String selectedText = txt.getSelectedText();
			
			// 2. Stylish alert if no text is selected
			if (selectedText == null || selectedText.isEmpty()) {
				statusLabel.setText("No selection found.");
				JOptionPane.showMessageDialog(mb, 
					"<html><b style='color:red; font-size:18pt;'>Selection Required:</b><br>Please select the Unicode text you wish to decode...</html>", 
					"Conversion Warning", 
					JOptionPane.WARNING_MESSAGE);
				myfocus();
				return;
			}

			// Save original full text as backup for safety
			CANKURTARAN = txt.getText();
			
			String res = null;
			try {
				// 3. Convert only the selected Unicode string back to normal characters
				res = fromUnicode(selectedText);
			} catch (Exception e) {
				e.printStackTrace();
				statusLabel.setText("Unicode decoding failed!");
				res = null;
			}

			// 4. Replace the selected block with the decoded text
			if (res != null) {
				txt.replaceSelection(res);
				myfocus();
				statusLabel.setText("Selected Unicode form converted back to text successfully.");
			}
		}
	});
	// Add the item to the specific menu group
	m2b.add(convertFromUnicode);
    
    m2b.addSeparator();
    
    JMenuItem hexcvtItem=getMenuItem ("Convert To Hex", -1, "");
    hexcvtItem.addActionListener (new CodeURLListener ());
    m2b.add (hexcvtItem);
    
    JMenuItem hexFcvtItem=getMenuItem ("Convert From Hex", -1, "");
    hexFcvtItem.addActionListener (new FCodeURLListener ());
    m2b.add (hexFcvtItem);
    
    mb.add (m2b);
    ////////////////
    
    JMenu extraMenu = getMenu("Extra");
    
    JCheckBox isTextEnabledBox = new JCheckBox("Text Enabled", true);
    isTextEnabledBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
    isTextEnabledBox.setForeground(Color.BLACK);
    
    isTextEnabledBox.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
          if (e.getStateChange() == ItemEvent.SELECTED) {
            txt.setEnabled(true);
            } else {
            txt.setEnabled(false);
          }
        }
    });
    extraMenu.add(isTextEnabledBox);
    
    JMenuItem showFileInfoItem=getMenuItem ("File Info Report", -1, "");
    showFileInfoItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          // Open file chooser on top of the menu bar
          int returnVal = commonjfc.showOpenDialog(mb);
          
          // 1. Safety Check: If user cancels or closes the dialog
          if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
          }
          
          File selectedFile = commonjfc.getSelectedFile();
          
          // 2. Critical Safety: Check for null or non-existent (ghost) files
          // Short-circuit || ensures no NullPointerException
          if (selectedFile == null || !selectedFile.exists()) {
            JOptionPane.showMessageDialog(mb,
              "Selected file is not valid or does not exist!",
              "File Error",
            JOptionPane.WARNING_MESSAGE);
            return;
          }
          
          Path path = selectedFile.toPath();
          
          try {
            // Fetch detailed attributes (Java 7+ NIO.2)
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            UserPrincipal owner = Files.getOwner(path);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            
            // Extract file extension
            String fileName = selectedFile.getName();
            String extension = "Unknown";
            int dotIndex = fileName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
              extension = fileName.substring(dotIndex + 1).toUpperCase();
            }
            
            // Check permissions
            String permissions = (Files.isWritable(path) ? "Read/Write" : "Read-Only");
            if (Files.isExecutable(path)) permissions += " (Executable)";
            
            // Prepare elegant HTML display
            String result = "<html>" +
            "<div style='width: 320px; font-family: Segoe UI, Tahoma, sans-serif; padding: 10px;'>" +
            " <b style='font-size: 14px; color: #2c3e50;'>FILE PROPERTIES</b>" +
            " <hr size='1' color='#3498db' style='margin-bottom: 8px;'>" +
            " <table style='font-size: 14px; width: 100%;' cellpadding='3'>" +
            " <tr><td style='color: #7f8c8d;'><b>Name:</b></td><td>" + fileName + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Type:</b></td><td>" + extension + " File</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Size:</b></td><td>" + String.format("%,d bytes", attr.size()) + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Permissions:</b></td><td>" + permissions + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Created:</b></td><td>" + sdf.format(attr.creationTime().toMillis()) + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Modified:</b></td><td>" + sdf.format(attr.lastModifiedTime().toMillis()) + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Access:</b></td><td>" + sdf.format(attr.lastAccessTime().toMillis()) + "</td></tr>" +
            " <tr><td style='color: #7f8c8d;'><b>Owner:</b></td><td>" + owner.getName() + "</td></tr>" +
            " <tr><td style='color: #7f8c8d; vertical-align: top;'><b>Location:</b></td>" +
            " <td style='font-size: 12px; color: #34495e;'>" + selectedFile.getParent() + "</td></tr>" +
            " </table>" +
            " <hr size='1' color='#ecf0f1' style='margin-top: 10px;'>" +
            "</div></html>";
            
            // Display the final result
            JOptionPane.showMessageDialog(mb, result, "File Details", JOptionPane.PLAIN_MESSAGE);
            
            } catch (Exception ex) {
            // Handle IO or Permission exceptions
            JOptionPane.showMessageDialog(mb, "Error retrieving metadata: " + ex.getMessage(),
            "System Error", JOptionPane.ERROR_MESSAGE);
          }
        }
    });
    extraMenu.add(showFileInfoItem);
    
    JMenuItem addDetailedTimeStampItem = getMenuItem("Add Detailed Time Stamp", -1, "");
    addDetailedTimeStampItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          // 1. Define the 7 different format patterns
          String[] patterns = {
            "yyyy-MM-dd HH:mm:ss",
            "EEE, MMM dd, yyyy",
            "dd-MMM-yyyy HH:mm",
            "MM/dd/yyyy",
            "HH:mm",
            "yyyy.MM.dd",
            "EEEE, MMMM dd"
          };
          
          // 2. Prepare the UI components
          JPanel panel = new JPanel(new GridLayout(patterns.length, 1));
          ButtonGroup group = new ButtonGroup();
          JRadioButton[] buttons = new JRadioButton[patterns.length];
          java.util.Date now = new java.util.Date();
          
          // 3. Create buttons with the ACTUAL formatted time as their label
          for (int i = 0; i < patterns.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat(patterns[i], Locale.ENGLISH);
            String formattedTime = sdf.format(now);
            
            buttons[i] = new JRadioButton(formattedTime);
            buttons[i].setFont(TXTFONT);
            buttons[i].setForeground(Color.BLUE);
            
            if (i == 0) buttons[i].setSelected(true); // Default first item
              
            group.add(buttons[i]);
            panel.add(buttons[i]);
          }
          
          // 4. Show the dialog with the radio buttons
          int result = JOptionPane.showConfirmDialog(mb, panel, "Select Time Stamp",
          JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
          
          // 5. If user clicks OK, insert the text of the selected radio button
          if (result == JOptionPane.OK_OPTION) {
            String selectedTime = "";
            for (JRadioButton rb : buttons) {
              if (rb.isSelected()) {
                selectedTime = rb.getText();
                break;
              }
            }
            
            // Directly insert the text at the caret position
            txt.replaceSelection(selectedTime);
            myfocus();
            // Log to status label
            statusLabel.setText(" Inserted: " + selectedTime);
          }
        }
    });
    extraMenu.add(addDetailedTimeStampItem);
    
    extraMenu.addSeparator();
    
    JMenuItem base64EncodeItem = getMenuItem("Base64 Selected Encode", -1, "");
    base64EncodeItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          String seciliMetin = txt.getSelectedText();
          if (seciliMetin != null && !seciliMetin.isEmpty()) {
            try {
              String encoded = Base64.getEncoder().encodeToString(seciliMetin.getBytes("UTF-8"));
              txt.replaceSelection(encoded);
              myfocus();
              } catch (Exception ex) {
              JOptionPane.showMessageDialog(mb, "UTF-8 Not Supported Error!");
            }
            } else {
            statusLabel.setText("Warning: select some text first.");
            return;
          }
        }
    });
    extraMenu.add(base64EncodeItem);
    
    JMenuItem base64DecodeItem = getMenuItem("Base64 Selected Decode", -1, "");
    base64DecodeItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          String seciliMetin = txt.getSelectedText();
          if (seciliMetin != null && !seciliMetin.isEmpty()) {
            try {
              byte[] decodedBytes = Base64.getDecoder().decode(seciliMetin.trim());
              String decoded = new String(decodedBytes, "UTF-8");
              txt.replaceSelection(decoded);
              myfocus();
              } catch (IllegalArgumentException ex) {
              JOptionPane.showMessageDialog(mb, "Error: Selected Text is not valid Base64.");
              } catch (Exception ex) {
              ex.printStackTrace();
            }
            } else {
            statusLabel.setText("Warning: select some text first.");
            return;
          }
        }
    });
    extraMenu.add(base64DecodeItem);
    
    extraMenu.addSeparator();
    
    // Action for MD5 Generation
    JMenuItem generateMD5Item = getMenuItem("Generate MD5", -1, "");
    generateMD5Item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processHash("MD5");
        }
    });
    extraMenu.add(generateMD5Item);
    
    // Action for SHA-256 Generation
    JMenuItem generateSHAItem = getMenuItem("Generate SHA", -1, "");
    generateSHAItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processHash("SHA-256");
        }
    });
    extraMenu.add(generateSHAItem);
    
    extraMenu.addSeparator();
    
    JMenuItem xorAllItem = getMenuItem("XOR Encrypt All", -1, "");
    xorAllItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processXOR(false);
        }
    });
    extraMenu.add(xorAllItem);
    
    JMenuItem xorSelectedItem = getMenuItem("XOR Encrypt Selected", -1, "");
    xorSelectedItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processXOR(true);
        }
    });
    extraMenu.add(xorSelectedItem);
    
    extraMenu.addSeparator();
    
    JMenuItem removeStatusTextItem = getMenuItem("Remove Status Text", -1, "");
    removeStatusTextItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          return;
        }
    });
    extraMenu.add(removeStatusTextItem);
    
    JMenuItem gcItem = getMenuItem("GC", -1, "");
    gcItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          System.gc();
          statusLabel.setText("Garbage collector is okey.");
          return;
        }
    });
    extraMenu.add(gcItem);
    
    extraMenu.addSeparator();
    
    JMenuItem removeExtraSpacesItem = getMenuItem("Remove Extra Spaces", -1, "");
    removeExtraSpacesItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // 1. Get the content from JTextPane
          String content = txt.getText();
          
          // 2. Safety check: avoid processing if null or effectively empty
          if (content == null || content.isEmpty()) {
            return;
          }
          
          // 3. Process the text:
          // [ \t]+ matches one or more spaces or tab characters
          // We replace them with a single space to keep the line clean
          // We do NOT use \\s+ because it would also remove newlines (\n)
          String cleanedContent = content.replaceAll("[ \t]+", " ");
          
          // 4. Trim leading/trailing spaces from each line if necessary
          // (Optional: content.trim() could be added here for the whole document)
          String finalResult = cleanedContent.trim();
          
          // 5. Update the text pane with the refined text
          txt.setText(finalResult);
          statusLabel.setText("Removed extra spaces.");
        }
    });
    extraMenu.add(removeExtraSpacesItem);
    
    JMenuItem trimLineItem = getMenuItem("Trim Lines", -1, "");
    trimLineItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // 1. Retrieve the text
          String content = txt.getText();
          
          // 2. Return if empty
          if (content == null || content.isEmpty()) {
            return;
          }
          
          // 3. Split content by line breaks (\n)
          // We use -1 to keep empty lines at the end if they exist
          String[] lines = content.split("\n", -1);
          StringBuilder sb = new StringBuilder();
          
          for (int i = 0; i < lines.length; i++) {
            // 4. Trim each individual line
            sb.append(lines[i].trim());
            
            // 5. Re-add the newline character except for the very last line
            if (i < lines.length - 1) {
              sb.append("\n");
            }
          }
          
          // 6. Update the text area
          txt.setText(sb.toString());
          statusLabel.setText("Trimmed lines.");
        }
    });
    extraMenu.add(trimLineItem);
    
    extraMenu.addSeparator();
    
    JMenuItem wordCloudAllItem = getMenuItem("Word Analysis All", -1, "");
    wordCloudAllItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processWordCloud(false);
        }
    });
    extraMenu.add(wordCloudAllItem);
    
    JMenuItem wordCloudSelectedItem = getMenuItem("Word Analysis Selected", -1, "");
    wordCloudSelectedItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          processWordCloud(true);
        }
    });
    extraMenu.add(wordCloudSelectedItem);
    
    extraMenu.addSeparator();
    
    JMenuItem goToLineItem = getMenuItem("Go to Line", -1, "");
    goToLineItem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          statusLabel.setText("");
          // 1. Ask user for the line number
          String input = JOptionPane.showInputDialog(mb,
            "<html><body><font color=\"blue\" size=\"5\">Enter Line Number:</font></body></html>",
          "Go To Line", JOptionPane.QUESTION_MESSAGE);
          
          if (input != null && !input.isEmpty()) {
            try {
              int targetLine = Integer.parseInt(input);
              String content = txt.getText();
              String[] lines = content.split("\n", -1);
              
              // 2. Validate line number (Lines start from 1)
              if (targetLine > 0 && targetLine <= lines.length) {
                int pos = 0;
                // 3. Calculate character offset for the target line
                for (int i = 0; i < targetLine - 1; i++) {
                  pos += lines[i].length() + 1; // +1 for the newline character (\n)
                }
                
                // 4. Move the caret and request focus
                txt.setCaretPosition(pos);
                myfocus();
                } else {
                JOptionPane.showMessageDialog(mb, "Line number out of range!", "Error", JOptionPane.ERROR_MESSAGE);
              }
              } catch (NumberFormatException nfe) {
              JOptionPane.showMessageDialog(mb, "Please enter a valid number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
          }
        }
    });
    extraMenu.add(goToLineItem);
    
    mb.add(extraMenu);
    
    /////////////
    
    JMenu selectedMenu = getMenu ("Selected");
    
    final String [] families = {
      "Arial",
      "Times New Roman",
      "Verdana",
      "Calibri",
      "FreeSans"
    };
    
    final String [] ffamilies = {
      "Arial",
      "Times New Roman",
      "Scheherazade",
      "Verdana",
      "Comic Sans MS",
      "Select!"
    };
    
    int lenf = families.length;
    int lenff=ffamilies.length;
    
    for (int i=0; i<lenf; i++)
    {
      JMenuItem ix = getMenuItem (families [i], -1, "");
      ix.addActionListener (new StyledEditorKit.FontFamilyAction (families [i], families [i]));
      selectedMenu.add (ix);
    }
    
    selectedMenu.addSeparator ();
    
    JMenuItem sboldItem=getMenuItem ("Bold", -1, "");
    sboldItem.addActionListener (new StyledEditorKit.BoldAction ());
    //sboldItem.addActionListener (new TestActionListener ());
    selectedMenu.add (sboldItem);
    
    JMenuItem sitalicItem=getMenuItem ("Italic", -1, "");
    sitalicItem.addActionListener (new StyledEditorKit.ItalicAction ());
    selectedMenu.add (sitalicItem);
    
    JMenuItem sunderlineItem=getMenuItem ("Underline", -1, "");
    sunderlineItem.addActionListener (new StyledEditorKit.UnderlineAction ());
    selectedMenu.add (sunderlineItem);
    
    selectedMenu.addSeparator ();
    
    for (int i = 20; i <= 26; i += 2)
    {
      JMenuItem ix = getMenuItem (("Font Size: " +i+""), -1, "");
        ix.addActionListener (new StyledEditorKit.FontSizeAction ("FontSize", i));
        selectedMenu.add (ix);
      }
      
      selectedMenu.addSeparator ();
      
      Color [] colores = new Color [] {
        Color.BLACK,
        Color.RED,
        Color.GREEN,
        Color.BLUE
      };
      
      String colstrs [] = new String [] {
        "Black",
        "Red",
        "Green",
        "Blue"
      };
      
      for (int i = 0; i < colstrs.length; i++)
      {
        JMenuItem ix = getMenuItem (colstrs [i], -1, "");
        ix.addActionListener (new StyledEditorKit.ForegroundAction (colstrs [i], colores [i]));
        selectedMenu.add (ix);
      }
      
      selectedMenu.addSeparator();
      
      JMenuItem fgColorPickerItem = getMenuItem("FG Color Picker", -1, "");
      fgColorPickerItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            statusLabel.setText("");
            // Open the standard Java color chooser
            Color selectedColor = JColorChooser.showDialog(mb, "Select Text Color", Color.BLACK);
            
            if (selectedColor != null) {
              // Define the style attribute for foreground color
              SimpleAttributeSet attrs = new SimpleAttributeSet();
              StyleConstants.setForeground(attrs, selectedColor);
              
              // Apply the style to the selected text range
              int start = txt.getSelectionStart();
              int end = txt.getSelectionEnd();
              txt.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
            }
          }
      });
      selectedMenu.add(fgColorPickerItem);
      
      JMenuItem bgColorPickerItem = getMenuItem("BG Color Picker", -1, "");
      bgColorPickerItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            statusLabel.setText("");
            // Open the color chooser for highlight color
            Color selectedColor = JColorChooser.showDialog(mb, "Select Background Color", Color.YELLOW);
            
            if (selectedColor != null) {
              SimpleAttributeSet attrs = new SimpleAttributeSet();
              StyleConstants.setBackground(attrs, selectedColor);
              //final String bgstr = String.format("#%02x%02x%02x", selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue());
              //attrs.addAttribute(HTML.Attribute.STYLE, "background-color: " + bgstr + "\"");
              //"background-color: #FFFF00");
              
              int start = txt.getSelectionStart();
              int end = txt.getSelectionEnd();
              txt.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
            }
          }
      });
      selectedMenu.add(bgColorPickerItem);
      
      JMenuItem textSizePickerItem = getMenuItem("Text Size Picker", -1, "");
      textSizePickerItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            statusLabel.setText("");
            // Request the size from the user
            String sizeStr = JOptionPane.showInputDialog(mb, "Enter text size (e.g. 12, 24, 48):", "Text Size", JOptionPane.QUESTION_MESSAGE);
            
            if (sizeStr != null) {
              try {
                int size = Integer.parseInt(sizeStr.trim());
                
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontSize(attrs, size);
                
                int start = txt.getSelectionStart();
                int end = txt.getSelectionEnd();
                txt.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
                } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(mb, "Please enter a valid number!");
              }
            }
          }
      });
      selectedMenu.add(textSizePickerItem);
      
      JMenuItem fontPickerItem = getMenuItem("Font Picker", -1, "");
      fontPickerItem.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            statusLabel.setText("");
            // 1. Get all available font family names from the graphics environment
            String[] fontNames = genv.getAvailableFontFamilyNames();
            
            // 2. Show a searchable selection dialog using JOptionPane
            String selectedFont = (String) JOptionPane.showInputDialog(
              null,
              "Select a font family:",
              "Font Picker",
              JOptionPane.PLAIN_MESSAGE,
              null,
              fontNames,
              txt.getFont().getFamily() // Default selection is current font family
            );
            
            // 3. If user cancels or closes the dialog, return immediately
            if (selectedFont == null) {
              return;
            }
            
            // 4. Get the current style and size from the text pane to preserve them
            int currentStyle = txt.getFont().getStyle();
            int currentSize = txt.getFont().getSize();
            
            // 5. Create a SimpleAttributeSet to carry the new font attribute
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attrs, selectedFont);
            StyleConstants.setBold(attrs, (currentStyle & Font.BOLD) != 0);
            StyleConstants.setItalic(attrs, (currentStyle & Font.ITALIC) != 0);
            StyleConstants.setFontSize(attrs, currentSize);
            
            // 6. Apply the attributes to the selected text
            // If no text is selected, it will apply to the text typed at the current caret position
            int start = txt.getSelectionStart();
            int end = txt.getSelectionEnd();
            int length = end - start;
            
            if (length > 0) {
              txt.getStyledDocument().setCharacterAttributes(start, length, attrs, false);
              } else {
              // Apply for future typing if no selection exists
              txt.setCharacterAttributes(attrs, false);
            }
          }
      });
      selectedMenu.add(fontPickerItem);
      
      selectedMenu.addSeparator();
      
      JMenuItem removeAttributesItem=getMenuItem ("Remove Atrs", -1, "");
      removeAttributesItem.addActionListener (new ActionListener ()
        {
          public void actionPerformed (ActionEvent evt)
          {
            statusLabel.setText("");
            int p0=txt.getSelectionStart ();
            int p1=txt.getSelectionEnd ();
            
            if (p0<0 || p1<0 || p0==p1)
            {
              statusLabel.setText ("No Selection!");
              return;
            }
            
            StyledDocument doc=txt.getStyledDocument ();
            
            doc.setCharacterAttributes (p0, p1-p0, emptset, true);
            
            return;
          }
      });
      selectedMenu.add (removeAttributesItem);
      
      mb.add (selectedMenu);
      ///////////////
      ////////////////SELECTED 2
      JMenu fselectedMenu = getMenu ("HTMLSelected");
      for (int i=0; i<lenff; i++)
      {
        JMenuItem ix = getMenuItem (ffamilies [i], -1, "");
        ix.addActionListener (new SASListener (familySAS [i]));
        fselectedMenu.add (ix);
      }
      
      fselectedMenu.addSeparator ();
      
      JMenuItem fsboldItem=getMenuItem ("Bold", -1, "");
      fsboldItem.addActionListener (new SASListener (boldset));
      fselectedMenu.add (fsboldItem);
      
      JMenuItem fsitalicItem=getMenuItem ("Italic", -1, "");
      fsitalicItem.addActionListener (new SASListener (itlcset));
      fselectedMenu.add (fsitalicItem);
      
      JMenuItem fsunderlineItem=getMenuItem ("Underline", -1, "");
      fsunderlineItem.addActionListener (new SASListener (undlset));
      fselectedMenu.add (fsunderlineItem);
      
      fselectedMenu.addSeparator ();
      
      int volisyc=-1;
      
      for (int i = 18; i <= 30; i += 2)
      {
        JMenuItem ix = getMenuItem (("Font Size: " +i+""), -1, "");
          ix.addActionListener (new SASListener (sizeSAS [++volisyc]));
          fselectedMenu.add (ix);
        }
        
        fselectedMenu.addSeparator ();
        
        for (int i = 0; i < colstrs.length; i++)
        {
          JMenuItem ix = getMenuItem (colstrs [i], -1, "");
          ix.addActionListener (new SASListener (colorSAS [i]));
          fselectedMenu.add (ix);
        }
        
        mb.add(fselectedMenu);
        ////////////////SELECTED 2 END
        
		////// START - PAINT MENU //////
		JMenu paintMenu = getMenu("Paint");
		
		JMenuItem txtInfoItem = getMenuItem("Text Pane Information", -1, "");
		txtInfoItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				statusLabel.setText("");

				// Convert Colors to Hex format
				String fg = String.format("#%06X", (txt.getForeground().getRGB() & 0xFFFFFF));
				String bg = String.format("#%06X", (txt.getBackground().getRGB() & 0xFFFFFF));
				String sel = String.format("#%06X", (txt.getSelectionColor().getRGB() & 0xFFFFFF));
				String caret = String.format("#%06X", (txt.getCaretColor().getRGB() & 0xFFFFFF));
				//String selText = String.format("#%06X", (txt.getSelectedTextColor().getRGB() & 0xFFFFFF));

				// Determine Font Style
				String style = "Plain";
				if (txt.getFont().isBold() && txt.getFont().isItalic()) style = "Bold Italic";
				else if (txt.getFont().isBold()) style = "Bold";
				else if (txt.getFont().isItalic()) style = "Italic";

				// Get Margin Information
				java.awt.Insets m = txt.getMargin();
				String marginInfo = "T:" + m.top + " L:" + m.left + " B:" + m.bottom + " R:" + m.right;

				// Create HTML with 2x larger fonts and specific colors
				// Key: Red (#FF0000), Value: Blue (#0000FF)
				String msg = "<html><body style='width: 450px; font-family: sans-serif; font-size: 20pt; padding: 15px;'>"
						+ "<h1 style='color: #2C3E50; border-bottom: 3px solid #3498DB; font-size: 24pt;'>Component Info</h1>"
						+ "<table border='0' cellpadding='8' style='font-size: 18pt;'>"
						+ "<tr><td style='color: red;'><b>Font Family:</b></td><td style='color: blue;'>" + txt.getFont().getFamily() + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Font Size:</b></td><td style='color: blue;'>" + txt.getFont().getSize() + " pt</td></tr>"
						+ "<tr><td style='color: red;'><b>Font Style:</b></td><td style='color: blue;'>" + style + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Foreground:</b></td><td style='color: blue;'>" + fg + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Background:</b></td><td style='color: blue;'>" + bg + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Selection Text:</b></td><td style='color: blue;'>" + sel + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Caret Color:</b></td><td style='color: blue;'>" + caret + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Margins:</b></td><td style='color: blue;'>" + marginInfo + "</td></tr>"
						+ "<tr><td colspan='2'><hr style='border: 0; border-top: 2px solid #CCC;'></td></tr>"
						+ "<tr><td style='color: red;'><b>Doc Length:</b></td><td style='color: blue;'>" + txt.getDocument().getLength() + "</td></tr>"
						+ "<tr><td style='color: red;'><b>Editable:</b></td><td style='color: blue;'>" + (txt.isEditable() ? "Yes" : "No") + "</td></tr>"
						+ "</table></body></html>";

				JOptionPane.showMessageDialog(null, msg, "Detailed Statistics", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		paintMenu.add(txtInfoItem);
        
		paintMenu.addSeparator();
		
		// 1. Toggle Gradient Background
		final JCheckBoxMenuItem itemEnableGradient = new JCheckBoxMenuItem("Enable Gradient Background");
		itemEnableGradient.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        itemEnableGradient.setForeground(Color.BLACK);
		itemEnableGradient.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Toggle the gradient state in MTextPane
				txt.setGradientEnabled(itemEnableGradient.isSelected());
			}
		});
		paintMenu.add(itemEnableGradient);

		// 2. Set Gradient Colors
		JMenuItem itemSetGradientColors = getMenuItem("Set Gradient Colors...", -1, "");
		itemSetGradientColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Pick start and end colors using JColorChooser
				Color c1 = JColorChooser.showDialog(mb, "Select Start Color", Color.WHITE);
				if (c1 != null) {
					Color c2 = JColorChooser.showDialog(mb, "Select End Color", Color.GRAY);
					if (c2 != null) {
						txt.setGradientColors(c1, c2);
					}
				}
			}
		});
		paintMenu.add(itemSetGradientColors);

		// 3. Set Gradient Coordinates (X1, Y1, X2, Y2)
		JMenuItem itemSetCoords = getMenuItem("Set Gradient Coordinates...", -1, "");
		itemSetCoords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Input dialog with blue text size 5
				String msg = "<html><font color='blue' size='5'>Enter coords (x1,y1,x2,y2):</font></html>";
				String input = JOptionPane.showInputDialog(mb, msg, "0,0,0,500");
				
				if (input != null && input.contains(",")) {
					try {
						String[] parts = input.split(",");
						int x1 = Integer.parseInt(parts[0].trim());
						int y1 = Integer.parseInt(parts[1].trim());
						int x2 = Integer.parseInt(parts[2].trim());
						int y2 = Integer.parseInt(parts[3].trim());
						txt.setGradientCoords(x1, y1, x2, y2);
					} catch (Exception ex) {
						String errMsg = "<html><font color='red' size='5'>Invalid format! Use numeric values.</font></html>";
						JOptionPane.showMessageDialog(mb, errMsg, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		paintMenu.add(itemSetCoords);

		paintMenu.addSeparator();

		// 4a. Set BG Transparency (Alpha Composite)
		JMenuItem itemSetAlpha = getMenuItem("Set BG Transparency Alpha", -1, "");
		itemSetAlpha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Input dialog with blue text size 5
				String msg = "<html><font color='blue' size='5'>Enter BG Alpha value (0.01 - 1.0):</font></html>";
				String input = JOptionPane.showInputDialog(mb, msg, "1.0f");
				
				if (input != null) {
					try {
						float alpha = Float.parseFloat(input);
						txt.setAlphaValue(alpha);
					} catch (Exception ex) {
						String errMsg = "<html><font color='red' size='5'>Invalid alpha value!</font></html>";
						JOptionPane.showMessageDialog(mb, errMsg, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		paintMenu.add(itemSetAlpha);
		
        // 4b. Set Text Transparency (Alpha Composite)
		JMenuItem itemTextSetAlpha = getMenuItem("Set Text Transparency Alpha", -1, "");
		itemTextSetAlpha.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Input dialog with blue text size 5
				String msg = "<html><font color='blue' size='5'>Enter Text Alpha value (0.01 - 1.0):</font></html>";
				String input = JOptionPane.showInputDialog(mb, msg, "1.0f");
				
				if (input != null) {
					try {
						float alpha = Float.parseFloat(input);
						txt.setTextAlphaValue(alpha);
					} catch (Exception ex) {
						String errMsg = "<html><font color='red' size='5'>Invalid alpha value!</font></html>";
						JOptionPane.showMessageDialog(mb, errMsg, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		paintMenu.add(itemTextSetAlpha);
		
		// 5. Background Image & Texture Mapping (Using existing iconjfc)
		JMenuItem itemSetBgImage = getMenuItem("Set Background Image Texture", -1, "");
		itemSetBgImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Open dialog using existing JFileChooser
				int result = iconjfc.showOpenDialog(mb);
				
				if (result == JFileChooser.APPROVE_OPTION) {
					try {
						File file = iconjfc.getSelectedFile();
						BufferedImage img = javax.imageio.ImageIO.read(file);
						
						// Prompt for dimensions with blue text size 5
						String msg = "<html><font color='blue' size='5'>Enter Texture Width,|x|*Height:</font></html>";
						String sizeInput = JOptionPane.showInputDialog(mb, msg, img.getWidth() + "*" + img.getHeight());
						sizeInput = sizeInput.replace("x", ",");
						sizeInput = sizeInput.replace("*", ",");
						
						int w = 0, h = 0;
						if (sizeInput != null && sizeInput.contains(",")) {
							String[] s = sizeInput.split(",");
							w = Integer.parseInt(s[0].trim());
							h = Integer.parseInt(s[1].trim());
						}
						
						txt.setBackgroundImage(img, w, h);
					} catch (Exception ex) {
						String errMsg = "<html><font color='red' size='5'>Error loading image!</font></html>";
						JOptionPane.showMessageDialog(mb, errMsg, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		paintMenu.add(itemSetBgImage);

		// 6. Reset All Paint Settings
		JMenuItem itemResetBg = getMenuItem("Reset Background", -1, "");
		itemResetBg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				txt.setGradientEnabled(false);
				txt.setBackgroundImage(null, 0, 0);
				txt.setAlphaValue(1.0f);
				itemEnableGradient.setSelected(false);
				
				String msg = "<html><font color='blue' size='5'>Background has been reset.</font></html>";
				JOptionPane.showMessageDialog(mb, msg, "Reset", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		paintMenu.add(itemResetBg);

		mb.add(paintMenu);
		///// END - PAINT MENU //////
        
        ///////////////
        JMenu countMenu=getMenu ("Count");
        
        JMenuItem countItem = getMenuItem ("Char Count", -1, "");
        
        countItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              String text=txt.getText ();
              if (text==null)
              {
                statusLabel.setText ("Null Text!");
                return;
              }
              
              int len=text.length ();
              String lens=Integer.toString (len);
              
              String text2=text.replaceAll ("\r", "");
              text2=text2.replaceAll ("\n", "");
              text2=text2.replaceAll (" ", "");
              
              int len2=text2.length ();
              String lens2=Integer.toString (len2);
              JOptionPane.showMessageDialog(mb, "<html><body><font color=\"red\" size=\"5\">Character Count; <p><p><p>With Empties:</font><font color=\"blue\" size=\"5\">"+lens+"</font><p><p><p><font color=\"red\" size=\"5\">Without Empties: </font><font color=\"blue\" size=\"5\">"+lens2+"</font></body></html>");
              
              return;
            }
        });
        countMenu.add (countItem);
        
        JMenuItem selcountItem = getMenuItem ("Selected Char Count", -1, "");
        selcountItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              String text=txt.getSelectedText ();
              if (text==null)
              {
                statusLabel.setText ("No Selection!");
                return;
              }
              
              int len=text.length ();
              String lens=Integer.toString (len);
              
              String text2=text.replaceAll ("\r", "");
              text2=text2.replaceAll ("\n", "");
              text2=text2.replaceAll (" ", "");
              
              int len2=text2.length ();
              String lens2=Integer.toString (len2);
              
              JOptionPane.showMessageDialog(mb, "<html><body><font color=\"red\" size=\"5\">Selected Character Count; <p><p><p>With Empties:</font><font color=\"blue\" size=\"5\">"+lens+"</font><p><p><p><font color=\"red\" size=\"5\">Without Empties: </font><font color=\"blue\" size=\"5\">"+lens2+"</font></body></html>");
              
              return;
            }
        });
        countMenu.add (selcountItem);
        
        countMenu.addSeparator();
     
        JMenuItem otherCountItem = getMenuItem ("Other Text Information", -1, "");
        otherCountItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              otherInformation(false);
            }
        });
        countMenu.add (otherCountItem);
        
        JMenuItem otherSelectedCountItem = getMenuItem ("Other Selected Text Information", -1, "");
        otherSelectedCountItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              otherInformation(true);
            }
        });
        countMenu.add (otherSelectedCountItem);
        
        countMenu.addSeparator();
        
        JMenuItem cwInfoItem = getMenuItem ("Specific Char|Word Count", -1, "");
        cwInfoItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              cwInformation(false);
            }
        });
        countMenu.add (cwInfoItem);
        
        JMenuItem cwSelInfoItem = getMenuItem ("Selected Specific Char|Word Count", -1, "");
        cwSelInfoItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              cwInformation(true);
            }
        });
        countMenu.add (cwSelInfoItem);
        
        mb.add (countMenu);
        //////////////
        
        JMenu colorMenu=getMenu ("Color");
        mb.add (colorMenu);
        
        JMenuItem bgItem=getMenuItem ("BG", KeyEvent.VK_1, "CTRL-1");
        colorMenu.add (bgItem);
        bgItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Color c = JColorChooser.showDialog(mb, "BG Color", Color.WHITE);
              if (c != null)
              {
                txt.setBackground (c);
              }
            }
        });
        
        JMenuItem fgItem=getMenuItem ("FG", KeyEvent.VK_2, "CTRL-2");
        colorMenu.add (fgItem);
        fgItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Color c = JColorChooser.showDialog(mb, "FG Color", Color.BLACK);
              if (c != null)
              {
                txt.setForeground (c);
              }
            }
        });
        
        JMenuItem caretColorItem=getMenuItem ("Caret", KeyEvent.VK_3, "CTRL-3");
        colorMenu.add (caretColorItem);
        caretColorItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Color c = JColorChooser.showDialog(mb, "Caret Color", Color.RED);
              if (c != null)
              {
                txt.setCaretColor (c);
              }
            }
        });
        
        JMenuItem selectionColorItem=getMenuItem ("Selection", KeyEvent.VK_4, "CTRL-4");
        colorMenu.add (selectionColorItem);
        selectionColorItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Color c = JColorChooser.showDialog(mb, "Selection Color", Color.ORANGE);
              if (c != null)
              {
                txt.setSelectionColor (c);
              }
            }
        });
        
        JMenu fontMenu=getMenu ("Font");
        mb.add (fontMenu);
        
        JMenuItem familyItem=getMenuItem ("Family", KeyEvent.VK_Z, "CTRL-Z");
        fontMenu.add (familyItem);
        familyItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Font f = getFamiliedFont (txt);
              if (f != null)
              {
                txt.setFont (f);
              }
            }
        });
        
        JMenuItem styleItem=getMenuItem ("Style", -1, "");
        fontMenu.add (styleItem);
        styleItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Font f = getStyledFont (txt);
              if (f != null)
              {
                txt.setFont (f);
              }
            }
        });
        
        JMenuItem sizeItem=getMenuItem ("Size", KeyEvent.VK_W, "CTRL-W");
        fontMenu.add (sizeItem);
        sizeItem.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              Font f = getSizedFont (txt);
              if (f != null)
              {
                txt.setFont (f);
              }
            }
        });
        
        JMenu m2=getMenu ("Edit");
        
        JMenuItem gf=getMenuItem ("HTML~TXT", KeyEvent.VK_N, "CTRL-N");
        gf.addActionListener (new ConvertListener (true));
        m2.add (gf);
        
        m2.addSeparator ();
        
        JMenuItem gfp=getMenuItem ("HTML~TXT~Plain", -1, "");
        gfp.addActionListener (new ConvertListener (false));
        m2.add (gfp);
        
        m2.addSeparator ();
        
        JMenuItem resetText=getMenuItem ("Reset Text", KeyEvent.VK_E, "CTRL-E");
        resetText.addActionListener (new ActionListener ()
          {
            public void actionPerformed (ActionEvent evt)
            {
              statusLabel.setText("");
              txt.setText ("");
              txt.setText (CANKURTARAN);
              txt.setCaretPosition (0);
              statusLabel.setText ("Reset Text Operation is Okey!");
              
              myfocus();
            }
        });
        m2.add(resetText);
        
        mb.add (m2);
        
        JMenu mhelp=getMenu("Help");
        
        JMenuItem helpItem=getMenuItem ("Help Regex", -1, "");
        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              statusLabel.setText("");
              helpTextArea.setContentType("text/plain");
              helpTextArea.setText(Help.helpString + Help.rtfHint + Help.foreignCharsGuide);
              helpTextArea.setCaretPosition(0);
              
              JScrollPane scrollPane = new JScrollPane(helpTextArea);
              //scrollPane.setPreferredSize(new java.awt.Dimension(800, 600));
              
              JOptionPane.showMessageDialog(mb, scrollPane, "Regex Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        mhelp.add(helpItem);
        
        mb.add(mhelp);
        
        // textPane'i sarmalayan scroll pane'e numaralari ekle
        JScrollPane scrollPane = new JScrollPane(txt);
        scrollPane.setRowHeaderView(new LineNumberView(txt));
        
        add (mb, "North");
        add (scrollPane, "Center");
        
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setPreferredSize(new Dimension(1070, 40));
        labelPanel.add(statusLabel, "Center");
        add (labelPanel, "South");
      }//end of constructor
      //##############
      
      private final void myfocus() {
		int pos=txt.getCaretPosition ();
                    
        if (pos >= 0x0000 && pos<(txt.getText().length())) {
            txt.setCaretPosition (pos);
        }
        else {
            txt.setCaretPosition (0x0000);
        }
		txt.revalidate();
		txt.repaint();
		txt.requestFocus();
		
		return;  
	  }
	  
      private String replaceEscapeSequences(String input) {
          if (input == null) return null;
          return input.replace("\\t", "\t")
                .replace("\\n", "\n")
                .replace("\\r", "\r");
      }
   
      public void processXOR(boolean isSelected) {
        String content = isSelected ? txt.getSelectedText() : txt.getText();
        if (content == null || content.isEmpty()) return;
        
        // Ask user for a secret key
        String key = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter Secret Key for XOR:</font></body></html>");
        if (key == null || key.isEmpty()) return;
        
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
          // XOR bitwise operation
          output.append((char) (content.charAt(i) ^ key.charAt(i % key.length())));
        }
        
        // Replace the text in JTextPane
        if (isSelected) {
			txt.replaceSelection(output.toString());
		} else {
			txt.setText(output.toString());
        }
        myfocus();
      }
      
      /**
       * Helper method to calculate and display the hash
       * Compatible with Java 6+ (no external dependencies)
       */
      private void processHash(String algorithm) {
        String selectedText = txt.getSelectedText();
        
        if (selectedText == null || selectedText.isEmpty()) {
          JOptionPane.showMessageDialog(mb, "Please select some text first!", "No Selection", JOptionPane.WARNING_MESSAGE);
          return;
        }
        
        try {
          MessageDigest md = MessageDigest.getInstance(algorithm);
          byte[] hashBytes = md.digest(selectedText.getBytes("UTF-8"));
          
          // Manuel hex - for all java versions
          String hexResult = bytesToHex(hashBytes).toLowerCase();
          
          JTextField resultField = new JTextField(hexResult);
          resultField.setEditable(false);
          JOptionPane.showMessageDialog(mb, resultField, algorithm + " Result", JOptionPane.INFORMATION_MESSAGE);
          
          } catch (Exception ex) {
          JOptionPane.showMessageDialog(mb, "Error: " + ex.getMessage(), "Hash Error", JOptionPane.ERROR_MESSAGE);
        }
      }
      
      // Helper method
      private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
          sb.append(String.format("%02x", b));
        }
        return sb.toString();
      }
      
      public void removeLinesDuplicate(final String text) {
        String[] lines = text.split("\n", -1);
        // -1: Sondaki bos satirlari da yakalamak icin
        
        // Daha once eklenen dolu satirlari takip etmek icin bir Set
        java.util.Set<String> seenLines = new java.util.HashSet<String>();
        StringBuilder sb = new StringBuilder();
        
        for (String line : lines) {
          String trimmedLine = line.trim();
          
          if (trimmedLine.isEmpty()) {
            // Satir bossa (veya sadece bosluksa) dogrudan ekle
            sb.append(line).append("\n");
            } else {
            // Satir doluysa, daha once eklenip eklenmedigine bak
            if (!seenLines.contains(line)) {
              sb.append(line).append("\n");
              seenLines.add(line);
            }
          }
        }
        
        // Sonucu set et
        txt.setText(sb.toString().trim());
        txt.setCaretPosition(0);
        
        statusLabel.setText("Removed duplicate lines except empty lines.");
      }
      
      public void processWordCloud(boolean isSelected) {
        String content = isSelected ? txt.getSelectedText() : txt.getText();
        if (content == null || content.isEmpty()) return;
        
        // English comment: Split by non-word characters and count frequencies
        //String[] words = content.toLowerCase().split("\\W+");
        // Sadece harf olmayan (alfabetik olmayan) karakterlere göre böler
        String[] words = content.toLowerCase().split("[^\\p{L}\\p{N}]+");
        
        java.util.Map<String, Integer> freq = new java.util.HashMap<>();
        
        for (String w : words) {
          if (w.length() > 3) { // Skip short words like 'and', 'the', 'bir'
            freq.put(w, freq.getOrDefault(w, 0) + 1);
          }
        }
        
        // Sort by frequency and get Top 5
        String result = "<html><b>Top Keywords:</b><br>";
        // (Sort logic omitted for brevity, simple list display below)
        for (String key : freq.keySet()) {
          result += "- " + key + " (" + freq.get(key) + " times)<br>";
        }
        
        JOptionPane.showMessageDialog(mb, result + "</html>");
      }
      
      public void convertAllHexToUnicode() {
        final String text = txt.getSelectedText();
        if (text == null) {
          JOptionPane.showMessageDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Select at least 4 character like 0xNN format.</font></body></html>");
          return;
        }
        
        if (text.length() < 4) {
          statusLabel.setText("Insufficient text length...");
          return;
        }
        
        // Regex "0x" ardindan gelen en az 2,
        // en fazla 6 hex hanesini bulacak sekilde olacak.
        // [0-9a-fA-F]{2,6} -> Captures all from 0x21 to 0x10FFFF
        Pattern pattern = Pattern.compile("0x([0-9a-fA-F]{2,6})");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
          try {
            int codePoint = Integer.parseInt(matcher.group(1), 16);
            
            // Unicode max kontrol (Maksimum Unicode degeri = 0x10FFFF)
            if (Character.isValidCodePoint(codePoint)) {
              String unicodeChar = new String(Character.toChars(codePoint));
              matcher.appendReplacement(sb, Matcher.quoteReplacement(unicodeChar));
              } else {
              // Gecersiz bir kod noktasiysa oldugu gibi birak
              matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
            }
            } catch (Exception e) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(0)));
          }
        }
        
        matcher.appendTail(sb);
        
        String metin = sb.toString();
        txt.replaceSelection(metin);
        myfocus();
        
        return;
      }
      
      private final void prettyFormatXML() {
        String input = txt.getText();
        if (input == null) return;
        if (input.length() < 3) {
          statusLabel.setText("Insufficient text for this operation...");
          return;
        }
        
        try {
          Source xmlInput = new StreamSource(new StringReader(input));
          StringWriter stringWriter = new StringWriter();
          
          TransformerFactory transformerFactory = TransformerFactory.newInstance();
          try {
            transformerFactory.setAttribute("indent-number", 4);
            } catch (IllegalArgumentException e) {
            statusLabel.setText("Indent attribute not supported: " + e.getMessage());
          }
          
          Transformer transformer = transformerFactory.newTransformer();
          transformer.setOutputProperty(OutputKeys.INDENT, "yes");
          transformer.setOutputProperty("{http://apache.org}indent-amount", "4");
          // Old Java/Xalan compability
          transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
          
          transformer.transform(xmlInput, new StreamResult(stringWriter));
          String metin = stringWriter.toString();
          
          txt.setText(metin);
          txt.setCaretPosition(0);
          } catch (Exception e) {
          statusLabel.setText("Error: " + e.getMessage());
          return;
        }
      }
      
      public void openRTF() {
        // 1. Show dialog in the current thread (EDT)
        int rep = rtfjfc.showOpenDialog(mb);
        if (rep != JFileChooser.APPROVE_OPTION) {
          statusLabel.setText("Cancelled Opening File!");
          return;
        }
        
        final File file = rtfjfc.getSelectedFile();
        if (file == null || !file.exists()) {
          return;
        }
        
        // Extension check
        if (!file.getName().toLowerCase().endsWith(".rtf")) {
          JOptionPane.showMessageDialog(mb, "Not a supported RTF file!");
          return;
        }
        
        // 2. Load the file in a background thread to prevent UI freezing
        Thread t = new Thread() {
          public void run() {
            try {
              final RTFEditorKit rtfKit = new RTFEditorKit();
              final FileInputStream fis = new FileInputStream(file);
              
              // Use invokeLater to update the JTextPane safely
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    try {
                      // CRUCIAL: Clear existing content before reading new RTF
                      // Otherwise, the new file will be merged with the old text
                      txt.setText("");
                      
                      txt.setEditorKit(rtfKit);
                      rtfKit.read(fis, txt.getDocument(), 0);
                      
                      fis.close();
                      txt.setCaretPosition(0);
                      statusLabel.setText("RTF Loaded: " + file.getName());
                      } catch (Exception e) {
                      e.printStackTrace();
                    }
                  }
              });
              } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        };
        t.start();
      }
      
      public void saveRTF() {
        int rep = rtfjfc.showSaveDialog(mb);
        if (rep != JFileChooser.APPROVE_OPTION)
        {
          statusLabel.setText ("Cancelled Saving File!");
          return;
        }
        
        File file = rtfjfc.getSelectedFile ();
        
        if (file == null)
        {
          statusLabel.setText ("Null or Non-Existent File For Saving!");
          return;
        }
        
        if (file.exists ())
        {
          String m=JOptionPane.showInputDialog(mb,
          "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
          
          if (m==null)
          {
            statusLabel.setText ("Cancelled Overwrite RTF Saving Operation!");
            return;
          }
          
          if (m.equals (""))
          {
            statusLabel.setText ("Cancelled Overwrite RTF Saving Operation!");
            return;
          }
          
          if (m.equalsIgnoreCase ("y")==false && m.equalsIgnoreCase ("e")==false)
          {
            statusLabel.setText ("Cancelled Overwrite RTF Saving Operation! Enter E/e/Y/y For Overwriting on the Program!");
            return;
          }
        }
        
        String name = file.getName ().toLowerCase ();
        if (name.endsWith (".rtf") == false)
        {
          file = new File (file.getAbsolutePath ()+".rtf");
        }
        
        try {
          RTFEditorKit rtfKit = new RTFEditorKit();
          FileOutputStream fos = new FileOutputStream(file);
          
          // This writes the styled content of your JTextPane to the file in RTF format
          rtfKit.write(fos, txt.getDocument(), 0, txt.getDocument().getLength());
          
          fos.close();
          JOptionPane.showMessageDialog(mb, "Saved successfully as RTF!");
          } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(mb, "Error saving RTF: " + ex.getMessage());
        }
      }
      
      public void openHTML() {
        // Show open dialog
        int rep = htmljfc.showOpenDialog(mb);
        if (rep != JFileChooser.APPROVE_OPTION) {
          statusLabel.setText("Cancelled Opening HTML File!");
          return;
        }
        
        File file = htmljfc.getSelectedFile();
        
        // Check if file exists
        if (file == null || !file.exists()) {
          statusLabel.setText("File is Null or Does not Exist!");
          return;
        }
        
        // Validate file extension
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".html") && !name.endsWith(".htm")) {
          JOptionPane.showMessageDialog(helpTextArea, "<html><body><font color=\"red\" size=\"5\">" + file.getName() + "</font><font color=\"blue\" size=\"5\">" + " is not a supported HTML File!</font></body></html>");
          return;
        }
        
        try {
          // MIME tipini ayarla
          helpTextArea.setContentType("text/html");
          
          // JEditorPane'in dogrudan URL ile dosya okuma ozelligi (Java 6+)
          helpTextArea.setPage(file.toURI().toURL());
          
          // Imleci en basa cek
          helpTextArea.setCaretPosition(0);
          
          // Gorunum icin scroll pane
          JScrollPane scrollPane = new JScrollPane(helpTextArea);
          //scrollPane.setPreferredSize(new java.awt.Dimension(1000, 700));
          
          // Dialog icinde goster
          JOptionPane.showMessageDialog(mb, scrollPane, "HTML Viewer", JOptionPane.INFORMATION_MESSAGE);
          
          } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(mb, "Error opening HTML: " + ex.getMessage());
        }
      }
      
      public void saveHTML() {
        // Show save dialog
        int rep = htmljfc.showSaveDialog(mb);
        if (rep != JFileChooser.APPROVE_OPTION) {
          statusLabel.setText("Cancelled Saving HTML File!");
          return;
        }
        
        File file = htmljfc.getSelectedFile();
        
        if (file == null) {
          statusLabel.setText("Null File For Saving!");
          return;
        }
        
        // Overwrite check
        if (file.exists()) {
          String m = JOptionPane.showInputDialog(mb,
          "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
          
          if (m == null || m.isEmpty()) {
            statusLabel.setText("Cancelled Overwrite HTML Saving Operation!");
            return;
          }
          
          if (!m.equalsIgnoreCase("y") && !m.equalsIgnoreCase("e")) {
            statusLabel.setText("Cancelled Overwrite HTML Saving Operation!");
            return;
          }
        }
        
        // Ensure correct extension
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".html") && !name.endsWith(".htm")) {
          file = new File(file.getAbsolutePath() + ".html");
        }
        
        try {
          HTMLEditorKit htmlKit = new HTMLEditorKit();
          FileOutputStream fos = new FileOutputStream(file);
          
          //txt.getDocument().putProperty("IgnoreCharsetDirective", Boolean.TRUE);
          
          // This converts JTextPane styles (including background) into HTML tags/CSS
          htmlKit.write(fos, txt.getDocument(), 0, txt.getDocument().getLength());
          
          fos.close();
          JOptionPane.showMessageDialog(mb, "Saved successfully as HTML!");
          } catch (Exception ex) {
          ex.printStackTrace();
          JOptionPane.showMessageDialog(mb, "Error saving HTML: " + ex.getMessage());
        }
      }
      
      private final void cwInformation(boolean selected) {
        // Determine the source text based on selection
        String text = selected ? txt.getSelectedText() : txt.getText();
        
        // Check if the source text is empty
        if (text == null || text.length() < 1) {
          JOptionPane.showMessageDialog(mb, "No text!");
          return;
        }
        
        // Get the character or word to search for from the user
        String cw = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"4\">Enter a character or word:</font></body></html>");
        
        // User clicked cancel
        if (cw == null) {
          return;
        }
        
        // Check if input is empty
        if (cw.length() < 1) {
          JOptionPane.showMessageDialog(mb, "Insufficient char/word entered.");
          return;
        }
        
        int cwTimes = 0;
        int index = 0;
        
        // Search for occurrences of 'cw' within 'text'
        // This loop continues as long as an occurrence is found
        while ((index = text.indexOf(cw, index)) != -1) {
          cwTimes++;
          // Move the index forward to find the next occurrence
          index += cw.length();
        }
        
        // Prepare the result message
        String mesaj = "<html><body><font color=\"#0000DD\" size=\"4\">" + cw + " occurs " + cwTimes + " times in this text.</font></body></html>";
        JOptionPane.showMessageDialog(mb, mesaj);
      }
      
      private final void otherInformation(boolean selected) {
        String text = selected ? txt.getSelectedText() : txt.getText();
        
        if (text == null || text.length() < 1) {
          JOptionPane.showMessageDialog(mb, "No text!");
          return;
        }
        
        final int tlen = text.length();
        int bosluk = 0, buyuk = 0, kucuk = 0;
        int rakam = 0, ozel = 0, lineCount = 0, tabCount = 0;
        
        for (int i = 0; i < tlen; i++) {
          char c = text.charAt(i);
          
          // Check for specific whitespaces FIRST
          if (c == '\n') lineCount++;
          else if (c == '\t') tabCount++;
          
          // General character analysis
          if (Character.isWhitespace(c)) bosluk++;
          else if (Character.isUpperCase(c)) buyuk++;
          else if (Character.isLowerCase(c)) kucuk++;
          else if (Character.isDigit(c)) rakam++;
          else ozel++;
        }
        
        String[] words = text.split("\\s+");
        if (words == null) {
          words = new String[0];
        }
        
        // Larger font and padding for better visibility
        String mesaj = "<html>" +
        "<body style='padding: 10px;'>" +
        "  <font color='#EE2222' size='6'><b>Statistics</b></font><br><br>" +
        "  <table border='0' cellpadding='3'>" +
        "    <tr><td><font size='5'><b>Total:</b></font></td><td><font size='5' color='blue'>" + tlen + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Words:</b></font></td><td><font size='5' color='blue'>" + words.length + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Lines:</b></font></td><td><font size='5' color='blue'>" + lineCount + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Tabs:</b></font></td><td><font size='5' color='blue'>" + tabCount + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Spaces:</b></font></td><td><font size='5' color='blue'>" + bosluk + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Uppercase:</b></font></td><td><font size='5' color='blue'>" + buyuk + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Lowercase:</b></font></td><td><font size='5' color='blue'>" + kucuk + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Digits:</b></font></td><td><font size='5' color='blue'>" + rakam + "</font></td></tr>" +
        "    <tr><td><font size='5'><b>Special:</b></font></td><td><font size='5' color='blue'>" + ozel + "</font></td></tr>" +
        "  </table>" +
        "</body>" +
        "</html>";
        
        JOptionPane.showMessageDialog(mb, mesaj);
      }
      
      private void sortSelectedLines(JTextPane txt) {
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
          
          Collator trCollator = Collator.getInstance(Locale.forLanguageTag("tr-TR"));
          Collections.sort(lines, (s1, s2) -> {
              return trCollator.compare(s1, s2);
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
      
      ///////////////
      public static String convertToAscii(String text) {
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
      
      ///////////////
      private final boolean printObj() {
        // 1. Check if there are any print services available on the system
        javax.print.PrintService[] services = java.awt.print.PrinterJob.lookupPrintServices();
        
        if (services == null || services.length == 0) {
          String errdesc = "<html><body><font color=\"red\" size=\"4\">Error: No print service found!<br><br>" +
          "The CUPS service might be stopped or no printers are installed.<br><br>" +
          "You can start/stop the print server using:<br><b>sudo systemctl start|stop cups</b></font></body></html>";
          JOptionPane.showMessageDialog(mb, errdesc, "Printer Error", JOptionPane.ERROR_MESSAGE);
          return false;
        }
        
        try {
          boolean complete = txt.print(null, null, true, null, null, true);
          if (complete) {
            JOptionPane.showMessageDialog(mb, "Document sent to printer successfully.");
            } else {
            statusLabel.setText("Cancelled Print Operation!");
          }
          } catch (Exception ex) {
          JOptionPane.showMessageDialog(mb, "Printing Error: " + ex.getMessage());
          return false;
        }
        
        return true;
      }
      //////////////
      
      private String getFullPassword() {
        String last4 = JOptionPane.showInputDialog(mb,
          "<html><body><font size='6'>Enter last 4 characters(5EmrE4325EmrXXXX):</font></body></html>",
        "Password", JOptionPane.QUESTION_MESSAGE);
        
        if (last4 == null) return null;
        
        last4 = last4.trim();
        if (last4.length() == 4) {
          return SALT + last4;
          } else {
          JOptionPane.showMessageDialog(mb,
          "<html><body><font color='red' size='5'><b>Exactly 4 characters!</b></font></body></html>");
          return null;
        }
        
      }
      
      private SecretKey getSecretKey(String fullPassword) {
        byte[] keyBytes = fullPassword.getBytes(StandardCharsets.UTF_8);
        // Ensure 16 bytes for AES-128
        byte[] finalKey = new byte[16];
        System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, 16));
        return new SecretKeySpec(finalKey, "AES");
      }
      
      private byte[] encrypt(byte[] data, SecretKey key) throws Exception {
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
      
      private byte[] decrypt(byte[] encryptedData, SecretKey key) throws Exception {
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
      ///////////
      
      //////////
      
      private String processBlocksSeparately(String text) {
        String[] lines = text.split("\\n");
        StringBuilder result = new StringBuilder();
        StringBuilder currentTextBuffer = new StringBuilder();
        StringBuilder currentFootnoteBuffer = new StringBuilder();
        boolean insideFootnoteBlock = false;
        
        for (String line : lines) {
          String trimmed = line.trim();
          
          if (trimmed.startsWith(START_FN + ".")) {
            // Start of footnote block
            insideFootnoteBlock = true;
            currentFootnoteBuffer = new StringBuilder();
            currentFootnoteBuffer.append(line).append("\n");
          }
          else if (trimmed.startsWith(END_FN + ".")) {
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
      
      private Map<String, String> parseFootnotesFromBlock(String footnoteBlock) {
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
      
      private String applyFootnotesToText(String text, Map<String, String> footnoteMap) {
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
      
      //////////
      
      private static String escapeSequences(String input) {
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
      
      /**
       * Converts escape sequences like \n, \t, \r to actual characters.
       * Preserves \b as word boundary (does NOT convert to backspace).
       * For use in regex patterns.
       *
       * @param input The string containing escape sequences
       * @return The string with escape sequences converted
       */
      private String escapeSequencesForRegex(String input)
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
      
      private final void setMezayaConstants ()
      {
        StyleConstants.setFontFamily (ariaset, "Arial");
        StyleConstants.setFontFamily (timeset, "Times New Roman");
        StyleConstants.setFontFamily (scheset, "Scheherazade");
        StyleConstants.setFontFamily (verdset, "Verdana");
        StyleConstants.setFontFamily (comcset, "Comic Sans MS");
        StyleConstants.setFontFamily (seleset, "Select!");
        //ariaset, timeset, scheset, verdset, comcset, seleset
        
        StyleConstants.setBold (boldset, true);
        StyleConstants.setItalic (itlcset, true);
        StyleConstants.setUnderline (undlset, true);
        
        StyleConstants.setFontSize (sz18set, 18);
        StyleConstants.setFontSize (sz20set, 20);
        StyleConstants.setFontSize (sz22set, 22);
        StyleConstants.setFontSize (sz24set, 24);
        StyleConstants.setFontSize (sz26set, 26);
        StyleConstants.setFontSize (sz28set, 28);
        StyleConstants.setFontSize (sz30set, 30);
        
        StyleConstants.setForeground (blckset, Color.BLACK);
        StyleConstants.setForeground (reddset, Color.RED);
        StyleConstants.setForeground (grenset, Color.GREEN);
        StyleConstants.setForeground (blueset, Color.BLUE);
        StyleConstants.setForeground (orngset, Color.ORANGE);
        
        return;
      }
 
	   ////////////     
	   /** Undoable edit listener implementation */
	   private final class OwnUndoListener implements UndoableEditListener {
			public void undoableEditHappened(final UndoableEditEvent e) {
				// More clear by invokeLater
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						umngr.addEdit(e.getEdit());
						updateActions();
					}
				});
			}
		}

		/** Update from one location. This is more safe. */
		protected void updateActions() {
			if (uaction != null) uaction.again();
			if (raction != null) raction.again();
		}

		/** Reset undo process */
		protected void resetUndo() {
			umngr.discardAllEdits();
			updateActions();
		}

		/** Undo action */
		private final class UndoAction extends AbstractAction {
			private UndoAction() {
				super("Undo");
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent evt) {
				try {
					if (umngr.canUndo()) {
						umngr.undo();
					}
				} catch (Exception uex) {
					System.err.println("Undo Error: " + uex.getMessage());
				}
				updateActions();
			}

			protected void again() {
				boolean canu = umngr.canUndo();
				setEnabled(canu);
				putValue(Action.NAME, canu ? umngr.getUndoPresentationName() : "Undo");
			}
		}

		/** Redo action */
		private final class RedoAction extends AbstractAction {
			private RedoAction() {
				super("Redo");
				setEnabled(false);
			}

			public void actionPerformed(ActionEvent evt) {
				try {
					if (umngr.canRedo()) {
						umngr.redo();
					}
				} catch (Exception uex) {
					System.err.println("Redo Error: " + uex.getMessage());
				}
				updateActions();
			}

			protected void again() {
				boolean canu = umngr.canRedo();
				setEnabled(canu);
				putValue(Action.NAME, canu ? umngr.getRedoPresentationName() : "Redo");
			}
	  }
	  ///////////////

      ///////////////////////////
      private final class FCodeURLListener
      implements ActionListener
      {
        
        private FCodeURLListener ()
        {}
        
		public final void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			try {
				// 1. Get the selected portion of the text
				String s = txt.getSelectedText();

				// 2. Stylish alert if no selection is made
				if (s == null || s.isEmpty()) {
					statusLabel.setText("No selection found.");
					JOptionPane.showMessageDialog(mb,
						"<html><body><font color=\"red\" size=\"5\">Please select the text you want to decode!</font></body></html>",
						"Selection Required",
						JOptionPane.WARNING_MESSAGE);
					return;
				}

				// 3. Process the selected text (Symmetry limit removed as requested)
				// 100k+ characters are handled easily by modern JVMs
				String mm = fdecode(s);

				// 4. Efficiently replace only the highlighted part with the decoded result
				if (mm != null) {
					txt.replaceSelection(mm);
					myfocus();
					statusLabel.setText("Decoding completed successfully.");
				}
				
			} catch (Exception e) {
				// Standard error logging
				e.printStackTrace();
				statusLabel.setText("Error: Decoding failed.");
			}
		}
        
		private final String fdecode(String s) throws Exception {
			if (s == null || s.isEmpty()) return s;

			int len = s.length();
			// Use ByteArrayOutputStream to collect bytes and convert them back to UTF-8
			ByteArrayOutputStream baos = new ByteArrayOutputStream(len);

			for (int i = 0; i < len; i++) {
				char c = s.charAt(i);

				// Check for percent encoding pattern: %HH
				if (c == '%' && i + 2 < len) {
					char h1 = s.charAt(i + 1);
					char h2 = s.charAt(i + 2);

					// Convert hex characters to integer values
					int v1 = Character.digit(h1, 16);
					int v2 = Character.digit(h2, 16);

					// If both characters are valid hex digits (0-9, A-F)
					if (v1 != -1 && v2 != -1) {
						int b = (v1 << 4) | v2; // Bitwise operation is faster than Integer.parseInt
						baos.write(b);
						i += 2; // Move index forward by 2
					} else {
						// Not a valid hex sequence, treat '%' as a normal character
						baos.write(c);
					}
				} else {
					// Keep the character as is (maps directly to byte for ASCII range)
					baos.write(c);
				}
			}

			// Convert the collected bytes back to String using UTF-8 encoding
			String result = baos.toString("UTF-8");
			baos.close();
			return result;
		}
        
      }
      //////////////////////
      
      ///////////////////////////
      private final class CodeURLListener
      implements ActionListener
      {
        
        private CodeURLListener ()
        {}
        
		public final void actionPerformed(ActionEvent evt) {
			statusLabel.setText("");
			try {
				// 1. Fetch only the selected text from the pane
				String s = txt.getSelectedText();

				// 2. Alert the user if no text is selected
				if (s == null || s.isEmpty()) {
					statusLabel.setText("No selection found.");
					JOptionPane.showMessageDialog(mb,
						"<html><body><font color=\"red\" size=\"5\">Please select some text to encode!</font></body></html>",
						"Selection Required",
						JOptionPane.WARNING_MESSAGE);
					return;
				}

				// 3. Process the selected text using the original code(s) method
				// 1024 length limit removed to support large selections (100k+ chars)
				String mm = code(s);

				// 4. Update only the selected portion with the encoded result
				if (mm != null) {
					// Using replaceSelection instead of wiping the entire pane
					txt.replaceSelection(mm);
					myfocus();
					statusLabel.setText("The selected text has been encoded successfully.");
				}
				
			} catch (Exception e) {
				// Standard error logging for Java 6/7
				e.printStackTrace();
				statusLabel.setText("Encoding failed due to an error.");
			}
		}
        
		private final String code(String ch) throws Exception {
			if (ch == null || ch.isEmpty()) return ch;

			// Use UTF-8 bytes to ensure correct encoding for non-ASCII characters
			byte[] bits = ch.getBytes("UTF-8");
			int blen = bits.length;
			
			// Initial capacity set to double the length to avoid resizing
			StringBuffer sb = new StringBuffer(blen * 2);
			
			for (int i = 0; i < blen; i++) {
				int b = bits[i] & 0xFF; // Unsigned byte conversion
				
				// If the character is within safe ASCII range (up to 'z')
				if (b < 123) {
					sb.append((char) b);
				} else {
					// Percent encoding for special characters
					sb.append("%");
					String hex = Integer.toHexString(b);
					// Ensure two-digit hex (e.g., %0a instead of %a)
					if (hex.length() < 2) {
						sb.append('0');
					}
					sb.append(hex);
				}
			}
			
			return sb.toString();
	     }
      }
      //////////////////////
      
      //////////////////////////////SASListener Start
      private final class SASListener
      extends Object
      implements ActionListener
      {
        
        private SimpleAttributeSet finset=null;
        
        private SASListener ()
        {}
        
        private SASListener (SimpleAttributeSet aaa)
        {
          this.finset=aaa;
        }
        
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          if (finset==null)
          {
            statusLabel.setText ("Null Attribute!");
            return;
          }
          
          fiil ();
          
          return;
        }
        
        private final void fiil ()
        {
          String mm=txt.getText ();
          
          if (mm==null)
          {
            statusLabel.setText ("Null Text For!");
            return;
          }
          
          final int len=mm.length ();
          
          if (len < 1)
          {
            statusLabel.setText ("Very Short Length Text!");
            return;
          }
          
          CANKURTARAN=mm;
          
          int p0=txt.getSelectionStart ();
          int p1=txt.getSelectionEnd ();
          
          if (p0<0 || p1<0 || p0==p1)
          {
            statusLabel.setText ("No Selection!");
            return;
          }
          
          StringBuilder metin=new StringBuilder (mm);
          
          String START_TAG=getStartTag ();//finset'e gore sec
          String END_TAG=getEndTag ();//finset'e gore sec
          
          metin=metin.insert (p1, END_TAG);
          metin=metin.insert (p0, START_TAG);
          
          String res=metin.toString ();
          
          int carpos=txt.getCaretPosition ();
          
          txt.setText (res);
          
          StyledDocument doc=txt.getStyledDocument ();
          
          int tlen=START_TAG.length ();
          int r=p0+tlen;//3 yerine START_TAG uzunlugunu al
          int lenvo=res.length ();
          
          if (r>=lenvo)
          {
            statusLabel.setText ("Invalid or Difficult Index!");
            return;
          }
          
          //doc.setCharacterAttributes (r, p1-p0, finset, true);//yazi voyutlarinda false mi ture mi olsun dikkat et
          
          int reslen=res.length ();
          if (carpos>=0 && carpos<reslen)
          {
            txt.setCaretPosition (carpos);
          }
          else
          {
            txt.setCaretPosition (0);
          }
          
          return;
        }
        
        private final String getStartTag ()
        {
          if (finset==null)
          {
            statusLabel.setText ("Null AttributeSet!");
            return "";
          }
          
          String res="<>";
          
          if (finset==ariaset)
          {
            res="<font face=\"arial\">";
          }
          else if (finset==timeset)
          {
            res="<font face=\"times\">";
          }
          else if (finset==scheset)
          {
            res="<font face=\"Scheherazade-Regular\">";
          }
          //ariaset, timeset, scheset, verdset, comcset, seleset
          else if (finset==verdset)
          {
            res="<font face=\"verdana\"";
          }
          else if (finset==comcset)
          {
            res="<font face=\"comic\">";
          }
          else if (finset==seleset)
          {
            String s=JOptionPane.showInputDialog(mb, "Enter Font Family Name without \".ttf\":");
            if (s != null)
            {
              if (s.length () >= 1)
              {
                res="<font face="+"\""+s+"\""+">";
              }
            }
          }
          else if (finset==boldset)
          {
            res="<b>";
          }
          else if (finset==itlcset)
          {
            res="<i>";
          }
          else if (finset==undlset)
          {
            res="<u>";
          }
          else if (finset==sz18set)
          {
            res="<font size=\"2\">";
          }
          else if (finset==sz20set)
          {
            res="<font size=\"3\">";
          }
          else if (finset==sz22set)
          {
            res="<font size=\"3\">";
          }
          else if (finset==sz24set)
          {
            res="<font size=\"4\">";
          }
          else if (finset==sz26set)
          {
            res="<font size=\"4\">";
          }
          else if (finset==sz28set)
          {
            res="<font size=\"5\">";
          }
          else if (finset==sz30set)
          {
            res="<font size=\"7\">";
          }
          else if (finset==blckset)
          {
            res="<font color=\"#000000\">";
          }
          else if (finset==reddset)
          {
            res="<font color=\"#C81414\">";
          }
          else if (finset==grenset)
          {
            res="<font color=\"#006C00\">";
          }
          else if (finset==blueset)
          {
            res="<font color=\"#0000BB\">";
          }
          else if (finset==orngset)
          {
            res="<font color=\"#EB7A11\">";
          }
          else
          {
            res="";
          }
          
          return res;
        }
        
        private final String getEndTag ()
        {
          if (finset==null)
          {
            statusLabel.setText ("Null AttributeSet!");
            return "";
          }
          
          String res="</>";
          
          if (finset==boldset)
          {
            res="</b>";
          }
          else if (finset==itlcset)
          {
            res="</i>";
          }
          else if (finset==undlset)
          {
            res="</u>";
          }
          else
          {
            res="</font>";
          }
          
          return res;
        }
        
        public String toString ()
        {
          return "SAS Action Listener";
        }
        
      }
      /////////////////////SASListener End
      
      private JMenu getMenu(String str) {
        final JMenu myMenu = new JMenu(str);
        
        // Menu Cubuğu (MenuBar) ile uyumlu renkler
        final Color normalFg = new Color(20, 20, 20);
        final Color hoverFg = new Color(20, 20, 220); // Secilince
        final Font menuFont = new Font("Segoe UI", Font.PLAIN, 18);
        
        myMenu.setFont(menuFont);
        myMenu.setOpaque(true);
        myMenu.setForeground(normalFg);
        myMenu.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15)); // Genis ve ferah
        
        myMenu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
              myMenu.setForeground(hoverFg);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
              myMenu.setForeground(normalFg);
            }
        });
        
        return myMenu;
      }
      
      private final JMenuItem getMenuItem(String str, int KV, String syz) {
        JMenuItem item = new MyMenuItem(str, KV, syz);
        return item;
      }
      
      private final Font getFamiliedFont (JTextPane c)
      {
        Font f = c.getFont ();
        int stil = f.getStyle ();
        int size = f.getSize ();
        Object obj = null;
        try
        {
          obj = JOptionPane.showInputDialog
          (
            c,
            "<html><body><font color=\"blue\" size=\"5\">Select text font family</font></body></html>",
            "Font Families",
            JOptionPane.QUESTION_MESSAGE,
            null,
            fontNames,
            fontNames [0]
          );
        }
        catch (Exception ex)
        {
          obj = null;
        }
        
        if (obj == null)
        {
          return f;
        }
        
        f = new Font (obj.toString (), stil, size);
        return f;
      }
      
      private final Font getSizedFont (JTextPane c)
      {
        Font f = c.getFont ();
        String name = f.getFamily ();
        int stil = f.getStyle ();
        int size = f.getSize ();
        String str = null;
        try
        {
          str = JOptionPane.showInputDialog (c, "<html><body><font color=\"blue\" size=\"5\"> A font size value</font></body></html>");
        }
        catch (Exception ex)
        {
          return f;
        }
        
        if (str == null)
        {
          return f;
        }
        
        try
        {
          size = Integer.parseInt (str);
        }
        catch (Exception ex)
        {
          return f;
        }
        
        f = new Font (name, stil, size);
        return f;
      }
      
      private final  Font getStyledFont (JTextPane c)
      {
        Font f = c.getFont ();
        String name = f.getFamily ();
        int size = f.getSize ();
        Object obj = null;
        try
        {
          obj = JOptionPane.showInputDialog
          (
            c,
            "<html><body><font color=\"blue\" size=\"5\">Select text font style</font></body></html>",
            "Font Styles",
            JOptionPane.QUESTION_MESSAGE,
            null,
            styles,
            styles [0]
          );
        }
        catch (Exception ex)
        {
          obj = null;
        }
        
        if (obj == null)
        {
          return f;
        }
        
        String str = obj.toString ();
        if (str.indexOf ("+") != -1)
        {
          f = new Font (name, Font.BOLD+Font.ITALIC, size);
          return f;
        }
        
        if (str.equals ("Bold"))
        {
          f = new Font (name, Font.BOLD, size);
        }
        else if (str.equals ("Italic"))
        {
          f = new Font (name, Font.ITALIC, size);
        }
        else if (str.equals ("Plain"))
        {
          f = new Font (name, Font.PLAIN, size);
        }
        
        return f;
      }
       
      private final boolean sfnum (String str)
      {
        boolean issf=true;
        
        int strlen=str.length ();
        char ch = '*';
        int chm=0;
        
        for (int i=0; i<strlen; i++)
        {
          ch=str.charAt (i);
          chm=(int)ch;
          if (!(chm>=0x30 && chm<=0x39))
          {
            issf=false;
            break;
          }
        }
        
        return issf;
      }
      
	  /**
	   * Sets the character encoding for file operations.
	   * Provides a list of common charsets and a custom input option.
	   */
	  private final void setCharCode() {
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
				if (CHARCODE.equalsIgnoreCase(Help.charsets[i])) {
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
						CHARCODE = customInput.trim().toUpperCase();
					} else {
						CHARCODE = "UTF-8"; // Fallback for invalid/empty custom input
					}
				} else {
					CHARCODE = selectedSet;
				}

				// Update UI Label
				statusLabel.setText("Char Code: " + CHARCODE);
			}
			
			myfocus();
	  }
      
      //////////START OF ObjectOpenListener Inner Class/////////
      private final class ObjectOpenListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
          statusLabel.setText("");
          // Run heavy I/O in a separate thread to keep UI responsive
          Thread t = new Thread() {
            public void run() {
              openAtr();
            }
          };
          t.start();
        }
        
        private final boolean openAtr() {
          // 1. Show dialog on EDT
          int rep = objjfc.showOpenDialog(mb);
          if (rep != JFileChooser.APPROVE_OPTION) return false;
          
          final File f = objjfc.getSelectedFile();
          if (f == null || !f.exists()) return false;
          
          // Validation for .object extension
          if (!f.getName().toLowerCase().endsWith(".object")) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  JOptionPane.showMessageDialog(mb, "Not a supported Object file!");
                }
            });
            return false;
          }
          
          ObjectInputStream ois = null;
          try {
            ois = new ObjectInputStream(new FileInputStream(f));
            // Read the document object from file
            final Document d = (Document) ois.readObject();
            
            if (d != null) {
              // 2. Update UI on the Event Dispatch Thread
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    // Crucial: Set Kit BEFORE setting the Document
                    txt.setEditorKit(new StyledEditorKit());
                    txt.setDocument(d);
                    myfocus();
                    statusLabel.setText("Object Document Loaded: " + f.getName());
                  }
              });
            }
            } catch (Exception ex) {
            ex.printStackTrace();
            } finally {
            // 3. Always close streams in finally block
            try {
              if (ois != null) ois.close();
            } catch (Exception e) {}
          }
          return true;
        }
        
        public String toString ()
        {
          return "Object Open Listener";
        }
        
      }
      //////////END OF ObjectOpenListener Inner Class/////////
      
      //////////START OF ObjectSaveListener Inner Class/////////
      private final class ObjectSaveListener
      extends Object
      implements ActionListener
      {
        
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          saveAtr ();
        }
        
        private final boolean saveAtr ()
        {
          int rep = objjfc.showSaveDialog(mb);
          if (rep != JFileChooser.APPROVE_OPTION)
          {
            statusLabel.setText ("Cancelled Saving File!");
            return false;
          }
          
          File f = objjfc.getSelectedFile ();
          
          if (f == null)
          {
            statusLabel.setText ("Null or Non-Existent File For Saving!");
            return false;
          }
          
          if (f.exists ())
          {
            String m=JOptionPane.showInputDialog(mb,
            "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
            
            if (m==null)
            {
              statusLabel.setText ("Cancelled Overwrite Object Saving Operation!");
              return false;
            }
            
            if (m.equals (""))
            {
              statusLabel.setText ("Cancelled Overwrite Object Saving Operation!");
              return false;
            }
            
            if (m.equalsIgnoreCase ("y")==false && m.equalsIgnoreCase ("e")==false)
            {
              statusLabel.setText ("Cancelled Overwrite Object Saving Operation! Enter E/e/Y/y For Overwriting on the Program!");
              return false;
            }
          }
          
          String name = f.getName ().toLowerCase ();
          if (name.endsWith (".object") == false)
          {
            f = new File (f.getAbsolutePath ()+".object");
          }
          
          FileOutputStream fos = null;
          ObjectOutputStream oos = null;
          
          try
          {
            fos = new FileOutputStream (f);
            oos = new ObjectOutputStream (fos);
          }
          catch (Exception ex)
          {
            statusLabel.setText (""+ex.getMessage () + " error");
            return false;
          }
          
          if (fos == null || oos == null)
          {
            statusLabel.setText ("Output stream error: " + f.getName () + "");
            return false;
          }
          
          Document d = null;
          try
          {
            d = txt.getDocument ();
            if (d != null)
              oos.writeObject (d);
          }
          catch (Exception e)
          {
            statusLabel.setText (""+e.getMessage () + " error");
            e.printStackTrace ();
            try
            {
              oos.flush ();
              oos.close ();
              fos.close ();
            }
            catch (Exception x)
              {}
            return false;
          }
          
          try
          {
            oos.flush ();
            oos.close ();
            fos.close ();
          }
          catch (Exception ex)
            {}
          
          JOptionPane.showMessageDialog(mb, ("<html><body><font color=\"red\" size=\"5\">"+f.getName ()+"<fpnt><font color=\"blue\" size=\"4\">"+" saved successfully!</font></body></html>"));
          
          myfocus();
          
          return true;
        }
        
        public String toString ()
        {
          return "Object Save Listener";
        }
        
      }
      //////////END OF ObjectOpenListener Inner Class/////////
      
      ///////
      private final class ConvertListener
      extends Object
      implements ActionListener
      {
        
        private char [] KYN;
        private int KYNLEN;
        private String [] HTMLD;
        private String [] TXTD;
        
        private boolean isConvert=true;
        
        private ConvertListener (boolean isc)
        {
          super ();
          
          this.isConvert=isc;
          
          KYN = new char []
          {
            '\u0130',  '\u0131',  '\u00D6',  '\u00F6',
            '\u00DC',  '\u00FC',  '\u00C7',  '\u00E7',
            '\u011E',  '\u011F',  '\u015E',  '\u015F',
            (char)221, (char)253, (char)222, (char)254, (char)240
          };
          KYNLEN=KYN.length;
          
          HTMLD=new String []
          {
            "&#304;", "&#305;", "&#214;", "&#246;",
            "&#220;", "&#252;", "&#199;", "&#231;",
            "&#286;", "&#287;", "&#350;", "&#351;",
            "&#304;", "&#305;", "&#350;", "&#351;", "&#287;"
          };
          
          TXTD=new String []
          {
            "I", "i", "O", "o",
            "U", "u", "C", "c",
            "G", "g", "S", "s",
            "I", "i", "S", "s", "g"
          };
          
        }
        
        private final String renova (String old)
        {
          StringBuffer sb=new StringBuffer ();
          
          int l = old.length ();
          char ch='*';
          char chx='*';
          
          char RR='\r';
          char LN='\n';
          char NOKTA='.';
          char CIZGI='-';
          char BIX='\u0130';
          char UNLEM='!';
          
          char DX=((char)(272));
          
          int cval=0;
          
          for (int i=0x0; i<l-1; i++)
          {
            ch=old.charAt (i);
            if (ch==DX)
            {
              sb.append (BIX);
            }
            else
            {
              if (ch==LN)
              {
                sb.append (LN);
                //sb.append ("<p><p>");
              }
              else
              {
                cval=(int)(ch);
                if (cval > 256)
                {
                  if (isConvert)
                  {
                    sb.append (String.format ("&#%04d;", cval));
                  }
                  else
                  {
                    sb.append (ch);
                  }
                }
                else
                {
                  sb.append (ch);
                }
              }
            }
            
            if (ch==NOKTA||ch==CIZGI||ch==UNLEM)
            {
              chx=old.charAt (i+1);
              if (chx==LN)
              {
                //sb.append (RR);
                sb.append (LN);
              }
            }
          }
          
          return sb.toString ();
        }
        
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          Thread t=new Thread ()
          {
            public void run ()
            {
              try
              {
                //                  cms=0;
                String text = txt.getText ();
                if (text==null) return;
                
                CANKURTARAN=text;
                
                StringBuffer ucs=new StringBuffer ();
                ucs.append ((char)0x60);
                String ucnokta=ucs.toString ();
                
                text=text.replaceAll ("\u2019", "\'");
                text=text.replaceAll ("\u201c", "\"");
                text=text.replaceAll ("\u201d", "\"");
                text=text.replaceAll (ucnokta, "...");
                text=text.replaceAll ("�", "\"");
                text=text.replaceAll ("�", "\"");
                //text=text.replaceAll ("*", "-");
                
                char ch=text.charAt (0x0);
                statusLabel.setText ("First Caracter Number In The Text Is: "+Integer.toString ((int)ch));
                
                boolean removed=false;
                
                text = renova (text);
                
                //            setCharCode ();
                //CHARCODE="ISO-8859-9";
                CHARCODE="UTF-8";
                
                File f=new File ("jiptal.txt");
                
                OutputStream jfos=new FileOutputStream (f);
                OutputStream jbos=new BufferedOutputStream (jfos);
                PrintStream   jps=new PrintStream (jbos, true, CHARCODE);
                
                jps.println (text);
                
                jps.flush (); jps.close ();
                jbos.flush (); jbos.close ();
                jfos.flush (); jfos.close ();
                
                InputStream fis = new FileInputStream (f);
                InputStream bis=new BufferedInputStream (fis);
                InputStreamReader sr = new InputStreamReader (bis, CHARCODE);
                BufferedReader br=new BufferedReader (sr);
                
                String line = null;
                
                String abc="abc";
                
                String eleg = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\"> File New FileName Like Suffix:</font></body></html>");
                if (eleg!=null)
                {
                  if (eleg.length ()>=1)
                  {
                    abc=eleg;
                  }
                }
                
                OutputStream fos=new FileOutputStream (new File ("npoperations/"+abc+".html"));
                OutputStream bos=new BufferedOutputStream (fos);
                PrintStream ps=new PrintStream (bos, true, CHARCODE);
                
                OutputStream fos2=new FileOutputStream (new File ("npoperations/"+abc+".txt"));
                OutputStream bos2=new BufferedOutputStream (fos2);
                PrintStream ps2=new PrintStream (bos2, true, CHARCODE);
                
                OutputStream fos3=new FileOutputStream (new File ("npoperations/"+abc+"_lt"+".html"));
                OutputStream bos3=new BufferedOutputStream (fos3);
                PrintStream ps3=new PrintStream (bos3, true, CHARCODE);
                
                ps.print ("<html>\n");
                ps.print ("<head>\n");
                ps.print ("<title></title>\n");
                ps.print ("</head>\n");
                ps.print ("<body>\n");
                
                ps3.print ("<html>\n");
                ps3.print ("<head>\n");
                ps3.print ("<title></title>\n");
                ps3.print ("</head>\n");
                ps3.print ("<body>\n");
                
                String lm="";
                boolean found=false;
                boolean DVM=false;
                
                while ( (line=br.readLine ()) != null )
                {
                  if (removed)
                  {
                  }
                  
                  lm=line.trim ();
                  int llen=lm.length ();
                  if (llen<1)
                  {
                    ps3.print ("\n");
                    ps3.print ("<p><p>");
                    ps3.print ("\n");
                    
                    ps2.print ("\n\n");
                    continue;
                  }
                  
                  if (sfnum (lm)) continue;
                  
                  StringBuffer sb=new StringBuffer ();
                  StringBuffer sb2=new StringBuffer ();
                  
                  char [] chars = line.toCharArray ();
                  int clen = chars.length;
                  
                  char tmp='*';
                  for (int i=0; i<clen; i++)
                  {
                    tmp = chars [i];
                    sb2.append (tmp);
                    //sb.append (tmp);
                    
                    if (isConvert)
                    {
                      found=false;
                      for (int j=0; j<KYNLEN; j++)
                      {
                        if (tmp==KYN [j])
                        {
                          sb.append (HTMLD [j]);
                          found=true;
                          break;
                        }
                      }
                      
                      if (!found)
                      {
                        sb.append (tmp);
                      }
                    }
                    else
                    {
                      sb.append (tmp);
                    }
                  }
                  
                  sb.append ("\n");
                  sb2.append ("\n");
                  ps.print ((sb.toString ()).trim ());
                  ps.print (" ");
                  
                  int lmlen=lm.length ();
                  boolean ccor=Character.isLetter (lm.charAt (lmlen-1));
                  
                  if (ccor==false|| lmlen<52)
                  {
                    ps.print ("\n");
                    ps.print ("<p><p>");
                    ps.print ("\n");
                  }
                  
                  ps3.print (sb2.toString ());
                  ps3.print (" ");
                  ps2.print (sb2.toString ());
                  ps2.print (" ");
                }//while
                
                ps.print ("\n</body>\n");
                ps.print ("</html>\n");
                
                ps3.print ("\n</body>\n");
                ps3.print ("</html>\n");
                
                br.close ();  sr.close ();
                bis.close (); fis.close ();
                ps.flush ();  ps.close ();
                bos.flush (); bos.close ();
                fos.flush (); fos.close ();
                
                ps2.flush ();  ps2.close ();
                bos2.flush (); bos2.close ();
                fos2.flush (); fos2.close ();
                
                ps3.flush ();  ps3.close ();
                bos3.flush (); bos3.close ();
                fos3.flush (); fos3.close ();
                
                JOptionPane.showMessageDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Producing HTML Files Operation Okey!</font></body></html>");
                return;
              }
              catch (Exception e)
              {
                e.printStackTrace ();
                return;
              }
            }
          };
          t.start ();
        }
      }
      //
      
      // Main Open File Action
      public void actionPerformed(ActionEvent evt) {
        statusLabel.setText("");
        // 1. Show the dialog in the Event Dispatch Thread (EDT) for safety
        int rep = jfc.showOpenDialog(mb);
        if (rep != JFileChooser.APPROVE_OPTION) {
          return;
        }
        
        final File f = jfc.getSelectedFile();
        if (f == null) {
          return;
        }
        
        // 2. Start a background thread to prevent UI freezing during file I/O
        Thread t = new Thread() {
          public void run() {
            try {
              // Ensure the editor kit is set correctly before loading text
              // Use invokeLater because setEditorKit affects the UI
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    txt.setEditorKit(new StyledEditorKit());
                  }
              });
              
              // Optimization: Use StringBuilder instead of StringBuffer
              StringBuilder sb = new StringBuilder();
              
              // Open streams with explicit charset
              FileInputStream fis = new FileInputStream(f);
              BufferedInputStream bis = new BufferedInputStream(fis);
              InputStreamReader fr = new InputStreamReader(bis, CHARCODE);
              BufferedReader br = new BufferedReader(fr);
              
              String line = null;
              while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
              }
              
              // Close all resources in reverse order
              br.close();
              fr.close();
              bis.close();
              fis.close();
              
              final String finalContent = sb.toString();
              
              // 3. Update the UI back on the Event Dispatch Thread
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    txt.setText(finalContent);
                    txt.setCaretPosition(0);
                    myfocus();
                    statusLabel.setText("Opened " + f.getName() + " successfully!;;; Current Charcode is: " + CHARCODE);
                  }
              });
              
              } catch (final IOException e) {
              // Handle exceptions safely
              SwingUtilities.invokeLater(new Runnable() {
                  public void run() {
                    e.printStackTrace();
                  }
              });
            }
          }
        };
        t.start();
      }
      
      //////
      private final class KaydetListener
      implements ActionListener
      {
        
        public void actionPerformed (ActionEvent evt)
        {
          statusLabel.setText("");
          Thread t = new Thread ()
          {
            public void run ()
            {
              int rep = jfc.showSaveDialog (mb);
              
              if (rep != JFileChooser.APPROVE_OPTION) return;
              
              File f = jfc.getSelectedFile ();
              
              if (f.exists ())
              {
                String m=JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">This file exists. Enter Y/y/E/e For Overwrite!</font></body></html>");
                
                if (m==null)
                {
                  statusLabel.setText ("Cancelled Overwrite Saving Operation!");
                  return;
                }
                
                if (m.equals (""))
                {
                  statusLabel.setText ("Cancelled Overwrite Saving Operation!");
                  return;
                }
                
                if (m.equalsIgnoreCase ("y")==false && m.equalsIgnoreCase ("e")==false)
                {
                  statusLabel.setText ("Cancelled Overwrite Saving Operation! Enter E/e/Y/y For Overwriting on the Program!");
                  return;
                }
              }
              
              //setCharCode ();
              
              try
              {
                OutputStream fos = new FileOutputStream (f);
                OutputStreamWriter ps = new OutputStreamWriter (fos, CHARCODE);
                
                String text = txt.getText ();
                if (text == null) text = "";
                
                ps.write (text);
                
                //if (text!=null) CANKURTARAN=text;
                
                ps.flush ();  ps.close ();
                fos.flush (); fos.close ();
                
                JOptionPane.showMessageDialog(mb, ("<html><body><font color=\"red\" size=\"5\">"+f.getName ()+"<fpnt><font color=\"blue\" size=\"5\">"+" saved successfully!<br><br>Char code is: " + CHARCODE + "</font></body></html>"));
                //statusLabel.setText("Current Charcode is: " + CHARCODE);
                return;
              }
              catch (IOException io)
              {
                io.printStackTrace ();
                return;
              }
              
            }
          };
          t.start ();
        }
    }
    //////
      
	private final String toUnicode(String text) throws Exception {
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

	private final String fromUnicode(String text) throws Exception {
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
    
    private static final void ekran() {
      // Create necessary directory if it doesn't exist
      File f = new File("npoperations");
      if (!f.exists()) {
        f.mkdir();
      }
      
      try {
        // Define custom font resources
        final javax.swing.plaf.FontUIResource resourceA =
        new javax.swing.plaf.FontUIResource("SansSerif", Font.BOLD, 14);
        final javax.swing.plaf.FontUIResource resourceB =
        new javax.swing.plaf.FontUIResource("SansSerif", Font.BOLD, 20);
        final javax.swing.plaf.FontUIResource resourceC =
        new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, 13);
        
        //UIManager.put("OptionPane.messageFont", resourceA);
        //UIManager.put("OptionPane.buttonFont", resourceC);
        UIManager.put("TextField.font", resourceB);
        UIManager.put("TextField.foreground", Color.BLUE);
        
        // FileChooser specific customizations
        UIManager.put("FileChooser.font", resourceA);
        UIManager.put("FileChooser.listFont", resourceA);
        UIManager.put("FileChooser.tableFont", resourceA);
        UIManager.put("FileChooser.foreground", Color.BLACK);
        UIManager.put("FileChooser.listSelectionBackground", new Color(51, 153, 255));
        UIManager.put("FileChooser.listSelectionForeground", Color.WHITE);
        UIManager.put("FileChooser.buttonFont", resourceC);
        
        } catch (Exception e) {
        // Log errors if the Look and Feel fails to load
        e.printStackTrace();
      }
      
      // 3. Initialize the main frame
      JFrame fr = new JFrame("Not Defteri");
      fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // 4. Set the content pane using your custom class
      // Note: Ensure NotDefteriX extends JComponent or JPanel
      NotDefteriX jc = new NotDefteriX();
      jc.setOpaque(true);
      fr.setContentPane(jc);
      
      // 5. Frame sizing and positioning
      fr.pack(); // Adjusts size based on components
      fr.setSize(1070, 620); // Forces specific dimensions
      fr.setResizable(true);
      fr.setLocationRelativeTo(null); // Centers the window on screen
      
      // 6. Make the frame visible
      fr.setVisible(true);
    }
    
    public static final void main (String [] args)
    {
      javax.swing.SwingUtilities.invokeLater (new Runnable ()
        {
          public void run ()
          {
            ekran ();
          }
      });
    } // end of main
    
  }
  // class sonu
  
/*
  MANIFEST.MF
  -------------
  Manifest-Version: 1.0
  Created-By: 1.8 (Sun MicroSystems, Inc.)
  Main-Class: net.dilmerkezi.defter.NotDefteriX
  Class-Path:
  
*/
  
/*
  Linux; Bastan derleme yapilacak ise:
  
  javac -source 1.8 -target 1.8 -sourcepath src -cp obj -g:none -proc:none -nowarn -O -d obj src/net/dilmerkezi/defter/NotDefteriX.java
  
  jar cvmf MANIFEST.MF bin/notdefteri_110426.jar -C obj net -C . src compile_run.txt xcopy.sh ControlChanges.* changes.txt
  
  rm -rfv obj/net
  
  cd bin
  
  java -jar notdefteri_110426.jar &
*/
