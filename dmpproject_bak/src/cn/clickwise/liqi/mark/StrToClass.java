package cn.clickwise.liqi.mark;

import java.util.ArrayList;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 字符串转对象数组
 * @author lq
 *
 */
public class StrToClass {

	
	/**
	 * 字符串转单词数组
	 * @param str
	 * @return
	 */
	public  WORD[] str2words(String str)
	{
		str=str.trim();
		if(SSO.tioe(str))
		{
			return null;
		}
		
		String[] seg_arr=str.split("\\s+");
		String item="";
		String[] item_arr=null;
		String index="";
		String count="";
		ArrayList<WORD> wl=new ArrayList<WORD>();
		WORD w=null;
		/*
		double sum_count=0;
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i].trim();
			item_arr=item.split(":");
		    if(item_arr.length<2)
		    {
		    	continue;
		    }
		    count=item_arr[1].trim();
		    sum_count+=Double.parseDouble(count);
		}
		*/
		
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i].trim();
			item_arr=item.split(":");
		    if(item_arr.length<2)
		    {
		    	continue;
		    }
		    index=item_arr[0].trim();
		    count=item_arr[1].trim();
		   // w=new WORD(Integer.parseInt(index),Integer.parseInt(count));
		    w=new WORD(Integer.parseInt(index),Double.parseDouble(count));
		    wl.add(w);
		}
		
		WORD[] warr=new WORD[wl.size()];
		for(int i=0;i<warr.length;i++)
		{
			warr[i]=wl.get(i);
		}
		//System.out.println("warr.len:"+warr.length);
		return warr;
	}
	
	/**
	 * 字符串转label
	 * @param str
	 * @return
	 */
	public  LABEL str2label(String str,LEARNPARM learn_parm)
	{
		LABEL l=new LABEL(Integer.parseInt(str),learn_parm.label_num);		
		return l;
	}
	
}
