package cn.clickwise.liqi.mapreduce.app.travel_analysis;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * 获得到地点A的访问记录
 * @author zkyz
 *
 */
public class QunarSingleAreaMR {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
            String nstat_line=value.toString().trim();        	
        	String area="";
        	String atime="";
        	String pname="";
        	String sip="";
        	String dip="";
        	String host="";
        	String url="";
        	String refer="";
        	String cookie="";
        	String loc="";
        	String agent="";       
        	String[] seg_arr=null;
        	seg_arr=nstat_line.split("\001");
        	String link="";
 	
        	String Code_A="";

        	String date_a;
        	String adate_str="";
        	String atime_str="";
        	
        	if(seg_arr.length==11)
        	{
        		area=seg_arr[0].trim();
        		atime=seg_arr[1].trim();
        		pname=seg_arr[2].trim();
        		sip=seg_arr[3].trim();
        		dip=seg_arr[4].trim();
        		host=seg_arr[5].trim();
        		url=seg_arr[6].trim();
        		refer=seg_arr[7].trim();
        		cookie=seg_arr[8].trim();
        		loc=seg_arr[9].trim();
        		agent=seg_arr[10].trim();   
        		if(SSO.tnoe(host))
        		{
        			if(!(SSO.tnoe(url)))
        			{
        				url="";
        			}
        			link=host+url;	
                    Code_A=getAreaFromLink(link);	
                    Code_A=getDecodeUrl(Code_A);
                    Code_A=Code_A.replaceAll("&", "");
                    adate_str=TimeOpera.getDateFromStr(atime);
                    atime_str=TimeOpera.getTimeFromStr(atime);
                    if(SSO.tnoe(Code_A)&&SSO.tnoe(adate_str)&&SSO.tnoe(atime_str)&&SSO.tnoe(area))
                    {
                    	adate_str= adate_str.trim();
                    	atime_str= atime_str.trim();
                    	area=area.trim();
                    	Code_A=Code_A.trim();
                    	date_a=getDADateFromLink(link);
                    	word.set(area);
                    	word1.set(adate_str+"\001"+atime_str+"\001"+sip+"\001"+dip+"\001"+Code_A+"\001"+cookie+"\001"+loc+"\001"+agent+"\001"+date_a+"\001qunar");
                    	context.write(word, word1);
                    	
                    }
        		}        		        		
        	}
        		             				
		}
			
		
		/**
		 * 从链接中获得出发地点和到达地点
		 * urldecode： %3D  =
		 *            %26  &
		 *            %25  %
		 * @return
		 */
		public String getAreaFromLink(String link)
		{
        	Pattern ac_pat=Pattern.compile("(?:(?:hotel)|(?:visa)).qunar.com.*?(?:(?:city))\\s*(?:(?:=)|(?:%3D))\\s*([A-Z\\+\\%0-9]*)");
        	Matcher ac_mat=null;
			ac_mat=ac_pat.matcher(link);
					
        	
			String area_a="";
			
			if(ac_mat.find())
			{
				area_a=ac_mat.group(1);		
			}
		
			
		
			return area_a;
		}
		
		/**
		 * 获取出发日期和返回日期
		 * @param link
		 * @return  date_arr
		 */
		public String getDADateFromLink(String link)
		{
	     	Pattern ac_pat=Pattern.compile("(?:(?:hotel)|(?:visa)).qunar.com.*?(?:(?:fromDate))\\s*(?:(?:=)|(?:%3D))\\s*([\\-0-9]*)");
        	Matcher ac_mat=null;
			ac_mat=ac_pat.matcher(link);
					
        	
			String date_a="";
			
			if(ac_mat.find())
			{
				date_a=ac_mat.group(1);		
			}
			
			if(!(SSO.tnoe(date_a)))
			{
				date_a="";
			}
			
			return date_a;
		}
		
		/**
		 * 对Urlencode后的串进行解码
		 * @param encode_str
		 * @return decode_str
		 */
		public String getDecodeUrl(String encode_str)
		{
			String decode_str="";
			if(!(SSO.tnoe(encode_str)))
			{
				return "";
			}
			try{
			decode_str=URLDecoder.decode(encode_str);
			decode_str=URLDecoder.decode(decode_str);
			}
			catch(Exception e)
			{
				return "";
			}
			return decode_str;
		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text,NullWritable> {
		private Text result = new Text();
		private Text result_key = new Text();
		
		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			 String key_str=key.toString().trim();
			 Iterator<Text> it = values.iterator();
			 String info="";
			 if(SSO.tnoe(key_str))
			 {
				 while(it.hasNext())
				 {
					 info=it.next().toString();
					 info=info.trim();
					 if(SSO.tnoe(info))
					 {
						 result_key.set(key_str+"\001"+info);
						 context.write(result_key,NullWritable.get());
					 }
				 }
				 
				 
			 }
		}		
	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: QunarSingleAreaMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "QunarSingleAreaMR_" + day);
		job.setJarByClass(QunarSingleAreaMR.class);
		
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
}
