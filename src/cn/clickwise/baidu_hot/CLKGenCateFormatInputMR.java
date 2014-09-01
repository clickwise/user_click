package cn.clickwise.baidu_hot;
import java.io.IOException;
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

public class CLKGenCateFormatInputMR {

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

			if (seg_arr != null && seg_arr.length == 5) {
				url = seg_arr[0].trim();
				cookie_str = seg_arr[1].trim();
				title = seg_arr[2].trim();
				host = seg_arr[3].trim();
				cate = seg_arr[4].trim();

				if ((host != null) && (!host.equals(""))) {
					if (isValidTitle(title)) {
						word.set(title);
						word1.set(cookie_str);

						double ran = Math.random();
						int rani = -1;
						rani = (int) (100 * ran);
						if (title.length() > 10) {
							context.write(word, word1);
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
			String cookie_str = "";
			String red_str="";
			
			while (it.hasNext()) {
				val = it.next().toString();
                red_str=enlarge_cookie_str(red_str,val);
				// context.write(key, it.next());
			}

			red_str = red_str.trim();
			red_str = "\001" + red_str;
			result.set(red_str);
			context.write(key, result);
		}
		public String enlarge_cookie_str(String old_str,String incr_str)
		{
			String n_str="";
			Hashtable<String,String> cook_hash=new Hashtable<String,String>();
			String[] seg_arr=null;
			old_str=old_str.trim();
			String cookie="";
			
			if((old_str!=null)&&(!old_str.equals("")))
			{
				seg_arr=old_str.split("\\s+");
				if((seg_arr!=null)&&(seg_arr.length>0))
				{
			      for(int j=0;j<seg_arr.length;j++)
			      {
			    	  cookie=seg_arr[j].trim();
			    	  if((cookie!=null)&&(cookie.length()>0)&&(!cookie.equals("NA")))
			    	  {
			    		  if(!cook_hash.containsKey(cookie))
			    		  {
			    			  cook_hash.put(cookie, cookie);
			    		  }
			    	  }			    	  
			      }
				}
			}
			
			if((incr_str!=null)&&(!incr_str.equals("")))
			{
				seg_arr=incr_str.split("\\s+");
				if((seg_arr!=null)&&(seg_arr.length>0))
				{
			      for(int j=0;j<seg_arr.length;j++)
			      {
			    	  cookie=seg_arr[j].trim();
			    	  if((cookie!=null)&&(cookie.length()>0)&&(!cookie.equals("NA")))
			    	  {
			    		  if(!cook_hash.containsKey(cookie))
			    		  {
			    			  cook_hash.put(cookie, cookie);
			    		  }
			    	  }			    	  
			      }
				}
			}
			
			Enumeration enum_cooks=cook_hash.keys();
			
			while(enum_cooks.hasMoreElements())
			{
			   cookie=enum_cooks.nextElement()+"";
			   cookie=cookie.trim();
			   n_str=n_str+cookie+" ";
			}			
			n_str=n_str.trim();
			
			
			return n_str;
		}
		
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err
					.println("Usage: CLKGenCateFormatInputMR <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "CLKGenCateFormatInputMR_" + day);
		job.setJarByClass(CLKGenCateFormatInputMR.class);
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
