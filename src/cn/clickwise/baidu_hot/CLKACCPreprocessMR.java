package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.net.URI;
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



public class CLKACCPreprocessMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();


		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
			String[] seg_arr = (value.toString()).split("\001");

			String url = "";
			String cookie_str = "";
			String title = "";
                        URI sturi=null;
                        String host="";
			if (seg_arr != null && seg_arr.length == 3) {
				url = seg_arr[0].trim();
				cookie_str = seg_arr[1].trim();
				title = seg_arr[2].trim();
				try{
				sturi = new URI(url);
				host=sturi.getHost();
				}
				catch(Exception e){}
				
				if ((url != null) && (!url.equals(""))&&(host!=null)&&(!(host.trim()).equals(""))) {
					    host=host.trim();
						word.set(url);
						word1.set("\001" + cookie_str + "\001" + title+"\001"+host);
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
			System.err.println("Usage: CLKACCPreprocessMR <day> <input> <output>");
			System.exit(2);
		}

		String day = otherArgs[0];
		Job job = new Job(conf, "CLKACCPreprocessMR_" + day);
		job.setJarByClass(CLKACCPreprocessMR.class);
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
