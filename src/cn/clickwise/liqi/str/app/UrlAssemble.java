package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cn.clickwise.lib.string.SSO;

/**
 * 从输入流读入url前缀、后缀，组装出系列类似的url
 * @author zkyz
 */
public class UrlAssemble {

	public void urlAssem(int start,int increment,int pagenum)
	{
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			PrintWriter pw=new PrintWriter(new OutputStreamWriter(System.out));
			
			String line="";
			while(SSO.tioe(line))
			{
				line=br.readLine();
			}
			
			line=line.trim();
			
			String[] fields=line.split("\t");
			if(fields.length!=2)
			{
			  return;	
			}
			
			String prefix="";
			String suffix="";
			prefix=fields[0];
			suffix=fields[1];
			
			String url="";
			for(int i=0;i<pagenum;i++)
			{
				url=prefix+(start+i*increment)+suffix;
				pw.println(url);
			}
			br.close();
			pw.close();
			
			
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
			System.err.println("Usage:<startIndex> <increment> <pagenum>");
			System.exit(1);
		}
		
		int increment=0;
		int pagenum=0;
		int startIndex=0;
		startIndex=Integer.parseInt(args[0]);
		increment=Integer.parseInt(args[1]);
		pagenum=Integer.parseInt(args[2]);
		
		UrlAssemble ua=new UrlAssemble();
		ua.urlAssem(startIndex,increment, pagenum);
			
	}
	
}
