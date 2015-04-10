package cn.clickwise.clickad.hbase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

public class IP2Radius {

	private HashMap<String,Integer> ips=new HashMap<String,Integer>();
	
	/**
	 * 读取批量ip
	 * @param ips_file
	 */
	public void readIps(String ips_file)
	{
		String line="";
		
		try{
			
			BufferedReader br=new BufferedReader(new FileReader(ips_file));
			
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				if(!(ips.containsKey(line)))
				{
				    ips.put(line, 1);	
				}	
			}
			
			br.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public HashMap<String,HashMap<String,Integer>> getRadiusForIp()
	{
		
		HashMap<String,HashMap<String,Integer>> ipradius=null;
		try{
			
			ipradius=new HashMap<String,HashMap<String,Integer>>();
		   	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String line="";
			
			String[] fields=null;
			String ip="";
			String radiusId="";
			String status="";
			String time="";
			
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				fields=line.split("\t");
				
				if(fields.length!=4)
				{
					continue;
				}
				
				ip=fields[0];
				radiusId=fields[2];
				status=fields[1];
				time=fields[3];
			
				if(!(ips.containsKey(ip)))
				{
					continue;
				}
				
				if(!(ipradius.containsKey(ip)))
				{
					ipradius.put(ip, new HashMap<String,Integer>());
					ipradius.get(ip).put(radiusId, 1);
				}
				else if(!(ipradius.get(ip).containsKey(radiusId)))
				{
					if((ipradius.get(ip).size())>500)
					{
						continue;
					}
					ipradius.get(ip).put(radiusId, 1);
				}
			}
			
			
		   	PrintWriter pw=new PrintWriter(new OutputStreamWriter(System.out));
		   	
		   	for(Map.Entry<String, HashMap<String,Integer>> ipr:ipradius.entrySet())
		   	{
		   		if(ipr.getValue()==null)
		   		{
		   			continue;
		   		}
		   		
		   		String radiusIdStr="";
		   		for(Map.Entry<String, Integer> ipre:ipr.getValue().entrySet())
		   		{
		   			radiusIdStr=radiusIdStr+ipre.getKey()+"\t";
		   		}
		   		
		   		pw.println(ipr.getKey()+"\001"+radiusIdStr);
		   	}
   	
		   	pw.close();
		   	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return ipradius;
	}
	
	public HashMap<String,HashMap<String,Integer>> getRadiusForIp2()
	{
		
		HashMap<String,HashMap<String,Integer>> ipradius=null;
		try{
			
			ipradius=new HashMap<String,HashMap<String,Integer>>();
		   	BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			String line="";
			
			String[] fields=null;
			String ip="";
			String radiusId="";
			String status="";
			String time="";
			
			while((line=br.readLine())!=null)
			{
				if(SSO.tioe(line))
				{
					continue;
				}
				
				fields=line.split("\\s+");
				
				if(fields.length<1)
				{
					continue;
				}
				
				ip=fields[0];

			
				if(!(ips.containsKey(ip)))
				{
					continue;
				}
				
				String[] tokens=null;
				for(int k=1;k<fields.length;k++)
				{
				  tokens=fields[k].split(":");
				  if(tokens.length!=3)
				  {
					  continue;
				  }
				  
				  radiusId=tokens[1];
				  if(!(ipradius.containsKey(ip)))
				  {
					ipradius.put(ip, new HashMap<String,Integer>());
					ipradius.get(ip).put(radiusId, 1);
				  }
				  else if(!(ipradius.get(ip).containsKey(radiusId)))
				  {
					if((ipradius.get(ip).size())>500)
					{
						continue;
					}
					ipradius.get(ip).put(radiusId, 1);
				 }
				}
				
				
			}
			
			
		   	PrintWriter pw=new PrintWriter(new OutputStreamWriter(System.out));
		   	
		   	for(Map.Entry<String, HashMap<String,Integer>> ipr:ipradius.entrySet())
		   	{
		   		if(ipr.getValue()==null)
		   		{
		   			continue;
		   		}
		   		
		   		String radiusIdStr="";
		   		for(Map.Entry<String, Integer> ipre:ipr.getValue().entrySet())
		   		{
		   			radiusIdStr=radiusIdStr+ipre.getKey()+"\t";
		   		}
		   		
		   		pw.println(ipr.getKey()+"\001"+radiusIdStr);
		   	}
   	
		   	pw.close();
		   	
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		return ipradius;
	}
	
	public static void main(String[] args)
	{
		if(args.length!=1)
		{
		   System.err.println("Usage:<ips_file>");	
		   System.exit(1);
		}
		
		String ips_file=args[0];
		IP2Radius ip2r=new IP2Radius();
		ip2r.readIps(ips_file);
		ip2r.getRadiusForIp2();
		
	}
}
