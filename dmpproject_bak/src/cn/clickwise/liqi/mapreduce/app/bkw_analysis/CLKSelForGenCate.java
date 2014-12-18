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

public class CLKSelForGenCate {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");
			String url = "";
			String cookie_str = "";
			String title = "";
			String host = "";
			String cate = "";
            String gender_cate="未分类";
			if (seg_arr != null && seg_arr.length == 5) {
				url = seg_arr[0].trim();
				cookie_str = seg_arr[1].trim();
				title = seg_arr[2].trim();
				host = seg_arr[3].trim();
				cate = seg_arr[4].trim();
                if(cate.equals("女性时尚"))
                {
                	gender_cate="女";
                }
                            
				if ((host != null) && (!host.equals(""))) {
					if (isValidTitle(title)&&isValidUrl(url)) {						
						word.set(url);
						word1.set(cookie_str + "\001" + title + "\001"+ host + "\001" + cate+"\001"+gender_cate);
						double ran = Math.random();
						int rani = -1;
						rani = (int) (100 * ran);
						if (rani > 10 && rani < 12) {
							if (title.length() > 10) {
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

		public boolean isValidUrl(String url)
		{
			boolean isValUrl=true;
			return isValUrl;
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

			//word1.set( cookie_str + "\001" + title + "\001"+ host + "\001" + cate+"\001"+gender_cate);
			
			Iterator<Text> it = values.iterator();
			String val = "";
			String[] seg_arr = null;
			
			String cookie_str = "";
			String title="";
			String host="";
			String cate="";
			String gender_cate="";
			
			String red_str="";
			while (it.hasNext()) {
				val = it.next().toString();
				seg_arr = val.split("\001");
				if (seg_arr.length != 5) {
					continue;
				}
				cookie_str = seg_arr[0].trim();
				title=seg_arr[1].trim();
				host=seg_arr[2].trim();
				cate=seg_arr[3].trim();
				gender_cate=seg_arr[4].trim();				
				// context.write(key, it.next());
			}
			red_str="\001"+cookie_str+"\001"+title+"\001"+cate+"\001"+gender_cate;
			result.set(red_str);
			
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
