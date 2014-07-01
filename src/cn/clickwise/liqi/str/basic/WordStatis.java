package cn.clickwise.liqi.str.basic;

import java.util.Map.Entry;

import love.cq.util.MapCount;

public class WordStatis {

	public static String wordStatis(String seg_line)
	{
		if(SSO.tioe(seg_line))
		{
			return "";
		}
		seg_line=seg_line.trim();
		
		String[] seg_arr=seg_line.split("\\s+");
		MapCount<String> mc=new MapCount<String>();
		
		for(int i=0;i<seg_arr.length;i++)
		{
			mc.add(seg_arr[i]);
		}
		String wsline="";
        for (Entry<String, Integer> element : mc.get().entrySet()) {
            wsline+=element.getKey()+":"+element.getValue()+" ";
        }
		wsline=wsline.trim();
		return wsline;
	}
	
	
	
	public static void main(String[] args)
	{
		String line="app store 无法下载 应用程序 app";
		System.out.println(wordStatis(line));
	}
	
}
