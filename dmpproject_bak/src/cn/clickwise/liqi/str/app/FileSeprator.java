package cn.clickwise.liqi.str.app;

import java.util.ArrayList;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class FileSeprator {

	public void replaceSep(String input,String output) throws Exception
	{
		String[] lines=FileToArray.dirToDimArr(input);
		String item="";
		String[] seg_arr=null;
		String kind="";
		String host="";
		ArrayList<String> res_list=new ArrayList<String>();
		for(int i=0;i<lines.length;i++)
		{
		  item=lines[i].trim();
		  if(SSO.tioe(item))
		  {
			  continue;
		  }
		  seg_arr=item.split("\\s+");
		  if(seg_arr.length!=2)
		  {
			  continue;
		  }
		  kind=seg_arr[0].trim();
		  host=seg_arr[1].trim();
		  if(SSO.tioe(kind)||SSO.tioe(host))
		  {
			  continue;
		  }
		  res_list.add(host+"\001"+kind);
		}
		
		FileWriterUtil.writeArrayList(res_list, output, false);
		
	}
	
	public static void main(String[] args) throws Exception
	{
		String input="C:/eclipse_java_workspace/dmpproject/input/finance";
		String output="C:/eclipse_java_workspace/dmpproject/temp/finance/finance_map_fsp.txt";
		FileSeprator fsp=new FileSeprator();
		fsp.replaceSep(input, output);
	}
	
}
