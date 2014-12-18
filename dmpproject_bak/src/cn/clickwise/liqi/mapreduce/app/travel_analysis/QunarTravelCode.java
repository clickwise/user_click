package cn.clickwise.liqi.mapreduce.app.travel_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;

import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

public class QunarTravelCode {

	public Hashtable<String,String> getAreaCountryMap()
	{
		Hashtable<String,String> hm=new Hashtable<String,String>();
	    String item="";
        String code="";
        String city="";
        String country="";
        String host="";
        
		FileReader fr=null;
		BufferedReader br=null;
			
		String[] seg_arr=null;		
		String input_file="D:/projects/项目/travel/part-00000";
		String input_file2="D:/projects/项目/travel/part-00002";
		try{
		   fr=new FileReader(new File(input_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(SSO.tioe(item))
			   {
				   continue;
			   }
			   seg_arr=item.split("\001");
			   if(seg_arr.length!=4)
			   {
				   continue;
			   }
			   code=seg_arr[0].trim();
			   city=seg_arr[1].trim();
			   country=seg_arr[2].trim();
			   host=seg_arr[3].trim();

			   if(SSO.tioe(city))
			   {
				   continue;
			   }
			   
			   if(SSO.tioe(country))
			   {
				   continue;
			   }
			   
			   city=regular_city(city);
			   country=regular_city(country);
			   if(SSO.tioe(city)||SSO.tioe(country))
			   {
				   continue;
			   }
			   if(!(hm.containsKey(city)))
			   {
			     hm.put(city,country);
			   }   
		   } 
		   fr.close();
		   br.close();
		   
		   
		   fr=new FileReader(new File(input_file2));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(SSO.tioe(item))
			   {
				   continue;
			   }
			   seg_arr=item.split("\001");
			   if(seg_arr.length!=4)
			   {
				   continue;
			   }
			   code=seg_arr[0].trim();
			   city=seg_arr[1].trim();
			   country=seg_arr[2].trim();
			   host=seg_arr[3].trim();

			   city=regular_city(city);
			   country=regular_city(country);
			   if(SSO.tioe(city)||SSO.tioe(country))
			   {
				   continue;
			   }
			   if(SSO.tioe(city))
			   {
				   continue;
			   }
			   
			   if(SSO.tioe(country))
			   {
				   continue;
			   }
			   if(!(hm.containsKey(city)))
			   {
			     hm.put(city,country);
			   }   
		   } 
		   fr.close();
		   br.close();
		   
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}	
		return hm;
	}
	
	public String regular_city(String city)
	{
		String rcity="";
		city=city.trim();
		if(city.length()<1)
		{
			return null;
		}
	      
		city=SSO.truncAfterStr(city, "（");
		String last_char=city.charAt(city.length()-1)+"";
		if(last_char.equals("市"))
		{
			city=city.substring(0,city.length()-1);
			rcity=city;
		}
		else
		{
			rcity=city;
		}
		return rcity;
	}
	
	public void qunar_from_ctrip()
	{
	    String item="";
        String code="";
        String city="";
        String country="";
        String host="";
		FileReader fr=null;
		BufferedReader br=null;
			
		String[] seg_arr=null;		
		String input_file="D:/projects/项目/travel/part-00001";
		Hashtable<String,String> hm=getAreaCountryMap();
	
		ArrayList<String> arrlist=new ArrayList<String>();
		String newLine="";
		try{
		   fr=new FileReader(new File(input_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(SSO.tioe(item))
			   {
				   continue;
			   }
			   
			   newLine="";
			   seg_arr=item.split("\001");
			   if(seg_arr.length!=4)
			   {
				   continue;
			   }
			   code=seg_arr[0].trim();
			   city=seg_arr[1].trim();
			   country=seg_arr[2].trim();
			   host=seg_arr[3].trim();
               System.out.println(code+" "+city+" "+country+" "+host);
			   city=regular_city(city);
			   System.out.println("rcity:"+city);
			   if(SSO.tioe(city))
			   {
				   continue;
			   }
			   
			   if(SSO.tioe(country)|country.equals("unknown"))
			   {
				   country=hm.get(city);
			   }
			   else
			   {
				   country="unknown";
			   }
			   if(SSO.tioe(country))
			   {
				   continue;
			   }
			   
				city=city.trim();
			   
			   city=regular_city(city);
			   country=regular_city(country);
			   System.out.println("city:"+city+" country:"+country);
			   if(SSO.tioe(host))
			   {
				   continue;
			   }
			   System.out.println("2 "+code+" "+city+" "+country+" "+host);
			   newLine=code+"\001"+city+"\001"+country+"\001"+host;
			   newLine=newLine.trim();
			   if(SSO.tnoe(newLine))
			   {
				   arrlist.add(newLine);
			   }
		   } 
		   fr.close();
		   br.close();
		   
		   FileWriterUtil.writeArrayList(arrlist, "D:/projects/项目/travel/qunar_city_country.txt", false);
		   	   	   
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}	
		
	}
	
	public static void main(String[] args)
	{
		QunarTravelCode qtc=new QunarTravelCode();
		qtc.qunar_from_ctrip();
	}
	
	
}
