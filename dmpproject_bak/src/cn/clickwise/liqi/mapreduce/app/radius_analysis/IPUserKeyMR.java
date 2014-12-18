package cn.clickwise.liqi.mapreduce.app.radius_analysis;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import cn.clickwise.liqi.sort.utils.SortStrArray;
import cn.clickwise.liqi.str.basic.SSO;
import cn.clickwise.liqi.time.utils.TimeOpera;

public class IPUserKeyMR {
	
	private static class PrepareMapper extends Mapper<Object, Text, Text, Text> {

		private Text word = new Text();
		private Text word1 = new Text();
		public static String request_day="";

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
					
            String radius_line=value.toString().trim();        	
            String ip="";
            String status="";
            String userId="";
            String record_time="";
                      
        	String[] seg_arr=null;
        	seg_arr=radius_line.split("\t");
        	if(seg_arr.length==4)
        	{
        	   ip=seg_arr[0].trim();
        	   status=seg_arr[1].trim();
        	   userId=seg_arr[2].trim();
        	   record_time=seg_arr[3].trim();
               if(SSO.tnoe(ip))
               {
            	   word.set(ip);
            	   word1.set(status+"\t"+userId+"\t"+record_time);
            	   context.write(word, word1);
               }
        	}   		             				
		}
	}

	private static class PrepareReducer extends Reducer<Text, Text, Text, Text> {
		private Text result = new Text();
		private Text result_key = new Text();

		protected void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {

			 String key_str=key.toString().trim();
			 Iterator<Text> it = values.iterator();            
            
			 ArrayList<String> al=new ArrayList<String>();
			 
			 while(it.hasNext()) {
				//context.write(key, it.next());
			    	al.add(it.next().toString());		
			 }	
			 
		     String[] sort_arr=SortStrArray.sort_List(al, 2, "str", 3,"\t");
		     HashMap<String,String> onlineHM=new HashMap<String,String>();
		     HashMap<String,ArrayList<String>> offlineHM=new HashMap<String,ArrayList<String>>();
		     
		     String begin_record="";
		     String begin_status="";
		     String begin_userId="";
		     String begin_record_time="";
		     String[] begin_seg_arr=null;
		     		
		     begin_record=sort_arr[0];
		     begin_record=  begin_record.trim();
		     onlineHM.put(key_str, begin_record);
		     if(key_str.equals("1.202.57.142"))
			 {
			    System.out.println("online put:"+key_str+" "+  begin_record);
			 }
		     
		     begin_seg_arr=begin_record.split("\t");
		     ArrayList<String> begin_list=new ArrayList<String>();
		     
		     if(begin_seg_arr.length==3)
		     {
		    	 begin_status=begin_seg_arr[0].trim();
		    	 begin_userId=begin_seg_arr[1].trim();
		    	 begin_record_time=begin_seg_arr[2].trim();
		    	 
		    	 if(begin_status.equals("1"))
		    	 {
		    		begin_list.add("1\t"+begin_record_time);
      			  	offlineHM.put(begin_userId,begin_list);
		    	 }
		     }
		     
		     
		     String record="";
		     String status="";
		     String userId="";
		     String record_time="";
		     
		     String[] seg_arr=null;
		     
		     String ol_record="";
		     String ol_status="";
		     String olUserId="";
		     String ol_record_time=""; 
		     String[] ol_seg_arr=null; 
	
		     ArrayList<String> temp_ol_list=null;
		     ArrayList<String> temp_list=null;
		     
		     int los=0;
		     int nos=0;
		     
		     for(int i=1;i<sort_arr.length;i++)
		     {
		        record=sort_arr[i].trim();
		        seg_arr=record.split("\t");
		        if(seg_arr.length!=3)
		        {
		        	continue;
		        }
		        
		        status=seg_arr[0].trim();
		        userId=seg_arr[1].trim();
		        record_time=seg_arr[2].trim();	 
		        if(status.equals("3"))
		        {
		        	continue;
		        }
		        ol_record=onlineHM.get(key_str);
		        ol_seg_arr=ol_record.split("\t");
		        if(ol_seg_arr.length!=3)
		        {
		        	break;
		        }
		        
		        ol_status=ol_seg_arr[0].trim();
		        olUserId=ol_seg_arr[1].trim();
		        ol_record_time=ol_seg_arr[2].trim();
		        
		       // if((i%1000)==1)
		       // {
		       // 	System.out.println("next:"+status+" "+userId+" "+record_time);
		       //     System.out.println("online:"+ol_status+" "+olUserId+" "+ol_record_time);
		      //  }
		        if(status.equals("1"))
		        {
		        	//11
		          	if(ol_status.equals("1"))
		          	{
		          		if(!(userId.equals(olUserId)))
		          		{
		          			temp_ol_list=offlineHM.get(olUserId);
		          			if(temp_ol_list==null)
		          			{
		          				temp_ol_list=new ArrayList<String>();
		          			}
		          			
		          		    los=findLastStatus(temp_ol_list);		          		    
		          			if(los==1)
		          			{
		          				temp_ol_list.add("2\t"+TimeOpera.long2str(TimeOpera.str2long(record_time)));
		          			  	offlineHM.remove(olUserId);
		          			  	offlineHM.put(olUserId, temp_ol_list);
		          			}
		          			else if(los==2)
		          			{
		          				//nothing to do
		          			}
		          			
		          			
		          			temp_list=offlineHM.get(userId);
		          			if(temp_list==null)
		          			{
		          				temp_list=new ArrayList<String>();
		          			}
		          			nos=findLastStatus(temp_list);	
		          			
		     		        if(key_str.equals("1.202.57.142"))
		     		        {
		     		        	System.out.println("status:1 ol_status 1"+"los:"+los+" nos:"+nos);
		     		     
		     		        }
		          			if(nos==1)
		          			{
		          				temp_list=removeLast(temp_list);
		          				temp_list.add("1\t"+record_time);
		          			  	offlineHM.remove(userId);
		          			  	offlineHM.put(userId, temp_list);
		          			}
		          			else if(nos==2)
		          			{
		          				temp_list.add("1\t"+record_time);
		          			  	offlineHM.remove(userId);
		          			  	offlineHM.put(userId, temp_list);
		          			}
		          			else if((temp_list.size())==0)
		          			{
		          				temp_list.add("1\t"+record_time);
		          			  	offlineHM.remove(userId);
		          			  	offlineHM.put(userId, temp_list);
		          			}
		          		}
		          		else if(userId.equals(olUserId))
		          		{
		          			//nothing to do
		          		}        		
		          	}
		          	//21
		          	else if(ol_status.equals("2"))
		          	{
		          		if(!(userId.equals(olUserId)))
		          		{
		          			temp_ol_list=offlineHM.get(olUserId);
		          			if(temp_ol_list==null)
		          			{
		          				temp_ol_list=new ArrayList<String>();
		          			}
		          			
		          		    los=findLastStatus(temp_ol_list);	
		          			if(los==1)
		          			{
		          				temp_ol_list.add("2\t"+TimeOpera.long2str(TimeOpera.str2long(record_time)));
		          			  	offlineHM.remove(olUserId);
		          			  	offlineHM.put(olUserId, temp_ol_list);
		          			}
		          			else if(los==2)
		          			{
		          			    //nothing to do
		          			}
		          			
		          			temp_list=offlineHM.get(userId);
		          			if(temp_list==null)
		          			{
		          				temp_list=new ArrayList<String>();
		          			}
		          			nos=findLastStatus(temp_list);	
		          		    if(key_str.equals("1.202.57.142"))
		     		        {
		     		        	System.out.println("status:1 ol_status 2"+"los:"+los+" nos:"+nos);
		     		     
		     		        }
		          			if(nos==1)
		          			{
		          				temp_list=removeLast(temp_list);
		          				temp_list.add("1\t"+record_time);
		          				offlineHM.remove(userId);
		          				offlineHM.put(userId, temp_list);
		          			}
		          			else if(nos==2)
		          			{
		          				temp_list.add("1\t"+record_time);
		          				offlineHM.remove(userId);
		          				offlineHM.put(userId, temp_list);      				
		          			}
		          			else if((temp_list.size())==0)
		          			{
		          				temp_list.add("1\t"+record_time);
		          			  	offlineHM.remove(userId);
		          			  	offlineHM.put(userId, temp_list);
		          			}
		          		}
		          		else if(userId.equals(olUserId))
		          		{
	          			    //nothing to do
		          		}   
		          	}
		        }
		        else if (status.equals("2"))
		        {
		        	//12
		          	if(ol_status.equals("1"))
		          	{
		          		if(!(userId.equals(olUserId)))
		          		{
		          			temp_ol_list=offlineHM.get(olUserId);
		          			if(temp_ol_list==null)
		          			{
		          				temp_ol_list=new ArrayList<String>();
		          			}
		          			
		          		    los=findLastStatus(temp_ol_list);	
		          			if(los==1)
		          			{
		          				 //nothing to do	
		          			}
		          			else if(los==2)
		          			{
		          				 //nothing to do
		          			}
		          			
		          			temp_list=offlineHM.get(userId);
		          			if(temp_list==null)
		          			{
		          				temp_list=new ArrayList<String>();
		          			}
		          			
		          			nos=findLastStatus(temp_list);	
		          		    if(key_str.equals("1.202.57.142"))
		     		        {
		     		        	System.out.println("status:2 ol_status 1"+"los:"+los+" nos:"+nos);
		     		     
		     		        }
		          			if(nos==1)
		          			{
		        				temp_list.add("2\t"+record_time);
		          				offlineHM.remove(userId);
		          				offlineHM.put(userId, temp_list); 
		          			}
		          			else if(nos==2)
		          			{
		          				 //nothing to do
		          			}
		          		}
		          		else if(userId.equals(olUserId))
		          		{
		          			
		          			temp_list=offlineHM.get(userId);
		          			if(temp_list==null)
		          			{
		          				temp_list=new ArrayList<String>();
		          			}
		          			
		          			nos=findLastStatus(temp_list);	
		     		        if((i%1000)==1)
		     		        {
		     		        	System.out.println("equal status:2 ol_status 1"+"los:"+los+" nos:"+nos);
		     		     
		     		        }
		          			if(nos==1)
		          			{
		        				temp_list.add("2\t"+record_time);
		          				offlineHM.remove(userId);
		          				offlineHM.put(userId, temp_list); 
		          			}
		          			else if(nos==2)
		          			{
		          				 //nothing to do
		          			}		          				          			
		          		}   
		          	}
		          	//22
		          	else if(ol_status.equals("2"))
		          	{
		          		if(!(userId.equals(olUserId)))
		          		{
		          			temp_ol_list=offlineHM.get(olUserId);
		          			if(temp_ol_list==null)
		          			{
		          				temp_ol_list=new ArrayList<String>();
		          			}
		          			
		          		    los=findLastStatus(temp_ol_list);	
		          			if(los==1)
		          			{
		          				 //nothing to do
		          			}
		          			else if(los==2)
		          			{
		          				 //nothing to do
		          			}
		          			
		          			temp_list=offlineHM.get(userId);
		          			if(temp_list==null)
		          			{
		          				temp_list=new ArrayList<String>();
		          			}
		          			nos=findLastStatus(temp_list);	
		          		    if(key_str.equals("1.202.57.142"))
		     		        {
		     		        	System.out.println("status:2 ol_status 2"+"los:"+los+" nos:"+nos);
		     		     
		     		        }
		          			
		          			if(nos==1)
		          			{
		        				temp_list.add("2\t"+record_time);
		          				offlineHM.remove(userId);
		          				offlineHM.put(userId, temp_list); 
		          			}
		          			else if(nos==2)
		          			{
		          				 //nothing to do
		          			}
		          		}
		          		else if(userId.equals(olUserId))
		          		{
		          			 //nothing to do
		          		}   
		          	}
		        }
		        if(key_str.equals("1.202.57.142"))
		        {
		        	System.out.println("status:"+status);
		        }
		        if((status.equals("1"))||(status.equals("2")))
		        {
	
		          onlineHM.remove(key_str);
		          onlineHM.put(key_str, record);
			        if(key_str.equals("1.202.57.142"))
				    {
				       System.out.println("online put:"+key_str+" "+record);
				    }
		          
		        }
		     }
		     
		     Iterator iter = offlineHM.entrySet().iterator();
		     ArrayList<String> post_list=new ArrayList<String>();
		     String nrecord="";
		     while (iter.hasNext()) {
		         Map.Entry entry = (Map.Entry) iter.next();
		         userId=(String) entry.getKey();
		         temp_list =(ArrayList<String>) entry.getValue();
		         for(int j=0;j<temp_list.size();j++)
		         {
		        	 record=temp_list.get(j);
		        	 record=record.trim();
		        	 nrecord=userId+"\t"+record;
		        	 post_list.add(nrecord);
		         }      
		     } 
		     
		     sort_arr=SortStrArray.sort_List(post_list, 2, "str", 3,"\t");		     
		     String res=SSO.implode(sort_arr, "\001");
		     res=res+"\001"+"IDU"; 
		     if(SSO.tnoe(key_str))
		     {
		    	key_str=key_str.trim();
		        result_key.set(key_str+"\001");
		        result.set(res);
		        context.write(result_key, result);
		     }
		}
		
		public int findLastStatus(ArrayList<String> al)
		{
			if((al==null)||((al.size())<1))
			{
				return 0;
			}
			String record=al.get(al.size()-1);
			String status="";
			String record_time="";
			String[] seg_arr=null;
			seg_arr=record.split("\t");
			if(seg_arr.length!=2)
			{
				return 0;
			}
			status=seg_arr[0].trim();
			record_time=seg_arr[1].trim();	
			return Integer.parseInt(status);
		}
		
		public ArrayList<String> removeLast(ArrayList<String> al)
		{
			
			ArrayList<String> nal=new ArrayList<String>();
			
			for(int i=0;i<(al.size()-1);i++)
			{
				nal.add(al.get(i));
			}	
			return nal;			
		}
	}
	
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
				.getRemainingArgs();
		if (otherArgs.length != 3) {
			System.err.println("Usage: IPUserKeyMR <day> <input> <output>");
			System.exit(2);
		}
		
		String day = otherArgs[0];
		Job job = new Job(conf, "IPUserKeyMR_" + day);
		job.setJarByClass(IPUserKeyMR.class);
		PrepareMapper.request_day=day;
		job.setMapperClass(PrepareMapper.class);
		job.setReducerClass(PrepareReducer.class);
		job.setNumReduceTasks(10);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
	

}
