package cn.clickwise.liqi.mapreduce.app.video_analysis;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
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
import cn.clickwise.liqi.str.edcode.CharsetConv;
import cn.clickwise.liqi.str.edcode.UrlCode;
import cn.clickwise.liqi.str.regex.RegexDB;
import cn.clickwise.liqi.str.regex.RegexFind;
import cn.clickwise.liqi.time.utils.TimeOpera;

/**
 * 从各大视频网站的视频链接提取 视频名称 视频url 视频refer等
 * @author zkyz
 */
public class VideoMR {

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
          	String[] csh_arr=null;
          	
        	String city="";
        	String source_host="";
        	
        	String info="";
           	String adate_str="";
        	String atime_str="";
        	
            String video_name="";
            String video_url="";
            String video_refer="";
            String host_name="";
            String rn="";//regular name
            String sn="";//sub name
            String[] name_arr=null;
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
        			csh_arr=null;
                    if(SSO.tnoe(atime)){
                        adate_str=TimeOpera.getDateFromStr(atime);
                        atime_str=TimeOpera.getTimeFromStr(atime);
                    }
                    csh_arr=getVideoInfo(link);
        			if(csh_arr!=null)
        			{
        				video_name=csh_arr[0].trim();
        				video_url=csh_arr[1].trim();
        				video_refer=csh_arr[2].trim();
        				host_name=csh_arr[3].trim();
        				if(SSO.tnoe(area)&&SSO.tnoe(video_name)&&SSO.tnoe(host_name))
        				{      
        					name_arr=name_split(video_name);
        					rn=name_arr[0];
        					sn=name_arr[1];
        					if(SSO.tnoe(rn))
        					{
        					  area=area.trim();
                        	  adate_str=adate_str.trim();
                        	  atime_str=atime_str.trim();
                        	  city=city.toLowerCase();
        					  word.set(area);
        					  word1.set(adate_str+"\001"+atime_str+"\001"+sip+"\001"+dip+"\001"+rn+"\001"+sn+"\001"+video_url+"\001"+video_refer+"\001"+cookie+"\001"+loc+"\001"+agent+"\001"+host_name);
        					  context.write(word, word1);
        					}
        				}        	
        			}			
        		}		 		
        	} 		             				
		}
				
        public String[] getVideoInfo(String line)
        {
           String host_name="";
           if((line.indexOf("atm.youku.com"))>0)
           {
        	   host_name="youku";
           }
           else if((line.indexOf("dc.letv.com"))>-1)
           {
        	   host_name="letv";
        	  // System.out.println("letv:"+line);
           }
           else if((line.indexOf("aty.sohu.com"))>0)
           {
        	   host_name="sohu";
           }
           
           HashMap<String,String> vregex=new HashMap<String,String>();
           vregex.put("youku_vn","\\&tt\\=("+RegexDB.getUrlCodeRg()+"?)\\&");
           vregex.put("youku_vu","\\&pu\\=("+RegexDB.getUrlRg()+"?)\\&");
           vregex.put("youku_vr","\\&ref\\=("+RegexDB.getUrlRg()+"?)\\&");
           
           vregex.put("sohu_vn","\\&ti\\=("+RegexDB.getUrlCodeRg()+"?)\\&");
           vregex.put("sohu_vu","\\&(?:(?:url)|(?:pageUrl))\\=("+RegexDB.getUrlRg()+"?)\\&");
           vregex.put("sohu_vr","\\&pagerefer\\=("+RegexDB.getUrlRg()+"?)\\&");   
           
          // vregex.put("letv_vn", "");
           vregex.put("letv_vu", "\\&(?:(?:url)|(?:pageUrl))\\=("+RegexDB.getUrlRg()+"?)\\&");
           vregex.put("letv_vr", "\\&ref\\=("+RegexDB.getUrlRg()+"?)\\&");
           
           String video_name="";
           String video_url="";
           String video_refer="";
               
           String[] rgr_arr=null;
           if(host_name.equals("youku"))
           {
        	   video_name="";
        	   rgr_arr=RegexFind.find(vregex.get("youku_vn"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_name=rgr_arr[0];
        	   }
        	   
        	   video_url="";
        	   rgr_arr=RegexFind.find(vregex.get("youku_vu"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_url=rgr_arr[0];
        	   }
        	   
        	   video_refer="";
        	   rgr_arr=RegexFind.find(vregex.get("youku_vr"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_refer=rgr_arr[0];
        	   }       	   	   
           }
           else if(host_name.equals("letv"))
           {
        	   video_name="";
          	   video_url="";
        	   rgr_arr=RegexFind.find(vregex.get("letv_vu"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_url=rgr_arr[0];
        	   }
        	   
        	   video_refer="";
        	   rgr_arr=RegexFind.find(vregex.get("letv_vr"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_refer=rgr_arr[0];
        	   } 
        	   
        	  // System.out.println("letv  url: "+video_url+" refer :"+video_refer);
           }
           else if(host_name.equals("sohu"))
           {
        	   video_name="";
        	   rgr_arr=RegexFind.find(vregex.get("sohu_vn"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_name=rgr_arr[0];
        	   }
        	   
        	   video_url="";
        	   rgr_arr=RegexFind.find(vregex.get("sohu_vu"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_url=rgr_arr[0];
        	   }
        	   
        	   video_refer="";
        	   rgr_arr=RegexFind.find(vregex.get("sohu_vr"), line, 1);
        	   if(rgr_arr!=null)
        	   {
        		   video_refer=rgr_arr[0];
        	   }    
           }
           
           String[] res_arr=new String[4];
           for(int i=0;i<res_arr.length;i++)
           {
        	   res_arr[i]="";
           }
           video_name=UrlCode.getDecodeUrl(video_name);
           if(SSO.tioe(video_name))
           {
        	   video_name= getNameFromRefer(line,video_refer);
           }
           video_url=UrlCode.getDecodeUrl(video_url);
           video_refer=UrlCode.getDecodeUrl(video_refer);
           
           video_name=video_name.replaceAll("《", "");
           video_name=video_name.replaceAll("》", "");
           video_name=video_name.trim();
           res_arr[0]=video_name;
           res_arr[1]=video_url;
           res_arr[2]=video_refer;
           res_arr[3]=host_name;
                          
           return res_arr;
        }
        
        public String[] name_split(String video_name)
        {
        	String[] res_arr=new String[2];
        	String[] seg_arr=video_name.split("\\s+");
         	String extra_info="";
        	if(seg_arr.length>1)
        	{
        	  String[] extra_arr=RegexFind.find("((?:(?:\\s)|(?:国语)|(?:第[\\d]+集)|(?:第[\\d]+讲)|(?:\\d))*\\s.*)", video_name, 1);
        	  if(extra_arr!=null)
        	  {
        		extra_info=extra_arr[0];    		
        	  }	
        	}
        	else
        	{
          	  String[] extra_arr=RegexFind.find("((?:(?:国语)|(?:第[\\d]+集)|(?:第[\\d]+讲)|(?:\\d)).*)", video_name, 1);
          	  if(extra_arr!=null)
          	  {
          		extra_info=extra_arr[0];    		
          	  }	
        	}
        	String rn=SSO.replaceLast(video_name, extra_info, "");
        	rn=rn.trim();
        	extra_info=extra_info.trim();
        	res_arr[0]=rn;
        	res_arr[1]=extra_info;
        	return res_arr;
        }
        
        public String getNameFromRefer(String line,String refer)
        {
        
        	String[] extra_arr=null;
        	String extra_info="";
       
        	if(refer.indexOf("sogou")>0)
        	{
        		refer=UrlCode.getDecodeUrl(refer,"GB2312");
                extra_arr=RegexFind.find("query\\=("+RegexDB.getChineseRg()+"?)\\&", refer, 1);
        	    if(extra_arr!=null)
        	    {
        		   extra_info=extra_arr[0]; 
        		   try{
        		   extra_info=CharsetConv.gb2312_utf8(extra_info);
        		   }
        		   catch(Exception e)
        		   {
        			   
        		   }
        	     }
        	    
        	}
        	else
        	{
        	   refer=UrlCode.getDecodeUrl(refer);
               extra_arr=RegexFind.find("(?:(?:query)|(?:wd)|(?:word))\\=("+RegexDB.getChineseRg()+"?)\\&", refer, 1);
      	      if(extra_arr!=null)
      	      {
      		     extra_info=extra_arr[0];    		
      	       }	
        	}
      	  
      	  //从lrd获取视频名
      	  String lrd_url="";
  	      if(SSO.tioe(extra_info))
  	      {
  		    lrd_url=RegexFind.findSingle("\\&lrd\\=("+RegexDB.getUrlRg()+"?)\\&", line);
  		    if(SSO.tioe(lrd_url))
  		    {
  		    	return "";
  		    }
  	
  		     if(lrd_url.indexOf("baidu")>0)
  		     {
  			    lrd_url=UrlCode.getDecodeUrl(lrd_url,"GB2312");
  		        extra_info=RegexFind.findSingle("(?:(?:query)|(?:wd)|(?:word))\\=("+RegexDB.getChineseRg()+"?)\\&", lrd_url);
     		    try{
     		    extra_info=CharsetConv.gb2312_utf8(extra_info);
     		    }
     		    catch(Exception e)
     		    {
     			   
     		    }
  		     }
  		     else if(lrd_url.indexOf("sogou")>0)
  		     {
			     lrd_url=UrlCode.getDecodeUrl(lrd_url);
	  		       extra_info=RegexFind.findSingle("(?:(?:query)|(?:wd))\\=("+RegexDB.getChineseRg()+"?)\\&", lrd_url);
  		     }
  		    	 
  	      } 
        	return extra_info;
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
			System.err.println("Usage: VideoMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "VideoMR_" + day);
		job.setJarByClass(VideoMR.class);
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
