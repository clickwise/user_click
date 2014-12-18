package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class IACCPreprocessMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String line = value.toString();
			String pat_str = "^(\\S+) (\\S+) (\\S+) \\[([^:]+):(\\d+:\\d+:\\d+) ([^\\]]+)\\] \"(\\S+) (.*?) (\\S+)\" (\\S+) (\\S+) (\".*?\") (\".*?\") (\".*?\")$";
			Pattern pat = Pattern.compile(pat_str);
			Matcher mat = pat.matcher(line);

			String ip = "";
			String identity = "";
			String user = "";
			String date = "";
			String time = "";
			String timezone = "";
			String method = "";
			String path = "";
			String protocal = "";
			String status = "";
			String bytes = "";
			String referer = "";
			String agent = "";
			String cookie = "";

			ip = mat.group(1);
			identity = mat.group(2);
			user = mat.group(3);
			date = mat.group(4);
			time = mat.group(5);
			timezone = mat.group(6);
			method = mat.group(7);
			path = mat.group(8);
			protocal = mat.group(9);
			status = mat.group(10);
			bytes = mat.group(11);
			referer = mat.group(12);
			agent = mat.group(13);
			cookie = mat.group(14);

			word.set("");
			word1.set("");
			context.write(word, word1);

		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();

			while (it.hasNext()) {
				context.write(key, it.next());
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err
					.println("Usage: IACCPreprocessMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "IACCPreprocessMR_" + day);
		job.setJarByClass(IACCPreprocessMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(4);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
