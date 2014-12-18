package cn.clickwise.gaodong.radius;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class NstatIpMR {
	private static class IpMapper1 extends Mapper<Object, Text, Text, IntWritable>{
		private final static IntWritable one = new IntWritable(1);

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line=value.toString().trim();
			Text loc=new Text();
			String regex="\001";
			if(line!=null&&line.contains(regex)){
				String[] local = line.split(regex);
				if (local.length == 11) {				
					String browser=local[10].toUpperCase();
					if(browser.contains("IPHONE")||browser.contains("IPOD")
							||browser.contains("ANDROID")||browser.contains("PHONE")||browser.contains("MOBILE")
							||browser.contains("OPERA MINI")||browser.contains("XIAO MI")||browser.contains("HUA WEI")||browser.contains("MIUI")) {
						loc.set("手机\t"+local[3]+"\t"+local[9]);
					}else if(browser.contains("IPAD")){
						loc.set("IPAD\t"+local[3]+"\t"+local[9]);
					}
						context.write(loc, one);
					 }
			}
		}
		
		
	}
	
	private static class IpMapper2 extends Mapper<Object, Text, Text, IntWritable>{
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line=value.toString().trim();
			Text loc=new Text();
			IntWritable ipNum=new IntWritable();
			String regex="\t";
			if(line!=null&&line.contains(regex)){
				String[] local = line.split(regex);
				loc.set(local[0]+"\t"+local[2]);
				ipNum.set(Integer.parseInt(local[3]));
				context.write(loc, ipNum);
			}
		}
		
		
	}
	
	private static class IpReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		private IntWritable result = new IntWritable();
		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
			
		}
		
	}
	public static void main(String[] args) throws Throwable {
		Configuration config = new Configuration();
		String[] otherArgs = new GenericOptionsParser(config, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.out.println("Usage: NstatIpMR <input> <output>");
			System.exit(2);
		}
		Job job=new Job(config,"Ip");
		job.setJarByClass(NstatIpMR.class);
		job.setMapperClass(IpMapper1.class);
		job.setReducerClass(IpReducer.class);
		job.setCombinerClass(IpReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		job.waitForCompletion(true);
		Job job1 = new Job(config,"job1");
		job1.setJarByClass(NstatIpMR.class);
		job1.setMapperClass(IpMapper2.class);
		job1.setReducerClass(IpReducer.class);
		job1.setCombinerClass(IpReducer.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job1, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job1, new Path(otherArgs[2]));
		
		System.exit(job1.waitForCompletion(true) ? 0 : 1);
		

	}


}
