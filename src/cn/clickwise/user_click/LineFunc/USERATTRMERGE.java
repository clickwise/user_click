package cn.clickwise.user_click.LineFunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import cn.clickwise.liqi.time.utils.TimeOpera;
public class USERATTRMERGE extends lineFunc{


	@Override
	public String lineProcess(String line, String params) {
		// TODO Auto-generated method stub
                //double ran=Math.random();

                String[] seg_arr=line.split("\001");
                /*
                if(ran>0.99)
                {
                   System.out.println("seg_arr.length:"+seg_arr.length);
                }
                */

                if(seg_arr.length!=5)
                {
                     return "";
                }
                String datatype="USER_ATTR";
                /*
                if(ran>0.99)
                {
                   System.out.println(seg_arr[0]+"\001"+datatype+"\001"+TimeOpera.getCurrentTimeLong()+"\001"+seg_arr[1]+":1 "+seg_arr[2]+"|"+seg_arr[3]+":1 "+seg_arr[4]);
                }                
                */
		return seg_arr[0]+"\001"+datatype+"\001"+TimeOpera.getCurrentTimeLong()+"\001"+seg_arr[1]+":1 "+seg_arr[2]+"|"+seg_arr[3]+":1 "+seg_arr[4];
	}

}
