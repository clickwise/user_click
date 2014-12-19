package cn.clickwise.clickad.sample;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import love.cq.util.MapCount;
import cn.clickwise.lib.string.SSO;

public class SamplerMulTree {
	/**
	 * 统计单词出现的次数
	 */
	private MapCount<String> dictCounts;

	/**
	 * 统计label出现的次数
	 */
	private MapCount<String> labelCounts;

	/**
	 * 建立每个标记的各层索引，索引是在其父节点各子节点中的编号
	 */
	private HashMap<String, HashMap<String, HashMap<String, String>>> labelDicts;

	/**
	 * 词典索引
	 */
	private HashMap<String, Integer> dicts;

	/**
	 * label索引
	 */
	private HashMap<String, Integer> labels;

	public SamplerMulTree() {
		dictCounts = new MapCount<String>();
		labelCounts = new MapCount<String>();
		labelDicts = new HashMap<String, HashMap<String, HashMap<String, String>>>();
		dicts = new HashMap<String, Integer>();
		labels = new HashMap<String, Integer>();
	}

	public void getDictsAndLabels(int field_num, int sample_field_index,
			int label_field_index, String separator, ArrayList<String> docs) {

		String line = "";
		String[] fields = null;
		String label = "";
		String text = "";
		String[] tokens = null;

		int dict_index = 1;
		int label_index = 1;
		String token = "";

		String[] cates = null;
		String fcate = "";
		String scate = "";
		String tcate = "";

		for (int i = 0; i < docs.size(); i++) {
			line = docs.get(i);
			fields = line.split(separator);
			if (fields.length != field_num) {
				continue;
			}

			label = fields[label_field_index];
			text = fields[sample_field_index];
			if (SSO.tioe(label)) {
				continue;
			}
			label = label.trim();

			labelCounts.add(label);
			if (!(labels.containsKey(label))) {
				labels.put(label, label_index++);
			}

			cates = label.split("\\|");
			if (cates.length != 3) {
				continue;
			}
			fcate = cates[0].trim();
			scate = cates[1].trim();
			tcate = cates[2].trim();

			if (!(labelDicts.containsKey(fcate))) {
				labelDicts.put(fcate,new HashMap<String, HashMap<String, String>>());
			}
			else
			{
			  if (!(labelDicts.get(fcate).containsKey(scate))) {
				labelDicts.get(fcate).put(scate, new HashMap<String, String>());
			  }
			  else
			  {
			    if (!(labelDicts.get(fcate).get(scate).containsKey(tcate))) {
			    	
				   labelDicts.get(fcate).get(scate).put(tcate,(labelDicts.size())+"|"+(labelDicts.get(fcate).size())+ "|"+ (labelDicts.get(fcate).get(scate).size()+ 1));
				   
			    }
			  }
			}
			
			
			if (SSO.tioe(text)) {
				continue;
			}
			text = text.trim();

			tokens = text.split("\\s+");
			for (int j = 0; j < tokens.length; j++) {
				token = tokens[j];
				if (SSO.tioe(token)) {
					continue;
				}
				token = token.trim();
				dictCounts.add(token);
				if (!(dicts.containsKey(token))) {
					dicts.put(token, dict_index++);
				}
			}

		}

	}

	public String get_word_id(String s) {
		String words[] = s.split("\\s+");
		String res = "";
		String ids = "";
		HashMap<Long, Integer> cnts = new HashMap<Long, Integer>();
		for (int i = 0; i < words.length; i++) {
			try {
				// //ids = jedis.get(words[i]);
				ids = dicts.get(words[i]) + "";
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

	

	public void train2mulsample(int field_num, int sample_field_index,
			int label_field_index, String separator, String outputSeparator,
			String gendict, String genlabeldict, String gensample,
			ArrayList<String> docs) {
		
		getDictsAndLabels(field_num, sample_field_index, label_field_index,
				separator, docs);

		String line = "";
		String[] fields = null;
		String label = "";
		String text = "";
		PrintWriter gspw = null;
		try {
			
			gspw = new PrintWriter(new FileWriter(gensample));
			//int labelIndex = 0;
			String forsample = "";

			String labelIndexStr="";
			String[] cates=null;
			String fcate="";
			String scate="";
			String tcate="";
			
			for (int i = 0; i < docs.size(); i++) {
				line = docs.get(i);
				fields = line.split(separator);
				if (fields.length != field_num) {
					continue;
				}

				label = fields[label_field_index];
				text = fields[sample_field_index];
				if (SSO.tioe(label)) {
					continue;
				}
				label = label.trim();
				cates=label.split("\\|");
				if(cates.length!=3)
				{
					continue;
				}
				
				fcate=cates[0].trim();
				scate=cates[1].trim();
				tcate=cates[2].trim();
				labelIndexStr=labelDicts.get(fcate).get(scate).get(tcate)+outputSeparator+labelDicts.size()+"|"+labelDicts.get(fcate).size()+"|"+labelDicts.get(fcate).get(scate).size();
				/*
				labelIndex = 0;
				labelIndex = labels.get(label);
				if (labelIndex < 1) {
					continue;
				}
                */
				
				if (SSO.tioe(text)) {
					continue;
				}
				text = text.trim();
				
				forsample = get_word_id(text);
				if (SSO.tioe(forsample)) {
					continue;
				}
				forsample = forsample.trim();
				gspw.println(labelIndexStr + outputSeparator + forsample);
			}
			gspw.close();
			
			printDicts(gendict, outputSeparator);
			printMulLabelDicts(genlabeldict, outputSeparator);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public void printDicts(String gendict, String outputSeparator) {
		try {
			PrintWriter gdpw = new PrintWriter(new FileWriter(gendict));
			for (Map.Entry<String, Integer> e : dicts.entrySet()) {
				gdpw.println(e.getKey() + outputSeparator + e.getValue());
			}
			gdpw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	public void printMulLabelDicts(String genlabeldict, String outputSeparator) {
		try {
			PrintWriter glpw = new PrintWriter(new FileWriter(genlabeldict));
			for (Map.Entry<String, HashMap<String, HashMap<String, String>>> e1 : labelDicts.entrySet()) {
				for(Map.Entry<String, HashMap<String, String>> e2: e1.getValue().entrySet())
				{
					for(Map.Entry<String, String> e3:e2.getValue().entrySet())
					{
						glpw.println(e3.getValue()+"\001"+labelDicts.size()+"|"+e1.getValue().size()+"|"+e2.getValue().size()+"\001"+e1.getKey()+"|"+e2.getKey()+"|"+e3.getKey());
					}
				}	
			}
		
			glpw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 7) {
			System.err
					.println("Usage:<field_num> <sample_field_index> <label_field_index> <separator> <gendict> <genlabeldict> <gensample>");
			System.err.println("    field_num : 输入的字段个数");
			System.err
					.println("    sample_field_index: 样本体所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    label_field_index: 标记所在的字段，从0开始，即0表示第一个字段");
			System.err
					.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    gendict:生成的词典路径,从1开始");
			System.err.println("    genlabeldict:生成的标记索引路径，从1开始");
			System.err.println("    gensample:生成的样本路径");
			System.exit(1);
		}

		// 输入的字段个数用
		int fieldNum = 0;

		// 样本体所在的字段编号
		int sampleFieldIndex = 0;

		int labelFieldIndex = 0;

		// 字段间的分隔符:001 表示 \001
		// :blank 表示\\s+ 即连续空格
		String separator = "";
		String outputSeparator = "";

		String gendict = "";
		String genlabeldict = "";
		String gensample = "";

		fieldNum = Integer.parseInt(args[0]);
		sampleFieldIndex = Integer.parseInt(args[1]);
		labelFieldIndex = Integer.parseInt(args[2]);

		if (args[3].equals("001")) {
			separator = "\001";
			outputSeparator = "\001";
		} else if (args[3].equals("blank")) {
			separator = "\\s+";
			outputSeparator = "\t";
		} else if (args[3].equals("tab")) {
			separator = "\t";
			outputSeparator = "\t";
		} else {
			separator = args[3].trim();
			outputSeparator = separator.trim();
		}

		gendict = args[4].trim();
		genlabeldict = args[5].trim();
		gensample = args[6].trim();

		SamplerMulTree sampler = new SamplerMulTree();
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		// String line="";
		// while((line=br.readLine())!=null)
		// {
		// pw.println(posTagger.tag(line));
		// }

		String line = "";
		String[] fields = null;
		ArrayList<String> docs = new ArrayList<String>();
		while ((line = br.readLine()) != null) {
			if (SSO.tioe(line)) {
				continue;
			}
			line = line.trim();
			docs.add(line);
		}

		isr.close();
		br.close();

		sampler.train2mulsample(fieldNum, sampleFieldIndex, labelFieldIndex,
				separator, outputSeparator, gendict, genlabeldict, gensample,
				docs);

	}

	public HashMap<String, HashMap<String, HashMap<String, String>>> getLabelDicts() {
		return labelDicts;
	}

	public void setLabelDicts(
			HashMap<String, HashMap<String, HashMap<String, String>>> labelDicts) {
		this.labelDicts = labelDicts;
	}

}
