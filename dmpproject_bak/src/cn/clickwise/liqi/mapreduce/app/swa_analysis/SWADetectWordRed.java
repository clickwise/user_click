package cn.clickwise.liqi.mapreduce.app.swa_analysis;

import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

/**
 * 判断是否是符合要求的词，并将选出的词存入redis词典
 * 
 * @author lq
 * 
 */

public class SWADetectWordRed {

	public Jedis swa_small_dict_redis;
	public String swa_small_dict_ip;
	public int swa_small_dict_port;
	public int swa_small_dict_db;
	public SWASegDict swa_seg = null;
	public SWATagNoun swa_tag = null;

	public Jedis cw_jedis;
	public String redis_cw_ip = "";
	public int redis_cw_port = 6379;
	public int redis_cw_db = 0;
	public Hashtable<String, Integer> stop_words = null;

	public void load_local_config(Properties prop) {
		swa_small_dict_ip = prop.getProperty("swa_small_dict_ip");
		swa_small_dict_port = Integer.parseInt(prop
				.getProperty("swa_small_dict_port"));
		swa_small_dict_db = Integer.parseInt(prop
				.getProperty("swa_small_dict_db"));

		swa_small_dict_redis = new Jedis(swa_small_dict_ip,
				swa_small_dict_port, 100000);// redis服务器地址
		swa_small_dict_redis.ping();
		swa_small_dict_redis.select(swa_small_dict_db);

		redis_cw_ip = prop.getProperty("redis_cw_ip");
		redis_cw_port = Integer.parseInt(prop.getProperty("redis_cw_port"));
		redis_cw_db = Integer.parseInt(prop.getProperty("redis_cw_db"));

		cw_jedis = new Jedis(redis_cw_ip, redis_cw_port, 100000);// redis服务器地址
		cw_jedis.ping();
		cw_jedis.select(redis_cw_db);

		try {
			swa_seg = new SWASegDict();
			swa_seg.load_local_config(prop);

			swa_tag = new SWATagNoun();
			swa_tag.load_local_config(prop);
		} catch (Exception e) {

		}

	}

	public void load_local_config() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString();// 获得本机IP
			String address = addr.getHostName().toString();// 获得本机名

			swa_small_dict_port = 6379;
			swa_small_dict_db = 2;

			if (address.equals("adt6")) {
				swa_small_dict_ip = "192.168.110.186";
				redis_cw_ip = "192.168.110.186";
			}
			if (address.equals("adt1")) {
				swa_small_dict_ip = "192.168.110.181";
				redis_cw_ip = "192.168.110.182";
			} else if (address.equals("hndx_fx_100")) {
				redis_cw_ip = "192.168.1.100";
				swa_small_dict_ip = "192.168.1.100";
			}

			swa_small_dict_redis = new Jedis(swa_small_dict_ip,
					swa_small_dict_port, 100000);// redis服务器地址
			swa_small_dict_redis.ping();
			swa_small_dict_redis.select(swa_small_dict_db);

			redis_cw_port = 6379;
			redis_cw_db = 10;
			cw_jedis = new Jedis(redis_cw_ip, redis_cw_port, 100000);// redis服务器地址
			cw_jedis.ping();
			cw_jedis.select(redis_cw_db);

			swa_seg = new SWASegDict();
			swa_seg.load_local_config();

			swa_tag = new SWATagNoun();
			swa_tag.load_local_config();
		} catch (Exception e) {
			System.out.println("in swa_detec_word load local config "
					+ e.getMessage());
		}
	}

	public void set_cw_jedis(Jedis temp_jedis) {
		this.cw_jedis = temp_jedis;
	}

	public void setStopWords(Hashtable<String, Integer> temp_stop_words) {
		this.stop_words = temp_stop_words;
	}

	public String one_sw_process(String word, int word_len_limit) {
		String res = "";
		word = word.trim();
		if ((word == null) || (word.equals(""))) {
			return "NA";
		}
		// 如果单词长度小于6，则该搜索词作为一个单词，存入redis词库里
		/*
		 * if(((word.length())<6)&&((word.length())>1)) {
		 * if(!(swa_small_dict_redis.exists(word))) {
		 * swa_small_dict_redis.set(word, "1"); } }
		 */
		String seg_line = "";
		seg_line = swa_seg.sim_line_process(word, word_len_limit);
		// System.out.println("seg_line:"+seg_line);
		String tag_line = "";
		tag_line = swa_tag.simple_line_process(seg_line);
		// System.out.println("tag_line:"+tag_line);
		res = getWord_det_info(tag_line, word);

		return res;
	}

	public String one_direct_process(String word, int word_len_limit) {

		// System.out.println("in one_direct_process");
		String res = "";
		word = word.trim();
		if ((word == null) || (word.equals(""))) {
			return "NA";
		}
		// 如果单词长度小于6，则该搜索词作为一个单词，存入redis词库里
		/*
		 * if(((word.length())<6)&&((word.length())>1)) {
		 * if(!(swa_small_dict_redis.exists(word))) {
		 * swa_small_dict_redis.set(word, "1"); } }
		 */
		String seg_line = "";

		String blank_line = "";
		for (int i = 0; i < word.length(); i++) {
			blank_line = blank_line + word.charAt(i) + " ";
		}
		blank_line = blank_line.trim();
		// System.out.println("blank_line:"+blank_line);
		seg_line = swa_seg.get_direct_seg(blank_line, word_len_limit);
		// System.out.println(word_len_limit+" "+seg_line);
		// System.out.println("seg_line:"+seg_line);
		// String tag_line="";
		// tag_line=swa_tag.simple_line_process(seg_line);
		// System.out.println("tag_line:"+tag_line);
		res = getWord_det_info(seg_line, word);

		return res;
	}

	public String getWord_det_info(String word, String orig_word) {
		String wdi = "";
		String[] ws = null;
		ws = word.split("\\s+");
		String one_word = "";
		Vector wdi_vec = new Vector();

		String csw_info = "";

		for (int i = 0; i < ws.length; i++) {
			one_word = ws[i].trim();
			// System.out.println("one_word:"+one_word);
			csw_info = cw_jedis.get(one_word);
			if ((csw_info != null) && (!(csw_info.equals("")))) {
				// System.out.println("one_wordsqs:"+one_word+"  csw_infosqs:"+csw_info);
				wdi_vec.add(one_word + "\001" + csw_info);
			}
		}

		wdi = get_cate_and_small_words(wdi_vec, orig_word);
		// System.out.println("wdi:"+wdi);

		String[] tt_seg = wdi.split("\001");
		String cate = "";
		String sw_info = "";

		String tsw_small = "";
		String tsw_stop = "";
		if ((tt_seg.length) == 2) {
			cate = tt_seg[0].trim();
			sw_info = tt_seg[1].trim();
			word = "";
			// System.out.println("len2:"+cate+" "+sw_info);
			tsw_small = trim_small_words(word + " " + sw_info);
			tsw_stop = trim_stop_words(tsw_small);
			if (tsw_stop == null) {
				tsw_stop = "";
			}
			wdi = cate + "\001" + tsw_stop;
			// wdi=cate+"\001"+word+" "+sw_info;
		} else if ((tt_seg.length) == 1) {
			cate = tt_seg[0].trim();
			word = "";
			tsw_small = trim_small_words(word);
			tsw_stop = trim_stop_words(tsw_small);
			if (tsw_stop == null) {
				tsw_stop = "";
			}
			wdi = cate + "\001" + tsw_stop;
			// wdi=cate+"\001"+word;
		} else {
			word = "";
			tsw_small = trim_small_words(word);
			tsw_stop = trim_stop_words(tsw_small);
			if (tsw_stop == null) {
				tsw_stop = "";
			}
			wdi = "NA\001" + tsw_stop;
			// wdi="NA\001"+word;
		}
		wdi = wdi.trim();

		return wdi;
	}

	public String get_cate_and_small_words(Vector vec, String word) {
		String temp_csw = "";
		Hashtable<String, Integer> cate_hash = new Hashtable<String, Integer>();
		Hashtable<String, String> cate_words_hash = new Hashtable<String, String>();
		String one_csw = "";
		String cate = "";
		String sw_info = "";

		String[] temp_seg = null;
		int old_count = 0;
		String one_word = "";
		String old_word_str = "";
		for (int i = 0; i < vec.size(); i++) {
			one_csw = vec.get(i) + "";
			temp_seg = one_csw.split("\001");
			if ((temp_seg.length) < 2) {
				continue;
			}
			one_word = temp_seg[0].trim();
			cate = temp_seg[1].trim();
			// System.out.println("vec"+i+"  "+cate);
			// System.out.println("cate in sdwr test1:"+cate
			// +"one_word :"+one_word);
			sw_info = "";
			if ((temp_seg.length) > 1) {
				sw_info = temp_seg[1].trim();
			}
			if (isEnNumbers(one_word)) {
				sw_info = "";
			}
			if (!(cate_hash.containsKey(cate))) {
				cate_hash.put(cate, 1);
			} else {
				old_count = Integer.parseInt(cate_hash.get(cate) + "");
				cate_hash.remove(cate);
				old_count = old_count + 1;
				cate_hash.put(cate, old_count);
			}

			if (!(cate_words_hash.containsKey(cate))) {
				cate_words_hash.put(cate, one_word);
			} else {
				old_word_str = cate_words_hash.get(cate);
				old_word_str = old_word_str + " " + one_word;

				cate_words_hash.remove(cate);
				cate_words_hash.put(cate, old_word_str);
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

			// System.out.println("cate_enum "+cate+" "+temp_num);
			if (temp_num > max_num) {
				max_num = temp_num;
				max_cate = cate;
			}
		}

		String second_maxc = "";
		int second_maxn = 0;
		String tempc = "";
		int tempn = 0;
		Enumeration second_c_enum = cate_hash.keys();
		while (second_c_enum.hasMoreElements()) {
			tempc = second_c_enum.nextElement() + "";
			tempn = cate_hash.get(tempc);
			if ((tempn > second_maxn) && (!(tempc.equals(max_cate)))) {
				second_maxc = tempc;
				second_maxn = tempn;
			}
		}

		String max_cate_str = "";
		String second_cate_str = "";
		if ((max_num == second_maxn) && (max_num > 0)) {
			max_cate_str = cate_hash.get(max_cate) + "";
			second_cate_str = cate_hash.get(second_maxc) + "";
			if ((second_cate_str.length()) > (max_cate_str.length())) {
				max_cate = second_maxc;
			}
		}

		String all_sw_info = "";
		String one_word_temp = "";
		for (int i = 0; i < vec.size(); i++) {
			sw_info = "";
			one_csw = vec.get(i) + "";
			temp_seg = one_csw.split("\001");
			if ((temp_seg.length) < 2) {
				continue;
			}
			cate = temp_seg[1].trim();
			// System.out.println("cate in sdwr test:"+cate
			// +"max_cate :"+max_cate);
			if ((temp_seg.length) > 2) {
				sw_info = temp_seg[2].trim();
			}
			if (cate.equals(max_cate)) {
				all_sw_info = all_sw_info + sw_info + " ";
			}
		}

		all_sw_info = all_sw_info.trim();
		String trim_sw_info = trim_small_words(all_sw_info);
		String trim_stop_info = trim_stop_words(trim_sw_info);
		// System.out.println("trim_sw_info:"+trim_sw_info);
		// System.out.println("trim_stop_info:":+trim_stop_info);

		String cate_word_big_str = cate_words_hash.get(max_cate);
		if (cate_word_big_str != null) {

			// System.out.println("cate_word_big_str:"+cate_word_big_str);
			// System.out.println("max_cate:"+max_cate);
			// System.out.println("trim_stop_info:"+trim_stop_info);
			cate_word_big_str = cate_word_big_str.trim();
			if (cate_word_big_str == null) {
				cate_word_big_str = "";
			}
			temp_csw = max_cate + "\001" + trim_stop_info + " "
					+ cate_word_big_str;
		} else {
			// .out.println("max_cate:"+max_cate);
			// System.out.println("trim_stop_info:"+trim_stop_info);
			temp_csw = max_cate + "\001" + trim_stop_info;
		}
		// System.out.println("temp_csw:"+temp_csw);
		return temp_csw;
	}

	public String trim_small_words(String sw_info) {
		String tsw_info = "";
		String look_four_str = "";
		String look_three_str = "";
		String en_num_str = "";
		sw_info = sw_info.trim();
		if ((sw_info == null) || (sw_info.equals(""))) {
			return "";
		}

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
			if (temp_word.length() == 4) {
				look_four_str = look_four_str + temp_word + "_";
			} else if (temp_word.length() == 3) {
				look_three_str = look_three_str + temp_word + "_";
			}

			if (isEnNumbers(temp_word)) {
				en_num_str = en_num_str + temp_word + "_";
			}
		}

		for (int i = 0; i < seg_arr.length; i++) {
			temp_word = seg_arr[i].trim();
			if ((temp_word.length()) == 2) {
				if (((look_three_str.indexOf(temp_word)) == -1)
						&& ((look_four_str.indexOf(temp_word)) == -1)) {
					sel_word_vec.add(temp_word);
				}
			} else if ((temp_word.length()) == 3) {
				if ((look_four_str.indexOf(temp_word)) == -1) {
					sel_word_vec.add(temp_word);
				}
			} else if (isEnNumbers(temp_word)) {
				if ((en_num_str.indexOf(temp_word)) == -1) {
					sel_word_vec.add(temp_word);
				}
			} else {
				sel_word_vec.add(temp_word);
			}
		}

		for (int i = 0; i < sel_word_vec.size(); i++) {
			temp_word = sel_word_vec.get(i) + "";
			tsw_info = tsw_info + temp_word + " ";
		}
		tsw_info = tsw_info.trim();
		return tsw_info;
	}

	public String trim_stop_words(String sw_info) {
		// System.out.println("det stop_siz:"+stop_words.size());
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

			if ((!(stop_words.containsKey(temp_word)))
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

}
