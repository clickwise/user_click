package cn.clickwise.liqi.datastructure.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class HashMapUtil {

	public static String hm2str(HashMap hm)
	{
		Set<String> hm_keys=hm.keySet();
		Iterator hm_it=hm_keys.iterator();
		String temp_key="";
		String temp_val="";
	
		String ping_str="";
		while(hm_it.hasNext())
		{
			temp_key=hm_it.next()+"";
			temp_val=hm.get(temp_key)+"";
			ping_str+=("["+temp_key+":"+temp_val+"] ");
		}
		ping_str=ping_str.trim();
		
		return ping_str;
	}
	
	public static HashMap<Integer,String> copyHashMap(HashMap<Integer,String> hm)
	{
		Set<Integer> hm_keys=hm.keySet();
		Iterator<Integer> it=hm_keys.iterator();
		int key=0;
		String val="";
		HashMap<Integer,String> hmn=new HashMap<Integer,String>();
		while(it.hasNext())
		{
			key=it.next();
			val=hm.get(key)+"";
			if(!(hmn.containsKey(key)))
			{
			   hmn.put(key, val);	
			}		
		}
		
		return hmn;
	}
	
	
}
