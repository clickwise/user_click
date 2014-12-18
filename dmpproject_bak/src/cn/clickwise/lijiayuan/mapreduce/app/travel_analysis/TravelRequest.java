package cn.clickwise.lijiayuan.mapreduce.app.travel_analysis;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.str.basic.SSO;


/**
 * 从dns记录提取hive表各字段，包括
 * 字段名	字段说明	示例
 * areaId	地区编号	t6401   
 * atime	访问时间	2014-02-23 22:12:00
 * sip	源ip	  122.233.87.251
 * dip	目的ip	220.181.111.191
 * are_a	地区a的名称	天津
 * are_b	地区b的名称	桂林
 * cookie	用户标识	uid=dd0c2c118d70d347905590ec204590de
 * loc	用户所在的地区	浙江省宁波市
 * agent	用户访问客户端	Mozilla/5.0 (Windows NT 6.1; rv:27.0) Gecko/20100101 Firefox/27.0	 
 * @author ljy
 */

public class TravelRequest {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		@Override
		public void map(Object key, Text value, Context context)  throws IOException, InterruptedException
		{
			String dns_line=value.toString().trim();
	      
            String request_regex="\001";
            if(dns_line!=null&&dns_line.contains(request_regex));
            {
            	System.out.println(dns_line);
                String[] requestInfo=dns_line.split(request_regex);
                if(requestInfo.length==11)
                {
                	
                	String area_id=requestInfo[0];
                	String request_time=requestInfo[1];
                	String src_ip=requestInfo[3];
                	String dest_ip=requestInfo[4];
                	String areaInfo=requestInfo[5]+requestInfo[6];
                	areaInfo=getArea(areaInfo);
                	String area_nameA="";
                	String area_nameB="";
                	if(areaInfo!=null&&!areaInfo.trim().equals(""))
                	{
                    	area_nameA=areaInfo.substring(0,areaInfo.indexOf("\t"));
                        area_nameB=areaInfo.substring(areaInfo.indexOf("\t")+1);
                	}
                	String user_cookie=requestInfo[8];
                	String user_loca=requestInfo[9];
                	String user_agent=requestInfo[10];
                	
                	String request_info="";
                	if((SSO.tnoe(area_id))&&(SSO.tnoe(request_time))&&(SSO.tnoe(src_ip))&&(SSO.tnoe(dest_ip))&&(SSO.tnoe(area_nameA))&&(SSO.tnoe(area_nameB))&&(SSO.tnoe(user_loca))&&(SSO.tnoe(user_agent)))
                    {
                		area_id=area_id.trim();
                    	request_time=request_time.trim();
                    	src_ip=src_ip.trim();
                    	dest_ip=dest_ip.trim();
                    	area_nameA=area_nameA.trim();
                    	area_nameB=area_nameB.trim();
                    	user_cookie=user_cookie.trim();
                    	user_loca=user_loca.trim();
                    	user_agent=user_agent.trim();
                        request_info=area_id+"\001"+request_time+"\001"+src_ip+"\001"+dest_ip+"\001"+area_nameA+"\001"+area_nameB+"\001"+user_cookie+"\001"+user_loca+"\001"+user_agent;
                        word.set(request_info);
                        word1.set("");
                    	context.write(word, word1);	
                    }
                }
            }
        }
		
		public String getArea(String areaInfo)
		{
			String areas="";
			areaInfo=areaInfo.toLowerCase();
			if(areaInfo.contains("dcity=")&&areaInfo.contains("acity="))
			{
				String dcity=areaInfo.substring(areaInfo.indexOf("dcity=")+6,areaInfo.indexOf("&acity"));
				String acityInfo=areaInfo.substring(areaInfo.indexOf("acity"));
				String acity="";
				if(acityInfo.contains("&"))
				{
					acity=acityInfo.substring(acityInfo.indexOf("acity=")+6,acityInfo.indexOf("&"));
				}
				else
				{
					acity=acityInfo.substring(acityInfo.indexOf("acity=")+6);
				}
				areas=dcity+"\t"+acity;
			}
			else if(areaInfo.contains("flights.ctrip.com/booking/"))
			{
				String reg="([\\s\\S]*?)flights.ctrip.com/booking/([\\s\\S]*?)-day([\\s\\S]*?)";
				Pattern p = Pattern.compile(reg);
				Matcher m = p.matcher(areaInfo);
				if (m.matches())
				{
					areas = m.group(2);
					if(SSO.tnoe(areas)&&areas.contains("-"))
					{
						String dcity=areas.substring(0,areas.indexOf("-"));
						String acity=areas.substring(areas.indexOf("-")+1);
						areas=dcity+"\t"+acity;
					}
				}
			}
			return areas.toUpperCase();
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
		job.setJarByClass(TravelRequest.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
