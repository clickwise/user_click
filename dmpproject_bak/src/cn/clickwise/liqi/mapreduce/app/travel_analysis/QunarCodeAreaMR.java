package cn.clickwise.liqi.mapreduce.app.travel_analysis;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.mapreduce.app.dns_analysis.DNSRequestMR;
import cn.clickwise.liqi.str.basic.SSO;

/**
 * 提取去哪儿的所有 地区编码 
 * @author lq
 *
 */
public class QunarCodeAreaMR {
	
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
        	Pattern ac_pat=Pattern.compile("flight.qunar.com.*?departureCity=([A-Z]*)&arrivalCity=([A-Z]*)");
        	Matcher ac_mat=null;
        	
        	Pattern ac_pat2=Pattern.compile("flight.qunar.com.*?fromCity=([A-Z]*)&toCity=([A-Z]*)");
        	Matcher ac_mat2=null;  	
        	String Code_A="";
        	String Code_B="";
        	Code_B="";
        	String[] area_arr=null;
        	
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
        	        area_arr=getDAAreaFromLink(link);		
                    Code_A=area_arr[0];
                    Code_B=area_arr[1];
                    Code_A=getDecodeUrl(Code_A);
                    Code_B=getDecodeUrl(Code_B);
                    Code_A=Code_A.replaceAll("&", "");
                    Code_B=Code_B.replaceAll("&", "");
                    
                    if(SSO.tnoe(Code_A)&&SSO.tnoe(Code_B))
                    {
                    	Code_A=Code_A.trim();
                    	Code_B=Code_B.trim();
                    	word.set(Code_A);
                    	word1.set("");
                    	context.write(word, word1);
                    	
                    	word.set(Code_B);
                    	word1.set("");
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
		public String[] getDAAreaFromLink(String link)
		{
        	Pattern ac_pat=Pattern.compile("(?:(?:flight)|(?:ws)).qunar.com.*?(?:(?:departureCity)|(?:arrivalCity)|(?:searchDepartureAirport)|(?:searchArrivalAirport)|(?:fromCity)|(?:toCity)|(?:da)|(?:aa)|(?:from)|(?:to))\\s*(?:(?:=)|(?:%3D))\\s*([A-Z\\+\\%0-9]*)\\s*(?:(?:&)|(?:%26))\\s*(?:(?:departureCity)|(?:arrivalCity)|(?:searchDepartureAirport)|(?:searchArrivalAirport)|(?:fromCity)|(?:toCity)|(?:da)|(?:aa)|(?:from)|(?:to))\\s*(?:(?:=)|(?:%3D))\\s*([A-Z\\+\\%0-9]*)");
        	Matcher ac_mat=null;
			ac_mat=ac_pat.matcher(link);
					
        	Pattern ac_pat2=Pattern.compile("(?:(?:flight)|(?:ws)).qunar.com.*?INT\\s*\\|\\s*(?:(?:ROUND)|(?:ONEW))\\s*\\|\\s*([A-Z\\+%0-9]*)\\s*\\|\\s*([A-Z\\+%0-9]*)\\s*\\|");
        	Matcher ac_mat2=null;  
			
        	Pattern ac_pat3=Pattern.compile("(?:(?:flight)|(?:ws)).qunar.com/twelli/flight/cityinfo.jsp?city=([A-Z\\+%0-9]*),([A-Z\\+%0-9]*)&callback");
        	Matcher ac_mat3=null;  
        	
			String area_a="";
			String area_b="";
			String[] area_arr=new String[2];
			
			if(ac_mat.find())
			{
				area_a=ac_mat.group(1);
				area_b=ac_mat.group(2);
				area_arr[0]=area_a;
				area_arr[1]=area_b;			
			}
			else
			{
				ac_mat2=ac_pat2.matcher(link);
    			if(ac_mat2.find())
    			{			
    				area_a=ac_mat2.group(1);
    				area_b=ac_mat2.group(2);
    				area_arr[0]=area_a;
    				area_arr[1]=area_b;	
    			}
    			else
    			{
    				ac_mat3=ac_pat3.matcher(link);
    				if(ac_mat3.find())
    				{
        				area_a=ac_mat3.group(1);
        				area_b=ac_mat3.group(2);
        				area_arr[0]=area_a;
        				area_arr[1]=area_b;
    				}
    			}
			}
			
			
			
			return area_arr;
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
			decode_str=URLDecoder.decode(encode_str);
			decode_str=URLDecoder.decode(decode_str);
			return decode_str;
		}

	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			Iterator<Text> it = values.iterator();
            
        
			if(it.hasNext()) {
				context.write(key, it.next());
			}	
		}		
	}
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: QunarCodeAreaMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "QunarCodeAreaMR_" + day);
		job.setJarByClass(QunarCodeAreaMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
	
	
}
