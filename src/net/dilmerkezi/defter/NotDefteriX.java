package net.dilmerkezi.defter;

import java.io.*;

import java.net.URI;
import java.net.URLEncoder;

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
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Iterator;
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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.GCMParameterSpec;

import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.OutputKeys;

import org.xml.sax.InputSource;

// custom
import net.dilmerkezi.filter.*;
import net.dilmerkezi.listeners.*;
// End of import blog

// Start of class
public final class NotDefteriX extends JPanel {
  private final Utilities utilities = new Utilities();

  private final JMenuBar mb = new JMenuBar ();

  private final MTextPane txt = new MTextPane ();

  private final PostScriptExporter PSPRINTER = new PostScriptExporter();

  private String CANKURTARAN="";

  private final String [] USEDCHARS={"10", "13", "42", "95", "96", "127", "160", "161", "166", "168", "170", "171", "173", "175", "176", "180", "182", "183", "184", "186", "187", "191", "193", "199", "201", "205", "209", "211", "214", "218", "220", "225", "226", "231", "233", "237", "238", "241", "243", "246", "247", "250" , "251", "252", "286", "287", "304", "305", "350", "351", "8210", "8211", "8212", "8213", "8214", "8215", "8216", "8217", "8218", "8219", "8220", "8221", "8222", "8223", "8224", "8226", "8230", "8592", "8594", "9474", "61694", "65533"};

  private final int USEDCHARSLEN=USEDCHARS.length;

  private final JLabel statusLabel = new JLabel("", JLabel.LEFT);

  private final Vector <String> KONTROLVEC = new Vector <String> ();
  private final Vector <String> ACTUALVEC = new Vector <String> ();

  private final Font TXTFONT = new Font ("Monospaced", Font.PLAIN, 23);

  private final JFileChooser jfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser iconjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser objjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser rtfjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser encjfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser htmljfc = new JFileChooser (new File ("npoperations"));
  private final JFileChooser commonjfc = new JFileChooser (new File ("npoperations"));

  private String CONFIND="";

  private final JEditorPane helpTextArea = new JEditorPane();

  //START OF CONSTRUCTOR METHOD
  private NotDefteriX () {
    super (new BorderLayout ());

    jfc.setFileFilter(new TextFileFilter());
    iconjfc.setFileFilter(new IconFileFilter());
    iconjfc.setAccessory(new ImagePreview(iconjfc)); // Setup the preview accessory
    objjfc.setFileFilter(new ObjectFileFilter());
    rtfjfc.setFileFilter(new RTFFileFilter());
    encjfc.setFileFilter(new ENCFileFilter());
    htmljfc.setFileFilter(new HTMLFileFilter());

    helpTextArea.setEditable(true);
    helpTextArea.setFont(new Font("Monospaced", Font.BOLD, 18));
    helpTextArea.setForeground(Color.BLUE);
    helpTextArea.setPreferredSize(new Dimension(864, 432));

    statusLabel.setBackground(new Color(0xCCCC22));
    statusLabel.setForeground(Color.BLUE);
    statusLabel.setFont(new Font("SansSerif", Font.PLAIN | Font.ITALIC, 18));

    for (int i=0; i<USEDCHARSLEN; i++) {
      KONTROLVEC.add (USEDCHARS [i]);
    }

    txt.setFont (TXTFONT);
    txt.getDocument ().addUndoableEditListener (UndoRedoConstants.ulis);

    JMenu m = getMenu ("File");
    m.setMnemonic ('F');

    JMenuItem nfile = getMenuItem("New", KeyEvent.VK_N, "CTRL-N");
    nfile.addActionListener(new NewFileActionListener(txt, statusLabel, mb));
    m.add(nfile);

    m.addSeparator();

    JMenuItem ac = getMenuItem ("Open", KeyEvent.VK_O, "CTRL-O");
    ac.addActionListener(new OpenFileActionListener(txt, statusLabel, jfc, mb, utilities.getCharCode()));
    m.add (ac);

    JMenuItem kaydet = getMenuItem ("Save", KeyEvent.VK_S, "CTRL-S");
    kaydet.addActionListener(new SaveFileActionListener(txt, statusLabel, jfc, mb, utilities.getCharCode()));
    m.add (kaydet);

    m.addSeparator();

    JMenuItem openEnc = getMenuItem("Open Encrypted", KeyEvent.VK_0, "CTRL-0");
    openEnc.addActionListener(new OpenEncryptedFileActionListener(txt, statusLabel, encjfc, mb));
    m.add(openEnc);

    JMenuItem saveEnc = getMenuItem("Save Encrypted", KeyEvent.VK_K, "CTRL-K");
    saveEnc.addActionListener(new SaveEncryptedFileActionListener(txt, statusLabel, encjfc, mb));
    m.add(saveEnc);

    m.addSeparator();

    JMenuItem openObjectItem=getMenuItem ("Open Object File", -1, "");
    openObjectItem.addActionListener(new ObjectOpenListener(txt, statusLabel, objjfc, mb));
    m.add (openObjectItem);

    JMenuItem saveObjectItem=getMenuItem ("Save Object File", -1, "");
    saveObjectItem.addActionListener(new ObjectSaveListener(txt, statusLabel, objjfc, mb));
    m.add (saveObjectItem);

    m.addSeparator();

    JMenuItem openRTFItem = getMenuItem ("Open RTF", -1, "");
    openRTFItem.addActionListener(new OpenRTFFileActionListener(txt, statusLabel, rtfjfc, mb));
    m.add (openRTFItem);

    JMenuItem saveRTFItem = getMenuItem ("Save RTF", -1, "");
    saveRTFItem.addActionListener(new SaveRTFFileActionListener(txt, statusLabel, rtfjfc, mb));
    m.add (saveRTFItem);

    m.addSeparator();

    JMenuItem openHTMLItem = getMenuItem ("Open HTML", -1, "");
    openHTMLItem.addActionListener(new OpenHTMLFileActionListener(helpTextArea, statusLabel, htmljfc, mb));
    m.add (openHTMLItem);

    JMenuItem saveHTMLItem = getMenuItem ("Save HTML", -1, "");
    saveHTMLItem.addActionListener(new SaveHTMLFileActionListener(txt, statusLabel, htmljfc, mb));
    m.add (saveHTMLItem);

    m.addSeparator();

    JMenuItem undoItem = getMenuItem ("Undo", KeyEvent.VK_Z, "CTRL-Z");
    undoItem.addActionListener (UndoRedoConstants.uaction);
    m.add (undoItem);

    JMenuItem redoItem = getMenuItem ("Redo", -1, "");
    redoItem.addActionListener (UndoRedoConstants.raction);
    m.add (redoItem);

    m.addSeparator();

    JMenuItem screenItem = getMenuItem ("Screen", -1, "");
    screenItem.addActionListener(new ScreenSaverActionListener(txt, statusLabel, mb));
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
          myfocus();
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
        txt.requestFocus();
      }
    });
    m.add(printObjectItem);

    JMenuItem shareEmailItem = getMenuItem("Share", -1, "");
    shareEmailItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          final String shareText = txt.getText();

          if (shareText != null && !shareText.trim().isEmpty()) {
            String encodedText = URLEncoder.encode(shareText, "UTF-8").replace("+", "%20");
            String mailto = "mailto:?subject=Java%20Notepad%20Content&body=" + encodedText;

            // Sistem varsayılan e-posta istemcisini açma
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
              Desktop.getDesktop().mail(new URI(mailto));
            } else {
              JOptionPane.showMessageDialog(null,
              "There is not any e-mail app on your system!",
              "Error", JOptionPane.ERROR_MESSAGE);
            }
          } else {
            JOptionPane.showMessageDialog(null,
            "There is not any content!",
            "Warning", JOptionPane.WARNING_MESSAGE);
          }
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(null,
          "Un error ocurred when opening e-mail: " + ex.getMessage(),
          "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    });
    m.add(shareEmailItem);

    m.addSeparator();

    JMenuItem exitItem = getMenuItem ("Exit", KeyEvent.VK_Q, "CTRL-Q");
    exitItem.addActionListener(new ExitActionListener(txt, statusLabel, mb));
    m.add (exitItem);

    mb.add (m);

    JMenu beatm=getMenu ("Beautify");

    // Create the menu item for cleaning problematic/odd characters
    JMenuItem harapcaSil = getMenuItem("Remove Odd Chars", -1, "");
    harapcaSil.addActionListener(new RemoveOddCharsActionListener(txt, statusLabel));
    beatm.add(harapcaSil);

    beatm.addSeparator ();

    JMenuItem controlCharsItem = getMenuItem ("Control Chars", KeyEvent.VK_G, "CTRL-G");
    controlCharsItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
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

        for (int i=0; i<metinlen; i++) {
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

        if (vsize<1) {
          statusLabel.setText ("THERE IS NOT CONTENT!");
          return;
        }

        String [] dizi=new String [vsize];
        vsize=dizi.length;

        for (int i=0; i<vsize; i++) {
          dizi [i]=ACTUALVEC.elementAt (i);
        }

        Arrays.sort (dizi);

        vsize=dizi.length;

        StringBuilder sb=new StringBuilder (metin);
        sb.append ("\n\n\n");
        sb.append ("===================\n");
        String ssm="";
        char ccm='i';

        for (int i=0; i<vsize; i++) {
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

    JMenuItem farapcaSil = getMenuItem("Remove Words", -1, "");
    farapcaSil.addActionListener(new RemoveWordsActionListener(txt, statusLabel, mb));
    beatm.add(farapcaSil);

    beatm.addSeparator();

    JMenuItem removePageNumbersItem = getMenuItem ("Remove Page Numbers", -1, "");
    removePageNumbersItem.addActionListener(new RemovePageNumbersActionListener(txt, statusLabel, mb));
    beatm.add(removePageNumbersItem);

    JMenuItem removeDuplicateLinesItem = getMenuItem ("Remove Duplicate Lines", -1, "");
    removeDuplicateLinesItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String m=txt.getText ();
        if (m==null) {
          statusLabel.setText ("Null Text Error!");
          return;
        }

        if (m.length () < 3) {
          statusLabel.setText ("Insufficient Text Error!");
          return;
        }

        removeLinesDuplicate(m);
        myfocus();
      }
    });
    beatm.add(removeDuplicateLinesItem);

    JMenuItem addDiyez = getMenuItem("Add # for Footnotes", -1, "");
    addDiyez.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        statusLabel.setText("");
        String text = txt.getText();
        StringBuilder result = new StringBuilder();
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
              result.append("\n" + Help.START_FN + ".\n");
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
              result.append(Help.END_FN + ".\n");
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
    arapcaSil.addActionListener(new RemoveHighCodeCharsActionListener(txt, statusLabel, mb));
    beatm.add (arapcaSil);

    JMenuItem marapcaSil = getMenuItem ("Remove Chars Lesser Than", -1, "");
    marapcaSil.addActionListener(new RemoveLowCodeCharsActionListener(txt, statusLabel, mb));
    beatm.add (marapcaSil);

    JMenuItem karapcaSil = getMenuItem ("Remove StartsWith Lines", -1, "");
    karapcaSil.addActionListener(new RemoveLinesStartingWithActionListener(txt, statusLabel, mb));
    beatm.add (karapcaSil);

    JMenuItem tarapcaSil = getMenuItem ("Remove Contains Lines", -1, "");
    tarapcaSil.addActionListener(new RemoveLinesContainingActionListener(txt, statusLabel, mb));
    beatm.add (tarapcaSil);

    JMenuItem jjarapcaSil = getMenuItem ("Remove Not Contains Lines", -1, "");
    jjarapcaSil.addActionListener(new KeepLinesContainingActionListener(txt, statusLabel, mb));
    beatm.add (jjarapcaSil);

    beatm.addSeparator ();

    JMenuItem narapcaSil = getMenuItem ("Replace Multi Words", -1, "");
    narapcaSil.addActionListener(new MultiWordReplaceActionListener(txt, statusLabel, mb));
    beatm.add (narapcaSil);

    beatm.addSeparator ();

    JMenuItem replaceItem = getMenuItem ("Replace", KeyEvent.VK_R, "CTRL-R");
    replaceItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String mm=txt.getText ();

        if (mm==null) {
          statusLabel.setText ("Null text for Replace!");
          return;
        }

        if (mm.length () < 1) {
          statusLabel.setText ("Zero Length Text for Replace!!");
          return;
        }

        CANKURTARAN=mm;

        String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src string for replace:</font></body></html>");

        if (srcreps == null) {
          statusLabel.setText ("Not Replaced Null Value!");
          return;
        }

        if ((srcreps.length ()) < 1) {
          statusLabel.setText ("Not Replaced Zero Length Value!");
          return;
        }

        String dstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst string for replace:</font></body></html>");

        if (dstreps == null) {
          statusLabel.setText ("Not Replaced Null Value!");
          return;
        }

        try {
          String k=mm.replace (srcreps, dstreps);
          int klen=k.length ();

          txt.setText (k);

          statusLabel.setText ("Replace Operation is Okey: "+srcreps+" --> "+dstreps+"");
        }
        catch (Exception e) {
          statusLabel.setText ("Warning: Not Replaced!");
          return;
        }

        myfocus();

        return;
      }
    });
    beatm.add (replaceItem);

    JMenuItem sreplaceItem = getMenuItem ("Specific Replace", KeyEvent.VK_J, "CTRL-J");
    sreplaceItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String mm=txt.getText ();

        if (mm==null) {
          statusLabel.setText ("Null text for Specific Replace!");
          return;
        }

        if (mm.length () < 1) {
          statusLabel.setText ("Zero Length Text for Specific Replace!");
          return;
        }

        CANKURTARAN=mm;

        String xsrcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src char num for replace like: 10;10</font></body></html>");

        if (xsrcreps == null) {
          statusLabel.setText ("Not Specific Replaced Src Null Value!");
          return;
        }

        if ((xsrcreps.length ()) < 1) {
          statusLabel.setText ("Not Specific Replaced Src Zero Length!");
          return;
        }

        String xdstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst char num for replace like: 32;<br><br>[0(cero is empty character.)]</font></body></html>");

        if (xdstreps == null) {
          statusLabel.setText ("Not Specific Replaced Dst Null Value!");
          return;
        }

        if ((xdstreps.length ()) < 1) {
          statusLabel.setText ("Not Specific Replaced Dst Zero Length!");
          return;
        }

        String menba [] = xsrcreps.split (";");
        String zenba [] = xdstreps.split (";");

        int menbalen=menba.length;
        int zenbalen=zenba.length;

        StringBuilder fm=new StringBuilder ("");

        for (int i=0; i<menbalen; i++) {
          int r=Integer.parseInt (menba[i]);
          fm.append ((char)r);
        }

        String srcreps=fm.toString ();

        fm=new StringBuilder ();

        for (int i=0; i<zenbalen; i++) {
          int r=Integer.parseInt (zenba[i]);
          fm.append ((char)r);
        }

        String dstreps=fm.toString ();

        try {
          dstreps = dstreps.replace("\0", "");
          String k=mm.replace (srcreps, dstreps);
          int klen=k.length ();

          txt.setText (k);

          statusLabel.setText ("Specific Replace Operation is Okey: "+xsrcreps+" --> "+xdstreps+"");
        }
        catch (Exception e) {
          statusLabel.setText ("Warning: Not Specific Replaced!");
          return;
        }

        myfocus();

        return;
      }
    });
    beatm.add (sreplaceItem);

    JMenuItem multiSreplaceItem = getMenuItem ("Multi Specific Replace", -1, "");
    multiSreplaceItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String mm = txt.getText();

        if (mm == null || mm.length() < 1) {
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

        if (xsrcreps == null || xsrcreps.length() < 1) {
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

        if (xdstreps == null || xdstreps.length() < 1) {
          statusLabel.setText("Not Multi Specific Replaced - Dst Null or Zero!");
          return;
        }

        // Replace gruplarini ayir; - isareti ile
        String[] srcGroups = xsrcreps.split("-");
        String[] dstGroups = xdstreps.split("-");

        // Grup sayilari esit mi kontrol et
        if (srcGroups.length != dstGroups.length) {
          statusLabel.setText("Error: Source and Destination group counts don't match!");
          return;
        }

        String result = mm;

        // Her bir replace grubunu isle
        for (int g = 0; g < srcGroups.length; g++) {
          String srcGroup = srcGroups[g];
          String dstGroup = dstGroups[g];

          // src ve dst'yi ; ile ayir
          String[] srcParts = srcGroup.split(";");
          String[] dstParts = dstGroup.split(";");

          if (srcParts.length == 0 || dstParts.length == 0) {
            statusLabel.setText("Warning: Empty group at index " + g);
            continue;
          }

          // src stringini olustur
          StringBuilder srcBuf = new StringBuilder();
          for (int i = 0; i < srcParts.length; i++) {
            try {
              int r = Integer.parseInt(srcParts[i].trim());
              srcBuf.append((char) r);
            }
            catch (NumberFormatException e) {
              statusLabel.setText("Warning: Invalid number in src group " + g + ": " + srcParts[i]);
            }
          }

          // dst stringini oluştur
          StringBuilder dstBuf = new StringBuilder();
          for (int i = 0; i < dstParts.length; i++) {
            try {
              int r = Integer.parseInt(dstParts[i].trim());
              dstBuf.append((char) r);
            }
            catch (NumberFormatException e) {
              statusLabel.setText("Warning: Invalid number in dst group " + g + ": " + dstParts[i]);
            }
          }

          String srcReps = srcBuf.toString();
          String dstReps = dstBuf.toString();

          // Replace islemini yap
          try {
            dstReps = dstReps.replace("\0", "");
            result = result.replace(srcReps, dstReps);
            statusLabel.setText("Multi Replace Group " + (g+1) + ": " + srcGroup + " --> " + dstGroup);
          }
          catch (Exception e) {
            statusLabel.setText("Warning: Replace failed for group " + (g+1));
          }
        }

        // Sonucu text alanina yaz
        try {
          int klen = result.length();

          txt.setText(result);

          statusLabel.setText("Multi Specific Replace Operation Completed Successfully!");
        }
        catch (Exception e) {
          statusLabel.setText("Error: Failed to set text!");
          e.printStackTrace();
        }

        myfocus();
      }
    });
    beatm.add(multiSreplaceItem);

    JMenuItem excreplaceItem = getMenuItem ("Excepcional Replace", KeyEvent.VK_L, "CTRL-L");
    excreplaceItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String mm=txt.getText ();

        if (mm==null) {
          statusLabel.setText ("Null text for Excepcional Replace!");
          return;
        }

        if (mm.length () < 1) {
          statusLabel.setText ("Zero Length Text for Excepcional Replace!!");
          return;
        }

        CANKURTARAN=mm;

        String srcreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter src string for replace:</font></body></html>");

        if (srcreps==null) {
          statusLabel.setText ("Not Replaced Src Null Value!");
          return;
        }

        if ((srcreps.length ()) < 1) {
          statusLabel.setText ("Not Replaced Zero Src Length Value!");
          return;
        }

        String dstreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter dst string for replace:</font></body></html>");

        if (dstreps==null) {
          statusLabel.setText ("Not Replaced Dst Null Value!");
          return;
        }

        String excreps = JOptionPane.showInputDialog(mb, "<html><body><font color=\"blue\" size=\"5\">Enter excepcional strings separated with comma:</font></body></html>");

        if (excreps==null) {
          statusLabel.setText ("Not Replaced Exc Null Value!");
          return;
        }

        if ((excreps.indexOf (",")) < 0x0000) {
          excreps=""+excreps+", ";
        }

        String [] excos=excreps.split (",");

        if (excos == null) {
          statusLabel.setText ("Not Replaced for Null Excepcionales!");
          return;
        }

        final int exclen=excos.length;
        if (exclen < 1) {
          statusLabel.setText ("Not Replaced for Cero Excepcionales!");
          return;
        }

        try {
          String k="";

          Reader sr=new StringReader (mm);
          BufferedReader br=new BufferedReader (sr);

          String line=null;
          String xline="";

          String word="";

          String excword="";
          String miniword="";

          StringBuilder sb=new StringBuilder ();

          while ( (line=br.readLine ()) != null) {
            line=line.trim ();

            if (line.length () < 1) {
              sb.append ("\n");
              continue;
            }

            for (int i=0; i<10; i++) {
              line=line.replace ("  ", " ");
            }

            xline=line;

            String [] kelimes=xline.split (" ");
            if (kelimes == null) {
              sb.append (xline);
              sb.append ("\n");
              continue;
            }

            int kelen=kelimes.length;
            if (kelen < 1) {
              sb.append (xline);
              sb.append ("\n");
              continue;
            }

            boolean found=false;

            for (int i=0; i<kelen; i++) {
              word=(kelimes [i]).trim ();
              if (word.length () < 1) {
                continue;
              }

              if ((word.indexOf (srcreps)) < 0x0000) {
                sb.append (word);
                sb.append (" ");
                continue;
              }

              miniword=word.toLowerCase ();

              found=false;

              for (int j=0; j<exclen; j++) {
                excword=excos [j];

                if ((miniword.indexOf (excword)) >= 0x0000) {
                  found=true;
                  break;
                }
              }

              if (found) {
                sb.append (word);
                sb.append (" ");
              }
              else {
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
        catch (Exception e) {
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

            // Process escape sequences; e.g., LF to newline character
            String processedFrom = utilities.escapeSequences(from);
            String processedTo = utilities.escapeSequences(to);

            // Perform the regex replacement in the background thread
            Pattern pattern = Pattern.compile(processedFrom, Pattern.UNICODE_CHARACTER_CLASS);
            Matcher matcher = pattern.matcher(text);
            return matcher.replaceAll(processedTo);
          }

          @Override
          protected void done() {
            try {
              // 1. Capture the position BEFORE any change
              final int oldPos = txt.getCaretPosition();

              // 2. Get the result from background thread
              final String result = get();

              // 3. Update UI and restore focus safely
              SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                  txt.setText(result); // This moves caret to end

                  int newLen = result.length();
                  int targetPos = Math.min(oldPos, newLen);

                  if (targetPos >= 0) {
                    txt.setCaretPosition(targetPos);
                  }
                  txt.requestFocus();

                  statusLabel.setText("Regex Replace completed successfully.");
                }
              });

            } catch (java.util.concurrent.ExecutionException e) {
              Throwable cause = e.getCause();
              String msg = (cause != null) ? cause.getMessage() : "Unknown Error";
              JOptionPane.showMessageDialog(mb, "Regex Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
              e.printStackTrace();
            } finally {
              // Restore cursor - Do NOT call myfocus() here to avoid caret jump
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
    insertIndentsItem.addActionListener(new InsertIndentsActionListener(txt, statusLabel, mb));
    beatm.add(insertIndentsItem);

    JMenuItem prettyXMLItem = getMenuItem("Pretty XML", -1, "");
    prettyXMLItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        statusLabel.setText("");
        prettyFormatXML();
        myfocus();
      }
    });
    beatm.add(prettyXMLItem);

    beatm.addSeparator();

    JMenuItem bcItem = getMenuItem("Beautify Code", -1, "");
    bcItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        statusLabel.setText("Formatting code, please wait...");

        // Take the current caret position and text before changing anything
        final int oldCaretPos = txt.getCaretPosition();
        final String rawText = txt.getText();

        bcItem.setEnabled(false);

        // Simple SwingWorker to run code formatting in the background
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
          @Override
          protected String doInBackground() throws Exception {
            return CodeBeautifier.format(rawText);
          }

          @Override
          protected void done() {
            try {
              String result = get();
              txt.setText(result);

              // Put the caret back where it was before txt.setText() wiped it out
              txt.setCaretPosition(oldCaretPos);

              statusLabel.setText("Code formatted successfully.");

              // Directly calling your exact method as requested
              myfocus();

            } catch (Exception e) {
              statusLabel.setText("Error occurred during formatting.");
              e.printStackTrace();
            } finally {
              bcItem.setEnabled(true);
            }
          }
        };

        worker.execute();
      }
    });
    beatm.add(bcItem);

    beatm.addSeparator ();

    JMenuItem replaceTodoItem = getMenuItem("Beautify Text", KeyEvent.VK_T, "CTRL-T");
    replaceTodoItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        statusLabel.setText("");
        final String m = txt.getText();

        if (m == null || m.length() < 1) {
          statusLabel.setText("Invalid text for Beautify Operation!");
          return;
        }

        // 1. Get current caret position BEFORE text change
        final int oldCaretPos = txt.getCaretPosition();

        CANKURTARAN = m;
        V2Utils.setBorrowNums(false);

        String movitext = null;
        boolean error = false;

        try {
          movitext = V2UTFToISO.cnt(m);
        } catch (IOException ioe) {
          error = true;
        }

        if (error) {
          statusLabel.setText("An error occurred V2UTFToISO.");
          return;
        }

        if (movitext != null) {
          final String finalMovitext = movitext;

          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              txt.setText(finalMovitext);

              // 2. Restore caret position safely
              int newLen = finalMovitext.length();
              // Ensure the old position is still valid in the new text
              final int targetPos = Math.min(oldCaretPos, newLen);

              // Directly setting position instead of just calling myfocus()
              // because we need the 'targetPos' we captured earlier.
              if (targetPos >= 0) {
                txt.setCaretPosition(targetPos);
              }

              txt.requestFocus();
            }
          });
        }
      }
    });
    beatm.add(replaceTodoItem);

    mb.add (beatm);

    JMenu m2a=getMenu ("Show");

    JMenuItem changeEncodingItem = getMenuItem ("Change Encoding", -1, "");
    changeEncodingItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        utilities.setCharCode(txt, statusLabel, mb);
        txt.requestFocus();
      }
    });
    m2a.add(changeEncodingItem);

    m2a.addSeparator();

    JMenuItem sayCharItem = getMenuItem("Show Char Info", -1, "");
    sayCharItem.addActionListener(new ShowCharNumActionListener(txt, statusLabel, mb));
    m2a.add(sayCharItem);

    m2a.addSeparator ();

    JMenuItem sayCharItemY = getMenuItem("Show Numerous Char", -1, "");
    sayCharItemY.addActionListener(new ShowNumerousCharActionListener(txt, statusLabel, mb));
    m2a.add(sayCharItemY);

    JMenuItem appendNumerousChar = getMenuItem("Append Numerous Char", -1, "");
    appendNumerousChar.addActionListener(new AppendNumerousCharActionListener(txt, statusLabel, mb));
    m2a.add(appendNumerousChar);

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
        // Open the file chooser
        int result = iconjfc.showOpenDialog(mb);

        if (result == JFileChooser.APPROVE_OPTION) {
          File selectedFile = iconjfc.getSelectedFile();

          // Ask the user for dimensions in Width*Height format
          String sizeInput = JOptionPane.showInputDialog(
          null,
          "Enter dimensions (Width,*|xHeight):\nExample: 250*|x150",
          "Set Image Size",
          JOptionPane.QUESTION_MESSAGE
          );

          sizeInput = sizeInput.replace(",", "*");
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
              myfocus();

              // Add a trailing space after the icon for better text flow
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
          myfocus();
        } catch (Exception e) {
          statusLabel.setText("Error: Could not append ipsum; " + e.getMessage());
        }
      }
    });
    m2a.add(ipsumItem);

    JMenuItem enterEmojiItem = getMenuItem ("Convert Hex to Emoji", -1, "");
    enterEmojiItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        convertAllHexToUnicode();
        myfocus();
      }
    });
    m2a.add (enterEmojiItem);

    m2a.addSeparator();

    JMenuItem ltrItem = getMenuItem ("Left-to-Right", -1, "");
    ltrItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        txt.setComponentOrientation (ComponentOrientation.LEFT_TO_RIGHT);
      }
    });
    m2a.add (ltrItem);

    JMenuItem rtlItem = getMenuItem ("Right-to-Left", -1, "");
    rtlItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
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
        utilities.sortSelectedLines(txt, mb);
        myfocus();
      }
    });
    m2a.add(sortSelectedLinesItem);

    mb.add (m2a);

    ///////////
    JMenu m2b=getMenu ("Case");

    JMenuItem convertToAsciiItem = getMenuItem("Convert Selected to ASCII", -1, "");
    convertToAsciiItem.addActionListener(new ActionListener () {
      public void actionPerformed(ActionEvent evt) {
        statusLabel.setText("");
        String selected=txt.getSelectedText();
        if (selected==null) {
          statusLabel.setText ("There is Not Selected Text!");
          return;
        }

        if (selected.length() < 1) {
          statusLabel.setText ("There is Not Selected Text!");
          return;
        }

        String content=utilities.convertToAscii(selected);

        txt.replaceSelection(content);
        myfocus();

        return;
      }
    }
    );
    m2b.add (convertToAsciiItem);

    m2b.addSeparator();

    JMenuItem caseMagicItem = getMenuItem("Case Magic", KeyEvent.VK_8, "CTRL-8");
    caseMagicItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Get the current text selection from the text pane
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
        JRadioButton upperBtn = new JRadioButton("UPPER CASE");
        JRadioButton lowerBtn = new JRadioButton("lower case");
        JRadioButton sentenceBtn = new JRadioButton("Sentence case");
        JRadioButton reverseCaseBtn = new JRadioButton("rEVERSE cASE");
        JRadioButton firstUpperBtn = new JRadioButton("First Letters Upper");
        JRadioButton firstLowerBtn = new JRadioButton("First Letters Lower");
        JRadioButton camelBtn = new JRadioButton("camelCase");
        JRadioButton snakeBtn = new JRadioButton("snake_case");
        JRadioButton mirrorBtn = new JRadioButton("Mirror Text (Reverse)");
        JRadioButton desireBtn = new JRadioButton("Custom Replacement");

        JRadioButton[] buttons = {
          upperBtn, lowerBtn, sentenceBtn, reverseCaseBtn,
          firstUpperBtn, firstLowerBtn, camelBtn, snakeBtn, mirrorBtn, desireBtn
        };

        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < buttons.length; i++) {
          buttons[i].setFont(uiFont);
          buttons[i].setForeground(uiColor);
          group.add(buttons[i]);
        }
        upperBtn.setSelected(true); // Default action

        // Construct the message panel with HTML styling
        Object[] params = {
          "<html><div style='margin-bottom:5px;'><b style='font-size:14px; color:#2980b9;'>Transformation Magic</b><br>"
          + "<b style='font-size:14px; color:#0000ff;'>Choose your target format:</b></div></html>",
          upperBtn, lowerBtn, sentenceBtn, reverseCaseBtn,
          firstUpperBtn, firstLowerBtn, camelBtn, snakeBtn, mirrorBtn, desireBtn
        };

        // Show the dialog to the user
        int res = JOptionPane.showConfirmDialog(mb, params, "Case Magic", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {
          String result = selected;
          int len = selected.length();

          // Apply transformations using default Locale (System language)
          // to ensure "i" -> "İ" in Turkish and "i" -> "I" in English/Global Locales.
          if (upperBtn.isSelected()) {
            result = selected.toUpperCase();
          } else if (lowerBtn.isSelected()) {
            result = selected.toLowerCase();
          } else if (sentenceBtn.isSelected()) {
            // Sentence Case: Capitalize first letter, others lower (Locale sensitive)
            if (len > 0) {
              String temp = selected.toLowerCase();
              String firstChar = String.valueOf(temp.charAt(0)).toUpperCase();
              result = firstChar + temp.substring(1);
            }
          } else if (reverseCaseBtn.isSelected()) {
            // Swap Case: Logic uses String conversion to maintain Locale sensitivity for characters
            char[] chars = selected.toCharArray();
            for (int i = 0; i < chars.length; i++) {
              String s = String.valueOf(chars[i]);
              if (Character.isUpperCase(chars[i])) {
                chars[i] = s.toLowerCase().charAt(0);
              } else if (Character.isLowerCase(chars[i])) {
                chars[i] = s.toUpperCase().charAt(0);
              }
            }
            result = new String(chars);
          } else if (firstUpperBtn.isSelected()) {
            // Capitalize first letter of every word
            char[] chars = selected.toLowerCase().toCharArray();
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
              if (!found && Character.isLetter(chars[i])) {
                chars[i] = String.valueOf(chars[i]).toUpperCase().charAt(0);
                found = true;
              } else if (Character.isWhitespace(chars[i])) {
                found = false;
              }
            }
            result = new String(chars);
          } else if (firstLowerBtn.isSelected()) {
            // Lowercase first letter of every word
            char[] chars = selected.toUpperCase().toCharArray();
            boolean found = false;
            for (int i = 0; i < chars.length; i++) {
              if (!found && Character.isLetter(chars[i])) {
                chars[i] = String.valueOf(chars[i]).toLowerCase().charAt(0);
                found = true;
              } else if (Character.isWhitespace(chars[i])) {
                found = false;
              }
            }
            result = new String(chars);
          } else if (mirrorBtn.isSelected()) {
            // Standard reverse string logic
            result = new StringBuilder(selected).reverse().toString();
          } else if (snakeBtn.isSelected()) {
            // snake_case conversion: trim, lowercase (locale sensitive), and replace spaces
            result = selected.trim().toLowerCase().replaceAll("[\\s-]+", "_");
          } else if (camelBtn.isSelected()) {
            // camelCase conversion
            String[] words = selected.trim().split("[\\s_-]+");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
              String word = words[i].toLowerCase();
              if (i > 0 && word.length() > 0) {
                word = String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
              }
              sb.append(word);
            }
            result = sb.toString();
          } else if (desireBtn.isSelected()) {
            // Custom text replacement via user input
            String toReplace = JOptionPane.showInputDialog(mb,
            "<html><font color='blue'><b>Enter custom text:</b></font></html>");
            if (toReplace != null) {
              result = toReplace;
            }
          }

          // Apply the final transformation to the document selection
          txt.replaceSelection(result);
          statusLabel.setText("Case transformation applied.");
        }

        // Ensure focus returns to the editor pane
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

        input = utilities.replaceEscapeSequences(input);
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
        input = utilities.replaceEscapeSequences(input);
        StringBuilder sb = new StringBuilder(); // More efficient than StringBuilder

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

        // 5. Final Focus Management
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

        // 1. Check for Unicode format: uXXXX
        if (content.contains("\\u") || content.contains("\\U")) {
          try {
            processedWord = utilities.fromUnicode(content);
            // Using your existing professional fromUnicode method
            statusLabel.setText("Find word (from Unicode): " + processedWord);
          } catch (Exception e) {
            processedWord = content;
            statusLabel.setText("Unicode parse error! Using raw text.");
          }
        }
        // 2. Check for ASCII format -> 97;98;99
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
              StringBuilder decoded = new StringBuilder();
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
    findItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");

        String content=CONFIND;

        if (content==null) {
          statusLabel.setText ("There is Not Content!");
          return;
        }

        if (content.length () < 1) {
          statusLabel.setText ("There is Not Content!");
          return;
        }

        String metin=txt.getText ();

        if (metin==null) {
          statusLabel.setText ("There is Not Text!");
          return;
        }

        if (metin.length () < 1) {
          statusLabel.setText ("There is Not Text!");
          return;
        }

        CANKURTARAN=metin;

        int caret=txt.getCaretPosition ();

        int index = metin.indexOf (content, caret);

        if (index < 0) {
          statusLabel.setText (""+content+" --> Not Found!");
          return;
        }

        int strt=index;
        int endd=index+(content.length ());

        txt.setCaretPosition (endd);
        txt.requestFocus();
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

        final String processedPattern = utilities.escapeSequencesForRegex(patternStr);
        String safeLevel = ProductionRegexSafetyCheck.checkForLargeText(processedPattern, (long) text.length());

        if ("DANGEROUS".equals(safeLevel)) {
          String confirm = JOptionPane.showInputDialog(mb,
          "<html><body>" +
          "<font color='blue' size='5'><b>Regex Warning:</b> Potential freeze detected!</font><br>" +
          "<font color='gray' size='3'>This pattern might be slow on large text.</font><br><br>" +
          "<font color='red' size='4'>Do you want to continue? (y/e/t/yes/ok)</font>" +
          "</body></html>");

          if (confirm == null) {
            myfocus();
            return;
          }

          String input = confirm.trim().toLowerCase();

          // Original logic preserved: y-yes, e-evet, t-true, yes, ok
          boolean shouldContinue = input.equals("y") ||
          input.equals("e") ||
          input.equals("t") ||
          input.equals("yes") ||
          input.equals("ok");

          if (!shouldContinue) {
            statusLabel.setText("Search aborted by user.");
            myfocus();
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
                txt.requestFocus();
                txt.select(result.start, result.end);
                statusLabel.setText("Found: \"" + result.matchedText + "\"");
              } else {
                statusLabel.setText("No matches found.");
                JOptionPane.showMessageDialog(mb, "Pattern not found!", "Regex Find", JOptionPane.INFORMATION_MESSAGE);
                myfocus();
              }
            } catch (Exception e) {
              statusLabel.setText("Regex Error: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
          }
        };

        // --- 3. ABORT SHORTCUT ESC ---
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
    ftUpperItem.addActionListener(new FoundToUpperCaseActionListener(txt, statusLabel, mb));
    m2b.add (ftUpperItem);

    JMenuItem ftLowerItem = getMenuItem ("Found to Lower Case", KeyEvent.VK_6, "CTRL-6");
    ftLowerItem.addActionListener(new FoundToLowerCaseActionListener(txt, statusLabel, mb));
    m2b.add (ftLowerItem);

    m2b.addSeparator ();

    JMenuItem toSimplyNumsItem = getMenuItem ("Remove ExtraLines of Footnotes", -1, "");
    toSimplyNumsItem.addActionListener(new RemoveFootnoteExtraLinesActionListener(txt, statusLabel, Help.END_FN));
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
          String processedText = utilities.processBlocksSeparately(currentText);

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

        result = result.replaceAll(Help.START_FN+"\\.", "\n" + Help.START_FN + ".\n");
        result = result.replaceAll(Help.END_FN+"\\.", "\n" + Help.END_FN + ".\n");
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
    convertLatinItem.addActionListener(new LatinConverterActionListener(txt, statusLabel));
    m2b.add (convertLatinItem);

    // Create the menu item for converting text to Unicode escape sequences
    JMenuItem convertUnicode = getMenuItem("Convert To Unicode", -1, "");
    convertUnicode.addActionListener(new ConvertToUnicodeActionListener(txt, statusLabel, mb));
    m2b.add(convertUnicode);

    // Create the menu item for converting Unicode escape sequences back to characters
    JMenuItem convertFromUnicode = getMenuItem("Convert From Unicode", -1, "");
    convertFromUnicode.addActionListener(new ConvertFromUnicodeActionListener(txt, statusLabel, mb));
    m2b.add(convertFromUnicode);

    m2b.addSeparator();

    JMenuItem hexcvtItem=getMenuItem ("Convert To Hex", -1, "");
    hexcvtItem.addActionListener(new CodeURLListener(txt, statusLabel, mb));
    m2b.add (hexcvtItem);

    JMenuItem hexFcvtItem=getMenuItem ("Convert From Hex", -1, "");
    hexFcvtItem.addActionListener(new FCodeURLListener(txt, statusLabel, mb));
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

        // 2. Critical Safety: Check for null or non-existent -ghost- files
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
          // Fetch detailed attributes; Java 7+ NIO.2
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
          myfocus();
        } catch (Exception ex) {
          // Handle IO or Permission exceptions
          JOptionPane.showMessageDialog(mb, "Error retrieving metadata: " + ex.getMessage(),
          "System Error", JOptionPane.ERROR_MESSAGE);
          myfocus();
        }
      }
    });
    extraMenu.add(showFileInfoItem);

    JMenuItem saveContainsLinesItem = getMenuItem("Save Contains Lines", -1, "");
    saveContainsLinesItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        final String text = txt.getText();
        utilities.saveContainsLines(mb, text, statusLabel);
        myfocus();
      }
    });
    extraMenu.add(saveContainsLinesItem);

    extraMenu.addSeparator();

    JMenuItem wordsMeaningsItem = getMenuItem("Words Meanings", -1, "");
    wordsMeaningsItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Change cursor to wait state to give feedback
        txt.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        wordsMeaningsItem.setEnabled(false);

        // SwingWorker <ResultType, ProgressType>
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
          @Override
          protected String doInBackground() throws Exception {
            // Heavy processing happens here in a background thread
            return txt.getWordsAndMeanings();
          }

          @Override
          protected void done() {
            try {
              // Update UI only after the background work is finished
              String result = get();
              txt.setText(result);
              statusLabel.setText("Added meanings to words.");
              txt.setCaretPosition(0);
              txt.requestFocus();
            } catch (Exception ex) {
              ex.printStackTrace();
            } finally {
              // Restore UI state
              txt.setCursor(java.awt.Cursor.getDefaultCursor());
              wordsMeaningsItem.setEnabled(true);
            }
          }
        };

        // Fire the worker
        worker.execute();
      }
    });
    extraMenu.add(wordsMeaningsItem);

    extraMenu.addSeparator();

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
        myfocus();
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
          } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(mb, "Error: Selected Text is not valid Base64.");
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        } else {
          statusLabel.setText("Warning: select some text first.");
          return;
        }
        myfocus();
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
        myfocus();
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
        myfocus();
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
        myfocus();
      }
    });
    extraMenu.add(xorAllItem);

    JMenuItem xorSelectedItem = getMenuItem("XOR Encrypt Selected", -1, "");
    xorSelectedItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        statusLabel.setText("");
        processXOR(true);
        myfocus();
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
        // We do NOT use \\s+ because it would also remove newlines LF
        String cleanedContent = content.replaceAll("[ \t]+", " ");

        // 4. Trim leading/trailing spaces from each line if necessary
        // Optional: content.trim could be added here for the whole document
        String finalResult = cleanedContent.trim();

        // 5. Update the text pane with the refined text
        txt.setText(finalResult);
        myfocus();
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

        // 3. Split content by line breaks LF
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
        myfocus();
        statusLabel.setText("Trimmed lines.");
      }
    });
    extraMenu.add(trimLineItem);

    extraMenu.addSeparator();

    JMenuItem removeEmptyLinesItem = getMenuItem("Remove Empty Lines", -1, "");
    removeEmptyLinesItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        utilities.removeEmptyLines(txt, statusLabel);
        myfocus();
      }
    });
    extraMenu.add(removeEmptyLinesItem);

    extraMenu.addSeparator();

    JMenuItem wordCloudAllItem = getMenuItem("Word Analysis All", -1, "");
    wordCloudAllItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        statusLabel.setText("");
        processWordCloud(false);
        myfocus();
      }
    });
    extraMenu.add(wordCloudAllItem);

    JMenuItem wordCloudSelectedItem = getMenuItem("Word Analysis Selected", -1, "");
    wordCloudSelectedItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        statusLabel.setText("");
        processWordCloud(true);
        myfocus();
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

            // 2. Validate line number. Lines start from 1
            if (targetLine > 0 && targetLine <= lines.length) {
              int pos = 0;
              // 3. Calculate character offset for the target line
              for (int i = 0; i < targetLine - 1; i++) {
                pos += lines[i].length() + 1; // +1 for the newline character LF
              }

              // 4. Move the caret and request focus
              txt.setCaretPosition(pos);
              myfocus();
            } else {
              JOptionPane.showMessageDialog(mb, "Line number out of range!", "Error", JOptionPane.ERROR_MESSAGE);
              myfocus();
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
    File sFile = new File("npoperations/snippets.properties");
    if (sFile.exists()) {
      createSnippetMenu(mb, sFile);
    }
    /////////////
    JMenu selectedMenu = getMenu ("Selected");

    for (int i=0; i<utilities.lenf; i++) {
      JMenuItem ix = getMenuItem (utilities.families [i], -1, "");
      ix.addActionListener (new StyledEditorKit.FontFamilyAction (utilities.families [i], utilities.families [i]));
      selectedMenu.add (ix);
    }

    selectedMenu.addSeparator ();

    JMenuItem sboldItem=getMenuItem ("Bold", -1, "");
    sboldItem.addActionListener (new StyledEditorKit.BoldAction ());
    selectedMenu.add (sboldItem);

    JMenuItem sitalicItem=getMenuItem ("Italic", -1, "");
    sitalicItem.addActionListener (new StyledEditorKit.ItalicAction ());
    selectedMenu.add (sitalicItem);

    JMenuItem sunderlineItem=getMenuItem ("Underline", -1, "");
    sunderlineItem.addActionListener (new StyledEditorKit.UnderlineAction ());
    selectedMenu.add (sunderlineItem);

    selectedMenu.addSeparator ();

    for (int i = 20; i <= 26; i += 2) {
      JMenuItem ix = getMenuItem (("Font Size: " +i+""), -1, "");
      ix.addActionListener (new StyledEditorKit.FontSizeAction ("FontSize", i));
      selectedMenu.add (ix);
    }

    selectedMenu.addSeparator ();

    for (int i = 0; i < utilities.colstrs.length; i++) {
      JMenuItem ix = getMenuItem (utilities.colstrs [i], -1, "");
      ix.addActionListener (new StyledEditorKit.ForegroundAction (utilities.colstrs [i], utilities.colores [i]));
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

        myfocus();
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
          int start = txt.getSelectionStart();
          int end = txt.getSelectionEnd();
          txt.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        }

        myfocus();
      }
    });
    selectedMenu.add(bgColorPickerItem);

    JMenuItem fontPickerItem = getMenuItem("Font Picker", -1, "");
    fontPickerItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        statusLabel.setText("");
        // 1. Get all available font family names from the graphics environment
        String[] fontNames = utilities.getFontNames();

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
          myfocus();
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
        myfocus();
      }
    });
    selectedMenu.add(fontPickerItem);

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
        myfocus();
      }
    });
    selectedMenu.add(textSizePickerItem);

    selectedMenu.addSeparator();

    JMenuItem removeAttributesItem=getMenuItem ("Remove Atrs", -1, "");
    removeAttributesItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        int p0=txt.getSelectionStart ();
        int p1=txt.getSelectionEnd ();

        if (p0<0 || p1<0 || p0==p1) {
          statusLabel.setText ("No Selection!");
          return;
        }

        StyledDocument doc=txt.getStyledDocument ();

        doc.setCharacterAttributes (p0, p1-p0, new SimpleAttributeSet(), true);
        myfocus();
        return;
      }
    });
    selectedMenu.add (removeAttributesItem);

    mb.add (selectedMenu);

    mb.add(createHtmlMenu());

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

        // Determine Font Style
        String style = "Plain";
        if (txt.getFont().isBold() && txt.getFont().isItalic()) style = "Bold Italic";
        else if (txt.getFont().isBold()) style = "Bold";
        else if (txt.getFont().isItalic()) style = "Italic";

        // Get Margin Information
        java.awt.Insets m = txt.getMargin();
        String marginInfo = "T:" + m.top + " L:" + m.left + " B:" + m.bottom + " R:" + m.right;

        // Create HTML with 2x larger fonts and specific colors
        // Key: Red (#FF0000), Value: Blue #0000FF
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
        myfocus();
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
        txt.requestFocus();
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
        myfocus();
      }
    });
    paintMenu.add(itemSetGradientColors);

    // 3. Set Gradient Coordinates -> X1, Y1, X2, Y2
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

        txt.requestFocus();
      }
    });
    paintMenu.add(itemSetCoords);

    paintMenu.addSeparator();

    // 4a. Set BG Transparency Alpha Composite
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

        txt.requestFocus();
      }
    });
    paintMenu.add(itemSetAlpha);

    // 4b. Set Text Transparency Alpha Composite
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

        txt.requestFocus();
      }
    });
    paintMenu.add(itemTextSetAlpha);

    // 5. Background Image & Texture Mapping
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

        txt.requestFocus();
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
        txt.requestFocus();
      }
    });
    paintMenu.add(itemResetBg);

    mb.add(paintMenu);
    ///// END - PAINT MENU //////

    ///////////////
    JMenu countMenu=getMenu ("Count");

    JMenuItem countItem = getMenuItem ("Char Count", -1, "");

    countItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String text=txt.getText ();
        if (text==null) {
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
        myfocus();

        return;
      }
    });
    countMenu.add (countItem);

    JMenuItem selcountItem = getMenuItem ("Selected Char Count", -1, "");
    selcountItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        String text=txt.getSelectedText ();
        if (text==null) {
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
        myfocus();

        return;
      }
    });
    countMenu.add (selcountItem);

    countMenu.addSeparator();

    JMenuItem otherCountItem = getMenuItem ("Other Text Information", -1, "");
    otherCountItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        otherInformation(false);
        myfocus();
      }
    });
    countMenu.add (otherCountItem);

    JMenuItem otherSelectedCountItem = getMenuItem ("Other Selected Text Information", -1, "");
    otherSelectedCountItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        otherInformation(true);
        myfocus();
      }
    });
    countMenu.add (otherSelectedCountItem);

    countMenu.addSeparator();

    JMenuItem cwInfoItem = getMenuItem ("Specific Char|Word Count", -1, "");
    cwInfoItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        cwInformation(false);
        myfocus();
      }
    });
    countMenu.add (cwInfoItem);

    JMenuItem cwSelInfoItem = getMenuItem ("Selected Specific Char|Word Count", -1, "");
    cwSelInfoItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        cwInformation(true);
        myfocus();
      }
    });
    countMenu.add (cwSelInfoItem);

    mb.add (countMenu);
    //////////////

    JMenu colorMenu=getMenu ("Color");
    mb.add (colorMenu);

    JMenuItem bgItem=getMenuItem ("BG", KeyEvent.VK_1, "CTRL-1");
    colorMenu.add (bgItem);
    bgItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Color c = JColorChooser.showDialog(mb, "BG Color", Color.WHITE);
        if (c != null) {
          txt.setBackground (c);
        }
        myfocus();
      }
    });

    JMenuItem fgItem=getMenuItem ("FG", KeyEvent.VK_2, "CTRL-2");
    colorMenu.add (fgItem);
    fgItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Color c = JColorChooser.showDialog(mb, "FG Color", Color.BLACK);
        if (c != null) {
          txt.setForeground (c);
        }
        myfocus();
      }
    });

    JMenuItem caretColorItem=getMenuItem ("Caret", KeyEvent.VK_3, "CTRL-3");
    colorMenu.add (caretColorItem);
    caretColorItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Color c = JColorChooser.showDialog(mb, "Caret Color", Color.RED);
        if (c != null) {
          txt.setCaretColor (c);
        }
        myfocus();
      }
    });

    JMenuItem selectionColorItem=getMenuItem ("Selection", KeyEvent.VK_4, "CTRL-4");
    colorMenu.add (selectionColorItem);
    selectionColorItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Color c = JColorChooser.showDialog(mb, "Selection Color", Color.ORANGE);
        if (c != null) {
          txt.setSelectionColor (c);
        }
        myfocus();
      }
    });

    JMenu fontMenu=getMenu ("Font");
    mb.add (fontMenu);

    JMenuItem familyItem=getMenuItem ("Family", KeyEvent.VK_7, "CTRL-7");
    fontMenu.add (familyItem);
    familyItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Font f = utilities.getFamiliedFont (txt);
        if (f != null) {
          txt.setFont (f);
        }
        myfocus();
      }
    });

    JMenuItem styleItem=getMenuItem ("Style", -1, "");
    fontMenu.add (styleItem);
    styleItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Font f = utilities.getStyledFont (txt);
        if (f != null) {
          txt.setFont (f);
        }
        myfocus();
      }
    });

    JMenuItem sizeItem=getMenuItem ("Size", KeyEvent.VK_W, "CTRL-W");
    fontMenu.add (sizeItem);
    sizeItem.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        Font f = utilities.getSizedFont (txt);
        if (f != null) {
          txt.setFont (f);
        }
        myfocus();
      }
    });

    JMenu m2=getMenu ("Edit");

    JMenuItem gf=getMenuItem ("HTML~TXT", KeyEvent.VK_N, "CTRL-N");
    gf.addActionListener(new ConvertListener(txt, statusLabel, mb, utilities.getCharCode(), utilities, true));
    m2.add (gf);

    m2.addSeparator ();

    JMenuItem gfp=getMenuItem ("HTML~TXT~Plain", -1, "");
    gfp.addActionListener(new ConvertListener(txt, statusLabel, mb, utilities.getCharCode(), utilities, false));
    m2.add (gfp);

    m2.addSeparator ();

    JMenuItem resetText=getMenuItem ("Reset Text", KeyEvent.VK_E, "CTRL-E");
    resetText.addActionListener (new ActionListener () {
      public void actionPerformed (ActionEvent evt) {
        statusLabel.setText("");
        txt.setText ("");
        txt.setText (CANKURTARAN);
        txt.setCaretPosition (0);
        txt.requestFocus();
        statusLabel.setText ("Reset Text Operation is Okey!");
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

        JOptionPane.showMessageDialog(mb, scrollPane, "Regex Help", JOptionPane.INFORMATION_MESSAGE);
        myfocus();
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
    labelPanel.setPreferredSize(new Dimension(1165, 40));
    labelPanel.add(statusLabel, "Center");
    add (labelPanel, "South");
  }//end of constructor
  //##############

  private final void myfocus() {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        int pos = txt.getCaretPosition();
        int len = txt.getText().length();

        if (pos >= 0 && pos <= len) {
          txt.setCaretPosition(pos);
        } else {
          txt.setCaretPosition(0);
        }

        txt.requestFocus();
      }
    });
  }

  /**
  * Creates and attaches the Snippets menu to the menu bar.
  * Compatible with Java 6, 7, and 8. Uses anonymous inner classes.
  */
  private void createSnippetMenu(JMenuBar mb, File file) {
    // Parse the file using our custom parser
    Map snippets = SnippetsParser.parseSnippets(file);

    // If the file is empty or only contains invalid lines, don't add the menu
    if (snippets.isEmpty()) {
      return;
    }

    JMenu mainSnippetMenu = getMenu("Snippets");
    // To keep track of existing submenus (e.g., "Java", "HTML")
    Map<String, JMenu> subMenuMap = new HashMap<>();

    // Iterate through parsed snippets
    Iterator it = snippets.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry) it.next();
      String key = (String) entry.getKey();
      final String content = (String) entry.getValue();

      // Handle Category.Name structure (e.g., Java.Main)
      if (key.contains(".")) {
        int dotIndex = key.indexOf(".");
        String categoryName = key.substring(0, dotIndex).replace('_', ' ');
        String itemName = key.substring(dotIndex + 1).replace('_', ' ');

        // Get or create the submenu
        JMenu subMenu = (JMenu) subMenuMap.get(categoryName);
        if (subMenu == null) {
          subMenu = getMenu(categoryName);
          subMenuMap.put(categoryName, subMenu);
          mainSnippetMenu.add(subMenu);
        }

        addSnippetMenuItem(subMenu, itemName, content);
      } else {
        // Root level snippet
        addSnippetMenuItem(mainSnippetMenu, key.replace('_', ' '), content);
      }
    }

    mb.add(mainSnippetMenu);
  }

  /**
  * Helper to create a JMenuItem and attach the insertion logic.
  */
  private void addSnippetMenuItem(JMenu parentMenu, String label, final String content) {
    JMenuItem item = getMenuItem(label, -1, "");

    // Anonymous Inner Class for Java 6-8 compatibility
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try {
          txt.getDocument().insertString(txt.getCaretPosition(), content, null);
        } catch (BadLocationException ble) {
          ble.printStackTrace();
        }
        txt.requestFocus();
      }
    });

    parentMenu.add(item);
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
  * Compatible with Java 6+, no external dependencies
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
      String hexResult = utilities.bytesToHex(hashBytes).toLowerCase();

      JTextField resultField = new JTextField(hexResult);
      resultField.setEditable(false);
      JOptionPane.showMessageDialog(mb, resultField, algorithm + " Result", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
      JOptionPane.showMessageDialog(mb, "Error: " + ex.getMessage(), "Hash Error", JOptionPane.ERROR_MESSAGE);
    }
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
        // Satir bossa veya sadece bosluksa dogrudan ekle
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

    statusLabel.setText("Removed duplicate lines except empty lines.");
  }

  public void processWordCloud(boolean isSelected) {
    String content = isSelected ? txt.getSelectedText() : txt.getText();
    if (content == null || content.isEmpty()) return;

    // English comment: Split by non-word characters and count frequencies
    // Sadece harf olmayan -alfabetik olmayan- karakterlere göre böler
    String[] words = content.toLowerCase().split("[^\\p{L}\\p{N}]+");

    java.util.Map<String, Integer> freq = new java.util.HashMap<>();

    for (String w : words) {
      if (w.length() > 3) { // Skip short words like 'and', 'the', 'bir'
        freq.put(w, freq.getOrDefault(w, 0) + 1);
      }
    }

    // Sort by frequency and get Top 5
    String result = "<html><b>Top Keywords:</b><br>";
    // Sort logic omitted for brevity, simple list display below
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
    StringBuilder sb = new StringBuilder();

    while (matcher.find()) {
      try {
        int codePoint = Integer.parseInt(matcher.group(1), 16);

        // Unicode max kontrol; maksimum Unicode degeri = 0x10FFFF
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

    return;
  }

  private final void prettyFormatXML() {
    String input = txt.getText();
    if (input == null || input.trim().length() < 3) {
      statusLabel.setText("Insufficient text for XML formatting...");
      return;
    }

    try {
      // Step 1: Clean existing messy formatting
      String cleanXml = input.replaceAll(">[\\s\\r\\n]+<", "><")
      .replaceAll("\\r|\\n", " ")
      .replaceAll("\\s+", " ")
      .trim();

      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setIgnoringElementContentWhitespace(true);
      InputSource is = new InputSource(new StringReader(cleanXml));

      // Fully qualified to avoid JTextPane Document conflict
      org.w3c.dom.Document doc = dbf.newDocumentBuilder().parse(is);
      doc.getDocumentElement().normalize();

      // Step 2: Transformer for basic structural indentation
      TransformerFactory tf = TransformerFactory.newInstance();
      try { tf.setAttribute("indent-number", 4); } catch (Exception e) {}

      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.METHOD, "xml");
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty("{http://apache.org}indent-amount", "4");

      StringWriter sw = new StringWriter();
      transformer.transform(new DOMSource(doc), new StreamResult(sw));

      // Step 3: Advanced Post-Processing for "Pearl-Alignment"
      String rawResult = sw.toString().replace(" standalone=\"no\"", "");
      String[] lines = rawResult.split("\n");
      StringBuilder finalResult = new StringBuilder();

      for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        if (line.trim().length() == 0) continue;

        // Don't wrap attributes in the root tag -xmlns line- or comments
        if (line.contains("android:") &&
        !line.contains("xmlns:android") && !line.contains("<!--")) {
          // Find tag start to calculate base indentation
          int tagStart = line.indexOf("<") + 1;
          int spacePos = line.indexOf(" ", tagStart);

          if (spacePos != -1) {
            String baseIndent = "";
            // Create an indent that aligns with the start of the first attribute
            for (int j = 0; j < spacePos + 1; j++) baseIndent += " ";

            // Wrap attributes and also catch 'package' if it exists
            String formattedLine = line.replaceAll(" (android:|package=)", "\n" + baseIndent + "$1");
            finalResult.append(formattedLine).append("\n");
          } else {
            finalResult.append(line).append("\n");
          }
        } else {
          finalResult.append(line).append("\n");
        }
      }

      txt.setText(finalResult.toString().trim());
      txt.setCaretPosition(0);
      statusLabel.setText("XML Pearl-Formatting complete (Root Protected).");

    } catch (Exception e) {
      statusLabel.setText("Formatting Error: " + e.getMessage());
      e.printStackTrace();
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
    String mesaj = "<html><body><font color=\"#0000DD\" size=\"5\">" + cw + " occurs " + cwTimes + " times in this text.</font></body></html>";
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

  private JMenu getMenu(String str) {
    final JMenu myMenu = new JMenu(str);

    // MenuBar ile uyumlu renkler
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

  /////
  private JMenu createHtmlMenu() {
    JMenu htmlMenu = getMenu("HTML");

    // Font Family Items
    htmlMenu.add(getFontItem("Arial"));
    htmlMenu.add(getFontItem("Verdana"));
    htmlMenu.add(getFontItem("Courier New"));
    htmlMenu.add(getFontItem("Times New Roman"));

    htmlMenu.addSeparator();

    // Style Items
    htmlMenu.add(getStyleItem("Bold", "b"));
    htmlMenu.add(getStyleItem("Italic", "i"));
    htmlMenu.add(getStyleItem("Underline", "u"));

    htmlMenu.addSeparator();

    // Color Items
    htmlMenu.add(getColorItem("Black", "black"));
    htmlMenu.add(getColorItem("Red", "red"));
    htmlMenu.add(getColorItem("Green", "green"));
    htmlMenu.add(getColorItem("Blue", "blue"));
    htmlMenu.add(getColorItem("Orange", "orange"));

    htmlMenu.addSeparator();

    // Size Items
    for (int i = 2; i <= 6; i++) {
      htmlMenu.add(getSizeItem(String.valueOf(i)));
    }

    return htmlMenu;
  }

  private JMenuItem getFontItem(final String name) {
    JMenuItem item = getMenuItem(name, -1, "");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wrapSelection("<font face=\"" + name + "\">", "</font>");
      }
    });
    return item;
  }

  private JMenuItem getStyleItem(String label, final String tag) {
    JMenuItem item = getMenuItem(label, -1, "");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wrapSelection("<" + tag + ">", "</" + tag + ">");
      }
    });
    return item;
  }

  private JMenuItem getColorItem(String label, final String color) {
    JMenuItem item = getMenuItem(label, -1, "");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wrapSelection("<font color=\"" + color + "\">", "</font>");
      }
    });
    return item;
  }

  private JMenuItem getSizeItem(final String size) {
    JMenuItem item = getMenuItem("Size " + size, -1, "");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        wrapSelection("<font size=\"" + size + "\">", "</font>");
      }
    });
    return item;
  }

  /**
  * Wraps the selected text with given HTML tags.
  * If no text is selected, inserts a formatted warning message.
  */
  private void wrapSelection(String prefix, String suffix) {
    String selectedText = txt.getSelectedText();

    if (selectedText == null || selectedText.trim().length() == 0) {
      // Warning for no selection: Blue color and Size 5
      String warning = "<font color=\"blue\" size=\"5\">Select at least one character please...</font>";
      txt.replaceSelection(warning);
    } else {
      // Wrap selected text with tags
      txt.replaceSelection(prefix + selectedText + suffix);
    }
  }

  private static final void ekran() {
    try {
      // Define custom font resources
      final javax.swing.plaf.FontUIResource resourceA =
      new javax.swing.plaf.FontUIResource("SansSerif", Font.BOLD, 14);
      final javax.swing.plaf.FontUIResource resourceB =
      new javax.swing.plaf.FontUIResource("SansSerif", Font.BOLD, 20);
      final javax.swing.plaf.FontUIResource resourceC =
      new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, 13);

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
    fr.setSize(1165, 620); // Forces specific dimensions
    fr.setResizable(true);
    fr.setLocationRelativeTo(null); // Centers the window on screen

    // 6. Make the frame visible
    fr.setVisible(true);
  }

  public static final void main (String [] args) {
    Utilities utl = new Utilities();
    utl.createSnippetsFile();
    utl.createTestFile();
    utl.createExampleXMLFile();
    utl.createWordsFile();

    javax.swing.SwingUtilities.invokeLater (new Runnable () {
      public void run () {
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

jar cmf MANIFEST.MF bin/notdefteri_110426.jar -C obj net -C . src compile_run.txt xcopy.sh ControlChanges.* changes.txt

rm -rf obj/net

java -jar bin/notdefteri_110426.jar &
*/
