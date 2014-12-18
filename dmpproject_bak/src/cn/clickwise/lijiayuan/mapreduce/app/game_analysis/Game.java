package cn.clickwise.lijiayuan.mapreduce.app.game_analysis;

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
 * websit	游戏网站名称	腾讯游戏
 * gamename	游戏名称	暗黑世界  
 * ip_source	源ip	122.233.87.251
 * cookie	用户标识	uid=dd0c2c118d70d347905590ec204590de
 * location	用户所在的地区	浙江省宁波市	 
 * @author ljy
 */

public class Game {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> 
	{
		private Text word = new Text();
		private Text word1 = new Text();
		private Hashtable<String,String> host_gameweb=getJarFile("gameweb_host.txt");
		private Hashtable<String,String> game_code=getJarFile("game_code.txt");
//		private Hashtable<String,String> host_gameweb=getJarFile1("C:/Users/PC/Desktop/行业报告/游戏/gameweb_host.txt","UTF-8");
//		private Hashtable<String,String> game_code=getJarFile1("C:/Users/PC/Desktop/行业报告/游戏/game_code.txt","UTF-8");
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
                	String path_url=requestInfo[6];
                	String gamename="";
                	String ip_source=requestInfo[3];
                	String cookie=requestInfo[8];
                	String location=requestInfo[9];
                	if(SSO.tnoe(domainInfo))
                	{
                		website=getWebName(domainInfo.trim());
                		if(SSO.tnoe(website)&&(website.equals("百度游戏")||website.equals("腾讯游戏")||website.equals("17173")))
                		{
                			gamename=getGameName(website,domainInfo.trim(),path_url);
                		}
                		else
                		{
                			gamename="NA";
                		}
                	}
                	String game_info="";
                	if((SSO.tnoe(website))&&SSO.tnoe(ip_source)&&SSO.tnoe(location))
                    {
                		website=website.trim();
                		if(gamename!=null)
                		{
                    		gamename=gamename.trim();
                    	}
                		location=location.trim();
                		if(SSO.tnoe(cookie)&&cookie.contains("="))
                		{
                			cookie=cookie.substring(cookie.indexOf("=")+1);
                		}
                        game_info=website+split_flag+gamename+split_flag+ip_source+split_flag+cookie+split_flag+location;
                        word.set(game_info);
                        word1.set("");
                    	context.write(word, word1);	
                    }
                }
            }
        }
				
		/**
		 * 获取游戏网站名称
		 * @param host  域名
		 * @return  网站名称
		 */
		public String getWebName(String host)
		{
			String webname="";
			if(SSO.tnoe(host))
			{
				host=host.trim();
				for(String webhost:host_gameweb.keySet())
				{
					if(host.endsWith("."+webhost)||host.equals(webhost))
					{
						webname=host_gameweb.get(webhost);
					}
				}
			}
			return webname;
		}

		/**
		 * 获取游戏名称
		 * @param webname 游戏网站名称
		 * @param host 域名
		 * @param path_url url资源路径
		 * @return 游戏名称
		 */
		public String getGameName(String webname,String host,String path_url)
		{
			String gamename="NA";
			String code_web=getCode(webname,host,path_url);
			if(SSO.tnoe(code_web)&&!code_web.startsWith("\t")&&!code_web.endsWith("\t"))
			{
				String web=code_web.substring(code_web.indexOf("\t")+1);
				String code=code_web.substring(0,code_web.indexOf("\t"));
				for(String name:game_code.keySet())
				{
					if(name.contains(web)&&game_code.get(name).equals(code))
					{
						gamename=name;
						break;
					}
				}
				if(SSO.tnoe(gamename)&&gamename.contains("|"))
				{
					gamename=gamename.substring(0,gamename.indexOf("|"));
				}
			}
			return gamename;
		}
		
		/**
		 * 获取游戏编码
		 * @param webname 游戏网站名称
		 * @param host 域名
		 * @param path_url  url(host_path)中的路径
		 * @return 游戏编码以及游戏网站缩写
		 */
		public String getCode(String webname,String host,String path_url)
		{
			String code="";
			String web="";
			if(SSO.tnoe(webname))
			{
				webname=webname.trim();
				if(webname.equals("百度游戏"))
				{
					web="百度";
					if(SSO.tnoe(path_url))
					{
						path_url=path_url.trim();
						if(path_url.startsWith("/games/")&&path_url.indexOf("/games/")+7<path_url.length())
						{
							code=path_url.substring(path_url.indexOf("/games/")+7);
							if(code.contains("/")&&(code.indexOf("/")>0))
							{
								code=code.substring(0,code.indexOf("/"));
							}
						}
						else
						{
							if(path_url.length()>1)
							{
								code=path_url.substring(1);
								if(code.contains("/")&&(code.indexOf("/")>0))
								{
									code=code.substring(0,code.indexOf("/"));
								}
							}
						}
					}
				}
				else
				{
					if(webname.equals("17173"))
					{
						web="17173";
						if(SSO.tnoe(host)&&host.contains(".17173.com"))
						{
							code=host.substring(0,host.indexOf(".17173.com"));
							if(code.contains(".")&&(code.lastIndexOf(".")+1<code.length()))
							{
								code=code.substring(code.lastIndexOf(".")+1);
							}
						}
					}
					else if(webname.equals("腾讯游戏"))
					{
						web="腾讯";
						if(host.contains(".qq.com"))
						{
							code=host.substring(0,host.indexOf(".qq.com"));
							if(code.contains(".")&&(code.lastIndexOf(".")+1<code.length()))
							{
								code=code.substring(code.lastIndexOf(".")+1);
							}
						}
					}
				}
				if(SSO.tnoe(code))
				{
					code=deleteNoise(code);
				}
			}
			return code+"\t"+web;
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
		 * 去除游戏名称缩写中“_”“-”“.”及其后面的字符串
		 * @param codestr  游戏名称缩写
		 * @return 游戏名称缩写
		 */
		public String deleteNoise(String codestr)
		{
			String code=codestr;
			int deleteIndex=codestr.length();
			if(codestr.contains("_"))
			{
				if(codestr.indexOf("_")<deleteIndex)
				{
					deleteIndex=codestr.indexOf("_");
				}
			}
			if(codestr.contains("-"))
			{
				if(codestr.indexOf("-")<deleteIndex)
				{
					deleteIndex=codestr.indexOf("-");
				}
			}
			if(codestr.contains("."))
			{
				if(codestr.indexOf(".")<deleteIndex)
				{
					deleteIndex=codestr.indexOf(".");
				}
			}
			if(codestr.contains("%"))
			{
				if(codestr.indexOf("%")<deleteIndex)
				{
					deleteIndex=codestr.indexOf("%");
				}
			}
			if(codestr.contains("&"))
			{
				if(codestr.indexOf("&")<deleteIndex)
				{
					deleteIndex=codestr.indexOf("&");
				}
			}
			if(codestr.contains("="))
			{
				if(codestr.indexOf("=")<deleteIndex)
				{
					deleteIndex=codestr.indexOf("=");
				}
			}
			code=codestr.substring(0,deleteIndex);
			return code;
		}
	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: Game <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "Game_" + day);
		job.setJarByClass(Game.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}

