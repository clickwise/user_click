package cn.clickwise.segmenter.stanford;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;

public class StanterSegStream {

	public HttpClient httpclient ;
	public HttpPost httppost;
	public 	HttpResponse response;
	
	public void init()
	{
		httpclient = new DefaultHttpClient();
	}
	//对一个title进行分词
	public String seg_title(String title)
	{
		//httpclient = new DefaultHttpClient();
		String seg_s=title;
		String encode_seg_s=URLEncoder.encode(seg_s);
		encode_seg_s=encode_seg_s.replaceAll("\\s+", "");
         String prefix="http://192.168.110.181:7000/seg?s="+encode_seg_s;

		String url=prefix;
		
		String con="";
		try{
			//HttpGet httpget = new HttpGet(url);
			httppost=new HttpPost(url);
			//System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			response = httpclient.execute(httppost);

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
					//System.out.println("con:"+UrlCode.getDecodeUrl(con));
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
		
		return UrlCode.getDecodeUrl(con);
	}
	
	//从标准输入读取，对读取的每一行进行分词
	public void seg_stream() throws Exception
	{
		//FileReader fr=new FileReader(new File(""));
		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		
		OutputStreamWriter osw=new OutputStreamWriter(System.out);
		PrintWriter pw=new PrintWriter(osw);
		
		String line="";
		String host="";
		String keywords="";
		String seg_keywords="";
		
		String[] seg_arr=null;
		while((line=br.readLine())!=null)
		{
		   if(SSO.tioe(line))
		   {
			   continue;
		   }
		   
		   seg_arr=line.split("\\s+");
		   if(seg_arr.length<2)
		   {
			   continue;
		   }
		   
		   host=seg_arr[0];
		   
		   keywords="";
		   for(int j=1;j<seg_arr.length;j++)
		   {
			   keywords=keywords+seg_arr[j]+" ";
		   }
		   //System.out.println("keywords:"+keywords);
		   keywords=keywords.trim();
		   seg_keywords=seg_title(keywords);   
		  // System.out.println("seg_keywords:"+seg_keywords);
		   pw.println(host+"\001"+seg_keywords);
		}
		isr.close();
		osw.close();
		br.close();
		pw.close();
	}
	
	public String readStdin()
	{
		String s="";
	    BufferedReader br = 
	    	      new BufferedReader( 
	    	       new InputStreamReader(System.in)); 
	    	   
	    	    try { 
	    	      while((s = br.readLine()).length() != 0) 
	    	        System.out.println(s); 
	    	      // An empty line terminates the program 
	    	    } catch(IOException e) { 
	    	      e.printStackTrace(); 
	    	    }
	   return s;
	}
	
	public static void main(String[] args) throws Exception
	{
		StanterSegStream sss=new StanterSegStream();
		sss.init();
		//String s=sss.readStdin();
		sss.seg_stream();
	}
	
}
