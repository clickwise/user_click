package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class BKWSortKeyWordsMR {

	private static final Logger log = LoggerFactory.getLogger(BKWSortKeyWordsMR.class);
	private static class PrepareMapper extends Mapper<Object,Text,IntWritable,Text>{

		private IntWritable word=new IntWritable(); 
		private Text word1=new Text();
		
		public void map(Object key,Text value,Context context)
		    throws IOException,InterruptedException{


              String[] seg_arr=(value.toString()).split("\001");
              int temp_int=0;
              if(seg_arr!=null&&seg_arr.length>1)
              {
              temp_int=Integer.parseInt(seg_arr[1]);
              }
              
              word.set(10000000-temp_int);
              
              String v=seg_arr[0];

              word1.set(v.trim());
			  context.write(word, word1);

		}				
	}
	
	
	
	private static class PrepareReducer extends Reducer<IntWritable,Text,Text,Text>
	{
		private Text result=new Text();
		private Text result_r=new Text();
		private Text pvs_key_text=new Text();
		private IntWritable pvs_key=new IntWritable();
	    
		protected void reduce(IntWritable key,Iterable<Text> values,Context context) throws IOException,InterruptedException{
			pvs_key.set(10000000-Integer.parseInt(key.toString()));
		    pvs_key_text.set((10000000-Integer.parseInt(key.toString()))+"");

			Iterator<Text> it=values.iterator();	
		
			String temp_s="";
			if(it!=null)
			{
			while(it.hasNext())
			{
			   result=it.next();
			   
			    if(result!=null)
			    {	
			    temp_s=result.toString();
			    if(temp_s!=null)
			    {
			    result.set((temp_s.trim()));	
				context.write(result,pvs_key_text);
			    }
			    }
			   
			}
			}
			
		}	
	}
	
	
public static void main(String[] args) throws Exception{
		
		Configuration conf=new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
		.getRemainingArgs();
		if(otherArgs.length!=3){
			System.err.println("Usage: BKWSortKeyWordsMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day=otherArgs[0];
		Job job=new Job(conf,"BKWSortKeyWordsMR_"+day);
		job.setJarByClass(BKWSortKeyWordsMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}
	
	
	
	
	
	
}
