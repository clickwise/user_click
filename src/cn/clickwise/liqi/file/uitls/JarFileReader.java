package cn.clickwise.liqi.file.uitls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 读取 classpath 的文本文件，转换成各种数据结构，输入是classpath下的文件名
 * @author zkyz
 *
 */
public class JarFileReader {
	
	/**
	 * jarFile 转换为hashMap
	 * 输入文件为两列或一列
	 * 如果为两列，第一列为key、第二列为value
	 * 如果为一列，该列为key、value是自动编号
	 * @param fileName
	 * @return HashMap<String,String>
	 */
	public  HashMap<String,String> jarFile2Hash(String fileName) 
	{  
		HashMap<String,String> ht=new HashMap<String,String>();
		try
		{
	        InputStream in=this.getClass().getResourceAsStream("/"+fileName);//读jar包根目录下的fileName</span><span>  
	        Reader f = new InputStreamReader(in);         
	        BufferedReader fb = new BufferedReader(f);  
	        String line = fb.readLine();
	        line=line.trim();
	        int field_num=0;
	        String[] seg_arr=line.split("\001");
	        field_num=seg_arr.length;
	        
	        String key="";
	        String value="";
	        int index=0;
	        while(SSO.tnoe(line)) 
	        {
	        	if(field_num==2)
	        	{
	        	   key=line.substring(0,line.indexOf("\001")).trim();
				   value=line.substring(line.indexOf("\001")+1).trim();
				   if(!(ht.containsKey(key)))
				   {
				     ht.put(key, value);
				   }
	        	}
	        	else if(field_num==1)
	        	{
	        	   key=line;
	        	   value=(++index)+"";
	        	   if(!(ht.containsKey(key)))
	        	   {
	        	     ht.put(key, value);
	        	   }
	        	}
				line=fb.readLine();
	        }
	        fb.close();
	        f.close();
	        in.close();
        }
		catch (Exception e) 
		{
			e.printStackTrace();
		}  
        return ht;  
    }	
	
}
