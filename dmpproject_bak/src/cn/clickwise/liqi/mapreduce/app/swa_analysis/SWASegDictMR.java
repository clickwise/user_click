package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import redis.clients.jedis.Jedis;

/**
 * 使用stanford 进行分词 ，但是分词结果用百度搜索词作为词典进行合并
 * 
 * @author lq
 * 
 */

public class SWASegDictMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public String seg_server = "";
		public int seg_port = 0;
		public String tag_server = "";
		public int tag_port = 0;
		public Jedis swa_dict_redis;
		public String swa_dict_ip;
		public int swa_dict_port;
		public int swa_dict_db;

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\t");

			String url = "";
			String title = "";
			String ips = "";

			String title_ips = "";

			String[] temp_seg = null;
			if (seg_arr != null && seg_arr.length > 1) {
				url = seg_arr[0].trim();

				for (int j = 1; j < seg_arr.length; j++) {
					title_ips = title_ips + seg_arr[j];
				}
				title_ips = title_ips.trim();

				if ((title_ips != null) && (!title_ips.equals(""))) {
					temp_seg = title_ips.split("\001");
					if ((temp_seg != null) && (temp_seg.length == 2)) {
						title = temp_seg[0].trim();
						ips = temp_seg[1].trim();
						if ((title != null) && (!title.equals(""))
								&& (title.length() > 5)) {
							String seg_s = "";
							String men_s = "";
							try {

								//String local_config_file = "/home/hadoop/lq/class_medlda_justed/swa_local_config.properties";
								load_local_config();
								seg_s = seg(title);
								seg_s = seg_s.trim();

								/*
								 * if(seg_s.equals("")) { String
								 * global_config_file=
								 * "/home/hadoop/lq/class_medlda_justed/swa_global_config.properties"
								 * ; load_global_config(global_config_file);
								 * seg_s=seg(title); }
								 */

								men_s = merge_sen(seg_s);
								men_s = men_s.trim();

								if ((men_s != null) && (!men_s.equals(""))) {
									word.set(url);
									word1.set(title + "\001" + men_s + "\001"
											+ ips);
									context.write(word, word1);
								}
							} catch (Exception e) {

							}

						}
					}
				}

			}

		}

		public boolean isValidTitle(String title) {
			title = title.trim();
			if (title == null || title.equals("")) {
				return false;
			}
			boolean isVal = true;
			String eng_mat = "[a-zA-Z0-9\\.:\\?#=_/&\\-%]*";
			if (Pattern.matches(eng_mat, title)) {
				isVal = false;
			}
			if ((title.indexOf("<") != -1) || (title.indexOf(">") != -1)) {
				isVal = false;
			}

			char first_char = title.charAt(0);
			if (Pattern.matches(eng_mat, first_char + "")) {
				isVal = false;
			}
			if (!isChinese(first_char)) {
				isVal = false;
			}

			return isVal;

		}

		public boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}

		public void load_global_config(String config_file) throws Exception {

			FileInputStream fis = new FileInputStream(config_file);
			Properties prop = new Properties();
			prop.load(fis);
			seg_server = prop.getProperty("seg_server");
			seg_port = Integer.parseInt(prop.getProperty("seg_port"));
			tag_server = prop.getProperty("tag_server");
			tag_port = Integer.parseInt(prop.getProperty("tag_port"));

			swa_dict_ip = prop.getProperty("swa_dict_ip");
			swa_dict_port = Integer.parseInt(prop.getProperty("swa_dict_port"));
			swa_dict_db = Integer.parseInt(prop.getProperty("swa_dict_db"));

			swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
			swa_dict_redis.ping();
			swa_dict_redis.select(swa_dict_db);
			fis.close();

		}

		public void load_local_config() throws Exception {
		
			InetAddress addr = InetAddress.getLocalHost();
	        String ip=addr.getHostAddress().toString();//获得本机IP
	        String address=addr.getHostName().toString();//获得本机名
	        /*
			FileInputStream fis = new FileInputStream(config_file);
			Properties prop = new Properties();
			prop.load(fis);
			seg_server = prop.getProperty("seg_server");
			seg_port = Integer.parseInt(prop.getProperty("seg_port"));
			tag_server = prop.getProperty("tag_server");
			tag_port = Integer.parseInt(prop.getProperty("tag_port"));

			swa_dict_ip = prop.getProperty("swa_dict_ip");
			swa_dict_port = Integer.parseInt(prop.getProperty("swa_dict_port"));
			swa_dict_db = Integer.parseInt(prop.getProperty("swa_dict_db"));

			swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
			swa_dict_redis.ping();
			swa_dict_redis.select(swa_dict_db);
			fis.close();
			*/
	        
	        seg_port=8092;
	        swa_dict_port=6379;
	        swa_dict_db=2;
	        
	        address=address.trim();
	        if(address.equals("adt0"))
	        {
	        	seg_server="192.168.110.182";
	        	swa_dict_ip="192.168.110.182";
	        }
	        else if(address.equals("adt1"))
	        {
	        	seg_server="192.168.110.181";
	        	swa_dict_ip="192.168.110.181";	
	        }
	        else if(address.equals("adt6"))
	        {
	        	seg_server="192.168.110.186";
	        	swa_dict_ip="192.168.110.186";
	        }
	        else if(address.equals("adt8"))
	        {
	        	seg_server="192.168.110.188";
	        	swa_dict_ip="192.168.110.188";
	        }
			
			swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
			swa_dict_redis.ping();
			swa_dict_redis.select(swa_dict_db);
		}

		public String dict_seg(String s) {
			String dict_seg_s = "";

			return dict_seg_s;
		}

		public String seg(String s) throws Exception {

			s = s + "\n";
			String seg_s = "";
			String server = seg_server;
			int port = seg_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(10000);
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				out.write(s.getBytes());
				out.flush();

				byte[] receiveBuf = new byte[10032 * 8];
				in.read(receiveBuf);

				seg_s = new String(receiveBuf);
				socket.close();
			} catch (Exception e) {
				Thread.sleep(1000);
			}
			return seg_s;

		}

		public String tag(String seg_s) throws Exception {
			String tag_s = "";
			String server = tag_server;
			int port = tag_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(10000);
				seg_s = seg_s + "\n";
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				out.write(seg_s.getBytes());
				out.flush();

				byte[] receiveBuf = new byte[10032];
				in.read(receiveBuf);

				tag_s = new String(receiveBuf);
				socket.close();
			} catch (Exception e) {
				Thread.sleep(1000);
			}
			return tag_s;

		}

		public String clean_one_word(String word) {
			String new_word = word;
			// new_word=new_word.replaceAll("``", "");
			// new_word=new_word.replaceAll("''", "");
			new_word = new_word.replaceAll("&nbsp;", "");
			new_word = new_word.replaceAll("&nbsp", "");
			new_word = new_word.replaceAll("&ldquo;", "");
			new_word = new_word.replaceAll("&ldquo", "");
			new_word = new_word.replaceAll("&rdquo;", "");
			new_word = new_word.replaceAll("&rdquo", "");
			// new_word=new_word.replaceAll(";", "");
			new_word = new_word.replaceAll("&", "");
			new_word = new_word.replaceAll("VS", "");
			new_word = new_word.replaceAll("-RRB-", "");
			new_word = new_word.replaceAll("-LRB-", "");
			new_word = new_word.replaceAll("_", "");
			// new_word=new_word.replaceAll("[\\.]*", "");
			new_word = new_word.replaceAll("\\\\\\#", "");
			/*
			 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word))
			 * { return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+",
			 * new_word))) { return ""; }
			 */
			new_word = new_word.replaceAll("★", "");
			new_word = new_word.replaceAll("__", "");
			new_word = new_word.replaceAll("ˇ", "");
			new_word = new_word.replaceAll("®", "");
			new_word = new_word.replaceAll("♣", "");
			new_word = new_word.replaceAll("\\丨", "");
			new_word = new_word.trim();

			return new_word;
		}

		public String merge_sen(String stanf_seg_text) {
			String m_s = "";
			String[] stanf_seg_arr = stanf_seg_text.split("\\s+");
			String[] one_step_words = new String[stanf_seg_arr.length];
			int one_step_i = 0;

			for (int i = 0; i < one_step_words.length; i++) {
				one_step_words[i] = "";
			}
			String[] temp_stanf_arr = new String[stanf_seg_arr.length];
			int temp_arr_i = 0;
			String temp_arr_key = "";

			for (int i = 0; i < stanf_seg_arr.length; i++) {
				temp_arr_key = stanf_seg_arr[i].trim();
				if (temp_arr_key.length() < 1) {
					continue;
				} else {
					temp_stanf_arr[temp_arr_i++] = temp_arr_key;
				}
			}

			stanf_seg_arr = new String[temp_stanf_arr.length];
			for (int i = 0; i < temp_stanf_arr.length; i++) {
				stanf_seg_arr[i] = temp_stanf_arr[i];
			}

			String temp_words = "";
			for (int i = 0; i < (stanf_seg_arr.length - 1); i++) {
				temp_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1];
				temp_words = temp_words.trim();
				if ((temp_words.length() > 0)
						&& (swa_dict_redis.exists(temp_words))) {
					one_step_words[one_step_i++] = temp_words;
					i++;
				} else {
					if (i < (stanf_seg_arr.length - 2)) {
						one_step_words[one_step_i++] = stanf_seg_arr[i];
					} else if (i == (stanf_seg_arr.length - 2)) {
						one_step_words[one_step_i++] = stanf_seg_arr[i];
						one_step_words[one_step_i++] = stanf_seg_arr[i + 1];
						break;
					}
				}
			}

			String[] two_step_words = new String[one_step_words.length];
			int two_step_i = 0;
			for (int i = 0; i < two_step_words.length; i++) {
				two_step_words[i] = "";
			}

			for (int i = 0; i < (one_step_i); i++) {
				temp_words = one_step_words[i] + one_step_words[i + 1];
				temp_words = temp_words.trim();
				if ((temp_words.length() > 0)
						&& (swa_dict_redis.exists(temp_words))) {
					two_step_words[two_step_i++] = temp_words;
					i++;
				} else {
					if (i < (one_step_i - 2)) {
						two_step_words[two_step_i++] = one_step_words[i];
					} else if (i == (one_step_i - 2)) {
						two_step_words[two_step_i++] = one_step_words[i];
						two_step_words[two_step_i++] = one_step_words[i + 1];
						break;
					}
				}
			}

			String[] three_step_words = new String[two_step_words.length];
			for (int i = 0; i < three_step_words.length; i++) {
				three_step_words[i] = "";
			}
			int three_step_i = 0;
			for (int i = 0; i < (two_step_i); i++) {
				temp_words = two_step_words[i] + two_step_words[i + 1];
				if ((temp_words.length() > 0)
						&& (swa_dict_redis.exists(temp_words))) {
					three_step_words[three_step_i++] = temp_words;
					i++;
				} else {
					if (i < (two_step_words.length - 2)) {
						three_step_words[three_step_i++] = two_step_words[i];
					} else if (i == (two_step_words.length - 2)) {
						three_step_words[three_step_i++] = two_step_words[i];
						three_step_words[three_step_i++] = two_step_words[i + 1];
						break;
					}
				}

			}

			String nword = "";
			for (int i = 0; i < three_step_words.length; i++) {
				temp_words = three_step_words[i];
				if (temp_words == null) {
					continue;
				}
				temp_words = temp_words.trim();
				nword = clean_one_word(temp_words);
				nword = nword.trim();
				if ((nword == null) || nword.equals("null")) {
					continue;
				}
				if (!nword.equals("")) {
					m_s = m_s + nword + " ";
				}
				// System.out.println(i+"  "+nword);
			}

			return m_s;
		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();

			if (it.hasNext()) {
				context.write(key, it.next());
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SWASegDictMR <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "SWASegDictMR_" + day);
		job.setJarByClass(SWASegDictMR.class);
		job.setMapperClass(PrepareMapper.class);
		// job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(0);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		// job.setOutputKeyClass(Text.class);
		// job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
