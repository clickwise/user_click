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
	
	public static String array2str(String[] array)
	{
		String str="";
		for(int i=0;i<array.length;i++)
		{
			str=str+array[i]+" ";
		}
		str=str.trim();
		return str;
	}
	
	public static String douarray2str(double[] array)
	{
		String str="";
		for(int i=0;i<array.length;i++)
		{
			str=str+array[i]+" ";
		}
		str=str.trim();
		return str;
	}
	
	public static String trimfield(String[] arr,String seprator,int field_num,int index)
	{
		String str="";
		String item="";
		String[] seg_arr=null;
		for(int i=0;i<arr.length;i++)
		{
			item=arr[i];
			if(SSO.tioe(item))
			{
				continue;
			}
			item=item.trim();
			seg_arr=item.split(seprator);
			if(seg_arr.length!=field_num)
			{
				continue;
			}
			str=str+seg_arr[index]+" ";
		}
		str=str.trim();
		return str;
	}
	
}
