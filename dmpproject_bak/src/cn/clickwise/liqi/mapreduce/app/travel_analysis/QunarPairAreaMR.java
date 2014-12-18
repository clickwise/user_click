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
 * 获得从地点A 到地点 B的访问记录
 * @author zkyz
 *
 */
public class QunarPairAreaMR {
	
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
        	String Code_B="";
        	Code_B="";
        	String[] area_arr=null;
        	String[] date_arr=null;
        	
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
        	        area_arr=getDAAreaFromLink(link);		
                    Code_A=area_arr[0];
                    Code_B=area_arr[1];
                    Code_A=getDecodeUrl(Code_A);
                    Code_B=getDecodeUrl(Code_B);
                    Code_A=Code_A.replaceAll("&", "");
                    Code_B=Code_B.replaceAll("&", "");
                    adate_str=TimeOpera.getDateFromStr(atime);
                    atime_str=TimeOpera.getTimeFromStr(atime);
                    if(SSO.tnoe(Code_A)&&SSO.tnoe(Code_B)&&SSO.tnoe(area)&&SSO.tnoe(adate_str)&&SSO.tnoe(atime_str))
                    {
                    	area=area.trim();
                    	adate_str=adate_str.trim();
                    	atime_str=atime_str.trim();
                    	Code_A=Code_A.trim();
                    	Code_B=Code_B.trim();
                    	date_arr=getDADateFromLink(link);
                    	word.set(area);
                    	word1.set(adate_str+"\001"+atime_str+"\001"+sip+"\001"+dip+"\001"+Code_A+"\001"+Code_B+"\001"+cookie+"\001"+loc+"\001"+agent+"\001"+date_arr[0]+"\001"+date_arr[1]+"\001qunar");
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
		 * 获取出发日期和返回日期
		 * @param link
		 * @return  date_arr
		 */
		public String[] getDADateFromLink(String link)
		{
        	Pattern ac_pat=Pattern.compile("(?:(?:flight)|(?:ws)).qunar.com.*?(?:(?:searchDepartureTime)|(?:searchArrivalTime)|(?:departureDate)|(?:returnDate)|(?:fromDate)|(?:toDate)|(?:searchReturnTime))\\s*(?:(?:=)|(?:%3D))\\s*([\\-0-9]*)\\s*(?:(?:&)|(?:%26))\\s*(?:(?:searchDepartureTime)|(?:searchArrivalTime)|(?:departureDate)|(?:returnDate)|(?:fromDate)|(?:toDate)|(?:searchReturnTime))\\s*(?:(?:=)|(?:%3D))\\s*([\\-0-9]*)");
        	Matcher ac_mat=null;
			ac_mat=ac_pat.matcher(link);
					
        	Pattern ac_pat2=Pattern.compile("(?:(?:flight)|(?:ws)).qunar.com.*?INT\\s*\\|\\s*(?:(?:ROUND)|(?:ONEW))\\s*\\|\\s*([\\-0-9]*)\\s*\\|\\s*([\\-0-9]*)\\s*\\|");
        	Matcher ac_mat2=null;  

        	
			String date_a="";
			String date_b="";
			String[] date_arr=new String[2];
			
			if(ac_mat.find())
			{
				date_a=ac_mat.group(1);
				date_b=ac_mat.group(2);
				date_arr[0]=date_a;
				date_arr[1]=date_b;			
			}
			else
			{
				ac_mat2=ac_pat2.matcher(link);
    			if(ac_mat2.find())
    			{			
    				date_a=ac_mat2.group(1);
    				date_b=ac_mat2.group(2);
    				date_arr[0]=date_a;
    				date_arr[1]=date_b;	
    			}
			}
			
			if(!(SSO.tnoe(date_arr[0])))
			{
				date_arr[0]="";
			}
			
			if(!(SSO.tnoe(date_arr[1])))
			{
				date_arr[1]="";
			}
		
			return date_arr;
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
			System.err.println("Usage: QunarPairAreaMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "QunarPairAreaMR_" + day);
		job.setJarByClass(QunarPairAreaMR.class);
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
