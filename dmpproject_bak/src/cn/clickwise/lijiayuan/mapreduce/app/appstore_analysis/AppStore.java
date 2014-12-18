package cn.clickwise.lijiayuan.mapreduce.app.appstore_analysis;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import cn.clickwise.liqi.str.edcode.UrlCode;


/**
 * 从nstat记录提取hive表各字段，包括
 * 字段名	字段说明	示例
 * websit	手机应用网站名称	百度移动应用
 * appname	应用名称	墨迹天气  
 * ip_source	源ip	122.233.87.251
 * cookie	用户标识	dd0c2c118d70d347905590ec204590de
 * agent	访问浏览器	Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/4.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; SE 2.X MetaSr 1.0
 * location	用户所在的地区	浙江省宁波市	 
 * @author ljy
 */

public class AppStore {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> 
	{
		private Text word = new Text();
		private Text word1 = new Text();
		private Hashtable<String,String> host_appweb=getJarFile("app_host.txt");
		private Hashtable<String,String> appcode_baidu=getJarFile("appcode_baidu.txt");
		private Hashtable<String,String> appcode_anzhi=getJarFile("appcode_anzhi.txt");
//		private Hashtable<String,String> host_appweb=getJarFile1("C:/Users/PC/Desktop/行业报告/APP/app_host.txt","UTF-8");
//		private Hashtable<String,String> appcode_baidu=getJarFile1("C:/Users/PC/Desktop/行业报告/APP/appcode_baidu.txt","UTF-8");
//		private Hashtable<String,String> appcode_anzhi=getJarFile1("C:/Users/PC/Desktop/行业报告/APP/appcode_anzhi.txt","UTF-8");
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
                	String website="";
                	String domainInfo=requestInfo[5];
                	String url=requestInfo[5]+requestInfo[6];
                	String appname="";
                	String ip_source=requestInfo[3];
                	String cookie=requestInfo[8];
                	String location=requestInfo[9];
                	String accesstool=getAccessTool(requestInfo[10]);
                	if(SSO.tnoe(domainInfo))
                	{
                		website=getWebName(domainInfo.trim());
                	}
        			String appnameInfo=getAppName(url);
        			appname=appnameInfo.substring(appnameInfo.indexOf("\t")+1);
        			if(!SSO.tnoe(website))
        			{
        				website=appnameInfo.substring(0,appnameInfo.indexOf("\t"));
        			}
                	String app_info="";
                	if((SSO.tnoe(website))&&SSO.tnoe(ip_source)&&SSO.tnoe(location)&&SSO.tnoe(accesstool))
                    {
                		website=website.trim();
                		if(SSO.tnoe(appnameInfo))
                		{
                			appname=appname.trim();
                    	}
                		location=location.trim();
                		if(SSO.tnoe(cookie)&&cookie.contains("="))
                		{
                			cookie=cookie.substring(cookie.indexOf("=")+1);
                		}
                        app_info=website+split_flag+appname+split_flag+ip_source+split_flag+cookie+split_flag+location+split_flag+accesstool;
                        word.set(app_info);
                        word1.set("");
                    	context.write(word, word1);	
                    }
                }
            }
        }
				
		/**
		 * 获取手机应用网站名称
		 * @param host  域名
		 * @return  网站名称
		 */
		public String getWebName(String host)
		{
			String webname="";
			if(SSO.tnoe(host))
			{
				host=host.trim();
				for(String webhost:host_appweb.keySet())
				{
					if(host.endsWith("."+webhost)||host.equals(webhost))
					{
						webname=host_appweb.get(webhost);
					}
				}
			}
			return webname;
		}
					
		public Hashtable<String,String> getJarFile(String fileName) 
		{  
			Hashtable<String,String> ht=new Hashtable<String,String>();
			try
			{
		        InputStream in=this.getClass().getResourceAsStream("/"+fileName);//读jar包根目录下的fileName</span><span>  
		        Reader f = new InputStreamReader(in);         
		        BufferedReader fb = new BufferedReader(f);  
		        String line = fb.readLine();  
		        while(SSO.tnoe(line)) 
		        {  
		        	String key=line.substring(0,line.indexOf("\t")).trim();
					String value=line.substring(line.indexOf("\t")+1).trim();
					ht.put(key, value);
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
	        return ht;  
	    }
	
		public Hashtable<String,String> getJarFile1(String fileName,String encoding) 
		{  
			Hashtable<String,String> host_webs=new Hashtable<String,String>();
			try
			{
				FileInputStream inputStream = new FileInputStream(fileName);
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
		
		/**
		 * 通过url获取app名称
		 * @param url
		 * @return app名称
		 */
		public String getAppName(String url)
		{
			String appname="NA";
			String website="";
			if(SSO.tnoe(url)&&(url.endsWith(".apk")||url.endsWith(".gpk")||url.endsWith(".ipa")))
			{
				//"([\\s\\S]*?)<div class=\"moduleContent\">([\\s\\S]*?)<!--部位类别 结束-->([\\s\\S]*?)"
				String exp_baidu="([\\s\\S]*?)gdown.baidu.com/data([\\s\\S]*?)";
				String exp_xiaomi1="([\\s\\S]*?)market.xiaomi.com/download/AppStore([\\s\\S]*?)";
				String exp_xiaomi2="([\\s\\S]*?)market.mi-img.com/download/AppStore([\\s\\S]*?)";
				String exp_anzhi1="([\\s\\S]*?)apk.anzhi.com/data1/apk([\\s\\S]*?)";
				String exp_anzhi2="([\\s\\S]*?)apk.anzhi.com/data2/apk([\\s\\S]*?)";
				String code="";
				if(isMatchUrl(url,exp_baidu))
				{
					if(url.contains("_"))
					{
						code=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("_"));
					}
					else
					{
						code=url.substring(url.lastIndexOf("/")+1);
					}
					if(SSO.tnoe(code))
					{
						appname=appcode_baidu.get(code);
						website="百度移动应用";
					}
				}
				else if(isMatchUrl(url,exp_xiaomi1)||isMatchUrl(url,exp_xiaomi2))
				{
					if(url.contains("_"))
					{
						code=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("_"));
					}
					else
					{
						code=url.substring(url.lastIndexOf("/")+1);
					}
					if(SSO.tnoe(code))
					{
						appname=UrlCode.getDecodeUrl(code, "UTF-8");
						if(!appname.equals(code)&&isReadable(appname))
						{
							if(appname.contains("_"))
							{
								appname=appname.substring(0,appname.indexOf("_"));
							}
							if(appname.contains(".apk"))
							{
								appname=appname.substring(0,appname.indexOf(".apk"));
							}
							website="小米商店";
						}
					}
				}
				else if(isMatchUrl(url,exp_anzhi1)||isMatchUrl(url,exp_anzhi2))
				{
					if(url.contains("_"))
					{
						code=url.substring(url.lastIndexOf("/")+1,url.lastIndexOf("_"));
					}
					else
					{
						code=url.substring(url.lastIndexOf("/")+1);
					}
					if(SSO.tnoe(code))
					{
						appname=appcode_anzhi.get(code);
						website="安智市场";
					}
				}
			}
			if(appname==null||appname.trim().equals("")||appname.equals("null"))
			{
				appname="NA";
			}
			return website+"\t"+appname;
		}
		
		/**
		 * 获取用户访问的工具（移动端/PC端）
		 * @param agent 浏览器
		 * @return 移动端/PC端
		 */
		public String getAccessTool(String agent)
		{
			String tool="NA";
			if(SSO.tnoe(agent))
			{
				agent=agent.toLowerCase();
				if(agent.equals("(null)"))	
				{
					tool="NA";
				}
				else if(agent.contains("android")||agent.contains("iphone")||agent.contains("ipad")||agent.contains("ipod"))
				{
					tool="移动端";
				}
				else
				{
					tool="PC端";
				}
			}
			return tool;
		}
		
		/**
		 * 判断一个txt 是否与给定的正则表达式reg相匹配
		 * @param txt
		 * @param reg
		 * @return 是否匹配（true/false）
		 */
		public boolean isMatchUrl(String txt, String reg)
		{
			boolean isMatch=false;
			Pattern p = Pattern.compile(reg);
			Matcher m = p.matcher(txt);
			if (m.matches())
			{
				isMatch=true;
			}
			return isMatch;
		}
	
		/**
		 * 判断解码后的应用名是否乱码
		 * @param string
		 * @return 乱码：false； 无乱码：true
		 */
		public static boolean isReadable(String string)
		{
			boolean boo=true;
			String str=string;
			String chineseStr=getChineseStr(str);
			if(SSO.tnoe(chineseStr))
			{
				for(String chinesechar:chineseStr.split("\t"))
				{
					if(SSO.tnoe(chinesechar))
					{
						str=str.replaceAll(chinesechar, "");
					}
				}
			}
			boo=isReadableStr(str);
			return boo;
		}
		
		/**
		 * 判断一个不含有汉字的字符串是否乱码 
		 * @param str
		 * @return 乱码：false； 不乱码：true
		 */
		public static boolean isReadableStr(String  str) 
		{
			boolean readable=true;
			if(str!=null)
			{
		        String  retStr  =  str; 
		        byte  b[]; 
		        try  
		        {
		        	b  =  str.getBytes("ISO8859_1"); 
		        	for  (int  i  =  0;  i  <  b.length;  i++) 
		        	{
		        		byte  b1  =  b[i]; 
		        		if  (b1  ==  63) 
		         		{
		        			readable=false;
		        			break;    //1  
		         		}
		         		else  if  (b1  >  0) 
		         		{
		         			continue;//2 
		                }
		         		else  if  (b1  <  0)  
		         		{        //不可能为0，0为字符串结束符 
		         			readable=false;
		         			break;
		         		}
		        	}
		        }  
		        catch  (Exception  e)
		        { 
		        	e.printStackTrace();    //To  change  body  of  catch  statement  use  File    |  Settings    |  File  Templates. 
		        } 
		    }
	        return  readable; 
	    }

		/**
		 * 获取一个字符串中的所有汉字
		 * @param str
		 * @return 汉字
		 */
		public static String getChineseStr(String str)
		{
			String chinesestr="";
			String regEx = "[\\u4e00-\\u9fa5]";
			Pattern p = Pattern.compile(regEx); 
		    Matcher m = p.matcher(str); 
		    while (m.find())
		    {
		    	chinesestr=chinesestr+m.group()+"\t";
		    } 
		    return chinesestr;
		}


	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: AppStore <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "AppStore_" + day);
		job.setJarByClass(AppStore.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

