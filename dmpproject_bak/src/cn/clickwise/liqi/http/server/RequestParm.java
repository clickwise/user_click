package cn.clickwise.liqi.http.server;

import java.util.HashMap;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 处理请求的参数
 * @author lq
 *
 */
public class RequestParm {

	
	/**
	 * 从请求串获得参数列表
	 * 格式: {"date":20140329,"keyword":"k1 k2"}
	 * @param quest_str
	 * @return
	 */
	public static HashMap<String,String> getRequestParm(String quest_str)
	{
		String[] seg_arr=quest_str.split(",");
		String item="";
		String key="";
		String val="";
		
		HashMap<String,String> rphm=new  HashMap<String,String>();
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i].trim();
			System.out.println("item:"+item);
			item=SSO.truncBeforeStr(item, "{");
			System.out.println("item1:"+item);
			item=SSO.truncAfterStr(item, "}");
			System.out.println("item2:"+item);
			key=SSO.beforeStr(item, ":");
			System.out.println("key:"+key);
			key=SSO.truncBeforeStr(key, "\"");
			System.out.println("key1:"+key);
			key=SSO.truncAfterStr(key, "\"");
			System.out.println("key2:"+key);
			
			val=SSO.afterStr(item, ":");
			System.out.println("val:"+val);
			val=SSO.truncBeforeStr(val, "\"");
			System.out.println("val1:"+val);
			val=SSO.truncAfterStr(val, "\"");
			System.out.println("val2:"+val);
			if(SSO.tnoe(key)&&SSO.tnoe(val))
			{
				if(!(rphm.containsKey(key)))
				{
					rphm.put(key, val);
				}
				
			}
		}
		
		return rphm;
	}
	
}
