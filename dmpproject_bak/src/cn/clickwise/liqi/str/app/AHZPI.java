package cn.clickwise.liqi.str.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

import cn.clickwise.liqi.datastructure.map.HashMapUtil;
import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class AHZPI {
	static Logger logger = Logger.getLogger(AHZPI.class.getName());
	public static void main(String[] args) throws Exception
	{
		String date="";
		String host="";
		String pv="";
		String ip="";
		String[][] darr=FileToArray.fileToDoubleDimArr("D:/projects/hosts/20140417/adjust_host_hz_pv.txt", "\001");
		
		HashMap<String,HashMap<Integer,String>> hm=new HashMap<String,HashMap<Integer,String>>();
		HashMap<Integer,String> sub_hash=new HashMap<Integer,String>();
		for(int i=0;i<darr.length;i++)
		{
			date=darr[i][0];
			host=darr[i][1].trim();
			pv=darr[i][2];
		//	logger.info("date:"+date+" host:"+host+" pv:"+pv+" ip:"+ip);
			if(!(hm.containsKey(host)))
			{
				sub_hash=new HashMap<Integer,String>();
				sub_hash.put(Integer.parseInt(date), pv);
				hm.put(host, sub_hash);
			}
			else
			{
				sub_hash=HashMapUtil.copyHashMap(hm.get(host));
			//	logger.info(HashMapUtil.hm2str(sub_hash));
				if(!(sub_hash.containsKey(date)))
				{
					sub_hash.put(Integer.parseInt(date), pv);
				}
				else
				{					
				}				
	            hm.remove(host);
	            hm.put(host, sub_hash);
			}
		}
		
		FileWriter fw=new FileWriter(new File("D:/projects/hosts/20140417/adjust_host_hz_pv_rd.txt"));
		PrintWriter pw=new PrintWriter(fw);
		Set<String> hm_keys=hm.keySet();
		Iterator it=hm_keys.iterator();
		
		String hk="";
		String ping_line="";
		String pv_ip="";
	    String[] seg_arr=null;
	    
		while(it.hasNext())
		{
			ping_line="";
			hk=it.next()+"";
			logger.info("hk:"+hk);
			ping_line+=(hk+"\001");
			sub_hash=hm.get(hk);
			logger.info(HashMapUtil.hm2str(sub_hash));
			for(int iti=20140327;iti<=20140416;iti++)
			{
				if(iti>20140331&&iti<20140400)
				{
					continue;
				}
				pv_ip=sub_hash.get(iti);
				logger.info("pv_ip:"+pv_ip);
				//System.out.println("iti:"+iti);
				if(SSO.tnoe(pv_ip))
				{
				  //seg_arr=pv_ip.split("\\|");
				//  pv=seg_arr[1];
				  ping_line+=(pv_ip+"\001");
				}		
				else 
				{
				  ping_line+=(0+"\001");
				}
			}
			ping_line=ping_line.trim();
		    pw.println(ping_line);	
		}
		
		pw.close();
		fw.close();
	}
	
}
