package cn.clickwise.liqi.spark.job;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Base64;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import cn.clickwise.liqi.sort.utils.SortStrArray;
import cn.clickwise.liqi.str.basic.SSO;

import redis.clients.jedis.Tuple;
import scala.Tuple2;
import scala.Tuple3;
import org.slf4j.spi.LocationAwareLogger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * 输入： nstat数据
 * 格式：<area><atime><pname><sip><dip><host><url><refer><cookie><loc><agent>
 * area:地区编号,例如t6401 atime:访问时间，例如2014-02-23 22:12:00 pame: 数据来源，例如DEFAULT_NSTAT
 * sip: 源ip地址，例如122.233.87.251 dip: 目的ip地址，例如220.181.111.191 host:
 * 访问host地址，例如www.youku.com url: 访问url地址，例如/ refer:访问url的refer
 * http://www.hao123.com/ cookie: 用户标识，例如uid=dd0c2c118d70d347905590ec204590de
 * loc: 用户所在地区，例如浙江省宁波市 agent: 客户端，例如Mozilla/5.0 (Windows NT 6.1; rv:27.0)
 * Gecko/20100101 Firefox/27.0
 * 
 * 输出： adate_str+"\001"+atime_str+"\001"+sip+"\001"+dip+"\001"+host+"\001"+
 * host_attr+"\001"+cookie+"\001"+loc+"\001"+agent
 * 
 * @author zkyz
 * 
 */
public class FinanceSpark {

	public static Tuple3<String, String, String> extractKey(String line) {
		String[] seg_arr = null;
		String area = "";
		String atime = "";
		String pname = "";
		String sip = "";
		String dip = "";
		String host = "";
		String url = "";
		String refer = "";
		String cookie = "";
		String loc = "";
		String agent = "";
		seg_arr = line.split("\001");
		if (seg_arr.length == 11) {
			area = seg_arr[0].trim();
			atime = seg_arr[1].trim();
			pname = seg_arr[2].trim();
			sip = seg_arr[3].trim();
			dip = seg_arr[4].trim();
			host = seg_arr[5].trim();
			url = seg_arr[6].trim();
			refer = seg_arr[7].trim();
			cookie = seg_arr[8].trim();
			loc = seg_arr[9].trim();
			agent = seg_arr[10].trim();
			if (SSO.tioe(url)) {
				url = "unk";
			}
			if (SSO.tioe(refer)) {
				refer = "unk";
			}
			if (SSO.tnoe(host) && (host.indexOf("www.baidu.com") != -1)) {
				return new Tuple3<String, String, String>(host, url, refer);
			}
		}
		return new Tuple3<String, String, String>(null, null, null);
	}

	public static boolean extractStats(String line) {
		boolean iscont = false;

		String[] seg_arr = null;
		String area = "";
		String atime = "";
		String pname = "";
		String sip = "";
		String dip = "";
		String host = "";
		String url = "";
		String refer = "";
		String cookie = "";
		String loc = "";
		String agent = "";
		seg_arr = line.split("\001");
		if (seg_arr.length == 11) {
			area = seg_arr[0].trim();
			atime = seg_arr[1].trim();
			pname = seg_arr[2].trim();
			sip = seg_arr[3].trim();
			dip = seg_arr[4].trim();
			host = seg_arr[5].trim();
			url = seg_arr[6].trim();
			refer = seg_arr[7].trim();
			cookie = seg_arr[8].trim();
			loc = seg_arr[9].trim();
			agent = seg_arr[10].trim();
			if (SSO.tioe(url)) {
				url = "unk";
			}
			if (SSO.tioe(refer)) {
				refer = "unk";
			}
			if (SSO.tnoe(host) && (host.indexOf("sifu") != -1)) {
				iscont = true;
			}
		}

		return iscont;
	}
	public static boolean extractTStats(Tuple2<String, String> t) {
		
		boolean iscont = false;
		/*
		if(t==null)
		{
			return false;
		}
       if(t._2.indexOf(keyword)>-1)
       {
    	   iscont = true;
       }
       */
		return iscont;
	}
	
	public static String[] extract_host_title(String line) {
		String[] seg_arr = null;
		String host = "";
		String title = "";
		seg_arr = line.split("\t");
		String[] res_arr = null;
		if (seg_arr.length == 11) {
			host = seg_arr[4].trim();
			title = seg_arr[6].trim();

			if (SSO.tnoe(host) && SSO.tnoe(title)) {
				res_arr = new String[2];
				res_arr[0] = host;
				res_arr[1] = title;
			}
		}
		return res_arr;
	}

	/**
	 * 启动HttpServerThread的线程
	 * 
	 * @author zkyz
	 * 
	 */
 static public class HttpServerThread implements Runnable {

		// public Hashtable<String,String> wci_hash=null;

		private int port = 0;

		@Override
		public void run() {
			try {
				try {
					port=9066;
					HttpServer hs = HttpServer.create(new InetSocketAddress(
							port), 0);
					SWAHandlerRed mh = new SWAHandlerRed();
					hs.createContext("/host_find", mh);
					hs.setExecutor(null);
					hs.start();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Wait here until notified of shutdown.
				synchronized (this) {
					try {
						this.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		public void setPort(int port) {
			this.port = port;
		}

	}

	/* Responds to the /test URI. */
	static class SWAHandlerRed implements HttpHandler {

		public void handle(HttpExchange exchange) throws IOException {

			String title_str = exchange.getRequestURI().toString();
			// System.out.println("title_str:" + title_str);

			title_str = title_str.replaceFirst("/host_find/do\\?t=", "");
			// System.out.println("title_str:" + title_str);
			String de_title = new String(Base64.decode(title_str));// 输入title
			System.out.println("de_title:" + de_title);
			String res = "";// 返回结果

			try {

			} catch (Exception e) {
				System.out.println(e.getStackTrace());
			}

			keyword=de_title;
		    getHostInfo();
		    
			String[] rl_arr=null;
			rl_arr=SortStrArray.sort_List(rl, 0, "str", 3, "\001");
			for(int i=0;i<rl_arr.length;i++)
			{
				System.out.println(rl_arr[i]);
			}

			
			System.out.println("waiting to find");
			OutputStream os = exchange.getResponseBody();
			String response = "";
			String encode_res ="";
			for(int i=0;i<rl_arr.length;i++)
			{
				res=rl_arr[i];
		     encode_res = Base64.encodeBytes(res.getBytes());
			 encode_res = encode_res.replaceAll("\\s+", "");

			response = encode_res;
			exchange.sendResponseHeaders(200, response.length());
		
			os.write(response.getBytes());
			}
			os.close();
		}

	}
	public static class DefFunction extends Function<Tuple2<String, String>, Boolean>{
        public String keyword="";
		public DefFunction(String keyword)
        {
        	this.keyword=keyword;
        }
		public DefFunction() {
			// TODO Auto-generated constructor stub
		}
		@Override
		public Boolean call(Tuple2<String, String> t) throws Exception {
			if(t==null)
			{
				return false;
			}
	
		       if((t._2.indexOf(keyword))>(-0.5))
		       {
		    	  return true;
		       }
			return false;
		}
		
	}
	public static void getHostInfo(){
	
		rl=new ArrayList<String>();
		JavaRDD<Tuple2<String, String>> extracted = host_title
				.filter(new DefFunction(keyword));
		
		List<Tuple2<String, String>> output = extracted.collect();
		for (Tuple2<String, String> t : output) {
			//System.out.println(t._1+"\001"+t._2);
			rl.add(t._1+"\001"+t._2);
		}
	
	}

	public static JavaRDD<Tuple2<String, String>> host_title;
	public static JavaRDD<String> keyword_rdd=null;
	private static HttpServerThread serverInstance;
	private static ArrayList<String> rl;
	private static JavaSparkContext jsc;
	public static  String keyword=null;
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.err.println("Usage: JavaLogQuery <master> [logFile]");
			System.exit(1);
		}
		// System.setProperty("spark.serializer", "spark.KryoSerializer"); //
		// kryo is much faster
		// System.setProperty("spark.kryoserializer.buffer.mb", "256"); // I
		// serialize bigger objects
		// System.setProperty("spark.mesos.coarse", "true"); // link provided
		// System.setProperty("spark.akka.frameSize", "500"); // workers should
		// be able to send bigger messages
		// System.setProperty("spark.akka.askTimeout", "30"); // high CPU/IO
		// load

		jsc = new JavaSparkContext(args[0], "JavaLogQuery",
				System.getenv("SPARK_HOME"), "/home/hadoop/spark/swa/swa.jar");

		JavaRDD<String> dataSet = jsc.textFile(args[1]);// 这里可以是hdfs文件夹
		// dataSet.cache();
		host_title = dataSet
				.map(new Function<String, Tuple2<String, String>>() {
					public Tuple2<String, String> call(String s)
							throws Exception {
						String[] res_arr = extract_host_title(s);
						if (res_arr != null) {
							return new Tuple2<String, String>(res_arr[0], res_arr[1]);
						} else {
							return null;
						}
					}
				});

		host_title.cache();
		List kl=new ArrayList<String>();
		keyword="私服";

		getHostInfo();
		String[] rl_arr=null;
		rl_arr=SortStrArray.sort_List(rl, 0, "str", 3, "\001");
		for(int i=0;i<rl_arr.length;i++)
		{
			System.out.println(rl_arr[i]);
		}
		
		System.out.println("waiting to find");
		serverInstance=new HttpServerThread();
		try
		{
			Thread serverThread = new Thread(serverInstance);
			serverThread.start();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
        

		//System.exit(0);
	}

}
