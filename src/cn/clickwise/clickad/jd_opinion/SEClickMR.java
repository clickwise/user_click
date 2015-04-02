package cn.clickwise.clickad.jd_opinion;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

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
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.lib.string.SSO;
import cn.clickwise.smartjobs.TestMR;
import cn.clickwise.smartjobs.TestMR.PrepareMapper;
import cn.clickwise.smartjobs.TestMR.PrepareReducer;

public class SEClickMR {

	private static class PPrepareMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		
		private Text word = new Text();
		private static IntWritable valueOne = new IntWritable(1);

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String val = value.toString();
			ParseResult pr = null;
			pr = SEUrlParse.parseItem(val);
		
		    if (pr.isNull()) {
					return;
			}
			pr.decode();

			if (pr.isInValid()) {
					return;
			}
			
			String rs=pr.toString();
			
			String[] tokens=rs.split(";");
			if(tokens==null||tokens.length<1)
			{
				return;
			}
			
			String token="";
			for(int i=0;i<tokens.length;i++)
			{
				token=tokens[i];
				if(SSO.tioe(token))
				{
					continue;
				}
				token=token.trim();
				word.set(token);
				context.write(word, valueOne);
			}
			

		}
	}

	private static class PPrepareReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private Text result = new Text();
		private Text word_key = new Text();

		protected void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			String keyword = key.toString();

			Iterator<IntWritable> it = values.iterator();
			int sum=0;
			while (it.hasNext())
	        {
	            sum += it.next().get();
	        }
			context.write(key, new IntWritable(sum));
		}
	}
	
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: TestMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "TestMR_" + day);
		job.setJarByClass(TestMR.class);

		job.setMapperClass(PPrepareMapper.class);
		job.setReducerClass(PPrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}

}
