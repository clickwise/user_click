package cn.clickwise.user_click.user_features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.clickwise.admatch.MatchTool;
import cn.clickwise.liqi.file.uitls.JarFileReader;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;
import cn.clickwise.net.http.URIAnalysis;
import cn.clickwise.net.http.admatchtest.AdMatchTestBase;
import cn.clickwise.user_click.seg.AnsjSeg;

/**
 * 从日志数据提取用户特征 输入dsp用户访问日志数据 输出用户数据的标准格式
 * 
 * @author zkyz
 */

public class UserLogFeatures extends AdMatchTestBase{

	public AnsjSeg ansjseg=null;
	public String method="/adduserrecord?s=";
	
	public void init()
	{
		JarFileReader jfr=new JarFileReader();
		String seg_dict_file="five_dict_uniq.txt";
		String stop_dict_file="cn_stop_words_utf8.txt";
		HashMap<String,String> seg_dict=jfr.jarFile2Hash(seg_dict_file);
		HashMap<String,String> stop_dict=jfr.jarFile2Hash(stop_dict_file);
		ansjseg=new AnsjSeg();
		ansjseg.setSeg_dict(seg_dict);
		ansjseg.setStop_dict(stop_dict);
	}
	
	public String record2features(String uri, String head) {
		HashMap<String, String> param_map = MatchTool.convert_params(uri, head);
        String uid="";
        String hurl="";
        String refer="";
        String title="";
        
        uid=param_map.get("uid");
        hurl=param_map.get("hurl");
        refer=param_map.get("refer");
        title=param_map.get("title");
		
        if((SSO.tioe(hurl))&&(SSO.tioe(refer))&&(SSO.tioe(title)))
        {
        	return "";
        }
		return user_host(uid,hurl,refer,title);
	}

	public String user_host(String uid,String url, String refer,String title) {
		JSONObject json = new JSONObject();
		
		try {
			String url_host = URIAnalysis.getHost(url);
			String refer_host = URIAnalysis.getHost(refer);
			if (SSO.tnoe(url_host)) {
				json.put("url_host", url_host);
			} else {
				json.put("url_host", "NA");
			}

			if (SSO.tnoe(refer_host)) {
				json.put("refer_host", refer_host);
			} else {
				json.put("refer_host", "NA");
			}

			json.put("uid", uid);
			json.put("datatype", "HOSTTITLE");
			json.put("time", (TimeOpera.getCurrentTimeLong()/1000)+"");
			
			
		    JSONArray jsontitle = new JSONArray();
		    
		    if(SSO.tioe(title))
		    {
		    	jsontitle.put("NA");
		    }
		    else{
		    	String seg_title=ansjseg.seg(title);
		        String[] seg_arr=seg_title.split("\\s+");
			    if((seg_arr==null)||(seg_arr.length<1))
			    {
			    	jsontitle.put("NA");
			    }
			    else
			    {
			    	for(int j=0;j<seg_arr.length;j++)
			    	{
			    		if(SSO.tioe(seg_arr[j]))
			    		{
			    			continue;
			    		}
			    		jsontitle.put(seg_arr[j]);
			    	}
			    }
		    }
		  
		    json.put("title", jsontitle);
		    	
		} catch (Exception e) {
                   System.out.println(e.getMessage());
		}

		return json.toString();
	}


	public void traverse_log(File log)
	{
		FileReader fr = null;
		FileInputStream fis = null;
		InputStreamReader isr = null;

		BufferedReader br = null;
		String record="";
	    String json_record="";	
		try {
			// fr=new FileReader(input_file);
			fis = new FileInputStream(log.getAbsolutePath());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			while((record=br.readLine())!=null)
			{
				json_record=record2features(record,"/AddUserRec?");
				if(SSO.tioe(json_record))
				{
					continue;
				}
				addUserRecord(json_record);				
			}
		} catch (Exception e) {
		}
	}
	
	public void addUserRecord(String text)
	{
		String encode_text=URLEncoder.encode(text);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_text=encode_text.replaceAll("\\s+", "");
		String url=url_prefix+method+encode_text;
		String response=hct.postUrl(url);
		System.out.println("response:"+response);
	}
	
	public String user_se() {

		return null;
	}

	
	public static void main(String[] args)
	{
		if(args.length<1)
		{
			System.err.println("Usage:<UserLogFeatures> <log_file>");
			System.exit(1);
		}
		
		UserLogFeatures ulf=new UserLogFeatures();
		ulf.init();
		ulf.traverse_log(new File(args[0]));
	}
	
}
