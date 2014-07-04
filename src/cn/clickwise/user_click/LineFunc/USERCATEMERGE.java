package cn.clickwise.user_click.LineFunc;

import java.text.SimpleDateFormat;
import java.util.Date;
public class USERCATEMERGE extends lineFunc{


	@Override
	public String lineProcess(String line, String params) {
		// TODO Auto-generated method stub
                String[] seg_arr=line.split("\001");
                if(seg_arr.length!=5)
                {
                     return "";
                }
                String datatype="HOST_CATE";
                String time_str=seg_arr[1].trim();
                time_str+=(" 09:28:47");
                SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Date dt =null;	
		try
		{
			dt = sdf.parse(time_str);	
		}
		catch(Exception e)
		{
			
		}
		if(dt==null)
		{
			return "";
		}

		return seg_arr[0]+"\001"+datatype+"\001"+dt.getTime()+"\001"+seg_arr[2]+":"+seg_arr[3]+" "+seg_arr[4];
	}

}
