package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;

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
	public abstract HashMap<String,Double> getWordMetrics(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,HashMap<String,Integer> dicts,HashMap<String,Integer> labels);
    
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
	public abstract HashMap<String,HashMap<String,Double>> getCateWordMetrics(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,HashMap<String,Integer> dicts,HashMap<String,Integer> labels);

	public ArrayList<String> getWordsNotInDoc(HashMap<String,Integer> dicts ,String[] words,MapCount<String> dictCounts)
	{
		
		return null;
	}
	
	public ArrayList<String> getCatesNotThis(HashMap<String,Integer> labels ,String c)
	{
		
		return null;
	}
}
