package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class SWAHttpSimpleClient {

	public static void main(String[] args)
	{

		HttpClient httpclient = new DefaultHttpClient();
		String seg_s="光荣使命单机破解版";
		String encode_seg_s=Base64.encodeBytes(seg_s.getBytes());
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
		//encode_seg_s=encode_seg_s.replaceAll("[\n]+", "");
		//System.out.println("encode_seg_s:"+encode_seg_s);
	   // String prefix="http://222.85.64.100:8900/cate_tb/do?t=";
		String prefix="http://118.26.200.132:8901/cate_sw/do?t=";
		//String prefix="http://222.85.64.100:8901/cate_sw/do?t=";
		String url=prefix+encode_seg_s;
		
		String con="";
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
					//System.out.println("con:"+con);
					String de_con=new String(Base64.decode(con));
	    			System.out.println("de_con:"+de_con);
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
