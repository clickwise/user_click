package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {

	
	public String merge_cookie_str(String old_cookie_str,String cookie_str)
	{
		String new_cookie_str="";
		Hashtable cookie_hash=new Hashtable();
		String[] seg_arr=null;
		seg_arr=old_cookie_str.split("\\s+");
		String cookie="";
		for(int i=0;i<seg_arr.length;i++)
		{
			cookie=seg_arr[i].trim();
			//System.out.println("cookie:"+cookie);
			if(!cookie.equals(""))
			{
				if(!(cookie_hash.containsKey(cookie)))
				{
					cookie_hash.put(cookie, cookie);
				}
			}				
		}
		
		
		seg_arr=cookie_str.split("\\s+");
		for(int i=0;i<seg_arr.length;i++)
		{
			cookie=seg_arr[i].trim();
			if(!cookie.equals(""))
			{
				if(!(cookie_hash.containsKey(cookie)))
				{
					cookie_hash.put(cookie, cookie);
				}
			}				
		}
		
		
		Enumeration enum_cook=cookie_hash.keys();
		while(enum_cook.hasMoreElements())
		{
			cookie=enum_cook.nextElement()+"";
			cookie=cookie.trim();
			if(!(cookie.equals("")))
			{
				new_cookie_str=new_cookie_str+cookie+" ";
			}
		}
		
		new_cookie_str=new_cookie_str.trim();			
		return new_cookie_str;
	}
	
	
	public String merge_cookie_str2(String old_cookie_str,String cookie_str)
	{
		String new_cookie_str="";
		Hashtable cookie_hash=new Hashtable();
		String[] seg_arr=null;
		seg_arr=old_cookie_str.split("\\s+");
		String cookie="";
		for(int i=0;i<seg_arr.length;i++)
		{
			cookie=seg_arr[i].trim();
			if(!cookie.equals(""))
			{
				if(!cookie_hash.containsKey(cookie))
				{
					cookie_hash.put(cookie, cookie);
				}
			}				
		}
		
		
		seg_arr=cookie_str.split("\\s+");
		for(int i=0;i<seg_arr.length;i++)
		{
			cookie=seg_arr[i].trim();
			if(!cookie.equals(""))
			{
				if(!cookie_hash.containsKey(cookie))
				{
					cookie_hash.put(cookie, cookie);
				}
			}				
		}
		
		
		Enumeration enum_cook=cookie_hash.keys();
		while(enum_cook.hasMoreElements())
		{
			cookie=enum_cook.nextElement()+"";
			cookie=cookie.trim();
			if(!cookie.equals(""))
			{
				new_cookie_str=new_cookie_str+" ";
			}
		}
		
		new_cookie_str=new_cookie_str.trim();			
		return new_cookie_str;
	}
	
	
	public String match_user_cookie(String cookie_str)
	{
	   String user_cookie="";
	   Pattern cook_pat=Pattern.compile("uid=([^;:\\s]*)");
	   Matcher cook_mat=cook_pat.matcher(cookie_str);
	   while(cook_mat.find())
	   {
		   user_cookie=cook_mat.group(1);
	   }
	   
	   return user_cookie;
	}
	
	public static void main(String[] args)
	{
		/*
		String cookie_str_one="uid=056457ca6631b68f91b4597079fe3835 uid=c2ef520afd6bd5fa94fccb91cbbdd400 uid=7ca9b957985d27d0dd0a02bf24ca9dae;BAIDUID=F6FEBE40854D2A79D0F48E2CFE903E19:FG=1 uid=5b81ca2514e79307e45cea2991099ae8 uid=267567372.1452642060060220100.1376382524481.53610��E uid=669032d699fa37bcc6413f83e554ae83 uid=10f4cd9b7d1a87800490469437b96681 uid=9d8fa90eda1af4fbf45fe8ef665e3057";
		String cookie_str_two="";
		
		//cookie_str_one="";
		//cookie_str_two="";
		
		String new_cook_str="";
		Test te=new Test();
		new_cook_str=te.merge_cookie_str(cookie_str_one, cookie_str_two);
		System.out.println("new_cook_str:"+new_cook_str);
		*/
		/*
		String cookie_str="uid=60ccd97fe286cef823de6f1230b1a4a4";
		String cookie="";
		Test te=new Test();
		cookie=te.match_user_cookie(cookie_str);
		System.out.println("cookie:"+cookie);
		*/
		
	}
	
}
