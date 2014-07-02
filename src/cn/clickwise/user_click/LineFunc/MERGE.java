package cn.clickwise.user_click.LineFunc;

public class MERGE extends lineFunc{


	@Override
	public String lineProcess(String line, String params) {
		// TODO Auto-generated method stub
                String[] seg_arr=line.split("\001");
                if(seg_arr.length!=4)
                {
                     return "";
                }

		return seg_arr[0]+"\001"+seg_arr[1]+"\001"+seg_arr[2]+":"+seg_arr[3];
	}

}
