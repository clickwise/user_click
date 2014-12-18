package cn.clickwise.liqi.mapreduce.app.swa_analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.util.Base64;

import redis.clients.jedis.Jedis;

import cn.clickwise.liqi.str.configutil.ConfigFileReader;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.udpwork.ssdb.Response;
import com.udpwork.ssdb.SSDB;

import com.daguu.lib.httpsqs4j.Httpsqs4j;
import com.daguu.lib.httpsqs4j.HttpsqsClient;
import com.daguu.lib.httpsqs4j.HttpsqsException;
import com.daguu.lib.httpsqs4j.HttpsqsStatus;

public class SWAHttpServerMulRedSqs implements Runnable {
	// public Hashtable<String,String> wci_hash=null;
	public SWADetectWordRed swadw = null;
	public SWACrawlWordDetail swacwd = null;
	public Jedis cw_jedis;
	public String redis_cw_ip = "";
	public int redis_port = 6379;
	public int redis_cw_db = 0;

	public Jedis host_jedis;
	public String redis_host_ip = "";
	public int redis_host_port = 6379;
	public int redis_host_db = 0;
	public Hashtable<String, Integer> stop_words = null;
	public HttpsqsClient sqsclient = null;
	public HttpsqsStatus sqsstatus = null;
	public SSDB ssdb = null;
	public Response resp;

	public String indus_dict_dir = "";
	public String stop_dict_dir = "";

	public String sqs_ip = "";
	public int sqs_port = 0;

	public String ssdb_ip = "";
	public int ssdb_port = 0;

	public void load_local_config(String config_file) throws Exception {
		// URL is = this.getClass().getResource(config_file);

		Properties prop = null;
		prop = ConfigFileReader.getPropertiesFromFile(config_file);

		sqs_ip = prop.getProperty("sqs_ip");
		System.out.println("sqs_ip:" + sqs_ip);
		System.out.println("sqs_port:" + prop.getProperty("sqs_port"));
		sqs_port = Integer.parseInt(prop.getProperty("sqs_port"));
		Httpsqs4j.setConnectionInfo(sqs_ip, sqs_port, "UTF-8");
		sqsclient = Httpsqs4j.createNewClient();
		sqsstatus = sqsclient.getStatus("bd_sw");

		redis_cw_ip = prop.getProperty("redis_cw_ip");
		System.out.println("redis_port:" + prop.getProperty("redis_port"));
		redis_port = Integer.parseInt(prop.getProperty("redis_port"));

		redis_cw_db = Integer.parseInt(prop.getProperty("redis_cw_db"));
		cw_jedis = new Jedis(redis_cw_ip, redis_port, 100000);// redis服务器地址
		cw_jedis.ping();
		cw_jedis.select(redis_cw_db);

		redis_host_ip = prop.getProperty("redis_host_ip");
		System.out.println("redis_host_port:"
				+ prop.getProperty("redis_host_port"));
		redis_host_port = Integer.parseInt(prop.getProperty("redis_host_port"));
		System.out
				.println("redis_host_db:" + prop.getProperty("redis_host_db"));
		redis_host_db = Integer.parseInt(prop.getProperty("redis_host_db"));
		host_jedis = new Jedis(redis_host_ip, redis_host_port, 100000);// redis服务器地址
		host_jedis.ping();
		host_jedis.select(redis_host_db);

		swadw = new SWADetectWordRed();
		swadw.load_local_config(prop);
		swadw.set_cw_jedis(cw_jedis);
		swacwd = new SWACrawlWordDetail();
		swacwd.load_local_config(prop);
		ssdb_ip = prop.getProperty("ssdb_ip");
		ssdb_port = Integer.parseInt(prop.getProperty("ssdb_port"));
		ssdb = new SSDB(ssdb_ip, ssdb_port);
		indus_dict_dir = prop.getProperty("indus_dict_dir");
		stop_dict_dir = prop.getProperty("stop_dict_dir");

		port = Integer.parseInt(prop.getProperty("port"));

	}

	public void load_local_config() throws Exception {
		/*
		 * redis_cw_ip = "192.168.110.182"; redis_port = 6379; redis_cw_db = 10;
		 * cw_jedis = new Jedis(redis_cw_ip, redis_port, 100000);// redis服务器地址
		 * cw_jedis.ping(); cw_jedis.select(redis_cw_db); swadw=new
		 * SWADetectWordRed(); swadw.load_local_config();
		 * swadw.set_cw_jedis(cw_jedis);
		 */
		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名

		address = address.trim();
		System.out.println("address in swatsmr:" + address);

		String sqs_ip = "";
		int sqs_port = 0;

		sqs_port = 1218;
		if (address.equals("adt2")) {
			redis_cw_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			redis_cw_ip = "192.168.110.181";
			sqs_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			redis_cw_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			redis_cw_ip = "192.168.110.188";
		} else if (address.equals("adt0")) {
			redis_cw_ip = "192.168.110.180";
		} else if (address.equals("hndx_fx_100")) {
			redis_cw_ip = "192.168.1.100";
			sqs_ip = "192.168.1.100";
		}

		redis_port = 6379;
		redis_cw_db = 10;
		cw_jedis = new Jedis(redis_cw_ip, redis_port, 100000);// redis服务器地址
		cw_jedis.ping();
		cw_jedis.select(redis_cw_db);

		System.out.println("here1");

		redis_host_port = 6379;
		redis_host_db = 5;
		if (address.equals("adt1")) {
			redis_host_ip = "192.168.110.182";
		} else if (address.equals("hndx_fx_100")) {
			redis_host_ip = "192.168.1.100";
		}

		host_jedis = new Jedis(redis_host_ip, redis_host_port, 100000);// redis服务器地址
		host_jedis.ping();
		host_jedis.select(redis_host_db);
		System.out.println("here2");
		swadw = new SWADetectWordRed();
		swadw.load_local_config();
		swadw.set_cw_jedis(cw_jedis);
		swacwd = new SWACrawlWordDetail();
		swacwd.load_local_config();

		System.out.println("here5");

		Httpsqs4j.setConnectionInfo(sqs_ip, sqs_port, "UTF-8");
		sqsclient = Httpsqs4j.createNewClient();
		sqsstatus = sqsclient.getStatus("bd_sw");
		/*
		 * System.out.println(sqsstatus.version);
		 * System.out.println(sqsstatus.queueName);
		 * System.out.println(sqsstatus.maxNumber);
		 * System.out.println(sqsstatus.getLap);
		 * System.out.println(sqsstatus.getPosition);
		 * System.out.println(sqsstatus.putLap);
		 * System.out.println(sqsstatus.putPosition);
		 * System.out.println(sqsstatus.unreadNumber);
		 * System.out.println("here3");
		 */

		ssdb = new SSDB("127.0.0.1", 8888);
		indus_dict_dir = "dict/indus_dict/nonstandard_word/bdw_fir_bat";
		stop_dict_dir = "dict/stop_dict/standard_word/indus_stop_words";

	}

	public void load_stop_words(String stop_word_dir) throws Exception {
		File stw_dir_file = new File(stop_word_dir);
		File[] stw_files = stw_dir_file.listFiles();

		String line = "";
		String word = "";

		stop_words = new Hashtable<String, Integer>();

		for (int i = 0; i < stw_files.length; i++) {
			if (stw_files[i].isDirectory()) {
				continue;
			}
			FileReader fr = new FileReader(stw_files[i]);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				word = line.trim();
				if ((word == null) || (word.equals(""))) {
					continue;
				}
				if (!(stop_words.containsKey(word))) {
					stop_words.put(word, 1);
				}
			}
			br.close();
			fr.close();
		}

		Enumeration stop_enum = stop_words.keys();
		String stop_key = "";
		while (stop_enum.hasMoreElements()) {
			stop_key = stop_enum.nextElement() + "";
			// System.out.println(stop_key);
		}

	}

	public int load_cate_to_redis(String wci_dir) throws Exception {
		int res = 0;
		File wci_dir_file = new File(wci_dir);
		File[] wci_files = wci_dir_file.listFiles();
		long rdb_size = 0;
		rdb_size = cw_jedis.dbSize();
		if (rdb_size > 0) {
			System.out.println("redis db has been loaded");
			return 1;
		}
		String line = "";
		String word = "";
		String cate = "";
		String hn = "";
		String small_words = "";
		String[] seg_arr = null;
		String wch_info = "";
		String sw_info = "";
		String[] temp_seg = null;

		for (int i = 0; i < wci_files.length; i++) {
			if (wci_files[i].isDirectory()) {
				continue;
			}
			FileReader fr = new FileReader(wci_files[i]);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {

				seg_arr = line.split("\001");
				// System.out.println(line+":"+seg_arr.length);
				if ((seg_arr.length) < 1) {
					continue;
				}

				wch_info = seg_arr[0].trim();
				sw_info = "";
				if (seg_arr.length > 1) {
					sw_info = seg_arr[1].trim();
				}
				if ((wch_info == null) || (wch_info.equals(""))) {
					continue;
				}
				temp_seg = wch_info.split("\\s+");

				if ((temp_seg.length) != 3) {
					continue;
				}

				word = temp_seg[0].trim();
				cate = temp_seg[1].trim();
				hn = temp_seg[2].trim();
				if (cate.equals("店商")) {
					cate = "电商";
				}
				if (!(cw_jedis.exists(word))) {
					cw_jedis.set(word, cate + "\001" + sw_info);
				}
			}
			br.close();
			fr.close();
		}

		return res;
	}

	private static SWAHttpServerMulRedSqs serverInstance;
	private HttpServer httpServer;
	private ExecutorService executor;
	public int port = 0;

	@Override
	public void run() {
		try {
			try {
				HttpServer hs = HttpServer.create(new InetSocketAddress(port),
						0);
				SWAHandlerRedSqs mh = new SWAHandlerRedSqs();
				mh.setSwaDW(swadw);
				mh.setSwaCwd(swacwd);
				mh.setJedis(cw_jedis);
				mh.setHostJedis(host_jedis);
				mh.setMyStopWords(stop_words);
				mh.setSqsClient(sqsclient);
				mh.setSqsStatus(sqsstatus);
				mh.setSSDB(ssdb);
				mh.setResponse(resp);
				// mh.setEwa(ewa);
				hs.createContext("/cate_sw", mh);
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

	static void shutdown() {

		try {
			System.out.println("Shutting down TestServer.");
			serverInstance.httpServer.stop(0);

		} catch (Exception e) {
			e.printStackTrace();
		}

		synchronized (serverInstance) {
			serverInstance.notifyAll();
		}

	}

	public static void main(String[] args) throws Exception {
		String config_file = "";
		if (args.length != 1) {
			System.out.println("用法 :EWAECServer <configure file>");
			System.exit(1);
		}

		config_file = args[0];
		System.out.println("config_file:" + config_file);
		serverInstance = new SWAHttpServerMulRedSqs();
		serverInstance.load_local_config(config_file);
		serverInstance.load_cate_to_redis(serverInstance.indus_dict_dir);
		serverInstance.load_stop_words(serverInstance.stop_dict_dir);
		serverInstance.swadw.setStopWords(serverInstance.stop_words);

		Thread serverThread = new Thread(serverInstance);
		serverThread.start();

		Runtime.getRuntime().addShutdownHook(new OnShutdownRedSqs());

		try {
			serverThread.join();
		} catch (Exception e) {
		}
	}

}

/* Responds to the /test URI. */
class SWAHandlerRedSqs implements HttpHandler {

	public Jedis my_cw_jedis;
	public Jedis my_host_jedis;
	public SWADetectWordRed my_swadw = null;
	public SWACrawlWordDetail my_swacwd = null;
	boolean debug = Boolean.getBoolean("test.debug");
	public Hashtable<String, Integer> my_stop_words = null;
	public HttpsqsClient my_sqsclient = null;
	public HttpsqsStatus my_sqsstatus = null;
	public SSDB my_ssdb = null;
	public Response my_resp = null;

	/*
	 * 直接查询redis,若能得到结果，则输出，若不能得到结果，则放到队列里，并返回NA (non-Javadoc)
	 * 
	 * @see
	 * com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange
	 * )
	 */
	public void handle(HttpExchange exchange) throws IOException {

		String title_str = exchange.getRequestURI().toString();
		// System.out.println("title_str:" + title_str);

		title_str = title_str.replaceFirst("/cate_sw/do\\?t=", "");
		// System.out.println("title_str:" + title_str);
		String de_title = new String(Base64.decode(title_str));
		System.out.println("de_title:" + de_title);
		de_title = de_title.replaceAll("\\+", "");
		String res = "";
		int limit = 8;
		String crawl_cate = "";
		try {
			// 从redis读取结果
			res = getWord_det_info(de_title, "redis");
			// System.out.println("res0:" + res);
			if ((res.indexOf("NA")) != -1) {
				// 如果含有NA,从ssdb里读取结果
				res = getWord_det_info(de_title, "ssdb");
				// System.out.println("res ssdb:"+res);
			}
			if ((res.indexOf("NA")) != -1) {

				//放在队列中
				// System.out.println("put httpsqs:"+de_title);
				// my_sqsclient.putString("bd_sw", de_title);
			}

			if ((res.indexOf("NA")) != -1) {
				// System.out.println("begin call one_sw_process");
				limit = 8;
				res = my_swadw.one_direct_process(de_title, limit);
				// System.out.println("res8:" + res);
			}

			if ((res.indexOf("NA")) != -1) {
				// System.out.println("begin call one_sw_process");
				limit = 7;
				res = my_swadw.one_direct_process(de_title, limit);
				// System.out.println("res7:" + res);
			}
			if ((res.indexOf("NA")) != -1) {
				// System.out.println("begin call one_sw_process");
				limit = 6;
				res = my_swadw.one_direct_process(de_title, limit);
				// System.out.println("res6:" + res);
			}

			if ((res.indexOf("NA")) != -1) {
				// System.out.println("begin call one_sw_process");
				limit = 5;
				res = my_swadw.one_direct_process(de_title, limit);
				// System.out.println("res5:" + res);
			}

			if ((res.indexOf("NA")) != -1) {
				// System.out.println("begin call one_sw_process");
				limit = 4;
				res = my_swadw.one_direct_process(de_title, limit);
				// System.out.println("res4:" + res);
			}

			/*
			 * if((res.indexOf("NA"))!=-1) {
			 * //System.out.println("begin call one_sw_process");
			 * res=my_swacwd.getSWCate(de_title); crawl_cate=res;
			 * 
			 * if(de_title.length()<8) { res=res+"\001"+de_title;
			 * de_title=de_title.trim();
			 * if((de_title!=null)&&(!(de_title.equals(
			 * "")))&&(!(my_cw_jedis.exists(de_title)))) {
			 * my_cw_jedis.set(de_title, crawl_cate+"\001"); } } else {
			 * res=res+"\001"; } }
			 */


			/**
			 * 如果是url，直接获取url分类
			 */
			String url_cate = "";
			if (isUrl(de_title)) {
				url_cate = my_host_jedis.get(de_title);
				if (url_cate == null) {
					url_cate = "NA";
				}
				res = url_cate + "\001";
			}
			//如果是英语长句
			if (isLongEnSearch(de_title)) {
				res = "NA\001";
			}

			System.out.println("res:" + res);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

		// System.out.println("res is :" + res);
		String encode_res = Base64.encodeBytes(res.getBytes());
		encode_res = encode_res.replaceAll("\\s+", "");
		// System.out.println("encode_res:" + encode_res);

		String response = encode_res;
		exchange.sendResponseHeaders(200, response.length());
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	public void setJedis(Jedis temp_jedis) {
		this.my_cw_jedis = temp_jedis;
	}

	public void setHostJedis(Jedis temp_jedis) {
		this.my_host_jedis = temp_jedis;
	}

	public void setSwaDW(SWADetectWordRed temp_swadw) {
		this.my_swadw = temp_swadw;
	}

	public void setSwaCwd(SWACrawlWordDetail temp_swacwd) {
		this.my_swacwd = temp_swacwd;
	}

	public void setMyStopWords(Hashtable<String, Integer> temp_stop_words) {
		this.my_stop_words = temp_stop_words;
	}

	public void setSqsClient(HttpsqsClient temp_sqs_client) {
		this.my_sqsclient = temp_sqs_client;
	}

	public void setSqsStatus(HttpsqsStatus temp_sqs_status) {
		this.my_sqsstatus = temp_sqs_status;
	}

	public void setSSDB(SSDB temp_ssdb) {
		this.my_ssdb = temp_ssdb;
	}

	public void setResponse(Response temp_resp) {
		this.my_resp = temp_resp;
	}

	public String getWord_det_info(String word, String db_type) {
		String wdi = "";
		String[] ws = null;
		word = word.trim();
		if ((word == null) || (word.equals(""))) {
			return "NA";
		}
		// 根据分隔符分词
		ws = word.split("\\s+");
		String one_word = "";
		Vector wdi_vec = new Vector();
		byte[] ssdb_res = null;
		String ssdb_csw_info = "";
		if (db_type.equals("ssdb")) {
			/**
			 * 从ssdb中获取整词结果，如果有直接返回
			 */
			String word_md5 = MD5Test.MD5(word);
			word_md5 = word_md5.trim();
			try {
				ssdb_res = my_ssdb.get(word_md5);
			} catch (Exception e) {

			}
			if (ssdb_res != null) {
				ssdb_csw_info = new String(ssdb_res);
			} else {
				ssdb_csw_info = "NA";
			}

			return ssdb_csw_info;
		}

		/**
		 * 把每个短词结果放入向量wdi_vec中
		 */
		String csw_info = "";

		for (int i = 0; i < ws.length; i++) {
			one_word = ws[i].trim();
			csw_info = "";
			if (db_type.equals("redis")) {
				csw_info = my_cw_jedis.get(one_word);
			} else if (db_type.equals("ssdb")) {
				try {
					String one_word_md5 = MD5Test.MD5(one_word);
					one_word_md5 = one_word_md5.trim();
					ssdb_res = my_ssdb.get(one_word_md5);
					if (ssdb_res != null) {
						csw_info = new String(ssdb_res);
					} else {
						csw_info = "";
					}
				} catch (Exception e) {

				}
			}

			if ((csw_info != null) && (!(csw_info.equals("")))) {
				// System.out.println("one_word:"+one_word+"  csw_info:"+csw_info);
				wdi_vec.add(csw_info);
			}
		}

		/**
		 * 分析短词结果
		 */
		wdi = get_cate_and_small_words(wdi_vec);

		String[] tt_seg = wdi.split("\001");
		String cate = "";
		String sw_info = "";
		String tsw_small = "";
		String tsw_stop = "";
		if ((tt_seg.length) == 2) {
			cate = tt_seg[0].trim();
			sw_info = tt_seg[1].trim();
			tsw_small = trim_small_words(word + " " + sw_info);
			tsw_stop = trim_stop_words(tsw_small);
			if (tsw_stop == null) {
				tsw_stop = "";
			}
			// wdi=cate+"\001"+word+" "+sw_info;
			wdi = cate + "\001" + tsw_stop;
		} else if ((tt_seg.length) == 1) {
			cate = tt_seg[0].trim();
			tsw_small = trim_small_words(word);
			tsw_stop = trim_stop_words(tsw_small);
			if (tsw_stop == null) {
				tsw_stop = "";
			}
			// wdi=cate+"\001"+word;
			wdi = cate + "\001" + tsw_stop;
		} else {
			wdi = "NA";
		}
		wdi = wdi.trim();

		return wdi;
	}

	/**
	 * 从一些cate\001swinfo向量中，选出最可能的cate，以及相应的词
	 * 
	 * @param vec
	 * @return
	 */
	public String get_cate_and_small_words(Vector vec) {
		String temp_csw = "";
		Hashtable<String, Integer> cate_hash = new Hashtable<String, Integer>();

		String one_csw = "";
		String cate = "";
		String sw_info = "";

		String[] temp_seg = null;
		int old_count = 0;
		for (int i = 0; i < vec.size(); i++) {
			one_csw = vec.get(i) + "";
			temp_seg = one_csw.split("\001");
			if ((temp_seg.length) < 1) {
				continue;
			}
			cate = temp_seg[0].trim();
			sw_info = "";
			if ((temp_seg.length) > 1) {
				sw_info = temp_seg[1].trim();
			}

			if (!(cate_hash.containsKey(cate))) {
				cate_hash.put(cate, 1);
			} else {
				old_count = Integer.parseInt(cate_hash.get(cate) + "");
				cate_hash.remove(cate);
				old_count = old_count + 1;
				cate_hash.put(cate, old_count);
			}
		}

		Enumeration cate_enum = cate_hash.keys();
		int temp_num = 0;
		int max_num = 0;
		String max_cate = "NA";
		while (cate_enum.hasMoreElements()) {
			cate = cate_enum.nextElement() + "";
			cate = cate.trim();
			if ((cate == null) || (cate.equals(""))) {
				continue;
			}
			temp_num = cate_hash.get(cate);
			if (temp_num > max_num) {
				max_num = temp_num;
				max_cate = cate;
			}
		}

		String all_sw_info = "";
		for (int i = 0; i < vec.size(); i++) {
			sw_info = "";
			one_csw = vec.get(i) + "";
			temp_seg = one_csw.split("\001");
			if ((temp_seg.length) < 1) {
				continue;
			}
			cate = temp_seg[0].trim();
			if ((temp_seg.length) > 1) {
				sw_info = temp_seg[1].trim();
			}
			if (cate.equals(max_cate)) {
				all_sw_info = all_sw_info + sw_info + " ";
			}
		}
		all_sw_info = all_sw_info.trim();
		String trim_sw_info = trim_small_words(all_sw_info);
		String trim_stop_info = trim_stop_words(trim_sw_info);
		// System.out.println("trim_sw_info:"+trim_sw_info);
		// System.out.println("trim_stop_info:"+trim_stop_info);
		temp_csw = cate + "\001" + trim_stop_info;
		// System.out.println("temp_csw:"+temp_csw);
		return temp_csw;
	}

	/**
	 * 去除更短的词
	 * 
	 * @param sw_info
	 * @return
	 */
	public String trim_small_words(String sw_info) {
		String tsw_info = "";
		String look_four_str = "";
		String look_three_str = "";
		String en_num_str = "";
		sw_info = sw_info.trim();
		if ((sw_info == null) || (sw_info.equals(""))) {
			return "";
		}

		// 分割切词
		String[] seg_arr = sw_info.split("\\s+");
		String temp_word = "";
		if (seg_arr == null) {
			return "";
		}

		Vector sel_word_vec = new Vector();
		for (int i = 0; i < seg_arr.length; i++) {
			temp_word = seg_arr[i].trim();
			if ((temp_word == null) || (temp_word.equals(""))) {
				continue;
			}
			// 长度为4的词
			if (temp_word.length() == 4) {
				look_four_str = look_four_str + temp_word + "_";
			} else if (temp_word.length() == 3) {
				// 长度为3的词
				look_three_str = look_three_str + temp_word + "_";
			}

			// 英文和数字
			if (isEnNumbers(temp_word)) {
				en_num_str = en_num_str + temp_word + "_";
			}
		}

		/**
		 * 应该有bug，如果短词先于长词出现，则同样会输出结果
		 */
		for (int i = 0; i < seg_arr.length; i++) {
			temp_word = seg_arr[i].trim();
			// 长度为2的词
			if ((temp_word.length()) == 2) {
				// 不是更长的词的子串
				if (((look_three_str.indexOf(temp_word)) == -1)
						&& ((look_four_str.indexOf(temp_word)) == -1)) {
					sel_word_vec.add(temp_word);
				}
			} else if ((temp_word.length()) == 3) {
				// 不是长度为4的词
				if ((look_four_str.indexOf(temp_word)) == -1) {
					sel_word_vec.add(temp_word);
				}
			} else if (isEnNumbers(temp_word)) {
				// 不是英文和数字
				if ((en_num_str.indexOf(temp_word)) == -1) {
					sel_word_vec.add(temp_word);
				}
			} else {
				// 否则增加
				sel_word_vec.add(temp_word);
			}
		}

		// 最后结果
		for (int i = 0; i < sel_word_vec.size(); i++) {
			temp_word = sel_word_vec.get(i) + "";
			tsw_info = tsw_info + temp_word + " ";
		}
		tsw_info = tsw_info.trim();
		return tsw_info;
	}

	/**
	 * 去除停用词
	 * 
	 * @param sw_info
	 * @return
	 */
	public String trim_stop_words(String sw_info) {
		// System.out.println("stop_siz:"+my_stop_words.size());
		String tsw = "";
		sw_info = sw_info.trim();

		if ((sw_info == null) || (sw_info.equals(""))) {
			return "";
		}

		String[] seg_arr = null;
		String temp_word = "";

		seg_arr = sw_info.split("\\s+");
		if (seg_arr == null) {
			return "";
		}

		Vector sel_word_vec = new Vector();
		Hashtable<String, Integer> redup_hash = new Hashtable<String, Integer>();
		for (int i = 0; i < seg_arr.length; i++) {
			temp_word = seg_arr[i].trim();
			if ((temp_word == null) || (temp_word.equals(""))) {
				continue;
			}

			if ((!(my_stop_words.containsKey(temp_word)))
					&& (!(redup_hash.containsKey(temp_word)))) {
				sel_word_vec.add(temp_word);
			}
			if (!(redup_hash.containsKey(temp_word))) {
				redup_hash.put(temp_word, 1);
			}
		}

		for (int i = 0; i < sel_word_vec.size(); i++) {
			temp_word = sel_word_vec.get(i) + "";
			tsw = tsw + temp_word + " ";
		}
		tsw = tsw.trim();

		return tsw;
	}

	public boolean isEnNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+A-Za-z]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}

	public boolean isUrl(String str) {
		boolean isUrl = false;
		if (Pattern
				.matches(
						"(?:(?:www)|(?:WWW)|[a-zA-Z0-9])[\\.]?[a-z0-9_A-Z\\.\\?\\&\\=]*",
						str)) {
			isUrl = true;
		} else if (Pattern
				.matches(
						"http:\\/\\/(?:(?:www)|(?:WWW)|[a-zA-Z0-9])[\\.]?[a-z0-9_A-Z\\.\\/\\?\\&\\=]*",
						str)) {
			isUrl = true;
		}
		return isUrl;
	}

	public boolean isLongEnSearch(String str) {
		boolean isles = false;
		String les_reg = "[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*\\+[A-Za-z0-9\\.\\,]*";
		Pattern les_pat = Pattern.compile(les_reg);
		Matcher les_mat = les_pat.matcher(str);
		while (les_mat.find()) {
			isles = true;
		}
		les_reg = "[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*\\s+[A-Za-z0-9\\.\\,]*";
		les_pat = Pattern.compile(les_reg);
		les_mat = les_pat.matcher(str);
		while (les_mat.find()) {
			isles = true;
		}

		return isles;
	}

}

/* Responds to a JVM shutdown by stopping the server. */
class OnShutdownRedSqs extends Thread {
	public void run() {
		SWAHttpServerMulRedSqs.shutdown();
	}

}
