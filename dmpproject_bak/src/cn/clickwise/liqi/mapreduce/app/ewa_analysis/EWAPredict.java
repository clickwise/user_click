package cn.clickwise.liqi.mapreduce.app.ewa_analysis;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;


public class EWAPredict {

	public double[] line_weights;
	public String Version;
	public int NUM_FIRST_CLASS;
	public int NUM_SECOND_CLASS;
	public int NUM_THIRD_CLASS;
	public int NUM_WORDS;
	public int loss_function;
	public int kernel_type;
	public int para_d;
	public int para_g;
	public int para_s;
	public int para_r;
	public String para_u;
	public int NUM_FEATURES;
	public int train_num;
	public int suv_num;
	public double b;
	public double alpha;
	public int qid;
	public Label[] sample_label_set;
	public Hashtable first_level_hash;
	public Hashtable second_level_hash;
	public Hashtable third_level_hash;
	
	public EWASegDict ewa_seg_dict=null;
	public EWATagNoun ewa_tag_noun=null;
	public EWASampleGen ewa_sam_gen=null;
	
	public String cate_server="";
	public int cate_port=0;
	public Hashtable<String,String> cate_words_hash=null;
	
	public class SortElement implements Comparable{
		
		public String key;
		public double val;
		public String cate;
		public int docid;
		
		public SortElement(String key,double val,String cate,int docid)
		{
		   this.key=key;
		   this.val=val;
		   this.cate=cate;
		   this.docid=docid;
		}
		public int compareTo(Object o) {
			SortElement s = (SortElement)o;
			return val < s.val ? 1 : (val == s.val ? 0 : -1);
		};
		
		public String toString(){
		    return  "key = " + this.key+ ",cate = " + this.cate+",val = " + this.val +",docid = " + this.docid ;
		}
		
	}
	public class Word {
		int wnum;
		double weight;
	}

	public class Label {
		int first_class;
		int second_class;
		int third_class;
		double score;
	}

	public void read_model(String model_path, String sls_path,
			String first_level_path, String second_level_path,
			String third_level_path) throws Exception {
		File model_file = new File(model_path);
		FileReader fr = new FileReader(model_file);
		BufferedReader br = new BufferedReader(fr);
		Version = cut_comment(br.readLine());
		NUM_FIRST_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		System.out.println("NUM_FIRST_CLASS:" + NUM_FIRST_CLASS);
		NUM_SECOND_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		System.out.println("NUM_SECOND_CLASS:" + NUM_SECOND_CLASS);
		NUM_THIRD_CLASS = Integer.parseInt(cut_comment(br.readLine()));
		System.out.println("NUM_THIRD_CLASS:" + NUM_THIRD_CLASS);
		NUM_WORDS = Integer.parseInt(cut_comment(br.readLine()));
		System.out.println("NUM_WORDS:" + NUM_WORDS);
		loss_function = Integer.parseInt(cut_comment(br.readLine()));
		kernel_type = Integer.parseInt(cut_comment(br.readLine()));
		para_d = Integer.parseInt(cut_comment(br.readLine()));
		para_g = Integer.parseInt(cut_comment(br.readLine()));
		para_s = Integer.parseInt(cut_comment(br.readLine()));
		para_r = Integer.parseInt(cut_comment(br.readLine()));
		para_u = cut_comment(br.readLine());
		NUM_FEATURES = Integer.parseInt(cut_comment(br.readLine()));
		System.out.println("NUM_FEATURES:" + NUM_FEATURES);
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
					} else if (Pattern.matches("\\d+:[\\-]?[\\d\\.]*", temp_token)) {
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
		fr.close();
		br.close();
		// System.out.println("max_index:" + max_index);
		readSampleLabels(sls_path);
		read_level_names(first_level_path, second_level_path, third_level_path);
		//printW(line_weights);
	}

	public void read_level_names(String first_level_path,
			String second_level_path, String third_level_path) throws Exception {
		first_level_hash = new Hashtable();
		second_level_hash = new Hashtable();
		third_level_hash = new Hashtable();

		FileReader fr_first = new FileReader(new File(first_level_path));
		BufferedReader br_first = new BufferedReader(fr_first);

		String line_first = "";
		String[] pair_first = null;
		String name_first = "";
		String index_first = "";
		while ((line_first = br_first.readLine()) != null) {
			line_first = line_first.trim();
			pair_first = line_first.split("\\s+");
			if ((pair_first.length) != 2) {
				continue;
			}
			name_first = pair_first[0];
			index_first = pair_first[1];
			if (!(first_level_hash.containsKey(index_first))) {
				first_level_hash.put(index_first, name_first);
			}
		}
		br_first.close();
		fr_first.close();

		FileReader fr_second = new FileReader(new File(second_level_path));
		BufferedReader br_second = new BufferedReader(fr_second);

		String line_second = "";
		String[] pair_second = null;
		String name_second = "";
		String index_second = "";
		while ((line_second = br_second.readLine()) != null) {
			line_second = line_second.trim();
			pair_second = line_second.split("\\s+");
			if ((pair_second.length) != 2) {
				continue;
			}
			name_second = pair_second[0];
			index_second = pair_second[1];
			if (!(second_level_hash.containsKey(index_second))) {
				second_level_hash.put(index_second, name_second);
			}
		}
		br_second.close();
		fr_second.close();

		FileReader fr_third = new FileReader(new File(third_level_path));
		BufferedReader br_third = new BufferedReader(fr_third);

		String line_third = "";
		String[] pair_third = null;
		String name_third = "";
		String index_third = "";
		
		while ((line_third = br_third.readLine()) != null) {
			line_third = line_third.trim();
			//System.out.println("line_third:"+line_third);
			pair_third = line_third.split("\\s+");
			if ((pair_third.length) != 2) {
				continue;
			}
			name_third = pair_third[0];
			index_third = pair_third[1];
			if (!(third_level_hash.containsKey(index_third))) {
				third_level_hash.put(index_third, name_third);
			}
		}
		br_third.close();
		fr_third.close();

	}

	public Word[] psi(Word[] sample, Label y) {
		Word[] fvec = null;
		int veclength = (sample.length) * (NUM_FIRST_CLASS + NUM_SECOND_CLASS+NUM_THIRD_CLASS);
		fvec = new Word[veclength];
		for (int i = 0; i < veclength; i++) {
			fvec[i] = new Word();
		}

		int c1 = y.first_class;
		int c2 = y.second_class;
		int c3 = y.third_class;

		Word temp_word = null;
		int fi = 0;
		// System.out.println();
		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			// System.out.print(temp_word.wnum+":"+temp_word.weight+" ");
		}
		// System.out.println();
		// System.out.println("y.first_class:"+y.first_class+" "+y.second_class);

		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			fvec[fi].wnum = temp_word.wnum + (c1 - 1) * NUM_WORDS;
			fvec[fi].weight = temp_word.weight;
			// System.out.print(fvec[fi].wnum+":"+fvec[fi].weight+" ");
			fi++;

		}

		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			fvec[fi].wnum = temp_word.wnum + NUM_FIRST_CLASS * NUM_WORDS
					+ (c2 - 1) * NUM_WORDS;
			fvec[fi].weight = temp_word.weight;
			// System.out.print(fvec[fi].wnum+":"+fvec[fi].weight+" ");
			fi++;
		}

		for (int i = 0; i < sample.length; i++) {
			temp_word = sample[i];
			fvec[fi].wnum = temp_word.wnum + NUM_FIRST_CLASS * NUM_WORDS
					+ NUM_SECOND_CLASS * NUM_WORDS + (c3 - 1) * NUM_WORDS;
			fvec[fi].weight = temp_word.weight;
			// System.out.print(fvec[fi].wnum+":"+fvec[fi].weight+" ");
			fi++;
		}

		// System.out.println();
		return fvec;
	}

	public Label classify_struct_example(Word[] sample) {

		Label y = null;
		double score = 0;

		Label best_label = null;
		double best_score = -1;

		Word[] fvec = null;
		for (int i = 0; i < sample_label_set.length; i++) {
			y = sample_label_set[i];
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

	public double classify_example(Word[] fvec) {
		double score = 0;
		//BigDecimal b1=null;
		//BigDecimal b2=null;
		//BigDecimal m1=null;
		//BigDecimal t1=null;
		//BigDecimal s1=null;
		//s1=new BigDecimal(0.000000);
		Word samp_word = null;

		for (int i = 0; i < fvec.length; i++) {
			samp_word = fvec[i];
			if ((samp_word.wnum < (NUM_FEATURES))) {
				//b1=new BigDecimal(samp_word.weight);
				//b2=new BigDecimal(line_weights[samp_word.wnum]);
				//m1=b1.multiply(b2);
				//s1=s1.add(m1);
				score = score + samp_word.weight * line_weights[samp_word.wnum];
			}
		}
       // score=s1.doubleValue();
		return score;
	}

	public Label classify_samline(String sample_line) {
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
			if (Pattern.matches("\\d+:[\\-]?[\\d\\.]*", temp_token)) {
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

	public void readSampleLabels(String sls_path) throws Exception {

		// second_first_map=new Hashtable();
		FileReader fr = new FileReader(new File(sls_path));
		BufferedReader br = new BufferedReader(fr);
		Vector temp_vector = new Vector();
		String line = "";
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (!(line.equals(""))) {
				temp_vector.add(line);
			}
		}
		sample_label_set = new Label[temp_vector.size()];
		for (int i = 0; i < sample_label_set.length; i++) {
			sample_label_set[i] = new Label();
		}

		String label_s = "";
		String[] label_pair;
		for (int i = 0; i < sample_label_set.length; i++) {
			label_s = temp_vector.get(i) + "";
			label_pair = label_s.split("_");
			if ((label_pair.length) != 3) {
				continue;
			}
			sample_label_set[i].first_class = Integer.parseInt(label_pair[0]);
			sample_label_set[i].second_class = Integer.parseInt(label_pair[1]);
			sample_label_set[i].third_class = Integer.parseInt(label_pair[2]);
			
			//System.out.println(sample_label_set[i].first_class+" "+sample_label_set[i].second_class+" "+sample_label_set[i].third_class);
			// second_first_map.put(sample_label_set[i].second_class,
			// sample_label_set[i].first_class);
		}
		
		fr.close();
		br.close();

	}

	public String seg_dict(String plain_line)
	{
		String seg_s="";
		seg_s=ewa_seg_dict.simple_line_process(plain_line);
		return seg_s;
	}
	
	public String tag_noun(String seg_line)
	{
		String tag_s="";
		tag_s=ewa_tag_noun.simple_line_process(seg_line);
		return tag_s;
	}
	
	public String sam_gen(String line)
	{
		String sam_line="";
		sam_line=ewa_sam_gen.simple_line_process(line);
		/*
		Vector topV=ewa_sam_gen.rankwordvec;
		System.out.print("top words: ");
		for(int i=0;i<topV.size();i++)
		{
			System.out.print(topV.get(i)+" ");
		}
		System.out.println();
		*/
		return sam_line;
	}
	
	public void load_config() throws Exception
	{
		ewa_seg_dict = new EWASegDict();
		ewa_seg_dict.load_local_config();
		
		ewa_tag_noun= new EWATagNoun();
		ewa_tag_noun.load_local_config();
		
		ewa_sam_gen = new EWASampleGen();
		ewa_sam_gen.load_local_config();
		
		
		//***********dist***********************
		cate_server="192.168.110.181";
		cate_port=8090;
		//**************************************
		
		//***********local**********************
		//cate_server="192.168.1.100";
		//cate_port=8090;
		//*************************************
		
	
	}
	
	public void load_config(Properties prop) throws Exception
	{
		ewa_seg_dict = new EWASegDict();
		ewa_seg_dict.load_config(prop);
		
		ewa_tag_noun= new EWATagNoun();
		ewa_tag_noun.load_config(prop);
		
		ewa_sam_gen = new EWASampleGen();
		ewa_sam_gen.load_config(prop);
		
		
		//***********dist***********************
		cate_server=prop.getProperty("cate_server");
		cate_port=Integer.parseInt(prop.getProperty("cate_port"));
		//**************************************
		
		//***********local**********************
		//cate_server="192.168.1.100";
		//cate_port=8090;
		//*************************************
		
	
	}
	
	public void load_cate_wrods(String cw_file) throws Exception
	{
		cate_words_hash=new Hashtable<String,String>();
		FileReader fr=new FileReader(new File(cw_file));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		String cate_str="";
		String[] seg_arr=null;
		String token="";
		String word="";
		String val="";
		String[] temp_seg=null;
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			seg_arr=line.split("\001");
			if(seg_arr.length<3)
			{
				continue;
			}
			cate_str=seg_arr[0].trim();
			for(int i=2;i<seg_arr.length;i++)
			{
				token=seg_arr[i].trim();
				if((token==null)||(token.equals("")))
				{
					continue;
				}
				temp_seg=token.split("\\|");
				if(temp_seg.length!=2)
				{
					continue;
				}
				word=temp_seg[0].trim();
				val=temp_seg[1].trim();
				if((word==null)||(word.equals("")))
				{
					continue;
				}
				if(!(cate_words_hash.containsKey(word)))
				{
					cate_words_hash.put(word, cate_str+"\001"+val);
				}
			}
		}
		
		
	}
	
	public String predictFromPlainText(String plain_line) throws Exception
	{
		String cate_str="";
		plain_line=plain_line.trim();
		if((plain_line==null)||(plain_line.equals("")))
		{
			return "NA";
		}
				
		String seg_s="";
		seg_s=seg_dict(plain_line);
		seg_s=seg_s.trim();
		System.out.println("seg_s:"+seg_s);
		if((seg_s==null)||(seg_s.equals("")))
		{
			return "NA";
		}
		
		String key_s="";
		key_s=tag_noun(seg_s);
		if((key_s==null)||(key_s.equals("")))
		{
			return "NA";
		}
		System.out.println("key_s:"+key_s);
		String sam_s="";
		sam_s=sam_gen(key_s);
		if((sam_s==null)||(sam_s.equals("")))
		{
			return "NA";
		}
		//System.out.println("sam_s:"+sam_s);
		String ses=service_call("1 1 1 "+sam_s);
		System.out.println("ses:"+ses);
		
		/*
		String test_str=call_classify("1 1 1 "+sam_s);
		//System.out.println("test_str:"+test_str);
		Label predict_label=null;
		predict_label=classify_samline(sam_s);
		cate_str=label_to_str(predict_label);
		*/
		ses=ses.trim();
		String[] ses_seg=ses.split("\\s+");
		if((ses_seg.length)!=4)
		{
			return "";
		}
		
		Label predict_label=null;
		predict_label=new Label();
		predict_label.first_class=Integer.parseInt(ses_seg[0].trim());
		predict_label.second_class=Integer.parseInt(ses_seg[1].trim());
		predict_label.third_class=Integer.parseInt(ses_seg[2].trim());
		cate_str=label_to_str(predict_label);
		System.out.println("cate_str:"+cate_str);
		
		String[] key_seg=key_s.split("\\s+");
		ArrayList<SortElement> al =new ArrayList<SortElement>();
		SortElement sele=null;
		String cate_info="";
		String val_info="";
		String key_item="";
		String[] te_seg=null;
		String cate_name="";
		for(int i=0;i<key_seg.length;i++)
		{
			key_item=key_seg[i].trim();
			if((key_item==null)||(key_item.equals("")))
			{
				continue;
			}
			cate_info=cate_words_hash.get(key_item);
			if(cate_info==null)
			{
				continue;
			}
			te_seg=cate_info.split("\001");
			if(te_seg.length!=2)
			{
				continue;
			}
			cate_name=te_seg[0].trim();
			val_info=te_seg[1].trim();
			if(cate_str.equals(cate_name))
			{
				sele=new SortElement(key_item,Double.parseDouble(val_info),cate_str,0);
				al.add(sele);
			}			
		}
		
		Collections.sort(al);
        Iterator it=al.iterator();
        SortElement stl=null;
        String word_info="";
        while(it.hasNext())
        {
        	stl=(SortElement)it.next();
        	word_info=word_info+stl.key+" ";
        }
		word_info=word_info.trim();
		System.out.println("cate_str:"+cate_str);
		System.out.println("word_info:"+word_info);
		
		return cate_str+"\001"+word_info;
	}
	
	public String predict_from_seg_line(String seg_line) throws Exception
	{
		String cate_res="";
		String cate_str="";
        String seg_s="";
		seg_s=seg_line.trim();
		System.out.println("seg_s:"+seg_s);
		if((seg_s==null)||(seg_s.equals("")))
		{
			return "NA";
		}
		
		String key_s="";
		key_s=tag_noun(seg_s);
		if((key_s==null)||(key_s.equals("")))
		{
			return "NA";
		}
		System.out.println("key_s:"+key_s);
		String sam_s="";
		sam_s=sam_gen(key_s);
		if((sam_s==null)||(sam_s.equals("")))
		{
			return "NA";
		}
		//System.out.println("sam_s:"+sam_s);
		String ses=service_call("1 1 1 "+sam_s);
		System.out.println("ses:"+ses);
		
		/*
		String test_str=call_classify("1 1 1 "+sam_s);
		//System.out.println("test_str:"+test_str);
		Label predict_label=null;
		predict_label=classify_samline(sam_s);
		cate_str=label_to_str(predict_label);
		*/
		ses=ses.trim();
		String[] ses_seg=ses.split("\\s+");
		if((ses_seg.length)!=4)
		{
			return "";
		}
		
		Label predict_label=null;
		predict_label=new Label();
		predict_label.first_class=Integer.parseInt(ses_seg[0].trim());
		predict_label.second_class=Integer.parseInt(ses_seg[1].trim());
		predict_label.third_class=Integer.parseInt(ses_seg[2].trim());
		cate_str=label_to_str(predict_label);
		System.out.println("cate_str:"+cate_str);
		
		String[] key_seg=key_s.split("\\s+");
		ArrayList<SortElement> al =new ArrayList<SortElement>();
		SortElement sele=null;
		String cate_info="";
		String val_info="";
		String key_item="";
		String[] te_seg=null;
		String cate_name="";
		for(int i=0;i<key_seg.length;i++)
		{
			key_item=key_seg[i].trim();
			if((key_item==null)||(key_item.equals("")))
			{
				continue;
			}
			cate_info=cate_words_hash.get(key_item);
			if(cate_info==null)
			{
				continue;
			}
			te_seg=cate_info.split("\001");
			if(te_seg.length!=2)
			{
				continue;
			}
			cate_name=te_seg[0].trim();
			val_info=te_seg[1].trim();
			
			if(cate_str.equals(cate_name))
			{
				sele=new SortElement(key_item,Double.parseDouble(val_info),cate_str,0);
				al.add(sele);
			}			
		}
		
		Collections.sort(al);
        Iterator it=al.iterator();
        SortElement stl=null;
        String word_info="";
        while(it.hasNext())
        {
        	stl=(SortElement)it.next();
        	word_info=word_info+stl.key+" ";
        }
		word_info=word_info.trim();
		System.out.println("cate_str:"+cate_str);
		System.out.println("word_info:"+word_info);
		//return cate_str+"\001"+word_info;
		
		String[] test_seg=word_info.split("\\s+");
		
		/////if((test_seg!=null)&&(test_seg.length>1)&&(!(word_info.equals("")))) restrict num
		if((test_seg!=null)&&(test_seg.length>0)&&(!(word_info.equals(""))))
		{
		cate_res=cate_str+"\001"+word_info;
		}
		else
		{
			cate_res="NA";
		}
		return cate_res;
	}
	
	public String call_classify(String s) throws Exception
	{
		
		s = s + "\n\n";
		String cate_s = "";
		String server = cate_server;
		int port = cate_port;
		Socket socket=null;
		byte[] receiveBuf=null ;
		try {
			socket = new Socket(server, port);
			socket.setSoTimeout(10);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(s.getBytes());
			out.flush();
	
			receiveBuf = new byte[10032 * 8];
			in.read(receiveBuf);
			//Thread.sleep(30000);
			int ret=0;
			/*
			for(int i=0;i<30;i++)
			{
				try{
			    Thread.sleep(1000);
			    receiveBuf = new byte[10032 * 8];
				in.read(receiveBuf);
				cate_s=cate_s+ new String(receiveBuf);
				}
				catch(Exception e1)
				{
					//System.out.println("error:"+e1.getMessage());
				}
			}
           */
			cate_s = new String(receiveBuf);
			System.out.println("cate_s:"+cate_s);
			//socket.close();
		
			//cate_s = new String(receiveBuf);
			//System.out.println("cate_s:"+cate_s);
			//socket.close();
		} catch (Exception e) {
			System.out.println("error:"+e.getMessage());
			Thread.sleep(1000);
		} finally
		{
			socket.close();
		}

		return cate_s;
	}
	
	
	public String service_call(String s) throws Exception
	{
		String cate_s="";
		String server = cate_server;
		int port = cate_port;
		Socket socket=null;
		socket = new Socket(server, port);
		socket.setSoTimeout(1000);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		DataOutputStream dos=new DataOutputStream(out);
		dos.writeUTF(s);
		DataInputStream dis = new DataInputStream(in);
		String ret="";
		try{
		for(int i=0;i<10;i++)
		{
		ret = dis.readUTF();
		System.out.println("ret:"+ret);
		}
		}
		catch(Exception e)
		{
			Thread.sleep(1000);
		}
		return ret;
	}
	public String[] call_local_classify(String[] s_arr) throws Exception
	{
		String[] cate_s_arr=null;
		String s="";
		s = s + "                                                                                                               \n";
		String cate_s = "";
		String server = cate_server;
		int port = cate_port;
		try {
			Socket socket = new Socket(server, port);
			socket.setSoTimeout(10000);
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			out.write(s.getBytes());
			out.flush();

			byte[] receiveBuf = new byte[10032 * 8];
			in.read(receiveBuf);

			cate_s = new String(receiveBuf);
			socket.close();
		} catch (Exception e) {
			Thread.sleep(1000);
		}

		return cate_s_arr;
	}
	
	
	public String label_to_str(Label y)
	{
	  String ls="";
	  int fc=y.first_class;
	  int sc=y.second_class;
	  int tc=y.third_class;
	  
	  if((fc<1)||(sc<1)||(tc<1))
	  {
		  return "NA";
	  }
	  
	  String fs="";
	  String ss="";
	  String ts="";
	  System.out.println("fc:"+fc+"  sc:"+sc+"  tc:"+tc);
	  fs=first_level_hash.get(fc+"")+"";
      ss=second_level_hash.get(sc+"")+"";
      ts=third_level_hash.get(tc+"")+"";
      ls=fs+"|"+ss+"|"+ts;
      ls=ls.trim();
      
	  return ls;
	}

	public void printW(double[] weights) throws Exception
	{
		FileWriter fw=new FileWriter(new File("output/wlog.txt"));
		PrintWriter pw=new PrintWriter(fw);
		
		for(int i=0;i<weights.length;i++)
		{
			System.out.println(i+" "+weights[i]);
			pw.println(i+" "+weights[i]);
		}
		fw.close();
		pw.close();
	}
	
	public static void main(String[] args) throws Exception{

		EWAPredict ewa=new EWAPredict();
		String model_path="model_dir/model"; 
		String sls_path="model_dir/lll.txt";
		String first_level_path="model_dir/fhc.txt"; 
		String second_level_path="model_dir/shc.txt";
		String third_level_path="model_dir/thc.txt";
		String cw_file="input/ec_ckws_num.txt";
		ewa.read_model(model_path, sls_path, first_level_path, second_level_path, third_level_path);
		ewa.load_config();
		ewa.load_cate_wrods(cw_file);
		//String test_line="线香香插 香炉 塔香座 点香器--素莲香插（方便携带 可插三支香）";
		//String test_line="哈森专柜正品代购13秋冬款女靴HA37102 HA37101 HA36401 HA32205";
		//String test_line="恒源祥正品女士羊毛衫 秋冬新款女装堆堆领100%羊毛毛衣 KV00010";
		//String test_line="【Mewer喵族】美国短毛猫 加白银虎斑 家养美短GG &quot;小米鼠&quot; 预订";
		//String test_line="【玖合玉器】新疆和田碧玉平安扣手链   15颗碧玉平安扣串最新款";
		//String test_line="集成i7 高端客厅电脑 HTPC/迷你电脑/HTPC整机";
		//String test_line="秋冬季新款短裙半身裙打底裙蕾丝蓬蓬裙蛋糕裙迷你裙裙子T001";
		String test_line="木工电脑雕刻机";
		System.out.println("res a:"+ewa.predictFromPlainText(test_line));
		/*
		FileReader fr=new FileReader(new File("input/test_10000.txt"));
		BufferedReader br=new BufferedReader(fr);
		String line="";
		
		FileWriter fw=new FileWriter(new File("output/test_output_10000.txt"));
		PrintWriter pw=new PrintWriter(fw);
		String[] temp_seg=null;
		
		String title="";
		String cate_s="";
		while((line=br.readLine())!=null)
		{
			line=line.trim();
			if((line==null)||(line.equals("")))
			{
				continue;
			}
			temp_seg=line.split("\\t");
			if((temp_seg.length)!=3)
			{
				continue;
			}
			title=temp_seg[2].trim();
			cate_s=ewa.predictFromPlainText(title);
			pw.println(line+"  "+cate_s);
		  //System.out.println(ewa.predictFromPlainText(test_line));
		}
		fw.close();
		pw.close();
		fr.close();
		br.close();
		*/
	}

}
