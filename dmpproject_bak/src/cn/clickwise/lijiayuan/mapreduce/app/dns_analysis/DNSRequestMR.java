package cn.clickwise.lijiayuan.mapreduce.app.dns_analysis;

import java.io.IOException;
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

public class DNSRequestMR {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
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
                	if((SSO.tnoe(area_id))&&(SSO.tnoe(request_time))&&(SSO.tnoe(src_ip))&&(SSO.tnoe(dest_ip))&&(SSO.tnoe(area_nameA))&&(SSO.tnoe(area_nameB))&&(SSO.tnoe(user_cookie))&&(SSO.tnoe(user_loca))&&(SSO.tnoe(user_agent)))
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
                        request_info=area_id+"\001"+request_time+"\001"+src_ip+"\001"+area_nameA+"\001"+area_nameB+"\001"+user_cookie+"\001"+user_loca+"\001"+user_agent;
                        System.out.println(request_info);
                        word.set(request_info);
                    	context.write(word, null);	
                    }
                }
            }
        }
		
		public String getArea(String areaInfo)
		{
			String areas="";
			if(areaInfo.contains("flights.ctrip.com/Domestic/Search/BuildOrderDynamicShowSection"))
			{
				String dcity=areaInfo.substring(areaInfo.indexOf("=")+1,areaInfo.indexOf("&"));
				String acity=areaInfo.substring(areaInfo.lastIndexOf("=")+1);
				areas=dcity+"\t"+acity;
			}
			return areas;
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
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
