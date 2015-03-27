package cn.clickwise.web.bd;

import java.io.IOException;

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

import cn.clickwise.lib.string.SSO;

public class Redirect {
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
	 
	 
	 public static String getRedirect(String code_url) {

			String[] proxy_hosts = { "122.72.56.151", "122.72.56.152",
					"122.72.56.153", "122.72.102.60", "122.72.111.92",
					"122.72.111.98", "122.72.76.131", "122.72.76.132",
					"122.72.76.133", "122.72.11.129", "122.72.11.130",
					"122.72.11.131", "122.72.11.132", "122.72.99.2",
					"122.72.99.3", "122.72.99.4", "122.72.99.8" };
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
				//System.out.println("ran:" + ran);
				int rani = -1;
				rani = (int) (ran * 16);
				// //HttpHost proxy = new HttpHost(proxy_hosts[rani], 80,
				// "http");
				// //httpclient.getParams().setParameter(
				// // ConnRoutePNames.DEFAULT_PROXY, proxy);
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
			String url="http://www.baidu.com/link?url=XHN5NH5UpNMY2cwaiJ27BZWVgKr6dozdbqeSA8p5JjWkEIi7K8Gw0wqKOjqkzcZdNPnzz9jH74C5HwIrVQxEVOeMpUpXUtl4YN27K8h9LAq";
			System.out.println(Redirect.getRedirect(url));
			
			
		}
}
