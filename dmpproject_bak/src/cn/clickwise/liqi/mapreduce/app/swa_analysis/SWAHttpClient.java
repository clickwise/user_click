package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class SWAHttpClient {
	
	public String get_one_word(String sw)
	{
		String res="";
		HttpClient httpclient = new DefaultHttpClient();
		String seg_s=sw;
		String encode_seg_s=Base64.encodeBytes(seg_s.getBytes());
		//System.out.println("encode_seg_s:"+encode_seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
		//encode_seg_s=encode_seg_s.replaceAll("[\n]+", "");
		//System.out.println("encode_seg_s:"+encode_seg_s);
	    // String prefix="http://222.85.64.100:8900/cate_tb/do?t=";
		 String prefix="http://192.168.110.181:8901/cate_sw/do?t=";
		//String prefix="http://222.85.64.100:8901/cate_sw/do?t=";
		String url=prefix+encode_seg_s;
		
		String con="";
		try{
			HttpGet httpget = new HttpGet(url);
			//System.out.println("executing request " + httpget.getURI());
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
					res=de_con;
	    			//System.out.println("de_con:"+de_con);
				}
			}
			//con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";

			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return res;
	}
	
	public static void main(String[] args) throws Exception
	{
		SWAHttpClient swa = new SWAHttpClient();

		//FileReader fr=new FileReader(new File(""));
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line =swa.get_one_word(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(line+"\001"+new_line);

		}

		br.close();
		isr.close();
		osw.close();
		pw.close();
		//fr.close();
		
		
	}
}
