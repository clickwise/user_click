package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HttpContext;

import redis.clients.jedis.Jedis;

public class TestRedis{


                public Jedis jedis;
                public Jedis cated_redis;

                public String redis_host_ip = "";
                public String redis_cated_words_ip = "";
                public int redis_port = 6379;
                public int redis_host_cate_db = 0;
                public int redis_cated_words_db = 0;

               public void test()
               {

                        jedis = new Jedis(redis_host_ip, redis_port, 100000);// redis服务器地址
                        jedis.ping();
                        jedis.select(redis_host_cate_db);

                        cated_redis = new Jedis(redis_cated_words_ip, redis_port, 100000);
                        cated_redis.ping();
                        cated_redis.select(redis_cated_words_db);
                        System.out.println("redis is connected");


               }

                public void load_config() throws Exception {
                        Properties prop = new Properties();
                        // URL is = this.getClass().getResource("jbkw_config.properties");
                        InputStream model_is = this.getClass().getResourceAsStream(
                                        "/jbkw_config.properties");
                        prop.load(model_is);

                        redis_host_ip = prop.getProperty("redis_host_ip");
                        redis_cated_words_ip = prop.getProperty("redis_cated_words_ip");
                        redis_port = Integer.parseInt(prop.getProperty("redis_port"));
                        redis_host_cate_db = Integer.parseInt(prop
                                        .getProperty("redis_host_cate_db"));
                        redis_cated_words_db = Integer.parseInt(prop
                                        .getProperty("redis_cated_words_db"));

                }

                private String getRedirect(String code_url) {

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
                                System.out.println("ran:" + ran);
                                int rani = -1;
                                rani = (int) (ran * 16);
                                ////HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
                                ////httpclient.getParams().setParameter(
                                ////            ConnRoutePNames.DEFAULT_PROXY, proxy);
                                 httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);
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

                public boolean isValidUrl(String url) {
                        boolean isVal = true;
                        if ((url.indexOf("'") != -1) || (url.indexOf("}") != -1)) {
                                return false;
                        }

                        return isVal;
                }

                public static void main(String[] args) throws Exception
                {
                    TestRedis tr=new TestRedis();
                    tr.load_config();
                    tr.test();
                    String url1="www.baidu.com/link?url=PZogKuGkjG4wbU8sJ1kDweXzvFquzBovGQJLK14UQm_FWpdhqA3LT8TwcjNyFNop";
                    String url2="www.baidu.com/link?url=dzPzDFCie-SRzYu1W7KhYdbCJElz9U1CfPrQFeU04U7";
                    System.out.println(tr.getRedirect(url1));
                    System.out.println(tr.getRedirect(url2));
                        
                }

}
