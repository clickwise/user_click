package cn.clickwise.liqi.mapreduce.app.radius_analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.crawler.basic.UrlStatusTest;
import cn.clickwise.liqi.math.random.RandomGen;
import cn.clickwise.liqi.str.basic.SSO;

public class SplitFileMR {

	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
					
             String host=value.toString();
             if(SSO.tnoe(host))
             {
            	 host=host.trim();
                 word.set(host);
                 word1.set("");
                 context.write(word, word1);             
             }          
		}
			
	}
	

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			 Iterator<Text> it = values.iterator(); 
			  while(it.hasNext())
	      	  {
	      	     context.write(key, it.next());
	      	  }
			 
			 /*
			 ArrayList<String> url_list=new ArrayList<String>();
			 String host="";
			 String mulhost="";
      	     while(it.hasNext())
      	     {
      	    	 host=it.next().toString();
      	    	 if(!(SSO.tnoe(host)))
      	    	 {
      	    		 continue;
      	    	 }
      	    	 host=host.trim();
      	    	 url_list.add(host);
      	    	 if(url_list.size()>10)
      	    	 {
      	    		 mulhost=SSO.implode(url_list, "\001");
          	    	 if(SSO.tnoe(mulhost))
          	    	 {
          	    		 mulhost=mulhost.trim();
          	    	     result_key.set(mulhost);
          	    	     result.set("");
          		    	 context.write(result_key, result);
          	    	 } 		 
          	    	url_list=new ArrayList<String>();
      	    	 }    	 
      	     }
      	     
	    	 mulhost=SSO.implode(url_list, "\001");
      	     if(SSO.tnoe(mulhost))
      	     {
      	    		 mulhost=mulhost.trim();
      	    	     result_key.set(mulhost);
      	    	     result.set("");
      		    	 context.write(result_key, result);
      	    } 		 
      	    */
  	     
		}
	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SplitFileMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "SplitFileMR_" + day);
		job.setJarByClass(SplitFileMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(100);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
