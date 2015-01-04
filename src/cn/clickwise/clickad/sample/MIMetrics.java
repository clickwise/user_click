package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import love.cq.util.MapCount;

import cn.clickwise.lib.string.SSO;

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
			HashMap<String, Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts) {
		   double N=docs.size();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, HashMap<String, Double>> getCateWordMetrics(
			int field_num, int sample_field_index, int label_field_index,
			String separator, ArrayList<String> docs,
			HashMap<String, Integer> dicts, HashMap<String, Integer> labels,MapCount<String> dictCounts,MapCount<String> labelCounts) {
		
		double N=docs.size();
		
		//第一级索引类别c，第二级索引单词t ,t and c co-occur
		HashMap<String,HashMap<String,Double>> AH=new HashMap<String,HashMap<String,Double>>();
		
		//第一级索引类别c，第二级索引单词t ,t occurs without c
		HashMap<String,HashMap<String,Double>> BH=new HashMap<String,HashMap<String,Double>>();
		
		//第一级索引类别c，第二级索引单词t, c occurs without t
		HashMap<String,HashMap<String,Double>> CH=new HashMap<String,HashMap<String,Double>>();
		
		String line="";
		String[] fields=null;
		
		//类别
		String label="";
		
		String text="";
		String[] tokens=null;	
		int dict_index = 1;
		int label_index = 1;
		
		//单词
		String token="";
		
		ArrayList<String> otherWords=null;
		ArrayList<String> otherCates=null;
		
		String word="";
		
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
		    
		    if(SSO.tioe(text))
		    {
		    	continue;
		    }
		    text=text.trim();
		    
		    tokens=text.split("\\s+");
		    otherWords=getWordsNotInDoc(dicts,tokens,dictCounts);
		    
		    //更新CH
		    for(int j=0;j<otherWords.size();j++)
		    {
		        word=otherWords.get(j);
		        if(SSO.tioe(word))
		        {
		        	continue;
		        }
		        word=word.trim();
		        if(!(CH.containsKey(label)))
		        {
		        	CH.put(label, new HashMap<String,Double>());
		        	if(!(CH.get(label).containsKey(word)))
		        	{
		        		CH.get(label).put(word, 1.0);
		        	}
		        	else
		        	{
		        		CH.get(label).put(word, CH.get(label).get(word)+1);
		        	}
		     
		        }
		        else
		        {
		        	if(!(CH.get(label).containsKey(word)))
		        	{
		        		CH.get(label).put(word, 1.0);
		        	}
		        	else
		        	{
		        		CH.get(label).put(word, CH.get(label).get(word)+1);
		        	}  	
		        }
		        
		    }
		    
		    
		    
		    otherCates=getCatesNotThis(labels,label);
		    MapCount<String> tc=new MapCount<String>();
		    for(int j=0;j<tokens.length;j++)
		    {
		      token=tokens[j];
		      if(SSO.tioe(token))
		      {
		    	  continue;
		      }
		      token=token.trim();
		      tc.add(token);
		    }
		    
		    //更新BH
		    String tempc="";
		    String tempw="";
		    
		    for(Map.Entry<String, Integer> m:tc.get().entrySet())
		    {
		    	tempw=m.getKey();
		       	for(int k=0;k<otherCates.size();k++)
		       	{
		       		tempc=otherCates.get(k);
		       		
			        if(!(BH.containsKey(tempc)))
			        {
			        	BH.put(tempc, new HashMap<String,Double>());
			        	if(!(BH.get(tempc).containsKey(tempw)))
			        	{
			        		BH.get(tempc).put(tempw, 1.0);
			        	}
			        	else
			        	{
			        		BH.get(tempc).put(tempw, BH.get(tempc).get(tempw)+1);
			        	}
			     
			        }
			        else
			        {
			        	if(!(BH.get(tempc).containsKey(tempw)))
			        	{
			        		BH.get(tempc).put(tempw, 1.0);
			        	}
			        	else
			        	{
			        		BH.get(tempc).put(tempw, BH.get(tempc).get(tempw)+1);
			        	}  	
			        }
		       	}
		    }
		    
		    
		    
		}
		
		return null;
	}
	
	
	
	
	

}
