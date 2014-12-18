package cn.clickwise.liqi.str.app;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class SaveSingleAd {
	public static void save_one_ad_record(String info)
	{
		HttpClient httpclient = new DefaultHttpClient();
		String encode_info=URLEncoder.encode(info);
		encode_info=encode_info.replaceAll("\\s+", "");
		System.out.println("encode_info:"+encode_info);
		String prefix="http://42.62.29.25:8000/addadrecord?s=";
		//String prefix="http://42.62.29.25:8000/addadrecord?s=";
		String url=prefix+encode_info;
		url=url+"&platform=adshow_test";
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
	
	public static void main(String[] args)
	{
		String record="{\"adid\":\"30792\",\"keywords\":[\"保护套\",\"充电器\",\"其他手机配件\",\"屏幕贴膜\",\"手机座\",\"手机挂件\",\"手机电池\",\"手机贴膜\",\"手机零部件\",\"数据线\",\"线控耳机\"]}";	
		save_one_ad_record(record);
		
	}
}
