package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;

import love.cq.util.MapCount;


/**
 * chi square 标准
 * CHI(t,c)=N*(AD-CB)^2/[(A+C)(B+D)(A+B)(C+D)]
 * t term 
 * c category
 *  A: the number of times t and c co-occur
 *  B: the number of times the t occurs without c
 *  C: the number of times c occurs without t　 
 *  D: the number of times neither c nor t occurs
 *  N: the total number of documents
 *  CHIavg(t)=sum(i:1~m)[p(ci)CHI(t,ci)]
 *  CHImax(t)=max(i:1~m)[CHI(t,ci)]
 * @author lq
 */
public class CHIMetrics extends Metrics{

	@Override
	public HashMap<String, Double> getWordMetrics(int field_num,
			int sample_field_index, int label_field_index, String separator,
			ArrayList<String> docs, HashMap<String, Integer> dicts,
			HashMap<String, Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, Double>> getCateWordMetrics(
			int field_num, int sample_field_index, int label_field_index,
			String separator, ArrayList<String> docs,
			HashMap<String, Integer> dicts, HashMap<String, Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts) {
		// TODO Auto-generated method stub
		return null;
	}

}
