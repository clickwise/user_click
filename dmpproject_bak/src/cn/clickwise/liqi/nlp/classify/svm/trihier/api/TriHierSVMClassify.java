package cn.clickwise.liqi.nlp.classify.svm.trihier.api;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import redis.clients.jedis.Jedis;

import cn.clickwise.liqi.nlp.classify.basic.ModelClassify;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSel;
import cn.clickwise.liqi.nlp.keyword.basic.KeywordSelFactory;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTag;
import cn.clickwise.liqi.nlp.postagger.basic.PostaggerTagFactory;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSeg;
import cn.clickwise.liqi.nlp.segmenter.basic.SegmenterSegFactory;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.configutil.ConfigFileReader;

/**
 * 调用训练好的分类模型进行分类
 * @author lq
 */
public class TriHierSVMClassify extends ModelClassify{

	/*****模型源代码所在的文件夹*********/
	private String src_workplace;
	
	/*****生成模型所在的文件夹**********/
	private String model_data;
	
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
     * 是否使用分词tcp server
     */
    private boolean use_classify_server;
	
    /**
     * 是否适用格式化的样本输入
     */
    private boolean  use_format_sample;
    
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
	 * 读取配置文件
	 * @param config_file
	 */
	public void load_config(String config_file) throws Exception
	{
	    prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
		
		src_workplace=prop.getProperty("src_workplace");
		model_data=prop.getProperty("model_data");
		c=Double.parseDouble(prop.getProperty("c"));
				
	}
	
	public void load_config(Properties prop) 
	{
		this.prop=prop;
		src_workplace=this.prop.getProperty("src_workplace");
		model_data=this.prop.getProperty("model_data");
		c=Double.parseDouble(this.prop.getProperty("c"));
		use_classify_server=Boolean.parseBoolean(prop.getProperty("use_classify_server"));
		if(use_classify_server==true)
		{
			try{
		    	start_classify_server();
			}
			catch(Exception e){}
		}
				
		use_format_sample=Boolean.parseBoolean(this.prop.getProperty("use_format_sample"));
		if(use_format_sample==false)
		{
		    getDictFromPlainFile();
		  	segmodel=SegmenterSegFactory.create(prop);
		  	tagmodel=PostaggerTagFactory.create(prop);
		  	keymodel=KeywordSelFactory.create(prop);
		}
		
		vec_space=Short.parseShort(this.prop.getProperty("vec_space"));
		if(vec_space>1)
		{
			vec_size=Integer.parseInt(this.prop.getProperty("vec_size"));
			word_vec_ip=this.prop.getProperty("word_vec_ip");
			word_vec_port=Integer.parseInt(this.prop.getProperty("word_vec_port"));
			word_vec_db=Integer.parseInt(this.prop.getProperty("word_vec_db"));
			word_vec_redis = new Jedis(word_vec_ip, word_vec_port, 100000);
			word_vec_redis.ping(); 
			word_vec_redis.select(word_vec_db);
		}
		
		getLabelNameFromFile();
	}
	
    public String predictFromPlainText(String text) 
    {
    	String cate="";
    	String seg_s="";
    	String tag_s="";
    	String key_s="";
    	try{
    	seg_s=segmodel.seg(text);
    	tag_s=tagmodel.tag(seg_s);
    	key_s=keymodel.keywordFromTag(tag_s);
    	
    	}
    	catch(Exception e){}
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
     		return "NA";
     	}
     	
     	String cate_s="";
     	if(use_classify_server==true)
     	{
     		try{
     		cate_s=server_classify(fat_s);
     		}
     		catch(Exception e)
     		{
     			
     		}
     	}
     	
    	return cate;
    }
    
    
	public String server_classify(String s) throws Exception
	{
		String cate_s="";
		String server = "";
		int port = 0;
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
    
    
	@Override
	public void predictFromPlainFile(String unlabel_file, String predict_file) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 读取模型的词典文件，建立单词索引
	 * 词典索引从1开始
	 * @return
	 */
	public void getDictFromPlainFile() {
		// TODO Auto-generated method stub
	
		String dict_file=model_data+"/dict.txt";
	    String item="";
	    String word="";
	    String index_str="";
		int index=0;
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
	
		dict=new HashMap<String,Integer>();
		
		try{
		   fr=new FileReader(new File(dict_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   word=seg_arr[0].trim();
			   index_str=seg_arr[1].trim();
			   
			   if(!(SSO.tnoe(word)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(index_str)))
			   {
				   continue;
			   }
			   index=Integer.parseInt(index_str);
			   if(index<1)
			   {
				   continue;
			   }
			   dict.put(word, index);			   
		   }
		   
		}
		catch(Exception e)
		{
			
		}	
	}
	
	/**
	 * 从类别信息文件读取类别编号和类别名的对应关系
	 */
	public void getLabelNameFromFile()
	{
		String label_file=model_data+"/label.txt";
	    String item="";
	    String label_num="";
	    String label_name="";
	  
		FileReader fr=null;
		BufferedReader br=null;
		
		String[] seg_arr=null;
	
		label_name_map=new HashMap<String,String>();
		
		try{
		   fr=new FileReader(new File(label_file));
		   br=new BufferedReader(fr);
		   while((item=br.readLine())!=null)
		   {
			   if(!(SSO.tnoe(item)))
			   {
				   continue;
			   }
			   seg_arr=item.split("\\s+");
			   if(seg_arr.length!=2)
			   {
				   continue;
			   }
			   label_num=seg_arr[0].trim();
			   label_name=seg_arr[1].trim();
			   
			   if(!(SSO.tnoe(label_num)))
			   {
				   continue;
			   }
			   
			   if(!(SSO.tnoe(label_name)))
			   {
				   continue;
			   }
			   
			   label_name_map.put(label_num, label_name);			   
		   }
		   
		}
		catch(Exception e)
		{
			
		}	
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
	   res=res.trim();
	   if(!(SSO.tnoe(res)))
	   {
		   return "";
	   }
	   
	   format_sample="1 1 1 "+res;
	   return format_sample;
   }
   
   /**
    * 启动在线的分类服务
    * @return
    * @throws Exception
    */
   public int start_classify_server() throws Exception
   {
	   int classify_port=Integer.parseInt(prop.getProperty("classify_port"));
	   String cmd=src_workplace+"/svm_multiclass_proxy -m "+model_data+"/model  -l "+model_data+"/lll.txt -p "+classify_port+" > log.txt 2>&1 &";
	   Process pro = Runtime.getRuntime().exec(cmd);
	   return 0;
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
			System.out.println("用法 :EWAHttpServer <configure file>");
			System.exit(1);
		}
	
		config_file=args[0];
		Properties prop = null;
		prop=ConfigFileReader.getPropertiesFromFile(config_file);
		TriHierSVMClassify thsc=new TriHierSVMClassify();
		thsc.load_config(prop);
		
		String title="绝秘特效诱鱼剂钓鱼饵料顶级小药添加剂/藏药配方/试用折扣包邮";
		String cate=thsc.predictFromPlainText(title);
		System.out.println("cate:"+cate);
		
	}
	
}
