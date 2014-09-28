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

import org.jmlp.classify.svm_struct.source.LABEL;
import org.jmlp.classify.svm_struct.source.svm_struct_api_factory;
import org.jmlp.classify.svm_struct.source.svm_struct_classify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.clickwise.clickad.classify.Classifier.Label;
import cn.clickwise.clickad.classify.Classifier.Word;
import cn.clickwise.clickad.keyword.KeyExtract;
import cn.clickwise.clickad.seg.Segmenter;
import cn.clickwise.clickad.tag.PosTagger;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 对普通文本进行分类
 * 
 * @author zkyz
 * 
 */

public class ClassifierUseText {

	private Segmenter seg = null;
	private PosTagger posTagger = null;
	private KeyExtract ke = null;
	private HashMap video_dict = null;
	private HashMap label_names = null;

	static Logger logger = LoggerFactory.getLogger(Classifier.class);
	private static int verbosity = 5;

	public ClassifierUseText() {
		try {
			//seg = new Segmenter();
			//posTagger = new PosTagger("chinese-nodistsim.tagger");
			//ke = new KeyExtract();
			video_dict = getDictFromStream("dict_host.txt");
			label_names = getIndexLabelFromStream("label_host.txt");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSample(String filter_content) throws Exception {

		String sample = "";

		/*
		 * String seg_s = seg.segAnsi(filter_content); seg_s = seg_s.trim(); if
		 * (seg_s.equals("")) { return ""; }
		 * 
		 * String tag_s = posTagger.tag(seg_s); tag_s = tag_s.trim(); if
		 * (tag_s.equals("")) { return ""; }
		 * 
		 * String key_s = ""; key_s = ke.keyword_extract(tag_s); key_s =
		 * key_s.trim(); if (key_s.equals("")) { return ""; }
		 */

		sample = get_word_id(filter_content);
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
    /*
	public String cate(String line) {
		String cate_name = "";
		try {

			String sample = getSample(line);
			// Label label_pre = docate(sample);
			Label label_pre = null;
			cate_name = getCateName(label_pre);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return cate_name;
	}
    */
	public String getCateName(LABEL y) {
		String cate_name = "";
		int tempid = y.class_index;

		// if ((tempid >= 1) && (tempid <= NUM_CLASS)) {
		if ((tempid >= 1)) {
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


	public HashMap getDictFromStream(String input_file) {
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

	public HashMap getIndexLabelFromStream(String input_file) {
		// TODO Auto-generated method stub

		HashMap hm = new HashMap();
		String item = "";
		String label = "";
		String index_str = "";
		int index = 0;
		// FileReader fr=null;
		// BufferedReader br=null;
		InputStream model_is = this.getClass().getResourceAsStream(
				"/" + input_file);
		InputStreamReader model_isr = new InputStreamReader(model_is);

		BufferedReader br = new BufferedReader(model_isr);

		String[] seg_arr = null;

		try {
			// fr=new FileReader(new File(input_file));
			// br=new BufferedReader(fr);
			while ((item = br.readLine()) != null) {
				if (!(SSO.tnoe(item))) {
					continue;
				}
				seg_arr = item.split("\\s+");
				if (seg_arr.length != 2) {
					continue;
				}
				label = seg_arr[0].trim();
				index_str = seg_arr[1].trim();

				if (!(SSO.tnoe(index_str))) {
					continue;
				}

				if (!(SSO.tnoe(label))) {
					continue;
				}
				index = Integer.parseInt(index_str);

				if (index < 1) {
					continue;
				}
				hm.put(index_str, label);
			}

			br.close();
			model_is.close();
			model_isr.close();
		} catch (Exception e) {

		}
		return hm;
	}

	public static void main(String[] args) throws Exception {


		if (args.length < 3||args.length>5) {
			System.err.println("Usage:ClassifierUseText [<api_type>] [<model>] <field_num> <seg_field_index> <separator>");
			System.err.println("    api_type: svm struct api type for example:multiclass(0)");
			System.err.println("    model: model save path");
			System.err.println("    dict : 外加词典路径");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    seg_field_index: 要分词的字段编号，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格,tab 表示\t");
			System.exit(1);
		}
		
        //分类的体系，一层分类、二层分类等,0表示一层分类，1表示细化的两层分类
		int api_type=0;
		
		//模型文件
		String model="";
		
		//输入的字段个数用
		int fieldNum=0;
		
		//待分词的字段编号
		int segFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001
		//             :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		
		if(args.length==3)
		{
			fieldNum=Integer.parseInt(args[0]);
			segFieldIndex=Integer.parseInt(args[1]);
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
		}
		else if(args.length==4)
		{
		    if(Pattern.matches("[\\d]+", args[0]))
		    {
		    	api_type=Integer.parseInt(args[0]);
		    }
		    else
		    {
		    	model=args[0];
		    }
		    
			fieldNum=Integer.parseInt(args[1]);
			segFieldIndex=Integer.parseInt(args[2]);
			if(args[3].equals("001"))
			{
				separator="\001";
				outputSeparator="\001";
			}
			else if(args[3].equals("blank"))
			{
				separator="\\s+";
				outputSeparator="\t";
			}
			else if(args[3].equals("tab"))
			{
				separator="\t";
				outputSeparator="\t";
			}
			else
			{
				separator=args[3];
				outputSeparator=separator.trim();
			}
		}
		else if(args.length==5)
		{
			api_type=Integer.parseInt(args[0]);
			model=args[1];
			fieldNum=Integer.parseInt(args[2]);
			segFieldIndex=Integer.parseInt(args[3]);
			if(args[4].equals("001"))
			{
				separator="\001";
				outputSeparator="\001";
			}
			else if(args[4].equals("blank"))
			{
				separator="\\s+";
				outputSeparator="\t";
			}
			else if(args[4].equals("tab"))
			{
				separator="\t";
				outputSeparator="\t";
			}
			else
			{
				separator=args[4];
				outputSeparator=separator.trim();
			}
		}
		
		svm_struct_classify ssc = new svm_struct_classify();
		
		if (args.length == 3) {
			String default_model_file = "model_host";
			// ssc.classify_from_arraylist(sample_list,default_model_file);
			ssc.init_svm_struct(default_model_file);
		} else if (args.length == 4) {// default: multiclass

			// 选用何种分类体系
			svm_struct_api_factory ssaf = new svm_struct_api_factory(api_type);
			ssc.init_svm_struct(model);
			
			
			// ssc.classify_from_arraylist(sample_list,args[0]);

		} else if (args.length == 5) {

			// 选用何种分类体系
			svm_struct_api_factory ssaf = new svm_struct_api_factory(api_type);
			ssc.init_svm_struct(model);
			// ssc.classify_from_arraylist(sample_list,args[1]);
		}
		
		ClassifierUseText cut = new ClassifierUseText();



		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		
		OutputStreamWriter osw=new OutputStreamWriter(System.out);
		PrintWriter pw=new PrintWriter(osw);
		
		String line="";
		String[] fields=null;
		while((line=br.readLine())!=null)
		{
			fields=line.split(separator);
			if(fields.length!=fieldNum)
			{
				continue;
			}
			for(int j=0;j<fields.length;j++)
			{
				pw.print(fields[j]+outputSeparator);
			}
			pw.print(cut.getCateName(ssc.classifyWordString(cut.get_word_id(fields[segFieldIndex]))).trim());
			pw.println();
		}
		
		isr.close();
		osw.close();
		br.close();
		pw.close();

	}

}
