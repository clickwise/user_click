package cn.clickwise.clickad.classify;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.file.uitls.FileReaderUtil;

public class Classifier {

	/*** model params */
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

	private Segmenter seg = null;
	private PosTagger posTagger = null;
	private KeyExtract ke = null;
	private HashMap video_dict = null;
	private HashMap label_names = null;

	private String dict2jar="";
	public Classifier() {
		try {
			String model_path = "model_host";
			read_model(model_path);
			seg = new Segmenter();
			posTagger = new PosTagger("chinese-nodistsim.tagger");
			ke = new KeyExtract();
			video_dict = FileReaderUtil.getDictFromPlainFile("dict_host.txt");
			label_names = FileReaderUtil.getIndexLabelFromPlainFile("label_host.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
						qid = Integer.parseInt(temp_token.substring(temp_token
								.indexOf(":") + 1), temp_token.length());
					} else if (Pattern.matches("\\d+:[\\d\\.]+", temp_token)) {
						temp_index = Integer.parseInt(temp_token.substring(0,
								temp_token.indexOf(":")));
						temp_weight = Double.parseDouble(temp_token.substring(
								temp_token.indexOf(":") + 1,
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

	public Label classify_struct_example(Word[] sample) {

		Label y = null;
		double score = 0;

		Label best_label = null;
		double best_score = -1;

		Word[] fvec = null;
		for (int i = 0; i < NUM_CLASS; i++) {
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
				score = score + samp_word.weight * line_weights[samp_word.wnum];
			}
		}

		return score;
	}

	public String getSample(String filter_content) throws Exception {
		String sample = "";
		String seg_s = seg.segAnsi(filter_content);
		seg_s = seg_s.trim();
		if (seg_s.equals("")) {
			return "";
		}

		String tag_s = posTagger.tag(seg_s);
		tag_s = tag_s.trim();
		if (tag_s.equals("")) {
			return "";
		}

		String key_s = "";
		key_s = ke.keyword_extract(tag_s);
		key_s = key_s.trim();
		if (key_s.equals("")) {
			return "";
		}

		sample = get_word_id(key_s);
		sample = sample.trim();
		if (sample.equals("")) {
			return "";
		}

		return sample;

	}

	public String get_word_id(String s) {
		String words[] = s.split("[\\s]+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				// //ids = jedis.get(words[i]);
				ids = video_dict.get(words[i]) + "";
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
	
	public String cate(String line)
	{
		String cate_name="";
		try{
		String sample = getSample(line);
		Label label_pre = docate(sample);
		cate_name = getCateName(label_pre);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return cate_name;
	}
	
	public String getCateName(Label y) {
		String cate_name = "";
		int tempid = y.first_class;
		if ((tempid >= 1) && (tempid <= NUM_CLASS)) {
			cate_name = label_names.get(tempid+"")+"";
		} else {
			cate_name = "NA";
		}

		return cate_name;
	}
	
	
	public class Word {
		int wnum;
		double weight;
	}

	public class Label {
		int first_class;
		double score;
	}
	
	public static void main(String[] args) throws Exception {

		Classifier cf = new Classifier();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		OutputStreamWriter osw = new OutputStreamWriter(System.out);
		PrintWriter pw = new PrintWriter(osw);

		String line = "";
		while ((line = br.readLine()) != null) {
			pw.println(cf.cate(line));
		}

		isr.close();
		osw.close();
		br.close();
		pw.close();

	
		

	}

	
}
