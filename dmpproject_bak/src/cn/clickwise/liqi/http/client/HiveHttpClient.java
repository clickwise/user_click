package cn.clickwise.liqi.http.client;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.clickwise.liqi.file.utils.FileWriterUtil;

public class HiveHttpClient extends HttpClientDefination {

	
	@Override
	public String getResponse(String request) {
		
		HttpClient httpclient = new DefaultHttpClient();
		String encode_request=Base64.encodeBytes(request.getBytes());
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_request=encode_request.replaceAll("\\s+", "");
		//encode_seg_s=encode_seg_s.replaceAll("[\n]+", "");
		System.out.println("encode_seg_s:"+encode_request);
	    //String prefix="http://222.85.64.100:8900/cate_tb/do?t=";
		 String prefix="http://"+server_ip+":"+server_port+"/"+method_name+"/do?t=";
		//String prefix="http://222.85.64.100:8901/cate_sw/do?t=";
		String url=prefix+encode_request;
		
		String con="";
		String de_con="";
		try{
			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				if(entity != null) {
					entity = response.getEntity();
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
					//System.out.println("con:"+con);
					de_con=new String(Base64.decode(con));
	    			//System.out.println("de_con:"+de_con);
				}
			}
			//con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";

			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		// TODO Auto-generated method stub
		return de_con;
	}

	public static void main(String[] args)
	{
		String req_s="\"date\":\"20140329\",\"keyword\":\"私人定制\"";
		HiveHttpClient hhc=new HiveHttpClient();
		hhc.set_server_ip("123.125.149.137");
		hhc.set_server_port(8909);
		hhc.set_method_name("hive");
		String output_file="temp/hive_opera/test.txt";
	    FileWriterUtil.writeContent(hhc.getResponse(req_s), output_file);
		//System.out.println(hhc.getResponse(req_s));
		
		
	}
	
}
