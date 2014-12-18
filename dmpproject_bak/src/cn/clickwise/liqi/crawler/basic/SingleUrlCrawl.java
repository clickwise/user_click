package cn.clickwise.liqi.crawler.basic;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.clickwise.liqi.str.basic.SSO;

/**
 * 抓取一个url，
 * 可选方法：抓取网页源代码
 *         抓取网页title  
 *         抓取网页meta info 
 *         抓取网页重定向url  
 *         允许重定向抓取网页源代码  
 * 配置项：
 *        是否使用代理，代理ip列表        
 * @author zkyz
 *
 */
public class SingleUrlCrawl {


    /**
     * 读取网页源代码,
     * url:待抓取的url地址
     * useProxy：是否使用代理
     * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
     * @param url
     * @param useProxy
     * @param proxy_arr
     * @return
     */
	public static String getSource(String url,boolean useProxy,String[] proxy_arr)
	{
		String source="";
		HttpClient httpclient = new DefaultHttpClient();
        if(!(SSO.tnoe(url)))
        {
        	return "";
        }
		url=format_url(url);
		
		if ((isValidUrl(url)) == false) {
			return "";
		}
		
		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    
	      if(proxy_arr==null)
	      {
	    	  proxy_arr=proxy_hosts;
	      }
		  int rani = -1;
		  rani = (int) (ran * (proxy_arr.length));
		  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
		  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		}
		
		httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
		httpclient.getParams().setParameter("http.socket.timeout",60000);

		httpclient.getParams().setParameter("http.connection.timeout",60000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",60000);

		String con = "";
		
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
			
			
			byte[] bytes = new byte[1024];
			InputStream is = entity.getContent();
			is.read(bytes);
			String us = new String(bytes);
			String ds="";
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
		return source;
	}
	
	
    /**
     * 读取网页纯文本,
     * url:待抓取的url地址
     * useProxy：是否使用代理
     * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
     * @param url
     * @param useProxy
     * @param proxy_arr
     * @return
     */
	public static String getSourceText(String url,boolean useProxy,String[] proxy_arr)
	{
		String source="";
		HttpClient httpclient = new DefaultHttpClient();
        if(!(SSO.tnoe(url)))
        {
        	return "";
        }
		url=format_url(url);
		
		if ((isValidUrl(url)) == false) {
			return "";
		}
		
		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    
	      if(proxy_arr==null)
	      {
	    	  proxy_arr=proxy_hosts;
	      }
		  int rani = -1;
		  rani = (int) (ran * (proxy_arr.length));
		  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
		  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		}
		
		httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
		httpclient.getParams().setParameter("http.socket.timeout",60000);

		httpclient.getParams().setParameter("http.connection.timeout",60000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",60000);

		String con = "";
		
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
			
			
			byte[] bytes = new byte[1024];
			InputStream is = entity.getContent();
			is.read(bytes);
			String us = new String(bytes);
			String ds="";
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
		source=getFilterContent(source);
		return source;
	}
	
	/**
     * 读取网页title,
     * url:待抓取的url地址
     * useProxy：是否使用代理
     * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
	 * @param url
	 * @param useProxy
	 * @param proxy_arr
	 * @return
	 */
	public static String getWebPageTitle(String url,boolean useProxy,String[] proxy_arr) {
		
        if(!(SSO.tnoe(url)))
        {
        	return "";
        }
        
		if ((isValidUrl(url)) == false) {
			return "";
		}
		
		url=format_url(url);
		
		String title = "";
		String keywords="";
		String description="";
		//System.out.println("new httpclient :"+url);
		HttpClient httpclient = new DefaultHttpClient();


		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    
	      if(proxy_arr==null)
	      {
	    	  proxy_arr=proxy_hosts;
	      }
		  int rani = -1;
		  rani = (int) (ran * (proxy_arr.length));
		  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
		  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		}
	
		httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  6000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  6000);
		httpclient.getParams().setParameter("http.socket.timeout",6000);

		httpclient.getParams().setParameter("http.connection.timeout",6000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",6000);
		
		
		

		String con = "";
		try {
			//System.out.println("httpget :"+url);
			HttpGet httpget = new HttpGet(url);

			//System.out.println("executing request ==================" + httpget.getURI());
			// 执行get请求.
			//System.out.println("exec the httpget :"+url);
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
		
				
				System.out.println("charset1:"+charset);
				String ds="";
				if(!(charset.equals("")))
				{
					ds=new String(bytes,charset);
				}
				ds=ds.trim();
				//System.out.println("ds:"+ds);
				if(charset.equals(""))
				{
					//charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))[^<>]*?(?:(?:charset)|(?:CHARSET))=([^\">]*)[^<>]*?>");
					charset_pat=charset_pat=Pattern.compile("<(?:(?:meta)|(?:META)|(?:Meta))\\s*[^<>]*?(?:(?:charset)|(?:CHARSET))=\\s*([^>\\s]*)[^<>]*?\\s*>");
					charset_mat=charset_pat.matcher(us);
					//System.out.println("us:"+us);
				
					if(charset_mat.find())
					{
						charset=charset_mat.group(1);
					}
					charset=charset.replaceAll("\"", "");
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
	
	/**
    /**
     * 读取网页meta信息,
     * url:待抓取的url地址
     * useProxy：是否使用代理
     * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
	 * @param url
	 * @param useProxy
	 * @param proxy_arr
	 * @return
	 */
	public static String getWebPageMeta(String url,boolean useProxy,String[] proxy_arr) {
		
        if(!(SSO.tnoe(url)))
        {
        	return "";
        }
		if ((isValidUrl(url)) == false) {
			return "";
		}
		
		url=format_url(url);
		
		String source="";
		HttpClient httpclient = new DefaultHttpClient();
	
		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_hosts = { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    
	      if(proxy_arr==null)
	      {
	    	  proxy_arr=proxy_hosts;
	      }
		  int rani = -1;
		  rani = (int) (ran * (proxy_arr.length));
		  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
		  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		}
		
		
		httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
		httpclient.getParams().setParameter("http.socket.timeout",60000);

		httpclient.getParams().setParameter("http.connection.timeout",60000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",60000);
		
		
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
	
	/**
     * 读取网页源代码,允许重定向
     * url:待抓取的url地址
     * useProxy：是否使用代理
     * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
	 * @param url
	 * @param useProxy
	 * @param proxy_arr
	 * @return
	 */
	 public static String getRedirectInfo(String url,boolean useProxy,String[] proxy_arr){
		 
	        if(!(SSO.tnoe(url)))
	        {
	        	return "";
	        }
	        
			if ((isValidUrl(url)) == false) {
				return "";
			}
			url=format_url(url);
			
		 
		  String red_info="";
		  HttpClient httpclient = new DefaultHttpClient();
		  
			if(useProxy==true)
			{
			  double ran = Math.random();
		      String[] proxy_hosts = { 
					"122.72.111.98", "122.72.76.132",
					 "122.72.11.129", "122.72.11.130",
					"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
					"122.72.99.4", "122.72.99.8" };
		    
		      if(proxy_arr==null)
		      {
		    	  proxy_arr=proxy_hosts;
		      }
			  int rani = -1;
			  rani = (int) (ran * (proxy_arr.length));
			  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
			  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
			}
			
			
			httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
			httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
			httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
			httpclient.getParams().setParameter("http.socket.timeout",60000);

			httpclient.getParams().setParameter("http.connection.timeout",60000);

			httpclient.getParams().setParameter("http.connection-manager.timeout",60000);
		  
		  
		  HttpContext httpContext = new BasicHttpContext();
		  HttpGet httpGet = new HttpGet(url);
		  try {
		   //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中
		   HttpResponse response = httpclient.execute(httpGet, httpContext);
		   
		   //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"
		   //HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		   
		   //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
		  // HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
		   
		  // System.out.println("主机地址:" + targetHost);
		  // System.out.println("URI信息:" + realRequest.getURI());
		   HttpEntity entity = response.getEntity();
		   if(null != entity){
			red_info=EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset());
		    //System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));
		    
		    EntityUtils.consume(entity);
		   }
		  } catch (Exception e) {
		   e.printStackTrace();
		  }finally{
		   httpclient.getConnectionManager().shutdown();
		  }
		  
		  return red_info;
	}
	 
	 
	    /**
         * 获取重定向后的url
         * url:待抓取的url地址
         * useProxy：是否使用代理
         * proxy_arr：代理ip列表，若useProxy为false,则proxy_arr=null
	     * @param url
	     * @param useProxy
	     * @param proxy_arr
	     * @return
	     */
	   public static  String getRedirectUrl(String url,boolean useProxy,String[] proxy_arr) {

	        if(!(SSO.tnoe(url)))
	        {
	        	return "";
	        }
	        
			if ((isValidUrl(url)) == false) {
				return "";
			}
			
			url=format_url(url);

			DefaultHttpClient httpclient = null;
			String red_url = "";
			
			try {
				httpclient = new DefaultHttpClient();
				
				if(useProxy==true)
				{
				  double ran = Math.random();
			      String[] proxy_hosts = { 
						"122.72.111.98", "122.72.76.132",
						 "122.72.11.129", "122.72.11.130",
						"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
						"122.72.99.4", "122.72.99.8" };
			    
			      if(proxy_arr==null)
			      {
			    	  proxy_arr=proxy_hosts;
			      }
				  int rani = -1;
				  rani = (int) (ran * (proxy_arr.length));
				  HttpHost proxy = new HttpHost(proxy_arr[rani], 80, "http");
				  httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
						proxy);
				}
				
				
				 httpclient.getParams().setParameter(HttpMethodParams.USER_AGENT,"Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1"); 	
			     httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  60000);//连接时间20s
				 httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  60000);
				 httpclient.getParams().setParameter("http.socket.timeout",60000);

				 httpclient.getParams().setParameter("http.connection.timeout",60000);

				 httpclient.getParams().setParameter("http.connection-manager.timeout",60000);
				
				// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
				 httpclient.setRedirectStrategy(new RedirectStrategy() { // 设置重定向处理方式

							@Override
							public boolean isRedirected(HttpRequest arg0,
									HttpResponse arg1, HttpContext arg2)
									throws ProtocolException {

								return false;
							}

							@Override
							public HttpUriRequest getRedirect(HttpRequest arg0,
									HttpResponse arg1, HttpContext arg2)
									throws ProtocolException {

								return null;
							}
						});

				// 创建httpget.
				HttpGet httpget = new HttpGet(url);
				// 执行get请求.
				HttpResponse response = httpclient.execute(httpget);

				int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode == HttpStatus.SC_OK) {
					// 获取响应实体
					// HttpEntity entity = response.getEntity();
					// if (entity != null) {
					// 打印响应内容长度
					// System.out.println("Response content length: "
					// + entity.getContentLength());
					// 打印响应内容
					// System.out.println("Response content: "
					// + EntityUtils.toString(entity));
					// }
				} else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY
						|| statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {

					// System.out.println("当前页面发生重定向了---");

					Header[] headers = response.getHeaders("Location");
					if (headers != null && headers.length > 0) {
						String redirectUrl = headers[0].getValue();
						red_url = redirectUrl;
						System.out.println("重定向的URL:" + redirectUrl);
						/*
						 * redirectUrl = redirectUrl.replace(" ", "%20");
						 * get(redirectUrl);
						 */
					}
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// 关闭连接,释放资源
				httpclient.getConnectionManager().shutdown();
			}

			return red_url;
		}
	 
	/**
	 * 正规化链接地址
	 * @param host
	 * @return
	 */
	public static String format_url(String url)
	{
		String fu="";
		if(!(SSO.tnoe(url)))
		{
			return "";
		}
		url=url.trim();
		if((url.indexOf("http://"))>-1)
		{
			return url;
		}
		
		fu="http://"+url;
		
		//System.out.println("fu:"+fu);
		fu=fu.trim();			
		return fu;
	}
	
	/**
	 * 测试是否是合法url
	 * @param url
	 * @return
	 */
	public static boolean isValidUrl(String url) {
		boolean isVal = true;
		if ((url.indexOf("'") != -1) || (url.indexOf("}") != -1)) {
			return false;
		}

		return isVal;
	}
	
	public static void main(String[] args)
	{
		/**getSource***/
		/*
		String url="http://youxi.baidu.com/ajax_search.xhtml?c=index&q=ddt";
		System.out.println(SingleUrlCrawl.getSource(url, true, null));
		System.out.println(SingleUrlCrawl.getSource(url, false, null));
	    */
		
		/**getWebPageTitle***/
		
		String url="http://jiaoyu.baidu.com/mp/index";
		System.out.println(SingleUrlCrawl.getWebPageTitle(url, true, null));
		System.out.println(SingleUrlCrawl.getWebPageTitle(url, false, null));
	    
		
		
		/**getWebPageMeta***/
		/*
		String url="http://www.jyubuy.com/tts/?cid=101/";
		System.out.println(SingleUrlCrawl.getWebPageMeta(url, true, null));
		System.out.println(SingleUrlCrawl.getWebPageMeta(url, false, null));
		*/
		
		/**getRedirectInfo***/
		/*
		String url="www.sina.com.cn";
		System.out.println(SingleUrlCrawl.getRedirectInfo(url, true, null));
		System.out.println(SingleUrlCrawl.getRedirectInfo(url, false, null));
		*/
		
		/**getRedirectUrl***/
		/*
		String url="http://www.baidu.com/link?url=8bUKa34vavcVf4-3sQfW0hB5t1bFz0nzVOg5d4qQ0Q3fq4ot-L5_qUbwY1kaUAWk";
		System.out.println(SingleUrlCrawl.getRedirectUrl(url, true, null));
		System.out.println(SingleUrlCrawl.getRedirectUrl(url, false, null));
		*/
		
		/***getSourceText***/
		/*
		String url="http://hjmg.tshehe.com/";
		System.out.println(SingleUrlCrawl.getSourceText(url, true, null));
		System.out.println(SingleUrlCrawl.getSourceText(url, false, null));
		*/
		
		/**getWebPageHeadInfo**/
	//	String url="http://www.google.com";
	//	System.out.println(SingleUrlCrawl.getWebPageHeadInfo(url));
		
		/*
		String url="www.sf123.com";
		System.out.println(SingleUrlCrawl.getWebPageTitle(url));
		*/
	}
	
	public static String getFilterContent(String raw_content) {
		String filter_content = "";

		filter_content = raw_content
				.replaceAll("http://www.baidu.com(?s).*<!DOCTYPE\\s*html>",
						"")
				.replaceAll("<html>(?s).*?用手机随时随地上百度</a>", "")
				.replaceAll(
						"<input type=\"submit\"\\s*value=\"百度一下\"(?s).*?</html>",
						"")
				.replaceAll("<\\s*script\\s*>(?s).*?<\\s*/script\\s*>", "")
				.replaceAll("<\\s*style\\s*>(?s).*?<\\s*/style\\s*>", "")
				.replaceAll("<(?s)[^<>]*?>", "")
				.replaceAll("\\d+-\\d+-\\d+", "")
				.replaceAll("-\\s*百度快照", "")
				.replaceAll("查看更多.{0,100}?内容", "")
				.replaceAll("下一页.{0,10}百度为您找到相关结果约[\\d\\,]*个.{0,100}?相关搜索",
						"")
				.replaceAll("查看", "")
				.replaceAll("更多", "")
				.replaceAll("内容", "")
				.replaceAll("百度", "")
				.replaceAll("猜您>喜欢", "")
				.replaceAll("[\\.a-z\\/0-9]*\\.\\.\\.htm[l]?", "")
				.replaceAll("[\\.A-Za-z\\/0-9\\=]*?[a-z0-9]", " ")
				.replaceAll("\\.\\.\\.", " ")
				.replaceAll("&nbsp;", "")
				.replaceAll("&gt;", "")
				.replaceAll("显示全部", "")
				.replaceAll("收起", "")
				.replaceAll("[&;-]", "")
				.replaceAll("[\\(\\)\\+\\|\\{\\}\\=\\*\\/<>]", "")
				.replaceAll(
						"\\s[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]{1,10}\\s",
						"").replaceAll("_::\\s", "");
		filter_content = filter_content.replaceFirst(
				"[\"\\?\\,\\:\\_\\a-zA-Z0-9\\[\\]\\\\\\#\\%\\$]*", "");

		return filter_content;
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
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  6000);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  6000);
		httpclient.getParams().setParameter("http.socket.timeout",6000);

		httpclient.getParams().setParameter("http.connection.timeout",6000);

		httpclient.getParams().setParameter("http.connection-manager.timeout",6000);
	
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
				byte[] bytes = new byte[10240];
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
	
	public static int getWebPageHeadInfo(String url)
	{
		String title = "";
		String keywords="";
		String description="";
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
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  600);//连接时间20s
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,  600);
		httpclient.getParams().setParameter("http.socket.timeout",600);

		httpclient.getParams().setParameter("http.connection.timeout",600);

		httpclient.getParams().setParameter("http.connection-manager.timeout",600);
	
	   System.out.println("crawling the url :"+url);

		url = url.trim();
		if ((url == null) || (url.length() < 5)) {
			return -1;
		}
		if (url.indexOf("http") < 0) {
			url = "http://" + url;
		}
		int statusCode=-1;
		String con = "";
		try {
			System.out.println("httpget :"+url);
			HttpGet httpget = new HttpGet(url);

			//System.out.println("executing request ==================" + httpget.getURI());
			// 执行get请求.
			System.out.println("exec the httpget :"+url);
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
		    statusCode = response.getStatusLine().getStatusCode();
			System.out.println("statusCode:"+statusCode);   
	      }
	      catch(Exception e)
	      {
	    	  
	      }
		
		return statusCode;
			
	}
	
}
