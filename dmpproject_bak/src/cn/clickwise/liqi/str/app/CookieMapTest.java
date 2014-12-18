package cn.clickwise.liqi.str.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.clickwise.liqi.str.basic.SSO;

public class CookieMapTest {

	static Logger logger = Logger.getLogger(CookieMapTest.class.getName());
	
	public static String getOTHERTYPE(String type)
	{
		if(type.equals("BAIDU"))
		{
			return "TBSEARCH";
		}
		else if(type.equals("TBSEARCH"))
		{
			return "BAIDU";
		}		
		return null;
	}
	public static void main(String[] args) throws Exception
	{
		FileReader fr=new FileReader(new File("D:/projects/admatch/20140418/bdtb_look.txt"));
		BufferedReader br=new BufferedReader(fr);
		
		String line="";
		String[] seg_arr=null;
		String cookie="";
		String DTYPE="";
		String ping_cook="";
		HashMap<String,String> hm=new HashMap<String,String>();

		String other_type="";
		String ping_other_cook="";
	    while((line=br.readLine())!=null)
	    {	    	
	    	seg_arr=line.split("\t");
	    	if(seg_arr.length<2)
	    	{
	    		continue;
	    	}
	        cookie=seg_arr[0].trim();
	        DTYPE=seg_arr[1].trim();
	        ping_cook=cookie+DTYPE;
	        other_type=getOTHERTYPE(DTYPE);
	        if(SSO.tioe(other_type))
	        {
	        	continue;
	        }
	        
	        if(!(hm.containsKey(ping_cook)))
	        {
	        	hm.put(ping_cook, line);
	        }
	        else
	        {
	        	ping_other_cook=cookie+other_type;
	        	if(hm.containsKey(ping_other_cook))
	        	{
	              logger.info("other:"+hm.get(ping_other_cook)+" this:"+line);
	        	}
	        }
	    }
		
		
		
		
	}
	
}
