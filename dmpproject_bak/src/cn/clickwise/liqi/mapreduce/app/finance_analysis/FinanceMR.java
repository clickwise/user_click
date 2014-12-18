package cn.clickwise.liqi.mapreduce.app.finance_analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

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

import cn.clickwise.liqi.mapreduce.app.video_analysis.VideoMR;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.str.edcode.CharsetConv;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.str.regex.RegexDB;
import cn.clickwise.liqi.str.regex.RegexFind;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class FinanceMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";
		public HashMap<String,String> finance_map=null;

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
          
          	
        	String city="";
        	String source_host="";
        	
        	String info="";
           	String adate_str="";
        	String atime_str="";
        	//finance_map=getFinanceMap();
        	String host_attr="";
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
  			        host_attr="";
  			        
                    if(SSO.tnoe(atime)){
                        adate_str=TimeOpera.getDateFromStr(atime);
                        atime_str=TimeOpera.getTimeFromStr(atime);
                    }
                    host=host.trim();
                    /*
                    if(host.indexOf("alipay.com")>0)
                    {
                    	host_attr="支付宝";
                    }
                    else if(host.indexOf("tenpay.com")>0)
                    {
                    	host_attr="财富通";
                    }
                    else
                    {
                        host_attr=finance_map.get(host);
                    }
                    */
        			if(SSO.tnoe(area))
        			{      
        					if(SSO.tnoe(host))
        					{
        					  area=area.trim();
                        	  adate_str=adate_str.trim();
                        	  atime_str=atime_str.trim();
                        	  city=city.toLowerCase();
        					  word.set(host);
        					  word1.set(adate_str+"\001"+atime_str+"\001"+sip+"\001"+dip+"\001"+area+"\001"+cookie+"\001"+loc+"\001"+agent);
        					  context.write(word, word1);
        					}
        			}        	
        						
        		}		 		
        	} 		             				
		}
		/*
		public HashMap<String,String> getFinanceMap() 
		{  
			HashMap<String,String> finance_map=new HashMap<String,String>();
			try
			{
		        InputStream in=this.getClass().getResourceAsStream("/finance_map.txt"); 
		        Reader f = new InputStreamReader(in);         
		        BufferedReader fb = new BufferedReader(f);  
		        String line = fb.readLine();  
		        String[] seg_arr=null;
		        while(SSO.tnoe(line)) 
		        {  
		        	line=line.trim();
		        	seg_arr=line.split("\\s+");
		        	if(seg_arr.length!=2)
		        	{
		        		continue;
		        	}
		        	String host_attr=seg_arr[0];
					String host=seg_arr[1];
					if(SSO.tnoe(host))
					{
						if(!(finance_map.containsKey(host)))
						{
					      finance_map.put(host, host_attr);
						}
					}
					line=fb.readLine();
		        }
		        fb.close();
		        f.close();
		        in.close();
	        }
			catch (Exception e) 
			{
				e.printStackTrace();
			}  
	        return finance_map;  
	    }
		
		*/
	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, NullWritable> {
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
			System.err.println("Usage: FinanceMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "FinanceMR_" + day);
		job.setJarByClass(FinanceMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(100);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
}
