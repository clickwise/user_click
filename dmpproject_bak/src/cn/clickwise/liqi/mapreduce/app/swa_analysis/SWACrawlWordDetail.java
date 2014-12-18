package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import redis.clients.jedis.Jedis;

/**
 * 抓取百度搜索首页，获取单词top_urls,根据top_urls对该单词进行类别划分
 * 
 * @author lq
 * 
 */
public class SWACrawlWordDetail {
	public Jedis host_jedis;
	public String redis_host_ip = "";
	public int redis_port = 6379;
	public int redis_host_db = 0;
	public Hashtable<Integer, String> ban_proxys = null;
    public String[] proxy_hosts = { 
			"122.72.111.98", "122.72.76.132",
			 "122.72.11.129", "122.72.11.130",
			"122.72.11.131", "122.72.11.132", "122.72.99.2", "122.72.99.3",
			"122.72.99.4", "122.72.99.8" };
	public Vector getTrueUrls(Vector urls_top_vec) {
		Vector nv = new Vector();
		String codeurl = "";
		String turl = "";
		String[] seg_arr = null;
		String ourl = "";
		for (int i = 0; i < urls_top_vec.size(); i++) {
			codeurl = urls_top_vec.get(i) + "";
			codeurl = codeurl.trim();
			// seg_arr = codeurl.split("\t");
			/*
			 * if (seg_arr.length < 2) { continue; }
			 */
			if (codeurl.equals("")) {
				continue;
			}
			ourl = codeurl;
			turl = getRedirectTry(ourl);
			if ((turl != null)&&(turl.indexOf("baidu")==-1)&&(turl.indexOf("bbs")==-1)) {
				nv.add(turl);
			}
		}
		return nv;
	}

	private String getRedirect(String code_url) {


		String red_url = "";
		DefaultHttpClient httpclient = null;
		code_url = code_url.trim();
		String url = "";
		if (code_url.indexOf("http://") == -1) {
			url = "http://" + code_url;
		} else {
			url = code_url;
		}
		try {
			httpclient = new DefaultHttpClient();
			double ran = Math.random();
			System.out.println("ran:" + ran);
			int rani = -1;
			rani = (int) (ran * 10);
			//HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
			//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			//		proxy);
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
			if ((isValidUrl(url)) == false) {
				return null;
			}
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println("statusCode:" + statusCode);
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
					//System.out.println("重定向的URL:" + redirectUrl);
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

	public String getRedirectClient(DefaultHttpClient httpclient, String url) throws Exception{
		String red_url = "";
          try{
			if ((isValidUrl(url)) == false) {
				return null;
			}
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println("statusCode:" + statusCode);
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
					//System.out.println("重定向的URL:" + redirectUrl);
					/*
					 * redirectUrl = redirectUrl.replace(" ", "%20");
					 * get(redirectUrl);
					 */
				}
			}
          }
          catch(Exception e)
          {
        	  System.out.println(e.getMessage());
          }
		return red_url;
	}

	private String getRedirectTry(String code_url) {

		String red_url = "";
		DefaultHttpClient httpclient = null;
		code_url = code_url.trim();
		String url = "";
		if (code_url.indexOf("http://") == -1) {
			url = "http://" + code_url;
		} else {
			url = code_url;
		}
		try {
			httpclient = new DefaultHttpClient();
			
			boolean isSeled=false;
			double ran=0.0;
			 int rani =0;
			while(isSeled==false)
			{
			   ran = Math.random();
			  // System.out.println("ran:" + ran);		
			   rani = (int) (ran * 10);
			  // System.out.println("rani:" + rani);	
			   if(!(ban_proxys.containsKey(rani)))
			   {
				   isSeled=true;
			   }
			}
			//System.out.println("rani:" + rani);	
			//HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
			//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			//		proxy);
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
			red_url=getRedirectClient(httpclient,url);
			//System.out.println("red_url1:"+red_url);
			if(red_url.equals(""))
			{
				ban_proxys.put(rani,"Time Out");
				httpclient = new DefaultHttpClient();
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
				
				red_url=getRedirectClient(httpclient,url);			
			}			
		//	System.out.println("red_url2:"+red_url);

		}catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			// 关闭连接,释放资源
			httpclient.getConnectionManager().shutdown();
		}

		return red_url;
	}

	public String getContentClient(DefaultHttpClient httpclient) {
		String con = "";
		httpclient = new DefaultHttpClient();
		return con;
	}

	public boolean isValidUrl(String url) {
		boolean isVal = true;
		if ((url.indexOf("'") != -1) || (url.indexOf("}") != -1)) {
			return false;
		}

		return isVal;
	}

	private String getContent(String url) throws Exception {
		String con = "";
		//FileWriter fw = new FileWriter(new File("test_html.txt"));
		//PrintWriter pw = new PrintWriter(fw);
		HttpClient httpclient = new DefaultHttpClient();
		double ran = Math.random();
		// System.out.println("ran:"+ran);
		int rani = -1;
		rani = (int) (ran * 10);
		//HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		//httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		//		proxy);
		// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
		//System.out.println("rani:" + rani);	
		try {
			// 创建httpget.

			HttpGet httpget = new HttpGet(url);
			//System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
			//System.out.println("statusCode:" + statusCode);
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

		//fw.close();
		//pw.close();

		return con;
	}

	public String getCodeUrl(String keyword) {
		String code_url = "";
		String code_word = "";
		code_word = URLEncoder.encode(keyword);
		code_word = code_word.trim();

		String url_prefix = "http://www.baidu.com/s?wd=";
		code_url = url_prefix + code_word;

		return code_url;
	}

	public Vector extract_links(String content) {
		Vector url_vec = new Vector();

		String url_regex = "href=\"(http:\\/\\/www\\.baidu\\.com\\/link\\?url=.*?)\"";
		Pattern url_pat = Pattern.compile(url_regex);
		Matcher url_mat = url_pat.matcher(content);
		String temp_url = "";

		while (url_mat.find()) {
			temp_url = url_mat.group(1);
			if (temp_url != null) {
				temp_url = temp_url.trim();
				if (!(temp_url.equals(""))) {
					url_vec.add(temp_url);
				}
			}
		}
		return url_vec;
	}

	public void load_local_config() throws Exception {
		
		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名
		
       // address="adt1";
		if (address.equals("adt1")) {
			redis_host_ip = "192.168.110.182";
		}
		else if (address.equals("hndx_fx_100")) {
			redis_host_ip = "192.168.1.100";
		}

		redis_port = 6379;
		redis_host_db = 5;
		
		
		
		host_jedis = new Jedis(redis_host_ip, redis_port, 100000);// redis服务器地址
		host_jedis.ping();
		host_jedis.select(redis_host_db);
		ban_proxys = new Hashtable<Integer,String>();	
	}
	
	public void load_local_config(Properties prop)
	{
		redis_host_ip = prop.getProperty("redis_host_ip");
		redis_port = Integer.parseInt(prop.getProperty("redis_port"));
		redis_host_db = Integer.parseInt(prop.getProperty("redis_host_db"));
		host_jedis = new Jedis(redis_host_ip, redis_port, 100000);// redis服务器地址
		host_jedis.ping();
		host_jedis.select(redis_host_db);
		ban_proxys = new Hashtable<Integer,String>();	
	}

	public String predict_preliminar(Vector trueurls_top_vec) {
		String preli_cate = "NA";
		URI sturi = null;
		String sturl = "";
		String sthost = "";
		Hashtable tag_res = new Hashtable();
		String cate_res = "";
		int old_res_c = 0;
		for (int i = 0; i < trueurls_top_vec.size(); i++) {
			sturl = trueurls_top_vec.get(i) + "";
			sturl = sturl.trim();
			try {
				sturi = new URI(sturl);
				sthost = sturi.getHost();
			} catch (Exception e) {
			}

			if (sthost == null) {
				continue;
			}

			if (sthost.equals("")) {
				continue;
			}
			sthost = sthost.trim();
			try {
				cate_res = host_jedis.get(sthost);
			} catch (Exception ec) {
				try {
					double ran = Math.random();
					// System.out.println("ran:" + ran);
					int rani = -1;
					rani = (int) (ran * 10000);
					Thread.sleep(rani);
				} catch (Exception ei) {

				}
			}
			if (cate_res == null) {
				continue;
			}
			cate_res = cate_res.trim();
			if (!tag_res.containsKey(cate_res)) {
				tag_res.put(cate_res, 1);
			} else {
				old_res_c = Integer.parseInt(tag_res.get(cate_res) + "");
				old_res_c++;
				tag_res.remove(cate_res);
				tag_res.put(cate_res, old_res_c);
			}
		}

		Enumeration tag_keys = tag_res.keys();
		String maxTag = "NA";
		int maxTagCount = 0;
		String temp_key = "";
		int temp_count = 0;
		while (tag_keys.hasMoreElements()) {
			temp_key = tag_keys.nextElement() + "";
			// System.out.println("temp_key:"+temp_key);
			temp_count = Integer.parseInt(tag_res.get(temp_key) + "");
			if (temp_count > maxTagCount) {
				maxTagCount = temp_count;
				maxTag = temp_key;
			}
		}

		if (maxTag.equals("-1")) {
			preli_cate = "NA";
		} else {
			preli_cate = maxTag;
		}
		return preli_cate;
	}

	public String getSWCate(String sw) 
	{
		
		String cate="";
		try{
		String url = getCodeUrl(sw);
		//System.out.println("url:" + url);
		String con = getContent(url);
		// System.out.println("con:"+con);
		Vector uvec = extract_links(con);
		String turl = "";
		for (int i = 0; i < uvec.size(); i++) {
			turl = uvec.get(i) + "";
			//System.out.println(i + ":" + turl);
		}

		Vector tvec = getTrueUrls(uvec);
		for (int i = 0; i < tvec.size(); i++) {
			turl = tvec.get(i) + "";
			//System.out.println(i + ":" + turl);
		}

		String prem_cate = predict_preliminar(tvec);
		cate=prem_cate;
		}
		catch(Exception e)
		{
			
		}
		return cate;
	}
	
	public static void main(String[] args) throws Exception {
		SWACrawlWordDetail swacwd = new SWACrawlWordDetail();
		swacwd.load_local_config();
		String keyword = "威努与奴朗普";
		String cate=swacwd.getSWCate(keyword);
		System.out.println("cate:"+cate);
		/*
		String url = swacwd.getCodeUrl(keyword);
		System.out.println("url:" + url);
		String con = swacwd.getContent(url);
		// System.out.println("con:"+con);
		Vector uvec = swacwd.extract_links(con);
		String turl = "";
		for (int i = 0; i < uvec.size(); i++) {
			turl = uvec.get(i) + "";
			System.out.println(i + ":" + turl);
		}

		Vector tvec = swacwd.getTrueUrls(uvec);
		for (int i = 0; i < tvec.size(); i++) {
			turl = tvec.get(i) + "";
			System.out.println(i + ":" + turl);
		}

		String prem_cate = swacwd.predict_preliminar(tvec);
		System.out.println("prem_cate:" + prem_cate);
		*/
	}

}
