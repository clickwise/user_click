package cn.clickwise.liqi.mapreduce.app.radius_analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

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
 * 查找出所有访问用户纠错页面的账号
 * @author zkyz
 *
 */
public class CorrUserIDMR {
    
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			
		   String line=value.toString();
		   String[] seg_arr=null;
		   String cate_code="";
		   
		   String so_ip="";
		   String so_time="";
		   String so_host="";
		   String so_refer="";
		   String so_code="";
		   
		   String idu_info="";
		   String temp="";
		   
		   if(SSO.tnoe(line))
		   {
			   seg_arr=line.split("\001");
			   if(seg_arr.length>0)
			   {
			      cate_code=seg_arr[seg_arr.length-1];
			      cate_code=cate_code.trim();
			      if(cate_code.equals("SIP"))
			      {
					  so_ip="";
					  so_time="";
					  so_host="";
					  so_refer="";
					  so_code="";
			    	  if(seg_arr.length==5)
			    	  {
			    		  so_ip=seg_arr[0].trim();
			    		  so_time=seg_arr[1].trim();
			    		  so_host=seg_arr[2].trim();
			    		  so_refer=seg_arr[3].trim();
			    		  so_code=seg_arr[4].trim();
			    		  if(SSO.tnoe(so_ip))
			    		  {
			    			  word.set(so_ip);
			    			  word1.set(so_time+"\001"+so_refer+"\001"+so_code);
			    			  //System.out.println("SIP:"+so_ip+" "+so_time+"\001"+so_refer+"\001"+so_code);
			    			  context.write(word, word1);
			    		  }
			    	  }
			      }
			      else if(cate_code.equals("IDU"))
			      {
			    	  idu_info="";
			    	  so_ip=seg_arr[0].trim();
			    	  for(int j=1;j<seg_arr.length;j++)
			    	  {
			            temp=seg_arr[j].trim();
			            if(SSO.tnoe(temp))
			            {
			            	idu_info=idu_info+temp+"\001";
			            }
			    	  }
			    	  			    	  
			    	  idu_info=idu_info.trim(); 
		    		  if(SSO.tnoe(so_ip))
		    		  {
		    			  word.set(so_ip);
		    			  word1.set(idu_info);
		    			  //System.out.println("IDU:"+so_ip+" "+idu_info);
		    			  context.write(word, word1);
		    		  }   		  
			      }		      
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
			 String all_info_str="";
			 String ccode="";
			 String[] seg_arr=null;
			 String record_info="";
			 Record one_rec=null;
			 
			 String sid="";
			 String ssta="";
			 String stime="";
			 
			 String eid="";
			 String esta="";
			 String etime="";		 
			 
			 String[] rec_seg=null;
			 
			 ArrayList<Record> rec_list=new ArrayList<Record>();
			 ArrayList<String> user_list=new ArrayList<String>();
			 
			 String user_info="";
			 
			 while(it.hasNext())
			 {
			    all_info_str=it.next().toString();
			    if(!(SSO.tnoe(all_info_str)))
			    {
			    	continue;
			    }
			    all_info_str=all_info_str.trim();
			    seg_arr=all_info_str.split("\001");
			    if(seg_arr.length<1)
			    {
			    	continue;
			    }
			    ccode=seg_arr[seg_arr.length-1];
			    if(SSO.tnoe(ccode))
			    {
			    	ccode=ccode.trim();
			    	//System.out.println("ccode:"+ccode);
			    	if(ccode.equals("IDU"))
			    	{
			    	  for(int i=0;i<(seg_arr.length-1);i++)
			    	  {
			    		  record_info=seg_arr[i].trim();
			    		  if(!(SSO.tnoe(record_info)))
			    		  {
			    			  continue;
			    		  }
			    		  
			    		  rec_seg=record_info.split("\t");
			    		  //System.out.println("rec_seg.length:"+rec_seg.length);
			    		  if(rec_seg.length!=3)
			    		  {
			    			  continue;
			    		  }
			    				    		  
			    		  if(i%2==0)
			    		  {
			    	          sid=rec_seg[0].trim();
			    	          ssta=rec_seg[1].trim();
			    	          stime=rec_seg[2].trim();	
			    	          //System.out.println("sid:"+sid+" ssta:"+ssta+" stime:"+stime);
			    	          if(ssta.equals("1"))
			    	          {
			    			     one_rec=new Record();	    	          
			    	             one_rec.start_time=TimeOpera.str2long(stime);
			    	          }			    	         		  
			    		  }
			    		  
			    		  if(i%2==1)
			    		  {
			    	          eid=rec_seg[0].trim();
			    	          esta=rec_seg[1].trim();
			    	          etime=rec_seg[2].trim();	
			    	          //System.out.println("eid:"+eid+" esta:"+esta+" etime:"+etime+" sid:"+sid);
			    	          if(esta.equals("2"))
			    	          {    	          
			    	             one_rec.end_time=TimeOpera.str2long(etime);
			    	             if(eid.equals(sid))
			    	             {
			    	            	 one_rec.userId=sid;
			    	            	 rec_list.add(one_rec);      	 
			    	             }
			    	          }			    	         		  
			    		  } 
			    		  
			    	  }  	  	
			    	}
			    	else if(ccode.equals("SIP"))
			    	{
			    		user_info="";
			    		for(int i=0;i<(seg_arr.length-1);i++)
			    		{
			    			user_info=user_info+seg_arr[i].trim()+"\001";
			    		}
			    		user_info=user_info.trim();
			    		if(SSO.tnoe(user_info))
			    		{
			    			user_list.add(user_info);
			    		}
			    	}
	    	
			    }			    
			 }
			 
			 String[] user_seg=null;
			 String vtime="";
			 String refer="";
			 long vltime=0;
			 ArrayList<String> userid_list=new ArrayList<String>();
		
			 String rec_unit="";
			 //System.out.println("rec_list.size:"+rec_list.size());
		     for(int j=0;j<rec_list.size();j++)
		     {
		    	 one_rec=rec_list.get(j);
                // System.out.println("one_rec:"+key_str+"  "+one_rec.userId+" "+one_rec.start_time+" "+one_rec.end_time+" "); 	 
		     } 
			 
			 for(int i=0;i<user_list.size();i++)
			 {
			     user_info=user_list.get(i)+"";
			     if(!(SSO.tnoe(user_info)))
			     {
			    	 continue;
			     }
			     user_seg=user_info.split("\001");
			     if(user_seg.length!=2)
			     {
			    	 continue;
			     }
			     vtime=user_seg[0].trim();
			     refer=user_seg[1].trim();
			     vltime=TimeOpera.str2long(vtime);
			     for(int j=0;j<rec_list.size();j++)
			     {
			    	 one_rec=rec_list.get(j);
		    	     if((vltime>one_rec.start_time)&&(vltime<one_rec.end_time))
		    	     {
		    	    	 rec_unit=vtime+"\001"+refer+"\001"+one_rec.userId;
		    	    	 userid_list.add(rec_unit);
		    	    	 break;
		    	     } 	 
			     }   
			 }
			 
			 if(userid_list.size()>0)
			 {
			   for(int i=0;i<userid_list.size();i++)
			   {
			     String output_info=userid_list.get(i);
			     if(SSO.tnoe(key_str))
			     {
				   result_key.set(key_str+"\001"+output_info);
				//   result.set(output_info);
				   context.write(result_key,NullWritable.get());
			     }
			   }
			   
			 }
			 
		}
		
		private class Record{
			private long start_time;
			private long end_time;
			private String userId="";
			
			public Record()
			{
				
			}
			public Record(long start_time,long end_time,String userId)
			{
				this.setStart_time(start_time);
				this.setEnd_time(end_time);
				this.setUserId(userId);
			}
			public long getStart_time() {
				return start_time;
			}
			public void setStart_time(long start_time) {
				this.start_time = start_time;
			}
			public long getEnd_time() {
				return end_time;
			}
			public void setEnd_time(long end_time) {
				this.end_time = end_time;
			}
			public String getUserId() {
				return userId;
			}
			public void setUserId(String userId) {
				this.userId = userId;
			}
			
			
		}
		
	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err.println("Usage: CorrUserIDMR <day> <idu_input> <sip_input>  <output>");
			System.err.println("<idu_input> radius用户记录  ");
			System.err.println("<sip_input> 189so用户访问记录");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "CorrUserIDMR_" + day);
		job.setJarByClass(CorrUserIDMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(20);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileInputFormat.addInputPath(job, new Path(otherArgs[2]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[3]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	
		
}
