package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import cn.clickwise.liqi.str.basic.DS2STR;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.net.http.admatchtest.AddUserHistoryMatchTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SaveUserHistoryMatch {
	
	public AddUserHistoryMatchTest auhmt=new AddUserHistoryMatchTest();
	public String datatype="BAIDU,TBSEARCH,HOSTTITLE";
	public String infotype="cates,attrs,items,bdcates,bdkeys,refer_host,url_host,url_title,host_cate";
	public String adinfotype="keywords";
	public String platform="adshow";
	
	public void traverse_log(File log) {
		FileReader fr = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;
         
		BufferedReader br = null;
		String record = "";
		String json_record = "";
		String[] seg_arr=null;
        String cookie="";
        String adinfo="";
		try {

			// fr=new FileReader(input_file);
			fis = new FileInputStream(log.getAbsolutePath());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while ((record = br.readLine()) != null) {
              if(SSO.tioe(record))
              {
            	 continue;
              }
              record=record.trim();	
        	  
              seg_arr=record.split("\001");
        	  if(seg_arr.length!=2)
        	  {
        		  continue;
        	  }
              cookie=seg_arr[0].trim();
              adinfo=seg_arr[1].trim();
              if(adinfo.equals("nomatch"))
              {
            	  continue;
              }
              save_record(cookie, adinfo);
			}
			br.close();

		} catch (Exception e) {
		}
	}
	
	public void save_record(String cookie,String adinfo)
	{
		HashMap<String,String> adscore=json2map(adinfo);
		if(adscore==null)
		{
			return;
		}
		String adid="";
		String similarity="";
		for(Entry<String,String> entry: adscore.entrySet())
		{
			auhmt.testAddUserHistoryMatch(cookie, platform+"_"+entry.getKey(), entry.getValue(), datatype, infotype, adinfotype);
		}	
	}
	
	public HashMap<String,String> json2map(String jstr)
	{
		HashMap<String,String> adscore=new HashMap<String,String>();
		String jstr_content=SSO.midstrs(jstr, "{", "}");
		String[] seg_arr=jstr_content.split(",");
		if(seg_arr==null)
		{
			return null;
		}
		String item="";
		
		String adid="";
		String score="";
		String[] item_seg=null;
		
		for(int i=0;i<seg_arr.length;i++)
		{
			item=seg_arr[i].trim();
		    if(SSO.tioe(item))
		    {
		    	continue;
		    }
			item=item.trim();
			item_seg=item.split(":");
		    if(item_seg.length!=2)
		    {
		    	continue;
		    }
		    
		    adid=item_seg[0].trim();
		    score=item_seg[1].trim();
		    adid=adid.replaceAll("\"", "");
		    score=score.replaceAll("\"", "");
            if(adid.indexOf("test")<0)
            {
            	adscore.put(adid, score);
            }
		}
			
		return adscore;
	}
	
	
	public static void main(String[] args)
	{
        if(args.length!=1)
        {
        	System.err.println("Usage:<input_file>");
        	System.exit(1);
        }
		       
		SaveUserHistoryMatch suhm=new SaveUserHistoryMatch();
		suhm.traverse_log(new File(args[0]));
	}
	
	
}
