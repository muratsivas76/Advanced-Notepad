import java.io.*;

final
public class ConvertCharCodes
extends Object
implements Serializable
{

    private ConvertCharCodes ()
    {
	super ();
    }
    
    public String toString ()
    {
	return "";
    }
    
    final
    private static void usage (final String [] args)
    {
	if (args.length < 4)
	{
	    System.out.println ("Usage:\n\tjava -cp cls; ConvertCharCodes [<src>] [<dst>] [<srcCharCode>] [<dstCharCode>]\n");
	    System.out.println ("Example:\n\tjava -cp cls; ConvertCharCodes src.txt dst.txt UTF-8 ISO-8859-9\n");
	    System.exit (-1);
	}
	
	return;
    }
    
    final
    private static void cnt (String src, String dst, String srcCode, String dstCode)
    throws IOException
    {
	File f=new File (src);
	InputStream fis=new FileInputStream (f);
	Reader isr=new InputStreamReader (fis, srcCode);
	BufferedReader br=new BufferedReader (isr);
	
	File fd=new File (dst);
	OutputStream fos=new FileOutputStream (fd);
	PrintStream ps=new PrintStream (fos, true, dstCode);
	
	String line=null;
	
	while ( (line=br.readLine ()) != null )
	{
	    ps.println (line);
	}

	br.close ();
	isr.close ();
	fis.close ();
	
	 ps.flush ();  ps.close ();
	fos.flush (); fos.close ();
	
	System.out.println (""+src+" --> "+dst+"");
	
	return;
    }
    
    final
    public static void main (final String [] args)
    {
	usage (args);

	try
	{
	    cnt (args [0], args [1], args [2], args [3]);
	}
	catch (IOException ioe)
	{
	    ioe.printStackTrace ();
	    System.exit (-1);
	}

	System.exit (0);
    }

}
