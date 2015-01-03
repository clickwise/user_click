package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 计算词典中每个单词指标值
 * @author lq
 */
public abstract class Metrics {

	public abstract HashMap<String,Double> getWordMetrics(int field_num,int sample_field_index,int label_field_index,String separator,ArrayList<String> docs,HashMap<String,Integer> dicts,HashMap<String,Integer> labels);
    
}
