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
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.str.basic.SSO;


/**
 * 从dns记录提取hive表各字段，包括
 * 字段名	           字段说明	示例
 * src_ip	源ip	   115.193.177.182
 * src_port	源端口	  39781
 * dest_ip	目的ip	202.101.172.47
 * dest_port	目的端口	53(domain)
 * atime	查询时间	11:30:43
 * Id	请求序列号	59709
 * domain_name	查询域名	www.baidu.com
 * response_format	解析结果格式	  3/5/5
 * type	解析结果类型	A, CNAME, MX, AAAA, TXT, SOA, NS 等
 * response_value	解析结果	115.239.210.26, www.a.shifen.com
 *                          115.239.210.27 ns: a.shifen.com
 *                          119.75.219.43
 *                          www.a.shifen.com., www.a.shifen.com
 *                          …
 * extra_info	附注	 
 * @author lq
 */
public class DNSResponseMR {

	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {


            String dns_line=value.toString().trim();
	
           // String response_regex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.(?:(?:domain)|(?:53))\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+";           
            String response_regex="[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.(?:(?:domain)|(?:53))\\s*>\\s*[\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+\\.[a-zA-Z0-9_\\-\\.]*?\\s*:";
            Pattern response_pat=Pattern.compile(response_regex);
            Matcher response_mat=response_pat.matcher(dns_line);
            
            boolean isResponse=false;
            if(response_mat.find())
            {
            	isResponse=true;
            }
            
        	String request_time="";
        	String src_ip="";
        	String src_port="";
        	String dest_ip="";
        	String dest_port="";
        	String request_id="";
           	String request_type="";
        	String domain_name="";
        	String response_format="";
        	String response_type="";
        	String response_value="";
        	String extra_info="";
        	
        	String[] response_arr=null;
        	
        	String request_info="";
        	//if(isResponse==false)
        	//{
        		//System.out.println("not response:"+dns_line);
        	//}
            if(isResponse)
            {
              	//String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53)))\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([\\d]+).*?\\[udp sum ok\\]\\s*([\\d]+)\\s*q:\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)))\\s*\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*([\\d]+/[\\d]+/[\\d]+)\\s*[a-zA-Z0-9_\\-\\.]*\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)).*)$";
              	//String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP.*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53)))\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*:.*?\\[udp sum ok\\]\\s*([\\d]+)\\s*q:\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)))\\s*\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*([\\d]+/[\\d]+/[\\d]+)\\s*[a-zA-Z0-9_\\-\\.]*\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)).*)$";
            	String field_regex="([\\d]+:[\\d]+:[\\d]+)\\.[\\d]+\\s*IP\\s*\\(.*?id\\s*([\\d]+).*?([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.((?:(?:domain)|(?:53)))\\s*>\\s*([\\d]+\\.[\\d]+\\.[\\d]+\\.[\\d]+)\\.([a-zA-Z0-9_\\-\\.]*?)\\s*:.*?\\[udp sum ok\\]\\s*([\\d]+)\\s*q:\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)))\\s*\\?\\s*([a-zA-Z0-9_\\-\\.]*)\\s*([\\d]+/[\\d]+/[\\d]+)\\s*[a-zA-Z0-9_\\-\\.]*\\s*((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS)).*)$";
            	Pattern field_pat=Pattern.compile(field_regex);
              	Matcher field_mat=field_pat.matcher(dns_line);
            	int gnum=11;
            	         	
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
            		response_format=field_mat.group(10);
            		response_value=field_mat.group(11);
            		          		
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
            		
            		if((SSO.tnoe(request_time))&&(SSO.tnoe(src_ip))&&(SSO.tnoe(src_port))&&(SSO.tnoe(dest_ip))&&(SSO.tnoe(dest_port))&&(SSO.tnoe(request_id))&&(SSO.tnoe(request_type))&&(SSO.tnoe(domain_name))&&(SSO.tnoe(response_format))&&(SSO.tnoe(response_value))&&(SSO.tnoe(extra_info)))
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
            			response_format=response_format.trim();
            			response_value=response_value.trim();
            			
            		    if((domain_name.charAt(domain_name.length()-1))=='.')
            		    {
            		    	domain_name=domain_name.substring(0,domain_name.length()-1);
            		    }
            		    response_arr= getResponseType(response_value);
            		    request_info=src_ip+"\001"+src_port+"\001"+dest_ip+"\001"+dest_port+"\001"+request_id+"\001"+request_type+"\001"+domain_name+"\001"+response_format+"\001"+response_arr[0]+"\001"+response_arr[1]+"\001"+extra_info;
            			word.set(request_time);
            		    word1.set(request_info);
            		    context.write(word, word1);		           		
            		}
            		//else
            		//{
            		//	System.out.println("not mat response:"+dns_line);
            		//}
            	}
     	
            }
                    				
		}
		
		public static String[] getResponseType(String response_value)
		{
			String[] responseArr=new String[2];
			responseArr[0]="";
			responseArr[1]="";
			String responseType="";
			String responseSplitVal="";
			
			String rt_regex="((?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS))\\s*[a-zA-Z0-9_\\-\\.\\s\\,:]*?)\\s*(?:(?:A)|(?:MX)|(?:AAAA)|(?:TXT)|(?:CNAME)|(?:SOA)|(?:NS))";
			ArrayList rt_list=new ArrayList();
			Pattern rt_pat=Pattern.compile(rt_regex);
			Matcher rt_mat=rt_pat.matcher(response_value);
			String sin_type="";
			String left_value="";
			boolean isFind=false;
			while(rt_mat.find())
			{
				isFind=true;
				sin_type=rt_mat.group(1);
				rt_list.add(sin_type);
				response_value=response_value.replaceFirst(sin_type,"");
				//System.out.println("response_value:"+response_value);
				left_value=response_value;
				rt_mat=rt_pat.matcher(response_value);
			}
	        if(isFind==false)
	        {
	        	left_value=response_value;
	        }
		    left_value=left_value.replaceAll("\\([\\d]+\\)", "");
		    left_value=left_value.trim();
		    //System.out.println("left_value: "+left_value.trim());
		    rt_list.add(left_value);
		    
		    String[] seg_arr=null;
		    
		    for(int i=0;i<(rt_list.size());i++)
		    {
		    	sin_type=rt_list.get(i)+"";
		    	responseSplitVal=responseSplitVal+sin_type+"|";
		    	sin_type=sin_type.trim();
		    	if(!(SSO.tnoe(sin_type)))
		    	{
		    		continue;
		    	}
		    	seg_arr=sin_type.split("\\s+");
		    	if((seg_arr==null)||(seg_arr.length<1))
		    	{
		    		continue;
		    	}
		    	
		    	//System.out.println(i+" "+seg_arr[0]);
		    	responseType=responseType+seg_arr[0]+"|";   	
		    }
		    if(SSO.tnoe(responseType))
		    {
		       responseType=responseType.substring(0,responseType.lastIndexOf("|"));
		    }
		    if(SSO.tnoe(responseSplitVal))
		    {
		    	responseSplitVal=responseSplitVal.substring(0,responseSplitVal.lastIndexOf("|"));
		    }
		    //System.out.println("responseType: "+responseType);
		    //responseType=responseType+"|"+rt_list.get((rt_list.size()-1)); 
		    responseArr[0]=responseType;
		    responseArr[1]=responseSplitVal;	
			return responseArr;
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
			System.err.println("Usage: DNSResponseMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "DNSResponseMR_" + day);
		job.setJarByClass(DNSResponseMR.class);
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
