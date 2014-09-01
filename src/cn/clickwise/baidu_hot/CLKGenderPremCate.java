package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
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


public class CLKGenderPremCate {

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
                            
				if ((host != null) && (!host.equals(""))&&(cate.equals("女性时尚"))) {
					if (isValidTitle(title)&&isValidUrl(url)) {						
						word.set(title);
						word1.set(gender_cate+"\001"+cookie_str);
						double ran = Math.random();
						int rani = -1;
						rani = (int) (100 * ran);
						//if (rani > 10 && rani < 12) {
							if (title.length() > 10) {
								context.write(word, word1);
							}
						//}
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
			String gender_cate="";			
			String red_str="";
			Vector gender_cate_vec=new Vector();
			while (it.hasNext()) {
				val = it.next().toString();
				seg_arr = val.split("\001");
				if (seg_arr.length != 2) {
					continue;
				}
				gender_cate=seg_arr[0].trim();	
				cookie_str = seg_arr[1].trim();
				red_str=enlarge_cookie_str(red_str,cookie_str);
				gender_cate_vec.add(gender_cate);
				// context.write(key, it.next());
			}
			String max_cate=voteCate(gender_cate_vec);
			red_str="\001"+max_cate+"\001"+red_str;
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
			    	  if((cookie!=null)&&(cookie.length()>0))
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
			    	  if((cookie!=null)&&(cookie.length()>0))
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
		
		public String voteCate(Vector all_cates_vec)
		{
			String max_cate="NA";
			Hashtable<String,Integer> cate_hash=new Hashtable<String,Integer>();
			String cate="";
			int old_cnum=0;
			for(int i=0;i<all_cates_vec.size();i++)
			{
				cate=all_cates_vec.get(i)+"";
				cate=cate.trim();
				if((cate!=null)&&(cate.length()>0))
				{
					if(!cate_hash.containsKey(cate))
					{
						cate_hash.put(cate, 1);
					}
					else
					{
					  old_cnum=cate_hash.get(cate);
					  cate_hash.put(cate, old_cnum+1);						
					}
				}		
			}
			
			Enumeration cate_enum=cate_hash.keys();
			int max_cnum=0;
			int temp_cnum=0;
			while(cate_enum.hasMoreElements())
			{
				cate=cate_enum.nextElement()+"";
				temp_cnum=cate_hash.get(cate);
				if(temp_cnum>max_cnum)
				{
					max_cnum=temp_cnum;
					max_cate=cate;
				}			
			}			
			return max_cate;
		}
		
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: CLKGenderPremCate <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		Job job = new Job(conf, "CLKGenderPremCate_" + day);
		job.setJarByClass(CLKGenderPremCate.class);
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
