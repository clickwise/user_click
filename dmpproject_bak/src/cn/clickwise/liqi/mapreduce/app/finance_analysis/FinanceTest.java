package cn.clickwise.liqi.mapreduce.app.finance_analysis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class FinanceTest {

	
	public  HashMap<String,String> getFinanceMap() 
	{  
		HashMap<String,String> finance_map=new HashMap<String,String>();
		try
		{
	        InputStream in=this.getClass().getResourceAsStream("/finance_map.txt"); 
	        Reader f = new InputStreamReader(in);         
	        BufferedReader fb = new BufferedReader(f);  
	        String line = fb.readLine();  
	        String[] seg_arr=null;
	        while(SSO.tnoe(line)) 
	        {  
	        	line=line.trim();
	        	seg_arr=line.split("\\s+");
	        	if(seg_arr.length!=2)
	        	{
	        		continue;
	        	}
	        	String host_attr=seg_arr[0];
				String host=seg_arr[1];
				if(SSO.tnoe(host))
				{
					if(!(finance_map.containsKey(host)))
					{
				      finance_map.put(host, host_attr);
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
        return finance_map;  
    }
	
	public static void main(String[] args)
	{
		FinanceTest ft=new FinanceTest();
		HashMap<String,String> hm=ft.getFinanceMap();
		FileWriterUtil.writeHashMap(hm, "../temp/finance/ft.txt");
	}
}
