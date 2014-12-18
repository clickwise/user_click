package cn.clickwise.liqi.mapreduce.app.radius_analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

import cn.clickwise.liqi.sort.utils.SortStrArray;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * 提取189so.cn的访问ip, 时间, host, refer信息
 * @author zkyz
 *
 */
public class So189MR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
					
            String log_line=value.toString().trim();        	
            String request_time="";
            String ip="";
            String host="";
            String refer="";
                      
    		request_time=SSO.beforeStr(log_line, "{");
    		//System.out.println("time:"+time);
    		request_time=SSO.truncAfterStr(request_time, ":");
    		//System.out.println("time2:"+time2);
    	
    		String info=SSO.midstrs(log_line, "{", "}");
    		//System.out.println("info:"+info);
    		
    		String[] seg_arr=info.split(",");
    		
    		String ikey="";
    		String ival="";
    		
    		for(int i=0;i<seg_arr.length;i++)
    		{
    		  ikey=SSO.beforeStr(seg_arr[i], ":");	
    		  ikey=SSO.midstrs(ikey, "\"", "\"");
    		  ival=SSO.afterStr(seg_arr[i], ":");
    		  ival=SSO.midstrs(ival, "\"", "\"");
    		  
    		  if(ikey.equals("ip"))
    		  {
    			  ip=ival;
    		  }
    		  else if(ikey.equals("host"))
    		  {
    			  host=ival;
    		  }
    		  else if(ikey.equals("refer"))
    		  {
    			  refer=ival;
    		  }
    		}
    		
    		
    	    String out_info="";
    	    out_info=request_time+"\001"+host+"\001"+refer;
    	   // out_info=out_info.trim();
    	    host=host.trim();
    	    if(host.equals("www.189so.cn"))
    	    {
        		if(SSO.tnoe(ip))
        		{
        			word.set(ip+"\001");
        			word1.set(out_info);
        			context.write(word, word1);
        		}
    	    }   	        			
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
			System.err.println("Usage: So189MR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "So189MR_" + day);
		job.setJarByClass(So189MR.class);
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
