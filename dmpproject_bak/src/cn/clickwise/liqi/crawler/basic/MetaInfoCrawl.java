package cn.clickwise.liqi.crawler.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.clickwise.liqi.str.basic.SSO;

public class MetaInfoCrawl {

	public static int page_num=0;
	public static int  max_page_num=390;
	public static Thread t ;
	
	public void getTitleBat(String input_file,String output_file) throws Exception
	{
		  FileReader fr=new FileReader(new File(input_file));
		  BufferedReader br=new BufferedReader(fr);
		  String line="";
		  FileWriter fw=new FileWriter(new File(output_file));
		  PrintWriter pw=new PrintWriter(fw);

		  String title="";
		  ArrayList<String> url_content=new ArrayList<String>();
		  String last_url="";
		  while((line=br.readLine())!=null)
		  {
			//  System.out.println("line:"+line);
			  line=line.trim();

			  page_num++;
              Runnable r =  new  PageCrawlThread(line,url_content);
              t =  new  Thread(r);       
              t.start();
              
              Thread.sleep(50);
            //  System.out.println(line+" "+title);
	        //  pw.println(line+"\001"+title);
              
              if(url_content.size()>20)
              {
            	  for(int j=0;j<url_content.size();j++)
            	  {
            	    pw.println(url_content.get(j));
            	  }
            	  pw.flush();
            	//  page_num=page_num+url_content.size();
            	  url_content=new ArrayList<String>();
            	  
              }
              last_url=line.trim();
		  }

    	  for(int j=0;j<url_content.size();j++)
    	  {
    	    pw.println(url_content.get(j));
    	    pw.flush();
    	  }
    	  System.out.println("page_num："+MetaInfoCrawl.page_num);
    	  page_num=page_num+url_content.size();
    	 // pw.flush();
    	  url_content=new ArrayList<String>();		  
		  fw.close();
		  pw.close();
		  br.close();
		  fr.close();

	}
	
	
	public static void main(String[] args) throws Exception {
		MetaInfoCrawl pc=new MetaInfoCrawl();
		String input_file="D:/projects/spread_data/zimeiti/data0304/url_20140304.2.txt";
		//String input_file="input/hosts/20140225/test_one.txt";
		String output_file="D:/projects/spread_data/zimeiti/data0304/url_20140304.2.crawl.txt";
		pc.getTitleBat(input_file, output_file);
	}
}


  class  PageCrawlThread  implements  Runnable {
	    public String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
		
    static  String url="";
    ArrayList<String> url_content=null;
    private Timer timer=new Timer();
    private int limit;//限制

    public  PageCrawlThread(String url,ArrayList<String> url_content) {
          this.url=url;
          this.url_content=url_content;
    }

    public   void  run() {
    	
    
            try  {
                System.out.println("page_num："+MetaInfoCrawl.page_num);
                if(MetaInfoCrawl.page_num>MetaInfoCrawl.max_page_num)
            	//if(url.trim().equals(MetaInfoCrawl.tail_url))
                {
                  Thread.sleep(10000);
              	  System.exit(1);
                }
                String title=getWebPageTitle(url);
                System.out.println("url :"+url);
                if(SSO.tnoe(title))
                {
                 url_content.add(url+"\001"+title);
                 System.out.println("url: "+title);
                }
          
		 
            }  catch  (Exception e) {
                    e.printStackTrace();
            }
            
            
    }
    
	public static String getWebPageTitle(String url) {
		String title = "";
		String keywords="";
		String description="";
		System.out.println("new httpclient :"+url);
		HttpClient httpclient = new DefaultHttpClient();

		//设置代理
		double ran = Math.random();
		int rani = -1;
		rani = (int) (ran * 10);
		//HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		//		proxy);
		//httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
		httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
		httpclient.getParams().setParameter("http.socket.timeout",60000);

		httpclient.getParams().setParameter("http.connection.timeout",60000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",60000);
	
	   System.out.println("crawling the url :"+url);

		url = url.trim();
		if ((url == null) || (url.length() < 5)) {
			return "";
		}
		if (url.indexOf("http") < 0) {
			url = "http://" + url;
		}
		String con = "";
		try {
			System.out.println("httpget :"+url);
			HttpGet httpget = new HttpGet(url);

			//System.out.println("executing request ==================" + httpget.getURI());
			// 执行get请求.
			System.out.println("exec the httpget :"+url);
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
					//System.out.println(one_head.getName() + " "
					//		+ one_head.getValue());
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
				byte[] bytes = new byte[102400];
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
				ds=ds.trim();
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
				
				ds=ds.trim();
				Pattern title_pat=Pattern.compile("<title>([^<>]*?)</title>");
				Matcher title_mat=title_pat.matcher(ds);
				title=title.trim();
				if(title_mat.find())
				{
					title=title_mat.group(1);
				}
			  
				//System.out.println("ds:"+ds);
				Pattern keywords_pat=Pattern.compile("<meta(?s)[^<>]*?name=\"keywords\"(?s)[^<>*]?content=\\s*\"([^<>]*)?\"[^<>]*?>");
				Matcher keywords_mat=keywords_pat.matcher(ds);
				if(keywords_mat.find())
				{
					keywords=keywords_mat.group(1);
				}
				
				Pattern description_pat=Pattern.compile("<meta(?s)[^<>]*?name=\"description\"(?s)[^<>*]?content=\\s*\"([^<>]*)?\"[^<>]*?>");
				Matcher description_mat=description_pat.matcher(ds);
				if(description_mat.find())
				{
					description=description_mat.group(1);
				}
				
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
      //  if(title.length()>0)
		//System.out.println(url+":"+title);
		if(!(SSO.tnoe(title)))
		{
			title="";
		}
		
		if(!(SSO.tnoe(keywords)))
		{
			keywords="";
		}
		
		if(!(SSO.tnoe(description)))
		{
			description="";
		}
		title=title.trim();
		keywords=keywords.trim();
		description=description.trim();
		return title+"\001"+keywords+"\001"+description;
	}
	
	
	public String getWebPageMeta(String url) {
		String source="";
		HttpClient httpclient = new DefaultHttpClient();
	
		double ran = Math.random();
	    String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
		int rani = -1;
		rani = (int) (ran * 10);
		HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,1000);
		
		
		url = url.trim();
		if ((url == null) || (url.length() < 5)) {
			return "";
		}
		if (url.indexOf("http") < 0) {
			url = "http://" + url;
		}
		
		String con = "";
		String ds="";
		try {
			HttpGet httpget = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			Pattern charset_pat=Pattern.compile("(?:(?:charset)|(?:CHARSET))=([^\">]*)");
			String charset="";
			Header type_head = null;
			type_head=response.getFirstHeader("Content-Type");
			
			String thlv=type_head.getValue().toString().toLowerCase();
			Matcher charset_mat=charset_pat.matcher(thlv);
			//System.out.println("Content-Type:"+thlv);
			
			if(thlv.indexOf("text/html")<0)
			{
				System.err.println("["+url+"] 不是普通网页");
				return "";
			}
			
			if(charset_mat.find())
			{
				charset=charset_mat.group(1);
			}
			charset=charset.trim();
			//System.out.println("charset1:"+charset);
			
			charset=charset.trim();
			
			
			byte[] bytes = new byte[10240];
			InputStream is = entity.getContent();
			is.read(bytes);
			String us = new String(bytes);
			if(charset.equals(""))
			{
				charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))[^<>]*?(?:(?:charset)|(?:CHARSET))=([^\">]*)[^<>]*?>");
				charset_mat=charset_pat.matcher(us.toLowerCase());
				//System.out.println("us:"+us);
				if(charset_mat.find())
				{
					charset=charset_mat.group(1);
				}
				//System.out.println("charset2:"+charset);
				ds=new String(bytes,charset);
			}
					
			
			// 获取响应状态
			
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				entity = response.getEntity();
				if (entity != null) {

					 con = EntityUtils.toString(entity,charset);
					//con = EntityUtils.toString(entity);
                     source=ds+con;
				}
			}
			

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}		
		/*
		Pattern title_pat=Pattern.compile("<title>([^<>]*?)</title>");
		Matcher title_mat=title_pat.matcher(ds);
		String title="";
		if(title_mat.find())
		{
			title=title_mat.group(1);
		}
		String meta_info=title;
		*/
		
		Document doc = Jsoup.parse(source);
		String title = doc.head().select("title").text();  
		String keywords = doc.head().select("meta[name=keywords]").attr("content");
		String description=doc.head().select("meta[name=description]").attr("content");
	
		String meta_info="";
		meta_info=title+"\001"+keywords+"\001"+description;
		
		return meta_info;
	}
	
}
