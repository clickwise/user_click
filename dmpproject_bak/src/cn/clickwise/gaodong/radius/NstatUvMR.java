package cn.clickwise.gaodong.radius;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

public class NstatUvMR {
	private static class UvMapper extends Mapper<Object, Text, Text, Text>{
		
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			Text ip=new Text();
			Text val=new Text();
			String line=value.toString().trim();
			String[] itr = line.split("\001");
			if(itr[itr.length-1].trim().equals("IDU")){
					ip.set(itr[0]);
					val.set(value);
			}else{
					if (itr.length == 11) {				
						String browser=itr[10].toUpperCase();
						if(browser.contains("IPHONE")||browser.contains("IPOD")
								||browser.contains("ANDROID")||browser.contains("PHONE")||browser.contains("MOBILE")
								||browser.contains("OPERA MINI")||browser.contains("XIAO MI")||browser.contains("HUA WEI")||browser.contains("MIUI")) {
							ip.set(itr[3]);
							
							val.set(itr[9]+"\001手机");
						}else if(browser.contains("IPAD")){
							ip.set(itr[3]);
							val.set(itr[9]+"\001IPAD");
							
							
						}
					}
				}
			context.write(ip, val);
			
		}
		
	}
	
private static class UvMapper2 extends Mapper<Object, Text, Text, IntWritable>{		
		private final static IntWritable one = new IntWritable(1);
		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			Text text =new Text();
			String line=value.toString().trim();
			String[] itr=line.split("\001");
			if(line.contains("手机")){
				for(String str: itr){
					String []uv=str.split("\t");
					if(uv.length>2){
						if(uv[0].trim().equals("")){
							text.set(uv[1]+"\001"+itr[itr.length-1]+itr[itr.length-2]);
						}else{
							text.set(uv[0]+"\001"+itr[itr.length-1]+itr[itr.length-2]);
						}
						
						context.write(text, one);
					}
				}		
			}else if(line.contains("IPAD")){
				for(String str: itr){
					String []uv=str.split("\t");
					if(uv.length>2){
						if(uv[0].trim().equals("")){
							text.set(uv[1]+"\001"+itr[itr.length-1]+itr[itr.length-2]);
						}else{
							text.set(uv[0]+"\001"+itr[itr.length-1]+itr[itr.length-2]);
						}
						context.write(text, one);
					}
				}	
				
			}
			
		}
		
	}
private static class UvMapper3 extends Mapper<Object, Text, Text, IntWritable>{	
	Text loc=new Text();
	IntWritable num=new IntWritable();
	@Override
	protected void map(Object key, Text value, Context context)
			throws IOException, InterruptedException {
		String str=value.toString().trim();
		String [] itr=str.split("\001");
		String[] itr2=itr[1].split("\t");
		loc.set(itr2[0]+"\t"+itr2[1]);
		num.set(Integer.parseInt(itr2[2]));
		context.write(loc, num);
	}
	
}

	private static class UvReducer extends Reducer<Text, Text, Text, Text>{
		private Text result = new Text();
		@Override
		protected void reduce(Text key, Iterable<Text> values,Context context)
				throws IOException, InterruptedException {
			String idu="";
			String nstat="";
			for (Text val : values) {
				if(val.toString().contains("IDU")){
					idu=val.toString().trim();
				}else{
					nstat=val.toString().trim();
				}
			}
				result.set(idu+"\t"+nstat);
				context.write(key, result);
			
		}
		
	}	
	private static class UvReducer2 extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			if((key.toString().contains("手机")||key.toString().contains("IPAD"))&&key.toString().contains("IDU")){
				context.write(key, result);
			}
			
		}

	}
	private static class UvReducer3 extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
			
		}

	}
	public static void main(String[] args) throws Throwable{
		Configuration config = new Configuration();
		String[] otherArgs = new GenericOptionsParser(config, args)
				.getRemainingArgs();
		if (otherArgs.length != 5) {
			System.out.println("Usage: NstatIpMR <input> <output>");
			System.exit(2);
		}
		Job job=new Job(config,"Uv");
		job.setJarByClass(NstatUvMR.class);
		job.setMapperClass(UvMapper.class);
		job.setReducerClass(UvReducer.class);
		job.setCombinerClass(UvReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		
		job.waitForCompletion(true);
		Job job1 = new Job(config,"job1");
		job1.setJarByClass(NstatUvMR.class);
		job1.setMapperClass(UvMapper2.class);
		job1.setReducerClass(UvReducer2.class);
		job1.setCombinerClass(UvReducer2.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job1, new Path(otherArgs[2]));
		FileOutputFormat.setOutputPath(job1, new Path(otherArgs[3]));
		
		job1.waitForCompletion(true);
		Job job2 = new Job(config,"job2");
		job2.setJarByClass(NstatUvMR.class);
		job2.setMapperClass(UvMapper3.class);
		job2.setReducerClass(UvReducer3.class);
		job2.setCombinerClass(UvReducer3.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job2, new Path(otherArgs[3]));
		FileOutputFormat.setOutputPath(job2, new Path(otherArgs[4]));
		
		System.exit(job2.waitForCompletion(true) ? 0 : 1);
		
	}

}
