package cn.clickwise.baidu_hot;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class NSTATReProcess {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] seg_arr = (value.toString()).split("\001");
			String area = "";
			String atime = "";
			String pname = "";
			String sip = "";
			String dip = "";
			String host = "";
			String url = "";
			String refer = "";
			String cookie = "";
			String loc = "";
            String for_url="";
			if (seg_arr != null && seg_arr.length == 10) {
				area = seg_arr[0].trim();
				atime = seg_arr[1].trim();
				pname = seg_arr[2].trim();
				sip = seg_arr[3].trim();
				dip = seg_arr[4].trim();
				host = seg_arr[5].trim();
				url = seg_arr[6].trim();
				refer = seg_arr[7].trim();
				cookie = seg_arr[8].trim();
				loc = seg_arr[9].trim();

				if ((host != null) && (!host.equals(""))) {
					if (isValidUrl(refer)) {
						for_url=format_ec_url(refer);
						word.set(for_url);
						word1.set(cookie);
						double ran = Math.random();
						int rani = -1;
						rani = (int) (10000 * ran);
						if (rani > 10 && rani < 110) {
							context.write(word, word1);
						}
					}

				}
			}

		}

		public boolean isValidUrl(String url) {

			boolean isVal = true;
			url=url.trim();
			if(url.equals("")||url.equals("(null)")||url.indexOf("(null)")!=-1||url.indexOf("zip")!=-1||url.indexOf("exe")!=-1||url.indexOf("jsp")!=-1||url.indexOf("swf")!=-1||url.indexOf("jpg")!=-1||url.indexOf("jpeg")!=-1||url.indexOf("asp")!=-1||url.length()<5)
			{
				isVal=false;
			}
			
			return isVal;

		}

		public boolean isChinese(char a) {
			int v = (int) a;
			return (v >= 19968 && v <= 171941);
		}
		
		public String format_ec_url(String url)
		{
			String feu="";
			String prefix="";
			String item_id="";
			if(url.indexOf("detail.tmall.com")!=-1)
			{
				prefix="http://detail.tmall.com/item.htm?";
				Pattern id_pat=Pattern.compile("(id=\\d+)");
				Matcher id_mat=id_pat.matcher(url);
				item_id="";
				if(id_mat.find())
				{
					item_id=id_mat.group(1);
				}
				feu=prefix+item_id;
				feu=feu.trim();
			}
			else if(url.indexOf("item.taobao.com")!=-1)
			{
			    prefix="http://item.taobao.com/item.htm?";	
				Pattern id_pat=Pattern.compile("(id=\\d+)");
				Matcher id_mat=id_pat.matcher(url);
				item_id="";
				if(id_mat.find())
				{
					item_id=id_mat.group(1);
				}
				feu=prefix+item_id;
				feu=feu.trim();	    
			}
			else
			{
				feu=url.trim();
			}
				
			return feu;
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
				cookie = val.trim();
				if (!cookie.equals("")) {
					cookie_str = cookie_str + cookie + " ";
				}
				// context.write(key, it.next());
			}

			cookie_str = cookie_str.trim();
			cookie_str = "\001" + cookie_str;
			result.set(cookie_str);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: NSTATReProcess <day> <input> <output>");
			System.exit(2);
		}
		String day = otherArgs[0];
		
		Job job = new Job(conf, "NSTATReProcess_" + day);
		job.setJarByClass(NSTATReProcess.class);
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(100);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}

}
