package cn.clickwise.clickad.radiusClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class StreamHead {
	
	public void head(String input,String output,int n)
	{
		try{
			FileInputStream fis=new FileInputStream(new File(input));
			FileOutputStream fos=new FileOutputStream(new File(output));
			byte[] buffer=new byte[1024];
			
			int m=0;
			while(m<n)
			{
				m+=1024;
				fis.read(buffer);
				fos.write(buffer);			
			}

			fis.close();
			fos.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args)
	{
		if(args.length!=3)
		{
			System.err.println("Usage:<StreamHead> <input> <output> <n>");
			System.exit(1);
		}
		
		String input=args[0];
		String output=args[1];
		int n=Integer.parseInt(args[2]);
		
		StreamHead sh=new StreamHead();
		sh.head(input, output, n);
		
		
	}
}
