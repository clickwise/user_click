package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 交互信息标准
 * I(t,c)~=log(A*N/((A+C)*(A+B)))
 * t term 
 * c category
 *  A: the number of times t and c co-occur
 *  B: the number of times the t occurs without c
 *  C: the number of times c occurs without t　 
 *  N: the total number of documents
 *  Iavg(t)=sum(i:1~m)[p(ci)I(t,ci)]
 *  Imax(t)=max(i:1~m)[I(t,ci)]
 * @author lq
 */
public class MIMetrics extends Metrics{

	@Override
	public HashMap<String, Double> getWordMetrics(int field_num,
			int sample_field_index, int label_field_index, String separator,
			ArrayList<String> docs, HashMap<String, Integer> dicts,
			HashMap<String, Integer> labels) {
		// TODO Auto-generated method stub
		return null;
	}

}
