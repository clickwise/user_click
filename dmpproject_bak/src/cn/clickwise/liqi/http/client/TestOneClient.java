package cn.clickwise.liqi.http.client;

import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class TestOneClient {

	public static void main(String[] args)
	{
		HttpClient httpclient = new DefaultHttpClient();
		//String seg_s="\"date\":\"20140224\",\"keyword\":\"大\"";
		//String seg_s="{\"bdcates\":[\"新闻资讯\"],\"bdkeys\":[\"凤凰网\"],\"time\":\"1399564801\",\"uid\":\"fcf4b1a142a7e5658ef41536b43d59f5\",\"datatype\":\"BAIDU\"}";
		//String seg_s="{\"bdcates\":[\"天气\"],\"bdkeys\":[\"天气预报\",\"锦州天气\",\"锦州天气预报\"],\"time\":\"1399564803\",\"uid\":\"4bf519bb22ece8a5e69918aa2c3e9258\",\"datatype\":\"BAIDU\"}";
		//String encode_seg_s=Base64.encodeBytes(seg_s.getBytes());
		
		//String seg_s="{\"attrs\":[\"定型\"],\"cates\":[\"护肤彩妆\",\"美发护发\",\"弹力素\"],\"items\":[\"啫喱\",\"啫喱水\"],\"time\":\"1399564830\",\"uid\":\"d6a540a9770e4fec0a375b11d9f370a3\",\"datatype\":\"TBSEARCH\"}";
		String seg_s="{\"keywords\":[\"啫喱\",\"啫喱水\"],\"adid\":\"001\"}";
		String encode_seg_s=URLEncoder.encode(seg_s);
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
		//encode_seg_s=encode_seg_s.replaceAll("[\n]+", "");
		//System.out.println("encode_seg_s:"+encode_seg_s);
	   // String prefix="http://42.62.29.24:8900/cate_tb/do?t=";
		// String prefix="http://192.168.110.186:11011/get";
		//String prefix="http://222.85.64.100:8901/cate_sw/do?t=";
		//String prefix="http://192.168.110.186:8000/adduserrecord?s=";
		//String prefix="http://192.168.110.186:8000/addadrecord?s=";
	    //String prefix="http://42.62.29.25:8000/matchad.json?uid=2e2a0482f760f609fdec2ddc5bc00ec7&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=adshow";
        //String prefix="http://42.62.29.25:8000/matchuser.json?adid=34&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=test";
		//String prefix="http://42.62.29.25:8000/deladrecord.json?adid=61&platform=test";
		//String prefix="http://192.168.110.186:8000/deluserrecord.json?uid=8df8747e744884eae51ef813d7035a7c&interval=1";
	    //String prefix="http://42.62.29.25:8000/matchuseradsimilarity.json?uid=ed7b4ae9183d54ac2c6388f413e892b7&adids=1,2,3,4,5,6&datatype=BAIDU,TBSEARCH&infotype=cates,attrs,items,bdcates,bdkeys&adinfotype=keywords&platform=adshow";

	    String prefix="http://42.62.29.25:8000/queryuserinfo.json?uid=ed7b4ae9183d54ac2c6388f413e892b7&query_type=BAIDU";
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
					System.out.println("con:"+con);
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
