package cn.clickwise.baidu_hot;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

public class CorpusToDictSampleDoubleCate {

	public String seg_server = "";
	public int seg_port = 0;
	public String tag_server = "";
	public int tag_port = 0;

	//public String labels_file = "";
	public String first_labels_file = "";
	public String second_labels_file = "";
	public Jedis jedis_dict;
	public String jedis_dict_ip = "";
	public int jedis_dict_port = 6379;
	public int jedis_dict_db = 0;
	public String dict_file = "";

	public Jedis jedis_dict_idf;
	public String jedis_dict_idf_ip = "";
	public int jedis_dict_idf_port = 6379;
	public int jedis_dict_idf_db = 0;

	public Hashtable<String, Integer> dict = null;
	//public Hashtable<String, Integer> label_hash = null;
	public Hashtable<String, Integer> first_label_hash = null;
	public Hashtable<String, Integer> second_label_hash = null;
	public Hashtable<String, Integer> dict_idf = null;

	public int dict_index = 0;

	public CorpusToDictSampleDoubleCate(String first_labels_file,String second_labels_file) throws Exception {
		init_config(first_labels_file,second_labels_file);
	}

	public String seg(String s) throws Exception {
		// s = s + "\n";
	  
		s = s.trim();
		s = s + "\n";
		String seg_s = "";
		String server = seg_server;
		int port = seg_port;
		 try{
		Socket socket = new Socket(server, port);

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		out.write(s.getBytes());
		out.flush();

		byte[] receiveBuf = new byte[10032 * 8];
		in.read(receiveBuf);

		seg_s = new String(receiveBuf);
		socket.close();
	   }
	   catch(Exception e)
	   {
		   Thread.sleep(1000);
	   }
		
		return seg_s;
	}

	public String tag(String seg_s) throws Exception {
		String tag_s = "";
		String server = tag_server;
		int port = tag_port;
		
		try{
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
		}
		catch(Exception e)
		{
			
		}
		
		return tag_s;

	}

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
					history_word_arr[0] = history_word_arr[0].replaceAll("/NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("/NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("/NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("/NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("/NN",
							"").trim();
					history_word_arr[5] = history_word_arr[5].replaceAll("/NN",
							"").trim();
					new_word_arr.add(history_word_arr[0] + history_word_arr[1]
							+ history_word_arr[2] + history_word_arr[3]
							+ history_word_arr[4] + history_word_arr[5]);
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
					history_word_arr[0] = history_word_arr[0].replaceAll("/NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("/NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("/NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("/NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("/NN",
							"").trim();

					new_word_arr.add(history_word_arr[0] + history_word_arr[1]
							+ history_word_arr[2] + history_word_arr[3]
							+ history_word_arr[4]);
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
					history_word_arr[0] = history_word_arr[0].replaceAll("/NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("/NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("/NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("/NN",
							"").trim();
					new_word_arr.add(history_word_arr[0] + history_word_arr[1]
							+ history_word_arr[2] + history_word_arr[3]);
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
					history_word_arr[0] = history_word_arr[0].replaceAll("/NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("/NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("/NN",
							"").trim();
					new_word_arr.add(history_word_arr[0] + history_word_arr[1]
							+ history_word_arr[2]);
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
					history_word_arr[0] = history_word_arr[0].replaceAll("/NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("/NN",
							"").trim();
					new_word_arr.add(history_word_arr[0] + history_word_arr[1]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";

			}
		}

		String temp_CC = "";
		for (int i = 0; i < new_word_arr.size(); i++) {
			temp_CC = new_word_arr.get(i) + "";
			temp_CC = temp_CC.trim();
			temp_CC = clean_one_word(temp_CC);
			temp_CC = temp_CC.trim();
			if ((!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_\\#]*", temp_CC)))
					&& (!temp_CC.equals(""))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		return k_s;
	}

	public String getKeyWordSample(String filter_content) throws Exception {
		String sample = "";
		String seg_s = seg(filter_content);
		seg_s = seg_s.trim();
		if (seg_s.equals("")) {
			return "";
		}

		String tag_s = tag(seg_s);
		tag_s = tag_s.trim();
		if (tag_s.equals("")) {
			return "";
		}

		String key_s = "";
		key_s = keyword_extract(tag_s);
		key_s = key_s.trim();
		if (key_s.equals("")) {
			return "";
		}

		return key_s;

	}

	public void corpusToDS(String corpus_file, String sample_file,
			String dict_file, String idf_file) throws Exception {

		FileReader fr = new FileReader(new File(corpus_file));
		BufferedReader br = new BufferedReader(fr);

		FileWriter sample_fw = new FileWriter(new File(sample_file));
		PrintWriter sample_pw = new PrintWriter(sample_fw);

		String line = "";
		String[] seg_arr = null;
		String keywords = "";
		String cate_str = "";
		String keyword_sample = "";
		String word = "";
		String[] word_seg_arr = null;
		int old_idf = 0;

		Hashtable one_line_hash = null;
		while ((line = br.readLine()) != null) {
			// System.out.println("line:"+line);
			seg_arr = line.split("\001");
			if (seg_arr.length != 2) {
				continue;
			}

			keywords = seg_arr[1].trim();
			cate_str = seg_arr[0].trim();
			if (keywords.length() < 10) {
				continue;
			}
			keyword_sample = getKeyWordSample(keywords);
			keyword_sample = keyword_sample.trim();

			if (keyword_sample.equals("")) {
				continue;
			}
            /*
			if (!(cate.equals("男") || cate.equals("女") || cate.equals("噪音"))) {
				continue;
			}
            */
			sample_pw.println(cate_str + "\001" + keyword_sample);
			word_seg_arr = keyword_sample.split("\\s+");
			one_line_hash = new Hashtable();
			for (int j = 0; j < word_seg_arr.length; j++) {
				word = word_seg_arr[j].trim();
				if (word.equals("")) {
					continue;
				}

				if (!dict.containsKey(word)) {
					dict_index++;
					dict.put(word, dict_index);
				}

				if (!one_line_hash.containsKey(word)) {
					one_line_hash.put(word, word);
					if (!dict_idf.containsKey(word)) {
						dict_idf.put(word, 1);
					} else {
						old_idf = dict_idf.get(word);
						dict_idf.remove(word);
						dict_idf.put(word, (old_idf + 1));
					}
				}

			}

			// System.out.println(keyword_sample+" "+cate);
		}

		Enumeration enum_dict = dict.keys();
		String enum_key = "";
		int enum_val = 0;

		FileWriter fw = new FileWriter(new File(dict_file));
		PrintWriter pw = new PrintWriter(fw);

		while (enum_dict.hasMoreElements()) {
			enum_key = enum_dict.nextElement() + "";
			enum_val = dict.get(enum_key);
			if (!jedis_dict.exists(enum_key)) {
				jedis_dict.set(enum_key, enum_val + "");
			}
			pw.println(enum_key + " " + enum_val);
		}

		pw.close();
		fw.close();

		FileWriter idf_fw = new FileWriter(new File(idf_file));
		PrintWriter idf_pw = new PrintWriter(idf_fw);

		Enumeration enum_idf_dict = dict_idf.keys();
		String enum_idf_key = "";
		int enum_idf_val = 0;

		while (enum_idf_dict.hasMoreElements()) {
			enum_idf_key = enum_idf_dict.nextElement() + "";
			enum_idf_val = dict_idf.get(enum_idf_key);
			if (!jedis_dict_idf.exists(enum_idf_key)) {
				if ((jedis_dict.get(enum_idf_key)) != null) {
					jedis_dict_idf.set(jedis_dict.get(enum_idf_key),
							enum_idf_val + "");
				}
			}
			idf_pw.println(enum_idf_key + " " + enum_idf_val);
		}

		idf_fw.close();
		idf_pw.close();

		br.close();
		fr.close();

		sample_pw.close();
		sample_fw.close();

	}

	public void corpusTSS(String corpus_file, String sample_file)
			throws Exception {
		FileReader fr = new FileReader(new File(corpus_file));
		BufferedReader br = new BufferedReader(fr);

		FileWriter sample_fw = new FileWriter(new File(sample_file));
		PrintWriter sample_pw = new PrintWriter(sample_fw);

		String line = "";
		String[] seg_arr = null;
		String keywords = "";
		String cate_str = "";
		String keyword_sample = "";
		String word = "";
		String[] word_seg_arr = null;

		String stand_sample = "";
		int first_cate_id = 0;
		int second_cate_id=0;
        String[] cate_seg=null;
        String first_cate="";
        String second_cate="";
        
		while ((line = br.readLine()) != null) {
			// System.out.println("line:"+line);
			seg_arr = line.split("\001");
			if (seg_arr.length != 2) {
				continue;
			}

			keywords = seg_arr[1].trim();
			cate_str = seg_arr[0].trim();
			if (keywords.length() < 10) {
				continue;
			}
			keyword_sample = getKeyWordSample(keywords);
			keyword_sample = keyword_sample.trim();

			if (keyword_sample.equals("")) {
				continue;
			}

			//if (!(cate.equals("男") || cate.equals("女") || cate.equals("噪音"))) {
			//	continue;
			//}
			if(cate_str.equals(""))
			{
				continue;
			}
			cate_seg=cate_str.split("@");
			if(cate_seg.length!=2)
			{
				continue;
			}
			first_cate=cate_seg[0].trim();
			second_cate=cate_seg[1].trim();
			if((first_cate.equals(""))||(second_cate.equals("")))
			{
				continue;
			}
			if((!first_label_hash.containsKey(first_cate))||(!second_label_hash.containsKey(second_cate)))
			{
				continue;
			}
			first_cate_id = first_label_hash.get(first_cate);
			second_cate_id = second_label_hash.get(second_cate);
			// sample_pw.println(keyword_sample);
			stand_sample = get_word_id(keyword_sample);
			sample_pw.println(first_cate_id + " " +second_cate_id+" "+ stand_sample);
		}

		br.close();
		fr.close();

		sample_pw.close();
		sample_fw.close();
	}
/*
	public void corpusTSSIDF(String corpus_file, String sample_file)
			throws Exception {
		FileReader fr = new FileReader(new File(corpus_file));
		BufferedReader br = new BufferedReader(fr);

		FileWriter sample_fw = new FileWriter(new File(sample_file));
		PrintWriter sample_pw = new PrintWriter(sample_fw);

		String line = "";
		String[] seg_arr = null;
		String keywords = "";
		String cate = "";
		String keyword_sample = "";
		String word = "";
		String[] word_seg_arr = null;

		String stand_sample = "";
		int cate_id = 0;

		while ((line = br.readLine()) != null) {
			// System.out.println("line:"+line);
			seg_arr = line.split("\001");
			if (seg_arr.length != 2) {
				continue;
			}

			keywords = seg_arr[0].trim();
			cate = seg_arr[1].trim();
			if (keywords.length() < 10) {
				continue;
			}
			keyword_sample = getKeyWordSample(keywords);
			keyword_sample = keyword_sample.trim();

			if (keyword_sample.equals("")) {
				continue;
			}

			if (!(cate.equals("男") || cate.equals("女") || cate.equals("噪音"))) {
				continue;
			}
			cate_id = label_hash.get(cate);
			// sample_pw.println(keyword_sample);
			stand_sample = get_word_id_idf(keyword_sample);
			sample_pw.println(cate_id + " " + stand_sample);
		}

		br.close();
		fr.close();

		sample_pw.close();
		sample_fw.close();
	}
*/
	public String get_word_id(String s) {
		String words[] = s.split("[\\s]+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				ids = jedis_dict.get(words[i]);
			} catch (Exception re) {

			}
			if (ids == null) {
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

	public String get_word_id_idf(String s) {
		String words[] = s.split("[\\s]+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				ids = jedis_dict.get(words[i]);
			} catch (Exception re) {

			}
			if (ids == null) {
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

		double idf = 0;
		double tf = 0;
		int tf_idf = 0;
		for (int i = 0; i < keys.size(); i++) {
			Long l = keys.get(i);
			idf = Double.parseDouble(jedis_dict_idf.get(l + ""));
			tf = cnts.get(l);
			if (idf > 0) {
				tf_idf = (int) ((tf / idf) * 100);
				if (tf_idf != 0) {
					if (i == 0)
						res += l + ":" + tf_idf;
					else
						res += " " + l + ":" + tf_idf;
				}
			}
		}
		return res;
	}

	public void init_config(String first_labels_file,String second_labels_file) throws Exception {
		seg_server = "192.168.110.186";
		seg_port = 8092;
		tag_server = "192.168.110.186";
		tag_port = 8093;

		dict = new Hashtable<String, Integer>();
		dict_idf = new Hashtable<String, Integer>();
		dict_index = 0;
		this.first_labels_file = first_labels_file;
		this.second_labels_file = second_labels_file;

		FileReader first_label_fr = new FileReader(new File(this.first_labels_file));
		BufferedReader first_label_br = new BufferedReader(first_label_fr);
		String line = "";
		String[] seg_arr = null;
		String cate_name = "";
		int cate_index = 0;
		first_label_hash = new Hashtable<String, Integer>();
		while ((line = first_label_br.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			seg_arr = line.split("\\s+");
			if (seg_arr.length != 2) {
				continue;
			}
			cate_name = seg_arr[0].trim();
			cate_index = Integer.parseInt(seg_arr[1].trim());

			if (!first_label_hash.containsKey(cate_name)) {
				// System.out.println(cate_name+" "+cate_index);
				first_label_hash.put(cate_name, cate_index);
			}
		}
		
		FileReader second_label_fr = new FileReader(new File(this.second_labels_file));
		BufferedReader second_label_br = new BufferedReader(second_label_fr);
		second_label_hash=new  Hashtable<String, Integer>();
		while ((line = second_label_br.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			seg_arr = line.split("\\s+");
			if (seg_arr.length != 2) {
				continue;
			}
			cate_name = seg_arr[0].trim();
			cate_index = Integer.parseInt(seg_arr[1].trim());

			if (!second_label_hash.containsKey(cate_name)) {
				// System.out.println(cate_name+" "+cate_index);
				second_label_hash.put(cate_name, cate_index);
			}
		}
		
		
		jedis_dict_ip = "192.168.110.186";
		jedis_dict_port = 6379;
		jedis_dict_db = 3;

		jedis_dict = new Jedis(jedis_dict_ip, jedis_dict_port, 100000);// redis服务器地址
		jedis_dict.ping();
		jedis_dict.select(jedis_dict_db);

		jedis_dict_idf_ip = "192.168.110.186";
		jedis_dict_idf_port = 6379;
		jedis_dict_idf_db = 4;

		jedis_dict_idf = new Jedis(jedis_dict_idf_ip, jedis_dict_idf_port);
		jedis_dict_idf.ping();
		jedis_dict_idf.select(jedis_dict_idf_db);

		/**
		 * 将词典加载到redis中，调用此方法之前要生成词典
		 */
		/*
		 * setDictFile("dict/gender_dict.txt"); loadDictFileToRedis();
		 */
	}

	public String clean_one_word(String word) {
		String new_word = word;
		new_word = new_word.replaceAll("``", "");
		new_word = new_word.replaceAll("''", "");
		new_word = new_word.replaceAll("&nbsp;", "");
		new_word = new_word.replaceAll("&nbsp", "");
		new_word = new_word.replaceAll("&ldquo;", "");
		new_word = new_word.replaceAll("&ldquo", "");
		new_word = new_word.replaceAll("&rdquo;", "");
		new_word = new_word.replaceAll("&rdquo", "");
		new_word = new_word.replaceAll(";", "");
		new_word = new_word.replaceAll("&", "");
		new_word = new_word.replaceAll("VS", "");
		new_word = new_word.replaceAll("-RRB-", "");
		new_word = new_word.replaceAll("-LRB-", "");
		new_word = new_word.replaceAll("_", "");
		new_word = new_word.replaceAll("[\\.]*", "");
		new_word = new_word.replaceAll("\\\\\\#", "");
		new_word = new_word.replaceAll("\\\\/", "");
		/*
		 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word)) {
		 * return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+", new_word)))
		 * { return ""; }
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

	public void setDictFile(String dict_file) {
		this.dict_file = dict_file;
	}

	public void loadDictFileToRedis() throws Exception {
		FileReader fr = new FileReader(new File(this.dict_file));
		BufferedReader br = new BufferedReader(fr);
		String line = "";
		String[] seg_arr = null;
		String word = "";
		String index = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}

			seg_arr = line.split("\\s+");
			if (seg_arr.length != 2) {
				continue;
			}

			word = seg_arr[0].trim();
			index = seg_arr[1].trim();
			if (!jedis_dict.exists(word)) {
				// System.out.println("word: "+word+"   index:"+index);
				jedis_dict.set(word, index);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		String first_label_file = "dict/first_nart_names.txt";
		String second_label_file="dict/second_nart_names.txt";
		
		
		CorpusToDictSampleDoubleCate ctds = new CorpusToDictSampleDoubleCate(first_label_file,second_label_file);

		String corpus_file = "input/user_sample_ot_nart.txt";
		// String corpus_file="input/gender_test.txt";

		String sample_file = "output/title_nntrain.txt";
		String dict_file = "output/title_dict.txt";
		String idf_file = "output/title_idf.txt";

		/**
		 * 从训练文本生成标准样本,调用此方法之前需要先调用corpusToDS来建立词典
		 */
		// ctds.corpusTSS(corpus_file, sample_file);
		/*****
		 * 从训练文本建立词典
		 */
		 //ctds.corpusToDS(corpus_file, sample_file, dict_file, idf_file);
		ctds.corpusTSS(corpus_file, sample_file);
		//ctds.corpusTSSIDF(corpus_file, sample_file);

	}

}
