package cn.clickwise.liqi.mapreduce.app.dns_analysis;

import java.io.IOException;
import java.util.ArrayList;
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
 * 从dns记录提取hive表各字段，包括
 * 字段名	           字段说明	示例
 * src_ip	源ip	   115.193.177.182
 * src_port	源端口	  39781
 * dest_ip	目的ip	202.101.172.47
 * dest_port	目的端口	53(domain)
 * atime	查询时间	11:30:43
 * Id	请求序列号	59709
 * type	查询类型	A, MX, AAAA, TXT等
 * domain_name	查询域名	www.baidu.com
 * extra_info	附注	 
 * @author lq
 */

public class DNSRequestMR {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {


            String dns_line=value.toString().trim();
	
           // String request_regex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.(?:(?:domain)|(?:53))";            
            String request_regex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.[a-zA-Z0-9_\\-\\.]*?\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.(?:(?:domain)|(?:53))";
            Pattern request_pat=Pattern.compile(request_regex);
            Matcher request_mat=request_pat.matcher(dns_line);
            
            boolean isRequest=false;
            if(request_mat.find())
            {
            	isRequest=true;
            }
            /*
            String response_regex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.(?:(?:domain)|(?:53))\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.[a-zA-Z0-9_\\-\\.]*?\\s*:";           
            Pattern response_pat=Pattern.compile(response_regex);
            Matcher response_mat=response_pat.matcher(dns_line);
            
            boolean isResponse=false;
            if(response_mat.find())
            {
            	isResponse=true;
            }
        	if((isRequest==false)&&(isResponse==false))
        	{
        		System.out.println("not request and response:"+dns_line);
        	}
        	*/
        	
        	String request_time="";
        	String src_ip="";
        	String src_port="";
        	String dest_ip="";
        	String dest_port="";
        	String request_id="";
        	String request_type="";
        	String domain_name="";
        	String extra_info="";
        	
        	String request_info="";
            if(isRequest)
            {
              	//String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([\\d]+)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?\\s*\\[udp\\s*sum\\s*ok\\]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
              	//String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?\\s*\\[udp\\s*sum\\s*ok\\]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
            	String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP\\s*\\(.*?id\\s*([\\d]+).*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53))).*?\\s*\\[udp\\s*sum\\s*ok\\]\\s*([\\d]+)[\\+]\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)))\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*";
            	Pattern field_pat=Pattern.compile(field_regex);
              	Matcher field_mat=field_pat.matcher(dns_line);
            	int gnum=9;
            	         	
            	if(field_mat.find())
            	{
            		request_time=field_mat.group(1);         		
            		//request_time=request_time.substring(0,request_time.lastIndexOf(":"));
            		request_time=request_time.replaceAll(":", ""); 
            		extra_info=field_mat.group(2); 
            		src_ip=field_mat.group(3);
            		src_port=field_mat.group(4);
            		dest_ip=field_mat.group(5);
            		dest_port=field_mat.group(6);
            		request_id=field_mat.group(7);
            		request_type=field_mat.group(8);
            		domain_name=field_mat.group(9);
            		
            		/*
            		System.out.println("request_time:"+request_time);
            		System.out.println("src_ip:"+src_ip);
            		System.out.println("src_port:"+src_port);
            		System.out.println("dest_ip:"+dest_ip);
            		System.out.println("dest_port:"+dest_port);
            		System.out.println("request_id:"+request_id);
            		System.out.println("request_type:"+request_type);
            		System.out.println("domain_name:"+domain_name);
            		*/
            		
            		if((SSO.tnoe(request_time))&&(SSO.tnoe(src_ip))&&(SSO.tnoe(src_port))&&(SSO.tnoe(dest_ip))&&(SSO.tnoe(dest_port))&&(SSO.tnoe(request_id))&&(SSO.tnoe(request_type))&&(SSO.tnoe(domain_name))&&(SSO.tnoe(extra_info)))
            		{
            			request_time=request_time.trim();
            			extra_info=extra_info.trim();
            			src_ip=src_ip.trim();
            			src_port=src_port.trim();
            			dest_ip=dest_ip.trim();
            			dest_port=dest_port.trim();
            			request_id=request_id.trim();
            			request_type=request_type.trim();
            			domain_name=domain_name.trim();           			
            		    if((domain_name.charAt(domain_name.length()-1))=='.')
            		    {
            		    	domain_name=domain_name.substring(0,domain_name.length()-1);
            		    }
            		    request_info=src_ip+"\001"+src_port+"\001"+dest_ip+"\001"+dest_port+"\001"+request_id+"\001"+request_type+"\001"+domain_name+"\001"+extra_info;
            			word.set(request_time);
            		    word1.set(request_info);
            		    context.write(word, word1);		           		
            		}
            		//else
            		//{
            		//	System.out.println("not  mat request:"+dns_line);
            		//}
            	}
     	
            }
                    				
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
			System.err.println("Usage: DNSRequestMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "DNSRequestMR_" + day);
		job.setJarByClass(DNSRequestMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(20);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
