package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class PageCrawl {

	public String getWebPageTitle(String url) {
		String title = "";

		HttpClient httpclient = new DefaultHttpClient();

		url = url.trim();
		if ((url == null) || (url.length() < 5)) {
			return "";
		}
		if (url.indexOf("http") < 0) {
			url = "http://" + url;
		}
		String con = "";
		try {
			HttpGet httpget = new HttpGet(url);

			System.out.println("executing request ==================" + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();

				
				Header[] allhead = response.getAllHeaders();
				Header one_head = null;
				for (int i = 0; i < allhead.length; i++) {
					one_head = allhead[i];
					System.out.println(one_head.getName() + " "
							+ one_head.getValue());
				}
				if(allhead.length<1)
				{
					return "";
				}
					
				InputStream is = entity.getContent();
				String s = "";

				// InputStreamReader isr=new InputStreamReader(is);
				//BufferedInputStream bis = new BufferedInputStream(is);
				int c = 0;
				int lc = 0;
				byte[] bytes = new byte[1024000];
				is.read(bytes);
				String us = new String(bytes);
				//System.out.println("us:"+us);
				
				
				
				Pattern charset_pat=Pattern.compile("(?:(?:charset)|(?:CHARSET))=([^\">]*)");
				String charset="";
				one_head=response.getFirstHeader("Content-Type");
				Matcher charset_mat=charset_pat.matcher(one_head.getValue());
				//System.out.println("head charset:"+one_head.getValue());
				if(charset_mat.find())
				{
					charset=charset_mat.group(1);
				}
				charset=charset.trim();
				
				//System.out.println("charset1:"+charset);
				String ds="";
				if(!(charset.equals("")))
				{
					ds=new String(bytes,charset);
				}
				//System.out.println("ds:"+ds);
				if(charset.equals(""))
				{
					charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))[^<>]*?(?:(?:charset)|(?:CHARSET))=([^\">]*)[^<>]*?>");
					charset_mat=charset_pat.matcher(us);
					//System.out.println("us:"+us);
					if(charset_mat.find())
					{
						charset=charset_mat.group(1);
					}
				//	System.out.println("charset2:"+charset);
					ds=new String(bytes,charset);
					
				}
				
				Pattern title_pat=Pattern.compile("<title>([^<>]*?)</title>");
				Matcher title_mat=title_pat.matcher(ds);
				if(title_mat.find())
				{
					title=title_mat.group(1);
				}	
				ds=ds.trim();
				System.out.println("ds:"+ds);
				
				// System.out.println("Response content: "
				// + EntityUtils.toString(entity));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return title;
	}

	public void getTitleBat(String input_file,String output_file) throws Exception
	{
		  FileReader fr=new FileReader(new File(input_file));
		  BufferedReader br=new BufferedReader(fr);
		  String line="";
		  FileWriter fw=new FileWriter(new File(output_file));
		  PrintWriter pw=new PrintWriter(fw);

		  String title="";
		  while((line=br.readLine())!=null)
		  {
			  line=line.trim();
              title=getWebPageTitle(line);	
              System.out.println(line+" "+title);
	          pw.println(line+"\001"+title);
		  }
		  
		  fw.close();
		  pw.close();
		  br.close();
		  fr.close();
	}
	
	public static void main(String[] args) throws Exception {

		//PageCrawl pc=new PageCrawl();
		//String input_file="input/test_url.txt";
		//String output_file="temp/test_url_output.txt";
		//pc.getTitleBat(input_file, output_file);
		
		//String url="http://www.tuniu.com/";
		//String title="";
		//title=pc.getWebPageTitle(url);
		//System.out.println("title:"+title);
		
		
		HttpClient httpclient = new DefaultHttpClient();

		String url = "http://www.tuniu.com/guide/d-zhejiang-3400/jingdian";
		FileWriter fw = new FileWriter(new File("page_output/index5.html"));
		PrintWriter pw = new PrintWriter(fw);
		// httpclient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,
		// "UTF-8");
		String con = "";
		try {
			HttpGet httpget = new HttpGet(url);

			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				
				/*
				InputStream is = entity.getContent();
				Header[] allhead = response.getAllHeaders();
				Header one_head = null;
				for (int i = 0; i < allhead.length; i++) {
					one_head = allhead[i];
					System.out.println(one_head.getName() + " "
							+ one_head.getValue());
				}

				String s = "";

				// InputStreamReader isr=new InputStreamReader(is);
				BufferedInputStream bis = new BufferedInputStream(is);
				int c = 0;
				int lc = 0;
				byte[] bytes = new byte[1024];
				bis.read(bytes);
				String us = new String(bytes, "utf-8");
				System.out.println("us:" + us);
                */
				if (entity != null) {
					// 打印响应内容长度
					// System.out.println("Response content length: "
					// + entity.getContentLength());
					// 打印响应内容
					// System.out.println("Response content: "
					// + EntityUtils.toString(entity));
					// pw.println("Response content: "+EntityUtils.toString(entity));
					 con = EntityUtils.toString(entity,"utf-8");
					// con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";
					// System.out.println("con:"+con);
					 pw.println(con);

				}
			}
			// con="6Z6L57G7566x5YyFfOeyvuWTgeeUt+WMhXzkvJHpl7LnlLfljIUB5pac6Leo5YyF5YyFIOWls+WMheaWsOasvjIwMTPmlrDmrL4=";

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		fw.close();
		pw.close();
		
	}

}
