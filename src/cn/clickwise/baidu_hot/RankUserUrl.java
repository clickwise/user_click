package cn.clickwise.baidu_hot;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class RankUserUrl {
 
	
	
	private static class PPrepareMapper extends Mapper<Object,Text,Text,Text>{
		private Text word=new Text(); 
		private Text word1=new Text();
		
		public void map(Object key,Text value,Context context)
		    throws IOException,InterruptedException{
			String val=value.toString();
			String arr[] =val.split("\001");
			String host="";
			String url="";
			String anchor_text="";
			String pvs="";
			String uvs="";
			String ips="";			
			if(arr.length==6)
			{
				host=arr[0];
				url=arr[1];
				anchor_text=arr[2];
				pvs=arr[3];
				uvs=arr[4];
				ips=arr[5];
				word.set(anchor_text);
				word1.set(host+"\001"+url+"\001"+pvs+"\001"+uvs+"\001"+ips);
				context.write(word, word1);
								
			}
						
		}				
	}
	
	private static class PPrepareReducer extends Reducer<Text,Text,IntWritable,Text>
	{
		private Text result=new Text();
		private IntWritable pvs_key=new IntWritable();
		
		protected void reduce(Text key,Iterable<Text> values,Context context) throws IOException,InterruptedException{
			String anchor_text=key.toString();
			int sum_pvs=0;
			Iterator<Text> it=values.iterator();
			String temp_s="";
			String[] seg_arr=null;
			String host="";
			String url="";
			String pvs="";
			String uvs="";
			String ips="";
			
			String url_s="";
			url_s=anchor_text+"\001";
			Vector ips_urls=new Vector();
			
			while(it.hasNext())
			{
				temp_s=it.next().toString();
				temp_s=temp_s.trim();
				if(temp_s.length()<1)
				{
					continue;					
				}
				seg_arr=temp_s.split("\001");
				if(seg_arr.length!=5)
				{
					continue;
				}
				host=seg_arr[0].trim();
				url=seg_arr[1].trim();
				pvs=seg_arr[2].trim();
				uvs=seg_arr[3].trim();
				ips=seg_arr[4].trim();
				sum_pvs+=Integer.parseInt(uvs);
				//url_s=url_s+url+" ";
				ips_urls.add(uvs+"\001"+url);
			}
			Vector n_ips_urls=null;
			n_ips_urls=rankVector(ips_urls);
			String[] n_seg_arr=null;
			String temp_ips_url="";
			for(int i=0;i<n_ips_urls.size();i++)
			{
				temp_ips_url=n_ips_urls.get(i)+"";
				temp_ips_url=temp_ips_url.trim();
				n_seg_arr=temp_ips_url.split("\001");
				if(n_seg_arr.length<2)
				{
					continue;
				}
				url_s=url_s+n_seg_arr[1]+" ";
			}
			
			
			url_s=url_s.trim();
			pvs_key.set(1000000-sum_pvs);
			result.set(url_s);
			
			context.write(pvs_key, result);		
		}	
	}
	
	public static Vector rankVector(Vector ips_urls)
	{
		Vector nv=new Vector();
		int minindex=-1;
        String tempS="";
        String minword="";
        String tempword="";
        
        String temp_ips_url="";
        String[] seg_arr=null;
        
        if(ips_urls.size()<10)
        {
        	return ips_urls;
        }
		for(int i=0;i<10;i++)
		{
			if(i==(ips_urls.size()-2))
			{
				break;
			}
			tempS=ips_urls.get(i)+"";
			tempS=tempS.trim();
			seg_arr=tempS.split("\001");
		    if(seg_arr.length<2)
		    {
		    	continue;
		    }
		    minword=seg_arr[0];
			minindex=i;
			for(int j=i;j<ips_urls.size();j++)
			{
				tempS=ips_urls.get(j)+"";
				tempS=tempS.trim();
				seg_arr=tempS.split("\001");
				if(seg_arr.length<2)
				{
					continue;
				}
				tempword=seg_arr[0];
				if(Integer.parseInt(tempword)>Integer.parseInt(minword))
				{
					minindex=j;
					minword=tempword;
				}				
			}
			temp_ips_url=ips_urls.get(i)+"";
			ips_urls.set(i, ips_urls.get(minindex)+"");
			nv.add(ips_urls.get(minindex));
			ips_urls.set(minindex, temp_ips_url);			
		}
		
			
		return nv;
	}
	
	public static void main(String[] args) throws Exception{
		
		Configuration conf=new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
		.getRemainingArgs();
		if(otherArgs.length!=3){
			System.err.println("Usage: RankUserUrl <day> <input> <output>");
			System.exit(2);
		}
		
		String day=otherArgs[0];
		Job job=new Job(conf,"RankUserUrl_"+day);
		job.setJarByClass(RankUserUrl.class);
		job.setMapperClass(PPrepareMapper.class);
		job.setReducerClass(PPrepareReducer.class);
		job.setNumReduceTasks(1);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(otherArgs[1]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[2]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
		
		
	}
	
	
	
}
