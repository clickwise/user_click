package cn.clickwise.gaodong.mapreduce.browser_analysis;

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


public class BrowserAnalysisMR {
	private static class PrepareMaper extends
			Mapper<Object, Text, Text, IntWritable> {
		private Text word = new Text();
		private final static IntWritable one = new IntWritable(1);

		@Override
		protected void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			String regex = "\001";
			if (line != null && line.contains(regex)) {
				String[] browser_line = line.split(regex);
				if (browser_line.length == 11) {
					String browser=browser_line[10].toUpperCase();
					if (browser.contains("IPHONE")||browser.contains("IPAD")
							||browser.contains("IPOD")||browser.contains("ANDROID")
							||browser.contains("PHONE")||browser.contains("MOBILE")
							||browser.contains("OPERA MINI")) {
							word.set("移动端：");
							context.write(word, one);
					}
					else{
							word.set("PC端：");
							context.write(word, one);
					}

				}

			}

		}
	}

	private static class PrepareReduce extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
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
		if (otherArgs.length != 2) {
			System.out.println("Usage: BrowserAnalysisMR <input> <output>");
			System.exit(2);
		}

		Job job = new Job(config);
		job.setJarByClass(BrowserAnalysisMR.class);

		job.setMapperClass(PrepareMaper.class);
		job.setCombinerClass(PrepareReduce.class);
		job.setReducerClass(PrepareReduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
