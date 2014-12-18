package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.hbase.util.Base64;
 
import redis.clients.jedis.Jedis;

public class CLKPreprocessMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();


		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] seg_arr = (value.toString()).split("\001");

			String oid = "";
			String ip = "";
			String curl = "";
			String ctext = "";
			String url = "";
			String refer = "";
			String title = "";
			String time = "";
			String chost = "";
			String host = "";

			String cate = "";
			if (seg_arr != null && seg_arr.length == 10) {
				oid = seg_arr[0].trim();
				ip = seg_arr[1].trim();
				curl = seg_arr[2].trim();
				ctext = seg_arr[3].trim();
				ctext=new String(Base64.decode(ctext));
				url = seg_arr[4].trim();
				refer = seg_arr[5].trim();
				title = seg_arr[6].trim();
				title=new String(Base64.decode(title));
				time = seg_arr[7].trim();
				chost = seg_arr[8].trim();
				host = seg_arr[9].trim();
				if ((host != null) && (!host.equals(""))) {	
						word.set(oid);
						word1.set("\001" + curl + "\001" + ctext + "\001" + url
								+ "\001" + title+"\001"+host);
						context.write(word, word1);
				}
			}

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
			System.err.println("Usage: CLKPreprocessMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "CLKPreprocessMR_" + day);
		job.setJarByClass(CLKPreprocessMR.class);
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
