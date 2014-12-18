package cn.clickwise.liqi.http.server;

import java.io.File;
import java.util.HashMap;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.hive.HiveUtils;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class HiveServerMethod extends ServerMethod {
	
	public static String hive_table="iaccess";
	public static String field_name="title";
	public static HashMap<String,String> rphm;
	public String[] method(String keyword)
	{
		File tmp_file=new File("/tmp/hive_lookup"); 
		if(!(tmp_file.exists()))
		{
			tmp_file.mkdir();
		}
		
		String tmp_dir="/tmp/hive_lookup"+"/hl_"+TimeOpera.getCurrentTimeLong();
		String sql="";
		rphm=RequestParm.getRequestParm(keyword);
		String date=rphm.get("date");
		String keystr=rphm.get("keyword");
		System.out.println("date:"+date);
		System.out.println("keystr:"+keystr);
		if(SSO.tioe(keystr))
		{
			return null;
		}	
		keystr=keystr.trim();
		String[] key_arr=keystr.split("\\s+");
		String plo=pingLikeOr(key_arr,field_name);
		if(SSO.tioe(plo))
		{
			return null;
		}
		sql="INSERT OVERWRITE LOCAL DIRECTORY '"+tmp_dir+"' SELECT url,trim(title),count(1) as num from  "+hive_table+" where "+plo+" and dt="+date+" group by url,trim(title) order by num desc;";
		HiveUtils.hive_proc(sql);
		String[] hive_res=null;
		File rm_dir=new File(tmp_dir);
		File[] rm_files=rm_dir.listFiles();
		try{
		   hive_res=FileToArray.dirToDimArr(tmp_dir);
		   for(int i=0;i<rm_files.length;i++)
		   {
			   rm_files[i].delete();
		   }
		   rm_dir.delete();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return hive_res;
	}
	
	public String pingLikeOr(String[] key_arr,String field_name)
	{
		if((key_arr==null)||(key_arr.length<1))
		{
			return "";
		}
		String plo="(";
		String item="";
		for(int i=0;i<(key_arr.length-1);i++)
		{
			item=key_arr[i];
			if(SSO.tioe(item))
			{
				continue;
			}
			item=item.trim();
			plo=plo+field_name+" like '%"+item+"%\' or ";
		}
		
		item=key_arr[key_arr.length-1];
		if(SSO.tioe(item))
		{
			return "";
		}
		item=item.trim();
		
		plo=plo+field_name+" like '%"+item+"%\') ";
		
		return plo;
	}
	
	public String pingLikeAnd(String[] key_arr,String field_name)
	{
		
		
		return null;
	}
	

}
