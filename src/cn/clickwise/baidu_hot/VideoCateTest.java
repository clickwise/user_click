package cn.clickwise.baidu_hot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import redis.clients.jedis.Jedis;
import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.file.uitls.FileReaderUtil;
import cn.clickwise.liqi.str.basic.SSO;


public class VideoCateTest {
	
	public Jedis jedis;
	public static double[] line_weights;
	public static String Version;
	public static int NUM_CLASS;
	public static int NUM_WORDS;
	public static int loss_function;
	public static int kernel_type;
	public static int para_d;
	public static int para_g;
	public static int para_s;
	public static int para_r;
	public static String para_u;
	public static int NUM_FEATURES;
	public static int train_num;
	public static int suv_num;
	public static double b;
	public static double alpha;
	public static int qid;
	public static String[] label_names = { "电影", "影视剧", "综艺", "噪音" };
	public String redis_video_dict_ip = "";
	public String redis_cated_words_ip = "";

	public int redis_port = 6379;
	public int redis_video_dict_db = 0;
	public String seg_server = "";
	public int seg_port = 0;
	public String tag_server = "";
	public int tag_port = 0;
	public Jedis cated_redis;
	public int redis_cated_words_db = 0;

	private Segmenter seg=null;
	private PosTagger posTagger=null;
	private KeyExtract ke=null;
	private HashMap video_dict=null;
	
	public VideoCateTest()
	{

		try {
			video_dict=getDictFromStream("dict_video.txt");	
			System.out.println(video_dict.get("秘史陆贞"));
			String model_path = "model";
			read_model(model_path);
			seg=new Segmenter();
			posTagger=new PosTagger("chinese-nodistsim.tagger");
			ke=new KeyExtract();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private String getContent(String url) throws Exception {
		String[] proxy_hosts = { "122.72.56.151", "122.72.56.152",
				"122.72.56.153", "122.72.102.60", "122.72.111.92",
				"122.72.111.98", "122.72.76.131", "122.72.76.132",
				"122.72.76.133", "122.72.11.129", "122.72.11.130",
				"122.72.11.131", "122.72.11.132", "122.72.99.2",
				"122.72.99.3", "122.72.99.4", "122.72.99.8" };
		String con = "";
		FileWriter fw = new FileWriter(new File("test_html.txt"));
		PrintWriter pw = new PrintWriter(fw);
		HttpClient httpclient = new DefaultHttpClient();
		double ran = Math.random();
		// System.out.println("ran:"+ran);
		int rani = -1;
		rani = (int) (ran * 16);
		// //HttpHost proxy = new HttpHost(proxy_hosts[rani], 80, "http");
		// //httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// // proxy);
		// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,20000);

		try {
			// 创建httpget.

			HttpGet httpget = new HttpGet(url);
			System.out.println("executing request " + httpget.getURI());
			// 执行get请求.
			HttpResponse response = httpclient.execute(httpget);

			// 获取响应状态
			int statusCode = response.getStatusLine().getStatusCode();
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

		fw.close();
		pw.close();

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

	public boolean isValidUrl(String url) {
		boolean isVal = true;
		if ((url.indexOf("'") != -1) || (url.indexOf("}") != -1)) {
			return false;
		}

		return isVal;
	}

	public String getFilterContent(String raw_content) {
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

	public String getSample(String filter_content) throws Exception {
		String sample = "";
		String seg_s = seg.segAnsi(filter_content);
		seg_s = seg_s.trim();
		if (seg_s.equals("")) {
			return "";
		}
                System.out.println("seg_s:"+seg_s);
		String tag_s = posTagger.tag(seg_s);
		tag_s = tag_s.trim();
		if (tag_s.equals("")) {
			return "";
		}

               System.out.println("tag_s:"+tag_s);
		String key_s = "";
		key_s = ke.keyword_extract(tag_s);
		key_s = key_s.trim();
		if (key_s.equals("")) {
			return "";
		}

                System.out.println("key_s:"+key_s);
		sample = get_word_id(key_s);
                System.out.println("sample:"+sample);
		sample = sample.trim();
		if (sample.equals("")) {
			return "";
		}

		return sample;

	}
    /*
	public String seg(String s) throws Exception {
		s = s + "\n";
		String seg_s = "";
		String server = seg_server;
		int port = seg_port;
		Socket socket = new Socket(server, port);

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		out.write(s.getBytes());
		out.flush();

		byte[] receiveBuf = new byte[10032 * 8];
		in.read(receiveBuf);

		seg_s = new String(receiveBuf);
		socket.close();
		return seg_s;
	}
    */
	/*
	public String tag(String seg_s) throws Exception {
		String tag_s = "";
		String server = tag_server;
		int port = tag_port;
		Socket socket = new Socket(server, port);
		seg_s = seg_s + "\n";
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		out.write(seg_s.getBytes());
		out.flush();

		byte[] receiveBuf = new byte[10032];
		in.read(receiveBuf);

		tag_s = new String(receiveBuf);
		socket.close();
		return tag_s;

	}
    */
	/*
	public String keyword_extract(String text) {
		String k_s = "";
		String[] seg_arr = text.split("\\s+");
		Vector new_word_arr = new Vector();
		String[] history_word_arr = new String[7];
		for (int i = 0; i < history_word_arr.length; i++) {
			history_word_arr[i] = "";
		}

		String key_word = "";
		String subkey1 = "", subkey2 = "", subkey4 = "", subkey5 = "", subkey6 = "", subkey7 = "", subkey8 = "";

		for (int i = 0; i < seg_arr.length; i++) {
			// System.out.println(i + ":" + seg_arr[i]);
			if (((seg_arr[i].indexOf("/NN")) != -1)
					|| ((seg_arr[i].indexOf("/NR")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("/NN")) != -1) {
					key_word = key_word.replaceAll("/NN", "");
				} else if ((seg_arr[i].indexOf("/NR")) != -1) {
					key_word = key_word.replaceAll("/NR", "");
				}
				key_word = key_word.trim();
				if (key_word.length() > 1) {
					new_word_arr.add(key_word);
					if ((key_word.length()) == 3) {
						subkey1 = key_word.substring(0, 2);
						subkey2 = key_word.substring(1, 3);
						new_word_arr.add(subkey1);
						new_word_arr.add(subkey2);
					}

					if ((key_word.length()) == 4) {
						subkey4 = key_word.substring(0, 2);
						subkey5 = key_word.substring(1, 3);
						subkey6 = key_word.substring(2, 4);
						subkey7 = key_word.substring(0, 3);
						subkey8 = key_word.substring(1, 4);
						new_word_arr.add(subkey4);
						new_word_arr.add(subkey5);
						new_word_arr.add(subkey6);
						new_word_arr.add(subkey7);
						new_word_arr.add(subkey8);
					}
				}

			} else if (seg_arr[i].length() > 5) {
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("/.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);
			}

			if (i > 4) {
				history_word_arr[0] = seg_arr[i - 5];
				history_word_arr[1] = seg_arr[i - 4];
				history_word_arr[2] = seg_arr[i - 3];
				history_word_arr[3] = seg_arr[i - 2];
				history_word_arr[4] = seg_arr[i - 1];
				history_word_arr[5] = seg_arr[i];
				if (((history_word_arr[0].indexOf("/NN")) != -1)
						&& ((history_word_arr[1].indexOf("/NN")) != -1)
						&& ((history_word_arr[2].indexOf("/NN")) != -1)
						&& ((history_word_arr[3].indexOf("/NN")) != -1)
						&& ((history_word_arr[4].indexOf("/NN")) != -1)
						&& ((history_word_arr[5].indexOf("/NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll(
							"/NN", "").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll(
							"/NN", "").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll(
							"/NN", "").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll(
							"/NN", "").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll(
							"/NN", "").trim();
					history_word_arr[5] = history_word_arr[5].replaceAll(
							"/NN", "").trim();
					new_word_arr.add(history_word_arr[0]
							+ history_word_arr[1] + history_word_arr[2]
							+ history_word_arr[3] + history_word_arr[4]
							+ history_word_arr[5]);
				}
				history_word_arr[0] = "";
				history_word_arr[1] = "";
				history_word_arr[2] = "";
				history_word_arr[3] = "";
				history_word_arr[4] = "";
				history_word_arr[5] = "";
			}

			if (i > 3) {
				history_word_arr[0] = seg_arr[i - 4];
				history_word_arr[1] = seg_arr[i - 3];
				history_word_arr[2] = seg_arr[i - 2];
				history_word_arr[3] = seg_arr[i - 1];
				history_word_arr[4] = seg_arr[i];
				if (((history_word_arr[0].indexOf("/NN")) != -1)
						&& ((history_word_arr[1].indexOf("/NN")) != -1)
						&& ((history_word_arr[2].indexOf("/NN")) != -1)
						&& ((history_word_arr[3].indexOf("/NN")) != -1)
						&& ((history_word_arr[4].indexOf("/NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll(
							"/NN", "").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll(
							"/NN", "").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll(
							"/NN", "").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll(
							"/NN", "").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll(
							"/NN", "").trim();

					new_word_arr.add(history_word_arr[0]
							+ history_word_arr[1] + history_word_arr[2]
							+ history_word_arr[3] + history_word_arr[4]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";
				history_word_arr[2] = "";
				history_word_arr[3] = "";
				history_word_arr[4] = "";
			}

			if (i > 2) {
				history_word_arr[0] = seg_arr[i - 3];
				history_word_arr[1] = seg_arr[i - 2];
				history_word_arr[2] = seg_arr[i - 1];
				history_word_arr[3] = seg_arr[i];
				if (((history_word_arr[0].indexOf("/NN")) != -1)
						&& ((history_word_arr[1].indexOf("/NN")) != -1)
						&& ((history_word_arr[2].indexOf("/NN")) != -1)
						&& ((history_word_arr[3].indexOf("/NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll(
							"/NN", "").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll(
							"/NN", "").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll(
							"/NN", "").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll(
							"/NN", "").trim();
					new_word_arr.add(history_word_arr[0]
							+ history_word_arr[1] + history_word_arr[2]
							+ history_word_arr[3]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";
				history_word_arr[2] = "";
				history_word_arr[3] = "";

			}

			if (i > 1) {
				history_word_arr[0] = seg_arr[i - 2];
				history_word_arr[1] = seg_arr[i - 1];
				history_word_arr[2] = seg_arr[i];
				if (((history_word_arr[0].indexOf("/NN")) != -1)
						&& ((history_word_arr[1].indexOf("/NN")) != -1)
						&& ((history_word_arr[2].indexOf("/NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll(
							"/NN", "").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll(
							"/NN", "").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll(
							"/NN", "").trim();
					new_word_arr.add(history_word_arr[0]
							+ history_word_arr[1] + history_word_arr[2]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";
				history_word_arr[2] = "";
			}

			if (i > 0) {
				history_word_arr[0] = seg_arr[i - 1];
				history_word_arr[1] = seg_arr[i];
				// System.out
				// .println("history_word_arr[0]:" + history_word_arr[0]);
				// System.out
				// .println("history_word_arr[1]:" + history_word_arr[1]);
				// System.out.println((history_word_arr[0].indexOf("/NN")) +
				// ":"
				// + (history_word_arr[1].indexOf("/NN")));
				if (((history_word_arr[0].indexOf("/NN")) != -1)
						&& ((history_word_arr[1].indexOf("/NN")) != -1)) {
					// System.out.println("add the:"
					// + (history_word_arr[0] + history_word_arr[1]));
					history_word_arr[0] = history_word_arr[0].replaceAll(
							"/NN", "").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll(
							"/NN", "").trim();
					new_word_arr.add(history_word_arr[0]
							+ history_word_arr[1]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";

			}
		}

		String temp_CC = "";
		for (int i = 0; i < new_word_arr.size(); i++) {
			temp_CC = new_word_arr.get(i) + "";
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_\\#]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		return k_s;
	}
    */
	
	public String get_word_id(String s) {
		String words[] = s.split("[\\s]+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				////ids = jedis.get(words[i]);
				ids = video_dict.get(words[i])+"";
                                //System.out.println("ids:"+ids);
			} catch (Exception re) {
                              re.printStackTrace();
			}
			if (ids == null) {
				continue;
			}
                        if(SSO.tioe(ids))
			{
				continue;
			}
			if(!(Pattern.matches("[\\d]*", ids)))
			{
				continue;
			}
			Long id = Long.parseLong(ids);
			if (id != null) {
				Integer cnt = cnts.get(id);
				if (cnt == null)
					cnts.put(id, 1);
				else
					cnts.put(id, cnt + 1);
			}
		}
		List<Long> keys = new ArrayList<Long>(cnts.keySet());
		Collections.sort(keys, new Comparator<Long>() {
			public int compare(Long l1, Long l2) {
				if (l1 > l2)
					return 1;
				else if (l1 < l2)
					return -1;
				return 0;
			}
		});

		for (int i = 0; i < keys.size(); i++) {
			Long l = keys.get(i);
			if (i == 0)
				res += l + ":" + cnts.get(l);
			else
				res += " " + l + ":" + cnts.get(l);
		}
		return res;
	}

	public Label docate(String sample_line) {

		Label y = null;
		Word[] sample = null;

		String[] sample_arr = sample_line.split("\\s+");
		sample = new Word[sample_arr.length];
		for (int i = 0; i < sample.length; i++) {
			sample[i] = new Word();
		}

		String temp_token = "";
		int temp_index = 0;
		double temp_weight = 0.0;

		for (int i = 0; i < sample_arr.length; i++) {
			// System.out.println(i+" "+sample_arr[i]);
			temp_token = sample_arr[i];
			if (Pattern.matches("\\d+:[\\d\\.]+", temp_token)) {
				temp_index = Integer.parseInt(temp_token.substring(0,
						temp_token.indexOf(":")));
				temp_weight = Double.parseDouble(temp_token.substring(
						temp_token.indexOf(":") + 1, temp_token.length()));
				sample[i].wnum = temp_index;
				sample[i].weight = temp_weight;
			}
		}

		// sample=getWords(sample_line);
		y = classify_struct_example(sample);
		// System.out.println("y.first_label:"+y.first_class+"  y.second_label:"+y.second_class);
		return y;
	}

	public Label classify_struct_example(Word[] sample) {

		Label y = null;
		double score = 0;

		Label best_label = null;
		double best_score = -1;

		Word[] fvec = null;
		for (int i = 0; i < 4; i++) {
			y = new Label();
			y.first_class = (i + 1);
			fvec = psi(sample, y);
			score = classify_example(fvec);
			if (score > best_score) {
				best_score = score;
				best_label = y;
			}
		}
		best_label.score = best_score;
		return best_label;
	}

	public class Word {
		int wnum;
		double weight;
	}

	public class Label {
		int first_class;
		double score;
	}

	public Word[] psi(Word[] sample, Label y) {
		Word[] fvec = null;
		int veclength = (sample.length) * NUM_CLASS;
		fvec = new Word[veclength];
		for (int i = 0; i < veclength; i++) {
			fvec[i] = new Word();
		}

		int c1 = y.first_class;
		Word temp_word = null;
		int fi = 0;
		// System.out.println();
		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			// System.out.print(temp_word.wnum+":"+temp_word.weight+" ");
		}
		// System.out.println();
		// System.out.println("y.first_class:"+y.first_class+" "+y.second_class);
		// 第一级类别特征
		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			fvec[fi].wnum = temp_word.wnum + (c1 - 1) * NUM_WORDS;
			fvec[fi].weight = temp_word.weight;
			// System.out.print(fvec[fi].wnum+":"+fvec[fi].weight+" ");
			fi++;

		}

		// System.out.println();
		return fvec;
	}

	public double classify_example(Word[] fvec) {
		double score = 0;
		Word samp_word = null;

		for (int i = 0; i < fvec.length; i++) {
			samp_word = fvec[i];
			if (samp_word.wnum < NUM_FEATURES) {
				score = score + samp_word.weight
						* line_weights[samp_word.wnum];
			}
		}

		return score;
	}

	public String getCateName(Label y) {
		String cate_name = "";
		int tempid = y.first_class;
                System.out.println("tempid:"+tempid);
		if ((tempid >= 1) && (tempid <= 4)) {
			cate_name = label_names[tempid - 1];
		} else {
			cate_name = "NA";
		}

		return cate_name;
	}
	
	/**
	 * 读取模型的词典文件，建立单词索引
	 * 词典索引从1开始
	 * @return
	 */
	public HashMap getDictFromStream(String input_file) {
		// TODO Auto-generated method stub
	
		HashMap hm=new HashMap();
	    String item="";
	    String word="";
	    String index_str="";
		int index=0;
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + input_file);
		InputStreamReader model_isr = new InputStreamReader(model_is);

		BufferedReader br = new BufferedReader(model_isr);
		//FileReader fr=null;
		
		String[] seg_arr=null;
			
		try{
		  // fr=new FileReader(new File(input_file));
		  // br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   item=item.trim();
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   word=seg_arr[0].trim();
			   index_str=seg_arr[1].trim();

			   if(!(SSO.tnoe(word)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(index_str)))
			   {
				   continue;
			   }
			   index=Integer.parseInt(index_str);
			   //if(index%100==0)
			   //{
				   //System.out.println(word+" "+index_str);
			  // }
			   if(index<1)
			   {
				   continue;
			   }
			   hm.put(word,index);			   
		   }
		   
		   br.close();
		   model_is.close();
		   model_isr.close();
		   
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
		return hm;
	}
	

	public void read_model(String model_path) throws Exception {
      
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + model_path);
		
		InputStreamReader model_isr = new InputStreamReader(model_is);
		// File model_file = new File(model_path);
		// FileReader fr = new FileReader(model_file);
		BufferedReader br = new BufferedReader(model_isr);
		Version = cut_comment(br.readLine());
		NUM_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		NUM_WORDS = Integer.parseInt(cut_comment(br.readLine()));
		// System.out.println("NUM_WORDS:" + NUM_WORDS);
		loss_function = Integer.parseInt(cut_comment(br.readLine()));
		kernel_type = Integer.parseInt(cut_comment(br.readLine()));
		para_d = Integer.parseInt(cut_comment(br.readLine()));
		para_g = Integer.parseInt(cut_comment(br.readLine()));
		para_s = Integer.parseInt(cut_comment(br.readLine()));
		para_r = Integer.parseInt(cut_comment(br.readLine()));
		para_u = cut_comment(br.readLine());
		NUM_FEATURES = Integer.parseInt(cut_comment(br.readLine()));
		// System.out.println("NUM_FEATURES:" + NUM_FEATURES);
		train_num = Integer.parseInt(cut_comment(br.readLine()));
		suv_num = Integer.parseInt(cut_comment(br.readLine()));
		b = Double.parseDouble(cut_comment(br.readLine()));
		line_weights = new double[NUM_FEATURES + 2];
		for (int i = 0; i < line_weights.length; i++) {
			line_weights[i] = 0;
		}
		String line = br.readLine();
		StringTokenizer st = new StringTokenizer(line, " ");
		// System.out.println("st.count:" + st.countTokens());
		// System.out.println("end:" + line.substring(0, 1000));

		int current_pos = 0;
		int forward_num = 0;
		String temp_token = "";
		int temp_index;
		double temp_weight;
		int max_index = -1;
		int search_blank = 0;
		// System.out.println("line.length:" + line.length());
		while (current_pos < (line.length())) {
			// if((current_pos%10000==0))
			// {
			// System.out.println("current_pos:"+current_pos);
			// }
			forward_num = 0;
			temp_token = "";
			while ((current_pos + forward_num) < (line.length())) {
				// if(current_pos>26080000)
				// {
				// System.out.println("current_pos+forward_num:"+(current_pos+forward_num));
				// System.out.println("cc:"+line.charAt(current_pos+forward_num));
				// }

				if (((line.charAt(current_pos + forward_num)) != ' ')
						&& ((line.charAt(current_pos + forward_num)) != '#')) {
					temp_token = temp_token
							+ line.charAt(current_pos + forward_num);
					forward_num++;
				} else {
					temp_token = temp_token.trim();
					// if(current_pos>26080000)
					// System.out.println("temp_token:"+temp_token);
					if (((temp_token.indexOf(":")) == -1)
							&& (!temp_token.equals(""))) {
						alpha = Double.parseDouble(temp_token);
					} else if ((temp_token.indexOf("qid")) != -1) {
						qid = Integer.parseInt(temp_token
								.substring(temp_token.indexOf(":") + 1),
								temp_token.length());
					} else if (Pattern
							.matches("\\d+:[\\d\\.]+", temp_token)) {
						temp_index = Integer.parseInt(temp_token.substring(
								0, temp_token.indexOf(":")));
						temp_weight = Double.parseDouble(temp_token
								.substring(temp_token.indexOf(":") + 1,
										temp_token.length()));
						line_weights[temp_index] = temp_weight;
						if (temp_index > max_index) {
							max_index = temp_index;
						}
					}
					search_blank = 0;
					while ((current_pos + forward_num + search_blank) < line
							.length()) {
						if (line.charAt(current_pos + forward_num
								+ search_blank) == ' ') {
							search_blank++;
						} else {
							break;
						}
					}
					// if((current_pos%10000==0)||current_pos>26080000)
					// {
					// System.out.println("forward_num+search_blank:"+(forward_num+search_blank));
					// }
					if ((line.charAt(current_pos + forward_num)) == '#') {
						forward_num++;
					}
					current_pos = current_pos + forward_num + search_blank;
					break;
				}
			}
		}
		// fr.close();
		model_is.close();
		model_isr.close();
		br.close();
		// System.out.println("max_index:" + max_index);
	}

	public String cut_comment(String s) {
		String cut_s = "";
		if ((s.indexOf("#")) != -1) {
			cut_s = s.substring(0, s.indexOf("#"));
		} else {
			cut_s = s;
		}
		cut_s = cut_s.trim();
		return cut_s;
	}

	
	

	
	public String cate(String keyword)
	{
		String cate="";
		try{
		String code_url = getCodeUrl(keyword);
                System.out.println("code_url:"+code_url);
		String raw_content = getContent(code_url);
                System.out.println("raw_content:"+raw_content);
		String filted_content = getFilterContent(raw_content);
                System.out.println("filted_content:"+filted_content);
		String sample = getSample(filted_content);
                System.out.println("sample:"+sample);
		Label label_pre = docate(sample);
		String cate_name = getCateName(label_pre);
                cate=cate_name;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cate;
	}
	
	public static void main(String[] args)
	{
		VideoCateTest vct=new VideoCateTest();
		System.out.println("cate:"+vct.cate("电视剧"));
	}
	
}
