package cn.clickwise.segmenter.stanford;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.clickwise.liqi.str.edcode.UrlCode;

public class StanterSegHttpClient {

	public static void main(String[] args)
	{
		HttpClient httpclient = new DefaultHttpClient();

		String seg_s="夏新款大码高中学生小凉鞋女平跟2014网纱松糕厚底少女高帮白色鞋";
		String encode_seg_s=URLEncoder.encode(seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
         String prefix="http://42.62.29.25:7000/seg?s="+encode_seg_s;

		String url=prefix;
		
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
					// 打印响应内容长度
					// System.out.println("Response content length: "
					// + entity.getContentLength());
					// 打印响应内容
					// System.out.println("Response content: "
					// + EntityUtils.toString(entity));
					// pw.println("Response content: "+EntityUtils.toString(entity));
					con = EntityUtils.toString(entity);
					//con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";
					con=con.replaceAll("\\s+", "");
					System.out.println("con:"+UrlCode.getDecodeUrl(con));
					//String de_con=new String(Base64.decode(con));
	    			//System.out.println("de_con:"+de_con);
				}
			}
			//con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
