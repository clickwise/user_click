package cn.clickwise.liqi.nlp.keyword.simple.api;

import java.util.Properties;
import java.util.Vector;
import java.util.regex.Pattern;

import cn.clickwise.liqi.nlp.keyword.basic.KeywordSel;

public class SimpleKeywordSel extends KeywordSel{

	@Override
	public String keywordFromSeg(String seg_s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String keywordFromTag(String tag_s) {
		// TODO Auto-generated method stub
		
		String k_s = "";
		String[] seg_arr = tag_s.split("\\s+");
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

	

	/**
	 * 只取名词
	 * @param tag_s
	 * @return
	 */
	public String keywordFromTagNoun(String tag_s) {
		// TODO Auto-generated method stub
		
		String k_s = "";
		String[] seg_arr = tag_s.split("\\s+");
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

				}

			} else if (seg_arr[i].length() > 5) {
				key_word = seg_arr[i];
				key_word = key_word.replaceAll("/.*", "");
				key_word = key_word.trim();
				new_word_arr.add(key_word);
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
	
	@Override
	public void keywordFromSegFile(String seg_file, String key_file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keywordFromTagFile(String tag_file, String key_file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load_config(Properties prop) {
		// TODO Auto-generated method stub
		
	}

}
