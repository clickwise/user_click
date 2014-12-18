package cn.clickwise.liqi.database.ssdb;

import java.util.ArrayList;
import java.util.Properties;

import com.udpwork.ssdb.SSDB;

import cn.clickwise.liqi.database.kv.KVDB;
import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class KVSSDB extends KVDB{
	
	public SSDB ssdb = null;
	public String ssdb_ip = "";
	public int ssdb_port = 0;
	
	@Override
	public void load_config(Properties prop) throws Exception {
		// TODO Auto-generated method stub
		
		ssdb_ip = prop.getProperty("ssdb_ip");
		ssdb_port = Integer.parseInt(prop.getProperty("ssdb_port"));
		ssdb = new SSDB(ssdb_ip, ssdb_port);
		
	}

	@Override
	public void set(String key, String value) throws Exception {
		// TODO Auto-generated method stub
		ssdb.set(key, value);		
	}

	@Override
	public String get(String key) throws Exception {
		// TODO Auto-generated method stub
		
		byte[] ssdb_res = null;
		ssdb_res = ssdb.get(key);
		if(ssdb_res==null)
		{
			return "";
		}
		return new String(ssdb_res);	
		
	}

	@Override
	public boolean exist(String key) throws Exception {
		// TODO Auto-generated method stub
		byte[] ssdb_res =ssdb.get(key);
	
		if(ssdb_res!=null)
		{
			return true;
		}
		return false;
	}

	@Override
	public void mulset(ArrayList<String> kv_list) throws Exception {
		// TODO Auto-generated method stub
		
		String kv_item="";
		String[] seg_arr=null;
		String vinfo="";
		String kinfo="";
		for(int i=0;i<kv_list.size();i++)
		{
			kv_item=kv_list.get(i)+"";
			if(!(SSO.tnoe(kv_item)))
			{
				continue;
			}
	        kv_item=kv_item.trim();
			seg_arr=kv_item.split("\001");
			if(seg_arr.length<2)
			{
				continue;
			}
			
			kinfo=seg_arr[0];
			vinfo="";
			for(int j=1;j<seg_arr.length;j++)
			{
				vinfo=vinfo+seg_arr[j]+"\001";
			}
			vinfo=vinfo.trim();
			
			if(!(SSO.tnoe(kinfo)))
			{
				continue;
			}
			kinfo=kinfo.trim();
			set(kinfo,vinfo);	
		}	
	}

	@Override
	public void mulsetfromfile(String file_name) throws Exception {
		// TODO Auto-generated method stub
		ArrayList<String> kv_list=FileToArray.fileToArrayList(file_name);
		mulset(kv_list);		
	}

	@Override
	public int size() throws Exception {
		// TODO Auto-generated method stub
		int ssdb_size=0;			
		return ssdb_size;
	}

}
