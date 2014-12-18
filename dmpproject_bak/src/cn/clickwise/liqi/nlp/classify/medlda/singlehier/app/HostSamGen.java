package cn.clickwise.liqi.nlp.classify.medlda.singlehier.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

import cn.clickwise.liqi.external.bash.BashCmd;
import cn.clickwise.liqi.file.utils.FileWriterUtil;
import cn.clickwise.liqi.nlp.classify.basic.ModelTrain;
import cn.clickwise.liqi.nlp.classify.basic.ModelTrainFactory;
import cn.clickwise.liqi.nlp.classify.medlda.singlehier.app.HostCate;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSel;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSelFactory;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTagFactory;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSegFactory;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

public class HostSamGen extends ModelTrain{
	
	private String shsvm_workplace="";
	
	private String shsvm_model_data="";
	
	/*****模型参数c****/
	private double c;
	
	/**
	 * 存储所有的配置选项
	 */
	private Properties prop;
	
	/**
	 * 模型的词典  
	 */
	private HashMap<String,Integer> dict=null;
	
	/**
	 *类别标记编码转换成类名 
	 */
	private HashMap<String,String>  label_name_map=null;
	
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
    
	@Override
	public void load_config(Properties prop) throws Exception{
		// TODO Auto-generated method stub
		this.prop=prop;
		shsvm_workplace=prop.getProperty("shsvm_workplace");
		shsvm_model_data=prop.getProperty("shsvm_model_data");
		c=Double.parseDouble(prop.getProperty("c"));
		vec_space=Short.parseShort(prop.getProperty("vec_space"));
		
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
	  	getDictLabelFromTrain();
	  	
	  	
	}
	
	@Override
	public void trainFromPlainText() throws Exception {
		// TODO Auto-generated method stub
		String train_file=this.shsvm_model_data+"/train.txt";
		String train_format_file=this.shsvm_model_data+"/train_format.txt";
		String model_file=this.shsvm_model_data+"/model.txt";
		
		
		FileWriter fw=new FileWriter(new File(train_format_file));
		PrintWriter pw=new PrintWriter(fw);
		
		
		FileReader fr=new FileReader(new File(train_file));
		BufferedReader br=new BufferedReader(fr);
		
		String line="";
		String[] seg_arr=null;
		
		String cate="";
		String text_s="";
    	String seg_s="";
    	String tag_s="";
    	String key_s="";
		String docid="";
    	String cate_index="";
   
	   	try{
		  while((line=br.readLine())!=null)
		  {
			if(!(SSO.tnoe(line)))
			{
				continue;
			}

			line=line.trim();
			seg_arr=line.split("\001");
			if(seg_arr.length!=3)
			{
				continue;
			}
			
			cate=seg_arr[0].trim();
			text_s=seg_arr[2].trim();
			docid=seg_arr[1].trim();
			if(!(SSO.tnoe(cate)))
			{
				continue;
			}
			
			cate_index=label_name_map.get(cate);
			
			if(!(SSO.tnoe(text_s)))
			{
				continue;
			}
			

		    seg_s=segmodel.seg(text_s);
		    tag_s=tagmodel.tag(seg_s);
		   // key_s=keymodel.keywordFromTag(tag_s);
		    key_s=keymodel.keywordFromTagNoun(tag_s);	

		    String fat_s="";
		        
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
		    	continue;
		    }
		    fat_s=fat_s.trim();
		    
		    pw.println(cate_index+"\001"+docid+"\001"+fat_s);
		   }
    	 }
    	catch(Exception e){
    		System.out.println(e.getMessage());
    	}	
		
	   	pw.close();
	   	fw.close();
	   	String train_cmd="";
	   	/*
	  	train_cmd=this.shsvm_workplace+"/svm_multiclass_learn -c "+this.c+" "+train_format_file+" "+model_file+" >logll.txt 2>&1 &";	   	
	   	System.out.println("train_cmd:"+train_cmd);
	  	BashCmd.execmd(train_cmd);
	   	*/
	}
	
	public void getDictLabelFromTrain() throws Exception
	{
		String train_file=this.shsvm_model_data+"/train.txt";
		String dict_file=this.shsvm_model_data+"/dict.txt";
		String label_file=this.shsvm_model_data+"/label.txt";
		
		FileReader fr=new FileReader(new File(train_file));
		BufferedReader br=new BufferedReader(fr);
		
		String line="";
	  	String cate="";
    	String seg_s="";
    	String tag_s="";
    	String key_s="";
    	
    	String[] seg_arr=null;
    	String text_s="";
    	
    	int label_index=1;
    	int dict_index=1;
    	
    	label_name_map=new HashMap<String,String>();
    	dict=new HashMap<String,Integer>();
    	
    	String[] temp_seg=null;
    	
    	String word="";
    	String label="";
    	try{
		  while((line=br.readLine())!=null)
		  {
			if(!(SSO.tnoe(line)))
			{
				continue;
			}
			
			line=line.trim();
			seg_arr=line.split("\001");
			if(seg_arr.length!=3)
			{
				continue;
			}
			
			cate=seg_arr[0].trim();
			text_s=seg_arr[2].trim();
			
			if(!(SSO.tnoe(cate)))
			{
				continue;
			}
			
    		if(!(label_name_map.containsKey(cate)))
    		{
    			label_name_map.put(cate, label_index+"");
    			label_index++;
    		}
			
			if(!(SSO.tnoe(text_s)))
			{
				continue;
			}
			
	    	
	        	seg_s=segmodel.seg(text_s);
	        	System.out.println("seg_s:"+seg_s);
	        	tag_s=tagmodel.tag(seg_s);
	        	System.out.println("tag_s:"+tag_s);
	        	key_s=keymodel.keywordFromTagNoun(tag_s);
	        	

			
	        temp_seg=key_s.split("\\s+");
	        
	    	for(int j=0;j<temp_seg.length;j++)
	    	{
	    		word=temp_seg[j].trim();
	    		if(!(dict.containsKey(word)))
	    		{
	    			dict.put(word, dict_index);
	    			dict_index++;
	    		}
	    	}  	
		  }
	    	
    	}
        catch(Exception e){
    	   System.out.println(e.getMessage());
        }
		FileWriterUtil.writeHashMap(dict, dict_file);
		FileWriterUtil.writeHashMap(label_name_map, label_file);
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
		
		
		public static void main(String[] args) throws Exception
		{
			String config_file="";
			if (args.length != 1) {
				System.out.println("用法  SingleHierSVMTrain <configure file>");
				System.exit(1);
			}
		
			config_file=args[0];
			Properties prop=ConfigFileReader.getPropertiesFromFile(config_file);
			
			HostSamGen hsg=new HostSamGen();
			hsg.load_config(prop);
			hsg.trainFromPlainText();
			
		}
	
}
