package cn.clickwise.weibo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.lib.string.SSO;

public class LongAddTag {

	public void addTag(String longFile,String labelFile,String output)
	{
		try{			
		  BufferedReader br=new BufferedReader(new FileReader(labelFile));
		  String line="";
		  
		  String[] fields=null;
		  String url="";
		  String tag="";
		  
		  HashMap<String,String> urlTag=new HashMap<String,String>();
		  
		  while((line=br.readLine())!=null)
		  {
			  if(SSO.tioe(line))
			  {
				  continue;
			  }
			  
			  fields=line.split("\t");
			  if(fields.length<2)
			  {
				  continue;
			  }
			  url=fields[0].trim();
			  tag=fields[1].trim();
			  
			  if(!(urlTag.containsKey(url)))
			  {
				  urlTag.put(url, tag);
			  }
			  
		  }
		  
		  br.close();
		  
		  PrintWriter pw=new PrintWriter(new FileWriter(output));  
		  BufferedReader longbr=new BufferedReader(new FileReader(longFile));
		  
		  HashMap<String,String> urlHash=new HashMap<String,String>();
		  while((line=longbr.readLine())!=null)
		  {
			 if(SSO.tioe(line))
			 {
				 continue;
			 }
			 
			 fields=line.split("\t");
			 if(fields.length<2)
			 {
				 continue;
			 }
			  
			 url=fields[0].trim();
			 
			 //if(urlTag.containsKey(url))
			 //{
			//	 pw.println(line);
			// }
			 
			 if(!(urlHash.containsKey(url)))
			 {
				 urlHash.put(url, url);
				 pw.println(line);
			 }
			 
		  }
		  
		  longbr.close();
		  pw.close();
		  	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
			
	}
	
	
	public static void main(String[] args){
		
		String longFile="weibo/labeltext_filter_with_post.txt";
		String labelFile="weibo/labeltext_filter_mod_u.txt";
		String output="weibo/labeltext_post_u.txt";
		
		LongAddTag lat=new LongAddTag();
		lat.addTag(longFile, labelFile, output);
		
	}
	
}
