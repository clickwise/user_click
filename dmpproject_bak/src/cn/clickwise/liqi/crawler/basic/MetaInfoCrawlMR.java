package cn.clickwise.liqi.crawler.basic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
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
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.liqi.file.utils.FileToArray;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 
 * @author zkyz
 *
 */

public class MetaInfoCrawlMR {
	public  ArrayList<Thread> extra_threads=new  ArrayList<Thread>();
	public  ArrayList<String> getTitleBat(String[] url_arr) throws Exception
	{

		  String line="";

		  String title="";
		  ArrayList<String> url_content=new ArrayList<String>();
		  String last_url="";
		  //ArrayList<Thread> started_threads=new ArrayList<Thread>();
		  ArrayList<ThreadGroup> started_groups=new ArrayList<ThreadGroup>();
		  Thread t=null;
		  for(int i=0;i<url_arr.length;i++)
		  {
			//  System.out.println("line:"+line);
			  line=url_arr[i];
			  line=line.trim();
			  Thread.sleep(50);
              Runnable r =  new  PageCrawlThreadMR(line,url_content);
              
              t =  new  Thread(r);
             // started_threads.add(t);
              started_groups.add(t.getThreadGroup());
              t.start();
              t.join();  
		  }
		  
		// Thread tt=null;
        // for(int i=0;i<started_threads.size();i++)
        // {
        //    tt=	started_threads.get(i);
        //    System.out.println(tt.getId()+" "+tt.isAlive()+" "+tt.getThreadGroup());
      //   }
         
         ThreadGroup tg=null;
         HashMap<String,String> eh=new HashMap<String,String>();
         for(int i=0;i<started_groups.size();i++)
         {
            tg=	started_groups.get(i);
           // Thread.sleep(10);
          //  tg.interrupt();
            Thread[] tgl=new Thread[tg.activeCount()];
            tg.enumerate(tgl);
            for(int j=0;j<tgl.length;j++)
            {
            	if(tgl[j]!=null)
            	{
            	// System.out.print(j+" "+tgl[j].getId()+" "+tgl[j].getName()+" ");
            	 //tgl[j].interrupt();
            	 if(!(eh.containsKey(tgl[j].getId()+"")))
            	 {
            		 extra_threads.add(tgl[j]);
                     eh.put(tgl[j].getId()+"", "1");
            	 }
            	}
            }
           // System.out.println();
           // System.out.println("tg "+i+"  :"+tg.activeCount());
         }
         
        Thread ct=null;
 	    for(int i=0;i<extra_threads.size();i++)
 	    {
 	    	ct=extra_threads.get(i);
 	    	System.out.println(ct.getName()+" "+ct.getId());
 	    	if(!((ct.getName().trim()).equals("main")))
 	    	{
 	    		ct.stop();
 	    	}
 	    }	    
         return url_content;
	}
	
	
	public static void main(String[] args) throws Exception {
		MetaInfoCrawlMR pc=new MetaInfoCrawlMR();
		String input_file="D:/projects/spread_data/zimeiti/data0304/url_20140304.2.test.txt";
		//String input_file="input/hosts/20140225/test_one.txt";
		String output_file="D:/projects/spread_data/zimeiti/data0304/url_20140304.2.crawl.txt";
	    String[] url_arr=FileToArray.fileToDimArr(input_file);
		for(int i=0;i<url_arr.length;i++)
		{
			System.out.println(i+"  "+url_arr[i]);
		}
	    
		ArrayList<String> res_list=pc.getTitleBat(url_arr);
		for(int i=0;i<res_list.size();i++)
		{
			System.out.println(i+"  "+res_list.get(i));
		}
		
      //  Thread ct=null;
       // ct=Thread.currentThread();
 
	    
	}
}


 class  PageCrawlThreadMR  implements  Runnable {
	    public String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
		
    static  String url="";
    ArrayList<String> url_content=null;
    private Timer timer=new Timer();
    private int limit;//限制

    public  PageCrawlThreadMR(String url,ArrayList<String> url_content) {
          this.url=url;
          this.url_content=url_content;
    }

    public   void  run() {
    	
    
            try  {
             
                String title=getWebPageTitle(url);
                //System.out.println("url :"+url);
                if(SSO.tnoe(title))
                {
                 url_content.add(url+"\001"+title);
                 System.out.println(url+":"+title);
                } 
            }  catch  (Exception e) {
            	    
                    e.printStackTrace();                
                    return;
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
