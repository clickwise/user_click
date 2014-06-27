package cn.clickwise.segmenter.stanford;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import redis.clients.jedis.Jedis;

import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.Base64Code;

/**
 * 提供stanford crf segmenter 的简单调用接口 输入普通文本，输出分词结果 实例化对象时加载新模型或调用后台server可选
 * 
 * @author lq
 * 
 */
public class StanterSeg extends SegmenterSeg {

	private boolean use_seg_server = false;

	private int seg_port = 8092;

	private String seg_server = "";

	public Jedis swa_dict_redis;
	public String swa_dict_ip;
	public int swa_dict_port;
	public int swa_dict_db;

	/**
	 * 读取配置信息
	 * 
	 * @param prop
	 */
	public void load_config(Properties prop) {
		use_seg_server = Boolean.parseBoolean(prop
				.getProperty("use_seg_server"));
		// System.out.println("use_seg_server:"+use_seg_server);
		if (use_seg_server == true) {
			seg_port = Integer.parseInt(prop.getProperty("seg_port"));
			// System.out.println("seg_port:"+seg_port);
			seg_server = prop.getProperty("seg_server");
			// System.out.println("seg_server:"+seg_server);
		} else {

		}

		swa_dict_ip = prop.getProperty("swa_dict_ip");
		swa_dict_port = Integer.parseInt(prop.getProperty("swa_dict_port"));
		swa_dict_db = Integer.parseInt(prop.getProperty("swa_dict_db"));

		swa_dict_redis = new Jedis(swa_dict_ip, swa_dict_port, 100000);// redis服务器地址
		swa_dict_redis.ping();
		swa_dict_redis.select(swa_dict_db);
	}

	/**
	 * 输入普通文本，输出分词结果
	 * 
	 * @return
	 */
	public String seg() {
		String seg_s = "";

		return seg_s;
	}

	@Override
	public void seg(String plainFile, String seg_file) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String seg(String text) throws Exception {
		// TODO Auto-generated method stub
		if (!(SSO.tnoe(text))) {
			return "";
		}
		String s = text.trim();
		String seg_s = "";
		if (use_seg_server == true) {
			s = s + "\n";

			String server = seg_server;
			int port = seg_port;
			try {
				Socket socket = new Socket(server, port);
				socket.setSoTimeout(100000);
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
		} else {

		}

		return seg_s;
	}

	public String seg_inte(String text) throws Exception {
		String stanf_seg = seg(text);
		// String inte_seg=merge_sen_limit(stanf_seg,4);
		return stanf_seg;
	}

	/**
	 * 启动分词服务
	 */
	public void start_seg_server() {

	}

	public String merge_sen_limit(String stanf_seg_text, int limit) {
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
		for (int i = 0; i < (stanf_seg_arr.length); i++) {
			temp_words = "";
			if (i < (stanf_seg_arr.length - 1)) {
				temp_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1];
				temp_words = temp_words.trim();
			}
			temp_three_words = "";
			if (i < (stanf_seg_arr.length - 2)) {
				temp_three_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2];
				temp_three_words = temp_three_words.trim();
				// System.out.println("temp_three_words:"+temp_three_words);
			}

			temp_four_words = "";
			if (i < (stanf_seg_arr.length - 3)) {
				temp_four_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3];
				temp_four_words = temp_four_words.trim();
				// System.out.println("temp_four_words:"+temp_three_words);
			}

			temp_five_words = "";
			if (i < (stanf_seg_arr.length - 4)) {
				temp_five_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4];
				temp_five_words = temp_five_words.trim();
				// System.out.println("temp_five_words:"+temp_three_words);
			}

			temp_six_words = "";
			if (i < (stanf_seg_arr.length - 5)) {
				temp_six_words = stanf_seg_arr[i] + stanf_seg_arr[i + 1]
						+ stanf_seg_arr[i + 2] + stanf_seg_arr[i + 3]
						+ stanf_seg_arr[i + 4] + stanf_seg_arr[i + 5];
				temp_six_words = temp_six_words.trim();
				// System.out.println("temp_six_words:"+temp_three_words);
			}

			if ((temp_words.length() > 0)
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
				one_step_words[one_step_i++] = temp_words;
				// System.out.println("two_temp_words:"+temp_words);
				i++;
			} else if ((temp_three_words.length() > 0)
					&& (temp_three_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_three_words)))) {
				one_step_words[one_step_i++] = temp_three_words;
				// System.out.println("three_temp_words:"+temp_words);
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (temp_four_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_four_words)))) {
				one_step_words[one_step_i++] = temp_four_words;
				// System.out.println("four_temp_words:"+temp_words);
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (temp_five_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_five_words)))) {
				one_step_words[one_step_i++] = temp_five_words;
				// System.out.println("five_temp_words:"+temp_words);
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (temp_six_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_six_words)))) {
				one_step_words[one_step_i++] = temp_six_words;
				// System.out.println("six_temp_words:"+temp_words);
				i = i + 5;
			} else {
				if (i < (stanf_seg_arr.length - 2)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
					// System.out.println("in else stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
				} else if (i == (stanf_seg_arr.length - 2)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
					// System.out.println("in else if stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
					one_step_words[one_step_i++] = stanf_seg_arr[i + 1];
					// System.out.println("stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i+1]);
					break;
				} else if (i == (stanf_seg_arr.length - 1)) {
					one_step_words[one_step_i++] = stanf_seg_arr[i];
					// System.out.println("stanf seg :"+(one_step_i-1)+"  "+stanf_seg_arr[i]);
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
				two_step_words[two_step_i++] = temp_words;
				i++;
			}

			else if ((temp_three_words.length() > 0)
					&& (temp_three_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_three_words)))) {
				two_step_words[two_step_i++] = temp_three_words;
				// System.out.println("temp_three_words:"+temp_three_words);
				i = i + 2;
			} else if ((temp_four_words.length() > 0)
					&& (temp_four_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_four_words)))) {
				two_step_words[two_step_i++] = temp_four_words;
				// System.out.println("temp_four_words:"+temp_four_words);
				i = i + 3;
			} else if ((temp_five_words.length() > 0)
					&& (temp_five_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_five_words)))) {
				two_step_words[two_step_i++] = temp_five_words;
				// System.out.println("temp_five_words:"+temp_five_words);
				i = i + 4;
			} else if ((temp_six_words.length() > 0)
					&& (temp_six_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_six_words)))) {
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
				} else if (i == (one_step_i - 1)) {
					two_step_words[two_step_i++] = one_step_words[i];
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
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
				} else if (i == (two_step_words.length - 1)) {
					three_step_words[three_step_i++] = two_step_words[i];
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
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
				} else if (i == (three_step_words.length - 1)) {
					four_step_words[four_step_i++] = three_step_words[i];
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists((Base64Code
							.getEncodeStr(temp_words))))) {
				five_step_words[five_step_i++] = temp_words;
				i++;
			} else {
				if (i < (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
				} else if (i == (four_step_words.length - 2)) {
					five_step_words[five_step_i++] = four_step_words[i];
					five_step_words[five_step_i++] = four_step_words[i + 1];
					break;
				} else if (i == (four_step_words.length - 1)) {
					five_step_words[five_step_i++] = four_step_words[i];
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
				six_step_words[six_step_i++] = temp_words;
				i++;
			} else {
				if (i < (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
				} else if (i == (five_step_words.length - 2)) {
					six_step_words[six_step_i++] = five_step_words[i];
					six_step_words[six_step_i++] = five_step_words[i + 1];
					break;
				} else if (i == (five_step_words.length - 1)) {
					six_step_words[six_step_i++] = five_step_words[i];
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
					&& (temp_words.length() < limit)
					&& (swa_dict_redis.exists(Base64Code
							.getEncodeStr(temp_words)))) {
				seven_step_words[seven_step_i++] = temp_words;
				i++;
			} else {
				if (i < (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
				} else if (i == (six_step_words.length - 2)) {
					seven_step_words[seven_step_i++] = six_step_words[i];
					seven_step_words[seven_step_i++] = six_step_words[i + 1];
					break;
				} else if (i == (six_step_words.length - 1)) {
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
			if (!(nword.equals(""))) {
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

	public static void main(String[] args) throws Exception {
		/***
		 * swa_dict_ip = prop.getProperty("swa_dict_ip"); swa_dict_port =
		 * Integer.parseInt(prop.getProperty("swa_dict_port")); swa_dict_db =
		 * Integer.parseInt(prop.getProperty("swa_dict_db"));
		 */

		StanterSeg ss = new StanterSeg();
		Properties prop = new Properties();
		prop.setProperty("seg_server", "192.168.110.186");
		prop.setProperty("seg_port", "8092");
		prop.setProperty("use_seg_server", "true");
		prop.setProperty("swa_dict_ip", "192.168.110.182");
		prop.setProperty("swa_dict_port", "6379");
		prop.setProperty("swa_dict_db", "2");
		ss.load_config(prop);
		String text = "荷兰好声音冠军叫什么 荷兰好声音冠军歌曲";
		System.out.println(ss.seg_inte(text));

	}

}
