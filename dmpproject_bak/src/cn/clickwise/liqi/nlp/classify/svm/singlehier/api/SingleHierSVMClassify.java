package cn.clickwise.liqi.nlp.classify.svm.singlehier.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

import cn.clickwise.liqi.file.utils.FileReaderUtil;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassify;
import cn.clickwise.liqi.nlp.classify.basic.ModelClassifyFactory;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSel;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSelFactory;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTagFactory;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSegFactory;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

public class SingleHierSVMClassify extends ModelClassify{
	
	/**Model variables***/
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
	
	/**model data dir**/
	public String shsvm_model_data="";
	
	/**
	 * 分词模型
	 */
	private SegmenterSeg segmodel;
	
	/**
	 * 词性标注模型
	 */
	private PostaggerTag tagmodel;
	
	/**
	 * 提取关键词的模型
	 */
    private KeywordSel keymodel;
    
	/**
	 * 向量空间选项，如：
	 * 1  普通文本向量空间
	 * 2 word2vec向量空间
	 * 3 medlda转换的向量空间
	 */
    private short vec_space; 
   
	/**
	 * 向量的维度
	 */
    private int vec_size=0;
    
	/**
	 * 单词转换向量的redis
	 */
    private Jedis word_vec_redis;
	
	/**
	 * 单词转换向量的redis ip 
	 */
    private String word_vec_ip;
	
	/**
	 * 单词转换向量的redis port
	 */
    private int word_vec_port;
	
	/**
	 * 单词转换向量的redis db 
	 */
    private int word_vec_db;
	
	/**
	 * 模型的词典  
	 */
	private HashMap<String,Integer> dict=null;
	
	/**
	 *类别标记编码转换成类名 
	 */
	private HashMap<String,String>  label_name_map=null;
	
	private int label_count=0;
	
	@Override
	public void load_config(String config_file) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load_config(Properties prop){
		// TODO Auto-generated method stub
		shsvm_model_data=prop.getProperty("shsvm_model_data");
		System.out.println("shsvm_model_data:"+shsvm_model_data);
		vec_space=Short.parseShort(prop.getProperty("vec_space"));
		System.out.println("vec_space:"+vec_space);
		
		if(vec_space>1)
		{
			vec_size=Integer.parseInt(prop.getProperty("vec_size"));
			word_vec_ip=prop.getProperty("word_vec_ip");
			word_vec_port=Integer.parseInt(prop.getProperty("word_vec_port"));
			word_vec_db=Integer.parseInt(prop.getProperty("word_vec_db"));
			word_vec_redis = new Jedis(word_vec_ip, word_vec_port, 100000);
			word_vec_redis.ping(); 
			word_vec_redis.select(word_vec_db);
		}
			
	  	segmodel=SegmenterSegFactory.create(prop);
	  	tagmodel=PostaggerTagFactory.create(prop);
	  	keymodel=KeywordSelFactory.create(prop);
	  	label_count=Integer.parseInt(prop.getProperty("label_count"));
	  	System.out.println("label_count:"+label_count);
	  	try{
	    readDict();
	  	read_model();
	  	
	  	}
	  	catch(Exception e)
	  	{
	  		System.out.println(e.getMessage());
	  	}
	}

	@Override
	public String predictFromPlainText(String text) {
		// TODO Auto-generated method stub
    	String seg_s="";
    	String tag_s="";
    	String key_s="";
	    String fat_s="";
	    Label pred_y=null;
	    String cate_s="";
    	try{
			if(!(SSO.tnoe(text)))
			{
				return "NA";
			}
			
		    seg_s=segmodel.seg(text);
		    System.out.println("seg_s:"+seg_s);
		    tag_s=tagmodel.tag(seg_s);
		    System.out.println("tag_s:"+tag_s);
		    key_s=keymodel.keywordFromTag(tag_s);
		    System.out.println("key_s:"+key_s);

	        
		    if(vec_space>1)
		    {
		       fat_s=vecFromKey(key_s);
		    }
		    else
		    {
		       fat_s=getFormatFromKey(key_s);
		    }
		    
		    if(!(SSO.tnoe(fat_s)))
		    {
		    	return "NA";
		    }
		    fat_s=fat_s.trim();		
		    System.out.println("fat_s:"+fat_s);
		    pred_y=docate(fat_s); 
		    cate_s=getCateName(pred_y);
		    System.out.println("cate_s:"+cate_s);
		    if(!(SSO.tnoe(cate_s)))
		    {
		    	cate_s="NA";
		    }
    	}
		catch(Exception e)
		{
			
		}
		return cate_s;
	}

	@Override
	public void predictFromPlainFile(String unlabel_file, String predict_file) {
		// TODO Auto-generated method stub
		
	}

	public void readDict()
	{
		String dict_file=this.shsvm_model_data+"/dict.txt";
		dict=FileReaderUtil.getDictFromPlainFile(dict_file);
		
		String label_file=this.shsvm_model_data+"/label.txt";	
		label_name_map=FileReaderUtil.getIndexLabelFromPlainFile(label_file);
		
	}
	
	 /**
	    * 从提取关键词后的文本获取格式化样本
	    * @param key_s
	    * @return
	    */
	   public String getFormatFromKey(String key_s)	
	   {
		   if(!(SSO.tnoe(key_s)))
		   {
			   return "";
		   }
		   String format_sample="";
			String words[] = key_s.split("[\\s]+");
			String res = "";
			String ids = "";
			HashMap<Integer, Integer> cnts = new HashMap<Integer, Integer>();
			for (int i = 0; i < words.length; i++) {
				Integer id = dict.get(words[i]);
				if (id != null) {
					Integer cnt = cnts.get(id);
					if (cnt == null)
						cnts.put(id, 1);
					else
						cnts.put(id, cnt + 1);
				}
			}
			List<Integer> keys = new ArrayList<Integer>(cnts.keySet());
			Collections.sort(keys, new Comparator<Integer>() {
				public int compare(Integer l1, Integer l2) {
					if (l1 > l2)
						return 1;
					else if (l1 < l2)
						return -1;
					return 0;
				}
			});

			for (int i = 0; i < keys.size(); i++) {
				Integer l = keys.get(i);
				if (i == 0)
					res += l + ":" + cnts.get(l);
				else
					res += " " + l + ":" + cnts.get(l);
			}
	
		   if(!(SSO.tnoe(res)))
		   {
			   return "";
		   }
		   res=res.trim();
		   //format_sample=cnts.size()+" "+"1 "+res;
		   format_sample=res;
		   return format_sample;
	   }
	   
		/**
		 * 提取关键词后的文本转换成向量
		 * @param key_s
		 * @return
		 */
		public String vecFromKey(String key_s) {

			//System.out.println("line:"+line);
			String new_line = "";
			String seg_title="";
			
	        double[] line_vec_words=new double[vec_size];
	        for(int i=0;i<line_vec_words.length;i++)
	        {
	        	line_vec_words[i]=0;
	        }
	           

	        seg_title=key_s.trim();
	        //System.out.println(cate+" "+seg_title);
	        if((seg_title==null)||(seg_title.equals("")))
	        {
	        	return "";
	        }
	        
	        
	        String[] temp_seg=null;
	        temp_seg=seg_title.split("\\s+");
	        
	        String word="";
	        //System.out.println();
	        if(temp_seg.length<1)
	        {
	        	return "";
	        }
	        
	       // word=temp_seg[0].trim();
	        String temp_rl="";
	        for(int i=0;i<temp_seg.length;i++)
	        {
	           word=temp_seg[i].trim();
	           if(isValidWord(word))
	           {
	        	//   System.out.print(word+" ");
	        	  // print_weghts("add1:",line_vec_words);
	        	   
	        	  // print_weghts("add2:",toLineWeights(swa_dict_redis.get(word)));
	        	   temp_rl=word_vec_redis.get(word);
	        	   
	        	   if((temp_rl==null)||(temp_rl.trim()).equals(""))
	        	   {
	        		   continue;
	        	   }
	        	   temp_rl=temp_rl.trim();
	        	   line_vec_words=addTwoWeights(line_vec_words,toLineWeights(temp_rl));
	        	  // print_weghts("addafter:", line_vec_words);
	           }
	        }

	        new_line=toLineWords(line_vec_words);  
	        new_line=new_line.trim();
	        new_line="1 1 1 "+new_line;
			return new_line;
		}
		
		
		public double[] toLineWeights(String line)
		{
			double[] lw=new double[vec_size];
			line=line.trim();
			String[] seg_arr=line.split("\\s+");
			if(seg_arr.length!=vec_size)
			{
				for(int j=0;j<lw.length;j++)
				{
					lw[j]=0;
				}
			}
			else
			{
				for(int j=0;j<vec_size;j++)
				{
					lw[j]=Double.parseDouble(seg_arr[j]);
				}
			}
			
			return lw;
		}
		
		public String toLineWords(double[] weights)
		{
			String wl="";
			String word_token="";
			for(int i=0;i<weights.length;i++)
			{
				word_token=(i+1)+":"+weights[i];
				wl=wl+word_token+" ";
			}
			wl=wl.trim();
			
			return wl;
		}
		
		public double[] addTwoWeights(double[] weights_1,double[] weights_2)
		{
			double[] nw=new double[vec_size];
			for(int i=0;i<vec_size;i++)
			{
			  nw[i]=weights_1[i]+weights_2[i];	
			}	
			return nw;
		}
	   
		public boolean isValidWord(String word)
		{
			boolean ivl=true;
			if(word==null)
			{
				return false;
			}
			if(word.equals(""))
			{
				ivl=false;
			}
			
			if(Pattern.matches("\\\\/", word))
			{
				ivl=false;
			}	
			return ivl;
		}
		
		public class Word {
			int wnum;
			double weight;
		}

		public class Label {
			int first_class;
			double score;
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
					score = score + samp_word.weight
							* line_weights[samp_word.wnum];
				}
			}

			return score;
		}
		
		
		public Label classify_struct_example(Word[] sample) {

			Label y = null;
			double score = 0;

			Label best_label = null;
			double best_score = -1;

			Word[] fvec = null;
			System.out.println("label_count:"+label_count);
			for (int i = 0; i < label_count; i++) {
				y = new Label();
				y.first_class = (i + 1);
				fvec = psi(sample, y);
				score = classify_example(fvec);
				System.out.println(y.first_class+":"+score);
				if (score > best_score) {
					best_score = score;
					best_label = y;
				}
			}
			best_label.score = best_score;
			return best_label;
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
			System.out.println("y.first_label:"+y.first_class);
			// System.out.println("y.first_label:"+y.first_class+"  y.second_label:"+y.second_class);
			return y;
		}
		
		/**
		 * 从model文件读取变量的值
		 * @param model_path
		 * @throws Exception
		 */
		public void read_model() throws Exception {
			
			String model_file=this.shsvm_model_data+"/model.txt";
			InputStream model_is=new FileInputStream(model_file);
			//InputStream model_is = this.getClass().getResourceAsStream(
			//		"/" + model_path);
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
							qid = Integer.parseInt(temp_token
									.substring(temp_token.indexOf(":") + 1),
									temp_token.length());
						} else if (Pattern
								.matches("\\d+:[\\d\\.]+", temp_token)) {
							temp_index = Integer.parseInt(temp_token.substring(
									0, temp_token.indexOf(":")));
							temp_weight = Double.parseDouble(temp_token
									.substring(temp_token.indexOf(":") + 1,
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
			
			System.out.println("NUM_CLASS:"+NUM_CLASS);
			System.out.println("NUM_WORDS:"+NUM_WORDS);
			System.out.println("NUM_FEATURES:"+NUM_FEATURES);
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
		
		public String getCateName(Label y) {
			String cate_name = "";
			int tempid = y.first_class;
			
		    cate_name = label_name_map.get(y.first_class+"");
            if(!(SSO.tnoe(cate_name)))
            {
            	cate_name="";
            }
			return cate_name;
		}
	
		public static void main(String[] args) throws Exception
		{
			String config_file="";
			if (args.length != 1) {
				System.out.println("用法 :EWAECServer <configure file>");
				System.exit(1);
			}
		
			config_file=args[0];
			Properties prop=ConfigFileReader.getPropertiesFromFile(config_file);
			
            ModelClassify mc=ModelClassifyFactory.create(prop);
			String title="新浪网为全球用户24小时提供全面及时的中文资讯，内容覆盖国内外突发新闻事件、体坛赛事、娱乐时尚、产业资讯、实用信息等，设有新闻、体育、娱乐、财经、科技、房产、汽车等30多个内容频道，同时开设博客、视频、论坛等自由互动交流空间";
			System.out.println(mc.predictFromPlainText(title));		
		}
		
}
