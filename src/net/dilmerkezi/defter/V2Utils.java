package net.dilmerkezi.defter;

import java.io.*;

final
public class V2Utils
extends Object
implements Serializable
{
  
  private static boolean tr=false;
  private static boolean isBorrowNums=true;
  
  private V2Utils ()
  {
    super ();
  }
  
  public String toString ()
  {
    return "";
  }
  
  final
  public static void setBorrowNums (boolean isb)
  {
    isBorrowNums=isb;
  }
  
  final
  public static boolean getBorrowNums ()
  {
    return isBorrowNums;
  }
  
  final
  public static void setTR (boolean isb)
  {
    tr=isb;
  }
  
  final
  public static String getBeautifiedText (String m)
  {
    if (m==null)
    {
      return ("Null Text for Beutify Operation!");
    }
    
    if (m.length () < 1)
    {
      return ("Zero Length Text for Beautify Operation!");
    }
    
    String metin ="";
    final int totalnum=83;
    int syc=0;
    
    String moti="ERRORED!";
    
    try
    {
      //m=m.replaceAll ("\t", " ");
      //m=m.replaceAll ("  ", " ");
      m=m.replaceAll (Character.toString ((char)(194)), "");
      
      //m=m.replaceAll ("-\n", "");
      
      //      String metin_01=m;
      //m=m.replaceAll ("  ", " ");
      //      System.out.println ("\n"+(++syc)+"/"+totalnum+" -> Successfull!");
      
      //      String metin_02=metin_01.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      String metin_03=metin_02.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      String metin_04=metin_03.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      String metin_05=metin_04.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      String metin_06=metin_05.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      //      String metin_07=metin_06.replaceAll ("  ", " ");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      //String metin_08=metin_07.replaceAll ("\\*", "~");
      metin=m.replaceAll ("\\* ", "~ ");
      //      metin=metin.replaceAll ("\\*\\* ", "~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\* ", "~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\* ", "~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\* ", "~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\* ", "~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\*\\* ", "~~~~~~~~~~~~ ");
      //      metin=metin.replaceAll ("\\*", "~");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_09=m.replaceAll ("�", "<<");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_10=metin_09.replaceAll ("�", ">>");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_11=metin_10.replaceAll ("`", "\'");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_12=metin_11.replaceAll (Character.toString ((char)(95)), "-");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_13=metin_12.replaceAll (Character.toString ((char)(127)), " ");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_14=metin_13.replaceAll (Character.toString ((char)(160)), " ");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_15=metin_14.replaceAll (Character.toString ((char)(166)), ":");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_16=metin_15.replaceAll (Character.toString ((char)(168)), "~");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_17=metin_16.replaceAll (Character.toString ((char)(170)), "a");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_18=metin_17.replaceAll (Character.toString ((char)(173)), "-");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_19=metin_18.replaceAll (Character.toString ((char)(175)), "~");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_20=metin_19.replaceAll (Character.toString ((char)(176)), "o:");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_21=metin_20.replaceAll (Character.toString ((char)(180)), "\'");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_22=metin_21.replaceAll (Character.toString ((char)(182)), "~");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_23=metin_22.replaceAll (Character.toString ((char)(183)), " ");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_24=metin_23.replaceAll (Character.toString ((char)(184)), " ");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_25=metin_24.replaceAll (Character.toString ((char)(186)), "o:");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_25=metin_25.replaceAll (Character.toString ((char)(171)), "\"");
      metin_25=metin_25.replaceAll (Character.toString ((char)(187)), "\"");
      
      String metin_26=metin_25.replaceAll (Character.toString ((char)(247)), "/");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_26=metin_26.replaceAll (Character.toString ((char)(8210)), "-");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_27=metin_26.replaceAll (Character.toString ((char)(8211)), "-");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_28=metin_27.replaceAll (Character.toString ((char)(8212)), "-");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_29=metin_28.replaceAll (Character.toString ((char)(8213)), "--");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_30=metin_29.replaceAll (Character.toString ((char)(8214)), "");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_30=metin_30.replaceAll (Character.toString ((char)(8215)), "=");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_31=metin_30.replaceAll (Character.toString ((char)(8216)), "\'");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_32=metin_31.replaceAll (Character.toString ((char)(8217)), "\'");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_32=metin_32.replaceAll (Character.toString ((char)(8218)), ",");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_32=metin_32.replaceAll (Character.toString ((char)(8219)), "/");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_33=metin_32.replaceAll (Character.toString ((char)(8220)), "\"");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_34=metin_33.replaceAll (Character.toString ((char)(8221)), "\"");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_34=metin_34.replaceAll (Character.toString ((char)(8222)), ",");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_34=metin_34.replaceAll (Character.toString ((char)(8223)), "\"");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_35=metin_34.replaceAll (Character.toString ((char)(8224)), "+");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin_35=metin_35.replaceAll (Character.toString ((char)(8225)), "+");//ILAVE
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_36=metin_35.replaceAll (Character.toString ((char)(8226)), "--");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_37=metin_36.replaceAll (Character.toString ((char)(8230)), "...");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_38=metin_37.replaceAll (Character.toString ((char)(8592)), "<--");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_39=metin_38.replaceAll (Character.toString ((char)(8594)), "-->");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_40=metin_39.replaceAll (Character.toString ((char)(9474)), "-");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_41=metin_40.replaceAll (Character.toString ((char)(61694)), "~");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_42=metin_41.replaceAll (Character.toString ((char)(65533)), " ");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_43=metin_42.replaceAll (" \\.", ".");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_44=metin_43.replaceAll (" ,", ",");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_45=metin_44.replaceAll (" !", "!");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_46=metin_45.replaceAll (" :", ":");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_47=metin_46.replaceAll (" ;", ";");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_48=metin_47.replaceAll (" \\?", "?");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_49=metin_48.replaceAll (" \n", "\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_50=metin_49.replaceAll ("\n ", "\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_51=metin_50;
      //metin_50.replaceAll ("-\n", "");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_52=metin_51.replaceAll ("\\(\n", "");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_53=metin_52.replaceAll ("\\.\n", ".\n\n\n\n\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_54=metin_53.replaceAll ("!\n", "!\n\n\n\n\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_55=metin_54.replaceAll ("\\?\n", "?\n\n\n\n\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_56=metin_55.replaceAll ("\\)\n", ")\n\n\n\n\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_57=metin_56.replaceAll ("\n\n\n", "\n\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      for (int i=0; i<20; i++)
      {
        metin_57=metin_57.replaceAll ("\n\n\n", "\n\n");
        System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      }
      
      String metin_67=replacePageNums (metin_57);
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_68=getDesignedText (metin_67);
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_69=metin_68.replaceAll (" \n", "\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      String metin_70=metin_69.replaceAll ("\n ", "\n");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      metin=metin_70;
      
      //      metin=metin.replaceAll ("-\n", "");
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      for (int i=0; i<10; i++)
      {
        metin=metin.replaceAll ("\n\n\n", "\n\n");
      }
      System.out.println ("Replaced LN-LN-LN with LN-LN.");
      
      ////////////////////////////
      ////////////////////////////
      //      String orig="";
      //
      //      for (int i=300; i>0; i--)
      //      {
      //        orig=("\\."+Integer.toString (i)+" "); //Ex: Boyle oldu.78
      //        metin=metin.replaceAll (orig, ".("+i+") ");
      //      }
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      for (int i=200; i>0; i--)
      //      {
      //        orig=("\""+Integer.toString (i)+" ");//Ex: "78
      //        metin=metin.replaceAll (orig, "\"("+i+") ");
      //      }
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      for (int i=200; i>0; i--)
      //      {
      //        orig=("\'"+Integer.toString (i)+" ");//Ex: '78
      //        metin=metin.replaceAll (orig, "\'("+i+") ");
      //      }
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      for (int i=200; i>0; i--)
      //      {
      //        metin=metin.replaceAll (" \\["+Integer.toString (i)+"\\] ", " ");
      //      }
      //      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      //
      //      System.out.println ("Cleaned Footnote indicators until 200.");
      
      ///////////////////////////////
      //////////////////////////////
      
      moti=callavi (metin);
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      //      metin=metin.replaceAll ("- ", "");
      System.out.println (""+(++syc)+"/"+totalnum+" -> Successfull!");
      
      System.out.println ("Beautify Text Operation is Okey!");
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return "ERROR!";
    }
    
    return moti;
  }//method end getBeautifiedText
  
  final
  private static boolean isDigit (String m)
  {
    if (m == null) return false;
    
    if (m.length () < 1) return false;
    
    char [] chars=m.toCharArray ();
    int clen=chars.length;
    
    boolean digit=true;
    
    for (int j=0; j<clen; j++)
    {
      char c=chars [j];
      
      boolean whyi=(Character.isDigit (c));
      if (whyi == false)
      {
        digit=false;
        break;
      }
    }
    
    return digit;
  }
  
  final
  private static int ncount (String wrd)
  {
    char [] chars=wrd.toCharArray ();
    final int clen=chars.length;
    
    int syc=0x0000;
    
    char ch='a';
    
    final char PUNTO='.';
    
    for (int i=0; i<clen; i++)
    {
      ch=chars [i];
      if (ch == PUNTO)
      {
        ++syc;
      }
    }
    
    return syc;
  }
  
  final
  private static String silNums (String m)
  {
    if (m == null) return m;
    
    m=m.trim ();
    
    int mlen=m.length ();
    
    if (mlen < 1) return m;
    
    String [] words=m.split (" ");
    if (words == null) return m;
    
    int wlen=words.length;
    if (wlen < 1)
    {
      words=new String [0x0001];
      words [0x0000]=m;
    }
    
    wlen=words.length;
    
    StringBuffer sb=new StringBuffer ();
    
    String word="";
    
    for (int i=0; i<wlen; i++)
    {
      word=(words [i]).trim ();
      mlen=word.length ();
      
      if (mlen < 1) continue;
      
      if (isDigit (word))
      {
        sb.append (word);
        sb.append (" ");
        continue;
      }
      
      char endChar=word.charAt (mlen-1);
      
      //	if ( (Character.isDigit (endChar)) == false )
      //	{
      //	    sb.append (word);
      //	    sb.append (" ");
      //	    continue;
      //	}
      
      int nc=ncount (word);
      int ucin=word.indexOf ("...");
      
      if ( (ucin < 0x0000) && (nc > 0x0001) )
      {
        sb.append (word);
        sb.append (" ");
        continue;
      }
      
      if (
        ((word.indexOf ("/")) >= 0x0000) ||
        ((word.indexOf ("-")) >= 0x0000) ||
        ((word.indexOf ("\'")) >= 0x0000) ||
      ((word.indexOf (":")) >= 0x0000) )
      {
        sb.append (word);
        sb.append (" ");
        continue;
      }
      
      StringBuffer ynb=new StringBuffer ();
      
      char [] chars=word.toCharArray ();
      int clen=chars.length;
      
      for (int j=0; j<clen; j++)
      {
        char c=chars [j];
        if (Character.isDigit (c)) continue;
        ynb.append (c);
      }
      
      word=ynb.toString ();
      word=word.replaceAll ("\\(\\)", "");
      
      sb.append (word);
      sb.append (" ");
    }
    
    String line=sb.toString ();
    
    line=line.trim ();
    
    return line;
  }
  
  final
  public static String ygetBeautifiedText (String m)
  {
    if (m == null)
    {
      return ("Null Text for Beutify Operation!");
    }
    
    if (m.length () < 1)
    {
      return ("Zero Length Text for Beautify Operation!");
    }
    
    String metin ="";
    StringBuffer sb=new StringBuffer ();
    
    String moti="ERRORED!";
    
    try
    {
      Reader sr=new StringReader (m);
      BufferedReader br=new BufferedReader (sr);
      
      String line=null;
      String left=" ";
      
      while ( (line=br.readLine ()) != null)
      {
        line=line.trim ();
        int len=line.length ();
        if (len < 1) continue;
        
        if (left.equals ("V"))
        {
          if (Character.isUpperCase (line.charAt (0)) ||
          line.charAt (0)=='\"')
          {
            sb.append ("\n\n");
          }
        }
        
        if (isBorrowNums)
        {
          line=silNums (line);
        }
        
        sb.append (line);
        
        len=line.length ();
        if (len < 1) continue;
        
        char ultimo=line.charAt (len-1);
        
        if (ultimo == '.' /*|| ultimo == ':'*/ ||
          ultimo == ';' || ultimo == '!' ||
        ultimo == '?')
        {
          sb.append ("\n\n");
          continue;
        }
        
        int ult=(int)(ultimo);
        
        if (ult > 64)
        {
          sb.append (" ");
          left="V";
        }
        else
        {
          sb.append (" ");
          left="Y";
        }
        
      }//end while
      
      metin=sb.toString ();
      
      metin=metin.replaceAll ("\"\"", "\"\n\n\"");
      metin=metin.replaceAll ("\" \"", "\"\n\n\"");
      
      //metin=metin.replaceAll (":", ":\n\n");
      //metin=metin.replaceAll (";", ";\n\n");
      
      for (int i=0; i<8; i++)
      {
        metin=metin.replaceAll ("\n\n\n", "\n\n");
      }
      
      metin=metin.replaceAll ("\n ", "\n");
      //---
      metin=metin.replaceAll (" b ", " b");
      metin=metin.replaceAll (" c ", " c");
      metin=metin.replaceAll (" d ", " d");
      metin=metin.replaceAll (" f ", " f");
      metin=metin.replaceAll (" g ", " g");
      metin=metin.replaceAll (" h ", " h");
      metin=metin.replaceAll (" j ", " j");
      metin=metin.replaceAll (" k ", " k");
      metin=metin.replaceAll (" l ", " l");
      metin=metin.replaceAll (" m ", " m");
      metin=metin.replaceAll (" n ", " n");
      metin=metin.replaceAll (" p ", " p");
      metin=metin.replaceAll (" r ", " r");
      metin=metin.replaceAll (" s ", " s");
      metin=metin.replaceAll (" t ", " t");
      metin=metin.replaceAll (" v ", " v");
      metin=metin.replaceAll (" z ", " z");
      metin=metin.replaceAll (" w ", " w");
      metin=metin.replaceAll (" q ", " q");
      metin=metin.replaceAll (" \u00E7 ", " \u00E7");
      metin=metin.replaceAll (" \u011F ", " \u011F");
      metin=metin.replaceAll (" \u00F6 ", " \u00F6");
      metin=metin.replaceAll (" \u015F ", " \u015F");
      metin=metin.replaceAll (" \u00FC ", " \u00FC");
      //----
      metin=metin.replaceAll (" B ", " B");
      metin=metin.replaceAll (" C ", " C");
      metin=metin.replaceAll (" D ", " D");
      metin=metin.replaceAll (" F ", " F");
      metin=metin.replaceAll (" G ", " G");
      metin=metin.replaceAll (" H ", " H");
      metin=metin.replaceAll (" J ", " J");
      metin=metin.replaceAll (" K ", " K");
      metin=metin.replaceAll (" L ", " L");
      metin=metin.replaceAll (" M ", " M");
      metin=metin.replaceAll (" N ", " N");
      metin=metin.replaceAll (" P ", " P");
      metin=metin.replaceAll (" R ", " R");
      metin=metin.replaceAll (" S ", " S");
      metin=metin.replaceAll (" T ", " T");
      metin=metin.replaceAll (" V ", " V");
      metin=metin.replaceAll (" Z ", " Z");
      metin=metin.replaceAll (" W ", " W");
      metin=metin.replaceAll (" Q ", " Q");
      metin=metin.replaceAll (" \u00C7 ", " \u00C7");
      metin=metin.replaceAll (" \u011E ", " \u011E");
      metin=metin.replaceAll (" \u00D6 ", " \u00D6");
      metin=metin.replaceAll (" \u015E ", " \u015E");
      metin=metin.replaceAll (" \u00DC ", " \u00DC");
      //----
      
      if (tr)
      {
        metin=metin.replaceAll (" a ", " a");
        metin=metin.replaceAll (" e ", " e");
        metin=metin.replaceAll (" i ", " i");
        //metin=metin.replaceAll (" o ", " o");
        metin=metin.replaceAll (" u ", " u");
        metin=metin.replaceAll (" y ", " y");
        //metin=metin.replaceAll (" x ", " x");
        metin=metin.replaceAll (" \u0131 ", " \u0131");
        
        metin=metin.replaceAll (" A ", " A");
        metin=metin.replaceAll (" E ", " E");
        metin=metin.replaceAll (" I ", " I");
        //metin=metin.replaceAll (" O ", " O");
        metin=metin.replaceAll (" U ", " U");
        metin=metin.replaceAll (" Y ", " Y");
        //metin=metin.replaceAll (" X ", " X");
        metin=metin.replaceAll (" \u0130 ", " \u0130");
      }
      
      metin=metin.replaceAll (": -", ":\n\n-");
      
      System.out.println ("XBeautify Text Operation is Okey!");
      
      br.close ();
      sr.close ();
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return moti;
    }
    
    //    metin=metin.replaceAll ("- ", "");
    
    return metin;
  }//method end getBeautifiedText
  
  final
  private static String callavi (String mm)
  {
    if (mm==null)
    {
      return("Null Text For!");
    }
    
    final int len=mm.length ();
    
    if (len < 3)
    {
      return ("VERY SHORT LENGTH TEXT!");
    }
    
    final StringBuffer metno=new StringBuffer ("   \n");
    metno.append (mm);
    metno.append ("   \n");
    
    final String rmetin=metno.toString ();
    
    StringBuilder metin=new StringBuilder (rmetin);
    
    char chr=',';
    char cnt=',';
    
    char dst=',';
    
    boolean isUpper=false;
    boolean isDigit=false;
    boolean isLetter=true;
    
    final char NL = (char)(10);
    final char BOSLUK=(char)(32);
    
    for (int i=4; i<len; i++)
    {
      chr=metin.charAt (i);
      cnt=metin.charAt (i-1);
      
      if (cnt != NL) continue;
      
      if (chr==NL) continue;
      
      isUpper=false;
      isDigit=false;
      isLetter=true;
      
      isUpper=Character.isUpperCase (chr);
      if (isUpper==true) continue;
      
      isDigit=Character.isDigit (chr);
      if (isDigit==true) continue;
      
      isLetter=Character.isLetter (chr);
      if (isLetter==false) continue;
      
      metin.setCharAt (i-1, BOSLUK);
      metin.setCharAt (i-2, BOSLUK);
      
      System.out.println ("Changed --> "+Character.toString (chr)+"");
      
      isUpper=false;
      isDigit=false;
      isLetter=true;
    }
    
    String res=metin.toString ();
    
    res=res.replaceAll ("   \n", "");
    res=res.replaceAll ("  ", " ");
    res=res.replaceAll ("  ", " ");
    
    System.out.println ("Corrected Unconvenient Lines Successfully!!");
    
    return res;
  }//end method callavi
  
  final
  private static String replacePageNums (String str)
  {
    if (str==null) return " ";
    
    final int BARS=750;
    
    int strlen=str.length ();
    if (strlen<BARS) return str;
    
    final int SFNUM=strlen/BARS;
    
    for (int i=1; i<=SFNUM; i++)
    {
      str=str.replaceAll (("\n"+Integer.toString (i)+"\n\n"), ("\n"));
      str=str.replaceAll (("\n\n"+Integer.toString (i)+"\n"), ("\n"));
      str=str.replaceAll (("\n"+Integer.toString (i)+"\n"), ("\n"));
    }
    
    return str;
  }
  
  final
  private static boolean sfnum (String xstr)
  {
    boolean issf=true;
    
    String str=xstr.trim ();
    str=xstr.replaceAll (" ", "");
    
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
  
  final
  public static String replace (String old)
  {
    old = old.replaceAll ("&#287;", "\u011F");
    old = old.replaceAll ("&#286;", "\u011E");
    
    old = old.replaceAll ("&#305;", "\u0131");
    old = old.replaceAll ("&#304;", "\u0130");
    
    old = old.replaceAll ("&#351;", "\u015F");
    old = old.replaceAll ("&#350;", "\u015E");
    
    old = old.replaceAll ("&laquo;", "<<");
    old = old.replaceAll ("&nbsp;", " ");
    
    return old;
  }
  
  final
  private static String getDesignedText (String oldi)
  {
    String m=oldi;
    StringBuffer sb=new StringBuffer ("");
    
    try
    {
      StringReader sr=new StringReader (m);
      BufferedReader br=new BufferedReader (sr);
      
      String line=null;
      char chrm=',';
      int linelen=0;
      
      while ( (line=br.readLine ()) != null )
      {
        line=line.trim ();
        if (line.equals ("") || line.length()<1)
        {
          sb.append ("\n\n");
          continue;
        }
        
        if (sfnum (line)) continue;
        
        linelen=line.length ();
        
        if (linelen <= 72)
        {
          char chm=line.charAt (0);
          if (Character.isUpperCase (chm))
          {
            line=(line+"\n\n");
          }
        }
        
        chrm=line.charAt (linelen-1);
        
        if (Character.isUpperCase (chrm))
        {
          sb.append (line);
          sb.append ("\n\n");
          continue;
        }
        
        sb.append (line);
        sb.append (" ");
      }
      
      br.close ();
      sr.close ();
    }
    catch (Exception e)
    {
      e.printStackTrace ();
      return " ";
    }
    
    sb.append (" ");
    
    return sb.toString ();
  }
  
}//class end
