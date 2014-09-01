package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BKWMergeNVMR {

	private static final Logger log = LoggerFactory
			.getLogger(BKWMergeNVMR.class);

	private static class PrepareMapper extends
			Mapper<Object, Text, IntWritable, Text> {

		private IntWritable word = new IntWritable();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");
			int temp_int = 0;
			if (seg_arr != null && seg_arr.length > 2) {
				temp_int = Integer.parseInt(seg_arr[2]);
			}

			word.set(10000000 - temp_int);

			context.write(word, value);

		}
	}

	private static class PrepareReducer extends
			Reducer<IntWritable, Text, Text, Text> {
		private Text result = new Text();
		private Text result_r = new Text();
		private Text pvs_key_text = new Text();

		protected void reduce(IntWritable key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {

			pvs_key_text
					.set((10000000 - Integer.parseInt(key.toString())) + "");
			Iterator<Text> it = values.iterator();

			String temp_s = "";
			String keyword = "";
			String keyword_info = "";
			String[] seg_arr = null;
			String cate = "";
			if (it != null) {
				while (it.hasNext()) {
					result = it.next();
					if (result != null) {
						temp_s = result.toString();
						if (temp_s != null) {
							temp_s = temp_s.trim();
							seg_arr = temp_s.split("\001");

							if (seg_arr.length > 2) {
								keyword = seg_arr[0].trim();
								cate = seg_arr[1].trim();
								keyword_info = "\001";
								for (int oj = 1; oj < seg_arr.length; oj++) {
									keyword_info = keyword_info
											+ seg_arr[oj].trim() + "\001";
								}
								//keyword_info = keyword_info.trim();
								if ((cate.equals("小说")) ||(cate.equals("新闻资讯")) || (cate.equals("电影"))
										|| (cate.equals("影视剧"))
										|| (cate.equals("综艺"))) {
									result = new Text();
									pvs_key_text = new Text();
									result.set(keyword);
									pvs_key_text.set(keyword_info);
									context.write(result, pvs_key_text);
								}
							}
						}
					}

				}
			}

		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err.println("Usage: BKWMergeNVMR <day> <preli_output> <video_output> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "BKWMergeNVMR_" + day);
		job.setJarByClass(BKWMergeNVMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileInputFormat.addInputPath(job, new Path(otherArgs[2]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[3]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
