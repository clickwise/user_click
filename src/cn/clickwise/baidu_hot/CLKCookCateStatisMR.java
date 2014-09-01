package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.util.Base64;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class CLKCookCateStatisMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");

			String title = "";
			String gender_cate = "";
			String cookie_str = "";
			String[] seg_tmp = null;
			String cookie = "";
			String cate = "";
			if (seg_arr != null && seg_arr.length == 3) {
				title = seg_arr[0].trim();
				gender_cate = seg_arr[1].trim();
				cookie_str = seg_arr[2].trim();

				if ((title != null) && (!title.equals(""))
						&& (cookie_str != null) && (!cookie_str.equals(""))) {
					seg_tmp = cookie_str.split("\\s+");
					if (seg_tmp.length > 0) {
						for (int j = 0; j < seg_tmp.length; j++) {
							cookie = seg_tmp[j].trim();
							if ((cookie != null) && (!cookie.equals(""))) {
								word.set(cookie);
								word1.set(gender_cate + " " + title);
								context.write(word, word1);
							}
						}
					}

				}
			}

		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
			String gt_str = "\001";
			String one_gt = "";
			int his_num=0;
			while (it.hasNext()) {
				one_gt = it.next().toString();
				one_gt = one_gt.trim();
				if ((one_gt == null) || (one_gt.equals(""))) {
					continue;
				}
				gt_str = gt_str + one_gt + "\001";
                his_num++;
			}
			gt_str = gt_str.trim();
			result.set(gt_str);
			if(his_num>1)
			{
			context.write(key, result);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err
					.println("Usage: CLKCookCateStatisMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "CLKCookCateStatisMR_" + day);
		job.setJarByClass(CLKCookCateStatisMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}
}
