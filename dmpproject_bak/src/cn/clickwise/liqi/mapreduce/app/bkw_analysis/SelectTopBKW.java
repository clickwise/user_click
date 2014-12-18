package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;


public class SelectTopBKW {

	private static class PrepareMapper extends Mapper<Object,Text,Text,Text>{
		private Text word=new Text(); 
		private Text word1=new Text();
		
		public void map(Object key,Text value,Context context)
		    throws IOException,InterruptedException{
			String val=value.toString();
			String arr[] =val.split("\001");
			String host="";
			String url="";
			String anchor_text="";
			String pvs="";
			String uvs="";
			String ips="";			
			if(arr.length==6)
			{
				host=arr[0];
				url=arr[1];
				anchor_text=arr[2];
				pvs=arr[3];
				uvs=arr[4];
				ips=arr[5];
				word.set(anchor_text);
				word1.set(host+"\001"+url+"\001"+pvs+"\001"+uvs+"\001"+ips);
				context.write(word, word1);
								
			}
						
		}				
	}
	
	private static class PrepareReducer extends Reducer<IntWritable,Text,IntWritable,Text>
	{
		private Text result=new Text();
		private Text result_r=new Text();
		private IntWritable pvs_key=new IntWritable();
	    
		protected void reduce(IntWritable key,Iterable<Text> values,Context context) throws IOException,InterruptedException{
			pvs_key.set(1000000-Integer.parseInt(key.toString()));

			Iterator<Text> it=values.iterator();	
		
			if(it!=null)
			{
			while(it.hasNext())
			{
			   result=it.next();
			   
			
				context.write(pvs_key, result);
			   
			}
			}
			
		}	
	}
	
	
	
	
}
