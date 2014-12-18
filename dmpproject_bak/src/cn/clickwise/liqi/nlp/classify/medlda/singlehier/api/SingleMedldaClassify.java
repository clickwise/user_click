package cn.clickwise.liqi.nlp.classify.medlda.singlehier.api;

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

import redis.clients.jedis.Jedis;
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

public class SingleMedldaClassify extends ModelClassify{
	
	private Properties prop;
	
	private String model_data;
	
	private boolean use_format_sample=false;
	
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
	 * 模型的词典  
	 */
	private HashMap<String,Integer> dict=null;
	
	/**
	 * 向量空间选项，如：
	 * 1  普通文本向量空间
	 * 2 word2vec向量空间
	 * 3 medlda转换的向量空间
	 */
    private short vec_space; 
    
    /**
     * 是否使用分词tcp server
     */
    private boolean use_classify_server;
    
    private int classify_port=0;
    
    private String classify_server="";
	
	/**
	 *类别标记编码转换成类名 
	 */
	private HashMap<String,String>  label_name_map=null;
	
	@Override
	public void load_config(String config_file) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void load_config(Properties prop) 
	{
	    this.prop=prop;
	    model_data=prop.getProperty("model_data");
	    //System.out.println("model_data:"+model_data);
	   
		use_format_sample=Boolean.parseBoolean(this.prop.getProperty("use_format_sample"));
	   // System.out.println("use_format_sample:"+use_format_sample);
		if(use_format_sample==false)
		{
		    getDictFromPlainFile();
		  	segmodel=SegmenterSegFactory.create(prop);
		  	tagmodel=PostaggerTagFactory.create(prop);
		  	keymodel=KeywordSelFactory.create(prop);
		}
		
		getLabelNameFromFile();
		vec_space=Short.parseShort(this.prop.getProperty("vec_space"));
		use_classify_server=Boolean.parseBoolean(prop.getProperty("use_classify_server"));
		
		classify_port=Integer.parseInt(prop.getProperty("classify_port"));
		classify_server=prop.getProperty("classify_server");
		
	}
	
	@Override
	public String predictFromPlainText(String text) {
		// TODO Auto-generated method stub	
		
	  	String cate="";
    	String seg_s="";
    	String tag_s="";
    	String key_s="";
    	try{
    	text=text.trim();
    	seg_s=segmodel.seg(text);
    	System.out.println("seg_s:"+seg_s.trim());
    	tag_s=tagmodel.tag(seg_s);
    	System.out.println("tag_s:"+tag_s.trim());
    	key_s=keymodel.keywordFromTag(tag_s);
    	
    	}
    	catch(Exception e){System.out.println(e.getMessage());}
    	 String fat_s="";
    	 
    	 System.out.println("key_s:"+key_s.trim());
        if(vec_space>1)
        {
        	//fat_s=vecFromKey(key_s);
        }
        else
        {
        	fat_s=getFormatFromKey(key_s);
        }
        
        //System.out.println("fat_s:"+fat_s);
        fat_s=fat_s+"\n";
        if(use_classify_server==true)
        {
        	try{
				Socket socket = new Socket(classify_server, classify_port);
				socket.setSoTimeout(10000);
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				out.write(fat_s.getBytes());
				out.flush();

				byte[] receiveBuf = new byte[10032];
				in.read(receiveBuf);

				cate = new String(receiveBuf);
				socket.close();
    		}
    		catch(Exception e)
    		{
    			System.out.println(e.getMessage());
    		}		
        }
      //  System.out.println("cate:"+cate);
        if(!(SSO.tnoe(cate)))
        {
        	return "";
        }
        cate=cate.trim();
        String[] line_seg=cate.split("\n");
        //System.out.println(" line_seg.length:"+ line_seg.length);
        String[] temp_seg=(line_seg[3].trim()).split("\\s+");
        //System.out.println("temp_seg.length:"+temp_seg.length);
       if((temp_seg.length)!=2)
       {
    	   return "";
       }
       //System.out.println("temp_seg[0]:"+temp_seg[0]);
        cate=label_name_map.get(temp_seg[0].trim());
		return cate;
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
			   //if(index%100==0)
			   //{
				   //System.out.println(word+" "+index_str);
			  // }
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
		   format_sample=cnts.size()+" "+"1 "+res;
		   return format_sample;
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
				   label_name=seg_arr[0].trim();
				   label_num=seg_arr[1].trim();
				  // System.out.println(label_name+" "+label_num);
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
		
	   public static void main(String[] args) throws Exception
	   {
			String config_file="";
			if (args.length != 1) {
				System.out.println("用法 :EWAECServer <configure file>");
				System.exit(1);
			}
		
			config_file=args[0];
			
			Properties prop=ConfigFileReader.getPropertiesFromFile(config_file);
			
			ModelClassify classifier=ModelClassifyFactory.create(prop);
			String text="《冲上云霄2》胡杏儿：工作是我生活的全部_扬子晚报网";
			System.out.println(classifier.predictFromPlainText(text));
	   }

}
