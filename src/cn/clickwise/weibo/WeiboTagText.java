package cn.clickwise.weibo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import cn.clickwise.lib.string.SSO;

public class WeiboTagText {

	public void tagText(String labelwu,String user_info,String output)
	{
		HashMap<String,String> lmap=new HashMap<String,String>();
		
		try{
			BufferedReader br=new BufferedReader(new FileReader(labelwu));
			String line="";
			
			String[] fields=null;
			String url="";
			String tag="";
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				line=line.trim();
				fields=line.split("\t");
				if(fields.length!=2)
				{
					continue;
				}
				
				tag=fields[0];
				if(SSO.tioe(tag))
				{
					continue;
				}
				tag=tag.trim();
				
				url=fields[1];
				if(SSO.tioe(url))
				{
					continue;
				}
				url=url.trim();
				if(!(lmap.containsKey(url)))
				{
					lmap.put(url, tag);
				}
			}
			
			br.close();
			
			System.out.println("lmap.size:"+lmap.size());
			
			BufferedReader ubr=new BufferedReader(new FileReader(user_info));
			String info="";
			PrintWriter pw=new PrintWriter(new FileWriter(output));
			while((line=ubr.readLine())!=null)
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
				
				url=fields[0];
				info="";
				for(int j=1;j<fields.length;j++)
				{
					info=info+fields[j]+"\t";
				}
				info=info.trim();
				if(SSO.tioe(url))
				{
					continue;
				}
				url=url.trim();
				tag=lmap.get(url);
				if(SSO.tioe(tag))
				{
					continue;
				}
				
				pw.println(url+"\t"+tag+"\t"+info);
				
			}
			
			ubr.close();
			pw.close();
	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args)
	{
		WeiboTagText wtt=new WeiboTagText();
		String labelwu="weibo/labelwu.txt";
		String user_info="weibo/user_info.txt";
		String output="weibo/labeltext.txt";
		wtt.tagText(labelwu, user_info, output);
		
	}
	
	
}
