package cn.clickwise.liqi.mapreduce.app.swa_analysis;
import java.io.IOException;
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


public class SWAUTIRevBlankMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\t");

			String url="";
			String title="";
			String ips="";
			
			String title_ips="";
			            
			String[] temp_seg=null;
			if (seg_arr != null && seg_arr.length >1) {
	                  url=seg_arr[0].trim();
	                    
	                  for(int j=1;j<seg_arr.length;j++)
	                  {
	                	  title_ips=title_ips+seg_arr[j];
	                  }
	                  title_ips=title_ips.trim();
	                  
	                  if((title_ips!=null)&&(!title_ips.equals("")))
	                  {
	                	  temp_seg=title_ips.split("\001");
	                	  if((temp_seg!=null)&&(temp_seg.length==2))
	                	  {
	                		  title=temp_seg[0].trim();
	                		  ips=temp_seg[1].trim();
	                		  if((title!=null)&&(!title.equals(""))&&(title.length()>5))
	                		  {
	                			  word.set(url);
	                			  word1.set(title_ips);
	                			  context.write(word, word1);	
	                		  }
	                	  }
	                  }									
				
			}

		}

		public boolean isValidTitle(String title) {
			title = title.trim();
			if(title==null||title.equals(""))
			{
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
			if (Pattern.matches(eng_mat, first_char+"")) {
				isVal = false;
			}
			if(!isChinese(first_char))
			{
				isVal = false;
			}

			return isVal;

		}

		public  boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
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
		
		public boolean isValidTitle(String title) {
			title = title.trim();
			if(title==null||title.equals(""))
			{
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
			if (Pattern.matches(eng_mat, first_char+"")) {
				isVal = false;
			}
			if(!isChinese(first_char))
			{
				isVal = false;
			}

			return isVal;

		}

		public  boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}
		
		
		
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: SWAUTIRevBlankMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "SWAUTIRevBlankMR_" + day);
		job.setJarByClass(SWAUTIRevBlankMR.class);
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
