package cn.clickwise.liqi.file.uitls;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.lib.string.SSO;

public class FileUniq {

	public void uniq(String input,String output)
	{
	   try{
		   
		BufferedReader br=new BufferedReader(new FileReader(input));   
		PrintWriter pw=new PrintWriter(new FileWriter(output));
		String line="";
		String[] fields=null;
		String url="";
		
		HashMap<String,String> urlHash=new HashMap<String,String>();
		
		while((line=br.readLine())!=null)
		{
			if(SSO.tioe(line))
			{
				continue;
			}
			
			line=line.trim();
			
			fields=line.split("\\s+");
			if(fields.length<2)
			{
				continue;
			}
			
			url=fields[0];
			if(SSO.tioe(url))
			{
				continue;
			}
			url=url.trim();
			
			if(!(urlHash.containsKey(url)))
			{
				urlHash.put(url, url);
				pw.println(line);
			}
			
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
		FileUniq fu=new FileUniq();
		String input="weibo/labeltext_filter_mod.txt";
		String output="weibo/labeltext_filter_mod_u.txt";
		fu.uniq(input, output);
	}
	
}
