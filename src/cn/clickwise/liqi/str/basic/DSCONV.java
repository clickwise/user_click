package cn.clickwise.liqi.str.basic;

import java.util.Map;

import love.cq.util.MapCount;

/**
 * 数据结构的简单处理
 * @author zkyz
 *
 */
public class DSCONV {

	public static String[] deduplicate(String[] array)
	{
	    MapCount<String> mc=new MapCount<String>();
	    String token="";
	    
	    for(int i=0;i<array.length;i++)
	    {
	    	token=array[i].trim();
	    	if(SSO.tioe(token))
	    	{
	    		continue;
	    	}
	    	mc.add(token);
	    }
	    
	    Object[] objects=(mc.get().keySet().toArray());
	    String[] narray=new String[objects.length];
	    for(int i=0;i<narray.length;i++)
	    {
	    	narray[i]=objects[i]+"";
	    }
	    
		return narray;
	}
	
	public static Map<String,Integer> wordCount(String[] array)
	{
	    MapCount<String> mc=new MapCount<String>();
	    String token="";
	    
	    for(int i=0;i<array.length;i++)
	    {
	    	token=array[i];
	    	if(SSO.tioe(token))
	    	{
	    		continue;
	    	}
	    	mc.add(token);
	    }
	    
        return mc.get();
	}
	
	public static void main(String[] args)
	{
		String[] array={"a","b","c","a","d"};
		System.out.println(DS2STR.array2str(array));
		
		String[] narray=deduplicate(array);
		System.out.println(DS2STR.array2str(narray));
	}
	
	
}
