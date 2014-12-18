package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;  
import java.io.PrintWriter;

import org.apache.http.Header;  
import org.apache.http.HttpEntity;  
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
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.protocol.HttpContext;  
import org.apache.http.util.EntityUtils;  
import org.apache.http.HttpHost;  
import org.apache.http.entity.ContentType;  
import org.apache.http.protocol.BasicHttpContext;  
import org.apache.http.protocol.ExecutionContext;   
  
public class HttpClientURLRedirectTest {  
  
    /** 
     * @param args 
     */  
    public static void main(String[] args) throws Exception{  
        redirect02();  
       // getRedirectInfo();
    }  
  
    /** 
     * Http URL重定向 
     */  
    private static void redirect02() throws Exception {  
        DefaultHttpClient httpclient = null;  
        String url = "http://www.baidu.com/link?url=d3hkqHuknW1hMGRpeKWoZima8iuhCkftqOlS3Rd8d1Yo3I180ilhfx089J8lpLHRMgDu2THTb9EEywu_zxNN6qy6kLaYU6PSvbSCNkM3z1O";  
        try {  
            httpclient = new DefaultHttpClient();  
            httpclient.setRedirectStrategy(new RedirectStrategy() { //设置重定向处理方式  
  
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
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    // 打印响应内容长度  
                 //   System.out.println("Response content length: "  
                    //        + entity.getContentLength());  
                    // 打印响应内容  
                 //   System.out.println("Response content: "  
                      //      + EntityUtils.toString(entity));  
                }  
            } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY  
                    || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {  
                  
                System.out.println("当前页面发生重定向了---");  
                  
                Header[] headers = response.getHeaders("Location");  
                if(headers!=null && headers.length>0){  
                    String redirectUrl = headers[0].getValue();  
                    System.out.println("重定向的URL:"+redirectUrl);  
                    
                    redirectUrl = redirectUrl.replace(" ", "%20");  
                    get(redirectUrl);
                    
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
    }  
  
    /** 
     * 发送 get请求 
     */  
    private static void get(String url) throws Exception {  
  
    	FileWriter fw=new FileWriter(new File("test_html.txt"));
    	PrintWriter pw=new PrintWriter(fw);
        HttpClient httpclient = new DefaultHttpClient();  
        
        try {  
            // 创建httpget.  
            HttpGet httpget = new HttpGet(url);  
            System.out.println("executing request " + httpget.getURI());  
            // 执行get请求.  
            HttpResponse response = httpclient.execute(httpget);  
              
            // 获取响应状态  
            int statusCode = response.getStatusLine().getStatusCode();  
            if(statusCode==HttpStatus.SC_OK){  
                // 获取响应实体  
                HttpEntity entity = response.getEntity();  
                if (entity != null) {  
                    // 打印响应内容长度  
                 //   System.out.println("Response content length: "  
                  //          + entity.getContentLength());  
                    // 打印响应内容  
                  //  System.out.println("Response content: "  
                          //  + EntityUtils.toString(entity));  
                   // pw.println("Response content: "+EntityUtils.toString(entity));
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
        
        fw.close();
        pw.close();
    }  
    
    public static void getRedirectInfo(){  
        HttpClient httpClient = new DefaultHttpClient();  
        HttpContext httpContext = new BasicHttpContext();  
        HttpGet httpGet = new HttpGet("http://www.baidu.com/link?url=226n1n4msY4u-FF5mrlstxCyP7usRVb2xwxNQsyYmN3951s61WJu5-UhjGWIM4Ng1bYwZ19yqL7dPBwqctYO-a");  
        try {  
            //将HttpContext对象作为参数传给execute()方法,则HttpClient会把请求响应交互过程中的状态信息存储在HttpContext中  
            HttpResponse response = httpClient.execute(httpGet, httpContext);  
            //获取重定向之后的主机地址信息,即"http://127.0.0.1:8088"  
            HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);  
            //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"  
            HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);  
            System.out.println("主机地址:" + targetHost);  
            System.out.println("URI信息:" + realRequest.getURI());  
            HttpEntity entity = response.getEntity();  
            if(null != entity){  
               // System.out.println("响应内容:" + EntityUtils.toString(entity, ContentType.getOrDefault(entity).getCharset()));  
            //    EntityUtils.consume(entity);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            httpClient.getConnectionManager().shutdown();  
        }  
    }  
  
}  