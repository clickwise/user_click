package cn.clickwise.clickad.classify_pattern;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 分类程序
 * @author zkyz
 */
public abstract class Classifier {

	/*** model params */
	ModelParams model;

	Segmenter seg = null;
	PosTagger posTagger = null;
	KeyExtract ke = null;
	HashMap video_dict = null;
	HashMap label_names = null;

	static Logger logger = LoggerFactory.getLogger(Classifier.class);
    static int verbosity = 5;

	public Classifier() {
	}

	public abstract ModelParams  read_model(String model_path) throws Exception;
	
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

	public abstract Label classify_struct_example(Word[] sample);

	public abstract Word[] psi(Word[] sample, Label y);
	
	public double classify_example(Word[] fvec) {
		//System.err.println("in classify_example");
		double score = 0;
		Word samp_word = null;

		for (int i = 0; i < fvec.length; i++) {
			samp_word = fvec[i];
			if (samp_word.wnum < model.NUM_FEATURES) {
				score = score + samp_word.weight * model.line_weights[samp_word.wnum];
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
		key_s = ke.keyword_extract_noun_ngram(tag_s);
		key_s = key_s.trim();
		if (key_s.equals("")) {
			return "";
		}
        
		System.out.println("key:"+key_s);	
		
		sample = get_word_id(key_s);
		sample = sample.trim();
		System.out.println("sample:"+sample);	
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
				// System.out.println("ids:"+ids);
			} catch (Exception re) {
				re.printStackTrace();
			}
			if (ids == null) {
				continue;
			}
			if (SSO.tioe(ids)) {
				continue;
			}
			if (!(Pattern.matches("[\\d]*", ids))) {
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

		sample_line=sample_line.trim();
		
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

	public String cate(String line) {
		String cate_name = "";
		try {
			
			String sample = getSample(line);
			Label label_pre = docate(sample);
			cate_name = getCateName(label_pre);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cate_name;
	}

	public String getCateName(Label y) {
		String cate_name = "";
		int tempid = y.first_class;
		if ((tempid >= 1) && (tempid <= model.NUM_CLASS)) {
			cate_name = label_names.get(tempid + "") + "";
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
		//multiclass
		int class_index;
		
		//multi-level
		int first_class;
		int second_class;
		int third_class;
		double score;
	}
	
	public Word copy_word(Word w)
	{
		Word nw=new Word();
		nw.weight=w.weight;
		nw.wnum=w.wnum;
		return nw;
	}

	public HashMap getDictFromStream(String input_file) {////名称在前 数字在后
		// TODO Auto-generated method stub

		HashMap hm = new HashMap();
		String item = "";
		String word = "";
		String index_str = "";
		int index = 0;
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + input_file);
		InputStreamReader model_isr = new InputStreamReader(model_is);

		BufferedReader br = new BufferedReader(model_isr);
		String[] seg_arr = null;

		try {
			while ((item = br.readLine()) != null) {

				if (!(SSO.tnoe(item))) {
					continue;
				}
				item = item.trim();
				seg_arr = item.split("\\s+");
				if (seg_arr.length != 2) {
					continue;
				}
				word = seg_arr[0].trim();
				index_str = seg_arr[1].trim();

				if (!(SSO.tnoe(word))) {
					continue;
				}

				if (!(SSO.tnoe(index_str))) {
					continue;
				}
				index = Integer.parseInt(index_str);

				if (index < 1) {
					continue;
				}
				hm.put(word, index);
			}

			br.close();
			model_is.close();
			model_isr.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}

	public abstract HashMap getIndexLabelFromStream(String input_file);

	
}
