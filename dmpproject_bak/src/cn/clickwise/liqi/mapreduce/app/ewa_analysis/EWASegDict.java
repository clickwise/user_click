package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import redis.clients.jedis.Jedis;

public class EWASegDict {

	public String seg_server = "";
	public int seg_port = 0;
	public String tag_server = "";
	public int tag_port = 0;
	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;

	public void load_config(String config_file) throws Exception {
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
		String temp_three_words = "";
		String temp_four_words = "";
		String temp_five_words = "";
		String temp_six_words = "";
		for (int i = 0; i < (stanf_seg_arr.length - 1); i++) {
			temp_words = "";
			temp_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1];
			temp_words = temp_words.trim();

			temp_three_words = "";
			if (i < (stanf_seg_arr.length - 2)) {
				temp_three_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2];
				temp_three_words = temp_three_words.trim();
			}

			temp_four_words = "";
			if (i < (stanf_seg_arr.length - 3)) {
				temp_four_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3];
				temp_four_words = temp_four_words.trim();
			}

			temp_five_words = "";
			if (i < (stanf_seg_arr.length - 4)) {
				temp_five_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4];
				temp_five_words = temp_five_words.trim();
			}

			temp_six_words = "";
			if (i < (stanf_seg_arr.length - 5)) {
				temp_six_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4] + stanf_seg_arr[i + 5];
				temp_six_words = temp_six_words.trim();
			}

			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				one_step_words[one_step_i++] = temp_words;
				i++;
			} else if ((temp_three_words.length() > 0)
					&& (swa_dict_redis.exists(temp_three_words))) {
				one_step_words[one_step_i++] = temp_three_words;
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (swa_dict_redis.exists(temp_four_words))) {
				one_step_words[one_step_i++] = temp_four_words;
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (swa_dict_redis.exists(temp_five_words))) {
				one_step_words[one_step_i++] = temp_five_words;
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (swa_dict_redis.exists(temp_six_words))) {
				one_step_words[one_step_i++] = temp_six_words;
				i = i + 5;
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

		// for (int i = 0; i < one_step_words.length; i++) {
		// System.out.println(i + " " + one_step_words[i]);
		// }
		// System.out.println("==============");
		String[] two_step_words = new String[one_step_words.length];
		int two_step_i = 0;
		for (int i = 0; i < two_step_words.length; i++) {
			two_step_words[i] = "";
		}

		// System.out.println("one_step_i:"+one_step_i);
		for (int i = 0; i < (one_step_i); i++) {
			temp_words = "";
			temp_words = one_step_words[i] + one_step_words[i + 1];
			temp_words = temp_words.trim();

			temp_three_words = "";
			if (i < (one_step_i - 2)) {
				temp_three_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2];
				temp_three_words = temp_three_words.trim();
				// System.out.println("temp_three_words:"+temp_three_words);
			}

			temp_four_words = "";
			if (i < (one_step_i - 3)) {
				temp_four_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3];
				temp_four_words = temp_four_words.trim();
				// System.out.println("temp_four_words:"+temp_four_words);
			}

			temp_five_words = "";
			if (i < (one_step_i - 4)) {
				temp_five_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3]
						+ one_step_words[i + 4];
				temp_five_words = temp_five_words.trim();
				// System.out.println("temp_five_words:"+temp_five_words);
			}

			temp_six_words = "";
			if (i < (one_step_i - 5)) {
				temp_six_words = one_step_words[i] + one_step_words[i + 1]
						+ one_step_words[i + 2] + one_step_words[i + 3]
						+ one_step_words[i + 4] + one_step_words[i + 5];
				temp_six_words = temp_six_words.trim();
				// System.out.println("temp_six_words:"+temp_six_words);
			}

			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				two_step_words[two_step_i++] = temp_words;
				i++;
			}

			else if ((temp_three_words.length() > 0)
					&& (swa_dict_redis.exists(temp_three_words))) {
				two_step_words[two_step_i++] = temp_three_words;
				// System.out.println("temp_three_words:"+temp_three_words);
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (swa_dict_redis.exists(temp_four_words))) {
				two_step_words[two_step_i++] = temp_four_words;
				// System.out.println("temp_four_words:"+temp_four_words);
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (swa_dict_redis.exists(temp_five_words))) {
				two_step_words[two_step_i++] = temp_five_words;
				// System.out.println("temp_five_words:"+temp_five_words);
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (swa_dict_redis.exists(temp_six_words))) {
				two_step_words[two_step_i++] = temp_six_words;
				// System.out.println("temp_six_words:"+temp_six_words);
				i = i + 5;
			}

			else {
				if (i < (one_step_i - 2)) {
					two_step_words[two_step_i++] = one_step_words[i];
				} else if (i == (one_step_i - 2)) {
					two_step_words[two_step_i++] = one_step_words[i];
					two_step_words[two_step_i++] = one_step_words[i + 1];
					break;
				}
			}
		}

		// for (int i = 0; i < two_step_words.length; i++) {
		// System.out.println(i + " " + two_step_words[i]);
		// }
		// System.out.println("==============");
		String[] three_step_words = new String[two_step_words.length];
		for (int i = 0; i < three_step_words.length; i++) {
			three_step_words[i] = "";
		}
		int three_step_i = 0;
		for (int i = 0; i < (two_step_i); i++) {
			temp_words = "";
			temp_words = two_step_words[i] + two_step_words[i + 1];
			/*
			 * temp_three_words=""; if (i < (two_step_i - 2)) { temp_three_words
			 * = two_step_words[i] + two_step_words[i + 1] + two_step_words[i +
			 * 2]; temp_three_words = temp_three_words.trim(); }
			 * 
			 * temp_four_words=""; if (i < (two_step_i - 3)) { temp_four_words =
			 * two_step_words[i] + two_step_words[i + 1] + two_step_words[i + 2]
			 * + two_step_words[i + 3]; temp_four_words =
			 * temp_four_words.trim(); }
			 * 
			 * temp_five_words=""; if (i < (two_step_i - 4)) { temp_five_words =
			 * two_step_words[i] + two_step_words[i + 1] + two_step_words[i + 2]
			 * + two_step_words[i + 3] + two_step_words[i + 4]; temp_five_words
			 * = temp_five_words.trim(); }
			 * 
			 * temp_six_words=""; if (i < (two_step_i - 5)) { temp_six_words =
			 * two_step_words[i] + two_step_words[i + 1] + two_step_words[i + 2]
			 * + two_step_words[i + 3] + two_step_words[i + 4] +
			 * two_step_words[i + 5]; temp_six_words = temp_six_words.trim(); }
			 */
			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				three_step_words[three_step_i++] = temp_words;
				i++;
			}
			/*
			 * else if ((temp_three_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_three_words))) {
			 * three_step_words[three_step_i++] = temp_three_words; i = i + 2; }
			 * else if ((temp_four_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_four_words))) {
			 * three_step_words[three_step_i++] = temp_four_words; i = i + 3; }
			 * else if ((temp_five_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_five_words))) {
			 * three_step_words[three_step_i++] = temp_five_words; i = i + 4; }
			 * else if ((temp_six_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_six_words))) {
			 * three_step_words[three_step_i++] = temp_six_words; i = i + 5; }
			 */
			else {
				if (i < (two_step_words.length - 2)) {
					three_step_words[three_step_i++] = two_step_words[i];
				} else if (i == (two_step_words.length - 2)) {
					three_step_words[three_step_i++] = two_step_words[i];
					three_step_words[three_step_i++] = two_step_words[i + 1];
					break;
				}
			}

		}

		String[] four_step_words = new String[three_step_words.length];
		for (int i = 0; i < four_step_words.length; i++) {
			four_step_words[i] = "";
		}
		int four_step_i = 0;
		for (int i = 0; i < (three_step_i); i++) {
			temp_words = "";
			temp_words = three_step_words[i] + three_step_words[i + 1];
			/*
			 * if (i < (three_step_i - 2)) { temp_three_words =
			 * three_step_words[i] + three_step_words[i + 1] +
			 * three_step_words[i + 2]; temp_three_words =
			 * temp_three_words.trim(); } if (i < (three_step_i - 3)) {
			 * temp_four_words = three_step_words[i] + three_step_words[i + 1] +
			 * three_step_words[i + 2] + three_step_words[i + 3];
			 * temp_four_words = temp_four_words.trim(); } if (i < (three_step_i
			 * - 4)) { temp_five_words = three_step_words[i] +
			 * three_step_words[i + 1] + three_step_words[i + 2] +
			 * three_step_words[i + 3] + three_step_words[i + 4];
			 * temp_five_words = temp_five_words.trim(); } if (i < (three_step_i
			 * - 5)) { temp_six_words = three_step_words[i] + three_step_words[i
			 * + 1] + three_step_words[i + 2] + three_step_words[i + 3] +
			 * three_step_words[i + 4] + three_step_words[i + 5]; temp_six_words
			 * = temp_six_words.trim(); }
			 */

			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				four_step_words[four_step_i++] = temp_words;
				i++;
			}
			/*
			 * else if ((temp_three_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_three_words))) {
			 * four_step_words[four_step_i++] = temp_three_words; i=i+2; } else
			 * if ((temp_four_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_four_words))) {
			 * four_step_words[four_step_i++] = temp_four_words; i=i+3; } else
			 * if ((temp_five_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_five_words))) {
			 * four_step_words[four_step_i++] = temp_five_words; i=i+4; } else
			 * if ((temp_six_words.length() > 0) &&
			 * (swa_dict_redis.exists(temp_six_words))) {
			 * four_step_words[four_step_i++] = temp_six_words; i=i+5; }
			 */
			else {
				if (i < (three_step_words.length - 2)) {
					four_step_words[four_step_i++] = three_step_words[i];
				} else if (i == (three_step_words.length - 2)) {
					four_step_words[four_step_i++] = three_step_words[i];
					four_step_words[four_step_i++] = three_step_words[i + 1];
					break;
				}
			}

		}

		String[] five_step_words = new String[four_step_words.length];
		for (int i = 0; i < five_step_words.length; i++) {
			five_step_words[i] = "";
		}
		int five_step_i = 0;
		for (int i = 0; i < (four_step_i); i++) {
			temp_words = "";
			temp_words = four_step_words[i] + four_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				five_step_words[five_step_i++] = temp_words;
				i++;
			} else {
				if (i < (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
				} else if (i == (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
					five_step_words[five_step_i++] = four_step_words[i + 1];
					break;
				}
			}

		}

		String[] six_step_words = new String[five_step_words.length];
		for (int i = 0; i < six_step_words.length; i++) {
			six_step_words[i] = "";
		}
		int six_step_i = 0;
		for (int i = 0; i < (five_step_i); i++) {
			temp_words = "";
			temp_words = five_step_words[i] + five_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				six_step_words[six_step_i++] = temp_words;
				i++;
			} else {
				if (i < (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
				} else if (i == (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
					six_step_words[six_step_i++] = five_step_words[i + 1];
					break;
				}
			}
		}

		String[] seven_step_words = new String[six_step_words.length];
		for (int i = 0; i < seven_step_words.length; i++) {
			seven_step_words[i] = "";
		}
		int seven_step_i = 0;
		for (int i = 0; i < (six_step_i); i++) {
			temp_words = "";
			temp_words = six_step_words[i] + six_step_words[i + 1];
			if ((temp_words.length() > 0)
					&& (swa_dict_redis.exists(temp_words))) {
				seven_step_words[seven_step_i++] = temp_words;
				i++;
			} else {
				if (i < (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
				} else if (i == (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
					seven_step_words[seven_step_i++] = six_step_words[i + 1];
					break;
				}
			}
		}

		String nword = "";
		for (int i = 0; i < seven_step_words.length; i++) {
			temp_words = seven_step_words[i];
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
			// System.out.println(i + "  " + nword);
		}

		return m_s;
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

	public String line_process(String line) {

		String new_line = "";
		String[] seg_arr = line.split("\t");

		String url = "";
		String cate = "";
		String title = "";

		String title_ips = "";

		String[] temp_seg = null;
		if (seg_arr != null && seg_arr.length > 2) {
			url = seg_arr[0].trim();
			cate = seg_arr[1].trim();
		   if(cate==null)
		   {
			   cate="NA";
		   }
			title = seg_arr[2].trim();
			if ((title != null) && (!title.equals("")) && (title.length() > 5)) {
				String seg_s = "";
				String men_s = "";
				try {

					// String local_config_file =
					// "/home/hadoop/lq/class_medlda_justed/swa_local_config.propertie
					seg_s = seg(title);
					seg_s = seg_s.trim();

					/*
					 * if(seg_s.equals("")) { String global_config_file=
					 * "/home/hadoop/lq/class_medlda_justed/swa_global_config.properties"
					 * ; load_global_config(global_config_file);
					 * seg_s=seg(title); }
					 */

					men_s = merge_sen(seg_s);
					men_s = men_s.trim();

					if ((men_s != null) && (!men_s.equals(""))) {
						if ((url != null)
								&& !(url.equals("")))
							new_line = url + "\001" + cate +"\001"+title+ "\001" + men_s;
						    new_line = new_line.trim();
					}
				} catch (Exception e) {

				}

			}

		}

		return new_line;
	}

	
	
	public String simple_line_process(String line) {

		String new_line = "";

		String title = "";
        title=line.trim();

			if ((title != null) && (!title.equals("")) && (title.length() > 1)) {
				String seg_s = "";
				String men_s = "";
				try {

					// String local_config_file =
					// "/home/hadoop/lq/class_medlda_justed/swa_local_config.propertie
					seg_s = seg(title);
					seg_s = seg_s.trim();

					/*
					 * if(seg_s.equals("")) { String global_config_file=
					 * "/home/hadoop/lq/class_medlda_justed/swa_global_config.properties"
					 * ; load_global_config(global_config_file);
					 * seg_s=seg(title); }
					 */

					men_s = merge_sen(seg_s);
					men_s = men_s.trim();
					if ((men_s != null) && (!men_s.equals(""))) {
							new_line = men_s;
						    new_line = new_line.trim();
					}
				} catch (Exception e) {

				}
			}

		return new_line;
	}
	
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

		seg_port = 8092;
		swa_dict_port = 6379;
		swa_dict_db = 6;//电商搜索词
		//swa_dict_db = 2;
		address = address.trim();
		address="adt2";
		if (address.equals("adt2")) {
			seg_server = "192.168.110.182";
			swa_dict_ip = "192.168.110.182";
		} else if (address.equals("adt1")) {
			seg_server = "192.168.110.181";
			swa_dict_ip = "192.168.110.181";
		} else if (address.equals("adt6")) {
			seg_server = "192.168.110.186";
			swa_dict_ip = "192.168.110.186";
		} else if (address.equals("adt8")) {
			seg_server = "192.168.110.188";
			swa_dict_ip = "192.168.110.188";
		}else if (address.equals("adt0")) {
			seg_server = "192.168.110.180";
			swa_dict_ip = "192.168.110.180";
		}else if (address.equals("hndx_fx_202")) {
			seg_server = "192.168.210.202";
			swa_dict_ip = "192.168.210.202";
		}
		else if (address.equals("hndx_fx_100")) {
			seg_server = "192.168.1.100";
			swa_dict_ip = "192.168.1.100";
		}
		

		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
	}
	
	public void load_config(Properties prop)
	{
		seg_port = Integer.parseInt(prop.getProperty("seg_port"));
		swa_dict_port = Integer.parseInt(prop.getProperty("swa_dict_port"));
		swa_dict_db =Integer.parseInt(prop.getProperty("swa_dict_db"));//电商搜索词
		seg_server=prop.getProperty("seg_server");
		swa_dict_ip=prop.getProperty("swa_dict_ip");
				
		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
	}

	public static void main(String[] args) throws Exception {

		EWASegDict ewa = new EWASegDict();
		ewa.load_local_config();

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String line = "";

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String new_line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if ((line == null) || (line.equals(""))) {
				continue;
			}

			new_line = ewa.line_process(line);

			if ((new_line == null) || (new_line.equals(""))) {
				continue;
			}
			pw.println(new_line);

		}

		br.close();
		isr.close();
		osw.close();
		pw.close();

		/*
		 * SWASegDict swa = new SWASegDict(); String config_file =
		 * "dict/swa_config.properties";
		 * 
		 * swa.load_config(config_file);
		 * 
		 * InetAddress addr = InetAddress.getLocalHost(); String ip =
		 * addr.getHostAddress().toString();// 获得本机IP String address =
		 * addr.getHostName().toString();// 获得本机名
		 * 
		 * String s1 =
		 * "正在播放《抹布女也有春天》第38集 - 国产电视剧 - 抹布女也有春天免费在线观看 - 闪播电影-最新电视剧,2013最新电影,2013快播电影,好看的电视剧免费在线观看 "
		 * ; // // String s1="朗文英语教学实践(第4版)/杰里米·哈默(Harmer.J)-图书-亚马逊中国"; // //
		 * String s1="龙王令：妃卿莫属-406：孩子没爹-摘书网提供的小说免费阅读"; String seg_s = ""; seg_s
		 * = swa.seg(s1); String men_s = ""; men_s = swa.merge_sen(seg_s);
		 * 
		 * // String tag_s=""; // tag_s=swa.tag(men_s);
		 * System.out.println("seg_s:" + seg_s); System.out.println("men_s:" +
		 * men_s); // System.out.println("tag_s:"+tag_s);
		 */
	}
}
