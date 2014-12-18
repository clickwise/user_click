package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

public class EWASWNWord {

	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;

	public void load_local_config() throws Exception {

		InetAddress addr = InetAddress.getLocalHost();
		String ip = addr.getHostAddress().toString();// 获得本机IP
		String address = addr.getHostName().toString();// 获得本机名
		/*
		 * FileInputStream fis = new FileInputStream(config_file); Properties
		 * prop = new Properties(); prop.load(fis); seg_server =
		 * prop.getProperty("seg_server"); seg_port =
		 * Integer.parseInt(prop.getProperty("seg_port")); tag_server =
		 * prop.getProperty("tag_server"); tag_port =
		 * Integer.parseInt(prop.getProperty("tag_port"));
		 * 
		 * swa_dict_ip = prop.getProperty("swa_dict_ip"); swa_dict_port =
		 * Integer.parseInt(prop.getProperty("swa_dict_port")); swa_dict_db =
		 * Integer.parseInt(prop.getProperty("swa_dict_db"));
		 * 
		 * swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);//
		 * redis服务器地址 swa_dict_redis.ping(); swa_dict_redis.select(swa_dict_db);
		 * fis.close();
		 */

		swa_dict_port = 6379;
		swa_dict_db = 6;

		address = address.trim();
		if (address.equals("adt2")) {
			swa_dict_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			swa_dict_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			swa_dict_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			swa_dict_ip = "192.168.110.188";
		}

		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
	}

	public void detect_new_words(String input_file, String output_file)
			throws Exception {
		FileReader fr = new FileReader(new File(input_file));
		BufferedReader br = new BufferedReader(fr);

		FileWriter fw = new FileWriter(output_file);
		PrintWriter pw = new PrintWriter(fw);

		String line = "";
		String[] seg_arr = null;
		int max_len = 0;
		String max_word = "";
		int c = 0;
		int d = 0;
		String word = "";
		String ping_word = "";
		Hashtable word_hash = new Hashtable();
		while ((line = br.readLine()) != null) {
			line = line.trim();
			seg_arr = line.split("\\s+");
			if ((seg_arr == null) || (seg_arr.length < 1)) {
				continue;
			}
			if (seg_arr.length == 1) {
				word = seg_arr[0].trim();
				if ((word != null) && (!(word.equals("")))
						&& (word.length() > 1) && (!isNumbers(word))) {
					if (!(word_hash.containsKey(word))) {
						pw.println(word);
						word_hash.put(word, 1);
					}
				}
				continue;
			}
			/*
			 * if((seg_arr.length)>max_len) { max_len=seg_arr.length;
			 * max_word=line; }
			 */
			if ((isAllEnglish(line)) && ((seg_arr.length) > 1)) {
				ping_word = "";
				for (int i = 0; i < seg_arr.length; i++) {
					word = seg_arr[i].trim();
					if (word != null) {
						ping_word = ping_word + word;
					}
					if ((word != null) && (!(word.equals("")))
							&& (word.length() > 1) && (!isNumbers(word))) {

						if (!(word_hash.containsKey(word))) {
							pw.println(word);
							word_hash.put(word, 1);
						}
					}
				}

				if ((ping_word != null) && (!(ping_word.equals("")))
						&& (ping_word.length() > 1) && (!isNumbers(ping_word))) {

					if (!(word_hash.containsKey(ping_word))) {
						pw.println(ping_word);
						word_hash.put(ping_word, 1);
					}
				}
			} else {
				for (int i = 0; i < seg_arr.length; i++) {
					word = seg_arr[i].trim();
					if ((word != null) && (!(word.equals("")))
							&& (word.length() > 1) && (!isNumbers(word))) {
						if (!(word_hash.containsKey(word))) {
							pw.println(word);
							word_hash.put(word, 1);
						}

					}
				}
			}
		}

		fr.close();
		br.close();
		fw.close();
		pw.close();

	}

	public boolean isAllEnglish(String s) {
		boolean iae = false;
		String pat = "[0-9a-zA-Z\\.\\-\\+\\－\\:\\·\\’\\'\\＆）\\s]*";
		if (Pattern.matches(pat, s)) {
			iae = true;
		}
		return iae;
	}

	public boolean isNumbers(String s) {
		boolean ian = false;
		String pat = "[0-9\\.\\-\\+]*";
		if (Pattern.matches(pat, s)) {
			ian = true;
		}
		return ian;
	}

	public static void main(String[] args) throws Exception {
		EWASWNWord esw = new EWASWNWord();
		String input_file = "input/userWords.txt";
		String output_file = "output/undict_words.txt";
		esw.detect_new_words(input_file, output_file);

	}

}
