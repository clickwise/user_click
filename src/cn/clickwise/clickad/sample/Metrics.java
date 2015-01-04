package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jmlp.sort.utils.SortStrArray;

import cn.clickwise.lib.string.SSO;

import love.cq.util.MapCount;

/**
 * 计算词典中每个单词指标值
 * @author lq
 */
public abstract class Metrics {

	/**
	 * 获得每个单词的指标值(综合所有类别)
	 * @param field_num
	 * @param sample_field_index
	 * @param label_field_index
	 * @param separator
	 * @param docs
	 * @param dicts
	 * @param labels
	 * @return
	 */
	public abstract HashMap<String,Double> getWordMetrics(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,HashMap<String,Integer> dicts,HashMap<String,Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts);
    
	/**
	 * 获得各个类别每个单词的指标值
	 * @param field_num
	 * @param sample_field_index
	 * @param label_field_index
	 * @param separator
	 * @param docs
	 * @param dicts
	 * @param labels
	 * @return
	 */
	public abstract HashMap<String,HashMap<String,Double>> getCateWordMetrics(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,HashMap<String,Integer> dicts,HashMap<String,Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts);

	public ArrayList<String> getWordsNotInDoc(HashMap<String,Integer> dicts ,String[] words,MapCount<String> dictCounts)
	{
		ArrayList<String> otherWords=new ArrayList<String>();
		HashMap<String,Integer> docWords=new HashMap<String,Integer>();
		
		String word="";
		for(int i=0;i<words.length;i++)
		{
			word=words[i];
			if(SSO.tioe(word))
			{
				continue;
			}
			
			word=word.trim();
			
			if(!(docWords.containsKey(word)))
			{
			  docWords.put(word, 1);
			}
		}
		
		for(Map.Entry<String, Integer> m:dicts.entrySet())
		{
			if(!(docWords.containsKey(m.getKey())))
			{
				otherWords.add(m.getKey());
			}
		}
		
		return otherWords;
	}
	
	public ArrayList<String> getCatesNotThis(HashMap<String,Integer> labels ,String c)
	{
		ArrayList<String> otherCates=new ArrayList<String>();
		
		for(Map.Entry<String, Integer> m:labels.entrySet())
		{
		  if(!(m.getKey().equals(c)))
		  {
			otherCates.add(m.getKey()); 
		  }
		}
		
		
		return otherCates;
	}
	
	public static HashMap<String,ArrayList<WORD>> sortCateWordsMetrics(HashMap<String,HashMap<String,Double>> cateWordsMetrics)
	{
		
		HashMap<String,ArrayList<WORD>> sortCWM=new HashMap<String,ArrayList<WORD>>();
		for(Map.Entry<String, HashMap<String,Double>> m:cateWordsMetrics.entrySet())
		{
			ArrayList<WORD> wlist=sortHash(m.getValue());
			sortCWM.put(m.getKey(), wlist);
		}
		
		return sortCWM;
	}
	
	public static ArrayList<WORD> sortHash(HashMap<String,Double> wh)
	{
		ArrayList<String> whlist=new ArrayList<String>();
		for(Map.Entry<String, Double> m:wh.entrySet())
		{
			whlist.add(m.getKey()+"\001"+m.getValue());
		}
		
		String[] sorted=SortStrArray.sort_List(whlist, 1, "dou", 2, "\001");
		String word="";
		double weight=0;
		
		String line="";
		String[] tokens=null;
		WORD w=null;
		
		ArrayList<WORD> wordList=new ArrayList<WORD>();
		for(int i=0;i<sorted.length;i++)
		{
		  	line=sorted[i];
		  	if(SSO.tioe(line))
		  	{
		  		continue;
		  	}
		  	
		  	tokens=line.split("\001");
		  	if(tokens.length!=2)
		  	{
		  		continue;
		  	}
		  	
		  	word=tokens[0];
		  	weight=Double.parseDouble(tokens[1]);
		  	w=new WORD(word,weight);
		  	wordList.add(w);
		}
		
		
		return wordList;
	}
	
}
