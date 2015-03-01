package cn.clickwise.sifu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jmlp.sort.utils.SortStrArray;

import cn.clickwise.lib.string.SSO;

public class SIFUURL {

	public void url_refer_statis(String astat,String output,String host_output)
	{
		HashMap<String,HashMap<String,Boolean>> url_refer_sip=new HashMap<String,HashMap<String,Boolean>>();
		HashMap<String,HashMap<String,Boolean>> url_refer_cookie=new HashMap<String,HashMap<String,Boolean>>();
		HashMap<String,HashMap<String,Boolean>> host_cookie=new HashMap<String,HashMap<String,Boolean>>();
		
		try{
			String url="";
			String refer="";
			String cookie="";
			String sip="";
			String[] seg_arr=null;
			String line="";
			String host="";
			String title="";
			
			BufferedReader br=new BufferedReader(new FileReader(astat));
			String url_refer="";
			
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				seg_arr=line.split("\001");
				if(seg_arr.length<14)
				{
					continue;
				}
				sip=seg_arr[10];
				cookie=seg_arr[4];
				url=seg_arr[7];
				host=seg_arr[6];
				refer=seg_arr[13];
				title=seg_arr[8];
				if(title.trim().equals("NA"))
				{
					continue;
				}
				
				if((title.indexOf("传奇")<0)&&(title.indexOf("私服")<0))
				{
					continue;
				}
				
				url_refer=url_refer_key(url,refer);
			    if(SSO.tioe(url_refer))
			    {
			    	continue;
			    }
			    
			    if(SSO.tnoe(sip))
			    {
			    	if(!(url_refer_sip.containsKey(url_refer)))
			    	{
			    		url_refer_sip.put(url_refer, new HashMap<String,Boolean>());
			    		url_refer_sip.get(url_refer).put(sip, true);
			    	}
			    	else
			    	{
			    		url_refer_sip.get(url_refer).put(sip, true);
			    	}
			    }
			    
			    if(SSO.tnoe(cookie))
			    {
			    	if(!(url_refer_cookie.containsKey(url_refer)))
			    	{
			    		url_refer_cookie.put(url_refer, new HashMap<String,Boolean>());
			    		url_refer_cookie.get(url_refer).put(cookie, true);
			    	}
			    	else
			    	{
			    		url_refer_cookie.get(url_refer).put(cookie, true);
			    	}
			    	
			    	if(SSO.tnoe(host))
			    	{
				    	if(!(host_cookie.containsKey(host)))
				    	{
				    		host_cookie.put(host, new HashMap<String,Boolean>());
				    		host_cookie.get(host).put(cookie, true);
				    	}
				    	else
				    	{
				    		host_cookie.get(host).put(cookie, true);
				    	}
			    	}
			    	
			    }
			}
			
			br.close();
			
			ArrayList<String> url_refer_list=new ArrayList<String>();		
			ArrayList<String> host_list=new ArrayList<String>();
			
			HashMap<String,Boolean> cookieMap=null;
			for(Map.Entry<String, HashMap<String,Boolean>> e:url_refer_sip.entrySet())
			{
				url_refer=e.getKey();
				cookieMap=url_refer_cookie.get(url_refer);
				if(cookieMap==null)
				{
					continue;
				}
				
				url_refer_list.add(url_refer+"\001"+e.getValue().size()+"\001"+cookieMap.size());
			}
			
			String[] url_refer_sort=SortStrArray.sort_List(url_refer_list, 2, "int", 4, "\001");
			PrintWriter pw=new PrintWriter(new FileWriter(output));
			for(int i=0;i<url_refer_sort.length;i++)
			{
				pw.println(url_refer_sort[i]);
			}
			
			pw.close();
			
			for(Map.Entry<String, HashMap<String,Boolean>> e:host_cookie.entrySet())
			{
				host=e.getKey();		
				host_list.add(host+"\001"+e.getValue().size());
			}
			
			PrintWriter hostpw=new PrintWriter(new FileWriter(host_output));
			String[] host_sort=SortStrArray.sort_List(host_list, 1, "int", 2, "\001");
			for(int i=0;i<host_sort.length;i++)
			{
				hostpw.println(host_sort[i]);
			}		
			hostpw.close();
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		
	}
	
	
	public String url_refer_key(String url,String refer)
	{
		if(refer.trim().equals("NA"))
		{
			refer="";
		}
		
		if(SSO.tioe(url))
		{
			return "";
		}
		
		refer=refer.trim();
		url=url.trim();
		
		return url+"\001"+refer;
	}
	
	public static void main(String[] args)
	{
		if(args.length!=3)
		{
			System.err.println("Usage:<astat> <url_refer> <host>");
			System.exit(1);
		}
		
		String astat=args[0];
		String url_refer=args[1];
		String host=args[2];
		SIFUURL su=new SIFUURL();
		su.url_refer_statis(astat, url_refer, host);
	}
	
}
