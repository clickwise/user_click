package cn.clickwise.clickad.feathouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

//统计rtb发送的cookie能查到特征的比例
public class CookieMatch {

	public void match(String cookies,String[] astats)
	{
		HashMap<String,Boolean> cookieMap=new HashMap<String,Boolean>();
		
		try{
			BufferedReader cbr=new BufferedReader(new FileReader(cookies));
			String cookie="";
			while((cookie=cbr.readLine())!=null)
			{
				if(SSO.tioe(cookie))
				{
					continue;
				}
				   if(!(cookie.endsWith("030")))
				   {
					   continue;
				   }
				cookie=cookie.trim();
				if(!(cookieMap.containsKey(cookie)))
				{
					cookieMap.put(cookie,false);
				}
			}
			
			cbr.close();
			
			System.out.println("uv:"+cookieMap.size());
			String acookie="";
			String record="";
			for(int i=0;i<astats.length;i++)
			{
				BufferedReader br=new BufferedReader(new FileReader(astats[i]));
				
				while((record=br.readLine())!=null)
				{
				   acookie=getCookieFromRecord(record);
				   if(SSO.tioe(acookie))
				   {
					   continue;
				   }
				   
				   if(!(acookie.endsWith("030")))
				   {
					   continue;
				   }
				   
				   if(cookieMap.containsKey(acookie))
				   {
					   cookieMap.put(acookie, true);
				   }
				  
				}
				br.close();
			}
			
			int t=0;
			int f=0;
			for(Map.Entry<String, Boolean> e:cookieMap.entrySet())
			{
				if(e.getValue()==true)
				{
					t++;
				}
				else 
				{
					f++;
				}
			}
			
			System.out.println("t:"+t);
			System.out.println("f:"+f);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String getCookieFromRecord(String record)
	{
		if(SSO.tioe(record))
		{
			return "";
		}
		
		String[] fields=record.split("\001");
		if(fields.length<5)
		{
			return "";
		}
		
		
		String cookie=fields[4];
		if(SSO.tioe(cookie))
		{
			return "";
		}
		
		
		return cookie.trim();
	}
	
	public static void main(String[] args)
	{
		if(args.length<2)
		{
			System.err.println("Usage:<cookie> <astat>+" +
					"cookie:cookie 文件" +
					"astat:astat 文件列表");
		    System.exit(1);	
		}
		
		
		String cookie=args[0];
		String[] astats=new String[args.length-1];
		for(int i=1;i<args.length;i++)
		{
			astats[i-1]=args[i];
		}
		
		CookieMatch cm=new CookieMatch();
		cm.match(cookie, astats);
		
	}
	
}
