import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.Hashtable;
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



public class SWAAddIPSMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");

	        //new_line=url+"\001"+title+"\001"+seg_title+"\001"+sws+"\001"+ips;
			String url = "";
			String title = "";
			String ips = "";
			ips="1";
			if (seg_arr != null && seg_arr.length == 2) {
				url = seg_arr[0].trim();
                title=seg_arr[1].trim();
                if((url!=null)&&(!url.equals(""))&&(title!=null)&&(!title.equals(""))&&(isValidTitle(title)))
                {
                	word.set(url);
                	word1.set(title+"\001"+ips);
                	context.write(word, word1);
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



		public String clean_one_word(String word) {
			String new_word = word;
			// new_word=new_word.replaceAll("``", "");
			// new_word=new_word.replaceAll("''", "");
			new_word = new_word.replaceAll("&nbsp;", "");
			new_word = new_word.replaceAll("&nbsp", "");
			new_word = new_word.replaceAll("&ldquo;", "");
			new_word = new_word.replaceAll("&ldquo", "");
			new_word = new_word.replaceAll("&rdquo;", "");
			new_word = new_word.replaceAll("&rdquo", "");
			// new_word=new_word.replaceAll(";", "");
			new_word = new_word.replaceAll("&", "");
			new_word = new_word.replaceAll("VS", "");
			new_word = new_word.replaceAll("-RRB-", "");
			new_word = new_word.replaceAll("-LRB-", "");
			new_word = new_word.replaceAll("_", "");
			// new_word=new_word.replaceAll("[\\.]*", "");
			new_word = new_word.replaceAll("\\\\\\#", "");
			/*
			 * if (Pattern.matches("[a-zA-Z\\,\\.\\?0-9\\!\\-\\s]*", new_word))
			 * { return ""; } if (!(Pattern.matches("[\\u4e00-\\u9fa5]+",
			 * new_word))) { return ""; }
			 */
			new_word = new_word.replaceAll("★", "");
			new_word = new_word.replaceAll("__", "");
			new_word = new_word.replaceAll("ˇ", "");
			new_word = new_word.replaceAll("®", "");
			new_word = new_word.replaceAll("♣", "");
			new_word = new_word.replaceAll("\\丨", "");
			new_word = new_word.trim();

			return new_word;
		}
		
	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();                    
			while(it.hasNext()) {
			  context.write(key, it.next());
			}					
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SWAAddIPSMR <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "SWAAddIPSMR_" + day);
		job.setJarByClass(SWAAddIPSMR.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(8);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}
	
	
}
