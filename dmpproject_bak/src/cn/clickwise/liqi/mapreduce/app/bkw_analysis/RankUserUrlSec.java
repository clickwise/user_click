package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RankUserUrlSec {

	private static final Logger log = LoggerFactory.getLogger(RankUserUrlSec.class);
	private static class PrepareMapper extends Mapper<Object,Text,IntWritable,Text>{

		private IntWritable word=new IntWritable(); 
		private Text word1=new Text();
		
		public void map(Object key,Text value,Context context)
		    throws IOException,InterruptedException{


              String[] seg_arr=(value.toString()).split("\t");
              int temp_int=0;
              if(seg_arr!=null&&seg_arr.length>0)
              {
              temp_int=Integer.parseInt(seg_arr[0]);
              }
              
              word.set(temp_int);
              
              String v="";
              if(seg_arr.length>1)
              {
                for(int i=1;i<seg_arr.length;i++)
                {
            	  v+=seg_arr[i]+" ";  
                }
            	
              }
              word1.set(v);
			  context.write(word, word1);

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
	
	
public static void main(String[] args) throws Exception{
		
		Configuration conf=new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
		.getRemainingArgs();
		if(otherArgs.length!=3){
			System.err.println("Usage: RankUserUrl <day> <input> <output>");
			System.exit(2);
		}
		
		String day=otherArgs[0];
		Job job=new Job(conf,"RankUserUrlSec_"+day);
		job.setJarByClass(RankUserUrlSec.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}
	
	
}
