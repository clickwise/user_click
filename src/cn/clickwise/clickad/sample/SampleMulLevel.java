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

public class SampleMulLevel {
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
	private HashMap<Integer, HashMap<String, Integer>> labelDicts;
	
	private HashMap<String,String> posslabels;

	/**
	 * 词典索引
	 */
	private HashMap<String, Integer> dicts;

	/**
	 * label索引
	 */
	private HashMap<String, Integer> labels;
	

	public SampleMulLevel() {
		dictCounts = new MapCount<String>();
		labelCounts = new MapCount<String>();
		labelDicts = new HashMap<Integer, HashMap<String, Integer>>();
		posslabels=new HashMap<String,String>();
		dicts = new HashMap<String, Integer>();
		labels = new HashMap<String, Integer>();
	}

	public void getDictsAndLabels(int field_num, int sample_field_index,
			int label_field_index,int level_num, String separator, ArrayList<String> docs) {

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
		
		for(int i=1;i<=level_num;i++)
		{
			labelDicts.put(i, new HashMap<String,Integer>());
		}

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
			if (cates.length != level_num) {
				continue;
			}
			if(level_num>0)
			{
			  fcate = cates[0].trim();
			}
			
			if(level_num>1)
			{
			  scate = fcate+"_"+cates[1].trim();
			}
			
			if(level_num>2)
			{
			  tcate = scate+"_"+cates[2].trim();
			}
			
			if(level_num>0)
			{
              if(!(labelDicts.get(1).containsKey(fcate)))
              {
            	labelDicts.get(1).put(fcate, labelDicts.get(1).size()+1);
              }
			}
			
			if(level_num>1)
			{
              if(!(labelDicts.get(2).containsKey(scate)))
              {
            	labelDicts.get(2).put(scate, labelDicts.get(2).size()+1);
              }
			}
			
			if(level_num>2)
			{
              if(!(labelDicts.get(3).containsKey(tcate)))
              {
            	labelDicts.get(3).put(tcate, labelDicts.get(3).size()+1);
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

    /**
     * 多层label,最多三层
     * 格式 l1[|l2[|l3]]<seprator>seg text
     * @param field_num
     * @param sample_field_index
     * @param label_field_index
     * @param level_num
     * @param separator
     * @param outputSeparator
     * @param gendict
     * @param genlabeldict
     * @param gensample
     * @param docs
     */
	public void train2mulsample(int field_num, int sample_field_index,
			int label_field_index,int level_num, String separator, String outputSeparator,
			String gendict, String genlabeldict, String gensample,
			ArrayList<String> docs) {
		
		getDictsAndLabels(field_num, sample_field_index, label_field_index,level_num,separator, docs);

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
				if(cates.length!=level_num)
				{
					continue;
				}
				
				if(level_num>0)
				{
				fcate = cates[0].trim();
				label=fcate;
				}
				if(level_num>1)
				{
				 scate = fcate+"_"+cates[1].trim();
				 label=fcate+"|"+scate;
				}
				if(level_num>2)
				{
				 tcate = scate+"_"+cates[2].trim();
				 label=fcate+"|"+scate+"|"+tcate;
				}
				
				if(level_num>0)
				{
				  labelIndexStr=labelDicts.get(1).get(fcate)+"";
				}
				if(level_num>1)
				{
				  labelIndexStr=labelDicts.get(1).get(fcate)+"|"+labelDicts.get(2).get(scate);
				}
				if(level_num>2)
				{
				  labelIndexStr=labelDicts.get(1).get(fcate)+"|"+labelDicts.get(2).get(scate)+"|"+labelDicts.get(3).get(tcate);
				}
				
				if(!(posslabels.containsKey(labelIndexStr)))
				{
				    posslabels.put(labelIndexStr, label);
				}
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
			
            for(Map.Entry<String, String> e:posslabels.entrySet())
            {
            	glpw.println(e.getKey()+outputSeparator+e.getValue());
            }
		
			glpw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 8) {
			System.err.println("Usage:<field_num> <sample_field_index> <label_field_index> <level_num>  <separator> <gendict> <genlabeldict> <gensample>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    sample_field_index: 样本体所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    label_field_index: 标记所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    level_num: 标记的层数");			
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
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
		
		int levelNum=0;

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
		levelNum=Integer.parseInt(args[3]);
		
		if (args[4].equals("001")) {
			separator = "\001";
			outputSeparator = "\001";
		} else if (args[4].equals("blank")) {
			separator = "\\s+";
			outputSeparator = "\t";
		} else if (args[4].equals("tab")) {
			separator = "\t";
			outputSeparator = "\t";
		} else {
			separator = args[4].trim();
			outputSeparator = separator.trim();
		}

		gendict = args[5].trim();
		genlabeldict = args[6].trim();
		gensample = args[7].trim();

		SampleMulLevel sampler = new SampleMulLevel();
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

		sampler.train2mulsample(fieldNum, sampleFieldIndex, labelFieldIndex,levelNum,
				separator, outputSeparator, gendict, genlabeldict, gensample,
				docs);

	}

	public HashMap<Integer, HashMap<String, Integer>> getLabelDicts() {
		return labelDicts;
	}

	public void setLabelDicts(HashMap<Integer, HashMap<String, Integer>> labelDicts) {
		this.labelDicts = labelDicts;
	}
}
