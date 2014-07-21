package cn.clickwise.admatch;

import java.util.HashMap;

import cn.clickwise.liqi.str.basic.SSO;

public class MatchTool {

	public static HashMap<String,String> convert_params(String uri,String head)
	{
		HashMap<String,String> param_map=new HashMap<String,String>();
		String eff_info="";
		eff_info=uri.substring(uri.indexOf(head)+head.length(),uri.length());
		String[] seg_arr=eff_info.split("&");
		String item="";
		String[] item_seg=null;
		String para_name="";
		String para_text="";
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i];
			if(SSO.tioe(item))
			{
				continue;
			}
			item=item.trim();
			item_seg=item.split("=");
			if(item_seg.length!=2)
			{
				continue;
			}
			
			para_name=item_seg[0].trim();
			para_text=item_seg[1].trim();
			if(!(param_map.containsKey(para_name)))
			{
			  param_map.put(para_name, para_text);
			}
		}
		
		return param_map;
	}
	
}
