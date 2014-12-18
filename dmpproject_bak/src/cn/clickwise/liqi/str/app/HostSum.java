package cn.clickwise.liqi.str.app;

import java.util.HashMap;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class HostSum {

	public static void main(String[] args) throws Exception
	{
		HashMap<String,Integer> host_map=new HashMap<String,Integer>();
		
		String[][] host_info_arr=FileToArray.fileToDoubleDimArr("D:/projects/spread_data/sifu/data0417/date_sf.txt", "\001");
		
		String host="";
		int pv=0;
		int old_count=0;
		for(int i=0;i<host_info_arr.length;i++)
		{
		  host=host_info_arr[i][1];
		  host=host.trim();
		  pv=Integer.parseInt(host_info_arr[i][2]);
		  if(SSO.tioe(host))
		  {
			  continue;
		  }
		  if(!(host_map.containsKey(host)))
		  {
			  host_map.put(host, pv);
		  }
		  else
		  {
			  old_count=host_map.get(host);
			  old_count+=pv;
			  host_map.remove(host);
			  host_map.put(host, old_count);
		  }	 
		}
		
		FileWriterUtil.writeHashMap(host_map, "D:/projects/spread_data/sifu/data0417/sum_sf.txt");
		
	}
}
