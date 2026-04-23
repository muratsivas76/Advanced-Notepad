package net.dilmerkezi.defter;

import java.io.*;

final
public class V2UTFToISO
extends Object
implements Serializable
{
  
  private static boolean isTR=false;
  
  final private static String VESTR="ve";
  final private static String KISTR="ki";
  final private static String DESTR="de";
  final private static String DASTR="da";
  final private static String ILESTR="ile";
  final private static String ILASTR="ila";
  
  private V2UTFToISO ()
  {
    super ();
  }
  
  public String toString ()
  {
    return "";
  }
  
  final
  public static String cnt (String src)
  throws IOException
  {
    Reader isr=new StringReader (src);
    BufferedReader br=new BufferedReader (isr);
    
    String line=null;
    
    StringBuffer sbr=new StringBuffer ();
    
    String moline="";
    final String TIRE="-";
    final String YYU=Character.toString ((char)(173));
    final int CERO=0x0000;
    final int UNO=0x0001;
    
    final String NOKTA=".";
    final String UNLEM="!";
    final String SORU="?";
    //final String TIRNAK="\"";
    
    while ( (line=br.readLine ()) != null )
    {
      moline=line.trim ();
      moline=moline.replaceAll ("\t", " ");
      moline=moline.trim ();
      
      if (moline.length () < 1) sbr.append ("\n");
      
      for (int j=0; j<12; j++)
      {
        moline=moline.replaceAll ("  ", " ");
      }
      
      if (sfnum (moline)) continue;
      
      if (moline.endsWith (TIRE) || moline.endsWith (YYU))
      {
        line=moline.substring (CERO, moline.length ()-UNO);
        sbr.append (line);
      }
      else
      {
        sbr.append (moline);
        
        if (moline.endsWith (NOKTA) ||
          moline.endsWith (UNLEM) ||
          moline.endsWith (SORU)  ||
          isFirstUpper (moline)   ||
          isRomen (moline)
        )
        {
          sbr.append ("\n");
        }
        else
        {
          sbr.append (" ");
        }
      }
    }
    
    br.close ();
    isr.close ();
    
    String text=sbr.toString ();
    
    V2Utils.setTR (false);
    
    String mext=V2Utils.getBeautifiedText (text);
    mext=V2Utils.ygetBeautifiedText (mext);
    
    mext=mext.replaceAll (" \n", "\n");
    
    mext=mext.replaceAll ("\\. ", ".");
    mext=mext.replaceAll ("\\.", ". ");
    
    mext=mext.replaceAll ("\\? ", "?");
    mext=mext.replaceAll ("\\?", "? ");
    
    mext=mext.replaceAll (", ", ",");
    mext=mext.replaceAll (",", ", ");
    
    mext=mext.replaceAll (" \n", "\n");
    
    mext=mext.replaceAll ("\"\n", "\"\n\n");
    
    mext=getTirnaked (mext);
    
    mext=mext.replaceAll ("\\. \\. \\.", "...");
    
    mext=mext.replaceAll ("\n\n\n", "\n\n");
    
    return mext;
  }
  
  final
  private static String getTirnaked (String metinx)
  {
    String metin=metinx;
    
    metin=metin.replaceAll ("\" A", "\"\n\nA");
    metin=metin.replaceAll ("\" E", "\"\n\nE");
    metin=metin.replaceAll ("\" I", "\"\n\nI");
    metin=metin.replaceAll ("\" \u0130", "\"\n\n\u0130");
    metin=metin.replaceAll ("\" O", "\"\n\nO");
    metin=metin.replaceAll ("\" \u00D6", "\"\n\n\u00D6");
    metin=metin.replaceAll ("\" U", "\"\n\nU");
    metin=metin.replaceAll ("\" \u00DC", "\"\n\n\u00DC");
    
    metin=metin.replaceAll ("\" B", "\"\n\nB");
    metin=metin.replaceAll ("\" C", "\"\n\nC");
    metin=metin.replaceAll ("\" \u00C7", "\"\n\n\u00C7");
    metin=metin.replaceAll ("\" D", "\"\n\nD");
    metin=metin.replaceAll ("\" F", "\"\n\nF");
    metin=metin.replaceAll ("\" G", "\"\n\nG");
    metin=metin.replaceAll ("\" \u011E", "\"\n\n\u011E");
    metin=metin.replaceAll ("\" H", "\"\n\nH");
    metin=metin.replaceAll ("\" J", "\"\n\nJ");
    metin=metin.replaceAll ("\" K", "\"\n\nK");
    metin=metin.replaceAll ("\" L", "\"\n\nL");
    metin=metin.replaceAll ("\" M", "\"\n\nM");
    metin=metin.replaceAll ("\" N", "\"\n\nN");
    metin=metin.replaceAll ("\" P", "\"\n\nP");
    metin=metin.replaceAll ("\" R", "\"\n\nR");
    metin=metin.replaceAll ("\" S", "\"\n\nS");
    metin=metin.replaceAll ("\" \u015E", "\"\n\n\u015E");
    metin=metin.replaceAll ("\" T", "\"\n\nT");
    metin=metin.replaceAll ("\" V", "\"\n\nV");
    metin=metin.replaceAll ("\" Z", "\"\n\nZ");
    metin=metin.replaceAll ("\" W", "\"\n\nW");
    metin=metin.replaceAll ("\" Q", "\"\n\nQ");
    
    metin=metin.replaceAll ("Hz\\.\n\n", "Hz. ");
    
    return metin;
  }
  
  final
  private static boolean isTotalUpper (String xstr)
  {
    boolean issf=true;
    
    String str=xstr;//.trim ();
    //str=xstr.replaceAll (" ", "");
    
    if (str == null)
    {
      issf=false;
      return issf;
    }
    
    int strlen=str.length ();
    if (strlen < 1)
    {
      issf=false;
      return issf;
    }
    
    char ch = '*';
    //int chm=0;
    
    for (int i=0; i<strlen; i++)
    {
      ch=str.charAt (i);
      if (Character.isLowerCase (ch))
      {
        issf=false;
        break;
      }
    }
    
    return issf;
  }
  
  final
  private static boolean isFirstUpper (String xstr)
  {
    boolean issf=true;
    
    String str=xstr;//.trim ();
    //str=xstr.replaceAll (" ", "");
    
    if (str == null)
    {
      issf=false;
      return issf;
    }
    
    int strlen=str.length ();
    if (strlen < 1)
    {
      issf=false;
      return issf;
    }
    
    char ch = '*';
    
    if (str.indexOf (" ") < 0)
    {
      ch=str.charAt (0);
      if (Character.isLowerCase (ch))
      {
        issf=false;
        return issf;
      }
    }
    
    //int chm=0;
    
    String [] words=str.split (" ");
    if (words == null)
    {
      issf=false;
      return issf;
    }
    
    strlen=words.length;
    if (strlen < 1)
    {
      issf=false;
      return issf;
    }
    
    issf=true;
    
    String word="";
    
    for (int i=0; i<strlen; i++)
    {
      word=(words [i]).trim ();
      
      if (word.equalsIgnoreCase (VESTR)  ||
        word.equalsIgnoreCase (KISTR)  ||
        word.equalsIgnoreCase (DESTR)  ||
        word.equalsIgnoreCase (DASTR)  ||
        word.equalsIgnoreCase (ILESTR) ||
      word.equalsIgnoreCase (ILASTR))
      {
        continue;
      }
      
      ch=word.charAt (0);
      if (Character.isLowerCase (ch))
      {
        issf=false;
        break;
      }
    }
    
    return issf;
  }
  
  final
  private static boolean isRomen (String xstr)
  {
    boolean issf=false;
    
    String str=xstr.trim ();
    str=xstr.replaceAll (" ", "");
    str=str.toUpperCase ();
    
    if (str.equals ("I")        ||
      str.equals ("II")       ||
      str.equals ("III")      ||
      str.equals ("IV")       ||
      str.equals ("V")        ||
      str.equals ("VI")       ||
      str.equals ("VII")      ||
      str.equals ("VIII")     ||
      str.equals ("IX")       ||
      str.equals ("X")        ||
      
      str.equals ("XI")       ||
      str.equals ("XII")      ||
      str.equals ("XIII")     ||
      str.equals ("XIV")      ||
      str.equals ("XV")       ||
      str.equals ("XVI")      ||
      str.equals ("XVII")     ||
      str.equals ("XVIII")    ||
      str.equals ("XIX")      ||
      str.equals ("XX")       ||
      
      str.equals ("XXI")      ||
      str.equals ("XXII")     ||
      str.equals ("XXIII")    ||
      str.equals ("XXIV")     ||
      str.equals ("XXV")      ||
      str.equals ("XXVI")     ||
      str.equals ("XXVII")    ||
      str.equals ("XXVIII")   ||
      str.equals ("XXIX")     ||
      str.equals ("XXX")      ||
      
      str.equals ("XXXI")     ||
      str.equals ("XXXII")    ||
      str.equals ("XXXIII")   ||
      str.equals ("XXXIV")    ||
      str.equals ("XXXV")     ||
      str.equals ("XXXVI")    ||
      str.equals ("XXXVII")   ||
      str.equals ("XXXVIII")  ||
      str.equals ("XXXIX")    ||
      str.equals ("XXXX")     ||
      
      str.equals ("XXXXI")    ||
      str.equals ("XXXXII")   ||
      str.equals ("XXXXIII")  ||
      str.equals ("XXXXIV")   ||
      str.equals ("XXXXV")    ||
      str.equals ("XXXXVI")   ||
      str.equals ("XXXXVII")  ||
      str.equals ("XXXXVIII") ||
      str.equals ("XXXXIX")   ||
      
      str.equals ("M")        ||
      str.equals ("C")        ||
      str.equals ("L")
    )
    {
      issf=true;
    }
    
    return issf;
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
  public static void main (final String [] args)
  {
    //	    System.out.println ("Do you want to remove numbers? (Y or N)");
    //	    BufferedReader br=new BufferedReader (new InputStreamReader (System.in));
    //	    String line=br.readLine ();
    //	    if (line.equalsIgnoreCase ("Y"))
    //	    {
    //		V2Utils.setBorrowNums (true);
    //	    }
    //	    else if (line.equalsIgnoreCase ("N"))
    //	    {
    //		V2Utils.setBorrowNums (false);
    //	    }
    //	    else
    //	    {
    //		V2Utils.setBorrowNums (true);
    //	    }
    //
    //	    br.close ();
    //
    System.exit (0);
  }
  
}
