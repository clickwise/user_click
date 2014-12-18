package cn.clickwise.liqi.str.app;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

public class SaveAdInfo {
	
	public static void save_one_ad_record(String info)
	{
		HttpClient httpclient = new DefaultHttpClient();
		String encode_info=URLEncoder.encode(info);
		encode_info=encode_info.replaceAll("\\s+", "");
		System.out.println("encode_info:"+encode_info);
		String prefix="http://192.168.110.186:8000/addadrecord?s=";
		//String prefix="http://42.62.29.25:8000/addadrecord?s=";
		String url=prefix+encode_info;
		url=url+"&platform=test";
		String con="";
		try{
			HttpGet httpget = new HttpGet(url);
			HttpPost httppost=new HttpPost(url);
			//System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httppost);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println("statusCode:"+statusCode);
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					con = EntityUtils.toString(entity);
					con=con.replaceAll("\\s+", "");
					//System.out.println("con:"+con);
				}
			}		
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void  main(String[] args) throws Exception
	{
		String[] line_arr=FileToArray.dirToDimArr("D:/projects/admatch/ad_data");
		String eff_field="";
		
		System.out.println("line_arr.length:"+line_arr.length);
		String padded_info="";
		String[] filed_seg=null;
		String adid="";
		String keywords="";
		String[] key_seg=null;
		String keyword="";
		
		String json_str="";
	    for(int i=0;i<line_arr.length;i++)
	    {
	    	
	    	filed_seg=line_arr[i].split("\\s+");
	    	if(filed_seg.length<2)
	    	{
	    		continue;
	    	}
	    	adid=filed_seg[0].trim();
	    	keywords="";
	    	json_str="";
	    	json_str="{\"adid\":\""+adid+"\",\"keywords\":[";
	    	
	    	for(int j=1;j<filed_seg.length;j++)
	    	{
	    	  keywords=keywords+filed_seg[j];   	
	    	}
	    	
	    	key_seg=keywords.split(",");
	    	
	    	for(int j=0;j<key_seg.length-1;j++)
	    	{
	           keyword=key_seg[j].trim();
	           if(SSO.tioe(keyword))
	           {
	        	   continue;
	           }
	           json_str=json_str+"\""+keyword+"\",";
	    	}
	    	
	    	json_str=json_str+"\""+key_seg[key_seg.length-1]+"\"]}";
	    	save_one_ad_record(json_str);
	    	System.out.println(i+": "+line_arr[i]);
	    	System.out.println(i+": "+json_str);
	    }
	}
	
    public static String convert(String text) throws UnsupportedEncodingException {  
        return new String(text.getBytes("ANSI"));  
    }
	
}
