package cn.clickwise.baidu_hot;
import java.io.IOException;
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

public class CLKCookCateStatisNumMR {

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
								if(cookie.indexOf("uid=")!=-1)
								{
									cookie=match_user_cookie(cookie);
									cookie=cookie.trim();
									if((cookie==null)||(cookie.equals("")))
									{
										continue;
									}
								}								
								word.set(cookie);
								word1.set(gender_cate);
								context.write(word, word1);
							}
						}
					}

				}
			}

		}
		
		public String match_user_cookie(String cookie_str)
		{
		   String user_cookie="";
		   Pattern cook_pat=Pattern.compile("uid=([^;:\\s]*)");
		   Matcher cook_mat=cook_pat.matcher(cookie_str);
		   while(cook_mat.find())
		   {
			   user_cookie=cook_mat.group(1);
		   }
		   
		   return user_cookie;
		}
		
	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
			String gt_str = "";
			String one_gt = "";
			int his_num = 0;
			int mnum=0;
			int wnum=0;
			
			String gender_cate="";
			while (it.hasNext()) {
				gender_cate = it.next().toString();
				gender_cate = gender_cate.trim();
				if ((gender_cate == null) || (gender_cate.equals(""))) {
					continue;
				}
                if(gender_cate.equals("男"))
                {   
                	mnum++;
                }
                else if(gender_cate.equals("女"))
                {
                    wnum++;        	
                }
			}
			gt_str =wnum+"\t"+mnum;
			result.set(gt_str);
			context.write(key, result);
			
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err
					.println("Usage: CLKCookCateStatisNumMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "CLKCookCateStatisNumMR_" + day);
		job.setJarByClass(CLKCookCateStatisNumMR.class);
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
