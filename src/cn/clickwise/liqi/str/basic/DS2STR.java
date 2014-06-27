package cn.clickwise.liqi.str.basic;

import java.util.ArrayList;

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
	
}
