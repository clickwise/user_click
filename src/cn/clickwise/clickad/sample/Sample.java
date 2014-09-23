package cn.clickwise.clickad.sample;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import love.cq.util.MapCount;

import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.file.uitls.FileToArray;
import cn.clickwise.liqi.file.uitls.FileWriterUtil;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 从标记语料生成标记样本、词典等
 * 
 * @author zkyz
 * 
 */
public class Sample {

	private HashMap<String, Integer> dictMap;

	public void train2sample(File corpus_file, File dict_file, File label_file,
			File train_file, File test_file) {

		try {
			String[] texts = FileToArray.fileToDimArr(corpus_file);
			File temp = new File("temp/keytemp.txt");
			PrintWriter pw = FileWriterUtil.getPWFile(temp);

			String line = "";
			String host = "";
			String label = "";
			String text = "";
			String[] seg_arr = null;

			//Segmenter seg = null;
			//PosTagger posTagger = null;
			//KeyExtract ke = null;
			//seg = new Segmenter();
			//posTagger = new PosTagger("chinese-nodistsim.tagger");
			//ke = new KeyExtract();

			String seg_s = "";
			String tag_s = "";
			String key_s = "";
			dictMap = new HashMap<String, Integer>();
			HashMap<String, Integer> labelMap = new HashMap<String, Integer>();
			int dict_index = 1;
			int label_index = 1;

			String[] tokens = null;
			String word = "";

			for (int i = 0; i < texts.length; i++) {
				line = texts[i];
			    line=line.trim();
				seg_arr = line.split("\001");
				if (seg_arr.length != 3) {
					continue;
				}

				host = seg_arr[0].trim();
				label = seg_arr[1].trim();
				if (!(labelMap.containsKey(label))) {
					labelMap.put(label, label_index++);
				}

				text = seg_arr[2].trim();
				/*
				seg_s = seg.segAnsi(text);
				seg_s = seg_s.trim();
				if (seg_s.equals("")) {
					continue;
				}

				tag_s = posTagger.tag(seg_s);
				tag_s = tag_s.trim();
				if (tag_s.equals("")) {
					continue;
				}

				key_s = "";
				key_s = ke.keyword_extract(tag_s);
				key_s = key_s.trim();
				if (key_s.equals("")) {
					continue;
				}
				*/

				tokens = text.split("\\s+");

				for (int j = 0; j < tokens.length; j++) {
					word = tokens[j].trim();
					if (!(dictMap.containsKey(word))) {
						dictMap.put(word, dict_index++);
					}
				}
				pw.println(host + "\001" + label + "\001" + text);
			}
			pw.close();

			FileWriterUtil.writeHashMap(dictMap, dict_file.getAbsolutePath());
			FileWriterUtil.writeHashMap(labelMap, label_file.getAbsolutePath());
           
			PrintWriter trainpw = FileWriterUtil.getPWFile(train_file);
			PrintWriter testpw = FileWriterUtil.getPWFile(test_file);

			texts = FileToArray.fileToDimArr(temp);
			String sample = "";
			double random = 0;
	

			for (int i = 0; i < texts.length; i++) {
				line = texts[i];
				seg_arr = line.split("\001");
				if (seg_arr.length != 3) {
					continue;
				}

				host = seg_arr[0].trim();
				label = seg_arr[1].trim();
				if (!(labelMap.containsKey(label))) {
					labelMap.put(label, label_index++);
				}

				text = seg_arr[2].trim();
				sample = get_word_id(text);
				sample = sample.trim();
				if (sample.equals("")) {
					continue;
				}
				
				if(!(labelMap.containsKey(label)))
				{
					continue;
				}
				
				label_index=labelMap.get(label);
				if(label_index<1)
				{
					continue;
				}
				
				random = Math.random();				
				if (random > 0.3) {
					trainpw.println(label_index + "\001" + sample);
				} else {
					testpw.println( label_index + "\001" + sample);
				}

			}
			trainpw.close();
			testpw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String get_word_id(String s) {
		String words[] = s.split("[\\s]+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				// //ids = jedis.get(words[i]);
				ids = dictMap.get(words[i]) + "";
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

	public static void main(String[] args) {

		String corpus_file = "";
		String dict_file = "";
		String label_file = "";
		String train_file = "";
		String test_file = "";

		if (args.length != 5) {
			System.err
					.println("Usage: <corpus_file> <dict_file> <label_file> <train_file> <test_file>");
			System.exit(1);
		}

		corpus_file = args[0];
		dict_file = args[1];
		label_file = args[2];
		train_file = args[3];
		test_file = args[4];

		Sample sl = new Sample();
		sl.train2sample(new File(corpus_file), new File(dict_file), new File(
				label_file), new File(train_file), new File(test_file));

	}

}
