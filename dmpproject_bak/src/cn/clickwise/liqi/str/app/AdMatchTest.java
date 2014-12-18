package cn.clickwise.liqi.str.app;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;

import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
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

public class AdMatchTest {
	
	public static String test_one_user(String cookie)
	{
		HttpClient httpclient = new DefaultHttpClient();
		String encode_info=URLEncoder.encode(cookie);
		encode_info=encode_info.replaceAll("\\s+", "");
		//System.out.println("encode_info:"+encode_info);
		//String prefix="http://42.62.29.25:8000/matchad.json?uid=";
		String prefix="http://42.62.29.25:8000/matchad.json?uid=";
		
		/***
         * datatype: BAIDU,TBSEARCH
         * infotype: cates,attrs,items,bdcates,bdkeys
         * adinfotype: keywords
		 */
		String url=prefix+encode_info+"&platform=test";
		
		String con="";
		try{
			HttpGet httpget = new HttpGet(url);
			HttpPost httppost=new HttpPost(url);
			System.out.println("executing request " + httpget.getURI());
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
		return con;
	}
	
	public static String get_cookie(String info)
	{
		JSONObject jsonObject = JSONObject.fromObject(info);  
		Object bean = JSONObject.toBean(jsonObject); 
		String cookie="";
		try{
		  cookie=PropertyUtils.getProperty(bean, "uid")+""; 
		}
		catch(Exception e){}
		return cookie;
	}
	
	public static void  main(String[] args) throws Exception
	{
		String[] line_arr=FileToArray.dirToDimArr("D:/projects/admatch/user_data/data0508");
		String eff_field="";
		FileWriter fw=new FileWriter(new File("temp/admatch/log.txt"));
		PrintWriter pw=new PrintWriter(fw);
		
		System.out.println("line_arr.length:"+line_arr.length);
		String cookie="";
	    for(int i=0;i<100;i++)
	    {
	    	//pw.println("i="+i+"  "+line_arr[i]);
	    	//pw.flush();
	    	//if(i%100==0)
	    	//{
	    		System.out.println("i="+i);
	    	//}
	    	eff_field=SSO.afterStr(line_arr[i], "[INFO ] ZADD");
	    	eff_field=SSO.afterStr(eff_field, "{");
	    	eff_field="{"+eff_field;
	    	cookie=get_cookie(eff_field);
	    	//System.out.println("i="+i+":  "+add_datatype(eff_field));
	    	if(SSO.tioe(cookie))
	    	{
	    		continue;
	    	}
	    	Thread.sleep(10);
	    	String res_temp=test_one_user(cookie);
	    	if(res_temp.indexOf("nomatch")==-1)
	    	{
	    	  pw.println("i="+i+" cookie:"+cookie+" mres:"+res_temp);
	    	  pw.flush();
	    	}
	    }
	    
	    fw.close();
	    pw.close();
	
	}
	
	
	
}
