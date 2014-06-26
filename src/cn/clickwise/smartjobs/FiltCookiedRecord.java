package cn.clickwise.smartjobs;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
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
import cn.clickwise.liqi.time.utils.TimeOpera;

public class FiltCookiedRecord {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day = "";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String nstat_line = value.toString().trim();
			String area = "";
			String atime = "";
			String pname = "";
			String sip = "";
			String dip = "";
			String host = "";
			String url = "";
			String refer = "";
			String cookie = "";
			String loc = "";
			String[] seg_arr = null;
			seg_arr = nstat_line.split("\001");

			String info = "";

			if (seg_arr.length == 10) {
				area = seg_arr[0].trim();
				atime = seg_arr[1].trim();
				pname = seg_arr[2].trim();
				sip = seg_arr[3].trim();
				dip = seg_arr[4].trim();
				host = seg_arr[5].trim();
				url = seg_arr[6].trim();
				refer = seg_arr[7].trim();
				cookie = seg_arr[8].trim();
				loc = seg_arr[9].trim();
				if ((SSO.tnoe(cookie))&&((cookie.indexOf("NA"))==-1)) {              
					word.set(area);
					info=atime+"\001"+pname+"\001"+sip+"\001"+dip+"\001"+host+"\001"+url+"\001"+refer+"\001"+cookie+"\001"+loc;
					word1.set(info);
					context.write(word, word1);
				}//host
			}//seg_arr.length
		}//map

	}//PrepareMapper

	private static class PrepareReducer extends
			Reducer<Text, Text, Text, NullWritable> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			String key_str = key.toString().trim();
			Iterator<Text> it = values.iterator();
			String info = "";
			if (SSO.tnoe(key_str)) {
				while (it.hasNext()) {
					info = it.next().toString();
					info = info.trim();
					if (SSO.tnoe(info)) {
						result_key.set(key_str + "\001" + info);
						context.write(result_key, NullWritable.get());
					}
				}

			}
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: FiltCookiedRecord <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "FiltCookiedRecord_" + day);
		job.setJarByClass(FiltCookiedRecord.class);
		PrepareMapper.request_day = day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
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
