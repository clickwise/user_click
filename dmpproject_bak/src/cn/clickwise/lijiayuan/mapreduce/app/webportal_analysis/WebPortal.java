package cn.clickwise.lijiayuan.mapreduce.app.webportal_analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
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
 * 从nstat记录提取hive表各字段，包括
 * 字段名	字段说明	示例
 * domain	域名	qq.com
 * subdomain	二级	news.qq.com  
 * ip_source	源ip	122.233.87.251
 * cookie	用户标识	uid=dd0c2c118d70d347905590ec204590de
 * location	用户所在的地区	浙江省宁波市	 
 * @author ljy
 */

public class WebPortal {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> 
	{
		private Text word = new Text();
		private Text word1 = new Text();
		//private Hashtable<String,String> host_web=getHost_web("/home/hadoop/lq/SWA/php/ljy/subhost.txt","UTF-8");
		private Hashtable<String,String> host_web=getJarFile();
		//private HashMap<String,String> host_web=getHost_web("C:/Users/PC/Desktop/旅游/subhost.txt","UTF-8");
		public static String request_day="";

		@Override
		public void map(Object key, Text value, Context context)  throws IOException, InterruptedException
		{
			String dns_line=value.toString().trim();
	      
            String split_regex="\001";
            String split_flag="\001";
            if(dns_line!=null&&dns_line.contains(split_regex));
            {
                String[] requestInfo=dns_line.split(split_regex);
                if(requestInfo.length==11)
                {
                	String domain="";
                	String domainInfo=requestInfo[5];
                	String subdomain="";
                	String ip_source=requestInfo[3];
                	String cookie=requestInfo[8];
                	String location=requestInfo[9];
                	if(SSO.tnoe(domainInfo))
                	{
                		domain=getWebName(domainInfo.trim());
                		if(SSO.tnoe(domain))
                		{
                			subdomain=getSubwebName(domainInfo.trim());
                		}
                	}
                	String request_info="";
                	if((SSO.tnoe(domain))&&SSO.tnoe(ip_source)&&SSO.tnoe(location))
                    {
                		domain=domain.trim();
                		if(subdomain!=null)
                		{
                    		subdomain=subdomain.trim();
                    	}
                		location=location.trim();
                		if(SSO.tnoe(cookie)&&cookie.contains("="))
                		{
                			cookie=cookie.substring(cookie.indexOf("=")+1);
                		}
                        request_info=domain+split_flag+subdomain+split_flag+ip_source+split_flag+cookie+split_flag+location;
                        word.set(request_info);
                        word1.set("");
                    	context.write(word, word1);	
                    }
                }
            }
        }
		
		/**
		 * 获取网站名称，如果是腾讯、新浪、网易、搜狐，则返回网站名称，否则返回空字符串
		 * @param domainInfo
		 * @return
		 */
		public String getWebName(String domainInfo)
		{
			String webname="";
			if(SSO.tnoe(domainInfo))
			{
				domainInfo=domainInfo.toLowerCase();
				if(domainInfo.endsWith("qq.com"))
				{
					webname="腾讯";
				}
				else if(domainInfo.endsWith("sina.com"))
				{
					webname="新浪";
				}
				else if(domainInfo.endsWith("sina.com.cn"))
				{
					webname="新浪";
				}
				else if(domainInfo.endsWith("163.com"))
				{
					webname="网易";
				}
				else if(domainInfo.endsWith("sohu.com"))
				{
					webname="搜狐";
				}
				else if(domainInfo.endsWith("sohu.lecai.com"))
				{
					webname="搜狐";
				}
			}
			return webname;
		}
		
		/**
		 * 获取二级频道名称。如果host_web有二级域名信息，则返回二级频道名称，否则返回NA
		 * @param hostInfo 域名信息
		 * @return
		 */
		public String getSubwebName(String hostInfo)
		{
			String subwebName="NA";
			hostInfo=hostInfo.toLowerCase();
			if(hostInfo.contains("."))
			{
				String[] splitedStr=hostInfo.split("\\.");
				String hoststr="";
				if(splitedStr.length>=4)
				{
					for(int i=3;i>=0;i--)
					{
						hoststr=splitedStr[i]+"."+hoststr;
					}
					hoststr=hoststr.substring(0,hoststr.lastIndexOf(".")).trim();
					if(host_web.keySet().contains(hoststr))
					{
						subwebName=host_web.get(hoststr);
					}
					else
					{
						for(int i=2;i>=0;i--)
						{
							hoststr=splitedStr[i]+"."+hoststr;
						}
						hoststr=hoststr.substring(0,hoststr.lastIndexOf(".")).trim();
						if(host_web.keySet().contains(hoststr))
						{
							subwebName=host_web.get(hoststr);
						}
					}
				}
				else if(splitedStr.length==3)
				{
					for(int i=2;i>=0;i--)
					{
						hoststr=splitedStr[i]+"."+hoststr;
					}
					hoststr=hoststr.substring(0,hoststr.lastIndexOf(".")).trim();
					if(host_web.keySet().contains(hoststr))
					{
						subwebName=host_web.get(hoststr);
					}
				}
			}
			return subwebName;
		}
		
		/**
		 * 格式化访问时间，将原有格式"yyyy-MM-dd HH:mm:ss"转化为"yyyyMMdd"
		 * @param atime
		 * @return
		 */
		public String formatTime(String atime)
		{
			String formatedTime="NA";
			if(SSO.tnoe(atime))
			{
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
				try 
				{
					Date date =new Date();
					date=sdf1.parse(atime);
					formatedTime=sdf2.format(date);
					formatedTime=formatedTime.substring(0,8);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			return formatedTime;
		}
	
		/**
		 * 读取二级频道及其对应的域名
		 * @param filePath  文件路径
		 * @param encoding  文件编码格式
		 * @return
		 */
		public Hashtable<String,String> getHost_web(String filePath,String encoding)
		{
			Hashtable<String,String> host_webs=new Hashtable<String,String>();
			try
			{
				FileInputStream inputStream = new FileInputStream(filePath);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,encoding));
				String line=br.readLine();
				while(SSO.tnoe(line))
				{
					String subhost=line.substring(0,line.indexOf("\t")).trim();
					String webname=line.substring(line.indexOf("\t")+1).trim();
					host_webs.put(subhost, webname);
					line=br.readLine();
				}
			    br.close();
			    inputStream.close();
			}
			catch (Exception e) 
			{
				System.out.println("error!");
				e.printStackTrace();
			}
			return host_webs;
		}
	
		public Hashtable<String,String> getJarFile() 
		{  
			Hashtable<String,String> host_webs=new Hashtable<String,String>();
			try
			{
		        InputStream in=this.getClass().getResourceAsStream("/subhost.txt");//读jar包根目录下的subhost.txt</span><span>  
		        Reader f = new InputStreamReader(in);         
		        BufferedReader fb = new BufferedReader(f);  
		        String line = fb.readLine();  
		        while(SSO.tnoe(line)) 
		        {  
		        	String subhost=line.substring(0,line.indexOf("\t")).trim();
					String webname=line.substring(line.indexOf("\t")+1).trim();
					host_webs.put(subhost, webname);
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
	        return host_webs;  
	    }
	}


	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: WebPortal <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "WebPortal_" + day);
		job.setJarByClass(WebPortal.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
