package cn.clickwise.clickad.sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.clickwise.lib.string.SSO;

import love.cq.util.MapCount;

public class DFMetrics extends  Metrics {

	@Override
	public HashMap<String, Double> getWordMetrics(int field_num,
			int sample_field_index, int label_field_index, String separator,
			ArrayList<String> docs, HashMap<String, Integer> dicts,
			HashMap<String, Integer> labels, MapCount<String> dictCounts,
			MapCount<String> labelCounts) {
		
		
		HashMap<String, Double> wm=new HashMap<String, Double>();
		
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
		MapCount<String> wc=new MapCount<String>();
		
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
		    //System.err.println("i="+i+"  :"+text);
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
		    MapCount<String> tc=new MapCount<String>();
		    for(int j=0;j<tokens.length;j++)
		    {
		    	token=tokens[j];
		    	if(SSO.tioe(token))
		    	{
		    		continue;
		    	}
		    	tc.add(token);
		    }
		    
		    for(Map.Entry<String, Integer> m:tc.get().entrySet())
		    {
		    	wc.add(m.getKey());
		    }		    
		}
		
		for(Map.Entry<String, Integer> m:wc.get().entrySet())
		{
			wm.put(m.getKey(), (double)m.getValue());
		}
		
		return wm;
	}

	@Override
	public HashMap<String, HashMap<String, Double>> getCateWordMetrics(
			int field_num, int sample_field_index, int label_field_index,
			String separator, ArrayList<String> docs,
			HashMap<String, Integer> dicts, HashMap<String, Integer> labels,
			MapCount<String> dictCounts, MapCount<String> labelCounts) {
		// TODO Auto-generated method stub
		return null;
	}

}
