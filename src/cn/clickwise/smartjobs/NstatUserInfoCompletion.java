package cn.clickwise.smartjobs;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.time.utils.TimeOpera;
import cn.clickwise.segmenter.stanford.StanterSeg;

public class NstatUserInfoCompletion {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {
		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day = "";
		public StanterSeg ss;
		
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
            String[] seg_arr=null;
            seg_arr=line.split("\001");
			if(seg_arr.length==2)
			{
				String host="";
				String cate="";
				host=seg_arr[0].trim();
				cate=seg_arr[1].trim();
				if(SSO.tnoe(host))
				{
				  word.set(host);
				  word1.set(cate);
				  context.write(word, word1);
				}
			}
			else if(seg_arr.length==3)
			{
				String host="";
				String title_seg="";
				String mark="";
				
				host=seg_arr[0].trim();
				title_seg=seg_arr[1].trim();
				mark=seg_arr[2].trim();
				
				if(SSO.tnoe(host))
				{
				  word.set(host);
				  word1.set(title_seg+"\001"+mark);
				  context.write(word, word1);
				}
			}
			else if(seg_arr.length==7)
			{
				String host="";
				String url="";
				String refer_host="";
				String refer_url="";
				String link_word="";
				String ltime="";
				String only_cookie="";
				
				host=seg_arr[0].trim();
				url=seg_arr[1].trim();
				refer_host=seg_arr[2].trim();
				refer_url=seg_arr[3].trim();
				link_word=seg_arr[4].trim();
				ltime=seg_arr[5].trim();
				only_cookie=seg_arr[6].trim();
				if(SSO.tnoe(host))
				{
				  word.set(host);
				  word1.set(url+"\001"+refer_host+"\001"+refer_url+"\001"+link_word+"\001"+ltime+"\001"+only_cookie);
				  context.write(word, word1);	
				}
			}		
		}//map		
	}//PrepareMapper

	private static class PrepareReducer extends
			Reducer<Text, Text, Text, NullWritable> {
	    //Reducer<Text, Text, Text, Text> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			String host = key.toString().trim();
			String cate="NA";
			String title_seg="NA";
			Vector<String> records=new Vector();
			
			Iterator<Text> it = values.iterator();
			String line="";
		    String[] seg_arr=null;
		    
		    /*****搜索cate和 title_seg******/
			while(it.hasNext())
			{
			  line=it.next().toString();
			  if(SSO.tioe(line))
			  {
				  continue;
			  }
			  seg_arr=line.split("\001");
			  if(seg_arr.length==1)
			  {
				  cate=line.trim();
			  }
			  else if(seg_arr.length==2)
			  {
				  title_seg=seg_arr[1].trim();
			  }
			  else if(seg_arr.length==6)
			  {
				  records.add(line);  
			  }  
			}
			
			String url="";
			String refer_host="";
			String refer_url="";
			String link_word="";
			String ltime="";
			String only_cookie="";
			for(int i=0;i<records.size();i++)
			{
				line=records.get(i);
				seg_arr=line.split("\001");
				if(seg_arr.length==6)
				{
				  url=seg_arr[0].trim();
				  refer_host=seg_arr[1].trim();
				  refer_url=seg_arr[2].trim();
				  link_word=seg_arr[3].trim();
				  ltime=seg_arr[4].trim();
				  only_cookie=seg_arr[5].trim();
				  String info=host+"\001"+cate+"\001"+url+"\001"+link_word+"\001"+title_seg+"\001"+refer_host+"\001"+refer_url+"\001"+ltime+"\001"+only_cookie;
				  if(SSO.tnoe(info))
				  {
				    result.set(info);					   
				    context.write(result, NullWritable.get());
				  }
				}				
			}						
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 5) {
			System.err.println("Usage: NstatUserInfoCompletion <day> <input:host_cate> <input:host_title_seg_mark> <input:nstat_analysis> <output>");
			System.exit(1);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "NstatUserInfoCompletion_" + day);
		job.setJarByClass(NstatUserInfoCompletion.class);
		PrepareMapper.request_day = day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(100);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		//job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileInputFormat.addInputPath(job, new Path(otherArgs[2]));
		FileInputFormat.addInputPath(job, new Path(otherArgs[3]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[4]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
