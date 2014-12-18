package cn.clickwise.liqi.file.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import cn.clickwise.liqi.str.basic.SSO;

public class FileReaderUtil {
	
	/**
	 * 获取hashmap,第一列为key,第二列为value
	 * @return
	 */
	public static HashMap<String,String> getHashFromPlainFile(String input_file) {
		// TODO Auto-generated method stub
	
		HashMap<String,String> hm=new HashMap<String,String>();
	    String item="";
	    String word="";
	    String index_str="";
		int index=0;
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
			
		try{
		   fr=new FileReader(new File(input_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   word=seg_arr[0].trim();
			   index_str=seg_arr[1].trim();

			   if(!(SSO.tnoe(word)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(index_str)))
			   {
				   continue;
			   }
			   if(!(hm.containsKey(word)))
			   {
			     hm.put(word,index_str);
			   }
		   }
			fr.close();
			br.close();
		}
		catch(Exception e)
		{
			
		}	

		return hm;
	}
	
	/**
	 * 读取模型的词典文件，建立单词索引
	 * 词典索引从1开始
	 * @return
	 */
	public static HashMap getDictFromPlainFile(String input_file) {
		// TODO Auto-generated method stub
	
		HashMap hm=new HashMap();
	    String item="";
	    String word="";
	    String index_str="";
		int index=0;
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
			
		try{
		   fr=new FileReader(new File(input_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   word=seg_arr[0].trim();
			   index_str=seg_arr[1].trim();

			   if(!(SSO.tnoe(word)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(index_str)))
			   {
				   continue;
			   }
			   index=Integer.parseInt(index_str);
			   //if(index%100==0)
			   //{
				   //System.out.println(word+" "+index_str);
			  // }
			   if(index<1)
			   {
				   continue;
			   }
			   hm.put(word,index);			   
		   }
		   
		}
		catch(Exception e)
		{
			
		}	
		return hm;
	}
	
	
	/**
	 * 类别索引和类别名的对应关系
	 * 类别索引从1开始
	 * @return
	 */
	public static HashMap getIndexLabelFromPlainFile(String input_file) {
		// TODO Auto-generated method stub
	
		HashMap hm=new HashMap();
	    String item="";
	    String label="";
	    String index_str="";
		int index=0;
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
			
		try{
		   fr=new FileReader(new File(input_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   label=seg_arr[0].trim();
			   index_str=seg_arr[1].trim();

			   if(!(SSO.tnoe(index_str)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(label)))
			   {
				   continue;
			   }
			   index=Integer.parseInt(index_str);
			   //if(index%100==0)
			   //{
				   //System.out.println(word+" "+index_str);
			  // }
			   if(index<1)
			   {
				   continue;
			   }
			   hm.put(index_str,label);			   
		   }
		   
		}
		catch(Exception e)
		{
			
		}	
		return hm;
	}
	
	
	
}
