package cn.clickwise.clickad.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Pattern;

import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.lib.string.SSO;

/**
 * 输入：分词后的title， 输出 ：tags，names
 * 
 * @author zkyz
 * 
 */
public class KeyExtract {

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
			if (((seg_arr[i].indexOf("#NN")) != -1)
					|| ((seg_arr[i].indexOf("#NR")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("#NN")) != -1) {
					key_word = key_word.replaceAll("#NN", "");
				} else if ((seg_arr[i].indexOf("#NR")) != -1) {
					key_word = key_word.replaceAll("#NR", "");
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
				key_word = key_word.replaceAll("#.*", "");
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)
						&& ((history_word_arr[4].indexOf("#NN")) != -1)
						&& ((history_word_arr[5].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("#NN",
							"").trim();
					history_word_arr[5] = history_word_arr[5].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)
						&& ((history_word_arr[4].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)) {
					// System.out.println("add the:"
					// + (history_word_arr[0] + history_word_arr[1]));
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
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
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		return k_s;
	}

public String keyword_extract_detail(String text) {
		
		String k_s = "";
		String[] seg_arr = text.split("\\s+");
		Vector new_word_arr = new Vector();
		String[] history_word_arr = new String[7];
		for (int i = 0; i < history_word_arr.length; i++) {
			history_word_arr[i] = "";
		}

		ArrayList<String> nouns=new ArrayList<String>();
		String key_word = "";
		String subkey1 = "", subkey2 = "", subkey4 = "", subkey5 = "", subkey6 = "", subkey7 = "", subkey8 = "";

		for (int i = 0; i < seg_arr.length; i++) {
			// System.out.println(i + ":" + seg_arr[i]);
			if (((seg_arr[i].indexOf("#NN")) != -1)
					|| ((seg_arr[i].indexOf("#NR")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("#NN")) != -1) {
					key_word = key_word.replaceAll("#NN", "");
				} else if ((seg_arr[i].indexOf("#NR")) != -1) {
					key_word = key_word.replaceAll("#NR", "");
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
				key_word = key_word.replaceAll("#.*", "");
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)
						&& ((history_word_arr[4].indexOf("#NN")) != -1)
						&& ((history_word_arr[5].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("#NN",
							"").trim();
					history_word_arr[5] = history_word_arr[5].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)
						&& ((history_word_arr[4].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
							"").trim();
					history_word_arr[4] = history_word_arr[4].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)
						&& ((history_word_arr[3].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
							"").trim();
					history_word_arr[3] = history_word_arr[3].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)
						&& ((history_word_arr[2].indexOf("#NN")) != -1)) {
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					history_word_arr[2] = history_word_arr[2].replaceAll("#NN",
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
				if (((history_word_arr[0].indexOf("#NN")) != -1)
						&& ((history_word_arr[1].indexOf("#NN")) != -1)) {
					// System.out.println("add the:"
					// + (history_word_arr[0] + history_word_arr[1]));
					history_word_arr[0] = history_word_arr[0].replaceAll("#NN",
							"").trim();
					history_word_arr[1] = history_word_arr[1].replaceAll("#NN",
							"").trim();
					new_word_arr.add(history_word_arr[0] + history_word_arr[1]);
				}

				history_word_arr[0] = "";
				history_word_arr[1] = "";

			}
		}

		String temp_CC = "";
		ArrayList<String> temp_list=null;
		String temp_CCC="";
		for (int i = 0; i < new_word_arr.size(); i++) {
			temp_CC = new_word_arr.get(i) + "";
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
			/*
			temp_list=SSO.ngram(temp_CC);
			for(int j=0;j<temp_list.size();j++)
			{
				temp_CCC=temp_list.get(j);
				if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_]*", temp_CCC))) {
					k_s = k_s + temp_CCC + " ";
				}
			}
			*/
			
		}
		k_s=k_s.trim();
		return k_s;
	}

	public String keyword_extract_noun(String text) {
		
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
			if (((seg_arr[i].indexOf("#NN")) != -1)
					|| ((seg_arr[i].indexOf("#NR")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("#NN")) != -1) {
					key_word = key_word.replaceAll("#NN", "");
				} else if ((seg_arr[i].indexOf("#NR")) != -1) {
					key_word = key_word.replaceAll("#NR", "");
				}
				key_word = key_word.trim();
				if (key_word.length() > 1) {
					new_word_arr.add(key_word);
				}

			} else if (seg_arr[i].length() > 5) {
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("#.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);
			}
		}

		String temp_CC = "";
		for (int i = 0; i < new_word_arr.size(); i++) {
			temp_CC = new_word_arr.get(i) + "";
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		return k_s;
	}
	
public String keyword_extract_noun_ngram(String text) {
		
		String k_s = "";
		String[] seg_arr = text.split("\\s+");
		Vector new_word_arr = new Vector();
		String[] history_word_arr = new String[7];
		for (int i = 0; i < history_word_arr.length; i++) {
			history_word_arr[i] = "";
		}

		String key_word = "";
		String subkey1 = "", subkey2 = "", subkey4 = "", subkey5 = "", subkey6 = "", subkey7 = "", subkey8 = "";

		String extra_word="";
		for (int i = 0; i < seg_arr.length; i++) {
			// System.out.println(i + ":" + seg_arr[i]);
			if (((seg_arr[i].indexOf("#NN")) != -1)
					|| ((seg_arr[i].indexOf("#NR")) != -1)) {
				key_word = seg_arr[i];
				if ((seg_arr[i].indexOf("#NN")) != -1) {
					key_word = key_word.replaceAll("#NN", "");
				} else if ((seg_arr[i].indexOf("#NR")) != -1) {
					key_word = key_word.replaceAll("#NR", "");
				}
				key_word = key_word.trim();
				if (key_word.length() > 1) {
					new_word_arr.add(key_word);
				}

			} else if (seg_arr[i].length() > 5) {
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("#.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);
			}else if(seg_arr[i].indexOf("女")>-1){
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("#.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);	
				extra_word="女";
			}else if(seg_arr[i].indexOf("男")>-1){
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("#.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);
				extra_word="男";
			}	
		}

		Vector ngram_word_arr=ngramOfWords(new_word_arr);	
		String temp_CC = "";
		for (int i = 0; i < ngram_word_arr.size(); i++) {
			temp_CC = ngram_word_arr.get(i) + "";
			if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_\\-]*", temp_CC))) {
				k_s = k_s + temp_CC + " ";
			}
		}

		k_s=k_s+" "+extra_word;
		k_s=k_s.trim();
		return k_s;
	}

public String keyword_extract_noun_ngram_vv_adj(String text) {
	
	String k_s = "";
	String[] seg_arr = text.split("\\s+");
	Vector new_word_arr = new Vector();
	String[] history_word_arr = new String[7];
	for (int i = 0; i < history_word_arr.length; i++) {
		history_word_arr[i] = "";
	}

	Vector vvadj = new Vector();
	
	String key_word = "";
	String subkey1 = "", subkey2 = "", subkey4 = "", subkey5 = "", subkey6 = "", subkey7 = "", subkey8 = "";

	for (int i = 0; i < seg_arr.length; i++) {
		// System.out.println(i + ":" + seg_arr[i]);
		if (((seg_arr[i].indexOf("#NN")) != -1)
				|| ((seg_arr[i].indexOf("#NR")) != -1)) {
			key_word = seg_arr[i];
			if ((seg_arr[i].indexOf("#NN")) != -1) {
				key_word = key_word.replaceAll("#NN", "");
			} else if ((seg_arr[i].indexOf("#NR")) != -1) {
				key_word = key_word.replaceAll("#NR", "");
			}
			key_word = key_word.trim();
			if (key_word.length() > 1) {
				new_word_arr.add(key_word);
			}

		} else if (seg_arr[i].length() > 5) {
			key_word = seg_arr[i];
			key_word = key_word.replaceAll("#.*", "");
			key_word = key_word.trim();
			new_word_arr.add(key_word);
		} else if(((seg_arr[i].indexOf("#VV")) != -1)||((seg_arr[i].indexOf("#JJ")) != -1))
		{
			key_word = seg_arr[i];
			key_word = key_word.replaceAll("#.*", "");
			key_word = key_word.trim();
	
			vvadj.add(key_word);
		}
	}

	Vector ngram_word_arr=ngramOfWords(new_word_arr);
	
	String temp_CC = "";
	for (int i = 0; i < ngram_word_arr.size(); i++) {
		temp_CC = ngram_word_arr.get(i) + "";
		if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_\\-]*", temp_CC))) {
			k_s = k_s + temp_CC + " ";
		}
	}
	
	for(int i=0;i<vvadj.size();i++)
	{
		temp_CC =vvadj.get(i) + "";
		if (!(Pattern.matches("[a-zA-Z%0-9\\\\\\\\_\\-]*", temp_CC))) {
			k_s = k_s + temp_CC + " ";
		}
	}

	k_s=k_s.trim();
	
	return k_s;
}

    public Vector ngramOfWords(Vector words)
    {
    	Vector nWords=new Vector();
    	String word="";
    	ArrayList list=null;
    	for(int i=0;i<words.size();i++)
    	{
    		word=words.get(i)+"";
    		if(SSO.tioe(word))
    		{
    			continue;
    		}
    		
    		list=SSO.ngram(word);
    		
    		for(int j=0;j<list.size();j++)
    		{
    		  nWords.add(list.get(j));	
    		}
    	}
    	
    	
    	return nWords;
    }
    
	
	public static void main(String[] args) throws Exception {
		// String
		// text="凤凰网 凤凰网是中国领先的综合门户网站，提供含文图音视频的全方位综合新闻资讯、深度访谈、观点评论、财经产品、互动应用、分享社区等服务，同时与凤凰无线、凤凰宽频形成动，为全球主流华人提供互联网、无线通信、电视网三网融合无缝衔接的新媒体优质体验。";
		/*
		 * Segmenter seg=new Segmenter(); seg.loadAnsjDic(new
		 * File("dict/five_dict_uniq.txt"));
		 * 
		 * PrintWriter pw=FileWriterUtil.getPW("temp/seg_test/test_seg.txt");
		 * long start_time=TimeOpera.getCurrentTimeLong();
		 * 
		 * String[] unsegs=FileToArray.fileToDimArr("temp/seg_test/test.txt");
		 * for(int i=0;i<unsegs.length;i++) {
		 * pw.println(seg.segAnsi(unsegs[i])); }
		 * 
		 * long end_time=TimeOpera.getCurrentTimeLong();
		 * 
		 * System.out.println(unsegs.length+" total doc, use time:"+((double)(
		 * end_time-start_time)/(double)1000)+" seconds"); pw.close();
		 */


		if(args.length!=4)
		{
			System.err.println("Usage:<field_num> <key_field_index> <separator>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    key_field_index: 要进行关键词提取的字段编号，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格,tab 表示 \t");
			System.err.println("    option:0 表示只取名词，1 表示取名词和ngram(大于2) ，2 表示detail, 3表示在1的基础上增加动词、形容词，但不取ngram");
			System.exit(1);
		}
		
		//输入的字段个数用
		int fieldNum=0;
		
		//待分词的字段编号
		int keyFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001
		//             :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		fieldNum=Integer.parseInt(args[0]);
		keyFieldIndex=Integer.parseInt(args[1]);
		
		
		if(args[2].equals("001"))
		{
			separator="\001";
			outputSeparator="\001";
		}
		else if(args[2].equals("blank"))
		{
			separator="\\s+";
			outputSeparator="\t";
		}
		else if(args[2].equals("tab"))
		{
			separator="\t";
			outputSeparator="\t";
		}
		else
		{
			separator=args[2].trim();
			outputSeparator=separator.trim();
		}	
		
		int option=Integer.parseInt(args[3]);
		
		KeyExtract ke = new KeyExtract();

		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		//String line = "";
		//while ((line = br.readLine()) != null) {
		//	pw.println(ke.keyword_extract(line));
		//}
		String line="";
		String[] fields=null;
		while((line=br.readLine())!=null)
		{
			fields=line.split(separator);
			if(fields.length!=fieldNum)
			{
				continue;
			}
			for(int j=0;j<keyFieldIndex;j++)
			{
				pw.print(fields[j]+outputSeparator);
			}
			if(keyFieldIndex<(fieldNum-1))
			{
				if(option==0)
				{
		    	  pw.print(ke.keyword_extract_noun(fields[keyFieldIndex]).trim()+outputSeparator);
				}
				else if(option==1)
				{
					 pw.print(ke.keyword_extract_noun_ngram(fields[keyFieldIndex]).trim()+outputSeparator);					
				}
				else if(option==2)
				{
					 pw.print(ke.keyword_extract_detail(fields[keyFieldIndex]).trim()+outputSeparator);					
				}
				else if(option==3)
				{
					 pw.print(ke.keyword_extract_noun_ngram_vv_adj(fields[keyFieldIndex]).trim()+outputSeparator);					
				}
			}
			else
			{
				if(option==0)
				{
				  pw.print(ke.keyword_extract_noun(fields[keyFieldIndex]).trim());
				}
				else if(option==1)
				{
					pw.print(ke.keyword_extract_noun_ngram(fields[keyFieldIndex]).trim());
				}
				else if(option==2)
				{
					pw.print(ke.keyword_extract_detail(fields[keyFieldIndex]).trim());
				}
				else if(option==3)
				{
					pw.print(ke.keyword_extract_noun_ngram_vv_adj(fields[keyFieldIndex]).trim());
				}
			}
			
			for(int j=keyFieldIndex+1;j<fieldNum-1;j++)
			{
				pw.println(fields[j]+outputSeparator);
			}
			
			if(keyFieldIndex<(fieldNum-1))
			{
				//pw.print(ke.keyword_extract_noun(fields[fieldNum-1]));
				pw.print(fields[fieldNum-1]);
			}	
			pw.println();
		}
		
		
		isr.close();
		osw.close();
		br.close();
		pw.close();

	}

}
