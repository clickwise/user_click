package cn.clickwise.liqi.mapreduce.app.radius_analysis;

import java.io.IOException;
import java.util.ArrayList;
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

import cn.clickwise.liqi.crawler.basic.BatchUrlCrawl;
import cn.clickwise.liqi.crawler.basic.UrlStatusTest;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * 抓取判定哪些host解析错误
 * @author zkyz
 *
 */
public class HostVerifyMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
				
			
             String host=value.toString();
             String source="";
             
             String url="";
             boolean icp=false;
        	 System.out.println("host:"+host);
        	 String ctime="";
        	 
        	 
             if(SSO.tnoe(host))
             {
            	 host=host.replaceAll("\"", "");
            	 url=format_url(host);
            	 host=host.trim();
            	 //System.out.println("url:"+url);
            	 try{
            	 source=UrlStatusTest.getRedirectInfo(url);
            	 }
            	 catch(Exception e){}
                 icp=isCorrPage(source); 
               //  word.set(host+"\001");
                 if(icp==true)
                 {
                	 ctime=TimeOpera.getCurrentTime();
                	 word.set(host+"\001true\001"+ctime);
                	 word1.set("");
                	 System.out.println(host+":true");
                 }
                 else
                 {
                	 ctime=TimeOpera.getCurrentTime();
                	 word.set(host+"\001false\001"+ctime);
                	 word1.set("");
                	 System.out.println(host+":false");
                 }
                 context.write(word, word1);             
             }
            
			/*
			String host_line=value.toString();
            String[] host_arr=host_line.split("\001"); 
            BatchUrlCrawl buc=new BatchUrlCrawl();
            ArrayList<String> host_crawl=null;        
            try{
            	host_crawl=buc.getRedirectInfoBat(host_arr, false, null);
            }
            catch(Exception e)
            {
            	
            }
            String[] info_seg=null;
            String host="";
            String source="";
            boolean icp=false;
            for(int i=0;i<host_crawl.size();i++)
            {
            	info_seg=(host_crawl.get(i)).split("\001");
            	if(info_seg.length<2)
            	{
            		continue;
            	}
            	host=info_seg[0].trim();
            	
            	source="";
            	for(int j=1;j<info_seg.length;j++)
            	{
            		source=source+info_seg[j]+"\001";
            	}
            	
            	source=source.trim();
            	icp=isCorrPage(source); 
                word.set(host+"\001");
                if(icp==true)
                {
               	 word1.set("true");
               	 System.out.println(host+":true");
                }
                else
                {
               	 word1.set("false");
               	 System.out.println(host+":false");
                }
                
                if(SSO.tnoe(host))
                {
                	word.set(host);
                	context.write(word, word1);
                }          	
            }
            
            */
			
		}
		
		public boolean isCorrPage(String source)
		{
			String key_regex="location=\"http://www.189so.cn";
			//String key_regex="window.location.replace\\(\"http://211.98.71.195";
			Pattern key_pat=Pattern.compile(key_regex);
			Matcher key_mat=key_pat.matcher(source);
			if(key_mat.find())
			{
				return true;
			}
			return false;
		}
		
		public String format_url(String host)
		{
			String fu="";
			if(!(SSO.tnoe(host)))
			{
				return "";
			}
			host=host.trim();
			if((host.indexOf("http://"))>-1)
			{
				return host;
			}
			
			fu="http://"+host;
			
			//System.out.println("fu:"+fu);
			fu=fu.trim();			
			return fu;
		}
		
	}
	
	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			 String key_str=key.toString().trim();
			 Iterator<Text> it = values.iterator();            
      	     		
		}
		
		public int findLastStatus(ArrayList<String> al)
		{
			if((al==null)||((al.size())<1))
			{
				return 0;
			}
			String record=al.get(al.size()-1);
			String status="";
			String record_time="";
			String[] seg_arr=null;
			seg_arr=record.split("\t");
			if(seg_arr.length!=2)
			{
				return 0;
			}
			status=seg_arr[0].trim();
			record_time=seg_arr[1].trim();	
			return Integer.parseInt(status);
		}
		
		public ArrayList<String> removeLast(ArrayList<String> al)
		{
			
			ArrayList<String> nal=new ArrayList<String>();
			
			for(int i=0;i<(al.size()-1);i++)
			{
				nal.add(al.get(i));
			}	
			return nal;			
		}
	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: HostVerifyMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "HostVerifyMR_" + day);
		job.setJarByClass(HostVerifyMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		
		//job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(0);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		//job.setOutputKeyClass(Text.class);
		//job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
	

}
