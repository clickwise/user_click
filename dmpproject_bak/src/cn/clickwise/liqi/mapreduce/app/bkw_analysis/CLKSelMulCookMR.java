package cn.clickwise.liqi.mapreduce.app.bkw_analysis;
import java.io.IOException;
import java.util.Iterator;
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

public class CLKSelMulCookMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");

			String cookie = "";
			String curl = "";
			String ctext = "";
			String url = "";
			String title = "";
			String host = "";

			String cate = "";
			if (seg_arr != null && seg_arr.length == 7) {
				cookie = seg_arr[0].trim();
				curl = seg_arr[1].trim();
				ctext = seg_arr[2].trim();
				url = seg_arr[3].trim();
				title = seg_arr[4].trim();
				host = seg_arr[5].trim();
				cate = seg_arr[6].trim();

				if ((host != null) && (!host.equals(""))) {
					if (isValidTitle(title)) {
						word.set(title);
						word1.set("\001" + cookie + "\001" + curl + "\001"
								+ ctext + "\001" + url + "\001" + host + "\001"
								+ cate);
						double ran = Math.random();
						int rani = -1;
						rani = (int) (100 * ran);
						if (rani > 10 && rani < 12) {
							if(title.length()>10)
							{
							 context.write(word, word1);
							}
						}
					}

				}
			}

		}

		public boolean isValidTitle(String title) {
			title = title.trim();
			if (title == null || title.equals("")) {
				return false;
			}
			boolean isVal = true;
			String eng_mat = "[a-zA-Z0-9\\.:\\?#=_/&\\-%]*";
			if (Pattern.matches(eng_mat, title)) {
				isVal = false;
			}
			if ((title.indexOf("<") != -1) || (title.indexOf(">") != -1)) {
				isVal = false;
			}

			char first_char = title.charAt(0);
			if (Pattern.matches(eng_mat, first_char + "")) {
				isVal = false;
			}
			if (!isChinese(first_char)) {
				isVal = false;
			}

			return isVal;

		}

		public boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
			String val = "";
			String[] seg_arr = null;
			String cookie = "";
			String cookie_str = "";
			while (it.hasNext()) {
				val = it.next().toString();
				seg_arr = val.split("\001");
				if (seg_arr.length != 7) {
					continue;
				}
				cookie = seg_arr[1].trim();
				cookie_str = cookie_str + cookie + " ";
				//context.write(key, it.next());
			}
			
			cookie_str=cookie_str.trim();
			cookie_str="\001"+cookie_str;
			result.set(cookie_str);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: CLKSelMulCookMR <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "CLKSelMulCookMR_" + day);
		job.setJarByClass(CLKSelMulCookMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(2);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
