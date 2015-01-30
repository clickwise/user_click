package cn.clickwise.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
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

public class Fetcher {
	/**
	 * 读取网页源代码
	 * 
	 * @param url
	 * @return source
	 */
	public String getSource(String url) {
		String source = "";
		HttpClient httpclient = new DefaultHttpClient();
	

		/*
		 * double ran = Math.random(); String[] proxy_hosts = { "122.72.111.98",
		 * "122.72.76.132", "122.72.11.129", "122.72.11.130", "122.72.11.131",
		 * "122.72.11.132", "122.72.99.2", "122.72.99.3", "122.72.99.4",
		 * "122.72.99.8" }; int rani = -1; rani = (int) (ran * 10); HttpHost
		 * proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		 * httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		 * proxy); httpclient.getParams().setParameter(CoreConnectionPNames.
		 * CONNECTION_TIMEOUT,1000);
		 */

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

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			Pattern charset_pat = Pattern
					.compile("(?:(?:charset)|(?:CHARSET))=([^\">]*)");
			String charset = "";
			Header type_head = null;
			type_head = response.getFirstHeader("Content-Type");

			String thlv = type_head.getValue().toString().toLowerCase();
			Matcher charset_mat = charset_pat.matcher(thlv);
			// System.out.println("Content-Type:"+thlv);

			if (thlv.indexOf("text/html") < 0) {
				System.err.println("[" + url + "] 不是普通网页");
				return "";
			}

			if (charset_mat.find()) {
				charset = charset_mat.group(1);
			}
			charset = charset.trim();
			// System.out.println("charset1:"+charset);

			charset = charset.trim();

			byte[] bytes = new byte[1024];
			InputStream is = entity.getContent();
			is.read(bytes);
			String us = new String(bytes);
			String ds = "";
			if (charset.equals("")) {
				charset_pat = Pattern
						.compile("<(?:(?:meta)|(?:META)|(?:Meta))[^<>]*?(?:(?:charset)|(?:CHARSET))=([^\">]*)[^<>]*?>");
				charset_mat = charset_pat.matcher(us.toLowerCase());
				// System.out.println("us:"+us);
				if (charset_mat.find()) {
					charset = charset_mat.group(1);
				}
				// System.out.println("charset2:"+charset);
				ds = new String(bytes, charset);
			}

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				// 获取响应实体
				entity = response.getEntity();
				if (entity != null) {

					con = EntityUtils.toString(entity, charset);
					// con = EntityUtils.toString(entity);
					source = ds + con;
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return source;
	}

	public String getUrl(String word) {
		String url = "";
		String prefixSE = "http://www.so.com/s?&q=";
		url = prefixSE + URLEncoder.encode(word);

		return url;
	}

	public String getSourceEasy(String url) {
		String source = "";
		HttpClient httpclient = new DefaultHttpClient();

		double ran = Math.random();
		String[] proxy_hosts = { "122.72.111.98",
				"122.72.11.129", "122.72.11.130", "122.72.11.131",
				"122.72.11.132", "122.72.99.2", "122.72.99.3", "122.72.99.4",
				"122.72.99.8" };
		int rani = -1;
		rani = (int) (ran * 9);
		System.err.println("rani:"+rani);
		
		HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
		
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);

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

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream inSm = entity.getContent();
			InputStreamReader isr = new InputStreamReader(inSm);
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			con = "";
			while ((line = br.readLine()) != null) {
				con += (line + "\n");
			}
			source = con;
			// source=entity.getContent();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return source;
	}
	
	public String getSourceEasyProxy(String url,int proxy_id) {
		String source = "";
		HttpClient httpclient = new DefaultHttpClient();

		double ran = Math.random();
		String[] proxy_hosts = { "122.72.111.98",
				"122.72.11.129", "122.72.11.130", "122.72.11.131",
				"122.72.11.132", "122.72.99.2", "122.72.99.3", "122.72.99.4",
				"122.72.99.8" };
		int rani = -1;
		rani = (int) (ran * 9);
		rani=proxy_id;
		HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);

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

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream inSm = entity.getContent();
			InputStreamReader isr = new InputStreamReader(inSm);
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			con = "";
			while ((line = br.readLine()) != null) {
				con += (line + "\n");
			}
			source = con;
			// source=entity.getContent();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return source;
	}
	
	public String getSourceUserProxy(String url) {
		String source = "";
		HttpClient httpclient = new DefaultHttpClient();

		double ran = Math.random();
		String[] proxy_hosts = { "122.72.111.98",
				"122.72.11.129", "122.72.11.130", "122.72.11.131",
				"122.72.11.132", "122.72.99.2", "122.72.99.3", "122.72.99.4",
				"122.72.99.8" };
		int rani = -1;
		rani = (int) (ran * 9);

		HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
				proxy);
		httpclient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);

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

			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			InputStream inSm = entity.getContent();
			InputStreamReader isr = new InputStreamReader(inSm);
			BufferedReader br = new BufferedReader(isr);
			String line = "";
			con = "";
			while ((line = br.readLine()) != null) {
				con += (line + "\n");
			}
			source = con;
			// source=entity.getContent();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return source;
	}
	
	
	/**
	 * 获取标题
	 * @param url
	 * @return
	 */
	public String getTitle(String url)
	{
		Document doc=null;
		try {

			//////String content=fetcher.getSourceEasyProxy(url,getProxy());
			//////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			
			if(doc==null)
			{
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc.title();
	}
	
	/**
	 * 获取title、meta、keywords
	 * @param url
	 * @return
	 */
	public String getKeywords(String url)
	{
		Document doc=null;
		try {

			//////String content=fetcher.getSourceEasyProxy(url,getProxy());
			//////doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).get();
			if(doc==null)
			{
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return doc.title();
	}
	
	public static String getSource(String url,boolean useProxy)
	{
		String source="";
		HttpClient httpclient = new DefaultHttpClient();

		
		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_arr= { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    

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
			
			/*
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
			
			//source=entity.getContent();
			*/
			
			
			String content="";
			BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
			String line="";
			while((line=br.readLine())!=null)
			{
				content=content+line;
			}
			source=content;
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}			
		return source;
	}
	
	public static InputStream getSourceInputStream(String url,boolean useProxy)
	{
		InputStream sourceis=null;
		HttpClient httpclient = new DefaultHttpClient();

		
		if(useProxy==true)
		{
		  double ran = Math.random();
	      String[] proxy_arr= { 
				"122.72.111.98", "122.72.76.132",
				 "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
				"122.72.99.4", "122.72.99.8" };
	    
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
			
			/*
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
			
			//source=entity.getContent();
			*/
			
			/*
			String content="";
			BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
			String line="";
			while((line=br.readLine())!=null)
			{
				content=content+line;
			}
			source=content;
			*/
			sourceis=entity.getContent();
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}			
		return sourceis;
	}
  
	
	public WebAbstract getAbstract(String url)
	{
		Document doc=null;
		WebAbstract wa=new WebAbstract();
		try {

			//String content=getSource(url,false);
			//doc=Jsoup.parse(content);
			doc = Jsoup.connect(url).timeout(20000).get();
			if(doc==null)
			{
				return null;
			}
			
			wa.setTitle(doc.title());
			wa.setKeywords(doc.head().select("meta[name=keywords]").attr("content"));
			wa.setDescription(doc.head().select("meta[name=description]").attr("content"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return wa;
	}
	
	public static void main(String[] args) {
		String url = "";
		String word = "电视剧";
		Fetcher fetcher = new Fetcher();

		System.out.println(fetcher.getSourceEasy(fetcher.getUrl(word)));
	}
}
