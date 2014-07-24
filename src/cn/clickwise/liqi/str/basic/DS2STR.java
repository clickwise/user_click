package cn.clickwise.liqi.str.basic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 各种数据结构转换成str
 * @author zkyz
 *
 */
public class DS2STR {

	public static String arraylist2str(ArrayList list)
	{
		String str="";
		for(int i=0;i<list.size();i++)
		{
			str=str+list.get(i)+" ";
		}
		str=str.trim();
		return str;
	}
	
	public static String hashmap2str(HashMap hm)
	{
		String str="";
        Set keys=hm.keySet();
        Iterator it=keys.iterator();
        Object key=null;
        while(it.hasNext())
        {
        	key=it.next();
        	str=(key+":"+hm.get(key)+" ");
        }
		str=str.trim();
		return str;
	}
	
	
}
