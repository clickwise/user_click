package cn.clickwise.clickad.sample;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import love.cq.util.MapCount;
import cn.clickwise.clickad.classify.Classifier;
import cn.clickwise.lib.string.SSO;


public class MetricsSampler {

	/**
	 * 统计单词出现的次数
	 */
	private MapCount<String> dictCounts;
	
	/**
	 * 统计label出现的次数
	 */
	private MapCount<String> labelCounts;
	
	/**
	 * 词典索引
	 */
	private HashMap<String,Integer> dicts;
	
	/**
	 * label索引
	 */
	private HashMap<String,Integer> labels;
	
	private Metrics metrics=null;
	
	static Logger logger = LoggerFactory.getLogger(MetricsSampler.class);
	
	public MetricsSampler(){
		dictCounts=new MapCount<String>();
		labelCounts=new MapCount<String>();
		dicts=new HashMap<String,Integer>();
		labels=new HashMap<String,Integer>();
		metrics=MetricsFactory.getMetrics();
	}
	
	public void getDictsAndLabels(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,int topNum){
		
		String line="";
		String[] fields=null;
		String label="";
		String text="";
		String[] tokens=null;
		
		int dict_index = 1;
		int label_index = 1;
		String token="";
		
		for(int i=0;i<docs.size();i++)
		{
			line=docs.get(i);
			fields=line.split(separator);
			if(fields.length!=field_num)
			{
				continue;
			}
			
		    label=fields[label_field_index];
		    text=fields[sample_field_index];
		    if(SSO.tioe(label))
		    {
		    	continue;
		    }
		    label=label.trim();
		    
		    labelCounts.add(label);
		    if(!(labels.containsKey(label)))
		    {
		    	labels.put(label, label_index++);
		    }
		    
		    if(SSO.tioe(text))
		    {
		    	continue;
		    }
		    text=text.trim();
		    
		    tokens=text.split("\\s+");
		    for(int j=0;j<tokens.length;j++)
		    {
		      token=tokens[j];
		      if(SSO.tioe(token))
		      {
		    	  continue;
		      }
		      token=token.trim();
		      dictCounts.add(token);
			  if(!(dicts.containsKey(token)))
			  {
			    dicts.put(token, dict_index++);
			  }
		    }
		    
		}//doc loop end
		
		if(MetricsConfig.isCateSel==true)
		{
		   HashMap<String,HashMap<String,Double>> cateWordMetrics=metrics.getCateWordMetrics(field_num, sample_field_index, label_field_index, separator, docs, dicts, labels, dictCounts, labelCounts);
		
		   for(Map.Entry<String, HashMap<String,Double>> m:cateWordMetrics.entrySet())
		   {
			 logger.info(m.getKey()+":"+m.getValue().size());
		   }
		
		   HashMap<String,ArrayList<WORD>> sortCWM=Metrics.sortCateWordsMetrics(cateWordMetrics);
		   dicts=new HashMap<String,Integer>();
		   dict_index=1;
		   System.out.println("sortCWM.size:"+sortCWM.size());
		   System.out.println("topNum:"+topNum);
		   for(Map.Entry<String, ArrayList<WORD>> m:sortCWM.entrySet())
		   {
			  ArrayList<WORD> swlist=m.getValue();
			  for(int i=0;(i<topNum)&&(i<swlist.size());i++)
			  {
				  logger.info(m.getKey()+"\t"+swlist.get(i).w+":"+swlist.get(i).v);
				  if(!(dicts.containsKey(swlist.get(i).w)))
				  {
				    dicts.put(swlist.get(i).w, dict_index++);
				  }
			  }
			
		   }
		
		}
		else
		{
			HashMap<String, Double> wordMetrics=metrics.getWordMetrics(field_num, sample_field_index, label_field_index, separator, docs, dicts, labels, dictCounts, labelCounts);
			ArrayList<WORD> sortWords=metrics.sortHash(wordMetrics);
			logger.info("sortWords.size:"+sortWords.size());
			logger.info("topNum:"+topNum);
			dicts=new HashMap<String,Integer>();
			dict_index=1;
			for(int i=0;(i<topNum)&&(i<sortWords.size());i++)
			{	  
				  if(!(dicts.containsKey(sortWords.get(i).w)))
				  {
					logger.info(sortWords.get(i).w+":"+sortWords.get(i).v);
				    dicts.put(sortWords.get(i).w, dict_index++);
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
				if(!(dicts.containsKey(words[i])))
				{
					continue;
				}
				ids = dicts.get(words[i])+"";
				
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
	
	public void train2sample(int field_num,int sample_field_index,int label_field_index,String separator,String outputSeparator,String gendict,String genlabeldict,String gensample,ArrayList<String> docs,int topNum,int samlenlimit)
	{
		getDictsAndLabels(field_num,sample_field_index, label_field_index,separator,docs,topNum);
		
		String line="";
		String[] fields=null;
		String label="";
		String text="";
		PrintWriter gspw=null;
		try{	
		gspw=new PrintWriter(new FileWriter(gensample));
		int labelIndex=0;
		String forsample="";
		
		String[] sampleseg=null;
		for(int i=0;i<docs.size();i++)
		{
			line=docs.get(i);
			fields=line.split(separator);
			if(fields.length!=field_num)
			{
				continue;
			}
			
		    label=fields[label_field_index];
		    text=fields[sample_field_index];
		    if(SSO.tioe(label))
		    {
		    	continue;
		    }
		    label=label.trim();
		    labelIndex=0;
		    labelIndex=labels.get(label);
		    if(labelIndex<1)
		    {
		    	continue;
		    }
		    
		    if(SSO.tioe(text))
		    {
		    	continue;
		    }
		    text=text.trim();
		    forsample=get_word_id(text);
		    if(SSO.tioe(forsample))
		    {
		    	continue;
		    }
		    forsample=forsample.trim();
		    
		    sampleseg=forsample.split("\\s+");
		    if(sampleseg.length<samlenlimit)
		    {
		    	continue;
		    }
		    
		    gspw.println(labelIndex+outputSeparator+forsample);
		    
		}
		 gspw.close();
		 printDicts(gendict,outputSeparator);
		 printLabelDicts(genlabeldict,outputSeparator);
		}catch(Exception e){
		  e.printStackTrace();	
		}
		
	}
	
	public void printDicts(String gendict,String outputSeparator)
	{
		try{
			PrintWriter gdpw=new PrintWriter(new FileWriter(gendict));
			for(Map.Entry<String, Integer> e:dicts.entrySet())
			{
				gdpw.println(e.getKey()+outputSeparator+e.getValue());
			}
			gdpw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void printLabelDicts(String genlabeldict,String outputSeparator)
	{
		try{
			PrintWriter glpw=new PrintWriter(new FileWriter(genlabeldict));
			for(Map.Entry<String, Integer> e:labels.entrySet())
			{
				glpw.println(e.getKey()+outputSeparator+e.getValue());
			}
			glpw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
	
		if(args.length!=9)
		{
			System.err.println("Usage:<field_num> <sample_field_index> <label_field_index> <separator> <gendict> <genlabeldict> <gensample> <topNum> <samlenlimit>");
			System.err.println("    field_num : 输入的字段个数");
			System.err.println("    sample_field_index: 样本体所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    label_field_index: 标记所在的字段，从0开始，即0表示第一个字段");
			System.err.println("    separator:字段间的分隔符，001 表示 字符001，blank 表示\\s+ 即连续空格 ,tab 表示 \t");
			System.err.println("    gendict:生成的词典路径,从1开始");
			System.err.println("    genlabeldict:生成的标记索引路径，从1开始");
			System.err.println("    gensample:生成的样本路径");
			System.err.println("    topNum : 每个类别保留的最大单词数");
			System.err.println("    samlenlimit: 样本的最小长度");
			System.exit(1);
		}
		
		
		//输入的字段个数用
		int fieldNum=0;
		
		//样本体所在的字段编号
		int sampleFieldIndex=0;		
		int labelFieldIndex=0;
		
		//字段间的分隔符:001 表示 \001 :blank 表示\\s+ 即连续空格
		String separator="";
		String outputSeparator="";
		
		String gendict="";
		String genlabeldict="";
		String gensample="";
		
		fieldNum=Integer.parseInt(args[0]);
		sampleFieldIndex=Integer.parseInt(args[1]);
		labelFieldIndex=Integer.parseInt(args[2]);
		
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
			separator=args[3].trim();
			outputSeparator=separator.trim();
		}	
		
		gendict=args[4].trim();
		genlabeldict=args[5].trim();
		gensample=args[6].trim();
		
		int topNum=0;
		MetricsSampler sampler=new MetricsSampler();
		
		InputStreamReader isr=new InputStreamReader(System.in);
		BufferedReader br=new BufferedReader(isr);
		topNum=Integer.parseInt(args[7]);
		
		int samlenlimit=0;
		samlenlimit=Integer.parseInt(args[8]);
		//String line="";
		//while((line=br.readLine())!=null)
		//{
		//	pw.println(posTagger.tag(line));
		//}
		
		String line="";
		String[] fields=null;
		ArrayList<String> docs=new ArrayList<String>();
		int c=0;
		
		while((line=br.readLine())!=null)
		{
			c++;
			if(c<2)
			{
				continue;
			}
		
		   if(SSO.tioe(line))
		   {
			   continue;
		   }
		   line=line.trim();
		   docs.add(line);
		}
		
		isr.close();
		br.close();
		
		sampler.train2sample(fieldNum, sampleFieldIndex,labelFieldIndex, separator, outputSeparator, gendict, genlabeldict, gensample, docs,topNum,samlenlimit);
		
	}
	
}
